package src;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;

public class RenderMesh extends RenderObject{
    private final Mesh mesh;
    private int texture;
    private final int shaderProgramID;
    private boolean isTextured = false;

    public RenderMesh(Mesh mesh, int shaderProgramID) {
        this.mesh = mesh;
        this.shaderProgramID = shaderProgramID;
        this.position = new Vector3f(0f, 0f, 0f);
        this.rotation = new Vector3f(0f, 0f, 0f);
        this.scale = new Vector3f(1f, 1f, 1f);
    }

    public RenderMesh(Mesh mesh, int shaderProgramID, Vector3f position, Vector3f rotation, Vector3f scale) {
        this.mesh = mesh;
        this.shaderProgramID = shaderProgramID;
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }

    public void setTexture(int textureID) {
        texture = textureID;
        isTextured = true;
    }

    @Override
    public void Update(float deltaTime) {
        Draw();
    }

    public void Draw() {
        glEnable(GL_LIGHTING);

        if (isTextured) {
            glEnable(GL_TEXTURE_2D);
            glBindTexture(GL_TEXTURE_2D, texture);
            glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_DECAL);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
        }
        else {
            glDisable(GL_TEXTURE_2D);
        }

        glPushMatrix();

        glTranslatef(position.x, position.y, position.z);
        glRotatef(rotation.x, 1, 0, 0);
        glRotatef(rotation.y, 0, 1, 0);
        glRotatef(rotation.z, 0, 0, 1);
        glScalef(scale.x, scale.y, scale.z);

        glUseProgram(shaderProgramID);

        int lightPosLoc = glGetUniformLocation(shaderProgramID, "lightPos");
        int viewPosLoc = glGetUniformLocation(shaderProgramID, "viewPos");
        int lightColorLoc = glGetUniformLocation(shaderProgramID, "lightColor");
        int objectColorLoc = glGetUniformLocation(shaderProgramID, "objectColor");
        int ambientStrengthLoc = glGetUniformLocation(shaderProgramID, "ambientStrength");
        int shininessLoc = glGetUniformLocation(shaderProgramID, "shininess");

        glUniform3f(lightPosLoc, 1.0f, 1.0f, 1.0f);
        glUniform3f(viewPosLoc, 0.0f, 0.0f, 5.0f);
        glUniform3f(lightColorLoc, 1.0f, 1.0f, 1.0f);
        glUniform3f(objectColorLoc, 1.0f, 1.0f, 1.0f);
        glUniform1f(ambientStrengthLoc, 0.5f);
        glUniform1f(shininessLoc, 32.0f);

        int modelLoc = glGetUniformLocation(shaderProgramID, "model");
        int projLoc = glGetUniformLocation(shaderProgramID, "projection");

        float[] modelMatrix = new float[16];
        float[] projectionMatrix = new float[16];

        glGetFloatv(GL_MODELVIEW_MATRIX, modelMatrix);
        glGetFloatv(GL_PROJECTION_MATRIX, projectionMatrix);

        glUniformMatrix4fv(modelLoc, false, modelMatrix);
        glUniformMatrix4fv(projLoc, false, projectionMatrix);

        glBindBuffer(GL_ARRAY_BUFFER, mesh.vertexVboId);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, mesh.textureVboId);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, mesh.normalVboId);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

        glColor3fv(Color.Magenta);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, mesh.indexVboId);

        glDrawElements(GL_TRIANGLES, mesh.numIndices, GL_UNSIGNED_INT, 0);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glPopMatrix();
        glUseProgram(0);
    }
}
