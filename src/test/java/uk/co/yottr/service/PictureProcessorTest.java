package uk.co.yottr.service;

import org.junit.jupiter.api.Test;
import uk.co.yottr.model.ListingPicture.Jpeg;
import uk.co.yottr.model.ListingPicture.Picture;
import uk.co.yottr.service.PictureProcessor.UnusablePictureException;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.co.yottr.builder.TestPictures.*;

public class PictureProcessorTest {

    private final PictureProcessor processor = new PictureProcessor();

    @Test
    public void aLargePictureIsScaledDownToFitThePageAndTheThumbnailSmallerStill() throws IOException {
        final Picture picture = processor.process(jpeg(4000, 3000));

        assertSize(1600, 1200, picture.image());
        assertSize(480, 360, picture.thumbnail());
        assertEquals("JPEG", formatOf(picture.image()));
    }

    @Test
    public void aSmallPictureIsntScaledUp() {
        final Picture picture = processor.process(jpeg(300, 200));

        assertSize(300, 200, picture.image());
        assertSize(300, 200, picture.thumbnail());
    }

    @Test
    public void aPhoneOnItsSideIsTurnedTheRightWayUp() throws IOException {
        // the left half is red: turned a quarter clockwise, it's the top half
        final Picture picture = processor.process(jpegWithOrientation(200, 100, 6));

        assertSize(100, 200, picture.image());
        final BufferedImage image = read(picture.image());
        assertTrue(isMostly(Color.RED, image.getRGB(50, 20)), "red at the top");
        assertTrue(isMostly(Color.BLUE, image.getRGB(50, 180)), "blue at the bottom");
    }

    @Test
    public void nothingButThePictureIsKept() {
        final Picture picture = processor.process(jpegWithOrientation(200, 100, 6));

        assertFalse(new String(picture.image().jpeg(), StandardCharsets.ISO_8859_1).contains("Exif"));
    }

    @Test
    public void aTransparentPngGoesOnWhite() throws IOException {
        final Picture picture = processor.process(transparentPng(40, 30));

        assertTrue(isMostly(Color.WHITE, read(picture.image()).getRGB(20, 15)));
    }

    @Test
    public void aWebPIsRead() {
        assertSize(1, 1, processor.process(WEBP).image());
    }

    @Test
    public void onlyJpegPngAndWebPAreTaken() {
        assertEquals("isn't a JPEG, PNG or WebP picture",
                assertThrows(UnusablePictureException.class, () -> processor.process(gif(40, 30))).getMessage());
        assertEquals("isn't a JPEG, PNG or WebP picture",
                assertThrows(UnusablePictureException.class, () -> processor.process("Not a picture".getBytes())).getMessage());
    }

    @Test
    public void aDamagedPictureIsRefused() {
        // a PNG's signature, then nonsense
        final byte[] damaged = "\u0089PNG\r\n\u001a\nnot the rest of a picture".getBytes(StandardCharsets.ISO_8859_1);

        final UnusablePictureException e = assertThrows(UnusablePictureException.class, () -> processor.process(damaged));

        assertEquals("couldn't be read", e.getMessage());
    }

    @Test
    public void aPictureWithTooManyPixelsIsRefusedBeforeItsDecoded() {
        // 8000 by 8000 is 64 megapixels, 256 MB decoded, but a small file
        final UnusablePictureException e = assertThrows(UnusablePictureException.class, () -> processor.process(blankPng(8000, 8000)));

        assertEquals("has too many pixels: up to 50 megapixels will do", e.getMessage());
    }

    @Test
    public void aFileOver10MbIsRefused() {
        final UnusablePictureException e = assertThrows(UnusablePictureException.class,
                () -> processor.process(new byte[PictureProcessor.MAX_BYTES + 1]));

        assertEquals("is larger than 10 MB", e.getMessage());
    }

    private static void assertSize(int width, int height, Jpeg jpeg) {
        assertEquals(width, jpeg.width(), "width");
        assertEquals(height, jpeg.height(), "height");
        try {
            final BufferedImage image = read(jpeg);
            assertEquals(width, image.getWidth(), "the JPEG's width");
            assertEquals(height, image.getHeight(), "the JPEG's height");
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    private static BufferedImage read(Jpeg jpeg) throws IOException {
        return ImageIO.read(new ByteArrayInputStream(jpeg.jpeg()));
    }

    private static String formatOf(Jpeg jpeg) throws IOException {
        try (var in = ImageIO.createImageInputStream(new ByteArrayInputStream(jpeg.jpeg()))) {
            return ImageIO.getImageReaders(in).next().getFormatName().toUpperCase();
        }
    }

    /**
     * Whether the pixel is near the colour, as JPEG doesn't keep colours exactly.
     */
    private static boolean isMostly(Color expected, int rgb) {
        final Color actual = new Color(rgb);
        return Math.abs(actual.getRed() - expected.getRed()) < 40
                && Math.abs(actual.getGreen() - expected.getGreen()) < 40
                && Math.abs(actual.getBlue() - expected.getBlue()) < 40;
    }
}
