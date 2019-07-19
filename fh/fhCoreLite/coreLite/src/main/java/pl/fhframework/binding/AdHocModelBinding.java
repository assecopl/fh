package pl.fhframework.binding;


import lombok.Setter;
import pl.fhframework.core.FhBindingException;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.BindingResult;
import pl.fhframework.annotations.RepeaterTraversable;
import pl.fhframework.model.forms.Component;
import pl.fhframework.model.forms.Form;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;

@RepeaterTraversable
public class AdHocModelBinding<V> extends ModelBinding<V> {

    protected Component owner;

    private Form form;

    /**
     * This array stores binding paths. More than one element is allowed only for combined
     * expressions (combined=true) - usually contains only one element If combined=true all
     * bindingPaths must refer to String values;
     */
    private String[] bindingPaths;
    /**
     * Indicates that this binding has no dynamic resolved value
     */
    @Getter
    private boolean staticValue;
    /**
     * Indicates does this binding contains static string values - if some text has occured in
     * bindingExpresion out of '{}'
     */
    @Getter
    private boolean combined;
    /**
     * This field is used only with combined=true. Stores static values
     */
    @Getter
    private String staticValueText;

    /**
     * This field is used only with combined=true. stores static and binding values
     */
    @Getter
    private List<CombinedExpression> combinedExpressions;

    /**
     * This field stores information about wrong binding expressions
     */
    @Getter
    private String bindingExpressionError;

    private AdHocModelBinding(String bindingExpression) {
        super(bindingExpression);
    }

    public AdHocModelBinding(Form form, Component owner, String bindingExpression) {
        super(bindingExpression);
        this.form = form;
        this.owner = owner;
        calculateBindingPaths();
    }

    public Form getForm() {
        return form;
    }

    public final static String STATIC_TEXT_PREFIX = "!!!";

    private void calculateBindingPaths() {
        if (getBindingExpression() == null || getBindingExpression().isEmpty()) {
            //if this is not expression its just static value no need to bind
            staticValue = true;
        } else if (getBindingExpression().startsWith(STATIC_TEXT_PREFIX)) {
            // LuK
            // static value that might contain '{'
            staticValue = true;
            staticValueText = getBindingExpression().substring(STATIC_TEXT_PREFIX.length());
        } else if (getUnescapedIndexOf(getBindingExpression(), '{', 0) == -1) {
            //if binding expression does not contain { it is also just a static value
            //no need to bind
            staticValue = true;
            staticValueText = unescapeBraces(getBindingExpression());
        } else {
            //if binding expression contains { and its not empty
            int startPos = getUnescapedIndexOf(getBindingExpression(), '{', 0);
            int endPos = getUnescapedIndexOf(getBindingExpression(), '}', startPos);
            //additional validation that someone did not type "abc}cde{"
            if (startPos > endPos) {
                bindingExpressionError = "Can't find end of binding expression!";
            } else {
                List<CombinedExpression> valuesAndBindings = new ArrayList<>();
                int bindingExpressionLength = getBindingExpression().length();

                if (endPos - startPos < bindingExpressionLength - 1) {
                    //So - we have combined expression.
                    combined = true;
                    int lastPos = 0;
                    while (lastPos < bindingExpressionLength) {
                        startPos = getUnescapedIndexOf(getBindingExpression(), '{', lastPos);
                        if (startPos >= 0) {
                            endPos = getUnescapedIndexOf(getBindingExpression(), '}', startPos);
                            if (endPos > startPos) {
                                processCombinedExpression(startPos, endPos, valuesAndBindings, lastPos);
                            } else {
                                bindingExpressionError = "Can't find end of binding expression in combined expression!";
                                break;
                            }
                        } else {
                            String staticValue = getBindingExpression().substring(lastPos);
                            valuesAndBindings.add(new CombinedExpression(unescapeBraces(staticValue), false));
                            break;
                        }
                        lastPos = endPos + 1;
                    }
                    if (bindingExpressionError == null) {
                        this.combinedExpressions = new ArrayList<>(valuesAndBindings);
                    }
                } else {
                    //We have simple binding expression
                    bindingPaths = new String[]{getBindingExpression().substring(1, endPos)};
                }
            }
        }
        if (bindingExpressionError != null) {
            FhLogger.error(bindingExpressionError);
        }
    }

    private void processCombinedExpression(int startPos, int endPos, List<CombinedExpression> valuesAndBindings, int lastPos) {
        String bindExp = getBindingExpression().substring(startPos + 1, endPos);
        String staticValue = getBindingExpression().substring(lastPos, startPos);

        valuesAndBindings.add(new CombinedExpression(unescapeBraces(staticValue), false));
        valuesAndBindings.add(new CombinedExpression(bindExp, true));

    }

    public boolean canChange() {
        return (combinedExpressions != null && combinedExpressions.stream().anyMatch(CombinedExpression::isBinding) || !combined) || (getBindingExpression() == null);
    }

    public void setValue(V value, Optional<String> formatter) {
        if (bindingExpressionError != null) {
            FhLogger.warn("Can't change broken binding expression: {}", this.bindingExpressionError);
        } else if (getBindingExpression() == null) {
            FhLogger.warn("Value has changed but there is no binding.");
        } else if (combined) {
            throw new FhBindingException("Can't change combined valued!");
        } else if (staticValue) {
            throw new FhBindingException("Can't change static value");
        } else {
            form.setModelValue(bindingPaths[0], value, formatter, owner);
        }
    }

    public BindingResult<V> getBindingResult() {
        try {
            if (bindingExpressionError != null) {
                return null;
            } else if (staticValue) {
                if (staticValueText != null) {
                    return new BindingResult(null, null, staticValueText);
                } else {
                    return null;
                }
            } else if (combined) {
                StringBuilder builder = new StringBuilder();
                for (CombinedExpression expression : combinedExpressions) {
                    if (expression.isBinding()) {
                        builder.append(form.getBindingResult(expression.getValue(), owner).getValue());
                    } else {
                        builder.append(expression.getValue());
                    }
                }
                return new BindingResult(null, null, builder.toString());
            } else {
                return form.getBindingResult(bindingPaths[0], owner);
            }
        } catch (Exception exc) {
            bindingExpressionError = "ERROR: " + exc.getMessage();
            FhLogger.error(bindingExpressionError);
            return null;
        }
    }

    @Getter
    @AllArgsConstructor
    public static class CombinedExpression {
        @Setter
        private String value;
        private boolean isBinding;
    }

    public AdHocModelBinding clone(Component newOwner) {
        AdHocModelBinding modelBindingClone = new AdHocModelBinding(getBindingExpression());
        modelBindingClone.form = form;
        if (combinedExpressions != null) {
            modelBindingClone.combinedExpressions = (List<CombinedExpression>) ((ArrayList<CombinedExpression>) combinedExpressions).clone();
        }
        if (bindingPaths != null) {
            modelBindingClone.bindingPaths = Arrays.copyOf(bindingPaths, bindingPaths.length);
        }
        modelBindingClone.staticValueText = staticValueText;
        modelBindingClone.combined = combined;
        modelBindingClone.staticValue = staticValue;
        modelBindingClone.bindingExpressionError = bindingExpressionError;
        modelBindingClone.owner = newOwner;
        return modelBindingClone;
    }

    private int getUnescapedIndexOf(String text, char character, int start) {
        while (true) {
            int currentLocation = text.indexOf(character, start);
            if (currentLocation == -1                                   // not found
                    || currentLocation == 0                             // found and cannot be escaped
                    || text.charAt(currentLocation - 1) != '\\') {      // found and is not escaped
                return currentLocation;
            }
            start = currentLocation + 1;
        }
    }

    private String unescapeBraces(String text) {
        return text.replace("\\{", "{").replace("\\}", "}");
    }
}
