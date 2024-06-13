package src.mesh;

import org.lwjgl.BufferUtils;
import src.utils.Vector3f;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;

public class Mesh {
    protected int numIndices;
    protected int vertexVBO;
    protected int textureVBO;
    protected int normalVBO;
    protected int indexVBO;

    public Mesh() {

    };

    public Mesh(String path) {
        try {
            loadObj(path, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Mesh(String path, boolean invertNormals) {
        try {
            loadObj(path, invertNormals);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int getNumIndices() {
        return numIndices;
    }

    public int getVertexVBO() {
        return vertexVBO;
    }

    public int getTextureVBO() {
        return textureVBO;
    }

    public int getNormalVBO() {
        return normalVBO;
    }

    public int getIndexVBO() {
        return indexVBO;
    }

    public void loadObj(String path, boolean invertNormals) throws IOException {
        releaseVbos();

        List<Vector3f> vertices = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Vector3f> textureCoords = new ArrayList<>();
        List<Integer> vertexIndices = new ArrayList<>();
        List<Integer> normalIndices = new ArrayList<>();
        List<Integer> textureIndices = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new FileReader(path));
        String line;

        while ((line = reader.readLine()) != null) {
            String[] parts = line.split("\\s+");
            switch (parts[0]) {
                case "v":
                    float vx = Float.parseFloat(parts[1]);
                    float vy = Float.parseFloat(parts[2]);
                    float vz = Float.parseFloat(parts[3]);
                    vertices.add(new Vector3f(vx, vy, vz));
                    break;
                case "vn":
                    float nx = Float.parseFloat(parts[1]);
                    float ny = Float.parseFloat(parts[2]);
                    float nz = Float.parseFloat(parts[3]);
                    Vector3f normal = new Vector3f(nx, ny, nz);
                    if (invertNormals) normal.invert();
                    normals.add(normal);
                    break;
                case "vt":
                    float tx = Float.parseFloat(parts[1]);
                    float ty = Float.parseFloat(parts[2]);
                    textureCoords.add(new Vector3f(tx, ty, 0f));
                    break;
                case "f":
                    for (int i = 1; i < parts.length; i++) {
                        String[] indicesStr = parts[i].split("/");
                        int vertexIndex = Integer.parseInt(indicesStr[0]) - 1;
                        int texIndex = Integer.parseInt(indicesStr[1]) - 1;
                        int normalIndex = Integer.parseInt(indicesStr[2]) - 1;
                        vertexIndices.add(vertexIndex);
                        textureIndices.add(texIndex);
                        normalIndices.add(normalIndex);
                    }
                    break;
                default:
                    break;
            }
        }

        reader.close();

        FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(vertexIndices.size() * 3);
        FloatBuffer textureBuffer = BufferUtils.createFloatBuffer(textureIndices.size() * 2);
        FloatBuffer normalsBuffer = BufferUtils.createFloatBuffer(normalIndices.size() * 3);

        int size = vertexIndices.size();

        int index = size - 1;
        int sign = -1;

        if (invertNormals) {
            sign = 1;
            index = 0;
        }

        for (int i = 0; i < size; i++) {
            int vertexIndex = vertexIndices.get(index);
            Vector3f vertex = vertices.get(vertexIndex);
            verticesBuffer.put(vertex.x).put(vertex.y).put(vertex.z);

            int textureIndex = textureIndices.get(index);
            Vector3f coord = textureCoords.get(textureIndex);
            textureBuffer.put(coord.y).put(coord.x);

            int normalIndex = normalIndices.get(index);
            Vector3f normal = normals.get(normalIndex);
            normalsBuffer.put(normal.x).put(normal.y).put(normal.z);

            index += sign;
        }
        verticesBuffer.flip();
        textureBuffer.flip();
        normalsBuffer.flip();

        vertexVBO = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vertexVBO);
        glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        textureVBO = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, textureVBO);
        glBufferData(GL_ARRAY_BUFFER, textureBuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        normalVBO = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, normalVBO);
        glBufferData(GL_ARRAY_BUFFER, normalsBuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        IntBuffer indicesBuffer = BufferUtils.createIntBuffer(vertexIndices.size());

        for (int i = 0; i < vertexIndices.size(); i++) {
            indicesBuffer.put(i);
        }

        indicesBuffer.flip();
        indexVBO = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexVBO);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

        numIndices = vertexIndices.size();
    }

    public void releaseVbos() {
        if (vertexVBO != 0) {
            glDeleteBuffers(vertexVBO);
        }
        if (textureVBO != 0) {
            glDeleteBuffers(textureVBO);
        }
        if (normalVBO != 0) {
            glDeleteBuffers(normalVBO);
        }
        if (indexVBO != 0) {
            glDeleteBuffers(indexVBO);
        }
        numIndices = 0;
    }
}
