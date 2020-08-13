package pl.fhframework.image.util;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import pl.fhframework.core.logging.FhLogger;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.Base64;

@Service
public class ImageUtils {

    public static final String DATA = "data:";
    public static final String BASE64 = ";base64,";

    public static final String TRANSPARENT_1X1 = "data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7";

    public String convertByteToBase64(byte[] bytes) {
        if (bytes != null) {
            try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes); InputStream is = new BufferedInputStream(byteArrayInputStream)) {
                String mimeType = URLConnection.guessContentTypeFromStream(is);
                return DATA + mimeType + BASE64 + new String(Base64.getEncoder().encode(bytes));
            } catch (IOException exception) {
                FhLogger.error(exception);
            }
        }
        return "";
    }

    public String convertByteToBase64(InputStream inputStream) {
        final byte[] bytes;
        try {
            bytes = IOUtils.toByteArray(inputStream);
            if (bytes == null) {
                return "";
            }
            return convertByteToBase64(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
