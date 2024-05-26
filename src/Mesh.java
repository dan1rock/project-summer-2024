package src;

import org.lwjgl.BufferUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;

public class Mesh {
    public int numIndices;
    public int vertexVboId;
    public int textureVboId;
    public int normalVboId;
    public int indexVboId;

    public void loadObj(String filename) throws IOException {
        List<Vector3f> vertices = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Vector3f> textureCoords = new ArrayList<>();
        List<Integer> vertexIndices = new ArrayList<>();
        List<Integer> normalIndices = new ArrayList<>();
        List<Integer> textureIndices = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new FileReader(filename));
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
                    normals.add(new Vector3f(nx, ny, nz));
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

        for (Integer index : vertexIndices) {
            Vector3f vertex = vertices.get(index);
            verticesBuffer.put(vertex.x).put(vertex.y).put(vertex.z);
        }
        for (Integer index : textureIndices) {
            Vector3f coord = textureCoords.get(index);
            textureBuffer.put(coord.y).put(coord.x);
        }
        for (Integer index : normalIndices) {
            Vector3f normal = normals.get(index);
            normalsBuffer.put(normal.x).put(normal.y).put(normal.z);
        }
        verticesBuffer.flip();
        textureBuffer.flip();
        normalsBuffer.flip();

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

        IntBuffer indicesBuffer = BufferUtils.createIntBuffer(vertexIndices.size());

        for (int i = 0; i < vertexIndices.size(); i++) {
            indicesBuffer.put(i);
        }

        indicesBuffer.flip();
        indexVboId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexVboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

        numIndices = vertexIndices.size();
    }
}
