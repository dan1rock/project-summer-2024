package src;

public abstract class RenderObject {
    public Vector3f position;
    public Vector3f rotation;
    public Vector3f scale;

    public abstract void Update(float deltaTime);
}
