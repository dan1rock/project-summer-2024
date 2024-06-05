package src.rendering;

import src.utils.Vector3f;

public abstract class Renderer {
    public Vector3f position;
    public Vector3f rotation;
    public Vector3f scale;

    protected final RenderEngine renderEngine;

    protected Renderer() {
        renderEngine = RenderEngine.getInstance();
    }

    public void Update() {}
    public void processKeyboard() {}
    public abstract void Render(boolean clipPlane);
}
