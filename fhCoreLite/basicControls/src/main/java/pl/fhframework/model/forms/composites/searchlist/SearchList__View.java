package pl.fhframework.model.forms.composites.searchlist;

import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.binding.ActionBinding;
import pl.fhframework.binding.ActionSignature;
import pl.fhframework.binding.CompiledActionBinding;
import pl.fhframework.binding.CompiledBinding;
import pl.fhframework.binding.StaticBinding;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.core.generator.CompiledClassesHelper;
import pl.fhframework.core.i18n.MessageService;
import pl.fhframework.core.rules.service.RulesService;
import pl.fhframework.events.ViewEvent;
import pl.fhframework.format.FhConversionService;
import pl.fhframework.model.forms.InputText;
import pl.fhframework.model.forms.Model;
import pl.fhframework.model.forms.OptionsList;
import pl.fhframework.model.forms.Spacer;
import pl.fhframework.model.forms.attribute.FormModalSize;
import pl.fhframework.model.forms.attribute.FormType;
import pl.fhframework.model.forms.attribute.IconAlignment;
import pl.fhframework.model.forms.attribute.Layout;
import pl.fhframework.model.forms.model.OptionsListElementModel;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SearchList__View extends SearchList
{
        
    
    @Autowired private FhConversionService __conversionService;
    @Autowired private MessageService __messagesService;
    @Autowired private RulesService __ruleService;
    
    InputText a_inputText_1;
    
    Spacer a_spacer_1;
    
    OptionsList u_list1_1;
    
    Model a_model_1;
    public static final Set<ActionSignature> ____actions = new LinkedHashSet<>();
    static {
        ____actions.add(new ActionSignature("onSearch", ViewEvent.class));
    }
    
    public static final Set<String> ____variants = new LinkedHashSet<>();


    public SearchList__View()
    {
        initCmp_this();
        setupAccessibility();

    }

    private FhConversionService __getConversionService() { return __conversionService; }
    private MessageService __getMessageService() { return __messagesService; }
    private SearchList__View getThisForm() { return this; }
    
    public static Map<DynamicClassName, Instant> getXmlTimestamps() {
        Map<DynamicClassName, Instant> timestamps = new HashMap<>();
        timestamps.put(DynamicClassName.forClassName("pl.fhframework.model.forms.composites.searchlist.SearchList"), Instant.ofEpochMilli(1560419352175L));
        timestamps.put(DynamicClassName.forClassName("pl.fhframework.model.forms.composites.searchlist.SearchListModel"), null);
        return timestamps;
    
    }
    
    private void initCmp_this() {
    this.setLabelModelBinding(new StaticBinding<>("SearchList"));
    this.setDeclaredContainer("mainForm");
    this.setHideHeader(false);
    this.setLayout(Layout.VERTICAL);
    this.setModal(false);
    this.setFormType(FormType.STANDARD);
    this.setModalSize(FormModalSize.REGULAR);
    this.setId("searchListForm");
    
    a_inputText_1 = new InputText(this);
    this.addSubcomponent(a_inputText_1);
    a_inputText_1.setGroupingParentComponent(this);
    initCmp_a_inputText_1(a_inputText_1);
    
    a_spacer_1 = new Spacer(this);
    this.addSubcomponent(a_spacer_1);
    a_spacer_1.setGroupingParentComponent(this);
    initCmp_a_spacer_1(a_spacer_1);
    
    u_list1_1 = new OptionsList(this);
    this.addSubcomponent(u_list1_1);
    u_list1_1.setGroupingParentComponent(this);
    initCmp_u_list1_1(u_list1_1);
    
    a_model_1 = new Model(this);
    this.setModelDefinition(a_model_1);
    initCmp_a_model_1(a_model_1);
    }
    
    private void initCmp_a_inputText_1(InputText a_inputText_1) {
    a_inputText_1.setMaxLength(65535);
    a_inputText_1.setOnInput(new CompiledActionBinding(
            "onSearch(this)", "onSearch",
            new ActionBinding.ActionArgument("this", (event) -> event) /* this */));
    a_inputText_1.setModelBinding(new CompiledBinding<>(
            "{inputValue}",
            "inputValue", /* property name */
            String.class, /* target type */
            this::__getConversionService,
            this::getModel, /* base object getter */
            (baseObject) -> this.getA_inputText_1_modelBinding(baseObject), /* getter of property on base object */
            (baseObject, newValue) -> this.setA_inputText_1_modelBinding(baseObject, newValue) /* setter of property on base object */
        ));
    a_inputText_1.setLabelModelBinding(new StaticBinding<>("Type searched option"));
    a_inputText_1.setIconAlignment(IconAlignment.BEFORE);
    a_inputText_1.setInputSize(60.0d);
    a_inputText_1.setWidth("md-3");
    a_inputText_1.setGroupingParentComponent(this);
    }
    
    
    // inputValue
    private String getA_inputText_1_modelBinding(SearchListModel baseObject) {
        try {
            return baseObject.getInputValue();
        } catch(NullPointerException e) {
            if (CompiledClassesHelper.isLocalNullPointerException(e, getThisForm().getClass().getName(), "getA_inputText_1_modelBinding")) {
                return null;
            } else {
                throw e;
            }
        }
    }
    
    // inputValue
    private void setA_inputText_1_modelBinding(SearchListModel baseObject, String newValue) {
        try {
            baseObject.setInputValue(newValue);
        } catch(NullPointerException e) {
            if (CompiledClassesHelper.isLocalNullPointerException(e, getThisForm().getClass().getName(), "setA_inputText_1_modelBinding")) {
                // ignore
            } else {
                throw e;
            }
        }
    }
    private void initCmp_a_spacer_1(Spacer a_spacer_1) {
    a_spacer_1.setWidth("md-10");
    a_spacer_1.setGroupingParentComponent(this);
    }
    
    private void initCmp_u_list1_1(OptionsList u_list1_1) {
    u_list1_1.setValuesBinding(new CompiledBinding<>(
            "{listElements}",
            "listElements", /* property name */
            List.class, /* target type */
            this::__getConversionService,
            this::getModel, /* base object getter */
            (baseObject) -> this.getU_list1_1_valuesBinding(baseObject), /* getter of property on base object */
            null /* setter of property on base object */
        ));
    u_list1_1.setTitleBinding(new CompiledBinding<>(
            "{listTitle}",
            "listTitle", /* property name */
            String.class, /* target type */
            this::__getConversionService,
            this::getModel, /* base object getter */
            (baseObject) -> this.getU_list1_1_titleBinding(baseObject), /* getter of property on base object */
            (baseObject, newValue) -> this.setU_list1_1_titleBinding(baseObject, newValue) /* setter of property on base object */
        ));
    u_list1_1.setEmptyValue(false);
    u_list1_1.setWidth("md-3");
    u_list1_1.setId("list1");
    u_list1_1.setGroupingParentComponent(this);
    }
    
    
    // listElements
    private List<OptionsListElementModel> getU_list1_1_valuesBinding(SearchListModel baseObject) {
        try {
            return baseObject.getListElements();
        } catch(NullPointerException e) {
            if (CompiledClassesHelper.isLocalNullPointerException(e, getThisForm().getClass().getName(), "getU_list1_1_valuesBinding")) {
                return null;
            } else {
                throw e;
            }
        }
    }
    
    // listTitle
    private String getU_list1_1_titleBinding(SearchListModel baseObject) {
        try {
            return baseObject.getListTitle();
        } catch(NullPointerException e) {
            if (CompiledClassesHelper.isLocalNullPointerException(e, getThisForm().getClass().getName(), "getU_list1_1_titleBinding")) {
                return null;
            } else {
                throw e;
            }
        }
    }
    
    // listTitle
    private void setU_list1_1_titleBinding(SearchListModel baseObject, String newValue) {
        try {
            baseObject.setListTitle(newValue);
        } catch(NullPointerException e) {
            if (CompiledClassesHelper.isLocalNullPointerException(e, getThisForm().getClass().getName(), "setU_list1_1_titleBinding")) {
                // ignore
            } else {
                throw e;
            }
        }
    }
    private void initCmp_a_model_1(Model a_model_1) {
    }
    
    private void setupAccessibility() {
        // defaultAvailability attributes of Variant elements
    }


}
