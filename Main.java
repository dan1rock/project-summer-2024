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

        MainShader mainShader = new MainShader();
        WaterShader waterShader = new WaterShader();

        renderEngine.shaders.add(mainShader);
        renderEngine.shaders.add(waterShader);

        Mesh penguinMesh = new Mesh("./Meshes/penguin.obj");
        Mesh rabbitMesh = new Mesh("./Meshes/rabbit.obj");
        Mesh baseMesh = new Mesh("./Meshes/base.obj", true);
        Texture penguinTexture = new Texture("./Textures/penguin.png");
        Texture gradientTexture = new Texture("./Textures/256-gradient.png");
        Texture grassTexture = new Texture("./Textures/grass.jpg");
        Texture baseTexture = new Texture("./Textures/base.png");

        MeshRenderer penguin = new MeshRenderer(penguinMesh, mainShader);
        MeshRenderer rabbit = new MeshRenderer(rabbitMesh, mainShader);
        penguin.setTexture(penguinTexture);
        rabbit.setTexture(gradientTexture);
        penguin.scale.mul(3f);

        rabbit.position = new Vector3f(2f, 0f, 0f);

        TerrainMesh terrainMesh = new TerrainMesh(500,  500, 20, 0.05f, 25f);
        MeshRenderer terrain = new MeshRenderer(terrainMesh, mainShader);
        terrain.position = new Vector3f(-250f, -0f, -250f);
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
