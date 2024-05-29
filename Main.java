import src.objects.Camera;
import src.objects.Terrain;
import src.rendering.*;
import src.utils.FileUtils;
import src.utils.ShaderUtils;
import src.utils.Vector3f;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Renderer renderer = Renderer.getInstance();
        renderer.camera = new Camera(new Vector3f(0f, 0f, 8f), new Vector3f(0f, 1f, 0f), -90f, 0f);

        String mainVertexShader = FileUtils.readFileAsString("src/shaders/mainVertex.glsl");
        String mainFragmentShader = FileUtils.readFileAsString("src/shaders/mainFragment.glsl");
        int mainShaderProgramID = ShaderUtils.createShaderProgram(mainVertexShader, mainFragmentShader);

        String waterVertexShader = FileUtils.readFileAsString("src/shaders/waterVertex.glsl");
        String waterFragmentShader = FileUtils.readFileAsString("src/shaders/waterFragment.glsl");
        int waterShaderProgramID = ShaderUtils.createShaderProgram(waterVertexShader, waterFragmentShader);

        Mesh penguinMesh = new Mesh();
        Mesh rabbitMesh = new Mesh();
        Mesh baseMesh = new Mesh();
        int penguinTexture;
        int gradientTexture;
        int grassTexture;
        int baseTexture;

        try {
            penguinMesh.loadObj("./Meshes/penguin.obj");
            rabbitMesh.loadObj("./Meshes/rabbit.obj");
            baseMesh.loadObj("./Meshes/base.obj", true);
            penguinTexture = Texture.loadRGBTexture("./Textures/penguin.png");
            gradientTexture = Texture.loadRGBTexture("./Textures/256-gradient.png");
            grassTexture = Texture.loadRGBTexture("./Textures/grass.jpg");
            baseTexture = Texture.loadRGBTexture("./Textures/base.png");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        RenderMesh penguin = new RenderMesh(penguinMesh, mainShaderProgramID);
        RenderMesh rabbit = new RenderMesh(rabbitMesh, mainShaderProgramID);
        penguin.setTexture(penguinTexture);
        rabbit.setTexture(gradientTexture);
        penguin.scale.mul(3f);

        rabbit.position = new Vector3f(2f, 0f, 0f);

        Terrain terrainMesh = new Terrain(500,  500, 10, 25f);
        RenderMesh terrain = new RenderMesh(terrainMesh, mainShaderProgramID);
        terrain.position = new Vector3f(-250f, -0f, -250f);
        terrain.setObjectColor(new Vector3f(0.05f, 0.7f, 0.4f));
        terrain.setTexture(grassTexture);
        terrain.setAmbient(0.6f);
        terrain.setSpecularStrength(0f);
        renderer.renderObjects.add(terrain);
        terrainMesh = new Terrain(500,  500, 0, 25f);
        WaterRenderer water = new WaterRenderer(terrainMesh, waterShaderProgramID);
        water.position = new Vector3f(-250f, 0f, -250f);
        renderer.renderObjects.add(water);

        RenderMesh base = new RenderMesh(baseMesh, mainShaderProgramID);
        base.setTexture(baseTexture);
        base.position = new Vector3f(0f, 0f, -10f);
        base.rotation = new Vector3f(0f, -90f, 0f);
        base.scale = new Vector3f(0.2f, 0.2f, 0.2f);

        renderer.renderObjects.add(rabbit);
        renderer.renderObjects.add(penguin);
        renderer.renderObjects.add(base);

        renderer.runMainLoop();
    }
}
