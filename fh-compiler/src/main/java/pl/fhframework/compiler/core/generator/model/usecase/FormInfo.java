package pl.fhframework.compiler.core.generator.model.usecase;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.compiler.core.generator.model.form.FormMm;
import pl.fhframework.compiler.core.generator.model.form.PropertyMm;
import pl.fhframework.compiler.core.uc.dynamic.model.element.command.ShowForm;
import pl.fhframework.compiler.forms.FormsManager;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.helper.AutowireHelper;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class FormInfo {
    @JsonIgnore
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    private ShowForm showForm;

    @JsonIgnore
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    private FormMm form;

    @Autowired
    @JsonIgnore
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    private FormsManager formsManager;

    public FormInfo(ShowForm showForm, FormMm form) { // todo: change to FormMM
        this.showForm = showForm;
        this.form = form;
        AutowireHelper.autowire(this, formsManager);
    }

    @JsonGetter
    public String getId() {
        return showForm.getForm();
    }

    @JsonGetter
    public Set<EventSignatureInfo> getEvents() {
        if (form == null) { // todo: dialog
            return null;
        }
        return formsManager.getFormActions(DynamicClassName.forClassName(showForm.getForm())).stream().map(EventSignatureInfo::new).collect(Collectors.toSet());
    }

    @JsonGetter
    public boolean isInternalModel() {
        if (form == null) { // todo: dialog
            return false;
        }
        return form.isInternalModel();
    }

    @JsonGetter
    public List<PropertyMm> getInternalModel() {
        if (form == null) { // todo: dialog
            return null;
        }
        return form.getInternalModel();
    }

    @JsonGetter
    public String getModelType() {
        if (form == null) { // todo: dialog
            return null;
        }
        return form.getModelType();
    }
}
