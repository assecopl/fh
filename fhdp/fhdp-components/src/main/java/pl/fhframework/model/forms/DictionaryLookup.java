package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import pl.fhframework.annotations.CompilationTraversable;
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
    @JsonIgnore//Web control don't need this information
    private String provider;

    @Getter
    @Setter
    @XMLProperty
    private Integer pageSize = 5;

    @Getter
    private List<NameValue> columns;


    @JsonIgnore
    @Getter
    @CompilationTraversable
    private List<NonVisualFormElement> nonVisualSubcomponents = new ArrayList<>();

    @JsonIgnore
    @Getter
    private final List<DictionaryComboParameterFhDP> subcomponents = new LinkedList<>();//TODO: Usunąć - mamy tylko kontrolki nonVisualSubcomponents

    @JsonIgnore
    private IDictionaryLookupProvider<Object, Object> dictionaryLookupProvider;

    @JsonIgnore
    private Intention servicedIntention; //It allows smoothly join logic in updateModel and updateView without using dozens of properties

    @JsonIgnore
    private List<?> rows = null;

    public DictionaryLookup(Form<?> form) {
        super(form);
    }

    @Override
    public void init() {
        super.init();
        this.dictionaryLookupProvider = getDictionaryLookupProvider(provider, this.getId());
    }

    @Override
    public void updateModel(ValueChange valueChange) {
        this.servicedIntention = new Intention(valueChange);
        Object selectedDictionaryElement;
        if (this.servicedIntention.isSelectingNewElement()) {
            final Integer selectedIndex = valueChange.getIntAttribute("select");
            selectedDictionaryElement = rows.get(selectedIndex);
            serviceSelectingNewElement(valueChange);
        } else if (this.servicedIntention.isLeavingControl()) {
            final String searchText = valueChange.getStringAttribute("blur");
            selectedDictionaryElement = dictionaryLookupProvider.getElementByModelValue(searchText, this::getParameterValue);
            if (selectedDictionaryElement == null) {
//                Pageable pageable = PageRequest.of(0, pageSize);
//                List<Object> rows = getMatchingElements(searchText, pageable);
                if (rows.size() == 1) {
                    selectedDictionaryElement = rows.get(0);
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

    @Override
    public ElementChanges updateView() {
        ElementChanges elementChange = super.updateView();

        if (this.servicedIntention != null) {
            ValueChange valueChange = this.servicedIntention.valueChange;
            if (this.servicedIntention.isSelectingNewElement()) {
                elementChange.addChange(RAW_VALUE_ATTR, getRawValue());
            } else if (this.servicedIntention.isLeavingControl()) {
                elementChange.addChange(RAW_VALUE_ATTR, getRawValue());
            } else if (this.servicedIntention.isLookingForMatchingElements()) {
                serviceLookingForElements(valueChange, elementChange);
            } else if (this.servicedIntention.isChangingPage()) {
                serviceChangedPage(valueChange, elementChange);
            }
        }

        //Aktualizacja listy kolumn TODO: Rozważyć, czy to się nie dzieje tylko w fazie inicjalizacji
        if (columns == null) {
            this.columns = dictionaryLookupProvider.getColumnDefinitions();
            ;
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
                Pageable pageable = PageRequest.of(0, pageSize);
                this.rows = getMatchingElements(searchText, pageable); //TODO: Tutaj powinien jeszcze zostać użyty konwerter aby dopuścić oprócz stringów obiekty bardziej złożone
                List<?> rows = getMatchingElements(searchText, pageable); //TODO: Tutaj powinien jeszcze zostać użyty konwerter aby dopuścić oprócz stringów obiekty bardziej złożone
                elementChange.addChange(ATTR_ROWS, rows);
                elementChange.addChange(ATTR_PAGE, pageable.getPageNumber());
                elementChange.addChange(ATTR_PAGES_COUNT, pageable.getPageSize());
                List<String> test = Arrays.asList("Ala", "Józek", "Tomek", "Basia");
                elementChange.addChange("testowaKolekcja", test);
                System.out.println("Searching by matching text " + searchText + " and found " + rows.size() + " elements.");
            }
        } else {
            elementChange.addChange(ATTR_ROWS, Collections.emptyList());
        }
    }


    private void serviceSelectingNewElement(ValueChange valueChange) {


    }

    private void serviceUndoChanges(ValueChange valueChange) {
        log.error("Undo intention not implemented yet");
    }

    private void serviceChangedPage(ValueChange valueChange, ElementChanges elementChange) {
        log.error("Changing page intention not implemented yet");
    }

    @Getter
    private static class Intention {
        private final ValueChange valueChange;
        private boolean lookingForMatchingElements;
        private boolean leavingControl;
        @Setter
        private boolean selectingNewElement;
        private boolean isChangingPage;

        public Intention(ValueChange valueChange) {
            this.valueChange = valueChange;
            this.lookingForMatchingElements = valueChange.hasAttributeChanged("text");
            this.leavingControl = valueChange.hasAttributeChanged("blur");
            this.selectingNewElement = valueChange.hasAttributeChanged("select");
        }
    }

    private List<Object> getMatchingElements(String searchText, Pageable pageable) {
        final PageModel<Object> pm = dictionaryLookupProvider.getDictionaryElementsPaged(searchText, pageable, this::getParameterValue);
        if (pm == null) {
            FhLogger.warn("Provider has returned null what has been converted into empty array!");
            return Collections.emptyList();
        } else {
            pm.doRefresh(pageable);
            Page<Object> p1 = pm.getPage();
            return (p1 != null) ? p1.getContent() : Collections.emptyList();
        }
    }


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
                    IComboDataProviderFhDP<?, ?> wrappedProvider = AutowireHelper.getBean(wrappedProviderClass);
                    return new DictionaryLookupLegacyProvider(wrappedProvider);
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
    public BaseInputField createNewSameComponent() {
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
}
