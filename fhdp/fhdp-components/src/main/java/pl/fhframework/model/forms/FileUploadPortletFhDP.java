package pl.fhframework.model.forms;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import pl.fhframework.BindingResult;
import pl.fhframework.annotations.*;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.forms.designer.BindingExpressionDesignerPreviewProvider;
import pl.fhframework.model.forms.widgets.Widget;

import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.CONTENT;

@Getter
@Setter
@DesignerControl(defaultWidth = 12)
@Control(parents = {PanelGroup.class, Tab.class, Row.class, Form.class, Group.class}, invalidParents = {Table.class, Widget.class, Repeater.class}, canBeDesigned = true)
@DocumentedComponent(category = DocumentedComponent.Category.IMAGE_HTML_MD, value = "Komponent do uploadu plików, bindowalny maxFileSize", icon = "fa fa-upload")
public class FileUploadPortletFhDP extends FileUpload {

    public static final String ATTR_MAX_FILE_SIZE = "maxFileSize";
    public static final String ATTR_BASE_URL = "fhBaseUrl";
    public static final String ATTR_CONTEXT_PATH = "fhContextPath";

    @Getter
    @Setter
    @Value("${fhdp.fileupload.protlet.baseUrl}")
    private String fhBaseUrl;

    @Getter
    @Setter
    @Value("${fhdp.fileupload.protlet.contextPath}")
    private String fhContextPath;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_MAX_FILE_SIZE)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = CONTENT)
    @DocumentedComponentAttribute(boundable = true, value = "Max file upload size.")
    private ModelBinding<Long> maxSizeModelBinding;


    public FileUploadPortletFhDP(Form form) {
        super(form);
    }

    @Override
    public void init() {
        super.init();
        if (maxSizeModelBinding != null) {
            BindingResult bidingResult = maxSizeModelBinding.getBindingResult();
            if (bidingResult != null) {
                if (bidingResult.getValue() != null) {
                    setMaxSize(convertValue(bidingResult.getValue(), Long.class));
                }
            }
        }
    }

    @Override
    protected ElementChanges updateView() {
        ElementChanges elCh = super.updateView();

        if (maxSizeModelBinding != null) {
            setMaxSize(maxSizeModelBinding.resolveValueAndAddChanges(this, elCh, getMaxSize(), ATTR_MAX_FILE_SIZE));
        }

        return elCh;
    }
}
