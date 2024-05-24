package src;

import static org.lwjgl.opengl.GL20.*;

public class ShaderUtils {
    public static int createShaderProgram(String vertexShaderSource, String fragmentShaderSource) {
        int vertexShader = compileShader(vertexShaderSource, GL_VERTEX_SHADER);
        int fragmentShader = compileShader(fragmentShaderSource, GL_FRAGMENT_SHADER);

        int program = glCreateProgram();
        glAttachShader(program, vertexShader);
        glAttachShader(program, fragmentShader);
        glLinkProgram(program);

        if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
            System.err.println("Shader program linking failed!");
            System.err.println(glGetProgramInfoLog(program));
            return -1;
        }

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);

        return program;
    }

    private static int compileShader(String shaderSource, int shaderType) {
        int shader = glCreateShader(shaderType);
        glShaderSource(shader, shaderSource);
        glCompileShader(shader);

        if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
            System.err.println("Shader compilation failed!");
            System.err.println(glGetShaderInfoLog(shader));
            return -1;
        }

        return shader;
    }
}
