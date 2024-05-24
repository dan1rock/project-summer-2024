package src;

public class Vector3f {
    public float x, y, z;

    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void normalize() {
        float length = (float) Math.sqrt(x * x + y * y + z * z);
        if (length != 0) {
            x /= length;
            y /= length;
            z /= length;
        }
    }

    public void mul(float value) {
        x *= value;
        y *= value;
        z *= value;
    }

    public static Vector3f sub(Vector3f left, Vector3f right) {
        return new Vector3f(left.x - right.x, left.y - right.y, left.z - right.z);
    }

    public static Vector3f cross(Vector3f a, Vector3f b) {
        return new Vector3f(
                a.y * b.z - a.z * b.y,
                a.z * b.x - a.x * b.z,
                a.x * b.y - a.y * b.x
        );
    }
}
