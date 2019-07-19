package pl.fhframework.core.uc.meta;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Executable;
import java.util.LinkedList;
import java.util.List;

/**
 * Describes one action in use case
 */
@Getter
@Setter
public class UseCaseActionInfo {

    private String id;
    private String name;
    private String formTypeId;
    private String description;
    private List<ActionInfo> actionStepsInfo;
    private Executable actionMethodHandler;
    private List<ParameterInfo> parameters = new LinkedList<>();
    private String parametersClassWraper;
    private boolean remoteEnabled;
}
