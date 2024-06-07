package src.shaderPrograms;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

public class ShadowShader extends ShaderProgram {
    private final int lightSpaceMatrixLoc;
    private final int modelLoc;

    public ShadowShader() {
        super("src/shaders/shadowVertex.glsl", "src/shaders/shadowFragment.glsl");

        lightSpaceMatrixLoc = glGetUniformLocation(shader, "lightSpaceMatrix");
        modelLoc = glGetUniformLocation(shader, "model");
    }

    public void setLightSpaceMatrix(float[] matrix) {
        glUniformMatrix4fv(lightSpaceMatrixLoc, false, matrix);
    }

    public void setModelMatrix(float[] matrix) {
        glUniformMatrix4fv(modelLoc, false, matrix);
    }
}
