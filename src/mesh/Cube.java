package src.mesh;

import src.rendering.Renderer;
import src.utils.Vector3f;

import static org.lwjgl.opengl.GL11.*;

public class Cube extends Renderer {
    public Cube() {
        this.position = new Vector3f(0f, 0f, 0f);
        this.rotation = new Vector3f(0f, 0f, 0f);
        this.scale = new Vector3f(1f, 1f, 1f);
    }

    public Cube(Vector3f position, Vector3f rotation, Vector3f scale) {
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }

    @Override
    public void Update(float deltaTime) {

    }

    @Override
    public void Render(boolean clipPlane) {
        float h = 1f;

        glPushMatrix();

        glTranslatef(position.x, position.y, position.z);
        glRotatef(rotation.x, 1, 0, 0);
        glRotatef(rotation.y, 0, 1, 0);
        glRotatef(rotation.z, 0, 0, 1);
        glScalef(scale.x, scale.y, scale.z);

        glEnable(GL_LIGHTING);
        glBegin(GL_QUADS);

        glColor3f(1.0f, 1.0f, 0.0f);

        // Front face
        glNormal3f(0.0f, 0.0f, 1.0f);
        glVertex3f(-h, -h, h);
        glVertex3f(h, -h, h);
        glVertex3f(h, h, h);
        glVertex3f(-h, h, h);

        // Right face
        glNormal3f(1.0f, 0.0f, 0.0f);
        glVertex3f(h, -h, -h);
        glVertex3f(h, h, -h);
        glVertex3f(h, h, h);
        glVertex3f(h, -h, h);

        // Back face
        glNormal3f(0.0f, 0.0f, -1.0f);
        glVertex3f(-h, -h, -h);
        glVertex3f(-h, h, -h);
        glVertex3f(h, h, -h);
        glVertex3f(h, -h, -h);

        // Left face
        glNormal3f(-1.0f, 0.0f, 0.0f);
        glVertex3f(-h, -h, -h);
        glVertex3f(-h, -h, h);
        glVertex3f(-h, h, h);
        glVertex3f(-h, h, -h);

        // Top face
        glNormal3f(0.0f, 1.0f, 0.0f);
        glVertex3f(-h, h, -h);
        glVertex3f(-h, h, h);
        glVertex3f(h, h, h);
        glVertex3f(h, h, -h);

        // Bottom face
        glNormal3f(0.0f, -1.0f, 0.0f);
        glVertex3f(-h, -h, -h);
        glVertex3f(h, -h, -h);
        glVertex3f(h, -h, h);
        glVertex3f(-h, -h, h);

        glEnd();

        glPopMatrix();
    }
}
