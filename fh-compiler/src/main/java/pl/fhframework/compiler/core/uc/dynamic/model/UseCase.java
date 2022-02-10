package pl.fhframework.compiler.core.uc.dynamic.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import pl.fhframework.compiler.core.uc.dynamic.model.element.attribute.Parameter;
import pl.fhframework.compiler.core.uc.dynamic.model.element.attribute.ParameterDefinition;
import pl.fhframework.compiler.core.uc.dynamic.model.element.behaviour.Linkable;
import pl.fhframework.compiler.core.uc.dynamic.model.element.command.*;
import pl.fhframework.compiler.core.uc.dynamic.model.element.command.detail.ActionLink;
import pl.fhframework.compiler.core.uc.dynamic.model.element.detail.UseCaseExit;
import pl.fhframework.aspects.snapshots.model.ISnapshotEnabled;
import pl.fhframework.aspects.snapshots.model.SkipSnapshot;
import pl.fhframework.compiler.core.uc.dynamic.model.element.*;

import javax.xml.bind.annotation.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Representation of usecase which is used to be saved as xml in dynamic use case.
 */
@Getter
@Setter
@NoArgsConstructor
@XmlRootElement(name = "UseCase")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"id", "label", "description", "declarations", "flow", "repository", "permissions", "layout"})
public class UseCase implements ISnapshotEnabled, Cloneable {

    @XmlAttribute
    @XmlID
    private String id;

    @XmlAttribute
    private String label;

    @XmlAttribute
    private String description;

    @XmlAttribute
    private String url;

    @XmlAttribute
    private int idSequence;

    @XmlElement(name = "Flow")
    private Flow flow = new Flow();

    @XmlElement(name = "Repository")
    private Repository repository = new Repository();

    @XmlTransient
    private UseCaseReference useCaseReference;

    @XmlTransient
    @SkipSnapshot
    private UseCaseCache useCaseCache;

    @XmlElements({
            @XmlElement(name = "Model", type = Model.class)
    })
    @XmlElementWrapper(name = "Declarations")
    private List<Model> declarations = new LinkedList<>();

    @XmlElements({
            @XmlElement(name = "Permission", type = Permission.class)
    })
    @XmlElementWrapper(name = "Permissions")
    private List<Permission> permissions = new LinkedList<>();

    @XmlAttribute
    private String layout;

    @XmlTransient
    @SkipSnapshot
    private Boolean notAdded;

    @XmlTransient
    @SkipSnapshot
    private List<String> notChanged = new LinkedList<>();

    public UseCase(UseCaseReference useCaseReference) {
        this.useCaseReference = useCaseReference;
        this.label = useCaseReference.getName();
        this.description = useCaseReference.getDescription();
    }

    public void addModelDeclaration(Model model) {
        if (declarations == null) {
            declarations = new LinkedList<>();
        }
        declarations.add(model);
    }

    /**
     * @return Start params, all Exits params (also duplicates)
     */
    public List<ParameterDefinition> getParameters() {
        List<ParameterDefinition> parameters = new LinkedList<>();

        if (useCaseCache.getStart() != null) {
            parameters.addAll(useCaseCache.getStart().getParameterDefinitions());
        }

        useCaseCache.getExits().forEach(finish -> {
            parameters.addAll(finish.getParameterDefinitions());
        });

        return parameters;
    }

    /**
     * @return Start params, all Exits params (also duplicates), UC model
     */
    public List<ParameterDefinition> getParametersAndModel() {
        List<ParameterDefinition> vars = getParameters();

        vars.addAll(declarations);

        return vars;
    }

    private UseCase(UseCase other) {
        this.id = other.id;
        this.label = other.label;
        this.description = other.description;
        if (other.flow != null) {
            this.flow = (Flow) other.flow.clone();
        }
        if (other.useCaseReference != null) {
            this.useCaseReference = (UseCaseReference) other.useCaseReference.clone();
        }
        if (!CollectionUtils.isEmpty(other.declarations)) {
            this.declarations = new LinkedList<>();
            other.declarations.stream().forEach(model -> this.declarations.add((Model) model.clone()));
        }
        if (!CollectionUtils.isEmpty(other.permissions)) {
            this.permissions = new LinkedList<>();
            other.permissions.stream().forEach(permission -> this.permissions.add((Permission) permission.clone()));
        }
        postLoad();
    }

    @Override
    public Object clone() {
        return copyOf();
    }

    @SkipSnapshot
    public UseCase copyOf() {
        return new UseCase(this);
    }

    public int nextIdSequence() {
        return ++idSequence;
    }

    public void postLoad() {
        getFlow().getUseCaseElements().forEach(useCaseElement -> useCaseElement.setParent(getFlow()));

        final List<Action> actions = getFlow().getUseCaseElements().stream().filter(e -> e instanceof Action).map(e -> (Action) e).collect(Collectors.toList());
        for (Action action : actions) {
            final List<Command> commands = action.getCommands();
            for (Command command : commands) {
                command.setParent(action);
                if (command instanceof ShowForm) {
                    final ShowForm showForm = (ShowForm) command;
                    if (showForm.getActionLinks() != null) {
                        showForm.getActionLinks().forEach(actionLink -> actionLink.setParent(showForm));
                    }
                }
            }
            action.getParameterDefinitions().forEach(parameterDefinition -> parameterDefinition.setParent(action));
        }

        final List<RunUseCase> runUseCases = getFlow().getUseCaseElements().stream().filter(e -> e instanceof RunUseCase).map(e -> (RunUseCase) e).collect(Collectors.toList());
        for (RunUseCase runUseCase : runUseCases) {
            final List<UseCaseExit> useCaseExits = runUseCase.getExits();
            if (useCaseExits != null) {
                for (UseCaseExit exit : useCaseExits) {
                    exit.setParent(runUseCase);
                }
            }
        }

        getRepository().getFunctions().forEach(function -> function.setParent(getRepository()));

        // todo: DataRead remove
        convertDataQuery();

        rebuildUseCaseCache();
    }

    private void convertDataQuery() {
        Map<String, StoreAccess> stores = flow.getUseCaseElements().stream().filter(StoreAccess.class::isInstance).map(StoreAccess.class::cast).collect(Collectors.toMap(StoreAccess::getId, Function.identity()));
        flow.getUseCaseElements().stream().filter(Action.class::isInstance).map(Action.class::cast).forEach(action -> {
            ListIterator<Command> commandIterator = action.getCommands().listIterator();
            while (commandIterator.hasNext()) {
                Command command = commandIterator.next();
                if (Run.class.isInstance(command)) {
                    Run run = Run.class.cast(command);
                    if (run.getRef() != null && stores.get(run.getRef()) != null) {
                        commandIterator.remove();

                        CallFunction callFunction = new CallFunction();
                        StoreAccess storeAccess = stores.get(run.getRef());
                        callFunction.setRef(ActivityTypeEnum.DataRead.name());
                        callFunction.setReturnHolder(run.getReturnHolder());

                        Parameter parameter = new Parameter();
                        parameter.setName("object");
                        parameter.setValue(storeAccess.getSelect());
                        callFunction.getParameters().add(parameter);

                        parameter = new Parameter();
                        parameter.setName("where");
                        parameter.setValue(storeAccess.getQueryClause());

                        callFunction.getParameters().add(parameter);

                        commandIterator.add(callFunction);
                        callFunction.setParent(action);
                    }
                }
            }
        });

        flow.getUseCaseElements().removeIf(StoreAccess.class::isInstance);
    }

    /**
     * Get available holders for left side assignment.
     *
     * @return Start params, set of Exits params, UC model
     */
    public List<ParameterDefinition> getAvailableHolders() {

        List<ParameterDefinition> vars = new LinkedList<>();

        if (getUseCaseCache().getStart() != null) {
            vars.addAll(getUseCaseCache().getStart().getParameterDefinitions());
        }

        Set<String> names = new HashSet<>();
        getUseCaseCache().getExits().forEach(finish -> {
            finish.getParameterDefinitions().forEach(elem -> {
                if (!names.contains(elem.getName())) {
                    vars.add(elem);
                    names.add(elem.getName());
                }
            });
        });
        vars.addAll(declarations);

        return vars;
    }

    public UseCaseCache getUseCaseCache() {
        if (useCaseCache == null) {
            rebuildUseCaseCache();
        }
        return useCaseCache;
    }

    public void rebuildUseCaseCache() {
        useCaseCache = new UseCaseCache();

        for (UseCaseElement useCaseElement : getFlow().getUseCaseElements()) {
            if (!StringUtils.isEmpty(useCaseElement.getId())) {
                getUseCaseCache().addElement(useCaseElement);
            }
            if (useCaseElement instanceof Action) {
                Action action = (Action) useCaseElement;
                if (action.getCommands() != null) {
                    for (Command command : action.getCommands()) {
                        getUseCaseCache().addCommand(command);
                        if (command instanceof Run && !StringUtils.isEmpty(((Run) command).getId())) {
                            getUseCaseCache().addElement((Run) command);
                        } else if (command instanceof ShowForm) {
                            if (((ShowForm) command).getActionLinks() != null) {
                                for (ActionLink actionLink : ((ShowForm) command).getActionLinks()) {
                                    if (!StringUtils.isEmpty(actionLink.getId())) {
                                        getUseCaseCache().addElement(actionLink);
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (useCaseElement instanceof RunUseCase) {
                RunUseCase runUseCase = (RunUseCase) useCaseElement;
                runUseCase.getExits().forEach(useCaseExit -> {
                    getUseCaseCache().addElement(useCaseExit);
                });
            }
        }

        getRepository().getFunctions().forEach(function -> useCaseCache.addFunction(function));

        useCaseCache.values().stream().filter(Linkable.class::isInstance).map(Linkable.class::cast).forEach(linkable -> {
            linkable.setTarget((UseCaseElement) useCaseCache.getElement(linkable.getTargetId()));
        });
    }
}
