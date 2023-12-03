package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import pl.fhframework.annotations.Control;
import pl.fhframework.annotations.DocumentedComponent;
import pl.fhframework.annotations.XMLProperty;
import pl.fhframework.core.FhCL;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.events.I18nFormElement;
import pl.fhframework.helper.AutowireHelper;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.dto.ValueChange;
import pl.fhframework.model.forms.provider.DictionaryLookupLegacyProvider;
import pl.fhframework.model.forms.provider.IComboDataProviderFhDP;
import pl.fhframework.model.forms.provider.IDictionaryLookupProvider;
import pl.fhframework.model.forms.provider.NameValue;

import java.util.*;

@Slf4j
@DocumentedComponent(value = "Enables users to quickly find and select from a pre-populated list of values as they type, leveraging searching and filtering.",
        icon = "fa fa-outdent")
@Control(parents = {PanelGroup.class, Group.class, Column.class, Tab.class, Row.class, Form.class, Repeater.class}, invalidParents = {Table.class}, canBeDesigned = false)
public class DictionaryLookup extends BaseInputFieldWithKeySupport implements IGroupingComponent<DictionaryComboParameterFhDP>, I18nFormElement {
    private static final String ATTR_COLUMNS = "columns";
    private static final String ATTR_ROWS = "rows";
    private static final String ATTR_PAGE = "page";
    private static final String ATTR_PAGES_COUNT = "pagesCount";


    @Getter
    @Setter
    @XMLProperty
    private Integer pageSize = 5;

    @Getter
    @Setter
    @XMLProperty
    @JsonIgnore//Web control don't need this information
    private String provider;

    @Getter
    private List<NameValue> columns;

    @Getter
    private Integer page;

    @JsonIgnore
    @Getter
    private final List<DictionaryComboParameterFhDP> subcomponents = new LinkedList<>();

    @JsonIgnore
    private IDictionaryLookupProvider<Object, Object> dictionaryLookupProvider;

    @JsonIgnore
    private Intention servicedIntention; //It allows smoothly join logic in updateModel and updateView without using dozens of properties

    @JsonIgnore
    private List<?> rows = null;

    @JsonIgnore
    private PageModel<Object> pageModel;

    public DictionaryLookup(Form<?> form) {
        super(form);
    }

    @Override
    public void init() {
        super.init();
        this.dictionaryLookupProvider = getDictionaryLookupProvider(provider, this.getId());
        this.page = 0;
    }


    private ValueChange commandValueData;

    @Override
    public void updateModel(ValueChange valueChange) {
        if (valueChange.hasAttributeChanged("command")) {
            this.commandValueData = valueChange;
            String command = valueChange.getStringAttribute("command");//Read command
            switch (command) {
                case "search":
                    serviceSearchCommand(valueChange);
                    break;
                case "changePage":
                    serviceChangePage(valueChange);
                    break;
                case "selectItem":
                    serviceOnSelectItem(valueChange);
                    break;
            }
        } else {
            //updateModelOLD(valueChange);
        }
    }

    private void serviceOnSelectItem(ValueChange valueChange) {
        final Integer selectedIndex = valueChange.getIntAttribute("select");
        final Object selectedDictionaryElement = pageModel.getPage().getContent().get(selectedIndex);
        this.getModelBinding().setValue(dictionaryLookupProvider.getModelValue(selectedDictionaryElement));
        this.setRawValue(dictionaryLookupProvider.getDisplayValue(selectedDictionaryElement));
        log.warn("Selected item {}", getRawValue());
    }

    private void serviceSearchCommand(ValueChange valueChange) {
        final String searchText = valueChange.getStringAttribute("text");
        if (searchText != null && !searchText.isEmpty()) {
            Pageable pageable = PageRequest.of(page, pageSize);
            this.pageModel = dictionaryLookupProvider.getDictionaryElementsPaged(searchText, pageable, this::getParameterValue);
            this.pageModel.doRefresh(pageable);
//            if (this.pageModel.getPage() != null) {
//                log.debug("Searching by matching text " + searchText + " and found " + rows.size() + " elements.");
//            } else {
//                log.warn("Unsuccessful searching by matchin text '" + searchText + "'!");
//            }
        } else {

        }
    }

    private void serviceChangePage(ValueChange valueChange) {
        String direction = valueChange.getStringAttribute("pageChange");
        Pageable pageable;
        if ("next".equals(direction)) {
            pageable = pageModel.getPage().nextOrLastPageable();
        } else if ("previous".equals(direction)) {
            pageable = pageModel.getPage().previousOrFirstPageable();
        } else {
            throw new RuntimeException("Unknown page change direction '" + direction + "'!");
        }
        this.pageModel.doRefresh(pageable);
        this.rows = this.pageModel.getPage().getContent();
    }

    @Override
    public ElementChanges updateView() {
        final String rawValueToShow = getRawValue();
        ElementChanges elementChange = super.updateView();
        if (this.commandValueData != null) {
            switch (this.commandValueData.getStringAttribute("command")) {
                case "selectItem":
                    elementChange.addChange(RAW_VALUE_ATTR, rawValueToShow);
                    elementChange.addChange(ATTR_ROWS, Collections.emptyList());
                    elementChange.addChange(ATTR_PAGE, null);
                    elementChange.addChange(ATTR_PAGES_COUNT, null);
                    break;

                case "search":
                case "changePage":
                    elementChange.addChange(ATTR_ROWS, pageModel.getPage().getContent());
                    elementChange.addChange(ATTR_PAGE, pageModel.getPage().getNumber()+1);
                    elementChange.addChange(ATTR_PAGES_COUNT, pageModel.getPage().getTotalPages());
                    if (columns == null) {
                        this.columns = dictionaryLookupProvider.getColumnDefinitions();
                        elementChange.addChange(ATTR_COLUMNS, columns);
                    }
                    break;
            }
        }else{
            FhLogger.warn("Ustaiwanie orgRawValue {}", getRawValue());
            elementChange.addChange("orgRawValue", getRawValue());
        }
        return elementChange;
    }

    public ElementChanges updateViewOLD() {
        ElementChanges elementChange = super.updateView();

        if (this.servicedIntention != null) {
            ValueChange valueChange = this.servicedIntention.valueChange;
            if (this.servicedIntention.isSelectingNewElement()) {
                elementChange.addChange(RAW_VALUE_ATTR, getRawValue());
                elementChange.addChange("tableVisibility", false);
                //elementChange.addChange(RAW_VALUE_ATTR, dictionaryLookupProvider.getDisplayValue(this.servicedIntention.selectedModelObject));
                //elementChange.addChange("displayedValue", dictionaryLookupProvider.getDisplayValue(this.servicedIntention.selectedModelObject));
            } else if (this.servicedIntention.isLeavingControl()) {
                elementChange.addChange(RAW_VALUE_ATTR, getRawValue());
                elementChange.addChange("tableVisibility", false);
                //elementChange.addChange(RAW_VALUE_ATTR, dictionaryLookupProvider.getDisplayValue(this.servicedIntention.selectedModelObject));
                //elementChange.addChange("displayedValue", dictionaryLookupProvider.getDisplayValue(this.servicedIntention.selectedModelObject));
            } else if (this.servicedIntention.isLookingForMatchingElements()) {
                serviceLookingForElements(valueChange, elementChange);
            } else if (this.servicedIntention.isChangingPage()) {
                serviceChangedPage(valueChange, elementChange);
            }
        }

        //Aktualizacja listy kolumn TODO: Rozważyć, czy to się nie dzieje tylko w fazie inicjalizacji
        if (columns == null) {
            this.columns = dictionaryLookupProvider.getColumnDefinitions();
            elementChange.addChange(ATTR_COLUMNS, columns);
        }

        return elementChange;
    }

    //************************************************************************************************************
    //************************************************************************************************************
    //************************************************************************************************************
    //************************************************************************************************************

    private void serviceLookingForElements(ValueChange valueChange, ElementChanges elementChange) {
        final String searchText = valueChange.getStringAttribute("text");
        if (searchText != null && !searchText.isEmpty()) {
            boolean searchByCode = searchText.length() > 1 && searchText.toUpperCase().equals(searchText);
            if (searchByCode) {
                Object foundObject = dictionaryLookupProvider.getElementByModelValue(searchText, this::getParameterValue);
                List<?> rows = (foundObject != null) ? Collections.singletonList(foundObject) : Collections.emptyList();
                elementChange.addChange(ATTR_ROWS, rows);
                FhLogger.info("Searching by code " + searchText + " and found " + (foundObject != null));
            } else {
                Pageable pageable = PageRequest.of(page, pageSize);
                //this.rows = getMatchingElements(searchText, pageable); //TODO: Tutaj powinien jeszcze zostać użyty konwerter aby dopuścić oprócz stringów obiekty bardziej złożone
                //List<?> rows = getMatchingElements(searchText, pageable); //TODO: Tutaj powinien jeszcze zostać użyty konwerter aby dopuścić oprócz stringów obiekty bardziej złożone
                this.pageModel = dictionaryLookupProvider.getDictionaryElementsPaged(searchText, pageable, this::getParameterValue);
                this.pageModel.doRefresh(pageable);
                if (this.pageModel.getPage() != null) {
                    this.rows = this.pageModel.getPage().getContent();
                    elementChange.addChange(ATTR_ROWS, rows);
                    elementChange.addChange(ATTR_PAGE, pageModel.getPage().getNumber());
                    elementChange.addChange(ATTR_PAGES_COUNT, pageModel.getPage().getTotalPages());
                    List<String> test = Arrays.asList("Ala", "Józek", "Tomek", "Basia");
                    elementChange.addChange("testowaKolekcja", test);
                    log.debug("Searching by matching text " + searchText + " and found " + rows.size() + " elements.");
                } else {
                    log.warn("Unsuccessful searching by matchin text '" + searchText + "'!");
                }
            }
        } else {
            elementChange.addChange(ATTR_ROWS, Collections.emptyList());
        }
        elementChange.addChange("tableIsVisible", true);
    }


    private void serviceSelectingNewElement(ValueChange valueChange) {


    }

    private void serviceUndoChanges(ValueChange valueChange) {
        log.error("Undo intention not implemented yet");
    }

    private void serviceChangedPage(ValueChange valueChange, ElementChanges elementChange) {
        String direction = valueChange.getStringAttribute("pageChange");
        Pageable pageable;
        if ("next".equals(direction)) {
            pageable = pageModel.getPage().nextOrLastPageable();
        } else if ("next".equals(direction)) {
            pageable = pageModel.getPage().previousOrFirstPageable();
        } else {
            throw new RuntimeException("Unknown page change direction '" + direction + "'!");
        }
        this.pageModel.doRefresh(pageable);
        this.rows = this.pageModel.getPage().getContent();
        elementChange.addChange(ATTR_ROWS, rows);
        elementChange.addChange(ATTR_PAGE, pageModel.getPage().getNumber());
    }

    @Getter
    private static class Intention {
        private final ValueChange valueChange;
        private boolean lookingForMatchingElements;
        private boolean leavingControl;
        @Setter
        private boolean selectingNewElement;
        private boolean isChangingPage;
        private Object selectedModelObject;

        public Intention(ValueChange valueChange) {
            this.valueChange = valueChange;
            this.lookingForMatchingElements = valueChange.hasAttributeChanged("text");
            this.leavingControl = valueChange.hasAttributeChanged("blur");
            this.selectingNewElement = valueChange.hasAttributeChanged("select");
            this.isChangingPage = valueChange.hasAttributeChanged("page");
        }
    }

//    private List<Object> getMatchingElementsOLD(String searchText, Pageable pageable) {
//        final PageModel<Object> pm = dictionaryLookupProvider.getDictionaryElementsPaged(searchText, pageable, this::getParameterValue);
//        if (pm == null) {
//            FhLogger.warn("Provider has returned null what has been converted into empty array!");
//            return Collections.emptyList();
//        } else {
//            pm.doRefresh(pageable);
//            Page<Object> p1 = pm.getPage();
//            return (p1 != null) ? p1.getContent() : Collections.emptyList();
//        }
//    }
//
//    private PageModel<Object> getMatchingElements(String searchText, Pageable pageable) {
//        final PageModel<Object> pm = dictionaryLookupProvider.getDictionaryElementsPaged(searchText, pageable, this::getParameterValue);
//        if (pm == null) {
//            FhLogger.warn("Provider has returned null what has been converted into empty array!");
//            return Collections.emptyList();
//        } else {
//            pm.doRefresh(pageable);
//            Page<Object> p1 = pm.getPage();
//            return (p1 != null) ? p1.getContent() : Collections.emptyList();
//        }
//    }


    private Object getParameterValue(String attributeName) {
        return subcomponents.stream()
                .filter(param -> param.getName().equals(attributeName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("You don't provide required parameter '" + attributeName + "' in DictionaryLookup '" + this.getId() + " on form '" + this.getForm().getId() + "'!"))
                .resolveValue();
    }


    private static IDictionaryLookupProvider<Object, Object> getDictionaryLookupProvider(String providerClassName, String componentId) {
        if (providerClassName != null) {
            try {
                Class<?> foundProviderClass = FhCL.classLoader.loadClass(String.format(providerClassName));
                if (IDictionaryLookupProvider.class.isAssignableFrom(foundProviderClass)) {
                    @SuppressWarnings("unchecked")
                    Class<? extends IDictionaryLookupProvider<Object, Object>> providerClass = (Class<? extends IDictionaryLookupProvider<Object, Object>>) foundProviderClass;
                    return AutowireHelper.getBean(providerClass);
                } else if (IComboDataProviderFhDP.class.isAssignableFrom(foundProviderClass)) {
                    @SuppressWarnings("unchecked")
                    Class<? extends IComboDataProviderFhDP<?, ?>> wrappedProviderClass = (Class<? extends IComboDataProviderFhDP<?, ?>>) foundProviderClass;
                    @SuppressWarnings("unchecked")
                    IComboDataProviderFhDP<Object, Object> wrappedProvider = (IComboDataProviderFhDP<Object, Object>) AutowireHelper.getBean(wrappedProviderClass);
                    return new DictionaryLookupLegacyProvider<Object, Object>(wrappedProvider);
                } else {
                    throw new RuntimeException("Not supported provider type '" + foundProviderClass.getName() + "'!");
                }
            } catch (ClassNotFoundException ex) {
                FhLogger.error("DictionaryCombo: Provider not found.", ex);
                throw new RuntimeException("DictionaryCombo: Provider not found.", ex);
            }
        } else {
            throw new RuntimeException("DictionaryComboFhDP has empty provider for " + componentId);
        }
    }


    //************************************************************************************************************
    //************************************************************************************************************
    //************************************************************************************************************
    //************************************************************************************************************

    @Override
    public DictionaryLookup createNewSameComponent() {
        return null;
    }

    @Override
    public void onSessionLanguageChange(String lang) {

    }

    @Override
    public void addSubcomponent(DictionaryComboParameterFhDP formElement) {
        subcomponents.add(formElement);
    }

    @Override
    public void removeSubcomponent(DictionaryComboParameterFhDP removedFormElement) {
        subcomponents.remove(removedFormElement);
    }

    @Override
    public List<NonVisualFormElement> getNonVisualSubcomponents() {
        return Collections.emptyList();
    }


    //************************************************************************************************************
    //************************************************************************************************************
    //************************************************************************************************************
    //************************************************************************************************************


    public void updateModelOLD(ValueChange valueChange) {
        log.info("Text:{}, blur:{}, select:{}, page:{}", valueChange.hasAttributeChanged("text"), valueChange.hasAttributeChanged("blur"), valueChange.hasAttributeChanged("select"), valueChange.hasAttributeChanged("page"));
        this.servicedIntention = new Intention(valueChange);
        Object selectedDictionaryElement;
        if (valueChange.hasAttributeChanged("page")) {
            selectedDictionaryElement = null;
            this.page = valueChange.getIntAttribute("page");
        } else if (this.servicedIntention.isSelectingNewElement()) {
            final Integer selectedIndex = valueChange.getIntAttribute("select");
            selectedDictionaryElement = rows.get(selectedIndex);
            this.servicedIntention.selectedModelObject = selectedDictionaryElement;
            this.setRawValue(dictionaryLookupProvider.getDisplayValue(this.servicedIntention.selectedModelObject));
            serviceSelectingNewElement(valueChange);
        } else if (this.servicedIntention.isLeavingControl()) {
            final String searchText = valueChange.getStringAttribute("blur");
            //selectedDictionaryElement = dictionaryLookupProvider.getElementByModelValue(searchText, this::getParameterValue);
            selectedDictionaryElement = null;
            if (selectedDictionaryElement == null) {
//                Pageable pageable = PageRequest.of(0, pageSize);
//                List<Object> rows = getMatchingElements(searchText, pageable);
                if (rows != null && rows.size() == 1) {
                    selectedDictionaryElement = rows.get(0);
                    this.servicedIntention.selectedModelObject = selectedDictionaryElement;
                    this.setRawValue(dictionaryLookupProvider.getDisplayValue(this.servicedIntention.selectedModelObject));
                }
            }
        } else {
            selectedDictionaryElement = null;
        }

        if (selectedDictionaryElement != null) {
            //this.setRawValue(dictionaryLookupProvider.getDisplayValue(selectedDictionaryElement));
            this.getModelBinding().setValue(dictionaryLookupProvider.getModelValue(selectedDictionaryElement)); //TODO: Tutaj jest bug zwiazany z komatybilnością wstecz - do modelu zamiast obiektu wpisujemy string z kodem
            this.servicedIntention.setSelectingNewElement(true);
        }

//        final String searchText = valueChange.getStringAttribute("text");
//        Object foundObject = dictionaryLookupProvider.getElementById(searchText);
//        if (foundObject != null) {//TODO: To powinno być przeniesione do intencji
//            //serviceSelectingNewElement(valueChange);
//            dictionaryLookupProvider.getDisplayValueByElement(foundObject);
//            this.setRawValue(searchText);
//            this.getModelBinding().setValue(dictionaryLookupProvider.getElementId(foundObject));
//            this.servicedIntention.setSelectingNewElement(true);
//        }else{
//            Pageable pageable = PageRequest.of(0, pageSize);
//            List<Object> rows = getMatchingElements(searchText, pageable);
//            if (rows.size()==1){
//                foundObject = rows.get(0);
//                this.setRawValue(searchText);
//                this.getModelBinding().setValue(dictionaryLookupProvider.getElementId(foundObject));
//                this.servicedIntention.setSelectingNewElement(true);
//            }
//        }
    }
}
