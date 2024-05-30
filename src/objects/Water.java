package src.objects;

import org.lwjgl.BufferUtils;
import src.rendering.Mesh;
import src.utils.Vector3f;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;

public class Water extends Mesh {
    public Water(int width, int depth) {
        List<Vector3f> vertices = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Vector3f> textureCoords = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        for (int z = 0; z < depth; z++) {
            for (int x = 0; x < width; x++) {
                vertices.add(new Vector3f(x, 0f, z));
                float u = (float) x / (width - 1);
                float v = (float) z / (depth - 1);
                textureCoords.add(new Vector3f(u, v, 0));
                normals.add(new Vector3f(0f, 1f, 0f));
            }
        }

        for (int z = 0; z < depth - 1; z++) {
            for (int x = 0; x < width - 1; x++) {
                int topLeft = (z * width) + x;
                int topRight = topLeft + 1;
                int bottomLeft = ((z + 1) * width) + x;
                int bottomRight = bottomLeft + 1;

                indices.add(topRight);
                indices.add(bottomLeft);
                indices.add(topLeft);

                indices.add(bottomRight);
                indices.add(bottomLeft);
                indices.add(topRight);
            }
        }

        FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(vertices.size() * 3);
        FloatBuffer textureBuffer = BufferUtils.createFloatBuffer(textureCoords.size() * 2);
        FloatBuffer normalsBuffer = BufferUtils.createFloatBuffer(normals.size() * 3);
        IntBuffer indicesBuffer = BufferUtils.createIntBuffer(indices.size());

        for (Vector3f vertex : vertices) {
            verticesBuffer.put(vertex.x).put(vertex.y).put(vertex.z);
        }
        for (Vector3f textureCoord : textureCoords) {
            textureBuffer.put(textureCoord.y).put(textureCoord.x);
        }
        for (Vector3f normal : normals) {
            normalsBuffer.put(normal.x).put(normal.y).put(normal.z);
        }
        for (Integer index : indices) {
            indicesBuffer.put(index);
        }

        verticesBuffer.flip();
        textureBuffer.flip();
        normalsBuffer.flip();
        indicesBuffer.flip();

        vertexVboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vertexVboId);
        glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        textureVboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, textureVboId);
        glBufferData(GL_ARRAY_BUFFER, textureBuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        normalVboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, normalVboId);
        glBufferData(GL_ARRAY_BUFFER, normalsBuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        indexVboId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexVboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

        numIndices = indices.size();
    }
}
