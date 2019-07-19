package pl.fhframework.core.io;

import org.springframework.core.io.ByteArrayResource;

/**
 * Resource that is based on a byte array and has assigned filename.
 */
public class ByteArrayFileResource extends ByteArrayResource {

    private String filename;

    public ByteArrayFileResource(byte[] byteArray, String filename) {
        super(byteArray);
        this.filename = filename;
    }

    @Override
    public String getFilename() {
        return filename;
    }
}
