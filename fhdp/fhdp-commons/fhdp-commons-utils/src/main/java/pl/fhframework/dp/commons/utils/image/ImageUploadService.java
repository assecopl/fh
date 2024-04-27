package pl.fhframework.dp.commons.utils.image;


import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.core.io.Resource;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

@Service
@Slf4j
public class ImageUploadService {

    @Value("${fhdp.imageUpload.maxFileSize:4194304}")
    private long maxFileSize;

    /**
     * Method returns content from file. If file is image, it is compacted to minimal readable size.
     *
     * @param file
     * @param contentType
     * @param imgTargetWidthPx
     * @return
     * @throws IOException
     */
    public byte[] compactIfPossible(Resource file, String contentType, int imgTargetWidthPx) throws IOException {
        if (isImage(contentType)) {
            BufferedImage originalImage = ImageIO.read(file.getFile());
            BufferedImage finalImage;
            if (Math.max(originalImage.getWidth(), originalImage.getHeight()) > imgTargetWidthPx) {
                finalImage = Scalr.resize(originalImage, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.AUTOMATIC,
                        imgTargetWidthPx, imgTargetWidthPx, Scalr.OP_ANTIALIAS);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(finalImage, getFormatName(contentType), baos);
                return baos.toByteArray();
            } else {
                return Files.readAllBytes(Paths.get(file.getURI()));
            }
        } else {
            return Files.readAllBytes(Paths.get(file.getURI()));
        }
    }

    private String getFormatName(String contentType) {
        return contentType.substring(contentType.indexOf("/") + 1);
    }

    private boolean isImage(String contentType) {
        return contentType.startsWith("image/");
    }

    public long getMaxUploadFileSize() {
        return maxFileSize;
    }


}
