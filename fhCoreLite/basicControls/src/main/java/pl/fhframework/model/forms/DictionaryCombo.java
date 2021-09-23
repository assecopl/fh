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
import pl.fhframework.core.FhCL;
import pl.fhframework.core.FhException;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.forms.provider.IComboDataProvider;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


@DocumentedComponent(documentationExample = true, value = "Enables users to quickly find and select from a pre-populated list of values as they type, leveraging searching and filtering.",
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

    @JsonIgnore
    private Method getValues;
    @JsonIgnore
    private Method getValue;
    @JsonIgnore
    private List<DictionaryComboParameter> getValuesParamsList = new LinkedList<>();
    @JsonIgnore
    private List<DictionaryComboParameter> getValueParamsList = new LinkedList<>();


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
            Class<? extends IComboDataProvider> providerClass = (Class<? extends IComboDataProvider>) FhCL.classLoader.loadClass(String.format(this.provider));
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
        allParamsList.addAll(this.getValuesFromDictionaryComboParameters(this.getValuesParamsList));
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
            this.getValues = ReflectionUtils.findMatchingPublicMethod(this.dataProvider.getClass(), "getValues").get();
            List<Object> paramsList = new LinkedList<>();
            Integer paramsCount = this.getValues.getParameterCount();
            for (int idx = 0; idx < paramsCount; idx++) {
                Optional<pl.fhframework.core.uc.Parameter> p = ReflectionUtils.getMethodParamAnnotation(this.getValues, idx, pl.fhframework.core.uc.Parameter.class);
                if (p.isPresent()) {
                    String paramName = p.get().name();
                    Optional<DictionaryComboParameter> optionalDictComboParam = subcomponents.stream().filter(e -> Objects.equals(e.getName(), paramName)).findFirst();
                    if (optionalDictComboParam.isPresent()) {
                        DictionaryComboParameter dictComboParam = optionalDictComboParam.get();
                        this.getValuesParamsList.add(dictComboParam);
                    } else {
                        throw new FhException("No attribute for " + DictionaryComboParameter.class.getSimpleName() + " : " + paramName);
                    }
                }
            }

            this.getValue = ReflectionUtils.findMatchingPublicMethod(this.dataProvider.getClass(), "getValue").get();
            Integer paramsCount2 = this.getValue.getParameterCount();
            for (int idx = 0; idx < paramsCount2; idx++) {
                Optional<pl.fhframework.core.uc.Parameter> p = ReflectionUtils.getMethodParamAnnotation(this.getValue, idx, pl.fhframework.core.uc.Parameter.class);
                if (p.isPresent()) {
                    String paramName = p.get().name();
                    Optional<DictionaryComboParameter> optionalDictComboParam = subcomponents.stream().filter(e -> Objects.equals(e.getName(), paramName)).findFirst();
                    if (optionalDictComboParam.isPresent()) {
                        DictionaryComboParameter dictComboParam = optionalDictComboParam.get();
                        this.getValueParamsList.add(dictComboParam);
                        BindingResult br = dictComboParam.getModelBinding().getBindingResult();
                    } else {
                        throw new FhException("No attribute for " + DictionaryComboParameter.class.getSimpleName() + " : " + paramName);
                    }
                }
            }


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
        if (s == null) return "";

        if (s.getClass().equals(String.class)) {
            return (String) s;
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

        if (getModelBinding() != null) {
            BindingResult selectedBindingResult = getModelBinding().getBindingResult();
            if (selectedBindingResult != null) {
                Object value = selectedBindingResult.getValue();
                if (!this.dataProvider.areObjectsEquals(value, (selectedItem != null ? this.dataProvider.getCode(selectedItem) : null))) {
                    List<Object> allParamsList = new LinkedList<>();
                    allParamsList.add(value);
                    allParamsList.addAll(this.getValuesFromDictionaryComboParameters(this.getValueParamsList));
                    this.selectedItem = (Object) ReflectionUtils.run(this.getValue, this.dataProvider, allParamsList.toArray());
                    this.rawValue = toRawValue(this.selectedItem);
                    elementChanges.addChange(RAW_VALUE_ATTR, this.rawValue);
                    this.filterText = rawValue != null ? rawValue : "";
                    updateFilterTextBinding();

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
    List<Object> getValuesFromDictionaryComboParameters(List<DictionaryComboParameter> dcp) {
        List<Object> l = new LinkedList<>();
        dcp.forEach(dictionaryComboParameter -> {
            BindingResult br = dictionaryComboParameter.getModelBinding().getBindingResult();
            if (br != null) {
                l.add(br.getValue());
            } else {
                throw new FhException("No attribute for " + DictionaryComboParameter.class.getSimpleName() + " : " + dictionaryComboParameter.getName());
            }
        });

        return l;
    }

    public ElementChanges comboParameterModelRefreash() {
        final ElementChanges elementChanges = super.updateView();
        this.processFiltering("");
        this.processFilterBinding(elementChanges, true);
        this.refreshView();
        return elementChanges;

    }


}




