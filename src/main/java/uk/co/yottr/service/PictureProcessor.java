package uk.co.yottr.service;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;
import uk.co.yottr.model.ListingPicture.Jpeg;
import uk.co.yottr.model.ListingPicture.Picture;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

/**
 * Turns an uploaded picture into the JPEGs a listing keeps: one scaled down to fit the listing's page, and a
 * thumbnail. Every picture is drawn afresh, so nothing of the upload is kept but the picture: no location or other
 * metadata, and nothing that isn't an image. A phone photo is turned the right way up, as its metadata says.
 */
@Component
public class PictureProcessor {

    public static final int MAX_BYTES = 10 * 1024 * 1024;
    // a picture is decoded whole, so this bounds the memory it takes: about 200 MB at 4 bytes a pixel
    static final long MAX_PIXELS = 50_000_000L;
    static final int IMAGE_SIZE = 1600;
    static final int THUMBNAIL_SIZE = 480;
    private static final float JPEG_QUALITY = 0.85f;
    private static final Set<String> FORMATS = Set.of("jpeg", "png", "webp");

    /**
     * @throws UnusablePictureException if it's too big, or isn't a JPEG, PNG or WebP picture that can be read
     */
    public Picture process(byte[] upload) {
        if (upload.length > MAX_BYTES) {
            throw new UnusablePictureException("is larger than 10 MB");
        }
        final int[] size = checkFormatAndSize(upload);

        final BufferedImage image;
        final BufferedImage thumbnail;
        try {
            image = fitWithin(Thumbnails.of(new ByteArrayInputStream(upload)), IMAGE_SIZE, size[0], size[1]);
            thumbnail = fitWithin(Thumbnails.of(image), THUMBNAIL_SIZE, image.getWidth(), image.getHeight());
        } catch (IOException | RuntimeException e) {
            throw new UnusablePictureException("couldn't be read", e);
        }
        return new Picture(jpeg(image), jpeg(thumbnail));
    }

    /**
     * Reads only as far as the picture's format and size, so a picture too big to decode is refused before it is.
     *
     * @return the width and height
     */
    private static int[] checkFormatAndSize(byte[] upload) {
        try (ImageInputStream in = ImageIO.createImageInputStream(new ByteArrayInputStream(upload))) {
            final Iterator<ImageReader> readers = ImageIO.getImageReaders(in);
            if (!readers.hasNext()) {
                throw new UnusablePictureException("isn't a JPEG, PNG or WebP picture");
            }
            final ImageReader reader = readers.next();
            try {
                if (!FORMATS.contains(reader.getFormatName().toLowerCase(Locale.ROOT))) {
                    throw new UnusablePictureException("isn't a JPEG, PNG or WebP picture");
                }
                reader.setInput(in);
                final int width = reader.getWidth(0);
                final int height = reader.getHeight(0);
                if ((long) width * height > MAX_PIXELS) {
                    throw new UnusablePictureException("has too many pixels: up to 50 megapixels will do");
                }
                return new int[] {width, height};
            } finally {
                reader.dispose();
            }
        } catch (IOException | RuntimeException e) {
            if (e instanceof UnusablePictureException unusable) {
                throw unusable;
            }
            throw new UnusablePictureException("couldn't be read", e);
        }
    }

    /**
     * Scales the picture down to fit a square of the given side, if it doesn't already: Thumbnailator would scale a
     * smaller one up.
     */
    private static BufferedImage fitWithin(Thumbnails.Builder<?> builder, int side, int width, int height) throws IOException {
        return (Math.max(width, height) > side ? builder.size(side, side) : builder.scale(1.0)).asBufferedImage();
    }

    /**
     * JPEG has no transparency, so a transparent picture goes on white.
     */
    private static Jpeg jpeg(BufferedImage picture) {
        final BufferedImage rgb = new BufferedImage(picture.getWidth(), picture.getHeight(), BufferedImage.TYPE_INT_RGB);
        final Graphics2D graphics = rgb.createGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, rgb.getWidth(), rgb.getHeight());
        graphics.drawImage(picture, 0, 0, null);
        graphics.dispose();

        final ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (ImageOutputStream imageOut = ImageIO.createImageOutputStream(out)) {
            writer.setOutput(imageOut);
            final ImageWriteParam param = writer.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(JPEG_QUALITY);
            writer.write(null, new IIOImage(rgb, null, null), param);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } finally {
            writer.dispose();
        }
        return new Jpeg(out.toByteArray(), rgb.getWidth(), rgb.getHeight());
    }

    /**
     * Why an upload can't be used, to finish a sentence that starts with its file name.
     */
    public static class UnusablePictureException extends RuntimeException {
        public UnusablePictureException(String reason) {
            super(reason);
        }

        public UnusablePictureException(String reason, Throwable cause) {
            super(reason, cause);
        }
    }
}
