package pl.fhframework.model.forms.messages;

import pl.fhframework.core.messages.Action;
import pl.fhframework.events.ViewEvent;

import java.util.function.Consumer;

/**
 * Helper class used to group together Button and its action to produce message dialog
 * Created by krzysztof.kobylarek on 2016-10-24.
 */

public class ActionButton {

    private Properties actionButtonProperties = null;

    private ActionButton(Properties actionButtonProperties){
        this.actionButtonProperties = actionButtonProperties;
    }

    public String getButtonLabel(){
        return actionButtonProperties.buttonLabel;
    }

    public String getButtonId(){
        return actionButtonProperties.buttonId;
    }

    public Action getAction(){
        return actionButtonProperties.action;
    }

    public String getButtonStyle() {
        return actionButtonProperties.buttonStyle;
    }

    public Consumer<? super ViewEvent> getViewEventAction(){
        return actionButtonProperties.viewEventAction;
    }

    public static Builder builder(){
        return new Builder();
    }

    public static ActionButton get(String buttonLabel, Action action){
        return builder().withAction(action).withButtonLabel(buttonLabel).build();
    }

    public static ActionButton get(String buttonLabel, String btnStyle, Action action){
        return builder().withAction(action)
                .withButtonLabel(buttonLabel)
                .withButtonStyle(btnStyle)
                .build();
    }

    public static ActionButton get(String buttonLabel, Consumer<?super ViewEvent> viewEventAction){
        return builder().withViewEventAction(viewEventAction).withButtonLabel(buttonLabel).build();
    }

    public static ActionButton get(String buttonLabel, String btnStyle, Consumer<?super ViewEvent> viewEventAction){
        return builder()
                .withViewEventAction(viewEventAction)
                .withButtonLabel(buttonLabel)
                .withButtonStyle(btnStyle)
                .build();
    }

    public static ActionButton getClose(String buttonLabel){
        return builder().withButtonLabel(buttonLabel).withViewEventAction(Messages::close).build();
    }

    private static class Properties {
        private String buttonLabel;
        private String buttonId;
        private Action action;
        private String buttonStyle;
        private Consumer<? super ViewEvent> viewEventAction;
    }

    public static class Builder {
        private Properties properties = new Properties();

        public Builder withButtonId(String buttonId){
            this.properties.buttonId=buttonId;
            return this;
        }
        public Builder withButtonLabel(String buttonLabel){
            this.properties.buttonLabel=buttonLabel;
            return this;
        }
        public Builder withAction(Action action){
            this.properties.action=action;
            return this;
        }
        public Builder withViewEventAction(Consumer<? super ViewEvent> viewEventAction){
            this.properties.viewEventAction=viewEventAction;
            return this;
        }
        public Builder withButtonStyle(String buttonStyle){
            this.properties.buttonStyle = buttonStyle;
            return this;
        }

        public ActionButton build(){
            return new ActionButton(properties);
        }
    }
}
