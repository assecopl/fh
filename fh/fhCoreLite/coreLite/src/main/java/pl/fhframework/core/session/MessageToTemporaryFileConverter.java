package pl.fhframework.core.session;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import pl.fhframework.SessionManager;
import pl.fhframework.io.FileService;
import pl.fhframework.io.TemporaryResource;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

/**
 * @author Tomasz Kozlowski (created on 08.01.2020)
 */
@Component
@RequiredArgsConstructor
public class MessageToTemporaryFileConverter implements HttpMessageConverter<TemporaryResource> {

    private static final List<MediaType> SUPPORTED_MEDIA_TYPES = Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM);

    private final FileService fileService;

    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        return TemporaryResource.class.isAssignableFrom(clazz);
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return false;
    }

    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return SUPPORTED_MEDIA_TYPES;
    }

    @Override
    public TemporaryResource read(Class<? extends TemporaryResource> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        String filename = inputMessage.getHeaders().getFirst("X-Log-Filename");
        TemporaryResource temp = fileService.createNewTemporaryResource(filename, SessionManager.getUserSession()).getSecond();
        try (OutputStream tempOutput = temp.getOutputStream()) {
            StreamUtils.copy(inputMessage.getBody(), tempOutput);
        }
        return temp;
    }

    @Override
    public void write(TemporaryResource temporaryResource, MediaType contentType, HttpOutputMessage outputMessage) {
        throw new UnsupportedOperationException();
    }

}
