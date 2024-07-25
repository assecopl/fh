package pl.fhframework.dp.commons.utils.image;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import pl.fhframework.dp.commons.utils.file.FileHelper;

import java.io.IOException;
import java.io.InputStream;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
@EnableConfigurationProperties
@Slf4j
public class ImageUploadServiceTest {

    @Autowired
    private ImageUploadService imageUploadService;

    @Test
    public void compactIfPossible() throws IOException {
        InputStream is = ImageUploadServiceTest.class.getResourceAsStream("/img/P2280010.jpeg");
        byte[] content = FileHelper.readInputStreamtoBytes(is);
        FileHelper.writeFile("/tmp/src.jpeg", content);

        Resource file = new FileUrlResource("/tmp/src.jpeg");
        byte[] bytes = imageUploadService.compactIfPossible(file, "image/jpeg", 2048);
        FileHelper.writeFile("/tmp/xxx.jpeg", bytes);
    }

}