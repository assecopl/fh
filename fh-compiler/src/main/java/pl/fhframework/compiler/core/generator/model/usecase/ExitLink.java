package pl.fhframework.compiler.core.generator.model.usecase;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.fhframework.compiler.core.generator.model.ParameterMm;
import pl.fhframework.compiler.core.uc.dynamic.model.element.detail.UseCaseExit;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class ExitLink implements Link {
    @JsonIgnore
    private boolean inDirection;

    @JsonIgnore
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    private UseCaseExit useCaseExit;

    @JsonIgnore
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    private UseCaseMm parent;

    public ExitLink(UseCaseExit useCaseExit, UseCaseMm parent, boolean inDirection) {
        this.useCaseExit = useCaseExit;
        this.parent = parent;
        this.inDirection = inDirection;
    }

    @JsonGetter
    public String getLabel() {
        return useCaseExit.getFrom();
    }

    @JsonGetter
    public UseCaseInfoMm getSource() {
        if (!inDirection) {
            return null;
        }
        return new UseCaseInfoMm(parent.getUseCaseFeatures().getUseCasesInfo().get(useCaseExit.getParent().getRef()), useCaseExit.getParent());
    }

    @JsonGetter
    public ElementInfo getTarget() {
        if (inDirection) {
            return null;
        }
        return getSourceOrTarget(useCaseExit.getTarget(), parent);
    }

    @JsonGetter
    public List<ParameterMm> getParameters() {
        return useCaseExit.getParameters().stream().map(ParameterMm::new).collect(Collectors.toList());
    }

    @Override
    @JsonGetter
    public String getId() {
        return useCaseExit.getId();
    }

    @Override
    public <T> T provideImpl() {
        return (T) useCaseExit;
    }
}
