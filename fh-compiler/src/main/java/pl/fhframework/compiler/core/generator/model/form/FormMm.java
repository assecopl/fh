package pl.fhframework.compiler.core.generator.model.form;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.fhframework.compiler.core.generator.ModuleMetaModel;
import pl.fhframework.compiler.core.generator.model.MetaModel;
import pl.fhframework.compiler.core.generator.model.usecase.EventSignatureInfo;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.model.forms.Form;
import pl.fhframework.model.forms.attribute.FormModalSize;
import pl.fhframework.model.forms.attribute.FormType;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@JsonPropertyOrder({"id", "type", "tagName", "className", "tagName", "container", "viewMode", "modalSize", "componentTree"})
public class FormMm extends MetaModel {
    @JsonIgnore
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    private Form<?> form;

    @JsonIgnore
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    private ModuleMetaModel metadata;

    @Getter
    private String id;

    public FormMm(Form<?> form, List<String> dependencies, ModuleMetaModel metadata, String id) {
        setDependencies(dependencies);
        this.form = form;
        this.metadata = metadata;
        this.id = id;
    }

    @JsonGetter
    public String getContainer() {
        return this.form.getContainer();
    }

    @JsonGetter
    public String getTagName() {
        return form.getGenerationUtils().getTagName();
    }

    @JsonGetter
    public String getFormId() {
        return this.form.getId();
    }

    @JsonGetter
    public String getClassName() {
        return this.form.getClass().getSimpleName();
    }

    @JsonGetter
    public Form.ViewMode getViewMode() {
        return this.form.getViewMode();
    }

    @JsonGetter
    public FormType getType() {
        return this.form.getFormType();
    }

    @JsonGetter
    public FormModalSize getModalSize() {
        return this.form.getModalSize();
    }

    @JsonGetter
    public Collection<ComponentMm> getComponentTree() {
        return this.form.getSubcomponents().stream().map(ComponentMm::new).collect(Collectors.toList());
    }

    @JsonGetter(value = "isInternalModel")
    public boolean isInternalModel() {
        return StringUtils.isNullOrEmpty(form.getModelDefinition().getExternalClass());
    }

    @JsonGetter(value = "internalModel")
    public List<PropertyMm> getInternalModel() {
        return form.getModelDefinition().getProperties().stream().map(PropertyMm::new).collect(Collectors.toList());
    }

    @JsonGetter
    public String getModelType() {
        return form.getModelDefinition().getExternalClass();
    }

    @JsonGetter
    public Set<EventSignatureInfo> getEvents() {
        return form.getGenerationUtils().getEvents().stream().map(EventSignatureInfo::new).collect(Collectors.toSet());
    }

    @Override
    public <T> T provideImpl() {
        return (T) form;
    }
}
