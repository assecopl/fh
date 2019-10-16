package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.util.ClassUtils;

import pl.fhframework.core.FhException;
import pl.fhframework.core.forms.IHasBoundableLabel;
import pl.fhframework.core.forms.IValidatedComponent;
import pl.fhframework.BindingResult;
import pl.fhframework.SessionManager;
import pl.fhframework.annotations.*;
import pl.fhframework.binding.*;
import pl.fhframework.helper.AutowireHelper;
import pl.fhframework.io.FileService;
import pl.fhframework.io.IFileMaxSized;
import pl.fhframework.io.IResourced;
import pl.fhframework.model.PresentationStyleEnum;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.dto.ValueChange;
import pl.fhframework.model.forms.designer.BindingExpressionDesignerPreviewProvider;
import pl.fhframework.model.forms.designer.ButtonStyleFixedValuesProvider;
import pl.fhframework.model.forms.utils.LanguageResolver;
import pl.fhframework.model.forms.validation.ValidationFactory;
import pl.fhframework.validation.ConstraintViolation;
import pl.fhframework.validation.FieldValidationResult;
import pl.fhframework.validation.FormFieldHints;
import pl.fhframework.validation.IValidationResults;
import pl.fhframework.validation.ValidationManager;
import pl.fhframework.model.dto.InMessageEventData;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;

import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.BEHAVIOR;
import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.CONTENT;
import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.LOOK_AND_STYLE;

@DesignerControl(defaultWidth = 2)
@DocumentedComponent(value = "Component responsible for file upload", icon = "fa fa-upload")
@Control(parents = {PanelGroup.class, Column.class, Tab.class, Row.class, Form.class, Repeater.class, Group.class}, invalidParents = {Table.class}, canBeDesigned = true)
public class FileUpload extends FormElement implements TableComponent<FileUpload>, IChangeableByClient, Boundable, IValidatedComponent, IFileMaxSized, IResourced, IHasBoundableLabel, Styleable {
    private static final String REQUIRED_ATTR = "required";
    private static final String LABEL_ATTR = "label";
    public static final String STYLE_ATTR = "style";
    protected static final String VALIDATION_LABEL_ATTR = "validationLabel";
    private static final String MESSAGE_FOR_FIELD_ATTR = "messageForField";

    private final static String ON_UPLOAD_ATTR = "onUpload";
    private static final String FILE_NAMES_ATTR = "fileNames";
    private static final String FILE_ATTR = "file";
    private static final String FILES_ATTR = "files";
    private static final String FILE_IDS_ATTR = "fileIds";
    private static final String MULTIPLE_ATTR = "multiple";
    private static final String MAX_SIZE_ATTR = "maxSize";
    private static final String LABEL_HIDDEN_ATTR = "labelHidden";
    private static final String EXTENSIONS_ATTR = "extensions";


    @Autowired
    @JsonIgnore
    private FileService fileService;

    @Getter
    @XMLProperty(defaultValue = "-")
    @DesignerXMLProperty(functionalArea = BEHAVIOR)
    @DocumentedComponentAttribute(value = "Action which will be executed after file is uploaded.", defaultValue = "-")
    private ActionBinding onUpload;

    @JsonIgnore
    private List<String> fileNames;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = FILE_ATTR)
    @DesignerXMLProperty(allowedTypes = Resource.class, functionalArea = CONTENT, commonUse = true)
    @DocumentedComponentAttribute(value = "Binding for file.")
    private ModelBinding fileModelBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = FILES_ATTR)
    @DesignerXMLProperty(allowedTypes = List.class, functionalArea = CONTENT, commonUse = true)
    @DocumentedComponentAttribute(value = "Binding for multiple files.")
    private ModelBinding filesModelBinding;

    @Getter
    @Setter
    @JsonIgnore
    @XMLProperty
    @DocumentedComponentAttribute(defaultValue = "false", value = "User can define if component is required for Form.")
    private boolean required;

    @Getter
    @Setter
    @XMLProperty(defaultValue = "1000000")
    @DocumentedComponentAttribute(defaultValue = "1000000", value = "Maximum allowed size of uploaded file " +
            "in bytes. The maximum file size depends on the configuration of the server. " +
            "This information should be obtained from the system administrator.")
    private long maxSize = 1000000;

    @Getter
    private String label = "";

    @Getter
    private Style style = Style.PRIMARY;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(EXTENSIONS_ATTR)
    @DocumentedComponentAttribute(value = "Defines what file extensions are allowed for upload (comma separated list of extensions e.g. .xml,.txt).", boundable = true)
    private ModelBinding<String> extensionsBinding;

    @Getter
    private String extensions;

    @Getter
    @Setter
    @XMLProperty
    @DocumentedComponentAttribute(boundable = true, value = "Represents label for created component.")
    private boolean labelHidden;

    @Getter
    @Setter
    @XMLProperty
    @DocumentedComponentAttribute(boundable = true, value = "Is multiple file upload allowed.")
    private boolean multiple;

    @Getter
    @Setter
    @JsonIgnore
    @XMLProperty(value = STYLE_ATTR)
    @DocumentedComponentAttribute(boundable = true, defaultValue = "primary", value = "Determines style of FileUpload button. It is possible to select one of six Bootstrap classes: default, primary, success, info, warning, danger or bind it with variable.")
    @DesignerXMLProperty(functionalArea = LOOK_AND_STYLE, priority = 77, fixedValuesProvider = ButtonStyleFixedValuesProvider.class)
    private ModelBinding styleModelBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = LABEL_ATTR)
    @DesignerXMLProperty(commonUse = true, functionalArea = CONTENT,
            previewValueProvider = BindingExpressionDesignerPreviewProvider.class)
    @DocumentedComponentAttribute(boundable = true, value = "Represents label for created component.")
    private ModelBinding labelModelBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = VALIDATION_LABEL_ATTR)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, priority = 100, functionalArea = CONTENT)
    @DocumentedComponentAttribute(boundable = true, value = "Represents label for created component used in validation messages. If not set, falls back to label attribute's value.")
    private ModelBinding validationLabelModelBinding;

    @Getter
    private PresentationStyleEnum presentationStyle;

    @Getter
    private String messageForField = "";

    @Getter
    private String language;

    public FileUpload(Form form) {
        super(form);
    }

    @Override
    public void init() {
        super.init();
        AutowireHelper.autowire(this, fileService);
    }

    @Override
    public FileUpload createNewSameComponent() {
        return new FileUpload(getForm());
    }

    @Override
    public void doCopy(Table table, Map<String, String> iteratorReplacements, FileUpload clone) {
        TableComponent.super.doCopy(table, iteratorReplacements, clone);
        clone.setOnUpload(table.getRowBinding(getOnUpload(), clone, iteratorReplacements));
        clone.setFileModelBinding(table.getRowBinding(getFileModelBinding(), clone, iteratorReplacements));
        clone.setFilesModelBinding(table.getRowBinding(getFilesModelBinding(), clone, iteratorReplacements));
        clone.setRequired(isRequired());
        clone.setMaxSize(getMaxSize());
        clone.setExtensionsBinding(table.getRowBinding(getExtensionsBinding(), clone, iteratorReplacements));
        clone.setLabelHidden(isLabelHidden());
        clone.setMultiple(isMultiple());
        clone.setLabelModelBinding(table.getRowBinding(getLabelModelBinding(), clone, iteratorReplacements));
    }

    @Override
    public Optional<ActionBinding> getEventHandler(InMessageEventData eventData) {
        if (ON_UPLOAD_ATTR.equals(eventData.getEventType())) {
            return Optional.ofNullable(onUpload);
        } else {
            return super.getEventHandler(eventData);
        }
    }

    @Override
    public void updateModel(ValueChange valueChange) {
        List<String> fileIds = valueChange.getStringListAttribute(FILE_IDS_ATTR);

        if (this.multiple) {
            List<Resource> tempResources = new ArrayList<>();
            fileIds.forEach(c -> {
                Resource tempResource = fileService.getResource(c, SessionManager.getUserSession());
                tempResources.add(tempResource);
            });

            if (filesModelBinding == null) {
                throw new FhException("Files container binding not set. Please bind resource to {files} option.");
            }
            filesModelBinding.setValue(tempResources);
        } else {
            Resource tempResource = fileService.getResource(fileIds.get(0), SessionManager.getUserSession());

            if (fileModelBinding == null) {
                throw new FhException("File container binding not set. Please bind resource to {file} option.");
            }
            fileModelBinding.setValue(tempResource);
        }

    }

    @Override
    protected ElementChanges updateView() {
        ElementChanges elementChanges = super.updateView();
        if (fileModelBinding != null || filesModelBinding != null) {
            skipSettingPresentation(elementChanges, getForm());
        }

        if (extensionsBinding != null) {
            this.extensions = extensionsBinding.resolveValueAndAddChanges(this, elementChanges, this.extensions, EXTENSIONS_ATTR);
        }

        this.language = LanguageResolver.languageChanges(getForm().getAbstractUseCase().getUserSession(), this.language, elementChanges);

        if (labelModelBinding != null) {
            Object newLabelValueObj = labelModelBinding.getBindingResult().getValue();
            String newLabelValue = newLabelValueObj != null ? newLabelValueObj.toString() : null;

            if (!areValuesTheSame(newLabelValue, label)) {
                this.label = newLabelValue;
                elementChanges.addChange(LABEL_ATTR, this.label);
            }
        }

        boolean refreshView = processStyleBinding(elementChanges, false);
        refreshView = processFileNameBinding(elementChanges, refreshView);
        this.prepareComponentAfterValidation(elementChanges);
        if (refreshView) {
            refreshView();
        }

        return elementChanges;
    }

    protected boolean processStyleBinding(ElementChanges elementChanges, boolean refreshView) {
        BindingResult labelBidingResult = styleModelBinding != null ? styleModelBinding.getBindingResult() : null;
        if (labelBidingResult != null) {
            String newLabelValue = this.convertBindingValueToString(labelBidingResult);
            if (!areValuesTheSame(newLabelValue, style.toValue())) {
                this.style = Style.forValue(newLabelValue);
                elementChanges.addChange(STYLE_ATTR, this.style);
                refreshView = true;
            }
        }
        return refreshView;
    }

    private boolean processFileNameBinding(ElementChanges elementChanges, boolean refreshView) {
        if (!this.multiple && fileModelBinding != null) {
            BindingResult labelBindingResult = fileModelBinding.getBindingResult();
            if (labelBindingResult != null) {
                Resource resource = (Resource) labelBindingResult.getValue();
                String resourceName = resource == null ? "" : resource.getFilename();
                if (!areValuesTheSame(resourceName, fileNames)) {
                    this.fileNames = Collections.singletonList(resourceName);
                    elementChanges.addChange(FILE_NAMES_ATTR, this.fileNames);
                    refreshView = true;
                }
            }
        }

        if (this.multiple && filesModelBinding != null) {
            BindingResult labelBindingResult = filesModelBinding.getBindingResult();
            if (labelBindingResult != null) {
                List<Resource> resources = (List<Resource>) labelBindingResult.getValue();
                List<String> resourceNames = resources == null ?
                        new ArrayList<>() :
                        resources.stream().map(Resource::getFilename).collect(Collectors.toList());
                if (!areValuesTheSame(resourceNames, fileNames)) {
                    this.fileNames = resourceNames;
                    elementChanges.addChange(FILE_NAMES_ATTR, this.fileNames);
                    refreshView = true;
                }
            }
        }

        return refreshView;
    }

    @Override
    public void validate() {
        ValidationManager<FileUpload> vm = ValidationFactory.getInstance().getFileUploadValidationProcess();
        List<ConstraintViolation<FileUpload>> formComponentValidationResult = vm.validate(this);
        IValidationResults validationResults = getForm().getAbstractUseCase().getUserSession().getValidationResults();
        formComponentValidationResult.forEach(x -> {
            BindingResult bindingResult = getModelBinding().getBindingResult();
            validationResults.addCustomMessageForComponent(this, bindingResult.getParent(), bindingResult.getAttributeName(), x.getMessage(), PresentationStyleEnum.BLOCKER);
        });
    }

    @Override
    public void prepareComponentAfterValidation(ElementChanges elementChanges) {
        if (getForm().getAbstractUseCase() != null) {
            IValidationResults validationResults = getForm().getAbstractUseCase().getUserSession().getValidationResults();

            BindingResult bindingResult = this.getModelBinding() != null ? this.getModelBinding().getBindingResult() : null;
            List<FieldValidationResult> fieldValidationResultFor = bindingResult == null ? Collections.emptyList() : validationResults.getFieldValidationResultFor(bindingResult.getParent(), bindingResult.getAttributeName());
            if (getAvailability() != AccessibilityEnum.EDIT) {
                fieldValidationResultFor.removeIf(FieldValidationResult::isFormSource);
            }
            processStylesAndHints(elementChanges, fieldValidationResultFor);
        }
    }

    @Override
    public ModelBinding getModelBinding() {
        return this.multiple ? getFilesModelBinding() : getFileModelBinding();
    }

    protected void processStylesAndHints(ElementChanges elementChanges, List<FieldValidationResult> fieldValidationResults) {
        // styling
        FormFieldHints formFieldHints = processPresentationStyle(elementChanges, fieldValidationResults);
        // messaging
        processMessagesForHints(elementChanges, formFieldHints);
    }

    private FormFieldHints processPresentationStyle(ElementChanges elementChanges, List<FieldValidationResult> fieldValidationResults) {
        PresentationStyleEnum oldPresentationStyle = this.presentationStyle;
        FormFieldHints formFieldHints = null;
        if (getModelBinding() != null && getModelBinding().getBindingResult() != null) {
            formFieldHints = calculatePresentationStyle(getModelBinding().getBindingResult());
            this.presentationStyle = (formFieldHints != null) ? formFieldHints.getPresentationStyleEnum() : null;
        } else {
            this.presentationStyle = null;
        }
        if (!fieldValidationResults.isEmpty() && (this.presentationStyle == null || this.presentationStyle != PresentationStyleEnum.BLOCKER)) {
            this.presentationStyle = PresentationStyleEnum.BLOCKER;
        }
        if (oldPresentationStyle != this.presentationStyle) {
            elementChanges.addChange(PRESENTATION_STYLE_ATTR, this.presentationStyle);
        }
        return formFieldHints;
    }

    private FormFieldHints calculatePresentationStyle(BindingResult bindingResult) {
        if (bindingResult.getParent() != null && bindingResult.getAttributeName() != null && getAvailability() == AccessibilityEnum.EDIT) {
            return getForm().getAbstractUseCase().getUserSession().getValidationResults().getPresentationStyleForField(bindingResult.getParent(), bindingResult.getAttributeName());
        } else {
            return null;
        }
    }

    private void processMessagesForHints(ElementChanges elementChanges, FormFieldHints formFieldHints) {
        if (formFieldHints == null) {
            return;
        }
        String oldHints = this.messageForField;
        this.messageForField = formFieldHints.getHints();
        if ((!Objects.equals(oldHints, messageForField)) || (oldHints != null && !oldHints.equals(messageForField)) || (messageForField != null && !messageForField.equals(oldHints))) {
            elementChanges.addChange(MESSAGE_FOR_FIELD_ATTR, this.messageForField);
        }
    }

    public void setOnUpload(ActionBinding onUpload) {
        this.onUpload = onUpload;
    }

    public IActionCallbackContext setOnUpload(IActionCallback onUpload) {
        return CallbackActionBinding.createAndSet(onUpload, this::setOnUpload);
    }

    @JsonIgnore
    @Override
    public Resource getResource() throws FileNotFoundException {
        Object value = getBindingResult(fileModelBinding);
        if (value != null && ClassUtils.isAssignableValue(Resource.class, value)) {
            return (Resource) value;
        }

        return null;
    }

    @JsonIgnore
    @Override
    public List<Resource> getResources() throws FileNotFoundException {
        Object value = getBindingResult(filesModelBinding);
        if (value != null && ClassUtils.isAssignableValue(List.class, value)) {
            return (List<Resource>) value;
        }

        return null;
    }

    private Object getBindingResult(ModelBinding binding) {
        if (binding != null) {
            BindingResult bindingResult = binding.getBindingResult();

            if (bindingResult != null) {
                return bindingResult.getValue();
            }
        }

        return null;
    }

    @Override
    protected List<ActionBinding> getAvailablityAffectingActions() {
        return Arrays.asList(onUpload);
    }
}
