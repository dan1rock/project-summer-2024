import src.Cube;
import src.Renderer;
import src.Vector3f;

public class Main {
    public static void main(String[] args) {
        Renderer renderer = new Renderer();
        Cube cube1 = new Cube();
        Cube cube2 = new Cube();

        cube1.position = new Vector3f(2f, 0f, 0f);

        cube2.position = new Vector3f(-2f, 0f, 0f);
        cube2.rotation = new Vector3f(45f, 60f, 0f);

        renderer.renderObjects.add(cube1);
        renderer.renderObjects.add(cube2);

        renderer.runMainLoop();
    }
}
