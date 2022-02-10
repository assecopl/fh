package pl.fhframework.compiler.core.generator.ts;

import org.springframework.expression.spel.ast.*;
import pl.fhframework.compiler.core.generator.AbstractExpressionProcessor;
import pl.fhframework.compiler.core.generator.BaseExpressionCodeGenerator;
import pl.fhframework.compiler.core.generator.ExpressionContext;
import pl.fhframework.compiler.core.generator.ModuleMetaModel;
import pl.fhframework.compiler.core.generator.model.service.ServiceMm;
import pl.fhframework.compiler.core.tools.JavaNamesUtils;
import pl.fhframework.ReflectionUtils;
import pl.fhframework.compiler.core.generator.model.spel.*;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.modules.services.ServiceTypeEnum;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ExpressionTsCodeGenerator extends BaseExpressionCodeGenerator {
    private boolean asynchronousCalls;

    public ExpressionTsCodeGenerator(ExpressionContext context, ModuleMetaModel moduleMetaModel) {
        this(context, moduleMetaModel, false);
    }

    public ExpressionTsCodeGenerator(ExpressionContext context, ModuleMetaModel moduleMetaModel, boolean asynchronousCalls) {
        super(context, moduleMetaModel);

        supportedSimpleOperators.put(OperatorInstanceof.class, new OperatorConfig("is", boolean.class, false));
        supportedSimpleOperators.put(OpNE.class, new OperatorConfig("!==", boolean.class, false));
        supportedSimpleOperators.put(OpEQ.class, new OperatorConfig("===", boolean.class, false));

        this.asynchronousCalls = asynchronousCalls;
    }

    @Override
    protected void processPropertyOrField(PropertyOrFieldReference node, StringBuilder expression, SpelNodeImpl parent, SpelNodeImpl previous, SpelNodeImpl next) {
        AbstractExpressionProcessor.InputAccessorExpression accessor = getContext(node.getName());
        // root/first property and it's not local variable or parameter
        if (previous == null && (accessor == null || !accessor.isPassedAsParameter())) {
            if (!StringUtils.isNullOrEmpty(getRootExpression())) {
                expression.append(getRootExpression()).append(".");
            }
        }
        if (previous != null) {
            expression.append(".");
        }
        expression.append(node.getName());
    }

    @Override
    protected void processMethodReference(MethodReference node, StringBuilder expression, SpelNodeImpl parent, SpelNodeImpl previous, SpelNodeImpl next) {
        if (previous == null) {
            expression.append(getRootExpression()).append(".");
        }
        else {
            expression.append(".");
        }
        expression.append(node.getName()).append("(");

        processAsParameters(getChildren(node), expression, node);

        expression.append(")");
    }

    protected SpelNodeImpl mapMethod(MethodReference node, Supplier<Type> ownerType) {
        String name = node.getName();
        if ("add".equals(name) && isCollection(ownerType)) { // todo: arguments
            // collection
            return new MethodReference(true, "push",node.getStartPosition(), node.getEndPosition(), super.getChildren(node).toArray(new SpelNodeImpl[0]));
        } else if ("size".equals(name) && isCollection(ownerType)) {
            // collection
            return new PropertyOrFieldReference(true, "length", node.getStartPosition(), node.getEndPosition());
        } else if ("remove".equals(name) && isCollection(ownerType)) {
            Type paramType = getType((SpelNodeImpl) node.getChild(0), node);
            if (int.class.isAssignableFrom(ReflectionUtils.getRawClass(paramType))) {
                return new MethodReference(true, "removeAt", node.getStartPosition(), node.getEndPosition(), super.getChildren(node).toArray(new SpelNodeImpl[0]));
            }
            return new MethodReference(true, "removeElement", node.getStartPosition(), node.getEndPosition(), super.getChildren(node).toArray(new SpelNodeImpl[0]));
        }
        return node;
    }

    private boolean isCollection(Supplier<Type> ownerType) {
        Type type = ownerType.get();
        return type != null && Collection.class.isAssignableFrom(ReflectionUtils.getRawClass(type));
    }

    protected final Type getType(SpelNodeImpl node, SpelNodeImpl parent) {
        List<SpelNodeImpl> spelNodesExp = new ArrayList<>();
        if (parent == null) {
            spelNodesExp.add(node);
        }
        else {
            List<SpelNodeImpl> allNodes = super.getChildren(parent);
            for (SpelNodeImpl spelNode : allNodes) {
                spelNodesExp.add(spelNode);
                if (spelNode == node) {
                    break;
                }
            }
        }
        return getExpressionType(spelNodesExp, globalExpressionContext);
    }

    @Override
    protected void processNotOperator(OperatorNot node, StringBuilder expression, SpelNodeImpl parent) {
        expression.append("!");
        processChild(node, expression, 0);
    }

    @Override
    protected void processOperator(Operator node, boolean unary, StringBuilder expression, SpelNodeImpl parent) {
        boolean rootExpression = parent instanceof ParentNode;
        if (!rootExpression) {
            expression.append("(");
        }
        if (!unary) {
            processChild(node, expression, 0);
        }
        expression.append(" ").append(supportedSimpleOperators.get(node.getClass()).getLiteral()).append(" ");
        processChild(node, expression,  unary ? 0 : 1);
        if (!rootExpression) {
            expression.append(")");
        }
    }

    @Override
    protected void processAssignment(Assign node, StringBuilder expression, SpelNodeImpl parent) {
        processChild(node, expression, 0);
        expression.append(" = ");
        processChild(node, expression, 1);
    }

    @Override
    protected void processLiteral(Literal node, StringBuilder expression, SpelNodeImpl parent) {
        if (node instanceof StringLiteral) {
            expression.append(String.format("'%s'", node.getLiteralValue().getValue()));
        }
        else if (node instanceof IntLiteral || node instanceof FloatLiteral || node instanceof RealLiteral){
            expression.append(node.toString());
        }
        else {
            expression.append(node.getOriginalValue());
        }
    }

    @Override
    protected void processRule(RuleNode node, StringBuilder expression, SpelNodeImpl parent) {
        expression.append(String.format("this.%s.%s(", JavaNamesUtils.getFieldName(getBaseName(node.getId())), node.getMethod()));
        processAsParameters(getChildren(node), expression, node);
        expression.append(")");
    }

    @Override
    protected void processService(ServiceNode node, StringBuilder expression, SpelNodeImpl parent) {
        ServiceMm serviceMm = getModuleMetaModel().getMetadata(node.getId());
        boolean synCall = !asynchronousCalls && serviceMm != null && (serviceMm.getServiceType() == ServiceTypeEnum.RestClient || serviceMm.getServiceType() == ServiceTypeEnum.RestService);
        String awaitCommand = synCall ? "await " : "";
        expression.append(String.format("%sthis.%s.%s(", awaitCommand, JavaNamesUtils.getFieldName(getBaseName(node.getId())), node.getMethod()));
        processAsParameters(getChildren(node), expression, node);
        expression.append(")");
    }

    @Override
    protected void processEnumValues(EnumValuesNode node, StringBuilder expression, SpelNodeImpl parent) {
        String enumClass = getBaseName(node.getId());
        expression.append(String.format("Object.keys(%s).filter(key => typeof %s[key as any] === \"number\")", enumClass, enumClass));
    }

    @Override
    protected void processEnum(EnumNode node, StringBuilder expression, SpelNodeImpl parent) {
        expression.append(String.format("%s.%s", getBaseName(node.getId()), node.getKey()));
    }

    @Override
    protected void processTernary(Ternary node, StringBuilder expression, SpelNodeImpl parent) {
        processChild(node, expression,  0);
        expression.append(" ? ");
        processChild(node, expression,  1);
        expression.append(" : ");
        processChild(node, expression,  2);
    }

    @Override
    protected void processIndexer(Indexer node, StringBuilder expression, SpelNodeImpl parent) {
        expression.append(node.toStringAST());
    }

    @Override
    protected void processI18nMessage(I18nNode node, StringBuilder expression, SpelNodeImpl parent) {
        expression.append(String.format("('%s' | translate)", node.getKey()));
    }

    @Override
    protected void processTypeReference(TypeReference node, StringBuilder expression, SpelNodeImpl parent) {
        FhLogger.errorSuppressed("TypeReference is not supported in TS");
        expression.append(DynamicClassName.forClassName(((TypeReference)node).getChild(0).toStringAST()).getBaseClassName());
    }

    private void processAsParameters(List<SpelNodeImpl> children, StringBuilder expression, SpelNodeImpl node) {
        expression.append(children.stream().map(param ->{
            StringBuilder value = new StringBuilder();
            processNode(param, node, value);
            return value.toString();
        }).collect(Collectors.joining(", ")));
    }

    private String getBaseName(String id) {
        return DynamicClassName.forClassName(id).getBaseClassName();
    }
}
