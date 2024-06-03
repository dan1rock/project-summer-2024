import src.mesh.Mesh;
import src.objects.Camera;
import src.mesh.TerrainMesh;
import src.mesh.WaterMesh;
import src.rendering.*;
import src.shaderPrograms.MainShader;
import src.shaderPrograms.WaterShader;
import src.utils.FileUtils;
import src.utils.ShaderUtils;
import src.utils.Vector3f;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        RenderEngine renderEngine = RenderEngine.getInstance();
        renderEngine.OnCreate();
        renderEngine.camera = new Camera(new Vector3f(0f, 0f, 8f), new Vector3f(0f, 1f, 0f), -90f, 0f);

        MainShader mainShader = new MainShader("src/shaders/mainVertex.glsl", "src/shaders/mainFragment.glsl");
        WaterShader waterShader = new WaterShader("src/shaders/waterVertex.glsl", "src/shaders/waterFragment.glsl");

        renderEngine.shaders.add(mainShader);
        renderEngine.shaders.add(waterShader);

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

        MeshRenderer penguin = new MeshRenderer(penguinMesh, mainShader);
        MeshRenderer rabbit = new MeshRenderer(rabbitMesh, mainShader);
        penguin.setTexture(penguinTexture);
        rabbit.setTexture(gradientTexture);
        penguin.scale.mul(3f);

        rabbit.position = new Vector3f(2f, 0f, 0f);

        TerrainMesh terrainMesh = new TerrainMesh(500,  500, 20, 0.05f, 25f);
        MeshRenderer terrain = new MeshRenderer(terrainMesh, mainShader);
        terrain.position = new Vector3f(-250f, -0f, -250f);
        terrain.setObjectColor(new Vector3f(0.05f, 0.7f, 0.4f));
        terrain.setTexture(grassTexture);
        terrain.setAmbient(0.6f);
        terrain.setSpecularStrength(0f);
        renderEngine.renderers.add(terrain);
        WaterMesh waterMesh = new WaterMesh(500,  500);
        WaterRenderer water = new WaterRenderer(waterMesh, waterShader);
        water.position = new Vector3f(-250f, 0f, -250f);
        renderEngine.renderers.add(water);

        MeshRenderer base = new MeshRenderer(baseMesh, mainShader);
        base.setTexture(baseTexture);
        base.position = new Vector3f(0f, 0f, -10f);
        base.rotation = new Vector3f(0f, -90f, 0f);
        base.scale = new Vector3f(0.2f, 0.2f, 0.2f);

        renderEngine.renderers.add(rabbit);
        renderEngine.renderers.add(penguin);
        renderEngine.renderers.add(base);

        renderEngine.runMainLoop();
    }
}
