package src.utils;

public class Vector3f {
    public float x, y, z;

    public Vector3f() {
        this.x = 0f;
        this.y = 0f;
        this.z = 0f;
    }


    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3f(Vector3f vector3f) {
        this.x = vector3f.x;
        this.y = vector3f.y;
        this.z = vector3f.z;
    }

    public Vector3f normalize() {
        float length = (float) Math.sqrt(x * x + y * y + z * z);
        if (length != 0) {
            x /= length;
            y /= length;
            z /= length;
        }
        return this;
    }

    public Vector3f invert() {
        x = -x;
        y = -y;
        z = -z;
        return this;
    }

    public Vector3f mul(float value) {
        x *= value;
        y *= value;
        z *= value;
        return this;
    }

    public Vector3f add(Vector3f vector3f) {
        x += vector3f.x;
        y += vector3f.y;
        z += vector3f.z;
        return this;
    }

    public Vector3f sub(Vector3f vector3f) {
        x -= vector3f.x;
        y -= vector3f.y;
        z -= vector3f.z;
        return this;
    }

    public static Vector3f sub(Vector3f left, Vector3f right) {
        return new Vector3f(left.x - right.x, left.y - right.y, left.z - right.z);
    }

    public Vector3f cross(Vector3f vector3f) {
        x = y * vector3f.z - z * vector3f.y;
        y = z * vector3f.x - x * vector3f.z;
        z = x * vector3f.y - y * vector3f.x;
        return this;
    }

    public float dot(Vector3f vector3f) {
        return x * vector3f.x + y * vector3f.y + z * vector3f.z;
    }

    public static Vector3f cross(Vector3f a, Vector3f b) {
        return new Vector3f(
                a.y * b.z - a.z * b.y,
                a.z * b.x - a.x * b.z,
                a.x * b.y - a.y * b.x
        );
    }
}
