package pl.fhframework.model.forms.composites.picklist;


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
import pl.fhframework.model.forms.Button;
import pl.fhframework.model.forms.Model;
import pl.fhframework.model.forms.OptionsList;
import pl.fhframework.model.forms.attribute.FormModalSize;
import pl.fhframework.model.forms.attribute.FormType;
import pl.fhframework.model.forms.attribute.Layout;
import pl.fhframework.model.forms.model.OptionsListElementModel;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PickList__View extends PickList {

    @Autowired
    private FhConversionService __conversionService;
    @Autowired
    private MessageService __messagesService;
    @Autowired
    private RulesService __ruleService;

    OptionsList u_list1_1;

    Button a_button_1;

    Button a_button_2_1;

    OptionsList u_list2_1;

    Model a_model_1;
    public static final Set<ActionSignature> ____actions = new LinkedHashSet<>();

    static {
        ____actions.add(new ActionSignature("onMoveToList2", ViewEvent.class));
        ____actions.add(new ActionSignature("onMoveToList1", ViewEvent.class));
    }

    public static final Set<String> ____variants = new LinkedHashSet<>();


    public PickList__View() {
        initCmp_this();
        setupAccessibility();

    }

    private FhConversionService __getConversionService() {
        return __conversionService;
    }

    private MessageService __getMessageService() {
        return __messagesService;
    }

    private PickList__View getThisForm() {
        return this;
    }

    public static Map<DynamicClassName, Instant> getXmlTimestamps() {
        Map<DynamicClassName, Instant> timestamps = new HashMap<>();
        timestamps.put(DynamicClassName.forClassName("pl.fhframework.model.forms.composites.picklist.PickListModel"), null);
        timestamps.put(DynamicClassName.forClassName("pl.fhframework.model.forms.composites.picklist.PickList"), Instant.ofEpochMilli(1560419352182L));
        return timestamps;

    }

    private void initCmp_this() {
        this.setLabelModelBinding(new StaticBinding<>("Template"));
        this.setDeclaredContainer("mainForm");
        this.setHideHeader(false);
        this.setLayout(Layout.VERTICAL);
        this.setModal(false);
        this.setFormType(FormType.STANDARD);
        this.setModalSize(FormModalSize.REGULAR);
        this.setId("addressOrderForm");

        u_list1_1 = new OptionsList(this);
        this.addSubcomponent(u_list1_1);
        u_list1_1.setGroupingParentComponent(this);
        initCmp_u_list1_1(u_list1_1);

        a_button_1 = new Button(this);
        this.addSubcomponent(a_button_1);
        a_button_1.setGroupingParentComponent(this);
        initCmp_a_button_1(a_button_1);

        a_button_2_1 = new Button(this);
        this.addSubcomponent(a_button_2_1);
        a_button_2_1.setGroupingParentComponent(this);
        initCmp_a_button_2_1(a_button_2_1);

        u_list2_1 = new OptionsList(this);
        this.addSubcomponent(u_list2_1);
        u_list2_1.setGroupingParentComponent(this);
        initCmp_u_list2_1(u_list2_1);

        a_model_1 = new Model(this);
        this.setModelDefinition(a_model_1);
        initCmp_a_model_1(a_model_1);
    }

    private void initCmp_u_list1_1(OptionsList u_list1_1) {
        u_list1_1.setValuesBinding(new CompiledBinding<>(
                "{valuesList1}",
                "valuesList1", /* property name */
                List.class, /* target type */
                this::__getConversionService,
                this::getModel, /* base object getter */
                (baseObject) -> this.getU_list1_1_valuesBinding(baseObject), /* getter of property on base object */
                null /* setter of property on base object */
        ));
        u_list1_1.setTitleBinding(new CompiledBinding<>(
                "{titleList1}",
                "titleList1", /* property name */
                String.class, /* target type */
                this::__getConversionService,
                this::getModel, /* base object getter */
                (baseObject) -> this.getU_list1_1_titleBinding(baseObject), /* getter of property on base object */
                null /* setter of property on base object */
        ));
        u_list1_1.setEmptyValue(false);
        u_list1_1.setWidth("md-3");
        u_list1_1.setId("list1");
        u_list1_1.setGroupingParentComponent(this);
    }


    // valuesList1
    private List<OptionsListElementModel> getU_list1_1_valuesBinding(PickListModel baseObject) {
        try {
            return baseObject.getValuesList1();
        } catch (NullPointerException e) {
            if (CompiledClassesHelper.isLocalNullPointerException(e, getThisForm().getClass().getName(), "getU_list1_1_valuesBinding")) {
                return null;
            } else {
                throw e;
            }
        }
    }

    // titleList1
    private String getU_list1_1_titleBinding(PickListModel baseObject) {
        try {
            return baseObject.getTitleList1();
        } catch (NullPointerException e) {
            if (CompiledClassesHelper.isLocalNullPointerException(e, getThisForm().getClass().getName(), "getU_list1_1_titleBinding")) {
                return null;
            } else {
                throw e;
            }
        }
    }

    private void initCmp_a_button_1(Button a_button_1) {
        a_button_1.setOnClick(new CompiledActionBinding(
                "onMoveToList2(this)", "onMoveToList2",
                new ActionBinding.ActionArgument("this", (event) -> event) /* this */));
        a_button_1.setLabelModelBinding(new StaticBinding<>(">"));
        a_button_1.setWidth("md-1");
        a_button_1.setGroupingParentComponent(this);
    }

    private void initCmp_a_button_2_1(Button a_button_2_1) {
        a_button_2_1.setOnClick(new CompiledActionBinding(
                "onMoveToList1(this)", "onMoveToList1",
                new ActionBinding.ActionArgument("this", (event) -> event) /* this */));
        a_button_2_1.setLabelModelBinding(new StaticBinding<>("<"));
        a_button_2_1.setWidth("md-1");
        a_button_2_1.setGroupingParentComponent(this);
    }

    private void initCmp_u_list2_1(OptionsList u_list2_1) {
        u_list2_1.setValuesBinding(new CompiledBinding<>(
                "{valuesList2}",
                "valuesList2", /* property name */
                List.class, /* target type */
                this::__getConversionService,
                this::getModel, /* base object getter */
                (baseObject) -> this.getU_list2_1_valuesBinding(baseObject), /* getter of property on base object */
                null /* setter of property on base object */
        ));
        u_list2_1.setTitleBinding(new CompiledBinding<>(
                "{titleList2}",
                "titleList2", /* property name */
                String.class, /* target type */
                this::__getConversionService,
                this::getModel, /* base object getter */
                (baseObject) -> this.getU_list2_1_titleBinding(baseObject), /* getter of property on base object */
                null /* setter of property on base object */
        ));
        u_list2_1.setEmptyValue(false);
        u_list2_1.setWidth("md-3");
        u_list2_1.setId("list2");
        u_list2_1.setGroupingParentComponent(this);
    }


    // valuesList2
    private List<OptionsListElementModel> getU_list2_1_valuesBinding(PickListModel baseObject) {
        try {
            return baseObject.getValuesList2();
        } catch (NullPointerException e) {
            if (CompiledClassesHelper.isLocalNullPointerException(e, getThisForm().getClass().getName(), "getU_list2_1_valuesBinding")) {
                return null;
            } else {
                throw e;
            }
        }
    }

    // titleList2
    private String getU_list2_1_titleBinding(PickListModel baseObject) {
        try {
            return baseObject.getTitleList2();
        } catch (NullPointerException e) {
            if (CompiledClassesHelper.isLocalNullPointerException(e, getThisForm().getClass().getName(), "getU_list2_1_titleBinding")) {
                return null;
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
