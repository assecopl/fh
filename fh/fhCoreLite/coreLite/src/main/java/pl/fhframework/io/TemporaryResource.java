package pl.fhframework.io;

import lombok.Setter;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.*;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import lombok.Getter;

@Getter
public class TemporaryResource extends FileSystemResource implements IResourced {
    public final static String TEMP_FILE_PREFIX = "__fh__";

    @Setter
    private String contentType;

    public TemporaryResource(File file) {
        super(file);
    }

    @Override
    public Resource getResource() throws FileNotFoundException {
        return this;
    }

    @Override
    public List<Resource> getResources() throws FileNotFoundException {
        return Collections.singletonList(this);
    }

    @Override
    public String getFilename() {
        String fileName = super.getFilename();
        return fileName.substring(0, fileName.indexOf(TEMP_FILE_PREFIX));
    }

    public String toBase64() {
        final byte[] bytes;
        try {
            bytes = IOUtils.toByteArray(getInputStream());
            if (bytes == null) {
                return "";
            }
            return new String(Base64.getEncoder().encode(bytes));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
