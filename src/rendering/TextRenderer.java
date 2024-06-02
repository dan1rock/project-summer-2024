package src.rendering;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import src.utils.FileUtils;
import src.utils.ShaderUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.stb.STBTruetype.*;

public class TextRenderer {
    private static final int FONT_TEXTURE_WIDTH = 1024;
    private static final int FONT_TEXTURE_HEIGHT = 1024;
    private int fontTextureID;
    private final int shaderProgramID;
    private STBTTBakedChar.Buffer charData;

    private final int projectionLoc;
    private final int modelLoc;
    private final int textureLoc;
    private final int textColorLoc;

    private final int vertexVboId;
    private final int textureVboId;
    private final int indexVboId;

    private final RenderEngine renderEngine;

    public TextRenderer(String fontPath) {
        renderEngine = RenderEngine.getInstance();

        try {
            byte[] bytes = Files.readAllBytes(Paths.get(fontPath));
            ByteBuffer fontBuffer = MemoryUtil.memAlloc(bytes.length);
            fontBuffer.put(bytes);
            fontBuffer.flip();
            ByteBuffer bitmap = MemoryUtil.memAlloc(FONT_TEXTURE_WIDTH * FONT_TEXTURE_HEIGHT);

            charData = STBTTBakedChar.malloc(96);

            stbtt_BakeFontBitmap(fontBuffer, 128, bitmap, FONT_TEXTURE_WIDTH, FONT_TEXTURE_HEIGHT, 32, charData);

            fontTextureID = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, fontTextureID);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RED, FONT_TEXTURE_WIDTH, FONT_TEXTURE_HEIGHT, 0, GL_RED, GL_UNSIGNED_BYTE, bitmap);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

            MemoryUtil.memFree(bitmap);
            MemoryUtil.memFree(fontBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String textVertexShader = FileUtils.readFileAsString("src/shaders/textVertex.glsl");
        String textFragmentShader = FileUtils.readFileAsString("src/shaders/textFragment.glsl");
        shaderProgramID = ShaderUtils.createShaderProgram(textVertexShader, textFragmentShader);

        projectionLoc = glGetUniformLocation(shaderProgramID, "projection");
        modelLoc = glGetUniformLocation(shaderProgramID, "model");
        textureLoc = glGetUniformLocation(shaderProgramID, "text");
        textColorLoc = glGetUniformLocation(shaderProgramID, "textColor");

        vertexVboId = glGenBuffers();
        textureVboId = glGenBuffers();
        indexVboId = glGenBuffers();
    }

    public void renderText(String text, float[] color, float x, float y, float scale) {
        glPushMatrix();
        glLoadIdentity();
        glRotatef(180f, 1f, 0f, 0f);
        glUseProgram(shaderProgramID);

        float[] modelMatrix = new float[16];

        glGetFloatv(GL_MODELVIEW_MATRIX, modelMatrix);

        glUniformMatrix4fv(modelLoc, false, modelMatrix);
        glUniformMatrix4fv(projectionLoc, false, renderEngine.overlayMatrix);

        glUniform3fv(textColorLoc, color);

        glUniform1i(textureLoc, 0);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, fontTextureID);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer xPos = stack.floats(0.0f);
            FloatBuffer yPos = stack.floats(0.0f);

            FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(text.length() * 8);
            FloatBuffer textureCoordsBuffer = BufferUtils.createFloatBuffer(text.length() * 8);

            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                if (c < 32 || c >= 128) continue;

                STBTTAlignedQuad quad = STBTTAlignedQuad.malloc(stack);
                stbtt_GetBakedQuad(charData, FONT_TEXTURE_WIDTH, FONT_TEXTURE_HEIGHT, c - 32, xPos, yPos, quad, true);

                float localScale = 0.01f;
                float x0 = quad.x0() * scale * localScale + x;
                float x1 = quad.x1() * scale * localScale + x;
                float y0 = quad.y0() * scale * localScale + y;
                float y1 = quad.y1() * scale * localScale + y;

                textureCoordsBuffer.put(quad.s0()).put(quad.t0());
                verticesBuffer.put(x0).put(y0);
                textureCoordsBuffer.put(quad.s1()).put(quad.t0());
                verticesBuffer.put(x1).put(y0);
                textureCoordsBuffer.put(quad.s1()).put(quad.t1());
                verticesBuffer.put(x1).put(y1);
                textureCoordsBuffer.put(quad.s0()).put(quad.t1());
                verticesBuffer.put(x0).put(y1);
            }

            IntBuffer indicesBuffer = BufferUtils.createIntBuffer(text.length() * 4);

            for (int i = 0; i < text.length() * 4; i++) {
                indicesBuffer.put(i);
            }

            verticesBuffer.flip();
            textureCoordsBuffer.flip();
            indicesBuffer.flip();

            glBindBuffer(GL_ARRAY_BUFFER, vertexVboId);
            glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
            glBindBuffer(GL_ARRAY_BUFFER, 0);

            glBindBuffer(GL_ARRAY_BUFFER, textureVboId);
            glBufferData(GL_ARRAY_BUFFER, textureCoordsBuffer, GL_STATIC_DRAW);
            glBindBuffer(GL_ARRAY_BUFFER, 0);

            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexVboId);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        }

        glBindBuffer(GL_ARRAY_BUFFER, vertexVboId);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, textureVboId);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexVboId);

        glDrawElements(GL_QUADS, text.length() * 4, GL_UNSIGNED_INT, 0);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glPopMatrix();
    }

    public void cleanup() {
        glDeleteTextures(fontTextureID);
        charData.free();
    }
}
