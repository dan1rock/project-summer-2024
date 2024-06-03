package src.shaderPrograms;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.*;

public class TextShader extends ShaderProgram {
    private final int modelLoc;
    private final int textureLoc;
    private final int textColorLoc;

    public TextShader() {
        super("src/shaders/textVertex.glsl", "src/shaders/textFragment.glsl");

        modelLoc = glGetUniformLocation(shader, "model");
        textureLoc = glGetUniformLocation(shader, "font");
        textColorLoc = glGetUniformLocation(shader, "textColor");
    }

    public void setModelMatrix(float[] matrix) {
        glUniformMatrix4fv(modelLoc, false, matrix);
    }

    public void bindTexture(int fontTexture) {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glUniform1i(textureLoc, 0);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, fontTexture);
    }

    public void resetBindings() {
        glDisable(GL_BLEND);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void setTextColor(float[] color) {
        glUniform3fv(textColorLoc, color);
    }
}
