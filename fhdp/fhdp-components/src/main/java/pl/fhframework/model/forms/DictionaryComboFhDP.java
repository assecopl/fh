package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import pl.fhframework.BindingResult;
import pl.fhframework.ReflectionUtils;
import pl.fhframework.SessionManager;
import pl.fhframework.annotations.*;
import pl.fhframework.binding.ActionBinding;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.core.FhCL;
import pl.fhframework.core.FhException;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.uc.Parameter;
import pl.fhframework.event.EventRegistry;
import pl.fhframework.helper.AutowireHelper;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.dto.InMessageEventData;
import pl.fhframework.model.dto.ValueChange;
import pl.fhframework.model.forms.designer.BindingExpressionDesignerPreviewProvider;
import pl.fhframework.model.forms.provider.IComboDataProviderFhDP;
import pl.fhframework.model.forms.provider.NameValue;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.CONTENT;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 02/06/2020
 */
@Slf4j
@DocumentedComponent(value = "Enables users to quickly find and select from a pre-populated list of values as they type, leveraging searching and filtering.",
        icon = "fa fa-outdent")
@Control(parents = {PanelGroup.class, Group.class, Column.class, Tab.class, Row.class, Form.class, Repeater.class}, invalidParents = {Table.class}, canBeDesigned = false)
public class DictionaryComboFhDP extends ComboFhDP implements IGroupingComponent<DictionaryComboParameterFhDP> {


    public static final String ATTR_DISPLAY_ONLY_CODE = "displayOnlyCode";
    public static final String ATTR_POPUP_COLOR = "popupColor";
    private static final String ATTR_ROWS = "rows";
    private static final String ATTR_COLUMNS = "columns";
    private static final String ATTR_PAGE = "page";

    private static final String ATTR_TITLE = "title";
    private static final String ATTR_PAGES_COUNT = "pagesCount";
    private static final String VALUE_FOR_CHANGED_BINDING_ATTR = "valueFromChangedBinding";

    @JsonIgnore
    @Autowired
    EventRegistry eventRegistry;

    @JsonIgnore
    @Getter
    private PageModel pageModel;

    @Getter
    private Integer pageSize = 5;

    @JsonIgnore
    @Getter
    private Pageable pageable = PageRequest.of(0, pageSize);

    @Getter
    private Boolean searchRequested = false;

    @Getter Boolean languageChanged = false;

    @Getter
    private Integer page;

    @Getter
    private Integer pagesCount;

    @Getter
    private String valueFromChangedBinding;

    @Getter
    private List rows = new ArrayList();

    @Getter
    private List<NameValue> columns;

    @Getter
    private String title;

    @Getter
    private String guuid;

    @Getter
    private Boolean displayOnlyCode = true;

    @Getter
    private String popupColor;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_DISPLAY_ONLY_CODE) //Powiązanie bindingu z property
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = CONTENT)
    @DocumentedComponentAttribute(boundable = true, value = "Display only code, there will be no method invoke from provider.")
    private ModelBinding<Boolean> displayOnlyCodeModelBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_POPUP_COLOR)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = CONTENT)
    @DocumentedComponentAttribute(boundable = true, value = "Popup display color provided in hex.")
    private ModelBinding<String> popupBackgroundColorBinding;

    @Getter
    @Setter
    @XMLProperty
    private String provider;

    @JsonIgnore
    private IComboDataProviderFhDP dataProvider;

    @JsonIgnore
    private List<DictionaryComboParameterFhDP> subcomponents = new LinkedList<>();

    @JsonIgnore
    private ComponentStateSaver componentStateSaver = new ComponentStateSaver();



    @JsonIgnore
    @Getter
    private boolean processComponentChange = true;

    @JsonIgnore
    @Getter
    @Setter
    @CompilationTraversable
    private List<NonVisualFormElement> nonVisualSubcomponents = new ArrayList<>();

    protected boolean multiselect = false;

    @JsonIgnore
    private Method getValuesPaged;
    @JsonIgnore
    private Method getValue;
    @JsonIgnore
    protected Method getTitle;
    @JsonIgnore
    private List<DictionaryComboParameterFhDP> getValuesParamsList = new LinkedList<>();
    @JsonIgnore
    private List<DictionaryComboParameterFhDP> getValueParamsList = new LinkedList<>();
    @JsonIgnore
    private List<DictionaryComboParameterFhDP> getTitleParamsList = new LinkedList<>();

    @JsonIgnore
    private boolean valueSelected = false;
    @JsonIgnore
    private Object currentValue = null;
    @JsonIgnore
    private Object currentLastValue;

    @Getter @Setter
    private Boolean dirty = true;

    @Getter
    private String language;

    @JsonIgnore
    private String lastCodeSelected;

    protected boolean noResult = false;

    public DictionaryComboFhDP(Form form) {
        super(form);
    }

    @Override
    public void init(){
        log.debug("Init...");
        super.init();
        try {
            AutowireHelper.autowire(this, eventRegistry);
            this.resolveDataProvider();
            this.resolveMethods();
            this.resolveParameters();

            columns = dataProvider.getColumnDefinitions();
//            pageModel = new PageModel<NameValue>(pageable -> initPageModel(pageable));
            List<Object> paramsList = new LinkedList<>();
            paramsList.addAll(getValuesFromDictionaryComboParameters(this.getTitleParamsList));
            title = (String) ReflectionUtils.run(this.getTitle, this.dataProvider, paramsList.toArray());
            page = -25;
            pagesCount = 0;
            rows = new ArrayList();
            dirty = true;
            language = SessionManager.getUserSession().getLanguage().toLanguageTag();

            if(displayOnlyCodeModelBinding != null && displayOnlyCodeModelBinding.getBindingResult() != null) {
                displayOnlyCode = getDisplayOnlyCodeModelBinding().getBindingResult().getValue();
            }
            if(popupBackgroundColorBinding != null && popupBackgroundColorBinding.getBindingResult() != null){
                popupColor = popupBackgroundColorBinding.getBindingResult().getValue();
            }
            if (getModelBinding() != null) {
                BindingResult selectedBindingResult = getModelBinding().getBindingResult();
                if (selectedBindingResult != null) {
                    currentValue = selectedBindingResult.getValue();
                    dirty = currentValue == null;
                    if (currentValue != null) {
                        if (displayOnlyCode) {
                            selectedItem = currentValue;
                            filterText = String.valueOf(currentValue);
                            rawValue = String.valueOf(currentValue);
                        } else {
                            filterText = String.valueOf(currentValue);
                            selectedItem = getValueFromProvider(filterText);
                            if (selectedItem != null) {
                                rawValue = dataProvider.getDisplayValue(selectedItem);
                            } else {
                                rawValue = null;
                            }
                        }
                    } else {
                        selectedItem = currentValue;
                        filterText = null;
                        rawValue = null;
                    }

                }
            }
            if(getLastValueModelBinding() != null && getLastValueModelBinding().getBindingResult() != null) {
                currentLastValue = getLastValueModelBinding().getBindingResult().getValue();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            FhLogger.warn("DictionaryCombo: Provider not found.", ex);
        }
    }

    private Object getValueFromProvider(String code) {
        List<Object> allParamsList = new LinkedList<>();
        allParamsList.add(code);
        allParamsList.addAll(this.getValuesFromDictionaryComboParameters(this.getValueParamsList));
        return  (Object) ReflectionUtils.run(this.getValue, this.dataProvider, allParamsList.toArray());
    }

    @JsonIgnore
    public List<DictionaryComboParameterFhDP> getParameters() {
        return subcomponents;
    }


    /**
     * Resolve data provider bean
     * @throws ClassNotFoundException
     */
    private void resolveDataProvider() throws ClassNotFoundException {
        if (this.provider != null) {
            Class<? extends IComboDataProviderFhDP> providerClass = (Class<? extends IComboDataProviderFhDP>) FhCL.classLoader.loadClass(String.format(this.provider));
            this.dataProvider = AutowireHelper.getBean(providerClass);
        } else {
            log.error("DictionaryComboFhDP has empty provider for " +this.getId());
        }
    }


    protected void processFiltering(String text) {
//        log.debug(">>>processFiltering...");
//        filteredObjectValues.clear();
//        processValuesExternal(text);
//        Map<String, List<Object>> filtered = values.entrySet().stream()
//                .collect(Collectors.toMap(Map.Entry::getKey, p -> p.getValue().stream().collect(Collectors.toList())));
//        filteredObjectValues.putAll(filtered);
        filterInvoked = false;
    }

    /**
     * Resolves dynamic methods
     */
    private void resolveMethods() {
        if (provider != null) {
            this.getValuesPaged = ReflectionUtils.findMatchingPublicMethod(this.dataProvider.getClass(), "getValuesPaged").get();
            List<Object> paramsList = new LinkedList<>();
            Integer paramsCount = this.getValuesPaged.getParameterCount();
            for (int idx = 0; idx < paramsCount; idx++) {
                Optional<Parameter> p = ReflectionUtils.getMethodParamAnnotation(this.getValuesPaged, idx, pl.fhframework.core.uc.Parameter.class);
                if (p.isPresent()) {
                    String paramName = p.get().name();
                    Optional<DictionaryComboParameterFhDP> optionalDictComboParam = subcomponents.stream().filter(e -> Objects.equals(e.getName(), paramName)).findFirst();
                    if (optionalDictComboParam.isPresent()) {
                        DictionaryComboParameterFhDP dictComboParam = optionalDictComboParam.get();
                        this.getValuesParamsList.add(dictComboParam);
                    } else {
                        throw new FhException("No attribute for " + DictionaryComboParameterFhDP.class.getSimpleName() + " : " + paramName);
                    }
                }
            }

            this.getValue = ReflectionUtils.findMatchingPublicMethod(this.dataProvider.getClass(), "getValue").get();
            Integer paramsCount2 = this.getValue.getParameterCount();
            for (int idx = 0; idx < paramsCount2; idx++) {
                Optional<pl.fhframework.core.uc.Parameter> p = ReflectionUtils.getMethodParamAnnotation(this.getValue, idx, pl.fhframework.core.uc.Parameter.class);
                if (p.isPresent()) {
                    String paramName = p.get().name();
                    Optional<DictionaryComboParameterFhDP> optionalDictComboParam = subcomponents.stream().filter(e -> Objects.equals(e.getName(), paramName)).findFirst();
                    if (optionalDictComboParam.isPresent()) {
                        DictionaryComboParameterFhDP dictComboParam = optionalDictComboParam.get();
                        this.getValueParamsList.add(dictComboParam);
                        BindingResult br = dictComboParam.getModelBinding().getBindingResult();
                    } else {
                        throw new FhException("No attribute for " + DictionaryComboParameterFhDP.class.getSimpleName() + " : " + paramName);
                    }
                }
            }
            this.getTitle = ReflectionUtils.findMatchingPublicMethod(this.dataProvider.getClass(), "getTitle").get();
            Integer paramsCount3 = this.getTitle.getParameterCount();
            for (int idx = 0; idx < paramsCount3; idx++) {
                Optional<pl.fhframework.core.uc.Parameter> p = ReflectionUtils.getMethodParamAnnotation(this.getTitle, idx, pl.fhframework.core.uc.Parameter.class);
                if (p.isPresent()) {
                    String paramName = p.get().name();
                    Optional<DictionaryComboParameterFhDP> optionalDictComboParam = subcomponents.stream().filter(e -> Objects.equals(e.getName(), paramName)).findFirst();
                    if (optionalDictComboParam.isPresent()) {
                        DictionaryComboParameterFhDP dictComboParam = optionalDictComboParam.get();
                        this.getTitleParamsList.add(dictComboParam);
//                        BindingResult br = dictComboParam.getModelBinding().getBindingResult();
                    } else {
                        throw new FhException("No attribute for " + DictionaryComboParameterFhDP.class.getSimpleName() + " : " + paramName);
                    }
                }
            }

        }
    }


    /**
     * Adds component to the container.
     */
    @Override
    public void addSubcomponent(DictionaryComboParameterFhDP component) {
        subcomponents.add(component);
    }

    @Override
    public void removeSubcomponent(DictionaryComboParameterFhDP removedFormElement) {
        subcomponents.remove(removedFormElement);
    }

    @Override
    public List<DictionaryComboParameterFhDP> getSubcomponents() {
        return subcomponents;
    }

    protected String objectToString(Object s) {
        if (s == null) return "";

        if (s.getClass().equals(String.class)) {
            return (String) s;
        } else if(s.getClass().equals(Boolean.class)) {
            return s.toString();
        } else {
            return this.dataProvider.getDisplayValue(s);
        }
    }

    protected void changeSelectedItemBinding() {
        if (getModelBinding() != null) {
            if (selectedItem == null) {
                getModelBinding().setValue(selectedItem);
            } else {
                getModelBinding().setValue(this.dataProvider.getCode(selectedItem));
            }

        }
    }

    protected MultiValueMap<String, ComboItemDTO> collectValues(MultiValueMap<String, Object> valuesToConvert) {
        MultiValueMap<String, ComboItemDTO> filteredConvertedValues = new LinkedMultiValueMap<>();
        AtomicReference<Long> idx = new AtomicReference<>(0L);
        valuesToConvert.forEach((key, values) -> values.forEach(value -> {
            ComboItemDTO item;
            item = new ComboItemDTO(this.dataProvider.getCode(value), idx.get(), false, this.dataProvider.getDisplayValue(value));
            idx.getAndSet(idx.get() + 1);
            filteredConvertedValues.add(key, item);
        }));
        return filteredConvertedValues;
    }

    @Override
    protected boolean processValueBinding(ElementChanges elementChanges) {
//        System.out.println("processValueBinding... elementId: "
//                + elementChanges.getFormElementId()
//                + "; MyID: " + this.getId());

        if (getModelBinding() != null) {
            BindingResult selectedBindingResult = getModelBinding().getBindingResult();
            if (selectedBindingResult != null) {
                Object value = selectedBindingResult.getValue();
                if("null".equals(value)) {
                    value = null;
                }
                if(currentValue == null && value == null) return false;
//                System.out.println("processValueBinding... value: " + value);
                if((currentValue == null) || !currentValue.equals(value)) {
                    System.out.println("processValueBinding... change from: " + currentValue + " to " + value);
                    currentValue = value;
                    if (displayOnlyCode) {
                        System.out.println("processValueBinding. displayOnlyCode = true");
                        rawValue = (value == null)? null :  String.valueOf(value);
                        filterText = rawValue;
                        selectedItem = filterText;
                    } else {
                        System.out.println("processValueBinding. displayOnlyCode = false");
                        if(value != null) {
                            filterText = String.valueOf(value);
                            selectedItem = getValueFromProvider(filterText);
                            if(selectedItem != null) {
                                rawValue = dataProvider.getDisplayValue(selectedItem);
                            } else {
                                rawValue = filterText;
                            }
                            elementChanges.addChange(VALUE_FOR_CHANGED_BINDING_ATTR, (rawValue == null) ? "" : rawValue);
                        } else {
                            filterText = null;
                            selectedItem = null;
                            rawValue = null;
                        }
                    }
                    if(!this.searchPerformed) {
                        System.out.println("processValueBinding. New rawValue: " + ((rawValue == null) ? "real null" : rawValue));
                        elementChanges.addChange(VALUE_FOR_CHANGED_BINDING_ATTR, (rawValue == null) ? "" : rawValue);
                        System.out.println("After processValueBinding. dirty: " + dirty);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Function for geting actual values from DictionaryComboParameter based on its model bindings.
     *
     * @param dcp
     * @return List<Object>
     */
    List<Object> getValuesFromDictionaryComboParameters(List<DictionaryComboParameterFhDP> dcp) {
        List<Object> l = new LinkedList<>();
        dcp.forEach(dictionaryComboParameterFhDP -> {
            BindingResult br = dictionaryComboParameterFhDP.getModelBinding().getBindingResult();
            if (br != null) {
                l.add(br.getValue());
            } else {
                throw new FhException("No attribute for " + DictionaryComboParameterFhDP.class.getSimpleName() + " : " + dictionaryComboParameterFhDP.getName());
            }
        });

        return l;
    }

    @Override
    public ElementChanges updateView() {
        log.debug("updateView... page: " + page);
        ElementChanges elementChange = super.updateView();
        if(searchPerformed) {
            searchPerformed = false;
            elementChange.addChange(ATTR_ROWS, rows);
            elementChange.addChange(ATTR_PAGE, page);
            elementChange.addChange(ATTR_PAGES_COUNT, pagesCount);

            columns = dataProvider.getColumnDefinitions();
            elementChange.addChange(ATTR_COLUMNS, columns);
            if(this.noResult == true){
                //Force model clean when there is no matching values found.
                this.updateBindingForValue(null, getModelBinding(), getModelBinding().getBindingExpression(), this.getOptionalFormatter());
                this.updateFilterTextBinding();
                elementChange.addChange("searchRequested", this.filterText);
//                elementChange.addChange(VALUE_FOR_CHANGED_BINDING_ATTR, this.filterText);
                this.noResult = false;
            }
        }
        if(currentLastValue != null) {
            Object lastValue = elementChange.getChangedAttributes().get(ATTR_LAST_VALUE);
            if(lastValue.equals(currentLastValue)) {
                elementChange.getChangedAttributes().remove(ATTR_LAST_VALUE);
            }
        }
        if(languageChanged == true){
            List<Object> paramsList = new LinkedList<>();
            paramsList.addAll(getValuesFromDictionaryComboParameters(this.getTitleParamsList));
            title = (String) ReflectionUtils.run(this.getTitle, this.dataProvider, paramsList.toArray());
            elementChange.addChange(ATTR_TITLE, title);
            this.languageChanged = false;
        }

        return elementChange;
    }


    @Override
    public void updateModel(ValueChange valueChange){
//        System.out.println("updateModel... valueSelected: " + valueSelected);
        if(valueChange.hasAttributeChanged("text")){
            String newValue = valueChange.getStringAttribute("text");
            if("null".equals(newValue)) {
                newValue = null;
            }
            String refValue = filterText;
            System.out.println("updateModel... new value: " + ((newValue == null)?"real null":"string null"));
            if(newValue == null) {
                dirty = true;
                rawValue = null;
                filterText = null;
                valueSelected = true;
            } else {
                if (!displayOnlyCode) {
                    if (selectedItem != null) {
                        refValue = dataProvider.getDisplayValue(selectedItem);
                    } else {
                        refValue = filterText;
                    }
                }
                if (!newValue.equals(refValue)) {
                    dirty = true;
                    rawValue = newValue;
                    System.out.println("updateModel... changed rawValue: " + rawValue);
                    filterText = rawValue;
                    boolean singleSearch = this.rawValue!=null && (!dirty || !getAvailability().equals(AccessibilityEnum.EDIT));
                    search(singleSearch, true);
                }
            }
//            refreshView();
        }
        if(valueSelected) {
            System.out.println("updateModel... value selected: " + rawValue);
            valueSelected = false;
            Object result = null;
            if(filterText != null) {
                selectedItem = getValueFromProvider(filterText);
                result = dataProvider.getCode(selectedItem);

                //Validate
                ValidateInput input = new ValidateInput();
                input.setId(this.getGuuid());
                input.setCode(filterText);
                dictionaryComboValidate(input);

                dirty = false;
            } else {
                selectedItem = null;
            }
            this.updateBindingForValue(result, getModelBinding(), getModelBinding().getBindingExpression(), this.getOptionalFormatter());
            if(!displayOnlyCode) {
                if(selectedItem != null) {
                    rawValue = dataProvider.getDisplayValue(selectedItem);
                }
            }
        }
        if(valueChange.hasAttributeChanged("dirty")){
            dirty = valueChange.getBooleanAttribute("dirty");
            System.out.println("updateModel. Changed dirty to: " + dirty);
        }
    }

    private void updateRows(Pageable pageable) {
        pageModel.doRefresh(pageable);
        pagesCount = pageModel.getPage().getTotalPages();
        page = pageModel.getPage().getNumber();
        log.debug("updateRows. page: " + page);
        rows.clear();
        rows.addAll(pageModel.getPage().getContent());

    }

//    private void changePage(Integer newPage, String valueToSearch) {
//        changePage(newPage, valueToSearch, false);
//    }

    private void search(boolean singleSearch, boolean currentValue) {
        pageable = PageRequest.of(0, pageSize);
        log.debug("New pageable page no: " + pageable.getPageNumber());
        List<Object> allParamsList = new LinkedList<>();
        if(currentValue) {
            if (singleSearch) {
                if(displayOnlyCode) {
                    allParamsList.add(rawValue);
                } else {
                    allParamsList.add(dataProvider.getCode(selectedItem));
                }
            } else {
                allParamsList.add(filterText);
                allParamsList.add(pageable);
            }
        } else {
            allParamsList.add(getLastValue());
        }
        allParamsList.addAll(this.getValuesFromDictionaryComboParameters(this.getValuesParamsList));
        if (singleSearch) {
            Object element = ReflectionUtils.run(this.getValue, this.dataProvider, allParamsList.toArray());
            rows.clear();
            rows.add(element);
            page = 0;
            pagesCount = 1;
        } else {
            pageModel = (PageModel) ReflectionUtils.run(this.getValuesPaged, this.dataProvider, allParamsList.toArray());
            updateRows(pageable);
            if(this.pageModel.getPage().isEmpty()){
                this.noResult = true;
            }

        }
        searchPerformed = true;
//        updateView();
    }

    /**
     * Important for events in view mode!!!
     * prevents from pl.fhframework.core.FhFormException: Request for given form component cannot be processed.
     *
     * @param eventType
     * @return
     */
    @Override
    public boolean isModificationEvent(String eventType) {
        if("onClickSearchIcon".equals(eventType)) return false;
        else if("cleanupSearch".equals(eventType)) return false;
        else if("onClickLastValue".equals(eventType)) return false;
        else return true;
    }

    private boolean isCountableEvent(String eventType) {
        if("nextPage".equals(eventType)) return true;
        else if("prevPage".equals(eventType)) return true;
        else if("recordSelected".equals(eventType)) return true;
        else if("dictionaryComboValidate".equals(eventType)) return true;
        else return !isModificationEvent(eventType);
    }

    @JsonIgnore
    private boolean searchPerformed = false;

    @JsonIgnore
    private String lastEvent;
    @JsonIgnore
    private int counter = 0;

    @Override
    public Optional<ActionBinding> getEventHandler(InMessageEventData eventData) {
        System.out.println("getEventHandler: " + eventData.getEventType() + "; " + ((eventData.getActionName() == null) ? "real null" : eventData.getActionName()));
        if(isCountableEvent(eventData.getEventType())) {
            if (!eventData.getEventType().equals(lastEvent)) {
                counter = 0;
                switch (eventData.getEventType()) {
                    case "onClickSearchIcon":
                        System.out.println("onClickSearchIcon. dirty: " + dirty);
                        boolean singleSearch = this.rawValue!=null && (!dirty || !getAvailability().equals(AccessibilityEnum.EDIT));
                        search(singleSearch, true);
                        break;
                    case "recordSelected":
                        String code = eventData.getActionName();
                        if("null".equals(code)) {
                            code = null;
                        }
                        if(code != null) {
                            filterText = eventData.getActionName();
                            valueSelected = true;
                            dirty = false;
                            lastCodeSelected = filterText;
                            rawValue = code;
                        } else {
                            filterText = null;
                            dirty = true;
                        }
                        page = -15;
                        pagesCount = 0;
                        rows = new ArrayList();
                        break;
                    case "dictionaryComboValidate":
                        String json = eventData.getActionName();
                        ValidateInput input = (ValidateInput) getFromJson(json, ValidateInput.class);
                        dictionaryComboValidate(input);
                        break;
                    case "cleanupSearch":
                        page = -10;
                        pagesCount = 0;
                        rows = new ArrayList();
                        break;
                    case "setGuuid":
                        setGuuidByEventData(eventData);
                        break;
                    case "nextPage":
                        pageable = pageable.next();
                        updateRows(pageable);
                        break;
                    case "prevPage":
                        pageable = pageable.previousOrFirst();
                        updateRows(pageable);
                        break;
                    case "onClickLastValue":
                        search(true, false);
                        break;
                    default:
                        break;
                }
            }
            searchPerformed = true;
            counter++;
            if (counter < 2) {
                lastEvent = eventData.getEventType();
            } else {
                counter = 0;
                lastEvent = null;
            }
            return Optional.empty();
        } else {
            counter = 0;
            lastEvent = null;
            return super.getEventHandler(eventData);
        }
    }

    private void dictionaryComboValidate(ValidateInput input) {
        Object testValue = getValueFromProvider(input.getCode());
        ValidateResult result = new ValidateResult();
        result.setId(input.getId());
        if(testValue != null) {
            result.setResult(true);
        }
        String jsonResult = toJson(result);
        eventRegistry.fireCustomActionEvent("dictionaryComboValidated", jsonResult);
    }

    private void setGuuidByEventData(InMessageEventData eventData) {
        String json = eventData.getActionName();
        ValidateInput input = (ValidateInput) getFromJson(json, ValidateInput.class);
        this.guuid = input.getId();
    }

    public static Object getFromJson(String json, Class mappedClass) {
        Object mappedCase = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            mappedCase = mapper.readValue(json, mappedClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mappedCase;
    }

    public static String toJson(Object bean) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(bean);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void validate() {
        if (getModelBinding() != null && getModelBinding().getBindingResult() != null) {
            processValidationForThisComponent();
        }
    }

    @Override
    public void onSessionLanguageChange(String lang) {
        if(!displayOnlyCode) {
            selectedItem = getValueFromProvider(filterText);
            if (selectedItem != null) {
                rawValue = dataProvider.getDisplayValue(selectedItem);
            } else {
                rawValue = null;
            }
        }
        languageChanged = true;
        super.onSessionLanguageChange(lang);
    }

    @Getter @Setter
    private static class ValidateInput {
        private String id;
        private String code;
    }

    @Getter @Setter
    private static class ValidateResult {
        private String id;
        private boolean result = false;
    }


    private void resolveParameters() {
        if( this.getValuesParamsList.size() > 0){
            this.getValuesParamsList.forEach(dictionaryComboParameter -> {
                dictionaryComboParameter.resolveValue();
            });
        }
    }

}
