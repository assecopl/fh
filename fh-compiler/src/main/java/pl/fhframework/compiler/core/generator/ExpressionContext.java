package pl.fhframework.compiler.core.generator;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Piotr on 2017-02-20.
 */
public class ExpressionContext {

    public static final String DEFAULT_ROOT_SYMBOL = "__DEFAULT_ROOT__";

    private Map<String, AbstractExpressionProcessor.InputAccessorExpression> bindingRoots = new LinkedHashMap<>();

    private ExpressionContext parent;

    public ExpressionContext() {
    }

    public ExpressionContext(ExpressionContext parent) {
        this.parent = parent;
    }

    public void addBindingRoot(String symbol, String getterExpression, Type type) {
        bindingRoots.put(symbol, AbstractExpressionProcessor.InputAccessorExpression.createProperty(getterExpression, null, type));
    }

    public void addTwoWayBindingRoot(String symbol, String getterExpression, Type type) {
        bindingRoots.put(symbol, AbstractExpressionProcessor.InputAccessorExpression.createTwoWayProperty(getterExpression, null, type));
    }

    public void addBindingRootAsParameter(String parameterName, Type type) {
        bindingRoots.put(parameterName, AbstractExpressionProcessor.InputAccessorExpression.createParameter(parameterName, type));
    }

    public void addBindingRootAsParameterWithOtherName(String parameterName, Type type, String... aliases) {
        AbstractExpressionProcessor.InputAccessorExpression param = AbstractExpressionProcessor.InputAccessorExpression.createParameter(parameterName, type);
        for (String alias : aliases) {
            bindingRoots.put(alias, param);
        }
    }

    public void addAll(ExpressionContext expressionContext) {
        this.bindingRoots.putAll(expressionContext.bindingRoots);
    }

    public void setDefaultBindingRoot(String getterExpression, Type type) {
        addBindingRoot(DEFAULT_ROOT_SYMBOL, getterExpression, type);
    }

    public void setDefaultBindingRootAsParameter(String parameterName, Type type) {
        bindingRoots.put(DEFAULT_ROOT_SYMBOL, AbstractExpressionProcessor.InputAccessorExpression.createParameter(parameterName, type));
    }

    public boolean hasBindingRoot(String symbol) {
        return bindingRoots.containsKey(symbol) || (parent != null && parent.hasBindingRoot(symbol));
    }

    public AbstractExpressionProcessor.InputAccessorExpression getDefaultBindingRoot() {
        return getBindingRoot(DEFAULT_ROOT_SYMBOL);
    }

    public AbstractExpressionProcessor.InputAccessorExpression getBindingRoot(String symbol) {
        if (bindingRoots.containsKey(symbol)) {
            return bindingRoots.get(symbol);
        } else if (parent != null) {
            return parent.getBindingRoot(symbol);
        } else {
            throw new RuntimeException("Not a binding root symbol: " + symbol);
        }
    }

    public AbstractExpressionProcessor.InputAccessorExpression[] getAllParametersAsArray() {
        List<AbstractExpressionProcessor.InputAccessorExpression> params = getAllParameters();
        return params.toArray(new AbstractExpressionProcessor.InputAccessorExpression[params.size()]);
    }

    public List<AbstractExpressionProcessor.InputAccessorExpression> getAllParameters() {
        List<AbstractExpressionProcessor.InputAccessorExpression> params = new ArrayList<>();
        if (parent != null) {
            params.addAll(parent.getAllParameters());
        }
        params.addAll(bindingRoots.values().stream().filter(AbstractExpressionProcessor.InputAccessorExpression::isPassedAsParameter).distinct().collect(Collectors.toList()));
        return params;
    }
}
