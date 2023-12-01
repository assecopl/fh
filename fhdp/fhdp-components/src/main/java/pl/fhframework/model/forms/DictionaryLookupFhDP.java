package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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

import java.io.IOException;
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
//public class DictionaryLookupFhDP extends BaseInputFieldWithKeySupport implements IGroupingComponent<DictionaryComboParameterFhDP>, I18nFormElement {
public class DictionaryLookupFhDP extends DictionaryLookup {
    public DictionaryLookupFhDP(Form form) {
        super(form);
    }

//    public static final String ATTR_DISPLAY_ONLY_CODE = "displayOnlyCode";
//    public static final String ATTR_POPUP_COLOR = "popupColor";
//    private static final String ATTR_ROWS = "rows";
//    private static final String ATTR_COLUMNS = "columns";
//    private static final String ATTR_PAGE = "page";
//
//    private static final String ATTR_TITLE = "title";
//    private static final String ATTR_PAGES_COUNT = "pagesCount";
//    private static final String VALUE_FOR_CHANGED_BINDING_ATTR = "valueFromChangedBinding";
//
////    @JsonIgnore
////    @Autowired
////    EventRegistry eventRegistry;
//
//    @JsonIgnore
//    @Getter
//    private PageModel pageModel;
//
//    @Getter
//    private Integer pageSize = 5;
//
//    @JsonIgnore
//    @Getter
//    private Pageable pageable = PageRequest.of(0, pageSize);
//
//    @Getter
//    private Boolean searchRequested = false;
//
//    @Getter Boolean languageChanged = false;
//
//    @Getter
//    private Integer page;
//
//    @Getter
//    private Integer pagesCount;
//
//    @Getter
//    private String valueFromChangedBinding;
//
//    @Getter
//    private List rows = new ArrayList();
//
//    @Getter
//    private List<NameValue> columns;
//
//    @Getter
//    private String title;
//
//    @Getter
//    private String guuid;
//
//    @Getter
//    @XMLProperty
//    private Boolean displayOnlyCode;
//
//    @Getter
//    private String popupColor;
//
//    @Getter
//    private boolean presentList;
//
//    @Getter
//    public class ListData{
//        private boolean visible = true;
//        private Map<String, Object> other = new HashMap<>();
//    }
//
//    @Getter
//    private ListData _listData = new ListData();
//
//    @JsonIgnore
//    @Getter
//    @Setter
//    @XMLProperty(required = true, value = ATTR_DISPLAY_ONLY_CODE) //Powiązanie bindingu z property
//    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = CONTENT)
//    @DocumentedComponentAttribute(boundable = true, value = "Display only code, there will be no method invoke from provider.")
//    private ModelBinding<Boolean> displayOnlyCodeModelBinding;
//
//    @JsonIgnore
//    @Getter
//    @Setter
//    @XMLProperty(required = true, value = ATTR_POPUP_COLOR)
//    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = CONTENT)
//    @DocumentedComponentAttribute(boundable = true, value = "Popup display color provided in hex.")
//    private ModelBinding<String> popupBackgroundColorBinding;
//
//    @Getter
//    @Setter
//    @XMLProperty
//    @JsonIgnore//Web control don't need this information
//    private String provider;//OK
//
//
//    @JsonIgnore
//    private IComboDataProviderFhDP dataProvider;//OK
//
//    @JsonIgnore
//    private List<DictionaryComboParameterFhDP> subcomponents = new LinkedList<>();
//
//    @JsonIgnore
//    private ComponentStateSaver componentStateSaver = new ComponentStateSaver();
//
//
//
//    @JsonIgnore
//    @Getter
//    private boolean processComponentChange = true;//TODO: ?
//
//    @JsonIgnore
//    @Getter
//    @Setter
//    @CompilationTraversable
//    private List<NonVisualFormElement> nonVisualSubcomponents = new ArrayList<>();
//
//    protected boolean multiselect = false;
//
//    @JsonIgnore
//    private Method getValuesPaged;//TODO: Do usunięcia
//    @JsonIgnore
//    private Method getValue;//TODO: Do usunięcia - wystarczy uchwyt do dataProvider
//    @JsonIgnore
//    protected Method getTitle;
//    @JsonIgnore
//    private List<DictionaryComboParameterFhDP> getValuesParamsList = new LinkedList<>();
//    @JsonIgnore
//    private List<DictionaryComboParameterFhDP> getValueParamsList = new LinkedList<>();
//    @JsonIgnore
//    private List<DictionaryComboParameterFhDP> getTitleParamsList = new LinkedList<>();
//
//    // Value of selected SRC instance from provider. RESULT is in value.
//    @JsonIgnore
//    private Object currentSourceObject = null;
////    TODO: for AIS
////    @JsonIgnore
////    private Object currentLastValue;
//
//
//    @Getter
//    private String language;
//
//    protected boolean noResult = false;
//
//    protected boolean dirty = false;
//
//
//    private Map<String, Object> _tm = new HashMap<>(); //Tymczasowa mapa, zastępująca mi zmienne - żeby nie musieć zbyt często restartować aplikacji
//    private <T> T _(String atrName){
//        return (T) _tm.getOrDefault(atrName, null);
//    }
//
//    private void _(String atrName, Object value){
//        _tm.put(atrName, value);
//    }
//
//    public DictionaryLookupFhDP(Form form) {
//        super(form);
//    }
//
//    //************************************************************************************************************
//    //************************************************************************************************************
//    //************************************************************************************************************
//    //************************************************************************************************************
//    @JsonIgnore
//    private Intention servicedIntention; //It allows smoothly join logic in updateModel and updateView without using dozens of properties
//
//    @Override
//    public void updateModel(ValueChange valueChange) {
//        this.servicedIntention = new Intention(valueChange);
//        if (this.servicedIntention.selectNewElement()) {
//            serviceSelectingNewElement(valueChange);
//        } else if (this.servicedIntention.leavingControl()) {
//            if (this.rows.size() == 1) {//TODO: To powinno być przeniesione do intencji
//                serviceSelectingNewElement(valueChange);
//            }
//        }
//    }
//
//    @Override
//    public ElementChanges updateView() {
//        ElementChanges elementChange = super.updateView();
//        if (this.servicedIntention != null) {
//            ValueChange valueChange = this.servicedIntention.valueChange;
//            if (this.servicedIntention.isLookingForMatchingElements()) {
//                serviceLookingForElements(valueChange, elementChange);
//            } else if (this.servicedIntention.changePage()) {
//                serviceChangedPage(valueChange, elementChange);
//            }
//        }
//
//        columns = dataProvider.getColumnDefinitions();//TODO: To powinno być ustawiane tylko przy init
//        elementChange.addChange(ATTR_COLUMNS, columns);
//        return elementChange;
//    }
//
//    private void serviceLookingForElements(ValueChange valueChange, ElementChanges elementChange) {
//        final String searchText = valueChange.getStringAttribute("text");
//        if (searchText != null && !searchText.isEmpty()) {
//            boolean searchByCode = searchText.length() > 1 && searchText.toUpperCase().equals(searchText);
//            if (searchByCode) {
//                final Method method = this.getValue;//TODO: Nie powinniśmy przechowywać metody we właściwościu "value". Pytanie dlaczego w ogóle używamy tutaj refleksji, skoro mamy interfejs.... Kontrolka zawsze będzie tak samo używać providera..... Na razie zostawiam aby nie wywracać stolika
//                Object foundObject = ReflectionUtils.run(method, this.dataProvider, searchText);
//                this.rows = (foundObject != null) ? Collections.singletonList(foundObject) : Collections.emptyList();
//                elementChange.addChange(ATTR_ROWS, rows);
//                System.out.println("Searching by code " + searchText + " and found " + (foundObject != null));
//            } else {
//                Pageable pageable = PageRequest.of(0, pageSize);
//                this.rows = getMatchingElements(searchText, pageable); //TODO: Tutaj powinien jeszcze zostać użyty konwerter aby dopuścić oprócz stringów obiekty bardziej złożone
//                elementChange.addChange(ATTR_ROWS, rows);
//                elementChange.addChange(ATTR_PAGE, pageable.getPageNumber());
//                elementChange.addChange(ATTR_PAGES_COUNT, pageable.getPageSize());
//                List<String> test = Arrays.asList("Ala", "Józek", "Tomek", "Basia");
//                elementChange.addChange("testowaKolekcja", test);
//                System.out.println("Searching by matching text " + searchText + " and found " + this.rows.size() + " elements.");
//            }
//        } else {
//            this.rows = Collections.emptyList();
//            elementChange.addChange(ATTR_ROWS, rows);
//        }
//    }
//
//
//    private void serviceSelectingNewElement(ValueChange valueChange) {
//        log.error("Select new element intention not implemented yet");
//        //this.
//    }
//
//    private void serviceUndoChanges(ValueChange valueChange) {
//        log.error("Undo intention not implemented yet");
//    }
//
//    private void serviceChangedPage(ValueChange valueChange, ElementChanges elementChange) {
//        log.error("Changing page intention not implemented yet");
//    }
//
//    @AllArgsConstructor
//    private static class Intention {
//        private final ValueChange valueChange;
//
//        public boolean isLookingForMatchingElements() {
//            return valueChange.hasAttributeChanged("text");
//        }
//
//
//        public boolean leavingControl() {
//            return valueChange.hasAttributeChanged("blur");
//        }
//
//        public boolean undoChanges() {
//            return false;
//        }
//
//        public boolean selectNewElement() {
//            return false;
//        }
//
//        public boolean changePage() {
//            return false;
//        }
//    }
//
//    private List<Object> getMatchingElements(String searchText, Pageable pageable) {
//        final Method method = this.getValuesPaged;//TODO: Nie powinniśmy przechowywać metody we właściwościu "value". Pytanie dlaczego w ogóle używamy tutaj refleksji, skoro mamy interfejs.... Kontrolka zawsze będzie tak samo używać providera..... Na razie zostawiam aby nie wywracać stolika
//        final Object responseObject = ReflectionUtils.run(method, this.dataProvider, searchText, pageable);
//        //Object responseObject = this.dataProvider.getValuesPaged(searchText, pageable);
//        if (responseObject == null) {
//            log.warn("Provider has returned null what has been converted into empty array!");
//            return Collections.emptyList();
//        } else if (responseObject instanceof PageModel) {
//            @SuppressWarnings("unchecked") final PageModel<Object> pm = (PageModel<Object>) responseObject;
//            pm.doRefresh(pageable);
//            Page<Object> p1 = pm.getPage();
//            return (p1 != null) ? p1.getContent() : Collections.emptyList();
//        } else {
//            throw new RuntimeException("Method " + method.getName() + " in provider " + this.dataProvider.getClass().getSimpleName() + " has returned not suspected type " + responseObject.getClass().getName());
//        }
//    }
//
//    @Override
//    public void init() {
//        initOLD();
//    }
//
//    //************************************************************************************************************
//    //************************************************************************************************************
//    //************************************************************************************************************
//    //************************************************************************************************************
//
//    //TODO: Trzeba stąd przenieść naprawdę potrzebne rzeczy do init
//    public void initOLD(){
//        System.out.println("Init...");
//        super.init();
//        try {
//            this.resolveDataProvider();
//            this.resolveMethods();
//            this.resolveParameters();
//
//            columns = dataProvider.getColumnDefinitions();
////            pageModel = new PageModel<NameValue>(pageable -> initPageModel(pageable));
//            List<Object> paramsList = new LinkedList<>();
//            paramsList.addAll(getValuesFromDictionaryComboParameters(this.getTitleParamsList));
//            title = (String) ReflectionUtils.run(this.getTitle, this.dataProvider, paramsList.toArray());
//            page = -25;
//            pagesCount = 0;
//            rows = new ArrayList();
//            language = SessionManager.getUserSession().getLanguage().toLanguageTag();
//
//            //TODO: check binding!!!
//            if(displayOnlyCode == null) displayOnlyCode = false;
//
////            if(displayOnlyCodeModelBinding != null && displayOnlyCodeModelBinding.getBindingResult() != null) {
////                displayOnlyCode = getDisplayOnlyCodeModelBinding().getBindingResult().getValue();
////            }
//            if(popupBackgroundColorBinding != null && popupBackgroundColorBinding.getBindingResult() != null){
//                popupColor = popupBackgroundColorBinding.getBindingResult().getValue();
//            }
//            if (getModelBinding() != null) {
//                BindingResult selectedBindingResult = getModelBinding().getBindingResult();
//                if (selectedBindingResult != null) {
//                    currentSourceObject = selectedBindingResult.getValue();
//
//                }
//            }
//        } catch (Exception ex) {
//            FhLogger.warn("DictionaryCombo: Provider not found.", ex);
//        }
//    }
//
//    //TODO: Do usunięcia - jest nowa metoda
//    public ElementChanges updateViewOLD() {
//        ElementChanges elementChange = super.updateView();
//        elementChange.addChange("dirty", dirty);
//        System.out.println("updateView... ElementChange: " + toJson(elementChange));
//        if(searchPerformed) {
//            searchPerformed = false;
//            elementChange.addChange(ATTR_ROWS, rows);
//            elementChange.addChange(ATTR_PAGE, page);
//            elementChange.addChange(ATTR_PAGES_COUNT, pagesCount);
//
//            columns = dataProvider.getColumnDefinitions();
//            elementChange.addChange(ATTR_COLUMNS, columns);
//            if(noResult == true){
//                //Force model clean when there is no matching values found.
//                this.updateBindingForValue(null, getModelBinding(), getModelBinding().getBindingExpression(), this.getOptionalFormatter());
//                elementChange.addChange("searchRequested", getRawValue());
////                elementChange.addChange(VALUE_FOR_CHANGED_BINDING_ATTR, this.filterText);
//                this.noResult = false;
//            }
//        }
////        if(currentLastValue != null) {
////            Object lastValue = elementChange.getChangedAttributes().get(ATTR_LAST_VALUE);
////            if(lastValue.equals(currentLastValue)) {
////                elementChange.getChangedAttributes().remove(ATTR_LAST_VALUE);
////            }
////        }
//        if(languageChanged == true){
//            List<Object> paramsList = new LinkedList<>();
//            paramsList.addAll(getValuesFromDictionaryComboParameters(this.getTitleParamsList));
//            title = (String) ReflectionUtils.run(this.getTitle, this.dataProvider, paramsList.toArray());
//            elementChange.addChange(ATTR_TITLE, title);
//            this.languageChanged = false;
//        }
//
//        return elementChange;
//    }
//
//    //TODO: Może zostać - wygląda na jakąś metodę przydatną do logowania
//    public static String toJson(Object bean) {
//        ObjectMapper mapper = new ObjectMapper();
//        try {
//            return mapper.writeValueAsString(bean);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//
//
//    //TODO: Do usunięcia
//    public void updateModelOLD(ValueChange valueChange){
//        String json = toJson(valueChange);
//        System.out.println("updateModel: " + json);
//        if(valueChange.hasAttributeChanged("text")) {
//            currentSourceObject = valueChange.getStringAttribute("text");
//            log.info("Wpisany filtr: {}", currentSourceObject);
//            dirty = true;
//            search(false, true, (String)currentSourceObject);
//        }
//        if(valueChange.hasAttributeChanged("blur")) {
//            String newValue = valueChange.getStringAttribute("blur");
//            if ("null".equals(newValue)) {
//                newValue = null;
//            }
//            if(newValue != null && newValue.length() > 1) {
//                Object result = getValueFromProvider(dataProvider.initResultFromKey(newValue));
//                if(result != null) {
//                    //Found the result by code. Finished.
//                    currentSourceObject = result;
//                    dirty = false;
//                    Object code = dataProvider.getCode(currentSourceObject);
//                    updateBindingForValue(code, getModelBinding(), code);
//                } else {
//                    Object value = getValueFromBinding();
//                    currentSourceObject = getValueFromProvider(value);
//                    dirty = false;
//                }
//            }
//        }
//    }
//
//
//
//    //TODO: Do usunięcia
//    private Object getValueFromBinding() {
//        BindingResult selectedBindingResult = getModelBinding().getBindingResult();
//        if (selectedBindingResult != null) {
//            return selectedBindingResult.getValue();
//        }
//        return null;
//    }
//
//    //TODO: To trzeba przepatrzeć. Bo ten obiekt currentSourceObject nie jest używany
//    @Override
//    protected boolean processValueBinding(ElementChanges elementChanges) {
//        if (!dirty && getModelBinding() != null) {
//            BindingResult selectedBindingResult = getModelBinding().getBindingResult();
//            if (selectedBindingResult != null) {
//                Object value = selectedBindingResult.getValue();
//                if("null".equals(value)) {
//                    value = null;
//                }
//                if(currentSourceObject == null && value == null) {
//                    return false;
//                }
//                if(value != null && currentSourceObject == null) {
//                    currentSourceObject = value;
//                }
////                System.out.println("processValueBinding... value: " + value);
//                if(currentSourceObject != null) {
//                    System.out.println("processValueBinding... change from: " + currentSourceObject + " to " + value);
////                    currentSourceObject = value;
//                    if (displayOnlyCode) {
//                        System.out.println("processValueBinding. displayOnlyCode = true");
//                        setRawValue((value == null)? null :  dataProvider.getSrcKey(currentSourceObject));
//                    } else {
//                        System.out.println("processValueBinding. displayOnlyCode = false");
//                        if(value != null) {
//                            setRawValue(dataProvider.getDisplayValue(currentSourceObject));
//                            elementChanges.addChange(RAW_VALUE_ATTR, (getRawValue() == null) ? "" : getRawValue());
//                        } else {
//                            setRawValue(null);
//                        }
//                    }
////                    if(!this.searchPerformed) {
////                        System.out.println("processValueBinding. New rawValue: " + ((rawValue == null) ? "real null" : rawValue));
////                        elementChanges.addChange(VALUE_FOR_CHANGED_BINDING_ATTR, (rawValue == null) ? "" : rawValue);
////                        System.out.println("After processValueBinding. dirty: " + dirty);
////                    }
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
//
//    //TODO: Do usunięcia
//    private Object getValueFromProvider(Object code) {
//        List<Object> allParamsList = new LinkedList<>();
//        allParamsList.add(code);
//        allParamsList.addAll(this.getValuesFromDictionaryComboParameters(this.getValueParamsList));
//        return ReflectionUtils.run(this.getValue, this.dataProvider, allParamsList.toArray());
//    }
//
//    //TODO: Wygląda na bardzo ambitne - pytanie czy to wogóle jest wykorzystywane - jeśli tak to trzeba to uwzględnić w metodach odwołujących się do provider. Dubluje się z getSubComponents
//    @JsonIgnore
//    public List<DictionaryComboParameterFhDP> getParameters() {
//        return subcomponents;
//    }
//
//
//    //TODO: OK
//    /**
//     * Resolve data provider bean
//     * @throws ClassNotFoundException
//     */
//    private void resolveDataProvider() throws ClassNotFoundException {
//        if (this.provider != null) {
//            Class<? extends IComboDataProviderFhDP> providerClass = (Class<? extends IComboDataProviderFhDP>) FhCL.classLoader.loadClass(String.format(this.provider));
//            this.dataProvider = AutowireHelper.getBean(providerClass);
//        } else {
//            log.error("DictionaryComboFhDP has empty provider for " +this.getId());
//        }
//    }
//
//
//    //TODO:Obecnie potrzebne do prawidłowego zainicjowania ale ta logika to koszmar. Przejście na interfejsy zdecydowanie rozwiązałoby problem. Przecież nawet jesli chcemy zachować tę funkcjonalność dostarczania swoich parametrów to zawsze możemy je dostarczyć w formie listy jako dodatkowy parametr dla providera a nawet jeśli nie możemy zmienić providera to możemy go zwrapować na zewnątrz. Wtedy pozbędziemy się tego koszarka a dodatkowo zyskamy lepsze tracebility kodu i weryfikację kodu na etapie kompilacji
//    /**
//     * Resolves dynamic methods
//     */
//    private void resolveMethods() {
//        if (provider != null) {
//            this.getValuesPaged = ReflectionUtils.findMatchingPublicMethod(this.dataProvider.getClass(), "getValuesPaged").get();
//            List<Object> paramsList = new LinkedList<>();
//            Integer paramsCount = this.getValuesPaged.getParameterCount();
//            for (int idx = 0; idx < paramsCount; idx++) {
//                Optional<Parameter> p = ReflectionUtils.getMethodParamAnnotation(this.getValuesPaged, idx, Parameter.class);
//                if (p.isPresent()) {
//                    String paramName = p.get().name();
//                    Optional<DictionaryComboParameterFhDP> optionalDictComboParam = subcomponents.stream().filter(e -> Objects.equals(e.getName(), paramName)).findFirst();
//                    if (optionalDictComboParam.isPresent()) {
//                        DictionaryComboParameterFhDP dictComboParam = optionalDictComboParam.get();
//                        this.getValuesParamsList.add(dictComboParam);
//                    } else {
//                        throw new FhException("No attribute for " + DictionaryComboParameterFhDP.class.getSimpleName() + " : " + paramName);
//                    }
//                }
//            }
//
//            this.getValue = ReflectionUtils.findMatchingPublicMethod(this.dataProvider.getClass(), "getValue").get();
//            Integer paramsCount2 = this.getValue.getParameterCount();
//            for (int idx = 0; idx < paramsCount2; idx++) {
//                Optional<Parameter> p = ReflectionUtils.getMethodParamAnnotation(this.getValue, idx, Parameter.class);
//                if (p.isPresent()) {
//                    String paramName = p.get().name();
//                    Optional<DictionaryComboParameterFhDP> optionalDictComboParam = subcomponents.stream().filter(e -> Objects.equals(e.getName(), paramName)).findFirst();
//                    if (optionalDictComboParam.isPresent()) {
//                        DictionaryComboParameterFhDP dictComboParam = optionalDictComboParam.get();
//                        this.getValueParamsList.add(dictComboParam);
//                        BindingResult br = dictComboParam.getModelBinding().getBindingResult();
//                    } else {
//                        throw new FhException("No attribute for " + DictionaryComboParameterFhDP.class.getSimpleName() + " : " + paramName);
//                    }
//                }
//            }
//            this.getTitle = ReflectionUtils.findMatchingPublicMethod(this.dataProvider.getClass(), "getTitle").get();
//            Integer paramsCount3 = this.getTitle.getParameterCount();
//            for (int idx = 0; idx < paramsCount3; idx++) {
//                Optional<Parameter> p = ReflectionUtils.getMethodParamAnnotation(this.getTitle, idx, Parameter.class);
//                if (p.isPresent()) {
//                    String paramName = p.get().name();
//                    Optional<DictionaryComboParameterFhDP> optionalDictComboParam = subcomponents.stream().filter(e -> Objects.equals(e.getName(), paramName)).findFirst();
//                    if (optionalDictComboParam.isPresent()) {
//                        DictionaryComboParameterFhDP dictComboParam = optionalDictComboParam.get();
//                        this.getTitleParamsList.add(dictComboParam);
////                        BindingResult br = dictComboParam.getModelBinding().getBindingResult();
//                    } else {
//                        throw new FhException("No attribute for " + DictionaryComboParameterFhDP.class.getSimpleName() + " : " + paramName);
//                    }
//                }
//            }
//
//        }
//    }
//
//
//    //TODO: Do przeniesienia
//    /**
//     * Adds component to the container.
//     */
//    @Override
//    public void addSubcomponent(DictionaryComboParameterFhDP component) {
//        subcomponents.add(component);
//    }
//
//    //TODO: Pytanie do czego tego używamy??
//    @Override
//    public void removeSubcomponent(DictionaryComboParameterFhDP removedFormElement) {
//        subcomponents.remove(removedFormElement);
//    }
//
//    //TODO: Dubluje się z getParameters()!
//    @Override
//    public List<DictionaryComboParameterFhDP> getSubcomponents() {
//        return subcomponents;
//    }
//
//    //TODO: Do usunięcia - bo nie używane
//    protected String objectToString(Object s) {
//        if (s == null) return "";
//
//        if (s.getClass().equals(String.class)) {
//            return (String) s;
//        } else if(s.getClass().equals(Boolean.class)) {
//            return s.toString();
//        } else {
//            return this.dataProvider.getDisplayValue(s);
//        }
//    }
//
//
//    //TODO: Jeśli faktycznie potrzebujemy możliwości dodawania custowowych parametrów dla providera to musi zostać takiej jak jest.
//    /**
//     * Function for geting actual values from DictionaryComboParameter based on its model bindings.
//     *
//     * @param dcp
//     * @return List<Object>
//     */
//    List<Object> getValuesFromDictionaryComboParameters(List<DictionaryComboParameterFhDP> dcp) {
//        List<Object> l = new LinkedList<>();
//        dcp.forEach(dictionaryComboParameterFhDP -> {
//            BindingResult br = dictionaryComboParameterFhDP.getModelBinding().getBindingResult();
//            if (br != null) {
//                l.add(br.getValue());
//            } else {
//                throw new FhException("No attribute for " + DictionaryComboParameterFhDP.class.getSimpleName() + " : " + dictionaryComboParameterFhDP.getName());
//            }
//        });
//
//        return l;
//    }
//
//    //TODO: Do usunięca - obsłużone w inny sposób
//    private void updateRows(Pageable pageable) {
//        pageModel.doRefresh(pageable);
//        pagesCount = pageModel.getPage().getTotalPages();
//        page = pageModel.getPage().getNumber();
//        log.debug("updateRows. page: " + page);
//        rows.clear();
//        rows.addAll(pageModel.getPage().getContent());
//
//    }
//
////    private void changePage(Integer newPage, String valueToSearch) {
////        changePage(newPage, valueToSearch, false);
////    }
//
//    //TODO: Do usunięca - obsłużone w inny sposób
//    private void search(boolean singleSearch, boolean searchByCurrentValue) {
//        pageable = PageRequest.of(0, pageSize);
//        log.debug("New pageable page no: " + pageable.getPageNumber());
//        List<Object> allParamsList = new LinkedList<>();
//        if(searchByCurrentValue) {
//            if (singleSearch) {
//                allParamsList.add(dataProvider.getCode(currentSourceObject));
//            } else {
//                //allParamsList.add(getRawValue());
//                String providedText = (String)currentSourceObject; //getRawValue()
//                allParamsList.add(providedText);
//                allParamsList.add(pageable);
//            }
//        } else {
//            //TODO: AIS
////            allParamsList.add(getLastValue());
//        }
//        allParamsList.addAll(this.getValuesFromDictionaryComboParameters(this.getValuesParamsList));
//        if (singleSearch) {
//            Object element = ReflectionUtils.run(this.getValue, this.dataProvider, allParamsList.toArray());
//            rows.clear();
//            rows.add(element);
//            page = 0;
//            pagesCount = 1;
//        } else {
//            pageModel = (PageModel) ReflectionUtils.run(this.getValuesPaged, this.dataProvider, allParamsList.toArray());
//            updateRows(pageable);
//            if(pageModel.getPage().isEmpty()){
//                noResult = true;
//            }
//
//        }
//        searchPerformed = true;
////        updateView();
//    }
//
//    //TODO: Moja pierwotna wariacha dla search.Do usunięca - obsłużone w inny sposób
//    private void search(boolean singleSearch, boolean searchByCurrentValue, String textValue) {
//        pageable = PageRequest.of(0, pageSize);
//        log.debug("New pageable page no: " + pageable.getPageNumber());
//        List<Object> allParamsList = new LinkedList<>();
//        if(searchByCurrentValue) {
//            if (singleSearch) {
//                allParamsList.add(dataProvider.getCode(currentSourceObject));
//            } else {
//                //allParamsList.add(getRawValue());
//                String providedText = textValue; //getRawValue()
//                allParamsList.add(providedText);
//                allParamsList.add(pageable);
//            }
//        } else {
//            //TODO: AIS
////            allParamsList.add(getLastValue());
//        }
//        allParamsList.addAll(this.getValuesFromDictionaryComboParameters(this.getValuesParamsList));
//        if (singleSearch) {
//            Object element = ReflectionUtils.run(this.getValue, this.dataProvider, allParamsList.toArray());
//            rows.clear();
//            rows.add(element);
//            page = 0;
//            pagesCount = 1;
//        } else {
//            pageModel = (PageModel) ReflectionUtils.run(this.getValuesPaged, this.dataProvider, allParamsList.toArray());
//            updateRows(pageable);
//            if(pageModel.getPage().isEmpty()){
//                noResult = true;
//            }
//
//        }
//        searchPerformed = true;
////        updateView();
//    }
//
//
//    //TODO: Do usunięca - w zasadzie wszystkie dane zwiazane z intencją mam w "servicedIntatnion"
//    @JsonIgnore
//    private boolean searchPerformed = false;
//
//
//
//
//    //TODO: Do weryfikacji czy konieczne
//    @Override
//    public void validate() {
//        if (getModelBinding() != null && getModelBinding().getBindingResult() != null) {
//            processValidationForThisComponent();
//        }
//    }
//
//    //TODO:Musi zostać acz do przepatrzenia czy jest kompatybilne z obecnym rozwiązaniem
//    @Override
//    public void onSessionLanguageChange(String lang) {
//        if(!displayOnlyCode) {
//            setRawValue(dataProvider.getDisplayValue(currentSourceObject));
//        }
//        languageChanged = true;
//    }
//
//    //TODO:Musi zostać acz do przepatrzenia czy jest kompatybilne z obecnym rozwiązaniem
//    @Override
//    public BaseInputField createNewSameComponent() {
//        return new DictionaryLookupFhDP(getForm());
//    }
//
//
//    //TODO: Trzeba to zderzyć z getValuesFromDictionaryComboParameters, bo wygląda na mocno powiązane. Pytanie po co w ogóle taka "inicjalizacja"? Nawet nigdzie nie używamy wartości zwracanych przez dictionaryComboParameter.resolveValue();
//    private void resolveParameters() {
//        if( this.getValuesParamsList.size() > 0){
//            this.getValuesParamsList.forEach(dictionaryComboParameter -> {
//                dictionaryComboParameter.resolveValue();
//            });
//        }
//    }

}
