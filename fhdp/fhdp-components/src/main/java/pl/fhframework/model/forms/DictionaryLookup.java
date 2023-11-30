package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
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

    @JsonIgnore
    private IDictionaryLookupProvider<?> dictionaryLookupProvider;

    @JsonIgnore
    private final List<DictionaryComboParameterFhDP> subcomponents = new LinkedList<>();//TODO: Usunąć - mamy tylko kontrolki nonVisualSubcomponents

    @JsonIgnore
    @Getter
    @Setter
    @CompilationTraversable
    private List<NonVisualFormElement> nonVisualSubcomponents = new ArrayList<>();

    @Getter
    private List<NameValue> columns;

    @Getter
    @Setter
    @XMLProperty
    private Integer pageSize = 5;

    public DictionaryLookup(Form<?> form) {
        super(form);
    }

    @Override
    public void init() {
        super.init();
        this.dictionaryLookupProvider = getDictionaryLookupProvider(provider, this.getId());
    }


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

    @Override
    public List<DictionaryComboParameterFhDP> getSubcomponents() {
        return subcomponents;
    }


    /**
     * Resolve data provider bean
     *
     */
    private static IDictionaryLookupProvider<?> getDictionaryLookupProvider(String providerClassName, String componentId) {
        if (providerClassName != null) {
            try {
                @SuppressWarnings("unchecked")
                Class<? extends IDictionaryLookupProvider<?>> providerClass = (Class<? extends IDictionaryLookupProvider<?>>) FhCL.classLoader.loadClass(String.format(providerClassName));
                return AutowireHelper.getBean(providerClass);
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
    @JsonIgnore
    private Intention servicedIntention; //It allows smoothly join logic in updateModel and updateView without using dozens of properties

    @Override
    public void updateModel(ValueChange valueChange) {
        this.servicedIntention = new Intention(valueChange);
        if (this.servicedIntention.selectNewElement()) {
            serviceSelectingNewElement(valueChange);
        }
//        else if (this.servicedIntention.leavingControl()) {
//            if (this.rows.size() == 1) {//TODO: To powinno być przeniesione do intencji
//                serviceSelectingNewElement(valueChange);
//            }
//        }
    }

    @Override
    public ElementChanges updateView() {
        ElementChanges elementChange = super.updateView();

        if (this.servicedIntention != null) {
            ValueChange valueChange = this.servicedIntention.valueChange;
            if (this.servicedIntention.isLookingForMatchingElements()) {
                serviceLookingForElements(valueChange, elementChange);
            } else if (this.servicedIntention.changePage()) {
                serviceChangedPage(valueChange, elementChange);
            }
        }

        //Aktualizacja listy kolumn TODO: Rozważyć, czy to się nie dzieje tylko w fazie inicjalizacji
        List<NameValue> newColumnsList = dictionaryLookupProvider.getColumnDefinitions();
        if (newColumnsList!=columns){
            this.columns = newColumnsList;
            elementChange.addChange(ATTR_COLUMNS, columns);
        }

        return elementChange;
    }

    private void serviceLookingForElements(ValueChange valueChange, ElementChanges elementChange) {
        final String searchText = valueChange.getStringAttribute("text");
        if (searchText != null && !searchText.isEmpty()) {
            boolean searchByCode = searchText.length() > 1 && searchText.toUpperCase().equals(searchText);
            if (searchByCode) {
                Object foundObject = dictionaryLookupProvider.getElementById(searchText);
                List<?> rows = (foundObject != null) ? Collections.singletonList(foundObject) : Collections.emptyList();
                elementChange.addChange(ATTR_ROWS, rows);
                FhLogger.info("Searching by code " + searchText + " and found " + (foundObject != null));
            } else {
                Pageable pageable = PageRequest.of(0, pageSize);
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
        log.error("Select new element intention not implemented yet");
        //this.
    }

    private void serviceUndoChanges(ValueChange valueChange) {
        log.error("Undo intention not implemented yet");
    }

    private void serviceChangedPage(ValueChange valueChange, ElementChanges elementChange) {
        log.error("Changing page intention not implemented yet");
    }

    @AllArgsConstructor
    private static class Intention {
        private final ValueChange valueChange;

        public boolean isLookingForMatchingElements() {
            return valueChange.hasAttributeChanged("text");
        }


        public boolean leavingControl() {
            return valueChange.hasAttributeChanged("blur");
        }

        public boolean undoChanges() {
            return false;
        }

        public boolean selectNewElement() {
            return false;
        }

        public boolean changePage() {
            return false;
        }
    }

    private List<Object> getMatchingElements(String searchText, Pageable pageable) {
        final PageModel<?> responseObject = dictionaryLookupProvider.getValuesPaged(searchText, pageable);
        if (responseObject == null) {
            FhLogger.warn("Provider has returned null what has been converted into empty array!");
            return Collections.emptyList();
        } else {
            @SuppressWarnings("unchecked") final PageModel<Object> pm = (PageModel<Object>) responseObject;
            pm.doRefresh(pageable);
            Page<Object> p1 = pm.getPage();
            return (p1 != null) ? p1.getContent() : Collections.emptyList();
        }
    }

    //************************************************************************************************************
    //************************************************************************************************************
    //************************************************************************************************************
    //************************************************************************************************************
}
