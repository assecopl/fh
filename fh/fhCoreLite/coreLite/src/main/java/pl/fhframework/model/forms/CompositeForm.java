package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.fhframework.core.events.OnEvent;
import pl.fhframework.core.uc.UseCaseContainer;
import pl.fhframework.ReflectionUtils;
import pl.fhframework.annotations.Action;
import pl.fhframework.annotations.composite.FireEvent;
import pl.fhframework.model.dto.ValueChange;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by krzysztof.kobylarek on 2017-01-13.
 */
public class CompositeForm<T> extends Form<T> {


    private Map<String, OnEvent> xmlHandledEventsMap = null;
    private Map<String, MethodContext> compositeActionMethods = new HashMap<>();
    private List<OnEvent> registeredEvents = new ArrayList<>();

    public CompositeForm() {
        org.springframework.util.ReflectionUtils.doWithMethods(
                this.getClass(),
                (method) -> compositeActionMethods.put(method.getName(), new MethodContext(method, method.getAnnotation(Action.class), method.getAnnotation(FireEvent.class))),
                (method) -> method.isAnnotationPresent(Action.class) && Modifier.isPublic(method.getModifiers())
        );
    }

    public void runAction(String action, Object... arguments) {

        MethodContext actionMethodContext = compositeActionMethods.get(action);
        boolean actionInvoked = false;
        if (actionMethodContext != null && actionMethodContext.actionMethod != null) {
            actionInvoked = getAbstractUseCase().runAction(actionMethodContext.actionMethod, this, action, arguments);
        }

        if (actionInvoked && actionMethodContext.fireEventAnnotation != null) {
            FireEvent fireEvent = actionMethodContext.fireEventAnnotation;
            for (String fireEventHandlerName : fireEvent.name()) {
                registeredEvents.stream().filter(event -> Objects.equals(fireEventHandlerName, event.getEventName())).forEach(event -> {
                    event.getForm().getAbstractUseCase().runAction(event.getEventHandler() != null ? event.getEventHandler() : event.getEventName(), ReflectionUtils.getClassName(this.getClass()), arguments);
                });
            }
        }
    }

    public void setEvents(List<OnEvent> eventList) {
        this.xmlHandledEventsMap = eventList.stream().collect(Collectors.toMap(OnEvent::getEventName, Function.identity()));
    }

    public void addRegisteredEvents(List<OnEvent> eventList) {
        registeredEvents.addAll(eventList);
    }

    @JsonIgnore
    public Form<?> getEventProcessingForm() {
        return this;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    private static class MethodContext {
        private Method actionMethod;
        private Action actionAnnotation;
        private FireEvent fireEventAnnotation;
    }

}



