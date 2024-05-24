import src.*;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Renderer renderer = new Renderer();
        Mesh teapotMesh = new Mesh();

        try {
            teapotMesh.loadObj("./Meshes/teapot.obj");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        RenderMesh teapot = new RenderMesh(teapotMesh);
        teapot.scale.mul(0.5f);

        Cube cube1 = new Cube();
        Cube cube2 = new Cube();

        cube1.position = new Vector3f(2f, 0f, 0f);

        cube2.position = new Vector3f(-2f, 0f, 0f);
        cube2.rotation = new Vector3f(45f, 60f, 0f);

//        renderer.renderObjects.add(cube1);
//        renderer.renderObjects.add(cube2);
        renderer.renderObjects.add(teapot);

        renderer.runMainLoop();
    }
}
