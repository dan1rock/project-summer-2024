package src.objects;

import src.utils.Input;
import src.utils.Matrix4f;
import src.utils.Time;
import src.utils.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class Camera {
    public Vector3f position;
    private Vector3f front;
    private Vector3f up;
    private Vector3f right;
    private Vector3f worldUp;

    private float yaw;
    private float pitch;

    private float baseSpeed;
    private float movementSpeed;
    private float mouseSensitivity;

    public Camera(Vector3f position, Vector3f up, float yaw, float pitch) {
        this.position = position;
        this.worldUp = up;
        this.yaw = yaw;
        this.pitch = pitch;
        this.front = new Vector3f(0.0f, 0.0f, -1.0f);
        this.baseSpeed = 5f;
        this.movementSpeed = 5f;
        this.mouseSensitivity = 0.1f;

        updateCameraVectors();
    }

    public float[] getViewMatrix() {
        return Matrix4f.lookAt(position, new Vector3f(position).add(front), up);
    }

    public float[] getReflectionMatrix() {
        Vector3f front = new Vector3f();
        front.x = (float) Math.cos(Math.toRadians(yaw)) * (float) Math.cos(Math.toRadians(-pitch));
        front.y = (float) Math.sin(Math.toRadians(-pitch));
        front.z = (float) Math.sin(Math.toRadians(yaw)) * (float) Math.cos(Math.toRadians(-pitch));
        front.normalize();
        Vector3f right = Vector3f.cross(front, worldUp).normalize();
        Vector3f up = Vector3f.cross(right, front).normalize();
        Vector3f position = new Vector3f(this.position);
        position.y = -position.y;
        return Matrix4f.lookAt(position, new Vector3f(position).add(front), up);
    }

    public void processKeyboard() {
        float velocity = movementSpeed * Time.deltaTime;
        if (Input.getKey(GLFW_KEY_LEFT_SHIFT)) {
            velocity *= 2f;
            movementSpeed *= 1f + 0.5f * Time.deltaTime;
        } else {
            movementSpeed = baseSpeed;
        }
        if (Input.getKey(GLFW_KEY_W)) {
            position.add(new Vector3f(front).mul(velocity));
        }
        if (Input.getKey(GLFW_KEY_S)) {
            position.sub(new Vector3f(front).mul(velocity));
        }
        if (Input.getKey(GLFW_KEY_A)) {
            position.sub(new Vector3f(right).mul(velocity));
        }
        if (Input.getKey(GLFW_KEY_D)) {
            position.add(new Vector3f(right).mul(velocity));
        }
    }

    public void processMouseMovement(float xOffset, float yOffset) {
        xOffset *= mouseSensitivity;
        yOffset *= mouseSensitivity;

        yaw += xOffset;
        pitch += yOffset;

        if (pitch > 89.0f)
            pitch = 89.0f;
        if (pitch < -89.0f)
            pitch = -89.0f;

        updateCameraVectors();
    }

    public void updateCameraVectors() {
        Vector3f front = new Vector3f();
        front.x = (float) Math.cos(Math.toRadians(yaw)) * (float) Math.cos(Math.toRadians(pitch));
        front.y = (float) Math.sin(Math.toRadians(pitch));
        front.z = (float) Math.sin(Math.toRadians(yaw)) * (float) Math.cos(Math.toRadians(pitch));
        front.normalize();
        this.front = front;

        right = Vector3f.cross(front, worldUp).normalize();
        up = Vector3f.cross(right, front).normalize();
    }
}
