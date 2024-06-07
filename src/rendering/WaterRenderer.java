package src.rendering;

import org.lwjgl.opengl.GL11;
import src.mesh.Mesh;
import src.shaderPrograms.WaterShader;
import src.utils.Color;
import src.utils.Time;
import src.utils.Vector3f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

public class WaterRenderer extends Renderer {
    private final Mesh mesh;
    private float[] baseColor = new float[]{1f, 1f, 1f};
    private final WaterShader shader;
    private float ambient = 0.8f;
    private float shininess = 32f;
    private float specularStrength = 0.5f;
    private float waveLength = 1.5f;
    private float waveAmplitude = 0.5f;
    private float distortionScale = 0.5f;
    private float localTime = 0f;

    public WaterRenderer(Mesh mesh, WaterShader shader) {
        this.mesh = mesh;
        this.shader = shader;
        this.position = new Vector3f(0f, 0f, 0f);
        this.rotation = new Vector3f(0f, 0f, 0f);
        this.scale = new Vector3f(1f, 1f, 1f);
    }

    public WaterRenderer(Mesh mesh, WaterShader shader, Vector3f position, Vector3f rotation, Vector3f scale) {
        this.mesh = mesh;
        this.shader = shader;
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
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

    public void setWaveLength(float waveLength) {
        this.waveLength = waveLength;
    }

    public void setWaveAmplitude(float waveAmplitude) {
        this.waveAmplitude = waveAmplitude;
    }

    private void setDistortionScale(float distortionScale) {
        this.distortionScale = distortionScale;
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
    public void Update() {
        localTime += Time.deltaTime;
    }

    @Override
    public void Render(boolean clipPlane, boolean shadowPass) {
        if (clipPlane || shadowPass) return;

        glEnable(GL_LIGHTING);
        glDisable(GL_TEXTURE_2D);

        glPushMatrix();

        glTranslatef(position.x, position.y, position.z);
        glRotatef(rotation.x, 1, 0, 0);
        glRotatef(rotation.y, 0, 1, 0);
        glRotatef(rotation.z, 0, 0, 1);
        glScalef(scale.x, scale.y, scale.z);

        shader.use();

        shader.setViewPos(renderEngine.viewPos);
        shader.setWaveTime(localTime * 0.2f);
        shader.setAmbientStrength(ambient);
        shader.setShininess(shininess);
        shader.setSpecularStrength(specularStrength);
        shader.setWaveLength(waveLength);
        shader.setWaveAmplitude(waveAmplitude);
        shader.setDistortionScale(distortionScale);
        shader.setFogColor(renderEngine.fogColor);
        shader.setFogLimits(renderEngine.fogStart, renderEngine.fogEnd);

        shader.bindTextures(renderEngine.reflectionTextureID, renderEngine.refractionTextureID);

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

        shader.resetBindings();

        glUseProgram(0);
    }
}
