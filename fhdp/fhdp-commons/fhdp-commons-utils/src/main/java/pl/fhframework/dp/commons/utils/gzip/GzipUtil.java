package pl.fhframework.dp.commons.utils.gzip;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 23/11/2021
 */
public class GzipUtil {

    public static byte[] compress(byte[] content) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final GZIPOutputStream compressor = new GZIPOutputStream(baos);

        compressor.write(content);
        compressor.finish();
        compressor.close();
        return baos.toByteArray();
    }

    public static byte[] decompress(byte[] content) throws IOException {
        ByteArrayInputStream bin = new ByteArrayInputStream(content);
        GZIPInputStream gzipper = new GZIPInputStream(bin);
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        int len;
        while ((len = gzipper.read(buffer)) > 0) {
            out.write(buffer, 0, len);
        }

        gzipper.close();
        out.close();
        return out.toByteArray();
    }
}
