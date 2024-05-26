import src.*;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Renderer renderer = Renderer.getInstance();
        renderer.camera = new Camera(new Vector3f(0f, 0f, 8f), new Vector3f(0f, 1f, 0f), -90f, 0f);;

        String vertexShader = FileUtils.readFileAsString("./src/shaders/vertex.glsl");
        String fragmentShader = FileUtils.readFileAsString("./src/shaders/fragment.glsl");
        int shaderProgramID = ShaderUtils.createShaderProgram(vertexShader, fragmentShader);

        Mesh penguinMesh = new Mesh();
        Mesh rabbitMesh = new Mesh();
        int penguinTexture;
        int gradientTexture;

        try {
            penguinMesh.loadObj("./Meshes/penguin.obj");
            rabbitMesh.loadObj("./Meshes/rabbit.obj");
            penguinTexture = Texture.loadRGBTexture("./Textures/penguin.png");
            gradientTexture = Texture.loadRGBTexture("./Textures/256-gradient.png");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        RenderMesh penguin = new RenderMesh(penguinMesh, shaderProgramID);
        RenderMesh rabbit = new RenderMesh(rabbitMesh, shaderProgramID);
        penguin.setTexture(penguinTexture);
        rabbit.setTexture(gradientTexture);
        penguin.scale.mul(3f);

        rabbit.position = new Vector3f(2f, 0f, 0f);

        Terrain terrainMesh = new Terrain(100,  100, 20);
        RenderMesh terrain = new RenderMesh(terrainMesh, shaderProgramID);
        terrain.scale = new Vector3f(0.5f, 0.5f, 0.5f);
        terrain.setObjectColor(new Vector3f(0.01f, 0.5f, 0.2f));

        renderer.renderObjects.add(rabbit);
        renderer.renderObjects.add(penguin);
        renderer.renderObjects.add(terrain);

        renderer.runMainLoop();
    }
}
