package src.shaderPrograms;

import src.utils.Vector3f;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glUniform1i;

public class WaterShader extends ShaderProgram {
    private final int modelLoc;
    private final int reflectionTextureLoc;
    private final int refractionTextureLoc;
    private final int viewPosLoc;
    private final int waveTimeLoc;
    private final int ambientStrengthLoc;
    private final int shininessLoc;
    private final int specularStrengthLoc;
    private final int waveLengthLoc;
    private final int waveAmplitudeLoc;
    private final int distortionScaleLoc;
    private final int fogColorLoc;
    private final int fogStartLoc;
    private final int fogEndLoc;

    public WaterShader() {
        super("src/shaders/waterVertex.glsl", "src/shaders/waterFragment.glsl");

        modelLoc = glGetUniformLocation(shader, "model");
        reflectionTextureLoc = glGetUniformLocation(shader, "reflectionTexture");
        refractionTextureLoc = glGetUniformLocation(shader, "refractionTexture");
        viewPosLoc = glGetUniformLocation(shader, "viewPos");
        waveTimeLoc = glGetUniformLocation(shader, "waveTime");
        ambientStrengthLoc = glGetUniformLocation(shader, "ambientStrength");
        shininessLoc = glGetUniformLocation(shader, "shininess");
        specularStrengthLoc = glGetUniformLocation(shader, "specularStrength");
        waveLengthLoc = glGetUniformLocation(shader, "waveLength");
        waveAmplitudeLoc = glGetUniformLocation(shader, "waveAmplitude");
        distortionScaleLoc = glGetUniformLocation(shader, "distortionScale");
        fogColorLoc = glGetUniformLocation(shader, "fogColor");
        fogStartLoc = glGetUniformLocation(shader, "fogStart");
        fogEndLoc = glGetUniformLocation(shader, "fogEnd");
    }

    public void setModelMatrix(float[] matrix) {
        glUniformMatrix4fv(modelLoc, false, matrix);
    }

    public void bindTextures(int reflectionTexture, int refractionTexture) {
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, reflectionTexture);
        glUniform1i(reflectionTextureLoc, 0);

        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, refractionTexture);
        glUniform1i(refractionTextureLoc, 1);
    }

    public void resetBindings() {
        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, 0);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void setViewPos(Vector3f viewPos) {
        glUniform3f(viewPosLoc, viewPos.x, viewPos.y, viewPos.z);
    }

    public void setWaveTime(float waveTime) {
        glUniform1f(waveTimeLoc, waveTime);
    }

    public void setWaveLength(float waveLength) {
        glUniform1f(waveLengthLoc, waveLength);
    }

    public void setWaveAmplitude(float waveAmplitude) {
        glUniform1f(waveAmplitudeLoc, waveAmplitude);
    }

    public void setDistortionScale(float distortionScale) {
        glUniform1f(distortionScaleLoc, distortionScale);
    }

    public void setAmbientStrength(float strength) {
        glUniform1f(ambientStrengthLoc, strength);
    }

    public void setShininess(float shininess) {
        glUniform1f(shininessLoc, shininess);
    }

    public void setSpecularStrength(float strength) {
        glUniform1f(specularStrengthLoc, strength);
    }

    public void setFogColor(float[] color) {
        glUniform3fv(fogColorLoc, color);
    }

    public void setFogLimits(float start, float end) {
        glUniform1f(fogStartLoc, start);
        glUniform1f(fogEndLoc, end);
    }
}
