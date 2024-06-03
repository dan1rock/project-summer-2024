package src.rendering;

import org.lwjgl.opengl.GL11;
import src.mesh.Mesh;
import src.utils.Color;
import src.utils.Vector3f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

public class WaterRenderer extends Renderer {
    private final Mesh mesh;
    private float[] baseColor = new float[]{1f, 1f, 1f};
    private final int shaderProgramID;
    private boolean isTextured = false;
    private float ambient = 0.8f;
    private float shininess = 32f;
    private float specularStrength = 0.5f;
    private float waveLength = 1.5f;
    private float waveAmplitude = 0.5f;
    private float distortionScale = 0.5f;
    private float localTime = 0f;

    int modelLoc;
    int reflectionTextureLoc;
    int refractionTextureLoc;
    int viewPosLoc;
    int moveFactorLoc;
    int waveTimeLoc;
    int ambientStrengthLoc;
    int shininessLoc;
    int specularStrengthLoc;
    int waveLengthLoc;
    int waveAmplitudeLoc;
    int distortionScaleLoc;

    public WaterRenderer(Mesh mesh, int shaderProgramID) {
        this.mesh = mesh;
        this.shaderProgramID = shaderProgramID;
        this.position = new Vector3f(0f, 0f, 0f);
        this.rotation = new Vector3f(0f, 0f, 0f);
        this.scale = new Vector3f(1f, 1f, 1f);

        getShaderLocations();
    }

    public WaterRenderer(Mesh mesh, int shaderProgramID, Vector3f position, Vector3f rotation, Vector3f scale) {
        this.mesh = mesh;
        this.shaderProgramID = shaderProgramID;
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;

        getShaderLocations();
    }

    private void getShaderLocations() {
        modelLoc = glGetUniformLocation(shaderProgramID, "model");
        reflectionTextureLoc = glGetUniformLocation(shaderProgramID, "reflectionTexture");
        refractionTextureLoc = glGetUniformLocation(shaderProgramID, "refractionTexture");
        viewPosLoc = glGetUniformLocation(shaderProgramID, "viewPos");
        moveFactorLoc = glGetUniformLocation(shaderProgramID, "moveFactor");
        waveTimeLoc = glGetUniformLocation(shaderProgramID, "waveTime");
        ambientStrengthLoc = glGetUniformLocation(shaderProgramID, "ambientStrength");
        shininessLoc = glGetUniformLocation(shaderProgramID, "shininess");
        specularStrengthLoc = glGetUniformLocation(shaderProgramID, "specularStrength");
        waveLengthLoc = glGetUniformLocation(shaderProgramID, "waveLength");
        waveAmplitudeLoc = glGetUniformLocation(shaderProgramID, "waveAmplitude");
        distortionScaleLoc = glGetUniformLocation(shaderProgramID, "distortionScale");
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
    public void Update(float deltaTime) {
        localTime += deltaTime * 2f;
    }

    @Override
    public void Render(boolean clipPlane) {
        if (clipPlane) return;

        glEnable(GL_LIGHTING);
        glDisable(GL_TEXTURE_2D);

        glPushMatrix();

        glTranslatef(position.x, position.y, position.z);
        glRotatef(rotation.x, 1, 0, 0);
        glRotatef(rotation.y, 0, 1, 0);
        glRotatef(rotation.z, 0, 0, 1);
        glScalef(scale.x, scale.y, scale.z);

        glUseProgram(shaderProgramID);

        float moveFactor = 0.0f;

        glUniform3f(viewPosLoc, renderEngine.viewPos.x, renderEngine.viewPos.y, renderEngine.viewPos.z);
        glUniform1f(moveFactorLoc, moveFactor);
        glUniform1f(waveTimeLoc, localTime * 0.1f);
        glUniform1f(ambientStrengthLoc, ambient);
        glUniform1f(shininessLoc, shininess);
        glUniform1f(specularStrengthLoc, specularStrength);
        glUniform1f(waveLengthLoc, waveLength);
        glUniform1f(waveAmplitudeLoc, waveAmplitude);
        glUniform1f(distortionScaleLoc, distortionScale);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, renderEngine.reflectionTextureID);
        glUniform1i(reflectionTextureLoc, 0);

        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, renderEngine.refractionTextureID);
        glUniform1i(refractionTextureLoc, 1);

        float[] modelMatrix = new float[16];

        glGetFloatv(GL_MODELVIEW_MATRIX, modelMatrix);

        glUniformMatrix4fv(modelLoc, false, modelMatrix);

        glBindBuffer(GL_ARRAY_BUFFER, mesh.vertexVboId);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, mesh.textureVboId);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, mesh.normalVboId);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

        GL11.glColor3fv(Color.Magenta);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, mesh.indexVboId);

        glDrawElements(GL_TRIANGLES, mesh.numIndices, GL_UNSIGNED_INT, 0);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glPopMatrix();
        resetTextureBindings();
        glUseProgram(0);
    }

    public void resetTextureBindings() {
        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, 0);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, 0);
    }
}
