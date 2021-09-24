package pl.fhframework.binding;

import org.springframework.expression.ParseException;
import org.springframework.expression.spel.ast.MethodReference;
import org.springframework.expression.spel.ast.PropertyOrFieldReference;
import org.springframework.expression.spel.ast.SpelNodeImpl;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import pl.fhframework.core.FhFormException;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.BindingResult;
import pl.fhframework.model.forms.Component;
import pl.fhframework.model.forms.Form;
import pl.fhframework.events.ViewEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Ad hoc binding of a form's action
 */
public class AdHocActionBinding extends ActionBinding {

    private static final SpelExpressionParser PARSER = new SpelExpressionParser(); // thread-safe instance

    private Form form;

    private Component owner;

    protected class AdHocArgumentValueBinding implements Function<ViewEvent<?>, Object> {

        private String valueExpression;

        private AdHocModelBinding<?> modelBinding;

        public AdHocArgumentValueBinding(String valueExpression) {
            this.valueExpression = valueExpression;
            this.modelBinding = new AdHocModelBinding<>(form, owner, valueExpression);
        }

        @Override
        public Object apply(ViewEvent<?> viewEvent) {
            if ((EVENT_KEYWORD + ".optionalValue").equals(valueExpression)) { // before implementation of action binding this was a # character
                return viewEvent.getOptionalValue();
            } else if (EVENT_KEYWORD.equals(valueExpression)
                    || EVENT_KEYWORD_OLD.equals(valueExpression)) { // backward compatibility with lowercase this as event
                return viewEvent;
            } else {
                BindingResult<?> bindingResult = form.getBindingResult(valueExpression, owner);
                return bindingResult != null ? bindingResult.getValue() : null;
            }
        }
    }

    public AdHocActionBinding(String actionBindingExpression, Form form, Component owner) {
        super(actionBindingExpression);
        this.form = form;
        this.owner = owner;
    }

    public ActionArgument[] getArguments() {
        if (super.getArguments() == null) {
            processExpression();
        }
        return super.getArguments();
    }

    public String getActionName() {
        if (super.getActionName() == null) {
            processExpression();
        }
        return super.getActionName();
    }

    protected void processExpression() {
        if ("-".equals(getActionBindingExpression()) || "+".equals(getActionBindingExpression())) {
            setActionName(getActionBindingExpression());
            setArguments(new ActionArgument[0]);
            return;
        }
        if (StringUtils.isNullOrEmpty(getActionBindingExpression())) {
            setActionName("");
            setArguments(new ActionArgument[0]);
            return;
        }

        // parse expression and extract arguments
        try {
            SpelNodeImpl parsedExpression = (SpelNodeImpl) PARSER.parseRaw(getActionBindingExpression()).getAST();
            if (parsedExpression instanceof PropertyOrFieldReference) {
                // simple action call without arguments
                PropertyOrFieldReference propertyExpression = (PropertyOrFieldReference) parsedExpression;
                setActionName(propertyExpression.getName());
                setArguments(new ActionArgument[0]);
            } else if (parsedExpression instanceof MethodReference) {
                // method calls
                MethodReference methodExpression = (MethodReference) parsedExpression;
                setActionName(methodExpression.getName());

                List<ActionArgument> arguments = new ArrayList<>();
                for (int i = 0; i < methodExpression.getChildCount(); i++) {
                    String expression = methodExpression.getChild(i).toStringAST().trim();
                    if (expression.startsWith("(") && expression.endsWith(")")) {
                        expression = expression.substring(1, expression.length() - 1);
                    }
                    expression = expression.replace(".[", "["); // work-around for changing collection[index] to collection.[index]
                    expression = StringUtils.removeSurroundingBraces(expression); // backward compatibility with expresions like action({argument})
                    ActionArgument arg = new ActionArgument();
                    arg.setBindingExpression(expression);
                    arg.setValueBinding(new AdHocArgumentValueBinding(expression));
                    arguments.add(arg);
                }
                setArguments(arguments.toArray(new ActionArgument[arguments.size()]));
            } else {
                throw new FhFormException("Invalid action expression '" + getActionBindingExpression() + "' type: " + parsedExpression.getClass().getSimpleName());
            }
        } catch (ParseException e) {
            throw new FhFormException("Invalid action expression '" + getActionBindingExpression() + "': " + e.getMessage(), e);
        }
    }
}
