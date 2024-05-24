package src;

import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class Texture {
    public static int loadRGBTexture(String path) {
        ByteBuffer buffer = null;
        int tw = 0, th = 0;

        try {
            BufferedImage img = ImageIO.read(new File(path));
            th = img.getHeight();
            tw = img.getWidth();

            buffer = BufferUtils.createByteBuffer(tw * th * 3);

            for (int i = 0; i < tw; i++) {
                for (int j = 0; j < th; j++) {
                    int pixel = img.getRGB(i, th - 1 - j);
                    buffer.put((byte) ((pixel >> 16) & 0xFF)); // Red component
                    buffer.put((byte) ((pixel >> 8) & 0xFF));  // Green component
                    buffer.put((byte) (pixel & 0xFF));         // Blue component
                }
            }
            buffer.flip();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int textureID = setTexture(buffer, tw, th);
        return textureID;
    }

    private static int setTexture(ByteBuffer buffer, int tw, int th) {
        int textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureID);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, tw, th, 0, GL_RGB, GL_UNSIGNED_BYTE, buffer);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glGenerateMipmap(GL_TEXTURE_2D);
        glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);

        return textureID;
    }
}
