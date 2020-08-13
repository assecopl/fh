package pl.fhframework.model.forms.docs;

import lombok.Getter;

@Getter
public class DocumentationPathParams {

    private final String formClassPath;
    private final String ucClassPath;
    private final String modelClassPath;
    private String componentsPackage;

    public DocumentationPathParams(String formClassPath, String modelClassPath, String ucClassPath) {
        this.formClassPath = formClassPath;
        this.modelClassPath = modelClassPath;
        this.ucClassPath = ucClassPath;
    }

    public DocumentationPathParams(String formClassPath, String modelClassPath, String componentsPackage, String ucClassPath) {
        this.formClassPath = formClassPath;
        this.modelClassPath = modelClassPath;
        this.componentsPackage = componentsPackage;
        this.ucClassPath = ucClassPath;
    }
}
