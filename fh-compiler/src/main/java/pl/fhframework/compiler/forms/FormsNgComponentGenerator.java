package pl.fhframework.compiler.forms;

import org.apache.commons.text.StringEscapeUtils;
import pl.fhframework.compiler.core.dynamic.dependency.DependenciesContext;
import pl.fhframework.compiler.core.generator.model.form.AttributeMm;
import pl.fhframework.compiler.core.generator.model.form.ComponentMm;
import pl.fhframework.compiler.core.generator.model.form.FormMm;
import pl.fhframework.ReflectionUtils;
import pl.fhframework.annotations.Action;
import pl.fhframework.compiler.core.generator.*;
import pl.fhframework.core.FhException;
import pl.fhframework.core.generator.GenerationContext;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.model.forms.Form;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

public class FormsNgComponentGenerator extends AbstractNgTemplateCodeGenerator {
    public static final String EVENT_UPDATE = "$update()";
    public static final String EVENT_UPDATE_WITH_VALIDATION = "$updateValidate()";
    private final ITypeProvider[] typeProviders;

    private int indent = 0;
    private FormMm form;

    private Stack<ExpressionContext> expressionContextStack = new Stack<>();

    public FormsNgComponentGenerator(FormMm form, ModuleMetaModel moduleMetaModel, MetaModelService metaModelService) {
        super(moduleMetaModel, metaModelService);
        this.form = form;
        this.typeProviders = getTypeProvidersInt();
    }

    @Override
    protected void generateContents() {
        addTag(contentsSection, START_TAG_TEMPLATE, form.getTagName());
        contentsSection.addCode(" " + FORM_GROUP_CONSTANT);
        contentsSection.addCode(String.format(" " + ATTRIBUTE_AND_VALUE_TEMPLATE, "container", form.getContainer()));
        contentsSection.addCode(String.format(" " + ATTRIBUTE_AND_VALUE_TEMPLATE, "id", form.getFormId()));
        contentsSection.addCode(TAG_ENDING);

        GenerationContext context = new GenerationContext();
        form.getComponentTree().forEach(c -> processComponent(c, context));
        contentsSection.addLine();
        contentsSection.addSectionWithoutLine(context, ++indent);

        addTag(contentsSection, END_TAG_TEMPLATE_COMPLETE, form.getTagName());
    }

    private void processComponent(ComponentMm component, GenerationContext context) {
        if (Objects.equals(component.getTagName(), "fh-table")) {
            processTable(component, context);
        }
        else {
            processFhComponent(component, context);
        }
    }

    private void processFhComponent(ComponentMm component, GenerationContext context) {
        addTag(context, START_TAG_TEMPLATE, component.getTagName());
        addTagAttributes(component, context);
        context.addCode(TAG_ENDING);

        addTagChildren(component, context);

        addTag(context, END_TAG_TEMPLATE_COMPLETE, component.getTagName());
        context.addLine();
    }

    private void processTable(ComponentMm component, GenerationContext context) {

        addTag(context, START_TAG_TEMPLATE, component.getTagName());
        addTagAttributes(component, context);
        context.addCode(TAG_ENDING);

        processTr(component, context);

        addTag(context, END_TAG_TEMPLATE_COMPLETE, component.getTagName());
        context.addLine();

    }

    private void processTr(ComponentMm component, GenerationContext context) {
        String iteratorName = component.getAttributes().containsKey("iterator") ? StringUtils.removeSurroundingCharacters(component.getAttributes().get("iterator").getValue()) : null;
        String collectionValue = component.getAttributes().containsKey("collection") ? StringUtils.nullToEmpty(component.getAttributes().get("collection").getValue()) : null;
        if (iteratorName == null || collectionValue == null) {
            return;
        }
        GenerationContext trContext = new GenerationContext();
        trContext.addLine();
        addTag(trContext, START_TAG_TEMPLATE, "tr");

        String collectionExp = collectionValue != null ? expresionConverterWith(this::fillDefaultContext).convertExpression(collectionValue, component, this.form) : null;
        trContext.addCode(" *iterator=\"let %s of %s; let %s$rowNo = index;\"", iteratorName, collectionExp, iteratorName);

        trContext.addCode(TAG_ENDING);

        expressionContextStack.push(getTableContext(component, iteratorName, collectionValue));

        addTagChildren(component, trContext);

        expressionContextStack.pop();

        addTag(trContext, END_TAG_TEMPLATE_COMPLETE, "tr");
        context.addSectionWithoutLine(trContext, indent + 1);
    }

    private void addTagChildren(ComponentMm component, GenerationContext context) {
        if (component.hasSubcomponents()) {
            indent++;
            GenerationContext subcomponentContext = new GenerationContext();
            subcomponentContext.addLine();
            component.getSubcomponents().forEach(subcomponent -> processComponent(subcomponent, subcomponentContext));
            context.addSectionWithoutLine(subcomponentContext, indent);
            indent--;
        }
    }

    private void addTagAttributes(ComponentMm component, GenerationContext context) {
        component.getAttributes().forEach((k, v) -> {
            String value;
            if (v.getType() == AttributeMm.AttributeType.StringLiteral) {
                value = StringUtils.removeSurroundingCharacters(StringUtils.nullToEmpty(v.getValue()));
            }
            else if (v.getType() == AttributeMm.AttributeType.TwoWayBinding) {
                value = expresionConverterWith(this::fillReactiveContext).convertExpression(StringUtils.nullToEmpty(v.getValue()), component, this.form);
            }
            else {
                value = expresionConverterWith(this::fillDefaultContext).convertExpression(StringUtils.nullToEmpty(v.getValue()), component, this.form);
            }
            context.addCode(String.format(" " + getTemplate(v), k, StringEscapeUtils.escapeHtml4(value)));
        });
        component.getActions().forEach((k, v) -> context.addCode(String.format(" " + getTemplate(v), k, convertEvent(v.getValue()))));
    }

    private void addTag(GenerationContext context, String endTagTemplateComplete, String tagName) {
        context.addCode(String.format(endTagTemplateComplete, tagName));
    }

    private String getTemplate(AttributeMm attribute) {
        switch (attribute.getType()) {
            case StringLiteral:
                return ATTRIBUTE_AND_VALUE_TEMPLATE;
            case Static:
            case OneWayBinding:
                return ONE_WAY_BINDING_TEMPLATE;
            case TwoWayBinding:
                return ATTRIBUTE_AND_VALUE_TEMPLATE;
            case ActionBinding:
                return ACTION_AND_VALUE_TEMPLATE;
            case CombinedExpression:
                return ATTRIBUTE_AND_INTERPOLATION_TEMPLATE;
            default:
                throw new FhException("Unknown component attribute type: " + attribute.getType().name());
        }
    }

    private String convertEvent(String event) {
        // todo: spel
        if (StringUtils.isNullOrEmpty(event)) {
            return event;
        }
        event = event.trim();
        if (Action.NO_ACTION_DEFAULT.equals(event)) {
            return EVENT_UPDATE;
        }
        if (Action.NO_ACTION_WITH_VALIDATION.equals(event)) {
            return EVENT_UPDATE_WITH_VALIDATION;
        }
        if (!event.endsWith(")")) {
            return event + "()";
        }
        return event;
    }

    private ExpressionContext fillDefaultContext(ExpressionContext expressionContext) {
        return fillContext(expressionContext, "model", "model", "this");
    }

    private ExpressionContext fillReactiveContext(ExpressionContext expressionContext) {
        return fillContext(expressionContext, null, null, "form");
    }

    private ExpressionContext fillContext(ExpressionContext expressionContext, String rootExp, String thisExp, String formTxt) {
        expressionContext.getDefaultBindingRoot().setExpression(rootExp);
        expressionContext.getBindingRoot("THIS").setExpression(thisExp);
        expressionContext.getBindingRoot("FORM").setExpression(formTxt);

        if (!expressionContextStack.empty()) {
            expressionContext.addAll(expressionContextStack.peek());
        }

        return expressionContext;
    }

    private ExpressionContext getTableContext(ComponentMm component, String iteratorName, String collectionValue) {
        ExpressionContext expressionContext = new ExpressionContext();
        if (!expressionContextStack.isEmpty()) {
            expressionContext.addAll(expressionContextStack.peek());
        }


        Type collectionType = expresionConverterWith(this::fillDefaultContext).getExpressionType(collectionValue, component, this.form);
        expressionContext.addBindingRootAsParameter(iteratorName, ReflectionUtils.getGenericArguments(collectionType)[0]);
        expressionContext.addBindingRootAsParameter(iteratorName + "$rowNo", Long.class);
        return expressionContext;
    }

    protected ITypeProvider[] getTypeProviders() {
        return typeProviders;
    }

    private ITypeProvider[] getTypeProvidersInt() {
        List<ITypeProvider> typeProviders = new ArrayList<>();

        DependenciesContext dependenciesContext = metaModelService.provideDependeciesContext(form.getId());
        typeProviders.add(new RulesTypeDependecyProvider(dependenciesContext)); // todo: move up the class hierarchy
        if (this.form.isInternalModel()) {
            typeProviders.add(new InternalFormModelTypeProvider(((Form<?>)form.provideImpl()).getModelDefinition(), dependenciesContext));
        }

        return typeProviders.toArray(new ITypeProvider[]{});
    }
}
