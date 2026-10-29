package uk.co.yottr.builder;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.util.Base64;

/**
 * Pictures to upload in the tests, made as they're needed.
 */
public final class TestPictures {

    // a 1 by 1 lossless WebP, as browsers' feature tests use; ImageIO can't write WebP
    public static final byte[] WEBP = Base64.getDecoder().decode("UklGRhoAAABXRUJQVlA4TA0AAAAvAAAAEAcQERGIiP4HAA==");

    private TestPictures() {
    }

    /**
     * A JPEG, red on the left half and blue on the right, so a test can tell which way up it is.
     */
    public static byte[] jpeg(int width, int height) {
        return write(halves(width, height, BufferedImage.TYPE_INT_RGB), "jpeg");
    }

    /**
     * A PNG with every pixel fully transparent.
     */
    public static byte[] transparentPng(int width, int height) {
        return write(new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB), "png");
    }

    /**
     * A black and white PNG, which stays small however many pixels it has.
     */
    public static byte[] blankPng(int width, int height) {
        return write(new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY), "png");
    }

    public static byte[] gif(int width, int height) {
        return write(halves(width, height, BufferedImage.TYPE_INT_RGB), "gif");
    }

    /**
     * A JPEG as a phone saves one taken on its side: the pixels as the sensor saw them, and an Exif orientation
     * saying how to turn them. Orientation 6 means turn it a quarter clockwise to show it.
     */
    public static byte[] jpegWithOrientation(int width, int height, int orientation) {
        final byte[] jpeg = jpeg(width, height);
        final ByteBuffer app1 = ByteBuffer.allocate(36);
        app1.putShort((short) 0xFFE1).putShort((short) 34);
        app1.put("Exif\0\0".getBytes());
        // TIFF header, big-endian, with the first directory straight after it
        app1.put("MM".getBytes()).putShort((short) 0x002A).putInt(8);
        // one entry: Orientation (0x0112), a SHORT, one of them
        app1.putShort((short) 1).putShort((short) 0x0112).putShort((short) 3).putInt(1).putShort((short) orientation).putShort((short) 0);
        app1.putInt(0);

        // the Exif segment goes straight after the start of image marker
        final byte[] withExif = new byte[jpeg.length + app1.capacity()];
        System.arraycopy(jpeg, 0, withExif, 0, 2);
        System.arraycopy(app1.array(), 0, withExif, 2, app1.capacity());
        System.arraycopy(jpeg, 2, withExif, 2 + app1.capacity(), jpeg.length - 2);
        return withExif;
    }

    private static BufferedImage halves(int width, int height, int type) {
        final BufferedImage image = new BufferedImage(width, height, type);
        final Graphics2D graphics = image.createGraphics();
        graphics.setColor(Color.RED);
        graphics.fillRect(0, 0, width / 2, height);
        graphics.setColor(Color.BLUE);
        graphics.fillRect(width / 2, 0, width - width / 2, height);
        graphics.dispose();
        return image;
    }

    private static byte[] write(BufferedImage image, String format) {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            if (!ImageIO.write(image, format, out)) {
                throw new IllegalStateException("No ImageIO writer for " + format);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return out.toByteArray();
    }
}
