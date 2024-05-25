import src.*;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Renderer renderer = new Renderer();
        String vertexShader = FileUtils.readFileAsString("./src/shaders/vertex.glsl");
        String fragmentShader = FileUtils.readFileAsString("./src/shaders/fragment.glsl");
        int shaderProgramID = ShaderUtils.createShaderProgram(vertexShader, fragmentShader);

        Mesh penguinMesh = new Mesh();
        int penguinTexture;

        try {
            penguinMesh.loadObj("./Meshes/penguin.obj");
            penguinTexture = Texture.loadRGBTexture("./Textures/penguin.png");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        RenderMesh penguin = new RenderMesh(penguinMesh, shaderProgramID);
        penguin.setTexture(penguinTexture);
        penguin.scale.mul(3f);

        Cube cube1 = new Cube();
        Cube cube2 = new Cube();

        cube1.position = new Vector3f(2f, 0f, 0f);

        cube2.position = new Vector3f(-2f, 0f, 0f);
        cube2.rotation = new Vector3f(45f, 60f, 0f);

        renderer.renderObjects.add(cube1);
        renderer.renderObjects.add(cube2);
        renderer.renderObjects.add(penguin);

        renderer.runMainLoop();
    }
}
