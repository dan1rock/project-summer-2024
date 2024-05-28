package src.utils;

public class Matrix4f {
    public static float[] lookAt(Vector3f eye, Vector3f center, Vector3f up) {
        Vector3f f = new Vector3f(center).sub(eye).normalize();
        Vector3f u = new Vector3f(up).normalize();
        Vector3f s = new Vector3f(f).cross(u).normalize();
        u = new Vector3f(s).cross(f);

        float[] result = new float[] {
                s.x, u.x, -f.x, 0f,
                s.y, u.y, -f.y, 0f,
                s.z, u.z, -f.z, 0f,
                -s.dot(eye), -u.dot(eye), f.dot(eye), 1f
        };

        return result;
    }
}
