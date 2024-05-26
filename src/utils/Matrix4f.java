package src.utils;

public class Matrix4f {
    public static float[] lookAt(Vector3f eye, Vector3f center, Vector3f up) {
        Vector3f f = new Vector3f(center).sub(eye).normalize();
        Vector3f u = new Vector3f(up).normalize();
        Vector3f s = new Vector3f(f).cross(u).normalize();
        u = new Vector3f(s).cross(f);

        float[] result = new float[] {
                s.x, u.x, -f.x, -s.dot(eye),
                s.y, u.y, -f.y, -u.dot(eye),
                s.z, u.z, -f.z, f.dot(eye),
                0f, 0f, 0f, 1f,
        };

        return result;
    }
}
