package pl.fhframework.compiler.core.generator;

import lombok.Getter;
import org.springframework.expression.spel.ast.*;
import pl.fhframework.compiler.core.i18n.MessagesTypeProvider;
import pl.fhframework.compiler.core.generator.model.spel.*;
import pl.fhframework.core.FhException;
import pl.fhframework.core.util.StringUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public abstract class BaseExpressionCodeGenerator extends AbstractExpressionProcessor {
    @Getter
    private ModuleMetaModel moduleMetaModel;

    public BaseExpressionCodeGenerator(ExpressionContext context, ModuleMetaModel moduleMetaModel) {
        super(context, Collections.emptyList()); // enums, rules, services, messages (i18n) type providers are required
        this.moduleMetaModel = moduleMetaModel;
    }

    public final String generate(String lelExp) {
        if (StringUtils.isNullOrEmpty(lelExp)) {
            return "";
        }

        List<SpelNodeImpl> spelNodes = convertNodes(parseExpression(lelExp));
        StringBuilder expression = new StringBuilder();

        processNodes(spelNodes, expression, wrapNodes(spelNodes));

        return expression.toString();
    }

    protected final void processChild(SpelNodeImpl node, StringBuilder expression, int idx) {
        List<SpelNodeImpl> children = getChildren(node);
        processNode(children.get(idx), expression, node, null, null);
    }

    protected final void processNode(SpelNodeImpl node, SpelNodeImpl parent, StringBuilder expression) {
        processNode(node, expression, parent, null, null);
    }

    protected AbstractExpressionProcessor.InputAccessorExpression getContext(String name) {
        if (globalExpressionContext.hasBindingRoot(name)) {
            return globalExpressionContext.getBindingRoot(name);
        }
        return null;
    }

    protected abstract void processPropertyOrField(PropertyOrFieldReference node, StringBuilder expression, SpelNodeImpl parent, SpelNodeImpl previous, SpelNodeImpl next);

    protected abstract void processMethodReference(MethodReference node, StringBuilder expression, SpelNodeImpl parent, SpelNodeImpl previous, SpelNodeImpl next);

    protected abstract void processNotOperator(OperatorNot node, StringBuilder expression, SpelNodeImpl parent);

    protected abstract void processOperator(Operator node, boolean unary, StringBuilder expression, SpelNodeImpl parent);

    protected abstract void processAssignment(Assign node, StringBuilder expression, SpelNodeImpl parent);

    protected abstract void processLiteral(Literal node, StringBuilder expression, SpelNodeImpl parent);

    protected abstract void processRule(RuleNode node, StringBuilder expression, SpelNodeImpl parent);

    protected abstract void processService(ServiceNode node, StringBuilder expression, SpelNodeImpl parent);

    protected abstract void processEnumValues(EnumValuesNode node, StringBuilder expression, SpelNodeImpl parent);

    protected abstract void processEnum(EnumNode node, StringBuilder expression, SpelNodeImpl parent);

    protected abstract void processI18nMessage(I18nNode node, StringBuilder expression, SpelNodeImpl parent);

    protected abstract void processTernary(Ternary node, StringBuilder expression, SpelNodeImpl parent);

    protected abstract void processIndexer(Indexer node, StringBuilder expression, SpelNodeImpl parent);

    protected abstract void processTypeReference(TypeReference node, StringBuilder expression, SpelNodeImpl parent);

    protected final void processNodes(List<SpelNodeImpl> spelNodes, StringBuilder expression, SpelNodeImpl parent) {
        for (int idx = 0; idx < spelNodes.size(); idx++) {
            processNode(spelNodes.get(idx), expression, parent, getPrevious(spelNodes, idx), getNext(spelNodes, idx));
        }
    }

    @Override
    protected final List<SpelNodeImpl> getChildren(SpelNodeImpl complexNode) {
        return convertNodes(super.getChildren(complexNode));
    }

    private List<SpelNodeImpl> convertNodes(List<SpelNodeImpl> children) {
        if (!children.isEmpty()) {
            if (isRule(children.get(0))) {
                return convertToRule(children);
            }
            else if (isService(children.get(0))) {
                return convertToService(children);
            }
            else if (isEnum(children.get(0))) {
                return convertToEnum(children);
            }
            else if (isI18n(children.get(0))) {
                return convertToI18n(children);
            }
        }
        return children;
    }

    private List<SpelNodeImpl> convertToRule(List<SpelNodeImpl> children) {
        MethodReference methodReference = (MethodReference)children.stream().filter(MethodReference.class::isInstance).findFirst().get();

        return Collections.singletonList(new RuleNode(children.get(0), concatFieldsNames(children, 1, children.indexOf(methodReference) - 1), methodReference.getName(), super.getChildren(methodReference), methodReference));
    }

    private List<SpelNodeImpl> convertToService(List<SpelNodeImpl> children) {
        MethodReference methodReference = (MethodReference)children.stream().filter(MethodReference.class::isInstance).findFirst().get();

        return Collections.singletonList(new ServiceNode(children.get(0), concatFieldsNames(children, 1, children.indexOf(methodReference) - 1), methodReference.getName(), super.getChildren(methodReference), methodReference));
    }

    private List<SpelNodeImpl> convertToEnum(List<SpelNodeImpl> children) {
        SpelNodeImpl lastNode = children.get(children.size() - 1);
        String name = concatFieldsNames(children, 1, children.size() - 2);
        if (lastNode instanceof MethodReference) {
            name = name.split("\\$")[0];
            return Collections.singletonList(new EnumValuesNode(children.get(0), name, (MethodReference) lastNode));
        }
        return Collections.singletonList(new EnumNode(children.get(0), name, ((PropertyOrFieldReference)lastNode).getName(), (PropertyOrFieldReference) lastNode));
    }

    private List<SpelNodeImpl> convertToI18n(List<SpelNodeImpl> children) {
        String key;
        if (children.get(1) instanceof MethodReference) {
            key = ((StringLiteral)children.get(1).getChild(0)).getOriginalValue();
        }
        else {
         key = concatFieldsNames(children, 1, children.size() - 1);
        }
        String bundle = ((PropertyOrFieldReference)children.get(0)).getName().substring(1);
        return Collections.singletonList(new I18nNode(children.get(0), bundle, key));
    }

    private String concatFieldsNames(List<SpelNodeImpl> children, int start, int end) {
        List<String> names = new ArrayList<>(end - start + 1);
        for (int idx = start; idx <= end; idx++) {
            names.add(((PropertyOrFieldReference)children.get(idx)).getName());
        }
        return String.join(".", names);
    }

    private boolean isRule(SpelNodeImpl spelNode) {
        return isPropertyNamed(spelNode, RulesTypeProvider.RULE_PREFIX);
    }

    private boolean isService(SpelNodeImpl spelNode) {
        return isPropertyNamed(spelNode, FhServicesTypeProvider.SERVICE_PREFIX);
    }

    private boolean isEnum(SpelNodeImpl spelNode) {
        return isPropertyNamed(spelNode, EnumsTypeProvider.ENUM_PREFIX);
    }

    private boolean isI18n(SpelNodeImpl spelNode) {
        return spelNode instanceof PropertyOrFieldReference && ((PropertyOrFieldReference) spelNode).getName().startsWith(MessagesTypeProvider.MESSAGE_HINT_PREFIX);
    }

    private boolean isPropertyNamed(SpelNodeImpl spelNode, String name) {
        return spelNode instanceof PropertyOrFieldReference && Objects.equals(((PropertyOrFieldReference) spelNode).getName(), name);
    }

    private void processNode(SpelNodeImpl node, StringBuilder expression, SpelNodeImpl parent, SpelNodeImpl previous, SpelNodeImpl next) {
        if (node instanceof CompoundExpression) {
            processNodes(unwrapCompoundExpression(node), expression, node);
        }
        else if (node instanceof PropertyOrFieldReference) {
            node = mapPropertyOrField((PropertyOrFieldReference) node, () -> getType(previous, parent));
            if (node instanceof PropertyOrFieldReference) {
                processPropertyOrField((PropertyOrFieldReference) node, expression, parent, previous, next);
            }
            else {
                processNode(node, expression, parent, previous, next);
            }
        }
        else if (node instanceof MethodReference) {
            node = mapMethod((MethodReference) node, () -> getType(previous, parent));
            if (node instanceof MethodReference) {
                processMethodReference((MethodReference) node, expression, parent, previous, next);
            }
            else {
                processNode(node, expression, parent, previous, next);
            }
        }
        else if (node instanceof OperatorNot) {
            processNotOperator((OperatorNot) node, expression, parent);
        }
        else if (node instanceof Operator) {
            processOperator((Operator) node, node.getChildCount() == 1, expression, parent);
        }
        else if (node instanceof Literal) {
            processLiteral((Literal) node, expression, parent);
        }
        else if (node instanceof Assign) {
            processAssignment((Assign) node, expression, parent);
        }
        else if (node instanceof ServiceNode) {
            processService((ServiceNode)node, expression, parent);
        }
        else if (node instanceof RuleNode) {
            processRule((RuleNode)node, expression, parent);
        }
        else if (node instanceof EnumValuesNode) {
            processEnumValues((EnumValuesNode)node, expression, parent);
        }
        else if (node instanceof EnumNode) {
            processEnum((EnumNode)node, expression, parent);
        }
        else if (node instanceof I18nNode) {
            processI18nMessage((I18nNode)node, expression, parent);
        }
        else if (node instanceof Ternary) {
            processTernary((Ternary)node, expression, parent);
        }
        else if (node instanceof Indexer) {
            processIndexer((Indexer)node, expression, parent);
        }
        else if (node instanceof TypeReference) {
            processTypeReference((TypeReference)node, expression, parent);
        }
        else {
            throw new FhException(String.format("Unsupported SPEL experssion: %s", node.toStringAST()));
        }
    }

    protected SpelNodeImpl mapMethod(MethodReference node, Supplier<Type> ownerType) {
        return node;
    }

    protected SpelNodeImpl mapPropertyOrField(PropertyOrFieldReference node, Supplier<Type> ownerType) {
        return node;
    }

    protected abstract Type getType(SpelNodeImpl node, SpelNodeImpl parent);

    private SpelNodeImpl getNext(List<SpelNodeImpl> spelNodes, int idx) {
        if (idx < spelNodes.size() - 1) {
            return spelNodes.get(idx + 1);
        }
        return null;
    }

    private SpelNodeImpl getPrevious(List<SpelNodeImpl> spelNodes, int idx) {
        if (idx > 0) {
            return spelNodes.get(idx - 1);
        }
        return null;
    }

    protected String getRootExpression() {
        return globalExpressionContext.getBindingRoot(ExpressionContext.DEFAULT_ROOT_SYMBOL).getExpression();
    }
}
