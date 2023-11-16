package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import pl.fhframework.BindingResult;
import pl.fhframework.ReflectionUtils;
import pl.fhframework.SessionManager;
import pl.fhframework.annotations.*;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.core.FhCL;
import pl.fhframework.core.FhException;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.uc.Parameter;
import pl.fhframework.events.I18nFormElement;
import pl.fhframework.helper.AutowireHelper;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.dto.ValueChange;
import pl.fhframework.model.forms.designer.BindingExpressionDesignerPreviewProvider;
import pl.fhframework.model.forms.provider.IComboDataProviderFhDP;
import pl.fhframework.model.forms.provider.NameValue;

import java.lang.reflect.Method;
import java.util.*;

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
public class DictionaryLookupFhDP extends BaseInputFieldWithKeySupport implements IGroupingComponent<DictionaryComboParameterFhDP>, I18nFormElement {


    public static final String ATTR_DISPLAY_ONLY_CODE = "displayOnlyCode";
    public static final String ATTR_POPUP_COLOR = "popupColor";
    private static final String ATTR_ROWS = "rows";
    private static final String ATTR_COLUMNS = "columns";
    private static final String ATTR_PAGE = "page";

    private static final String ATTR_TITLE = "title";
    private static final String ATTR_PAGES_COUNT = "pagesCount";
    private static final String VALUE_FOR_CHANGED_BINDING_ATTR = "valueFromChangedBinding";

//    @JsonIgnore
//    @Autowired
//    EventRegistry eventRegistry;

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

    // Value of selected SRC instance from provider. RESULT is in value.
    @JsonIgnore
    private Object currentSourceObject = null;
//    TODO: for AIS
//    @JsonIgnore
//    private Object currentLastValue;


    @Getter
    private String language;

    protected boolean noResult = false;


    public DictionaryLookupFhDP(Form form) {
        super(form);
    }

    @Override
    public void init(){
        log.debug("Init...");
        super.init();
        try {
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
                    currentSourceObject = selectedBindingResult.getValue();
                    if (currentSourceObject != null) {
                        if (displayOnlyCode) {
                            setRawValue(dataProvider.getSrcKey(currentSourceObject));
                        } else {
                            setRawValue(dataProvider.getDisplayValue(currentSourceObject));
                        }
                    }
                }
            }
            //Tymczasowe
            search(false, true);
        } catch (Exception ex) {
            FhLogger.warn("DictionaryCombo: Provider not found.", ex);
        }
    }

    private Object getValueFromProvider(String code) {
        List<Object> allParamsList = new LinkedList<>();
        allParamsList.add(code);
        allParamsList.addAll(this.getValuesFromDictionaryComboParameters(this.getValueParamsList));
        return ReflectionUtils.run(this.getValue, this.dataProvider, allParamsList.toArray());
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


    /**
     * Resolves dynamic methods
     */
    private void resolveMethods() {
        if (provider != null) {
            this.getValuesPaged = ReflectionUtils.findMatchingPublicMethod(this.dataProvider.getClass(), "getValuesPaged").get();
            List<Object> paramsList = new LinkedList<>();
            Integer paramsCount = this.getValuesPaged.getParameterCount();
            for (int idx = 0; idx < paramsCount; idx++) {
                Optional<Parameter> p = ReflectionUtils.getMethodParamAnnotation(this.getValuesPaged, idx, Parameter.class);
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
                Optional<Parameter> p = ReflectionUtils.getMethodParamAnnotation(this.getValue, idx, Parameter.class);
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
                Optional<Parameter> p = ReflectionUtils.getMethodParamAnnotation(this.getTitle, idx, Parameter.class);
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


    @Override
    protected boolean processValueBinding(ElementChanges elementChanges) {
        if (getModelBinding() != null) {
            BindingResult selectedBindingResult = getModelBinding().getBindingResult();
            if (selectedBindingResult != null) {
                Object value = selectedBindingResult.getValue();
                if("null".equals(value)) {
                    value = null;
                }
                if(currentSourceObject == null && value == null) return false;
//                System.out.println("processValueBinding... value: " + value);
                if((currentSourceObject == null) || !currentSourceObject.equals(value)) {
                    System.out.println("processValueBinding... change from: " + currentSourceObject + " to " + value);
                    currentSourceObject = value;
                    if (displayOnlyCode) {
                        System.out.println("processValueBinding. displayOnlyCode = true");
                        setRawValue((value == null)? null :  dataProvider.getSrcKey(currentSourceObject));
                    } else {
                        System.out.println("processValueBinding. displayOnlyCode = false");
                        if(value != null) {
                            setRawValue(dataProvider.getDisplayValue(currentSourceObject));
                            elementChanges.addChange(VALUE_FOR_CHANGED_BINDING_ATTR, (getRawValue() == null) ? "" : getRawValue());
                        } else {
                            setRawValue(null);
                        }
                    }
//                    if(!this.searchPerformed) {
//                        System.out.println("processValueBinding. New rawValue: " + ((rawValue == null) ? "real null" : rawValue));
//                        elementChanges.addChange(VALUE_FOR_CHANGED_BINDING_ATTR, (rawValue == null) ? "" : rawValue);
//                        System.out.println("After processValueBinding. dirty: " + dirty);
//                    }
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
            if(noResult == true){
                //Force model clean when there is no matching values found.
                this.updateBindingForValue(null, getModelBinding(), getModelBinding().getBindingExpression(), this.getOptionalFormatter());
                elementChange.addChange("searchRequested", getRawValue());
//                elementChange.addChange(VALUE_FOR_CHANGED_BINDING_ATTR, this.filterText);
                this.noResult = false;
            }
        }
//        if(currentLastValue != null) {
//            Object lastValue = elementChange.getChangedAttributes().get(ATTR_LAST_VALUE);
//            if(lastValue.equals(currentLastValue)) {
//                elementChange.getChangedAttributes().remove(ATTR_LAST_VALUE);
//            }
//        }
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
//        if(valueChange.hasAttributeChanged("text")){
//            String newValue = valueChange.getStringAttribute("text");
//            if("null".equals(newValue)) {
//                newValue = null;
//            }
//            String refValue = filterText;
//            System.out.println("updateModel... new value: " + ((newValue == null)?"real null":"string null"));
//            if(newValue == null) {
//                dirty = true;
//                rawValue = null;
//                filterText = null;
//                valueSelected = true;
//            } else {
//                if (!displayOnlyCode) {
//                    if (selectedItem != null) {
//                        refValue = dataProvider.getDisplayValue(selectedItem);
//                    } else {
//                        refValue = filterText;
//                    }
//                }
//                if (!newValue.equals(refValue)) {
//                    dirty = true;
//                    rawValue = newValue;
//                    System.out.println("updateModel... changed rawValue: " + rawValue);
//                    filterText = rawValue;
//                    boolean singleSearch = this.rawValue!=null && (!dirty || !getAvailability().equals(AccessibilityEnum.EDIT));
//                    search(singleSearch, true);
//                }
//            }
////            refreshView();
//        }
//        if(valueSelected) {
//            System.out.println("updateModel... value selected: " + rawValue);
//            valueSelected = false;
//            Object result = null;
//            if(filterText != null) {
//                selectedItem = getValueFromProvider(filterText);
//                result = dataProvider.getCode(selectedItem);
//
//                //Validate
//                ValidateInput input = new ValidateInput();
//                input.setId(this.getGuuid());
//                input.setCode(filterText);
//                dictionaryComboValidate(input);
//
//                dirty = false;
//            } else {
//                selectedItem = null;
//            }
//            this.updateBindingForValue(result, getModelBinding(), getModelBinding().getBindingExpression(), this.getOptionalFormatter());
//            if(!displayOnlyCode) {
//                if(selectedItem != null) {
//                    rawValue = dataProvider.getDisplayValue(selectedItem);
//                }
//            }
//        }
//        if(valueChange.hasAttributeChanged("dirty")){
//            dirty = valueChange.getBooleanAttribute("dirty");
//            System.out.println("updateModel. Changed dirty to: " + dirty);
//        }
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

    private void search(boolean singleSearch, boolean searchByCurrentValue) {
        pageable = PageRequest.of(0, pageSize);
        log.debug("New pageable page no: " + pageable.getPageNumber());
        List<Object> allParamsList = new LinkedList<>();
        if(searchByCurrentValue) {
            if (singleSearch) {
                allParamsList.add(dataProvider.getCode(currentSourceObject));
            } else {
                allParamsList.add(getRawValue());
                allParamsList.add(pageable);
            }
        } else {
            //TODO: AIS
//            allParamsList.add(getLastValue());
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
            if(pageModel.getPage().isEmpty()){
                noResult = true;
            }

        }
        searchPerformed = true;
//        updateView();
    }


    @JsonIgnore
    private boolean searchPerformed = false;




    @Override
    public void validate() {
        if (getModelBinding() != null && getModelBinding().getBindingResult() != null) {
            processValidationForThisComponent();
        }
    }

    @Override
    public void onSessionLanguageChange(String lang) {
        if(!displayOnlyCode) {
            setRawValue(dataProvider.getDisplayValue(currentSourceObject));
        }
        languageChanged = true;
    }

    @Override
    public BaseInputField createNewSameComponent() {
        return new DictionaryLookupFhDP(getForm());
    }



    private void resolveParameters() {
        if( this.getValuesParamsList.size() > 0){
            this.getValuesParamsList.forEach(dictionaryComboParameter -> {
                dictionaryComboParameter.resolveValue();
            });
        }
    }

}
