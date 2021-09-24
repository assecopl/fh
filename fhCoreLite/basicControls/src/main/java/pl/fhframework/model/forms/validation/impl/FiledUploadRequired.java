package pl.fhframework.model.forms.validation.impl;

import org.springframework.stereotype.Component;

import pl.fhframework.model.forms.FileUpload;

@Component
public class FiledUploadRequired extends AbstractRequiredFormElementValidator<FileUpload> {

    @Override
    public boolean canProcessValidation(FileUpload formElement) {
        return formElement.isRequired();
    }

    @Override
    public boolean isNotValid(FileUpload formElement) {
        return formElement.getFileModelBinding().getBindingResult() == null || formElement.getFileModelBinding().getBindingResult().getValue() == null;
    }
}
