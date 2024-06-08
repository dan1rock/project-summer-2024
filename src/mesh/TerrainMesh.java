package src.mesh;

import org.lwjgl.BufferUtils;
import src.utils.PerlinNoise;
import src.utils.Vector3f;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;

public class TerrainMesh extends Mesh {
    public TerrainMesh(int width, int depth, float maxHeight, float heightMapScale, float textureScale) {
        generateMesh(-1, width, depth, maxHeight, heightMapScale, textureScale);
    }

    public TerrainMesh(int seed, int width, int depth, float maxHeight, float heightMapScale, float textureScale) {
        generateMesh(seed, width, depth, maxHeight, heightMapScale, textureScale);
    }

    private void generateMesh(int seed, int width, int depth, float maxHeight, float heightMapScale, float textureScale) {
        List<Vector3f> vertices = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Vector3f> textureCoords = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        float[][] heightMap = generateHeightMap(seed, width, depth, maxHeight, heightMapScale);

        for (int z = 0; z < depth; z++) {
            for (int x = 0; x < width; x++) {
                float height = heightMap[x][z];
                vertices.add(new Vector3f(x, height, z));
                float u = (float) x / (width - 1) * textureScale;
                float v = (float) z / (depth - 1) * textureScale;
                textureCoords.add(new Vector3f(u, v, 0));
                normals.add(new Vector3f());
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

        calculateNormals(vertices, normals, indices);

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

    private float[][] generateHeightMap(int seed, int width, int depth, float maxHeight, float scale) {
        PerlinNoise perlinNoise;
        if (seed == -1) {
            perlinNoise = new PerlinNoise(2, 1f, 1f);
        } else {
            perlinNoise = new PerlinNoise(seed, 2, 1f, 1f);
        }

        float[][] heightMap = new float[width][depth];
        for (int z = 0; z < depth; z++) {
            for (int x = 0; x < width; x++) {
                heightMap[x][z] = perlinNoise.getPerlinNoise(x * scale, z * scale) * maxHeight;
            }
        }
        return heightMap;
    }

    private void calculateNormals(List<Vector3f> vertices, List<Vector3f> normals, List<Integer> indices) {
        for (int i = 0; i < indices.size(); i += 3) {
            int index0 = indices.get(i);
            int index1 = indices.get(i + 1);
            int index2 = indices.get(i + 2);

            Vector3f v0 = vertices.get(index0);
            Vector3f v1 = vertices.get(index1);
            Vector3f v2 = vertices.get(index2);

            Vector3f edge1 = new Vector3f(v1).sub(v0);
            Vector3f edge2 = new Vector3f(v0).sub(v2);

            Vector3f normal = Vector3f.cross(edge1, edge2).normalize();

            normals.get(index0).add(normal);
            normals.get(index1).add(normal);
            normals.get(index2).add(normal);
        }
    }
}
