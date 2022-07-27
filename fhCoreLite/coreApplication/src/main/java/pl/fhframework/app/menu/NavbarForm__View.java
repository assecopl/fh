package pl.fhframework.app.menu;


import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.binding.ActionBinding;
import pl.fhframework.binding.ActionSignature;
import pl.fhframework.binding.CompiledActionBinding;
import pl.fhframework.binding.CompiledBinding;
import pl.fhframework.binding.StaticBinding;
import pl.fhframework.core.generator.CompiledClassesHelper;
import pl.fhframework.core.i18n.MessageService;
import pl.fhframework.core.rules.service.RulesService;
import pl.fhframework.events.ViewEvent;
import pl.fhframework.format.FhConversionService;
import pl.fhframework.model.forms.AccessibilityEnum;
import pl.fhframework.model.forms.AccessibilityRule;
import pl.fhframework.model.forms.AvailabilityConfiguration;
import pl.fhframework.model.forms.Dropdown;
import pl.fhframework.model.forms.DropdownItem;
import pl.fhframework.model.forms.LocaleBundle;
import pl.fhframework.model.forms.attribute.FormModalSize;
import pl.fhframework.model.forms.attribute.FormType;
import pl.fhframework.model.forms.attribute.IconAlignment;
import pl.fhframework.model.forms.attribute.Layout;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class NavbarForm__View extends NavbarForm {


    public static final Set<ActionSignature> ____actions = new LinkedHashSet<>();
    public static final Set<String> ____variants = new LinkedHashSet<>();

    static {
        ____actions.add(new ActionSignature("toggleMenu"));
        ____actions.add(new ActionSignature("openFhStylesheet"));
        ____actions.add(new ActionSignature("closeAlternativeStylesheet"));
        ____actions.add(new ActionSignature("openStylesheet", String.class));
        ____actions.add(new ActionSignature("downloadUserLog"));
        ____actions.add(new ActionSignature("setLanguagePolish"));
        ____actions.add(new ActionSignature("setLanguageEnglish"));
        ____actions.add(new ActionSignature("setLangViewKey"));
    }

    static {
    }

    Dropdown a_dropdown_1;
    DropdownItem u_toggleMenu_1;
    DropdownItem u_fhCss_1;
    DropdownItem u_defaultCss_1;
    DropdownItem u_css1_1;
    DropdownItem u_css2_1;
    DropdownItem u_css3_1;
    DropdownItem u_css4_1;
    DropdownItem u_css5_1;
    DropdownItem u_css6_1;
    DropdownItem u_css7_1;
    DropdownItem a_dropdownItem_1;
    Dropdown a_dropdown_2_1;
    DropdownItem u_polishLang_1;
    DropdownItem u_englishLang_1;
    DropdownItem u_noLang_1;
    DropdownItem u_diLogout_1;
    DropdownItem u_diLogin_1;
    LocaleBundle a_localeBundle_1;
    AvailabilityConfiguration a_availabilityConfiguration_1;
    pl.fhframework.model.forms.Model a_model_1;
    @Autowired
    private FhConversionService __conversionService;
    @Autowired
    private MessageService __messagesService;
    @Autowired
    private RulesService __ruleService;


    public NavbarForm__View() {
        initCmp_this();
        setupAccessibility();

    }

    private FhConversionService __getConversionService() {
        return __conversionService;
    }

    private MessageService __getMessageService() {
        return __messagesService;
    }

    private NavbarForm__View getThisForm() {
        return this;
    }


    private void initCmp_this() {
        this.setDeclaredContainer("navbarForm");
        this.setLayout(Layout.VERTICAL);
        this.setModal(false);
        this.setFormType(FormType.HEADER);
        this.setModalSize(FormModalSize.REGULAR);
        this.setId("navbarFormInner");

        a_dropdown_1 = new Dropdown(this);
        this.addSubcomponent(a_dropdown_1);
        a_dropdown_1.setGroupingParentComponent(this);
        initCmp_a_dropdown_1();

        a_dropdown_2_1 = new Dropdown(this);
        this.addSubcomponent(a_dropdown_2_1);
        a_dropdown_2_1.setGroupingParentComponent(this);
        initCmp_a_dropdown_2_1();

        a_localeBundle_1 = new LocaleBundle(this);
        this.getLocaleBundle().add(a_localeBundle_1);
        initCmp_a_localeBundle_1();

        a_availabilityConfiguration_1 = new AvailabilityConfiguration(this);
        this.setAvailabilityConfiguration(a_availabilityConfiguration_1);
        initCmp_a_availabilityConfiguration_1();

        a_model_1 = new pl.fhframework.model.forms.Model(this);
        this.setModelDefinition(a_model_1);
        initCmp_a_model_1();
    }

    private void initCmp_a_dropdown_1() {
        a_dropdown_1.setLabelModelBinding(new CompiledBinding<>(
                "[icon=\'fa fa-cogs\'] {$msg.fh.menu.ui.navbar.buttons.options}",
                null,
                String.class, /* target type */
                this::__getConversionService,
                this::getA_dropdown_1_labelModelBinding, /* getter */
                null /* setter */
        ));
        a_dropdown_1.setStyleClasses("navbar-btn");

        u_toggleMenu_1 = new DropdownItem(this);
        a_dropdown_1.addSubcomponent(u_toggleMenu_1);
        u_toggleMenu_1.setGroupingParentComponent(a_dropdown_1);
        initCmp_u_toggleMenu_1();

        u_fhCss_1 = new DropdownItem(this);
        a_dropdown_1.addSubcomponent(u_fhCss_1);
        u_fhCss_1.setGroupingParentComponent(a_dropdown_1);
        initCmp_u_fhCss_1();

        u_defaultCss_1 = new DropdownItem(this);
        a_dropdown_1.addSubcomponent(u_defaultCss_1);
        u_defaultCss_1.setGroupingParentComponent(a_dropdown_1);
        initCmp_u_defaultCss_1();

        u_css1_1 = new DropdownItem(this);
        a_dropdown_1.addSubcomponent(u_css1_1);
        u_css1_1.setGroupingParentComponent(a_dropdown_1);
        initCmp_u_css1_1();

        u_css2_1 = new DropdownItem(this);
        a_dropdown_1.addSubcomponent(u_css2_1);
        u_css2_1.setGroupingParentComponent(a_dropdown_1);
        initCmp_u_css2_1();

        u_css3_1 = new DropdownItem(this);
        a_dropdown_1.addSubcomponent(u_css3_1);
        u_css3_1.setGroupingParentComponent(a_dropdown_1);
        initCmp_u_css3_1();

        u_css4_1 = new DropdownItem(this);
        a_dropdown_1.addSubcomponent(u_css4_1);
        u_css4_1.setGroupingParentComponent(a_dropdown_1);
        initCmp_u_css4_1();

        u_css5_1 = new DropdownItem(this);
        a_dropdown_1.addSubcomponent(u_css5_1);
        u_css5_1.setGroupingParentComponent(a_dropdown_1);
        initCmp_u_css5_1();

        u_css6_1 = new DropdownItem(this);
        a_dropdown_1.addSubcomponent(u_css6_1);
        u_css6_1.setGroupingParentComponent(a_dropdown_1);
        initCmp_u_css6_1();

        u_css7_1 = new DropdownItem(this);
        a_dropdown_1.addSubcomponent(u_css7_1);
        u_css7_1.setGroupingParentComponent(a_dropdown_1);
        initCmp_u_css7_1();

        a_dropdownItem_1 = new DropdownItem(this);
        a_dropdown_1.addSubcomponent(a_dropdownItem_1);
        a_dropdownItem_1.setGroupingParentComponent(a_dropdown_1);
        initCmp_a_dropdownItem_1();
        a_dropdown_1.setGroupingParentComponent(this);
    }


    // [icon='fa fa-cogs'] {$msg.fh.menu.ui.navbar.buttons.options}
    private String getA_dropdown_1_labelModelBinding() {
        try {
            return "[icon=\'fa fa-cogs\'] " + CompiledClassesHelper.nvl(__getMessageService().getBundle("menuMessageSource").getMessage("fh.menu.ui.navbar.buttons.options"));
        } catch (NullPointerException e) {
            if (CompiledClassesHelper.isLocalNullPointerException(e, getThisForm().getClass().getName(), "getA_dropdown_1_labelModelBinding")) {
                return null;
            } else {
                throw e;
            }
        }
    }

    private void initCmp_u_toggleMenu_1() {
        u_toggleMenu_1.setOnClick(new CompiledActionBinding(
                "toggleMenu", "toggleMenu"));
        u_toggleMenu_1.setIconAlignment(IconAlignment.BEFORE);
        u_toggleMenu_1.setModelBindingForValue(new CompiledBinding<>(
                "{$msg.fh.menu.ui.navbar.buttons.toggleMenu}",
                null,
                String.class, /* target type */
                this::__getConversionService,
                this::getU_toggleMenu_1_modelBindingForValue, /* getter */
                null /* setter */
        ));
        u_toggleMenu_1.setId("toggleMenu");
        u_toggleMenu_1.setGroupingParentComponent(a_dropdown_1);
    }


    // $msg.fh.menu.ui.navbar.buttons.toggleMenu
    private String getU_toggleMenu_1_modelBindingForValue() {
        try {
            return __getMessageService().getBundle("menuMessageSource").getMessage("fh.menu.ui.navbar.buttons.toggleMenu");
        } catch (NullPointerException e) {
            if (CompiledClassesHelper.isLocalNullPointerException(e, getThisForm().getClass().getName(), "getU_toggleMenu_1_modelBindingForValue")) {
                return null;
            } else {
                throw e;
            }
        }
    }

    private void initCmp_u_fhCss_1() {
        u_fhCss_1.setOnClick(new CompiledActionBinding(
                "openFhStylesheet", "openFhStylesheet"));
        u_fhCss_1.setIconAlignment(IconAlignment.BEFORE);
        u_fhCss_1.setModelBindingForValue(new CompiledBinding<>(
                "{$msg.fh.menu.ui.navbar.buttons.openstyle} FH",
                null,
                String.class, /* target type */
                this::__getConversionService,
                this::getU_fhCss_1_modelBindingForValue, /* getter */
                null /* setter */
        ));
        u_fhCss_1.setId("fhCss");
        u_fhCss_1.setGroupingParentComponent(a_dropdown_1);
    }


    // {$msg.fh.menu.ui.navbar.buttons.openstyle} FH
    private String getU_fhCss_1_modelBindingForValue() {
        try {
            return "" + CompiledClassesHelper.nvl(__getMessageService().getBundle("menuMessageSource").getMessage("fh.menu.ui.navbar.buttons.openstyle")) + " FH";
        } catch (NullPointerException e) {
            if (CompiledClassesHelper.isLocalNullPointerException(e, getThisForm().getClass().getName(), "getU_fhCss_1_modelBindingForValue")) {
                return null;
            } else {
                throw e;
            }
        }
    }

    private void initCmp_u_defaultCss_1() {
        u_defaultCss_1.setOnClick(new CompiledActionBinding(
                "closeAlternativeStylesheet", "closeAlternativeStylesheet"));
        u_defaultCss_1.setIconAlignment(IconAlignment.BEFORE);
        u_defaultCss_1.setModelBindingForValue(new CompiledBinding<>(
                "{$msg.fh.menu.ui.navbar.buttons.openstyledefault}",
                null,
                String.class, /* target type */
                this::__getConversionService,
                this::getU_defaultCss_1_modelBindingForValue, /* getter */
                null /* setter */
        ));
        u_defaultCss_1.setId("defaultCss");
        u_defaultCss_1.setGroupingParentComponent(a_dropdown_1);
    }


    // $msg.fh.menu.ui.navbar.buttons.openstyledefault
    private String getU_defaultCss_1_modelBindingForValue() {
        try {
            return __getMessageService().getBundle("menuMessageSource").getMessage("fh.menu.ui.navbar.buttons.openstyledefault");
        } catch (NullPointerException e) {
            if (CompiledClassesHelper.isLocalNullPointerException(e, getThisForm().getClass().getName(), "getU_defaultCss_1_modelBindingForValue")) {
                return null;
            } else {
                throw e;
            }
        }
    }

    private void initCmp_u_css1_1() {
        u_css1_1.setOnClick(new CompiledActionBinding(
                "openStylesheet(getId(0))", "openStylesheet",
                new ActionBinding.ActionArgument("getId(0)", (event) -> this.getU_css1_1_onClick_arg1(event)) /* getId(0) */));
        u_css1_1.setIconAlignment(IconAlignment.BEFORE);
        u_css1_1.setModelBindingForValue(new CompiledBinding<>(
                "{$msg.fh.menu.ui.navbar.buttons.openstyle} {getId(0)}",
                null,
                String.class, /* target type */
                this::__getConversionService,
                this::getU_css1_1_modelBindingForValue, /* getter */
                null /* setter */
        ));
        u_css1_1.setId("css1");
        u_css1_1.setGroupingParentComponent(a_dropdown_1);
    }


    // getId(0)
    private String getU_css1_1_onClick_arg1(ViewEvent event) {
        try {
            return getModel().getId(0);
        } catch (NullPointerException e) {
            if (CompiledClassesHelper.isLocalNullPointerException(e, getThisForm().getClass().getName(), "getU_css1_1_onClick_arg1")) {
                return null;
            } else {
                throw e;
            }
        }
    }

    // {$msg.fh.menu.ui.navbar.buttons.openstyle} {getId(0)}
    private String getU_css1_1_modelBindingForValue() {
        try {
            return "" + CompiledClassesHelper.nvl(__getMessageService().getBundle("menuMessageSource").getMessage("fh.menu.ui.navbar.buttons.openstyle")) + " " + CompiledClassesHelper.nvl(getModel().getId(0));
        } catch (NullPointerException e) {
            if (CompiledClassesHelper.isLocalNullPointerException(e, getThisForm().getClass().getName(), "getU_css1_1_modelBindingForValue")) {
                return null;
            } else {
                throw e;
            }
        }
    }

    private void initCmp_u_css2_1() {
        u_css2_1.setOnClick(new CompiledActionBinding(
                "openStylesheet(getId(1))", "openStylesheet",
                new ActionBinding.ActionArgument("getId(1)", (event) -> this.getU_css2_1_onClick_arg1(event)) /* getId(1) */));
        u_css2_1.setIconAlignment(IconAlignment.BEFORE);
        u_css2_1.setModelBindingForValue(new CompiledBinding<>(
                "{$msg.fh.menu.ui.navbar.buttons.openstyle} {getId(1)}",
                null,
                String.class, /* target type */
                this::__getConversionService,
                this::getU_css2_1_modelBindingForValue, /* getter */
                null /* setter */
        ));
        u_css2_1.setId("css2");
        u_css2_1.setGroupingParentComponent(a_dropdown_1);
    }


    // getId(1)
    private String getU_css2_1_onClick_arg1(ViewEvent event) {
        try {
            return getModel().getId(1);
        } catch (NullPointerException e) {
            if (CompiledClassesHelper.isLocalNullPointerException(e, getThisForm().getClass().getName(), "getU_css2_1_onClick_arg1")) {
                return null;
            } else {
                throw e;
            }
        }
    }

    // {$msg.fh.menu.ui.navbar.buttons.openstyle} {getId(1)}
    private String getU_css2_1_modelBindingForValue() {
        try {
            return "" + CompiledClassesHelper.nvl(__getMessageService().getBundle("menuMessageSource").getMessage("fh.menu.ui.navbar.buttons.openstyle")) + " " + CompiledClassesHelper.nvl(getModel().getId(1));
        } catch (NullPointerException e) {
            if (CompiledClassesHelper.isLocalNullPointerException(e, getThisForm().getClass().getName(), "getU_css2_1_modelBindingForValue")) {
                return null;
            } else {
                throw e;
            }
        }
    }

    private void initCmp_u_css3_1() {
        u_css3_1.setOnClick(new CompiledActionBinding(
                "openStylesheet(getId(2))", "openStylesheet",
                new ActionBinding.ActionArgument("getId(2)", (event) -> this.getU_css3_1_onClick_arg1(event)) /* getId(2) */));
        u_css3_1.setIconAlignment(IconAlignment.BEFORE);
        u_css3_1.setModelBindingForValue(new CompiledBinding<>(
                "{$msg.fh.menu.ui.navbar.buttons.openstyle} {getId(2)}",
                null,
                String.class, /* target type */
                this::__getConversionService,
                this::getU_css3_1_modelBindingForValue, /* getter */
                null /* setter */
        ));
        u_css3_1.setId("css3");
        u_css3_1.setGroupingParentComponent(a_dropdown_1);
    }


    // getId(2)
    private String getU_css3_1_onClick_arg1(ViewEvent event) {
        try {
            return getModel().getId(2);
        } catch (NullPointerException e) {
            if (CompiledClassesHelper.isLocalNullPointerException(e, getThisForm().getClass().getName(), "getU_css3_1_onClick_arg1")) {
                return null;
            } else {
                throw e;
            }
        }
    }

    // {$msg.fh.menu.ui.navbar.buttons.openstyle} {getId(2)}
    private String getU_css3_1_modelBindingForValue() {
        try {
            return "" + CompiledClassesHelper.nvl(__getMessageService().getBundle("menuMessageSource").getMessage("fh.menu.ui.navbar.buttons.openstyle")) + " " + CompiledClassesHelper.nvl(getModel().getId(2));
        } catch (NullPointerException e) {
            if (CompiledClassesHelper.isLocalNullPointerException(e, getThisForm().getClass().getName(), "getU_css3_1_modelBindingForValue")) {
                return null;
            } else {
                throw e;
            }
        }
    }

    private void initCmp_u_css4_1() {
        u_css4_1.setOnClick(new CompiledActionBinding(
                "openStylesheet(getId(3))", "openStylesheet",
                new ActionBinding.ActionArgument("getId(3)", (event) -> this.getU_css4_1_onClick_arg1(event)) /* getId(3) */));
        u_css4_1.setIconAlignment(IconAlignment.BEFORE);
        u_css4_1.setModelBindingForValue(new CompiledBinding<>(
                "{$msg.fh.menu.ui.navbar.buttons.openstyle} {getId(3)}",
                null,
                String.class, /* target type */
                this::__getConversionService,
                this::getU_css4_1_modelBindingForValue, /* getter */
                null /* setter */
        ));
        u_css4_1.setId("css4");
        u_css4_1.setGroupingParentComponent(a_dropdown_1);
    }


    // getId(3)
    private String getU_css4_1_onClick_arg1(ViewEvent event) {
        try {
            return getModel().getId(3);
        } catch (NullPointerException e) {
            if (CompiledClassesHelper.isLocalNullPointerException(e, getThisForm().getClass().getName(), "getU_css4_1_onClick_arg1")) {
                return null;
            } else {
                throw e;
            }
        }
    }

    // {$msg.fh.menu.ui.navbar.buttons.openstyle} {getId(3)}
    private String getU_css4_1_modelBindingForValue() {
        try {
            return "" + CompiledClassesHelper.nvl(__getMessageService().getBundle("menuMessageSource").getMessage("fh.menu.ui.navbar.buttons.openstyle")) + " " + CompiledClassesHelper.nvl(getModel().getId(3));
        } catch (NullPointerException e) {
            if (CompiledClassesHelper.isLocalNullPointerException(e, getThisForm().getClass().getName(), "getU_css4_1_modelBindingForValue")) {
                return null;
            } else {
                throw e;
            }
        }
    }

    private void initCmp_u_css5_1() {
        u_css5_1.setOnClick(new CompiledActionBinding(
                "openStylesheet(getId(4))", "openStylesheet",
                new ActionBinding.ActionArgument("getId(4)", (event) -> this.getU_css5_1_onClick_arg1(event)) /* getId(4) */));
        u_css5_1.setIconAlignment(IconAlignment.BEFORE);
        u_css5_1.setModelBindingForValue(new CompiledBinding<>(
                "{$msg.fh.menu.ui.navbar.buttons.openstyle} {getId(4)}",
                null,
                String.class, /* target type */
                this::__getConversionService,
                this::getU_css5_1_modelBindingForValue, /* getter */
                null /* setter */
        ));
        u_css5_1.setId("css5");
        u_css5_1.setGroupingParentComponent(a_dropdown_1);
    }


    // getId(4)
    private String getU_css5_1_onClick_arg1(ViewEvent event) {
        try {
            return getModel().getId(4);
        } catch (NullPointerException e) {
            if (CompiledClassesHelper.isLocalNullPointerException(e, getThisForm().getClass().getName(), "getU_css5_1_onClick_arg1")) {
                return null;
            } else {
                throw e;
            }
        }
    }

    // {$msg.fh.menu.ui.navbar.buttons.openstyle} {getId(4)}
    private String getU_css5_1_modelBindingForValue() {
        try {
            return "" + CompiledClassesHelper.nvl(__getMessageService().getBundle("menuMessageSource").getMessage("fh.menu.ui.navbar.buttons.openstyle")) + " " + CompiledClassesHelper.nvl(getModel().getId(4));
        } catch (NullPointerException e) {
            if (CompiledClassesHelper.isLocalNullPointerException(e, getThisForm().getClass().getName(), "getU_css5_1_modelBindingForValue")) {
                return null;
            } else {
                throw e;
            }
        }
    }

    private void initCmp_u_css6_1() {
        u_css6_1.setOnClick(new CompiledActionBinding(
                "openStylesheet(getId(5))", "openStylesheet",
                new ActionBinding.ActionArgument("getId(5)", (event) -> this.getU_css6_1_onClick_arg1(event)) /* getId(5) */));
        u_css6_1.setIconAlignment(IconAlignment.BEFORE);
        u_css6_1.setModelBindingForValue(new CompiledBinding<>(
                "{$msg.fh.menu.ui.navbar.buttons.openstyle} {getId(5)}",
                null,
                String.class, /* target type */
                this::__getConversionService,
                this::getU_css6_1_modelBindingForValue, /* getter */
                null /* setter */
        ));
        u_css6_1.setId("css6");
        u_css6_1.setGroupingParentComponent(a_dropdown_1);
    }


    // getId(5)
    private String getU_css6_1_onClick_arg1(ViewEvent event) {
        try {
            return getModel().getId(5);
        } catch (NullPointerException e) {
            if (CompiledClassesHelper.isLocalNullPointerException(e, getThisForm().getClass().getName(), "getU_css6_1_onClick_arg1")) {
                return null;
            } else {
                throw e;
            }
        }
    }

    // {$msg.fh.menu.ui.navbar.buttons.openstyle} {getId(5)}
    private String getU_css6_1_modelBindingForValue() {
        try {
            return "" + CompiledClassesHelper.nvl(__getMessageService().getBundle("menuMessageSource").getMessage("fh.menu.ui.navbar.buttons.openstyle")) + " " + CompiledClassesHelper.nvl(getModel().getId(5));
        } catch (NullPointerException e) {
            if (CompiledClassesHelper.isLocalNullPointerException(e, getThisForm().getClass().getName(), "getU_css6_1_modelBindingForValue")) {
                return null;
            } else {
                throw e;
            }
        }
    }

    private void initCmp_u_css7_1() {
        u_css7_1.setOnClick(new CompiledActionBinding(
                "openStylesheet(getId(6))", "openStylesheet",
                new ActionBinding.ActionArgument("getId(6)", (event) -> this.getU_css7_1_onClick_arg1(event)) /* getId(6) */));
        u_css7_1.setIconAlignment(IconAlignment.BEFORE);
        u_css7_1.setModelBindingForValue(new CompiledBinding<>(
                "{$msg.fh.menu.ui.navbar.buttons.openstyle} {getId(6)}",
                null,
                String.class, /* target type */
                this::__getConversionService,
                this::getU_css7_1_modelBindingForValue, /* getter */
                null /* setter */
        ));
        u_css7_1.setId("css7");
        u_css7_1.setGroupingParentComponent(a_dropdown_1);
    }


    // getId(6)
    private String getU_css7_1_onClick_arg1(ViewEvent event) {
        try {
            return getModel().getId(6);
        } catch (NullPointerException e) {
            if (CompiledClassesHelper.isLocalNullPointerException(e, getThisForm().getClass().getName(), "getU_css7_1_onClick_arg1")) {
                return null;
            } else {
                throw e;
            }
        }
    }

    // {$msg.fh.menu.ui.navbar.buttons.openstyle} {getId(6)}
    private String getU_css7_1_modelBindingForValue() {
        try {
            return "" + CompiledClassesHelper.nvl(__getMessageService().getBundle("menuMessageSource").getMessage("fh.menu.ui.navbar.buttons.openstyle")) + " " + CompiledClassesHelper.nvl(getModel().getId(6));
        } catch (NullPointerException e) {
            if (CompiledClassesHelper.isLocalNullPointerException(e, getThisForm().getClass().getName(), "getU_css7_1_modelBindingForValue")) {
                return null;
            } else {
                throw e;
            }
        }
    }

    private void initCmp_a_dropdownItem_1() {
        a_dropdownItem_1.setOnClick(new CompiledActionBinding(
                "downloadUserLog", "downloadUserLog"));
        a_dropdownItem_1.setIconAlignment(IconAlignment.BEFORE);
        a_dropdownItem_1.setModelBindingForValue(new CompiledBinding<>(
                "{$msg.fh.menu.ui.navbar.buttons.downloadlog}",
                null,
                String.class, /* target type */
                this::__getConversionService,
                this::getA_dropdownItem_1_modelBindingForValue, /* getter */
                null /* setter */
        ));
        a_dropdownItem_1.setGroupingParentComponent(a_dropdown_1);
    }


    // $msg.fh.menu.ui.navbar.buttons.downloadlog
    private String getA_dropdownItem_1_modelBindingForValue() {
        try {
            return __getMessageService().getBundle("menuMessageSource").getMessage("fh.menu.ui.navbar.buttons.downloadlog");
        } catch (NullPointerException e) {
            if (CompiledClassesHelper.isLocalNullPointerException(e, getThisForm().getClass().getName(), "getA_dropdownItem_1_modelBindingForValue")) {
                return null;
            } else {
                throw e;
            }
        }
    }

    private void initCmp_a_dropdown_2_1() {
        a_dropdown_2_1.setLabelModelBinding(new CompiledBinding<>(
                "[icon=\'fa fa-user\'] {login}",
                null,
                String.class, /* target type */
                this::__getConversionService,
                this::getA_dropdown_2_1_labelModelBinding, /* getter */
                null /* setter */
        ));
        a_dropdown_2_1.setStyleClasses("navbar-btn");

        u_polishLang_1 = new DropdownItem(this);
        a_dropdown_2_1.addSubcomponent(u_polishLang_1);
        u_polishLang_1.setGroupingParentComponent(a_dropdown_2_1);
        initCmp_u_polishLang_1();

        u_englishLang_1 = new DropdownItem(this);
        a_dropdown_2_1.addSubcomponent(u_englishLang_1);
        u_englishLang_1.setGroupingParentComponent(a_dropdown_2_1);
        initCmp_u_englishLang_1();

        u_noLang_1 = new DropdownItem(this);
        a_dropdown_2_1.addSubcomponent(u_noLang_1);
        u_noLang_1.setGroupingParentComponent(a_dropdown_2_1);
        initCmp_u_noLang_1();

        u_diLogout_1 = new DropdownItem(this);
        a_dropdown_2_1.addSubcomponent(u_diLogout_1);
        u_diLogout_1.setGroupingParentComponent(a_dropdown_2_1);
        initCmp_u_diLogout_1();

        u_diLogin_1 = new DropdownItem(this);
        a_dropdown_2_1.addSubcomponent(u_diLogin_1);
        u_diLogin_1.setGroupingParentComponent(a_dropdown_2_1);
        initCmp_u_diLogin_1();
        a_dropdown_2_1.setGroupingParentComponent(this);
    }


    // [icon='fa fa-user'] {login}
    private String getA_dropdown_2_1_labelModelBinding() {
        try {
            return "[icon=\'fa fa-user\'] " + CompiledClassesHelper.nvl(getModel().getLogin());
        } catch (NullPointerException e) {
            if (CompiledClassesHelper.isLocalNullPointerException(e, getThisForm().getClass().getName(), "getA_dropdown_2_1_labelModelBinding")) {
                return null;
            } else {
                throw e;
            }
        }
    }

    private void initCmp_u_polishLang_1() {
        u_polishLang_1.setOnClick(new CompiledActionBinding(
                "setLanguagePolish", "setLanguagePolish"));
        u_polishLang_1.setIconBinding(new StaticBinding<>("fa fa-flag"));
        u_polishLang_1.setIconAlignment(IconAlignment.BEFORE);
        u_polishLang_1.setModelBindingForValue(new CompiledBinding<>(
                "{$msg.fh.menu.ui.navbar.buttons.polish}",
                null,
                String.class, /* target type */
                this::__getConversionService,
                this::getU_polishLang_1_modelBindingForValue, /* getter */
                null /* setter */
        ));
        u_polishLang_1.setId("polishLang");
        u_polishLang_1.setGroupingParentComponent(a_dropdown_2_1);
    }


    // $msg.fh.menu.ui.navbar.buttons.polish
    private String getU_polishLang_1_modelBindingForValue() {
        try {
            return __getMessageService().getBundle("menuMessageSource").getMessage("fh.menu.ui.navbar.buttons.polish");
        } catch (NullPointerException e) {
            if (CompiledClassesHelper.isLocalNullPointerException(e, getThisForm().getClass().getName(), "getU_polishLang_1_modelBindingForValue")) {
                return null;
            } else {
                throw e;
            }
        }
    }

    private void initCmp_u_englishLang_1() {
        u_englishLang_1.setOnClick(new CompiledActionBinding(
                "setLanguageEnglish", "setLanguageEnglish"));
        u_englishLang_1.setIconBinding(new StaticBinding<>("fa fa-flag"));
        u_englishLang_1.setIconAlignment(IconAlignment.BEFORE);
        u_englishLang_1.setModelBindingForValue(new CompiledBinding<>(
                "{$msg.fh.menu.ui.navbar.buttons.english}",
                null,
                String.class, /* target type */
                this::__getConversionService,
                this::getU_englishLang_1_modelBindingForValue, /* getter */
                null /* setter */
        ));
        u_englishLang_1.setId("englishLang");
        u_englishLang_1.setGroupingParentComponent(a_dropdown_2_1);
    }

    private void initCmp_u_noLang_1() {
        u_noLang_1.setOnClick(new CompiledActionBinding(
                "setLangViewKey", "setLangViewKey"));
        u_noLang_1.setIconBinding(new StaticBinding<>("fa fa-cogs"));
        u_noLang_1.setIconAlignment(IconAlignment.BEFORE);
        u_noLang_1.setModelBindingForValue(new StaticBinding("View key"));
        u_noLang_1.setId("viewKeyLang");
        u_noLang_1.setGroupingParentComponent(a_dropdown_2_1);
    }


    // $msg.fh.menu.ui.navbar.buttons.english
    private String getU_englishLang_1_modelBindingForValue() {
        try {
            return __getMessageService().getBundle("menuMessageSource").getMessage("fh.menu.ui.navbar.buttons.english");
        } catch (NullPointerException e) {
            if (CompiledClassesHelper.isLocalNullPointerException(e, getThisForm().getClass().getName(), "getU_englishLang_1_modelBindingForValue")) {
                return null;
            } else {
                throw e;
            }
        }
    }

    private void initCmp_u_diLogout_1() {
        u_diLogout_1.setIconBinding(new StaticBinding<>("fa fa-power-off output"));
        u_diLogout_1.setIconAlignment(IconAlignment.BEFORE);
        u_diLogout_1.setModelBindingForValue(new CompiledBinding<>(
                "{$msg.fh.menu.ui.navbar.buttons.logout}",
                null,
                String.class, /* target type */
                this::__getConversionService,
                this::getU_diLogout_1_modelBindingForValue, /* getter */
                null /* setter */
        ));
        u_diLogout_1.setModelBindingForUrl(new CompiledBinding<>(
                "{logoutURL}",
                "logoutURL", /* property name */
                String.class, /* target type */
                this::__getConversionService,
                this::getModel, /* base object getter */
                (baseObject) -> this.getU_diLogout_1_modelBindingForUrl(baseObject), /* getter of property on base object */
                (baseObject, newValue) -> this.setU_diLogout_1_modelBindingForUrl(baseObject, newValue) /* setter of property on base object */
        ));
        u_diLogout_1.setId("diLogout");
        u_diLogout_1.setGroupingParentComponent(a_dropdown_2_1);
    }


    // $msg.fh.menu.ui.navbar.buttons.logout
    private String getU_diLogout_1_modelBindingForValue() {
        try {
            return __getMessageService().getBundle("menuMessageSource").getMessage("fh.menu.ui.navbar.buttons.logout");
        } catch (NullPointerException e) {
            if (CompiledClassesHelper.isLocalNullPointerException(e, getThisForm().getClass().getName(), "getU_diLogout_1_modelBindingForValue")) {
                return null;
            } else {
                throw e;
            }
        }
    }

    // logoutURL
    private String getU_diLogout_1_modelBindingForUrl(Model baseObject) {
        try {
            return baseObject.getLogoutURL();
        } catch (NullPointerException e) {
            if (CompiledClassesHelper.isLocalNullPointerException(e, getThisForm().getClass().getName(), "getU_diLogout_1_modelBindingForUrl")) {
                return null;
            } else {
                throw e;
            }
        }
    }

    // logoutURL
    private void setU_diLogout_1_modelBindingForUrl(Model baseObject, String newValue) {
        try {
            baseObject.setLogoutURL(newValue);
        } catch (NullPointerException e) {
            if (CompiledClassesHelper.isLocalNullPointerException(e, getThisForm().getClass().getName(), "setU_diLogout_1_modelBindingForUrl")) {
                // ignore
            } else {
                throw e;
            }
        }
    }

    private void initCmp_u_diLogin_1() {
        u_diLogin_1.setIconBinding(new StaticBinding<>("fa fa-power-off output"));
        u_diLogin_1.setIconAlignment(IconAlignment.BEFORE);
        u_diLogin_1.setModelBindingForValue(new CompiledBinding<>(
                "{$msg.fh.menu.ui.navbar.buttons.login}",
                null,
                String.class, /* target type */
                this::__getConversionService,
                this::getU_diLogin_1_modelBindingForValue, /* getter */
                null /* setter */
        ));
        u_diLogin_1.setModelBindingForUrl(new CompiledBinding<>(
                "{loginURL}",
                "loginURL", /* property name */
                String.class, /* target type */
                this::__getConversionService,
                this::getModel, /* base object getter */
                (baseObject) -> this.getU_diLogin_1_modelBindingForUrl(baseObject), /* getter of property on base object */
                (baseObject, newValue) -> this.setU_diLogin_1_modelBindingForUrl(baseObject, newValue) /* setter of property on base object */
        ));
        u_diLogin_1.setId("diLogin");
        u_diLogin_1.setGroupingParentComponent(a_dropdown_2_1);
    }


    // $msg.fh.menu.ui.navbar.buttons.login
    private String getU_diLogin_1_modelBindingForValue() {
        try {
            return __getMessageService().getBundle("menuMessageSource").getMessage("fh.menu.ui.navbar.buttons.login");
        } catch (NullPointerException e) {
            if (CompiledClassesHelper.isLocalNullPointerException(e, getThisForm().getClass().getName(), "getU_diLogin_1_modelBindingForValue")) {
                return null;
            } else {
                throw e;
            }
        }
    }

    // loginURL
    private String getU_diLogin_1_modelBindingForUrl(Model baseObject) {
        try {
            return baseObject.getLoginURL();
        } catch (NullPointerException e) {
            if (CompiledClassesHelper.isLocalNullPointerException(e, getThisForm().getClass().getName(), "getU_diLogin_1_modelBindingForUrl")) {
                return null;
            } else {
                throw e;
            }
        }
    }

    // loginURL
    private void setU_diLogin_1_modelBindingForUrl(Model baseObject, String newValue) {
        try {
            baseObject.setLoginURL(newValue);
        } catch (NullPointerException e) {
            if (CompiledClassesHelper.isLocalNullPointerException(e, getThisForm().getClass().getName(), "setU_diLogin_1_modelBindingForUrl")) {
                // ignore
            } else {
                throw e;
            }
        }
    }

    private void initCmp_a_localeBundle_1() {
        a_localeBundle_1.setBasename("menuMessageSource");
        a_localeBundle_1.setVar("msg");
        a_localeBundle_1.setGroupingParentComponent(this);
    }

    private void initCmp_a_availabilityConfiguration_1() {
        a_availabilityConfiguration_1.setGroupingParentComponent(this);
    }

    private void initCmp_a_model_1() {
    }


    // language eq 'pl'
    private boolean isGetAccessibilityOfpolishLang_1() {
        try {
            return (Objects.equals(getModel().getLanguage(), "pl"));
        } catch (NullPointerException e) {
            if (CompiledClassesHelper.isLocalNullPointerException(e, getThisForm().getClass().getName(), "isGetAccessibilityOfpolishLang_1")) {
                return false;
            } else {
                throw e;
            }
        }
    }

    // language eq 'en'
    private boolean isGetAccessibilityOfenglishLang_2() {
        try {
            return (Objects.equals(getModel().getLanguage(), "en"));
        } catch (NullPointerException e) {
            if (CompiledClassesHelper.isLocalNullPointerException(e, getThisForm().getClass().getName(), "isGetAccessibilityOfenglishLang_2")) {
                return false;
            } else {
                throw e;
            }
        }
    }

    // not fhCss
    private boolean isGetAccessibilityOffhCss_3() {
        try {
            return !(getModel().isFhCss());
        } catch (NullPointerException e) {
            if (CompiledClassesHelper.isLocalNullPointerException(e, getThisForm().getClass().getName(), "isGetAccessibilityOffhCss_3")) {
                return false;
            } else {
                throw e;
            }
        }
    }

    // not defaultCss
    private boolean isGetAccessibilityOfdefaultCss_4() {
        try {
            return !(getModel().isDefaultCss());
        } catch (NullPointerException e) {
            if (CompiledClassesHelper.isLocalNullPointerException(e, getThisForm().getClass().getName(), "isGetAccessibilityOfdefaultCss_4")) {
                return false;
            } else {
                throw e;
            }
        }
    }

    // cssIds.size() lt 1
    private boolean isGetAccessibilityOfcss1_5() {
        try {
            return ((getModel().getCssIds().size()) < (1));
        } catch (NullPointerException e) {
            if (CompiledClassesHelper.isLocalNullPointerException(e, getThisForm().getClass().getName(), "isGetAccessibilityOfcss1_5")) {
                return false;
            } else {
                throw e;
            }
        }
    }

    // cssIds.size() lt 2
    private boolean isGetAccessibilityOfcss2_6() {
        try {
            return ((getModel().getCssIds().size()) < (2));
        } catch (NullPointerException e) {
            if (CompiledClassesHelper.isLocalNullPointerException(e, getThisForm().getClass().getName(), "isGetAccessibilityOfcss2_6")) {
                return false;
            } else {
                throw e;
            }
        }
    }

    // cssIds.size() lt 3
    private boolean isGetAccessibilityOfcss3_7() {
        try {
            return ((getModel().getCssIds().size()) < (3));
        } catch (NullPointerException e) {
            if (CompiledClassesHelper.isLocalNullPointerException(e, getThisForm().getClass().getName(), "isGetAccessibilityOfcss3_7")) {
                return false;
            } else {
                throw e;
            }
        }
    }

    // cssIds.size() lt 4
    private boolean isGetAccessibilityOfcss4_8() {
        try {
            return ((getModel().getCssIds().size()) < (4));
        } catch (NullPointerException e) {
            if (CompiledClassesHelper.isLocalNullPointerException(e, getThisForm().getClass().getName(), "isGetAccessibilityOfcss4_8")) {
                return false;
            } else {
                throw e;
            }
        }
    }

    // cssIds.size() lt 5
    private boolean isGetAccessibilityOfcss5_9() {
        try {
            return ((getModel().getCssIds().size()) < (5));
        } catch (NullPointerException e) {
            if (CompiledClassesHelper.isLocalNullPointerException(e, getThisForm().getClass().getName(), "isGetAccessibilityOfcss5_9")) {
                return false;
            } else {
                throw e;
            }
        }
    }

    // cssIds.size() lt 6
    private boolean isGetAccessibilityOfcss6_10() {
        try {
            return ((getModel().getCssIds().size()) < (6));
        } catch (NullPointerException e) {
            if (CompiledClassesHelper.isLocalNullPointerException(e, getThisForm().getClass().getName(), "isGetAccessibilityOfcss6_10")) {
                return false;
            } else {
                throw e;
            }
        }
    }

    // cssIds.size() lt 7
    private boolean isGetAccessibilityOfcss7_11() {
        try {
            return ((getModel().getCssIds().size()) < (7));
        } catch (NullPointerException e) {
            if (CompiledClassesHelper.isLocalNullPointerException(e, getThisForm().getClass().getName(), "isGetAccessibilityOfcss7_11")) {
                return false;
            } else {
                throw e;
            }
        }
    }

    // guest
    private boolean isGetAccessibilityOfdiLogout_12() {
        try {
            return getModel().isGuest();
        } catch (NullPointerException e) {
            if (CompiledClassesHelper.isLocalNullPointerException(e, getThisForm().getClass().getName(), "isGetAccessibilityOfdiLogout_12")) {
                return false;
            } else {
                throw e;
            }
        }
    }

    // not guest
    private boolean isGetAccessibilityOfdiLogin_13() {
        try {
            return !(getModel().isGuest());
        } catch (NullPointerException e) {
            if (CompiledClassesHelper.isLocalNullPointerException(e, getThisForm().getClass().getName(), "isGetAccessibilityOfdiLogin_13")) {
                return false;
            } else {
                throw e;
            }
        }
    }

    private void setupAccessibility() {
        // <Invisible when="-language eq &apos;pl&apos;">polishLang</Invisible>
        this.getAllAccessibilityRules().add(new AccessibilityRule("polishLang", "", _rule_ -> (isGetAccessibilityOfpolishLang_1() ? AccessibilityEnum.HIDDEN : null), null /* null as xml object won't be provided in compiled form */));

        // <Invisible when="-language eq &apos;en&apos;">englishLang</Invisible>
        this.getAllAccessibilityRules().add(new AccessibilityRule("englishLang", "", _rule_ -> (isGetAccessibilityOfenglishLang_2() ? AccessibilityEnum.HIDDEN : null), null /* null as xml object won't be provided in compiled form */));

        // <Invisible when="-not fhCss">fhCss</Invisible>
        this.getAllAccessibilityRules().add(new AccessibilityRule("fhCss", "", _rule_ -> (isGetAccessibilityOffhCss_3() ? AccessibilityEnum.HIDDEN : null), null /* null as xml object won't be provided in compiled form */));

        // <Invisible when="-not defaultCss">defaultCss</Invisible>
        this.getAllAccessibilityRules().add(new AccessibilityRule("defaultCss", "", _rule_ -> (isGetAccessibilityOfdefaultCss_4() ? AccessibilityEnum.HIDDEN : null), null /* null as xml object won't be provided in compiled form */));

        // <Invisible when="-cssIds.size() lt 1">css1</Invisible>
        this.getAllAccessibilityRules().add(new AccessibilityRule("css1", "", _rule_ -> (isGetAccessibilityOfcss1_5() ? AccessibilityEnum.HIDDEN : null), null /* null as xml object won't be provided in compiled form */));

        // <Invisible when="-cssIds.size() lt 2">css2</Invisible>
        this.getAllAccessibilityRules().add(new AccessibilityRule("css2", "", _rule_ -> (isGetAccessibilityOfcss2_6() ? AccessibilityEnum.HIDDEN : null), null /* null as xml object won't be provided in compiled form */));

        // <Invisible when="-cssIds.size() lt 3">css3</Invisible>
        this.getAllAccessibilityRules().add(new AccessibilityRule("css3", "", _rule_ -> (isGetAccessibilityOfcss3_7() ? AccessibilityEnum.HIDDEN : null), null /* null as xml object won't be provided in compiled form */));

        // <Invisible when="-cssIds.size() lt 4">css4</Invisible>
        this.getAllAccessibilityRules().add(new AccessibilityRule("css4", "", _rule_ -> (isGetAccessibilityOfcss4_8() ? AccessibilityEnum.HIDDEN : null), null /* null as xml object won't be provided in compiled form */));

        // <Invisible when="-cssIds.size() lt 5">css5</Invisible>
        this.getAllAccessibilityRules().add(new AccessibilityRule("css5", "", _rule_ -> (isGetAccessibilityOfcss5_9() ? AccessibilityEnum.HIDDEN : null), null /* null as xml object won't be provided in compiled form */));

        // <Invisible when="-cssIds.size() lt 6">css6</Invisible>
        this.getAllAccessibilityRules().add(new AccessibilityRule("css6", "", _rule_ -> (isGetAccessibilityOfcss6_10() ? AccessibilityEnum.HIDDEN : null), null /* null as xml object won't be provided in compiled form */));

        // <Invisible when="-cssIds.size() lt 7">css7</Invisible>
        this.getAllAccessibilityRules().add(new AccessibilityRule("css7", "", _rule_ -> (isGetAccessibilityOfcss7_11() ? AccessibilityEnum.HIDDEN : null), null /* null as xml object won't be provided in compiled form */));

        // <Invisible when="-guest">diLogout</Invisible>
        this.getAllAccessibilityRules().add(new AccessibilityRule("diLogout", "", _rule_ -> (isGetAccessibilityOfdiLogout_12() ? AccessibilityEnum.HIDDEN : null), null /* null as xml object won't be provided in compiled form */));

        // <Invisible when="-not guest">diLogin</Invisible>
        this.getAllAccessibilityRules().add(new AccessibilityRule("diLogin", "", _rule_ -> (isGetAccessibilityOfdiLogin_13() ? AccessibilityEnum.HIDDEN : null), null /* null as xml object won't be provided in compiled form */));

        // defaultAvailability attributes of Variant elements
    }


}
