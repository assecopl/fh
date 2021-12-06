package pl.fhframework.compiler.core.generator;

import lombok.Getter;
import pl.fhframework.core.generator.GenerationContext;

public abstract class AbstractNgTemplateCodeGenerator extends AbstractTypescriptCodeGenerator {
    protected static final String START_TAG_TEMPLATE = "<%s";
    protected static final String ATTRIBUTE_AND_VALUE_TEMPLATE = "%s=\"%s\"";
    protected static final String ATTRIBUTE_AND_INTERPOLATION_TEMPLATE = "%s=\"{{%s}}\"";
    protected static final String ONE_WAY_BINDING_TEMPLATE = "[%s]=\"%s\"";
    protected static final String TWO_WAY_BINDING_TEMPLATE = "[(%s)]=\"%s\"";
    protected static final String ACTION_AND_VALUE_TEMPLATE = "(%s)=\"%s\"";
    protected static final String END_TAG_TEMPLATE_COMPLETE = "</%s>";
    protected static final String EMPTY_ACTION_CONSTANT = "-";
    protected static final String FORM_GROUP_CONSTANT = "[formGroup]=\"form\"";
    protected static final String TAG_ENDING = ">";

    protected static final String UNKNOWN_COMPONENT_WARNING_MESSAGE = "Tag for control {%s} not found. Control ommitted for typescript form template generation.";

    public AbstractNgTemplateCodeGenerator(ModuleMetaModel moduleMetaModel, MetaModelService metaModelService) {
        super(moduleMetaModel, metaModelService);
    }

    @Getter
    protected GenerationContext contentsSection = new GenerationContext();

    protected GenerationContext generateContext() {
        generateContents();
        return contentsSection;
    }

    public String generateTemplate() {
        return generateContext().resolveCode();
    }

    protected abstract void generateContents();
}
