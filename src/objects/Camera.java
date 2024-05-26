package src.objects;

import src.utils.Matrix4f;
import src.utils.Vector3f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Camera {
    public Vector3f position;
    private Vector3f front;
    private Vector3f up;
    private Vector3f right;
    private Vector3f worldUp;

    private float yaw;
    private float pitch;

    private float movementSpeed;
    private float mouseSensitivity;

    public Camera(Vector3f position, Vector3f up, float yaw, float pitch) {
        this.position = position;
        this.worldUp = up;
        this.yaw = yaw;
        this.pitch = pitch;
        this.front = new Vector3f(0.0f, 0.0f, -1.0f);
        this.movementSpeed = 2.5f;
        this.mouseSensitivity = 0.1f;

        updateCameraVectors();
    }

    public float[] getViewMatrix() {
        return Matrix4f.lookAt(position, new Vector3f(position).add(front), up);
    }

    public void gluLookAt() {
        float centerX = new Vector3f(position).add(front).x;
        float centerY = new Vector3f(position).add(front).y;
        float centerZ = new Vector3f(position).add(front).z;
        float eyeX = position.x;
        float eyeY = position.y;
        float eyeZ = position.z;
        float upX = up.x;
        float upY = up.y;
        float upZ = up.z;

        float[] forward = {centerX - eyeX, centerY - eyeY, centerZ - eyeZ};
        float[] up = {upX, upY, upZ};

        float forwardLength = (float) Math.sqrt(forward[0] * forward[0] + forward[1] * forward[1] + forward[2] * forward[2]);
        forward[0] /= forwardLength;
        forward[1] /= forwardLength;
        forward[2] /= forwardLength;

        float[] side = {
                forward[1] * up[2] - forward[2] * up[1],
                forward[2] * up[0] - forward[0] * up[2],
                forward[0] * up[1] - forward[1] * up[0]
        };

        up[0] = side[1] * forward[2] - side[2] * forward[1];
        up[1] = side[2] * forward[0] - side[0] * forward[2];
        up[2] = side[0] * forward[1] - side[1] * forward[0];

        float[] mat = {
                side[0], up[0], -forward[0], 0,
                side[1], up[1], -forward[1], 0,
                side[2], up[2], -forward[2], 0,
                0,       0,     0,            1
        };

        glLoadMatrixf(mat);
        glTranslatef(-eyeX, -eyeY, -eyeZ);
    }

    public void processKeyboard(boolean[] keys, float deltaTime) {
        float velocity = movementSpeed * deltaTime;
        if (keys[GLFW_KEY_LEFT_SHIFT]) {
            velocity *= 2f;
        }
        if (keys[GLFW_KEY_W]) {
            position.add(new Vector3f(front).mul(velocity));
        }
        if (keys[GLFW_KEY_S]) {
            position.sub(new Vector3f(front).mul(velocity));
        }
        if (keys[GLFW_KEY_A]) {
            position.sub(new Vector3f(right).mul(velocity));
        }
        if (keys[GLFW_KEY_D]) {
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

        right = Vector3f.cross(front, worldUp);
        right.normalize();
        up = Vector3f.cross(right, front);
        up.normalize();
    }
}
