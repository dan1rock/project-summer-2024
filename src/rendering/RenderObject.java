package src.rendering;

import src.utils.Vector3f;

public abstract class RenderObject {
    public Vector3f position;
    public Vector3f rotation;
    public Vector3f scale;

    protected final Renderer renderer;

    protected RenderObject() {
        renderer = Renderer.getInstance();
    }

    public abstract void Update(float deltaTime);
}
