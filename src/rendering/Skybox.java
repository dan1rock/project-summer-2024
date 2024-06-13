package src.rendering;

import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import src.shaderPrograms.SkyboxShader;
import src.utils.Matrix4f;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.*;

public class Skybox extends Renderer{
    private static final float[] skyboxVertices = {
            -1.0f,  1.0f, -1.0f,  -1.0f, -1.0f, -1.0f,   1.0f, -1.0f, -1.0f,   1.0f,  1.0f, -1.0f, // Back face
            -1.0f, -1.0f,  1.0f,  -1.0f, -1.0f, -1.0f,  -1.0f,  1.0f, -1.0f,  -1.0f,  1.0f,  1.0f, // Left face
            1.0f, -1.0f, -1.0f,   1.0f, -1.0f,  1.0f,   1.0f,  1.0f,  1.0f,   1.0f,  1.0f, -1.0f, // Right face
            -1.0f, -1.0f,  1.0f,  -1.0f,  1.0f,  1.0f,   1.0f,  1.0f,  1.0f,   1.0f, -1.0f,  1.0f, // Front face
            -1.0f,  1.0f, -1.0f,   1.0f,  1.0f, -1.0f,   1.0f,  1.0f,  1.0f,  -1.0f,  1.0f,  1.0f, // Top face
            -1.0f, -1.0f, -1.0f,  -1.0f, -1.0f,  1.0f,   1.0f, -1.0f,  1.0f,   1.0f, -1.0f, -1.0f  // Bottom face
    };
    private final SkyboxShader skyboxShader = new SkyboxShader();
    private int skyboxVAO;
    private int skyboxVBO;
    private final int skyboxTextureID;

    public Skybox(String path) {
        String[] faces = new String[] {
                path + "_ft.jpg",
                path + "_bk.jpg",
                path + "_up.jpg",
                path + "_dn.jpg",
                path + "_rt.jpg",
                path + "_lf.jpg"
        };

        skyboxTextureID = loadCubemap(faces);
        init();
    }

    private void init() {
        skyboxVAO = glGenVertexArrays();
        skyboxVBO = glGenBuffers();
        glBindVertexArray(skyboxVAO);
        glBindBuffer(GL_ARRAY_BUFFER, skyboxVBO);
        glBufferData(GL_ARRAY_BUFFER, skyboxVertices, GL_STATIC_DRAW);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glBindVertexArray(0);
    }

    private int loadCubemap(String[] faces) {
        int textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_CUBE_MAP, textureID);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            for (int i = 0; i < faces.length; i++) {
                IntBuffer width = stack.mallocInt(1);
                IntBuffer height = stack.mallocInt(1);
                IntBuffer nrChannels = stack.mallocInt(1);
                ByteBuffer data = STBImage.stbi_load(faces[i], width, height, nrChannels, 0);
                if (data != null) {
                    glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_RGB, width.get(0), height.get(0), 0, GL_RGB, GL_UNSIGNED_BYTE, data);
                    STBImage.stbi_image_free(data);
                } else {
                    System.err.println("Failed to load cubemap texture at " + faces[i]);
                }
            }
        }

        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);

        return textureID;
    }

    @Override
    public void Render(boolean clipPlane, boolean shadowPass) {
        glDepthFunc(GL_LEQUAL);
        glCullFace(GL_BACK);

        skyboxShader.use();

        skyboxShader.setViewMatrix(Matrix4f.clearTranslation(renderEngine.viewMatrix));

        float[] projectionMatrix = new float[16];

        glGetFloatv(GL_PROJECTION_MATRIX, projectionMatrix);

        skyboxShader.setProjectionMatrix(projectionMatrix);

        glBindVertexArray(skyboxVAO);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_CUBE_MAP, skyboxTextureID);
        glDrawArrays(GL_QUADS, 0, 24);
        glBindVertexArray(0);

        glDepthFunc(GL_LESS);
        glCullFace(GL_FRONT);
    }
}
