package src.shaderPrograms;

import src.utils.FileUtils;
import src.utils.ShaderUtils;
import src.utils.Vector3f;

import static org.lwjgl.opengl.GL20.*;

public class ShaderProgram {
    protected final int shader;
    protected final int viewLoc;
    protected final int projectionLoc;
    protected final int lightPosLoc;
    protected final int lightColorLoc;

    public ShaderProgram(String vertexPath, String fragmentPath) {
        String mainVertexShader = FileUtils.readFileAsString(vertexPath);
        String mainFragmentShader = FileUtils.readFileAsString(fragmentPath);
        shader = ShaderUtils.createShaderProgram(mainVertexShader, mainFragmentShader);

        viewLoc = glGetUniformLocation(shader, "view");
        projectionLoc = glGetUniformLocation(shader, "projection");
        lightPosLoc = glGetUniformLocation(shader, "lightPos");
        lightColorLoc = glGetUniformLocation(shader, "lightColor");
    }

    public int getShader() {
        return shader;
    }

    public void use() {
        glUseProgram(shader);
    }

    public void setViewMatrix(float[] matrix) {
        glUniformMatrix4fv(viewLoc, false, matrix);
    }

    public void setProjectionMatrix(float[] matrix) {
        glUniformMatrix4fv(projectionLoc, false, matrix);
    }

    public void setLightPos(Vector3f lightPos) {
        glUniform3f(lightPosLoc, lightPos.x, lightPos.y, lightPos.z);
    }

    public void setLightColor(float[] color) {
        glUniform3fv(lightColorLoc, color);
    }
}
