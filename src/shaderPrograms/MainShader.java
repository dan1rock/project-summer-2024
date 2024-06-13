package src.shaderPrograms;

import src.utils.Vector3f;

import static org.lwjgl.opengl.GL20.*;

public class MainShader extends ShaderProgram {
    private final int modelLoc;
    private final int viewPosLoc;
    private final int lightSpaceLoc;
    private final int objectColorLoc;
    private final int ambientStrengthLoc;
    private final int shininessLoc;
    private final int specularStrengthLoc;
    private final int isTexturedLoc;
    private final int fogColorLoc;
    private final int fogStartLoc;
    private final int fogEndLoc;
    private final int clipPlaneLoc;
    private final int useClipPlaneLoc;
    private final int shadowMapLoc;
    private final int mainTexLoc;

    public MainShader() {
        super("src/shaders/main.vert", "src/shaders/main.frag");

        modelLoc = glGetUniformLocation(shader, "model");
        viewPosLoc = glGetUniformLocation(shader, "viewPos");
        lightSpaceLoc = glGetUniformLocation(shader, "lightSpace");
        objectColorLoc = glGetUniformLocation(shader, "objectColor");
        ambientStrengthLoc = glGetUniformLocation(shader, "ambientStrength");
        shininessLoc = glGetUniformLocation(shader, "shininess");
        specularStrengthLoc = glGetUniformLocation(shader, "specularStrength");
        isTexturedLoc = glGetUniformLocation(shader, "isTextured");
        fogColorLoc = glGetUniformLocation(shader, "fogColor");
        fogStartLoc = glGetUniformLocation(shader, "fogStart");
        fogEndLoc = glGetUniformLocation(shader, "fogEnd");
        clipPlaneLoc = glGetUniformLocation(shader, "clipPlane");
        useClipPlaneLoc = glGetUniformLocation(shader, "useClipPlane");
        shadowMapLoc = glGetUniformLocation(shader, "shadowMap");
        mainTexLoc = glGetUniformLocation(shader, "mainTex");
    }

    public void setTextures(int mainTex, int shadowMap) {
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, mainTex);
        glUniform1i(mainTexLoc, 0);

        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, shadowMap);
        glUniform1i(shadowMapLoc, 1);
    }

    public void resetBindings() {
        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void setModelMatrix(float[] matrix) {
        glUniformMatrix4fv(modelLoc, false, matrix);
    }

    public void setLightSpace(float[] matrix) {
        glUniformMatrix4fv(lightSpaceLoc, false, matrix);
    }

    public void setViewPos(Vector3f viewPos) {
        glUniform3f(viewPosLoc, viewPos.x, viewPos.y, viewPos.z);
    }

    public void setObjectColor(float[] color) {
        glUniform3fv(objectColorLoc, color);
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

    public void isTextured(boolean state) {
        glUniform1i(isTexturedLoc, state ? 1 : 0);
    }

    public void setFogColor(float[] color) {
        glUniform3fv(fogColorLoc, color);
    }

    public void setFogLimits(float start, float end) {
        glUniform1f(fogStartLoc, start);
        glUniform1f(fogEndLoc, end);
    }

    public void setClipPlane(float[] clipPlane) {
        glUniform4fv(clipPlaneLoc, clipPlane);
    }

    public void useClipPlane(boolean state) {
        glUniform1i(useClipPlaneLoc, state ? 1 : 0);
    }
}
