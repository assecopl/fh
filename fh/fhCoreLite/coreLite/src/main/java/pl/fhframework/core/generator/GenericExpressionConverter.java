package pl.fhframework.core.generator;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.expression.spel.SpelNode;
import org.springframework.expression.spel.ast.CompoundExpression;
import org.springframework.expression.spel.ast.MethodReference;
import org.springframework.expression.spel.ast.PropertyOrFieldReference;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Service;
import pl.fhframework.core.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by pawel.ruta on 2017-12-01.
 */
@Service
public class GenericExpressionConverter {
    SpelExpressionParser spelExpressionParser = new SpelExpressionParser();


    public String convertSymbolNames(String expression, String symbol, Function<String, String> mapper) {
        return convertSymbolNames(expression, symbol, mapper, false);
    }

    public String convertSymbolNames(String expression, String symbol, Function<String, String> mapper, boolean alwaysLast) {
        if (StringUtils.isNullOrEmpty(expression)) {
            return expression;
        } else {
            return convertSymbolNamesInner(expression, symbol, mapper, alwaysLast);
        }
    }

    private String convertSymbolNamesInner(String expression, String symbol, Function<String, String> mapper, boolean alwaysLast) {
        if (expression != null && !expression.isEmpty()) {
            StringBuilder expressionBuilder = new StringBuilder(expression);
            List<SymbolInExpression> symbolsList = searchCalledSymbols(expression, symbol, false, alwaysLast);
            Collections.reverse(symbolsList);
            for (SymbolInExpression symbolExp : symbolsList) {
                String newName = mapper.apply(symbolExp.getName());
                if (newName != null) {
                    expressionBuilder.replace(symbolExp.getPositionInExpression(), symbolExp.getPositionInExpression() + symbolExp.getName().length(), newName);
                }
            }
            expression = expressionBuilder.toString();
        }
        return expression;
    }

    public List<SymbolInExpression> searchCalledSymbols(String expression, String symbol, boolean forDependecies) {
        return searchCalledSymbols(expression, symbol, forDependecies, false);
    }

    public List<SymbolInExpression> searchCalledSymbols(String expression, String symbol, boolean forDependecies, boolean alwaysLast) {
        List<SymbolInExpression> symbolsList = new ArrayList<>();
        if (!StringUtils.isNullOrEmpty(expression)) {
            try {
                SpelNode node = spelExpressionParser.parseRaw(expression).getAST();
                searchSymbols(node, symbolsList, symbol, forDependecies, alwaysLast);
            } catch (Exception ignored) {
                // ignore - this may be user entered expression that should fail during validation - not explode during processing
            }
        }
        return symbolsList;
    }

    private void searchSymbols(SpelNode node, List<GenericExpressionConverter.SymbolInExpression> symbolsList, String symbol, boolean forDependecies, boolean alwaysLast) {
        // only if this is a dot-separated expression
        if (node instanceof CompoundExpression) {
            CompoundExpression compoundExpr = (CompoundExpression) node;
            if (compoundExpr.getChildCount() >= 2) {
                SpelNode firstChild = compoundExpr.getChild(0);

                // the first child in a dot-separated expression is 'symbol' (prefix)
                if (firstChild instanceof PropertyOrFieldReference
                        && symbol.equals(((PropertyOrFieldReference) firstChild).getName())) {
                    List<String> packageAndClassNameParts = new LinkedList<>();
                    int position = -1;

                    for (int i = 1; i < compoundExpr.getChildCount(); i++) {
                        SpelNode child = compoundExpr.getChild(i);
                        // for the first element remember position in the expression
                        if (position == -1) {
                            position = child.getStartPosition();
                        }
                        boolean constant = false;
                        boolean last = false;
                        String name = null;
                        if (child instanceof PropertyOrFieldReference) {
                            last = (i == compoundExpr.getChildCount() - 1);
                            constant = last && ((PropertyOrFieldReference) child).getName().matches("[A-Z_1-9]+");
                            last = last && alwaysLast;
                            name = ((PropertyOrFieldReference) child).getName();
                            if (!constant && !last) {
                                // add this package element
                                packageAndClassNameParts.add(((PropertyOrFieldReference) child).getName());
                                continue;
                            }
                        }
                        if (child instanceof MethodReference || constant || last) {
                            if (!constant && !last) {
                                name = ((MethodReference) child).getName();
                            }
                            // method element
                            if (!forDependecies) {
                                packageAndClassNameParts.add(name);
                            }
                            if (packageAndClassNameParts.size() > 0) {
                                symbolsList.add(new GenericExpressionConverter.SymbolInExpression(packageAndClassNameParts.stream().collect(Collectors.joining(".")), position, name));
                            }
                            break;
                        } else {
                            break; // no sense to go further
                        }
                    }
                }
            }
        }

        // go deeper
        for (int i = 0; i < node.getChildCount(); i ++) {
            searchSymbols(node.getChild(i), symbolsList, symbol, forDependecies, alwaysLast);
        }
    }

    @Data
    @AllArgsConstructor
    public static class SymbolInExpression {
        private String name;

        private int positionInExpression;

        private String simpleName;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SymbolInExpression that = (SymbolInExpression) o;

            if (name != null ? !name.equals(that.name) : that.name != null) return false;
            return simpleName != null ? simpleName.equals(that.simpleName) : that.simpleName == null;
        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + (simpleName != null ? simpleName.hashCode() : 0);
            return result;
        }
    }
}
