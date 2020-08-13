package pl.fhframework.binding;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.events.ViewEvent;

import java.io.IOException;
import java.util.function.Function;

/**
 * Binding of a form's action
 */
@JsonSerialize(using = ActionBinding.ActionBindingSerializer.class)
@Getter
@Setter
public abstract class ActionBinding {

    public static class ActionBindingSerializer extends StdSerializer<ActionBinding> {

        public ActionBindingSerializer() {
            super(ActionBinding.class);
        }

        @Override
        public void serialize(ActionBinding actionBinding, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeString(actionBinding.getActionBindingExpression());
        }
    }

    public static final String EVENT_KEYWORD = "EVENT";
    public static final String EVENT_KEYWORD_OLD = "this";

    /**
     * Form's action argument
     */
    @Getter
    @Setter
    public static class ActionArgument {

        public ActionArgument() {
        }

        public ActionArgument(String bindingExpression, Function<ViewEvent<?>, Object> valueBinding) {
            this.bindingExpression = bindingExpression;
            this.valueBinding = valueBinding;
        }

        private String bindingExpression;

        // returns argument value, takes supplier of viewEvent as input (supplier is used / event is build only when it is necessary)
        private Function<ViewEvent<?>, Object> valueBinding;

        /**
         * Returns value of argument
         * @param eventBuilder builder of event object
         * @return value of argument
         */
        public Object getValue(ViewEvent<?> eventBuilder) {
            return valueBinding.apply(eventBuilder);
        }
    }

    private String actionBindingExpression;

    private String actionName;

    private ActionArgument[] arguments;

    public ActionBinding(String actionBindingExpression) {
        this.actionBindingExpression = actionBindingExpression.trim();
    }

}
