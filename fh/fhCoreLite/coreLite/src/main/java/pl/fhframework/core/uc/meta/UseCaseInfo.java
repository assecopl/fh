package pl.fhframework.core.uc.meta;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.FhException;
import pl.fhframework.core.uc.IUseCase;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.subsystems.Subsystem;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Information about use case
 */
@Getter
@Setter
public class UseCaseInfo {

    /**
     * Unique, fully qualified use case identifier
     */
    private String id;
    private String description;
    private boolean availableInMenu;
    private boolean isDynamic;
    private List<UseCaseActionInfo> actions = new ArrayList<>();
    private List<UseCaseActionInfo> eventsCallback = new ArrayList<>();
    private UseCaseActionInfo start;
    private String callbackClassStr;
    private Class callbackClass;
    private List<Class> callbackGenericParam = new ArrayList<>();
    private List<Class<? extends IUseCase>> implementedInterfaces = new ArrayList<>();
    private List<UseCaseActionInfo> exits = new ArrayList<>();
    private Subsystem subsystem;

    private Class<? extends IUseCase> clazz;
    private String urlAlias;
    private String defaultRemoteEvent;

    public Method getActionMethod(String actionName, String formTypeId) {
        List<UseCaseActionInfo> eventsNameMatch = getEventsCallback().stream().filter(eventInfo -> Objects.equals(eventInfo.getName(), actionName)).collect(Collectors.toList());

        if (eventsNameMatch.size() == 0) {
            return null;
        }
        else if (StringUtils.isNullOrEmpty(formTypeId) && eventsNameMatch.size() > 1) {
            throw new FhException("Event source form should be specified");
        }
        else {
            Optional<UseCaseActionInfo> eventCallback = eventsNameMatch.stream().filter(
                    eventInfo -> Objects.equals(eventInfo.getFormTypeId(), formTypeId) ||
                            StringUtils.isNullOrEmpty(eventInfo.getFormTypeId()) && StringUtils.isNullOrEmpty(formTypeId)).findAny();
            if (!eventCallback.isPresent() && StringUtils.isNullOrEmpty(formTypeId)) {
                return (Method) eventsNameMatch.get(0).getActionMethodHandler();
            }
            if (eventCallback.isPresent()) {
                return (Method) eventCallback.get().getActionMethodHandler();
            }
            else if (!StringUtils.isNullOrEmpty(formTypeId)){
                List<UseCaseActionInfo> eventCallbacks = eventsNameMatch.stream().filter(eventInfo -> StringUtils.isNullOrEmpty(eventInfo.getFormTypeId())).collect(Collectors.toList());
                if (eventCallbacks.size() == 1) {
                    return (Method) eventCallbacks.get(0).getActionMethodHandler();
                }
            }
            throw new FhException(String.format("'%s' event callback for form '%s' doesn't exists", actionName, formTypeId));
        }
    }

    public void addActionInfo(UseCaseActionInfo useCaseActionInfo) {
        getActions().add(useCaseActionInfo);
    }

    public void addEventCallback(UseCaseActionInfo eventCallbackInfo) {
        getEventsCallback().add(eventCallbackInfo);
    }

    public void addExitInfo(UseCaseActionInfo useCaseExitInfo) {
        getExits().add(useCaseExitInfo);
    }
}