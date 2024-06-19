package pl.fhframework.docs.forms.component.model;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import pl.fhframework.core.designer.ComponentElement;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FileUploadElement extends ComponentElement {
    private Resource data;
    private List<Resource> datas;
    private String convertedData;
    private String uploadLabel = "Upload file";
    private String uploadMultipleLabel = "Upload files";

    private Resource resourceWithExt;

    private Resource requiredData;
    private String exampleText = "Change value and lost focus. You will se validation error on below FileUpload form component.";
}
