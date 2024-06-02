package src.rendering;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import src.objects.Camera;
import src.utils.Color;
import src.utils.Matrix4f;
import src.utils.Projection;
import src.utils.Vector3f;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL30.*;

public class RenderEngine {
    private static long window;
    protected int width;
    protected int height;
    public Camera camera;
    public float deltaTime;
    public int fps;

    private final boolean[] keys = new boolean[1024];
    private float lastX = 400, lastY = 300;
    private boolean firstMouse = true;

    public float[] viewMatrix = new float[16];
    public float[] overlayMatrix = new float[16];
    public List<Renderer> renderers = new ArrayList<>();
    public List<Integer> shaders = new ArrayList<>();

    public Vector3f lightPos = new Vector3f(0f, 100f, 50f);
    public float[] clipPlane = new float[]{0f, 1f, 0f, 0f};
    public Vector3f viewPos;
    public float[] lightColor = new float[]{1.0f, 1.0f, 1.0f};
    public float[] fogColor = new float[]{0.5f, 0.5f, 0.5f};
    public float fogStart = 100f;
    public float fogEnd = 200f;

    public int reflectionTextureID;
    public int reflectionDepthBufferID;
    public int reflectionFrameBuffer;

    public int refractionTextureID;
    public int refractionDepthBufferID;
    public int refractionFrameBuffer;

    private TextRenderer textRenderer;
    private static RenderEngine instance;

    public static RenderEngine getInstance() {
        if (instance != null) {
            return instance;
        }
        synchronized (RenderEngine.class) {
            if (instance == null) {
                instance = new RenderEngine();
            }
        }
        return instance;
    }

    private void setViewport(int width, int height, float fov, float near, float far, Projection projection) {
        this.width = width;
        this.height = height;

        glViewport(0, 0, width, height);
        setProjection(fov, near, far, projection);

        float aspectRatio = (float) width / height;
        float w = 5f;
        float top = w;
        float bottom = -top;
        float right = w * aspectRatio;
        float left = -right;

        overlayMatrix = Matrix4f.createOrthoMatrix(left, right, bottom, top, near, far);
    }

    public void setVerticalSync(boolean enabled) {
        GLFW.glfwSwapInterval(enabled ? 1 : 0);
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
        GLFW.glfwSwapInterval(1);
        GL.createCapabilities();

        float[] diffuse = { 0.5f, 0.5f, 0.5f, 1.0f };
        glLightfv(GL_LIGHT0, GL_DIFFUSE, diffuse);
        glEnable(GL_LIGHTING);
        glEnable(GL_LIGHT0);
        glEnable(GL_COLOR_MATERIAL);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_FRONT);
        glClearColor(135f / 255f, 206f / 255f, 235f / 255f, 0.0f);
        setViewport(1920, 1080, 45.0f, 0.1f, 500.0f, Projection.Perspective);
        initReflectionRefraction();

        GLFW.glfwSetFramebufferSizeCallback(window, (win, width, height) -> {
            setViewport(width, height, 45.0f, 0.1f, 500.0f, Projection.Perspective);
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

        textRenderer = new TextRenderer("Fonts/Oswald-Bold.ttf");
    }

    private void initReflectionRefraction() {
        reflectionTextureID = createFramebufferTexture(width, height);
        reflectionDepthBufferID = createFramebufferDepthBuffer(width, height);
        reflectionFrameBuffer = createFramebuffer(reflectionTextureID, reflectionDepthBufferID);

        refractionTextureID = createFramebufferTexture(width, height);
        refractionDepthBufferID = createFramebufferDepthBuffer(width, height);
        refractionFrameBuffer = createFramebuffer(refractionTextureID, refractionDepthBufferID);
    }

    public void runMainLoop() {
        mainLoop(0);

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

        System.out.println("Setting refresh rate to " + refreshRate + " FPS");

        return  1000 / refreshRate;
    }

    private void mainLoop(long targetFrameTime) {
        long lastRenderTime = System.currentTimeMillis();

        while (!GLFW.glfwWindowShouldClose(window)) {
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - lastRenderTime;

            if (elapsedTime >= targetFrameTime) {
                deltaTime = (currentTime - lastRenderTime) / 1000f;
                Update();
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

    private float angle;
    private void Update() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        processInput();
        for (Renderer renderer : renderers) {
            renderer.Update(deltaTime);
        }

        glEnable(GL_CLIP_DISTANCE0);
        doReflectionPass();
        doRefractionPass();
        glDisable(GL_CLIP_DISTANCE0);
        doMainRenderPass();
        GLFW.glfwSwapBuffers(window);
        GLFW.glfwPollEvents();

        angle += deltaTime;
        if (angle > 6.29f) angle = 0f;
        lightPos.x = (float) Math.sin(angle) * 1000f;
        lightPos.z = (float) Math.cos(angle) * 1000f;

        calculateFPS();
    }

    private long lastRecordedTime;
    private int currentFPS;
    private void calculateFPS() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastRecordedTime > 1000) {
            fps = currentFPS;
            currentFPS = 0;
            lastRecordedTime = currentTime;
            return;
        }

        currentFPS += 1;
    }

    private void doMainRenderPass() {
        viewPos = camera.position;

        if (keys[GLFW_KEY_R]) {
            viewMatrix = camera.getReflectionMatrix();
        } else {
            viewMatrix = camera.getViewMatrix();
        }

        updateShaders();

        for (Renderer renderer : renderers) {
            renderer.Render(false);
        }

        textRenderer.renderText(String.valueOf(fps), Color.White, -8.5f, -4.5f, 0.5f);
    }

    private void doReflectionPass() {
        glBindFramebuffer(GL_FRAMEBUFFER, reflectionFrameBuffer);

        glViewport(0, 0, width, height);

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        viewPos = camera.position;

        viewMatrix = camera.getReflectionMatrix();

        updateShaders();

        clipPlane[0] = 0f;
        clipPlane[1] = 1f;
        clipPlane[2] = 0f;
        clipPlane[3] = 0f;
        for (Renderer renderer : renderers) {
            renderer.Render(true);
        }

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    private void doRefractionPass() {
        glBindFramebuffer(GL_FRAMEBUFFER, refractionFrameBuffer);
        glViewport(0, 0, width, height);

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        viewPos = camera.position;

        viewMatrix = camera.getViewMatrix();

        updateShaders();

        clipPlane[0] = 0f;
        clipPlane[1] = -1f;
        clipPlane[2] = 0f;
        clipPlane[3] = 0f;
        for (Renderer renderer : renderers) {
            renderer.Render(true);
        }

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    private void processInput() {
        camera.processKeyboard(keys, deltaTime);
    }

    private void updateShaders() {
        float[] projectionMatrix = new float[16];
        glGetFloatv(GL_PROJECTION_MATRIX, projectionMatrix);
        for (Integer shader : shaders) {
            glUseProgram(shader);
            int viewLoc = glGetUniformLocation(shader, "view");
            int projectionLoc = glGetUniformLocation(shader, "projection");
            int lightPosLoc = glGetUniformLocation(shader, "lightPos");
            int lightColorLoc = glGetUniformLocation(shader, "lightColor");

            glUniformMatrix4fv(viewLoc, false, viewMatrix);
            glUniformMatrix4fv(projectionLoc, false, projectionMatrix);
            glUniform3f(lightPosLoc, lightPos.x, lightPos.y, lightPos.z);
            glUniform3fv(lightColorLoc, lightColor);
        }
    }

    public int createFramebufferTexture(int width, int height) {
        int textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureID);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, (ByteBuffer) null);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        return textureID;
    }

    public int createFramebufferDepthBuffer(int width, int height) {
        int depthBuffer = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, depthBuffer);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, width, height);
        glBindRenderbuffer(GL_RENDERBUFFER, 0);
        return depthBuffer;
    }

    public int createFramebuffer(int textureID, int depthBufferID) {
        int framebuffer = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, framebuffer);

        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, textureID, 0);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthBufferID);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            System.out.println("Framebuffer is not complete!");
        }

        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        return framebuffer;
    }
}
