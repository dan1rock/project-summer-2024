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

    public static float[] createOrthoMatrix(float left, float right, float bottom, float top, float near, float far) {
        float[] orthoMatrix = new float[16];

        orthoMatrix[0] = 2.0f / (right - left);
        orthoMatrix[1] = 0.0f;
        orthoMatrix[2] = 0.0f;
        orthoMatrix[3] = 0.0f;

        orthoMatrix[4] = 0.0f;
        orthoMatrix[5] = 2.0f / (top - bottom);
        orthoMatrix[6] = 0.0f;
        orthoMatrix[7] = 0.0f;

        orthoMatrix[8] = 0.0f;
        orthoMatrix[9] = 0.0f;
        orthoMatrix[10] = -2.0f / (far - near);
        orthoMatrix[11] = 0.0f;

        orthoMatrix[12] = -(right + left) / (right - left);
        orthoMatrix[13] = -(top + bottom) / (top - bottom);
        orthoMatrix[14] = -(far + near) / (far - near);
        orthoMatrix[15] = 1.0f;

        return orthoMatrix;
    }

    public static float[] multiply(float[] mat1, float[] mat2) {
        if (mat1.length != 16 || mat2.length != 16) {
            throw new IllegalArgumentException("Wrong dimensions");
        }

        float[] result = new float[16];

        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                result[row * 4 + col] = 0;
                for (int k = 0; k < 4; k++) {
                    result[row * 4 + col] += mat1[row * 4 + k] * mat2[k * 4 + col];
                }
            }
        }

        return result;
    }
}
