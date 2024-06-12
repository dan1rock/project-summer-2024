package src.shaderPrograms;

public class SkyboxShader extends ShaderProgram {
    public SkyboxShader() {
        super("src/shaders/skyboxVertex.glsl", "src/shaders/skyboxFragment.glsl");
    }
}
