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

    @JsonIgnore
    @Getter
    private final List<DictionaryComboParameterFhDP> subcomponents = new LinkedList<>();

    @JsonIgnore
    private IDictionaryLookupProvider<Object, Object> dictionaryLookupProvider;

    @JsonIgnore
    private PageModel<Object> pageModel;

    @JsonIgnore
    private ValueChange commandValueData;

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
        }
    }

    @Override
    public ElementChanges updateView() {
        ElementChanges elementChange = super.updateView();
        final Object dictionaryElement = dictionaryLookupProvider.getElementByModelValue(getRawValue(), this::getParameterValue);
        final String rawValueToShow = dictionaryLookupProvider.getDisplayValue(dictionaryElement);
        if (this.commandValueData != null) {
            switch (this.commandValueData.getStringAttribute("command")) {
                case "selectItem":
                    elementChange.addChange(RAW_VALUE_ATTR, rawValueToShow);
                    elementChange.addChange("orgRawValue", rawValueToShow);
                    elementChange.addChange(ATTR_ROWS, Collections.emptyList());
                    elementChange.addChange(ATTR_PAGE, null);
                    elementChange.addChange(ATTR_PAGES_COUNT, null);
                    break;

                case "search":
                case "changePage":
                    elementChange.addChange(ATTR_ROWS, pageModel.getPage().getContent());
                    elementChange.addChange(ATTR_PAGE, pageModel.getPage().getNumber() + 1);
                    elementChange.addChange(ATTR_PAGES_COUNT, pageModel.getPage().getTotalPages());
                    if (columns == null) {
                        this.columns = dictionaryLookupProvider.getColumnDefinitions();
                        elementChange.addChange(ATTR_COLUMNS, columns);
                    }
                    break;
            }
        } else {
            elementChange.addChange("orgRawValue", rawValueToShow);
            elementChange.addChange(RAW_VALUE_ATTR, rawValueToShow);
        }
        return elementChange;
    }

    //************************************************************************************************************
    //************************************************************************************************************
    //************************************************************************************************************
    //************************************************************************************************************

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
            Pageable pageable = PageRequest.of(0, pageSize);
            this.pageModel = dictionaryLookupProvider.getDictionaryElementsPaged(searchText, pageable, this::getParameterValue);
            this.pageModel.doRefresh(pageable);
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
}