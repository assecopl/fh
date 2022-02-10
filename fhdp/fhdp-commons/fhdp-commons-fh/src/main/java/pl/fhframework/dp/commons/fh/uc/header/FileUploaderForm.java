package pl.fhframework.dp.commons.fh.uc.header;

import lombok.Getter;
import lombok.Setter;
import org.springframework.core.io.Resource;
import pl.fhframework.model.forms.Form;

@Getter
@Setter
public class FileUploaderForm extends Form<FileUploaderForm.Model> {

    @Getter
    @Setter
    public static class Model {
        private Resource fileResource;
        private String fileName;
    }

    public FileUploaderForm() {
        setId("fileUploaderForm");
    }
}
