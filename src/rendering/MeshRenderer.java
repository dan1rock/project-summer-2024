package src.rendering;

import org.lwjgl.opengl.GL11;
import src.mesh.Mesh;
import src.shaderPrograms.MainShader;
import src.utils.Color;
import src.utils.Vector3f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

public class MeshRenderer extends Renderer {
    private final Mesh mesh;
    private Texture texture;
    private float[] baseColor = new float[]{1f, 1f, 1f};
    private final MainShader shader;
    private boolean isTextured = false;
    private float ambient = 0.4f;
    private float shininess = 32f;
    private float specularStrength = 0.5f;
    private boolean isSelected = false;

    public MeshRenderer(Mesh mesh, MainShader shader) {
        this.mesh = mesh;
        this.shader = shader;
        this.position = new Vector3f(0f, 0f, 0f);
        this.rotation = new Vector3f(0f, 0f, 0f);
        this.scale = new Vector3f(1f, 1f, 1f);
    }

    public MeshRenderer(Mesh mesh, MainShader shader, Vector3f position, Vector3f rotation, Vector3f scale) {
        this.mesh = mesh;
        this.shader = shader;
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
        isTextured = true;
    }

    public void setAmbient(float ambient) {
        this.ambient = ambient;
    }

    public void setShininess(float shininess) {
        this.shininess = shininess;
    }

    public void setSpecularStrength(float specularStrength) {
        this.specularStrength = specularStrength;
    }

    public void setBaseColor(float[] color) {
        baseColor[0] = color[0];
        baseColor[1] = color[1];
        baseColor[2] = color[2];
    }

    public void setObjectColor(Vector3f color) {
        baseColor[0] = color.x;
        baseColor[1] = color.y;
        baseColor[2] = color.z;
    }

    @Override
    public void Update(float deltaTime) {

    }

    @Override
    public void processKeyboard(boolean[] keys) {
        isSelected = true;
        float velocity = 2f * renderEngine.deltaTime;
        if (keys[GLFW_KEY_W]) {
            position.add(new Vector3f(1f, 0f, 0f).mul(velocity));
        }
        if (keys[GLFW_KEY_S]) {
            position.sub(new Vector3f(1f, 0f, 0f).mul(velocity));
        }
        if (keys[GLFW_KEY_A]) {
            position.sub(new Vector3f(0f, 0f, 1f).mul(velocity));
        }
        if (keys[GLFW_KEY_D]) {
            position.add(new Vector3f(0f, 0f, 1f).mul(velocity));
        }
        if (keys[GLFW_KEY_SPACE]) {
            position.add(new Vector3f(0f, 1f, 0f).mul(velocity));
        }
        if (keys[GLFW_KEY_LEFT_SHIFT]) {
            position.sub(new Vector3f(0f, 1f, 0f).mul(velocity));
        }
        if (keys[GLFW_KEY_E]) {
            rotation.y += 90f * renderEngine.deltaTime;
        }
        if (keys[GLFW_KEY_Q]) {
            rotation.y -= 90f * renderEngine.deltaTime;
        }
    }

    @Override
    public void Render(boolean clipPlane) {
        glEnable(GL_LIGHTING);

        if (isTextured) {
            glEnable(GL_TEXTURE_2D);
            glBindTexture(GL_TEXTURE_2D, texture.get());
            glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_DECAL);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        }
        else {
            glDisable(GL_TEXTURE_2D);
        }

        glPushMatrix();

        glTranslatef(position.x, position.y, position.z);
        glRotatef(rotation.x, 1, 0, 0);
        glRotatef(rotation.y, 0, 1, 0);
        glRotatef(rotation.z, 0, 0, 1);
        glScalef(scale.x, scale.y, scale.z);

        shader.use();

        shader.setViewPos(renderEngine.viewPos);
        if (isSelected) {
            shader.setObjectColor(Color.Lemon);
        } else {
            shader.setObjectColor(baseColor);
        }
        shader.setAmbientStrength(ambient + (isSelected ? 0.5f : 0f));
        shader.setShininess(shininess);
        shader.setSpecularStrength(specularStrength);
        shader.isTextured(isTextured);
        shader.setFogColor(renderEngine.fogColor);
        shader.setFogLimits(renderEngine.fogStart, renderEngine.fogEnd);
        shader.setClipPlane(renderEngine.clipPlane);
        shader.useClipPlane(clipPlane);

        float[] modelMatrix = new float[16];

        glGetFloatv(GL_MODELVIEW_MATRIX, modelMatrix);

        shader.setModelMatrix(modelMatrix);

        glBindBuffer(GL_ARRAY_BUFFER, mesh.getVertexVboId());
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, mesh.getTextureVboId());
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, mesh.getNormalVboId());
        glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

        GL11.glColor3fv(Color.Magenta);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, mesh.getIndexVboId());

        glDrawElements(GL_TRIANGLES, mesh.getNumIndices(), GL_UNSIGNED_INT, 0);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glPopMatrix();
        glUseProgram(0);
        if (!clipPlane) isSelected = false;
    }
}
