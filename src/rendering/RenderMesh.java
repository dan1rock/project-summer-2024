package src.rendering;

import org.lwjgl.opengl.GL11;
import src.utils.Color;
import src.utils.Vector3f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

public class RenderMesh extends RenderObject{
    private final Mesh mesh;
    private int texture;
    private float[] baseColor = new float[]{1f, 1f, 1f};
    private final int shaderProgramID;
    private boolean isTextured = false;
    private float ambient = 0.4f;
    private float shininess = 32f;
    private float specularStrength = 0.5f;

    int modelLoc;
    int viewPosLoc;
    int lightColorLoc;
    int objectColorLoc;
    int ambientStrengthLoc;
    int shininessLoc;
    int specularStrengthLoc;
    int isTexturedLoc;
    int fogColorLoc;
    int fogStartLoc;
    int fogEndLoc;
    int clipPlaneLoc;
    int useClipPlaneLoc;

    public RenderMesh(Mesh mesh, int shaderProgramID) {
        this.mesh = mesh;
        this.shaderProgramID = shaderProgramID;
        this.position = new Vector3f(0f, 0f, 0f);
        this.rotation = new Vector3f(0f, 0f, 0f);
        this.scale = new Vector3f(1f, 1f, 1f);

        getShaderLocations();
    }

    public RenderMesh(Mesh mesh, int shaderProgramID, Vector3f position, Vector3f rotation, Vector3f scale) {
        this.mesh = mesh;
        this.shaderProgramID = shaderProgramID;
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;

        getShaderLocations();
    }

    private void getShaderLocations() {
        modelLoc = glGetUniformLocation(shaderProgramID, "model");
        viewPosLoc = glGetUniformLocation(shaderProgramID, "viewPos");
        lightColorLoc = glGetUniformLocation(shaderProgramID, "lightColor");
        objectColorLoc = glGetUniformLocation(shaderProgramID, "objectColor");
        ambientStrengthLoc = glGetUniformLocation(shaderProgramID, "ambientStrength");
        shininessLoc = glGetUniformLocation(shaderProgramID, "shininess");
        specularStrengthLoc = glGetUniformLocation(shaderProgramID, "specularStrength");
        isTexturedLoc = glGetUniformLocation(shaderProgramID, "isTextured");
        fogColorLoc = glGetUniformLocation(shaderProgramID, "fogColor");
        fogStartLoc = glGetUniformLocation(shaderProgramID, "fogStart");
        fogEndLoc = glGetUniformLocation(shaderProgramID, "fogEnd");
        clipPlaneLoc = glGetUniformLocation(shaderProgramID, "clipPlane");
        useClipPlaneLoc = glGetUniformLocation(shaderProgramID, "useClipPlane");
    }

    public void setTexture(int textureID) {
        texture = textureID;
        isTextured = true;
    }

    public void setAmbient(float ambient) {
        this.ambient = ambient;
    }

    public void setShininess(float shininess) {
        this.shininess = shininess;
    }

    public void setSpecularStrength(float specularStrength) {
        this.specularStrength = specularStrength;
    }

    public void setBaseColor(float[] color) {
        baseColor[0] = color[0];
        baseColor[1] = color[1];
        baseColor[2] = color[2];
    }

    public void setObjectColor(Vector3f color) {
        baseColor[0] = color.x;
        baseColor[1] = color.y;
        baseColor[2] = color.z;
    }

    @Override
    public void Update(float deltaTime) {

    }

    @Override
    public void Render(boolean clipPlane) {
        glEnable(GL_LIGHTING);

        if (isTextured) {
            glEnable(GL_TEXTURE_2D);
            glBindTexture(GL_TEXTURE_2D, texture);
            glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_DECAL);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
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

        glUniform3f(viewPosLoc, renderer.viewPos.x, renderer.viewPos.y, renderer.viewPos.z);
        glUniform3fv(lightColorLoc, renderer.lightColor);
        glUniform3fv(objectColorLoc, baseColor);
        glUniform1f(ambientStrengthLoc, ambient);
        glUniform1f(shininessLoc, shininess);
        glUniform1f(specularStrengthLoc, specularStrength);
        glUniform1i(isTexturedLoc, isTextured ? 1 : 0);
        glUniform3fv(fogColorLoc, renderer.fogColor);
        glUniform1f(fogStartLoc, renderer.fogStart);
        glUniform1f(fogEndLoc, renderer.fogEnd);
        glUniform4fv(clipPlaneLoc, renderer.clipPlane);
        glUniform1i(useClipPlaneLoc, clipPlane ? 1 : 0);

        float[] modelMatrix = new float[16];

        glGetFloatv(GL_MODELVIEW_MATRIX, modelMatrix);

        glUniformMatrix4fv(modelLoc, false, modelMatrix);

        glBindBuffer(GL_ARRAY_BUFFER, mesh.vertexVboId);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, mesh.textureVboId);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, mesh.normalVboId);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

        GL11.glColor3fv(Color.Magenta);

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
