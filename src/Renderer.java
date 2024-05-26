package src;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Renderer {
    private static long window;
    protected int width;
    protected int height;
    public Camera camera;
    public float deltaTime;

    private final boolean[] keys = new boolean[1024];
    private float lastX = 400, lastY = 300;
    private boolean firstMouse = true;

    public List<RenderObject> renderObjects = new ArrayList<>();

    public Vector3f lightPos = new Vector3f(0f, 0f, 5f);
    public Vector3f viewPos;
    public float[] lightColor = new float[]{1.0f, 1.0f, 1.0f};

    private static Renderer instance;

    private Renderer() {
        OnCreate();
    }

    public static Renderer getInstance() {
        if (instance == null) {
            synchronized (Renderer.class) {
                if (instance == null) {
                    instance = new Renderer();
                }
            }
        }
        return instance;
    }

    private void setViewport(int width, int height, float fov, float near, float far, Projection projection) {
        this.width = width;
        this.height = height;

        glViewport(0, 0, width, height);
        setProjection(fov, near, far, projection);
    }

    protected void setProjection(float fov, float near, float far, Projection projection) {
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();

        float aspectRatio = (float) width / height;
        if (projection == Projection.Perspective) {
            float top = near * (float) Math.tan(Math.toRadians(fov / 2));
            float bottom = -top;
            float right = top * aspectRatio;
            float left = -right;

            glFrustum(left, right, bottom, top, near, far);
        } else if (projection == Projection.Orthogonal) {
            float w = 5f;
            float top = w;
            float bottom = -top;
            float right = w * aspectRatio;
            float left = -right;
            glOrtho(left, right, bottom, top, near, far);
        }

        glMatrixMode(GL_MODELVIEW);
    }

    public void OnCreate() {
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        GLFW.glfwDefaultWindowHints();
        window = GLFW.glfwCreateWindow(1920, 1080, "Renderer", 0, 0);
        if (window == 0) {
            GLFW.glfwTerminate();
            throw new RuntimeException("Failed to create the GLFW window");
        }

        GLFW.glfwMakeContextCurrent(window);
        GL.createCapabilities();

        float[] diffuse = { 0.5f, 0.5f, 0.5f, 1.0f };
        glLightfv(GL_LIGHT0, GL_DIFFUSE, diffuse);
        glEnable(GL_LIGHTING);
        glEnable(GL_LIGHT0);
        glEnable(GL_COLOR_MATERIAL);
        glEnable(GL_DEPTH_TEST);
        glClearColor(0.1f, 0.0f, 0.2f, 0.0f);
        setViewport(1920, 1080, 45.0f, 0.1f, 100.0f, Projection.Perspective);

        GLFW.glfwSetFramebufferSizeCallback(window, (win, width, height) -> {
            setViewport(width, height, 45.0f, 0.1f, 100.0f, Projection.Perspective);
        });

        glfwSetKeyCallback(window, new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS)
                    glfwSetWindowShouldClose(window, true);
                if (action == GLFW_PRESS)
                    keys[key] = true;
                else if (action == GLFW_RELEASE)
                    keys[key] = false;
            }
        });

        glfwSetCursorPosCallback(window, new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double posX, double posY) {
                if (firstMouse) {
                    lastX = (float) posX;
                    lastY = (float) posY;
                    firstMouse = false;
                }

                float xOffset = (float) posX - lastX;
                float yOffset = lastY - (float) posY;
                lastX = (float) posX;
                lastY = (float) posY;

                camera.processMouseMovement(xOffset, yOffset);
            }
        });

        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
    }

    public void runMainLoop() {
        mainLoop(getMonitorFrameTime());

        GLFW.glfwDestroyWindow(window);
        GLFW.glfwTerminate();
    }

    private long getMonitorFrameTime() {
        GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        if (vidMode == null) {
            System.out.println("Failed to retrieve primary monitor's video mode, setting to 60 FPS");
            return 1000 / 60;
        }

        int refreshRate = vidMode.refreshRate();
        if (refreshRate <= 0) {
            System.out.println("Failed to retrieve primary monitor's video mode, setting to 60 FPS");
            return 1000 / 60;
        }

        return  1000 / refreshRate;
    }

    private void mainLoop(long targetFrameTime) {
        long lastRenderTime = System.currentTimeMillis();

        while (!GLFW.glfwWindowShouldClose(window)) {
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - lastRenderTime;

            if (elapsedTime >= targetFrameTime) {
                deltaTime = (currentTime - lastRenderTime) / 1000f;
                Update(currentTime - lastRenderTime);
                lastRenderTime = currentTime;
            } else {
                try {
                    Thread.sleep(targetFrameTime - elapsedTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void Update(long frameTime) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        drawScene(frameTime);
        GLFW.glfwSwapBuffers(window);
        GLFW.glfwPollEvents();
    }

    private float angle;
    private void drawScene(long frameTime) {
        glPushMatrix();
        glLoadIdentity();
        processInput();
        viewPos = camera.position;
        camera.gluLookAt();
        //glLoadMatrixf(camera.getViewMatrix());

        for (RenderObject renderObject : renderObjects) {
            renderObject.Update(deltaTime);
        }

        glPopMatrix();

        angle += 30f * deltaTime;
        if (angle > 360f) angle = 0f;
    }

    private void processInput() {
        camera.processKeyboard(keys, deltaTime);
    }
}
