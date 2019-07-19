package pl.fhframework.app.menu;

import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.binding.ActionSignature;
import pl.fhframework.binding.AdHocActionBinding;
import pl.fhframework.binding.AdHocModelBinding;
import pl.fhframework.core.i18n.MessageService;
import pl.fhframework.core.rules.service.RulesService;
import pl.fhframework.format.FhConversionService;
import pl.fhframework.model.forms.Tree;
import pl.fhframework.model.forms.TreeElement;
import pl.fhframework.model.forms.attribute.FormModalSize;
import pl.fhframework.model.forms.attribute.FormType;
import pl.fhframework.model.forms.attribute.Layout;

import java.util.LinkedHashSet;
import java.util.Set;

public class MenuForm__View extends MenuForm {
    public static final Set<ActionSignature> ____actions = new LinkedHashSet<>();
    public static final Set<String> ____variants = new LinkedHashSet<>();

    static {
    }

    Tree a_tree_1;

    TreeElement u_menuTreeElement_1;

    pl.fhframework.model.forms.Model a_model_1;
    @Autowired
    private FhConversionService __conversionService;
    @Autowired
    private MessageService __messagesService;
    @Autowired
    private RulesService __ruleService;


    public MenuForm__View() {
        initCmp_this();
        setupAccessibility();

    }

    private FhConversionService __getConversionService() {
        return __conversionService;
    }

    private MessageService __getMessageService() {
        return __messagesService;
    }

    private MenuForm__View getThisForm() {
        return this;
    }


    private void initCmp_this() {
        this.setDeclaredContainer("menuForm");
        this.setLayout(Layout.VERTICAL);
        this.setModal(false);
        this.setFormType(FormType.STANDARD);
        this.setModalSize(FormModalSize.REGULAR);
        this.setId("menuFormInner");

        a_tree_1 = new Tree(this);
        this.addSubcomponent(a_tree_1);
        a_tree_1.setGroupingParentComponent(this);
        initCmp_a_tree_1();

        a_model_1 = new pl.fhframework.model.forms.Model(this);
        this.setModelDefinition(a_model_1);
        initCmp_a_model_1();
    }

    private void initCmp_a_tree_1() {
        a_tree_1.setBindingForNodes(new AdHocModelBinding<>(this, a_tree_1, "{menuElements}"));
        a_tree_1.setRelation("{children}");
        a_tree_1.setNextLevelExpandableExpression("true");
        a_tree_1.setIterator("element");
        a_tree_1.setNodeIcon("fa fa-caret-down");
        a_tree_1.setCollapsedNodeIcon("fa fa-caret-right");
        a_tree_1.setLeafIcon("");
        a_tree_1.setLines(false);
        a_tree_1.setDynamic(false);
        a_tree_1.setExpanded(false);
        a_tree_1.setLazy(false);
        a_tree_1.setGroupingParentComponent(this);

        u_menuTreeElement_1 = new TreeElement(this);
        a_tree_1.setTemplateTreeElement(u_menuTreeElement_1);
        initCmp_u_menuTreeElement_1();
    }

    private void initCmp_u_menuTreeElement_1() {
        u_menuTreeElement_1.setLabelModelBinding(new AdHocModelBinding<>(this, u_menuTreeElement_1, "{element.decoratedName}"));
        u_menuTreeElement_1.setIconBinding(new AdHocModelBinding<>(this, u_menuTreeElement_1, "{element.icon}"));
        u_menuTreeElement_1.setOnLabelClick(new AdHocActionBinding("menuElementClicked(element)", this, u_menuTreeElement_1));
        u_menuTreeElement_1.setId("menuTreeElement");
        u_menuTreeElement_1.setGroupingParentComponent(a_tree_1);
        u_menuTreeElement_1.setTree(a_tree_1);
    }

    private void initCmp_a_model_1() {
    }

    private void setupAccessibility() {
        // defaultAvailability attributes of Variant elements
    }


}
