package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import pl.fhframework.BindingResult;
import pl.fhframework.ReflectionUtils;
import pl.fhframework.annotations.CompilationTraversable;
import pl.fhframework.annotations.Control;
import pl.fhframework.annotations.DocumentedComponent;
import pl.fhframework.annotations.XMLProperty;
import pl.fhframework.core.FhException;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.forms.provider.IComboDataProvider;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


@DocumentedComponent(value = "Enables users to quickly find and select from a pre-populated list of values as they type, leveraging searching and filtering.",
        icon = "fa fa-outdent")
@Control(parents = {PanelGroup.class, Group.class, Column.class, Tab.class, Row.class, Form.class, Repeater.class}, invalidParents = {Table.class}, canBeDesigned = false)
public class DictionaryCombo extends Combo implements IGroupingComponent<DictionaryComboParameter> {

    @Getter
    @Setter
    @XMLProperty
    private String provider;

    @JsonIgnore
    private IComboDataProvider dataProvider;
    @JsonIgnore
    private Method getValues;
    @JsonIgnore
    private Method getValue;
    @JsonIgnore
    private Method getDisplayValue;
    @JsonIgnore
    private List<Object> paramsList = new LinkedList<>();

    @JsonIgnore
    private List<DictionaryComboParameter> subcomponents = new LinkedList<>();

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

    public DictionaryCombo(Form form) {
        super(form);
    }


    @Override
    public void init() {
        super.init();
        try {
            this.resolveDataProvider();
            this.resolveMethods();
        } catch (Exception ex) {
            FhLogger.warn("DictionaryCombo: Provider not found.", ex);
        }
    }

    @JsonIgnore
    public List<DictionaryComboParameter> getParameters() {
        return subcomponents;
    }


    private void resolveDataProvider() throws ClassNotFoundException {

        if (this.provider != null) {
            Class<? extends IComboDataProvider> providerClass = (Class<? extends IComboDataProvider>) Class.forName(String.format(this.provider));
            this.dataProvider = pl.fhframework.helper.AutowireHelper.getBean(providerClass);
        }
    }

    @Override
    protected boolean processValuesBinding() {
        boolean valuesChanged = false;
        if (this.values.isEmpty()) {
            valuesChanged = processValuesExternal(null);
        }
        return valuesChanged;
    }

    protected boolean processValuesExternal(String text) {
        List<Object> allParamsList = new LinkedList<>();
        allParamsList.add(text);
        allParamsList.addAll(this.paramsList);
        List<Object> values = (List<Object>) ReflectionUtils.run(this.getValues, this.dataProvider, allParamsList.toArray());
        if (values != null) {
            List collection = (List) values;
            if (!CollectionUtils.isEmpty(collection)) {
                this.modelType = collection.stream().findFirst().get().getClass();
            }
            this.values.put("", new LinkedList<>(collection));
            return true;
        }

        return false;
    }


    protected void processFiltering(String text) {
        filteredObjectValues.clear();
        processValuesExternal(text);
        Map<String, List<Object>> filtered = values.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, p -> p.getValue().stream().collect(Collectors.toList())));
        filteredObjectValues.putAll(filtered);
        filterInvoked = true;
    }

    private void resolveMethods() {
        if (provider != null) {
            Method method = ReflectionUtils.findMatchingPublicMethod(this.dataProvider.getClass(), "getValues").get();
            List<Object> paramsList = new LinkedList<>();
            Integer paramsCount = method.getParameterCount();
            for (int idx = 0; idx < paramsCount; idx++) {
                Optional<pl.fhframework.core.uc.Parameter> p = ReflectionUtils.getMethodParamAnnotation(method, idx, pl.fhframework.core.uc.Parameter.class);
                if (p.isPresent()) {
                    String paramName = p.get().name();
                    Optional<DictionaryComboParameter> optionalDictComboParam = subcomponents.stream().filter(e -> Objects.equals(e.getName(), paramName)).findFirst();
                    if (optionalDictComboParam.isPresent()) {
                        DictionaryComboParameter dictComboParam = optionalDictComboParam.get();
                        BindingResult br = dictComboParam.getModelBinding().getBindingResult();
                        if (br != null) {
                            paramsList.add(br.getValue());
                        } else {
                            throw new FhException("No attribute for " + DictionaryComboParameter.class.getSimpleName() + " : " + paramName);
                        }
                    } else {
                        throw new FhException("No attribute for " + DictionaryComboParameter.class.getSimpleName() + " : " + paramName);
                    }
                }
            }

            this.getValues = method;
            this.paramsList = paramsList;

            this.getValue = ReflectionUtils.findMatchingPublicMethod(this.dataProvider.getClass(), "getValue").get();
            this.getDisplayValue = ReflectionUtils.findMatchingPublicMethod(this.dataProvider.getClass(), "getDisplayValue").get();

        }
    }


    /**
     * Adds component to the container.
     */
    @Override
    public void addSubcomponent(DictionaryComboParameter component) {
        subcomponents.add(component);
    }

    @Override
    public void removeSubcomponent(DictionaryComboParameter removedFormElement) {
        subcomponents.remove(removedFormElement);
    }

    @Override
    public List<DictionaryComboParameter> getSubcomponents() {
        return subcomponents;
    }

    protected String objectToString(Object s) {

        if (s.getClass().equals(String.class)) {
            return (String) s;
        } else {
            return this.dataProvider.getDisplayValue(s);
        }
    }

    protected void changeSelectedItemBinding() {
        if (getModelBinding() != null ) {
            if(selectedItem == null){
                getModelBinding().setValue(selectedItem);
            } else {
                getModelBinding().setValue(this.dataProvider.getValue(selectedItem));
            }

        }
    }

    protected MultiValueMap<String, ComboItemDTO> collectValues(MultiValueMap<String, Object> valuesToConvert) {
        MultiValueMap<String, ComboItemDTO> filteredConvertedValues = new LinkedMultiValueMap<>();
        AtomicReference<Long> idx = new AtomicReference<>(0L);
        valuesToConvert.forEach((key, values) -> values.forEach(value -> {
            ComboItemDTO item;
            item = new ComboItemDTO(this.dataProvider.getValue(value), idx.get(), false, this.dataProvider.getDisplayValue(value));
            idx.getAndSet(idx.get() + 1);
            filteredConvertedValues.add(key, item);
        }));
        return filteredConvertedValues;
    }

    @Override
    protected boolean processValueBinding(ElementChanges elementChanges) {
        if (getModelBinding() != null) {
            BindingResult selectedBindingResult = getModelBinding().getBindingResult();
            if (selectedBindingResult != null) {
                Object value = selectedBindingResult.getValue();
                if ( !Objects.equals(value, (selectedItem != null ? this.dataProvider.getValue(selectedItem) : null))) {
                    if (this.filteredValues.isEmpty()) {
                        if (this.filteredObjectValues.isEmpty()) {
                            processFiltering(null);
                        }
                        this.filteredValues = collectValues(filteredObjectValues);
                    }
                    String key = null;
                    Integer valueIndex = null;
                    for (Map.Entry<String, List<ComboItemDTO>> entry : filteredValues.entrySet()) {
                        key = entry.getKey();
                        Integer idx = 0;
                        List<ComboItemDTO> a = entry.getValue();
                        for (ComboItemDTO b : a) {
                            if (Objects.equals(b.getTargetValue(), value)) {
                                valueIndex = idx;
                                break;
                            }
                            idx++;
                        }
                        if (valueIndex != null) {
                            break;
                        }
                    }
                    this.selectedItem = (key != null && valueIndex != null) ? this.filteredObjectValues.get(key).get(valueIndex) : null;
                    if (selectedItem != null) {
                        this.rawValue = toRawValue(this.selectedItem);
                        elementChanges.addChange(RAW_VALUE_ATTR, this.rawValue);
                        this.filterText = rawValue != null ? rawValue : "";
                        updateFilterTextBinding();

                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }
        return false;
    }

}




