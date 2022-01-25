package pl.fhframework.compiler.core.generator.model.usecase;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.fhframework.compiler.core.generator.model.ParameterMm;
import pl.fhframework.compiler.core.uc.dynamic.model.element.command.Run;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class RunMm extends CommandMm<Run> implements Link {
    @JsonIgnore
    private boolean inDirection;

    public RunMm(Run command, UseCaseMm parent, boolean inDirection) {
        super(command, parent);
        this.inDirection = inDirection;
    }

    @JsonGetter
    public ElementInfo getTarget() {
        if (inDirection) {
            return null;
        }
        return getSourceOrTarget(command.getTarget(), parent);
    }

    @JsonGetter
    public ElementInfo getSource() {
        if (!inDirection) {
            return null;
        }
        return getSourceOrTarget(command.getParent(), parent);
    }

    @JsonGetter
    public List<ParameterMm> getParameters() {
        return command.getParameters().stream().map(ParameterMm::new).collect(Collectors.toList());
    }

    @Override
    @JsonGetter
    public String getId() {
        return command.getId();
    }
}
