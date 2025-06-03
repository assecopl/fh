package pl.fhframework.compiler.core.generator.model.usecase;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.compiler.core.generator.ModuleMetaModel;
import pl.fhframework.compiler.core.generator.model.MetaModel;
import pl.fhframework.compiler.core.uc.dynamic.model.UseCase;
import pl.fhframework.compiler.core.uc.dynamic.model.element.*;
import pl.fhframework.compiler.core.uc.dynamic.model.element.attribute.ParameterDefinition;
import pl.fhframework.compiler.core.uc.dynamic.model.element.behaviour.Linkable;
import pl.fhframework.compiler.core.uc.dynamic.model.element.command.Run;
import pl.fhframework.compiler.core.uc.dynamic.model.element.command.ShowForm;
import pl.fhframework.compiler.core.uc.dynamic.model.element.command.detail.ActionLink;
import pl.fhframework.compiler.core.uc.dynamic.model.element.detail.UseCaseExit;
import pl.fhframework.compiler.core.uc.service.UseCaseFeaturesHolder;
import pl.fhframework.compiler.core.uc.service.UseCaseServiceImpl;
import pl.fhframework.binding.ActionSignature;
import pl.fhframework.core.FhException;
import pl.fhframework.core.uc.meta.UseCaseInfo;
import pl.fhframework.helper.AutowireHelper;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class UseCaseMm extends MetaModel {
    @JsonIgnore
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    private UseCase useCase;

    @JsonIgnore
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    private ModuleMetaModel metadata;

    @Autowired
    @JsonIgnore
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    UseCaseServiceImpl useCaseService;

    @JsonIgnore
    @Setter(AccessLevel.PROTECTED)
    private UseCaseFeaturesHolder useCaseFeatures;

    public UseCaseMm(UseCase useCase, List<String> dependencies, ModuleMetaModel metadata) {
        setDependencies(dependencies);
        this.useCase = useCase;
        this.useCase.rebuildUseCaseCache();
        this.metadata = metadata;
        AutowireHelper.autowire(this, useCaseService); // todo MavenClassCompiler is outside of spring
        this.useCaseService.fillParameters(useCase);
    }

    @JsonGetter
    public String getId() {
        return useCase.getId();
    }

    @JsonGetter
    public StartMm getStart() {
        return new StartMm(useCase.getUseCaseCache().getStart(), this);
    }

    @JsonGetter
    public List<FinishMm> getExits() {
        return useCase.getUseCaseCache().getExits().stream().map(exit -> new FinishMm(exit, this)).collect(Collectors.toList());
    }

    @JsonGetter
    public List<ActionMm> getActions() {
        return useCase.getUseCaseCache().getElementIdMapper().values().stream().
                filter(element -> element.getClass().isAssignableFrom(Action.class)).
                map(Action.class::cast).
                map(action -> ActionMm.getInstance(action, this)).collect(Collectors.toList());
    }

    @JsonGetter
    public List<RunUseCaseMm> getRunUsecases() {
        return useCase.getUseCaseCache().getElementIdMapper().values().stream().
                filter(RunUseCase.class::isInstance).
                map(RunUseCase.class::cast).
                map(ruc -> new RunUseCaseMm(ruc, this)).
                collect(Collectors.toList());
    }

    @JsonGetter
    public List<Model> getDataModel() {
        return useCase.getDeclarations();
    }

    @JsonGetter
    public List<ParameterDefinition> getInputParams() {
        return getStart().getParameterDefinitions();
    }

    @JsonGetter
    public List<ShowFormMm> getShownForms() {
        return useCase.getUseCaseCache().getCommands().stream().
                filter(ShowForm.class::isInstance).
                map(ShowForm.class::cast).
                map(sf -> new ShowFormMm(sf, this)).
                collect(Collectors.toList());
    }

    /**
     * Returns all forms events without link to usecase action
     *
     * @return Map<Map<action name, form id>, action signature>
     */
    @JsonGetter
    public Map<Map.Entry<String, String>, ActionSignature> getNotAddedEvents() {
        return useCaseService.getNotAddedEvents(useCase);
    }

    @JsonIgnore
    protected UseCaseFeaturesHolder getUseCaseFeatures() {
        if (useCaseFeatures == null) {
            useCaseFeatures = useCaseService.getUseCaseContext(useCase);
        }

        return useCaseFeatures;
    }

    @Override
    public <T> T provideImpl() {
        return (T) useCase;
    }
}

interface Link extends WithExpression {
    @JsonIgnore
    default ElementInfo getSourceOrTarget(UseCaseElement target, UseCaseMm parent) {
        if (target instanceof Action) {
            UseCaseInfo useCaseInfo = parent.useCaseService.getUseCaseInfo(parent.getUseCase().getId(), parent.getUseCase());
            if (target instanceof Start) {
                if (Objects.equals(useCaseInfo.getStart().getId(), target.getId())) {
                    return new StartInfo(useCaseInfo.getStart());
                }
                throw new FhException("Unknown start with id: " + target.getId());
            }
            else if (target instanceof Finish) {
                return new FinishInfo(useCaseInfo.getExits().stream().filter(info -> Objects.equals(info.getId(), target.getId())).findFirst().get());
            }

            return new ActionInfo(parent.useCaseService.getUseCaseInfo(parent.getUseCase().getId(), parent.getUseCase()).getActions().stream().filter(info -> Objects.equals(info.getId(), target.getId())).findFirst().get());
        }
        else {
            return new UseCaseInfoMm(parent.getUseCaseFeatures().getUseCasesInfo().get(((RunUseCase)target).getRef()), target);
        }
    }

    static Link getInstance(Linkable<?> linkable, UseCaseMm parent, boolean inDirection) {
        if (linkable instanceof ActionLink) {
            return new ActionLinkMm((ActionLink) linkable, parent, inDirection);
        }
        else if (linkable instanceof UseCaseExit) {
            return new ExitLink((UseCaseExit) linkable, parent, inDirection);
        }
        else if (linkable instanceof Run) {
            return new RunMm((Run) linkable, parent, inDirection);
        }

        throw new FhException(new IllegalArgumentException());
    }

    String getId();
}

