package pl.fhframework.compiler.core.generator.model.usecase;

import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.fhframework.compiler.core.uc.dynamic.model.element.RunUseCase;
import pl.fhframework.compiler.core.uc.dynamic.model.element.TransactionTypeEnum;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class RunUseCaseMm extends Element<RunUseCase> {

    public RunUseCaseMm(RunUseCase runUseCase, UseCaseMm parent) {
        super(runUseCase, parent);
    }

    @JsonGetter
    public UseCaseInfoMm getUseCaseInfo() {
        return new UseCaseInfoMm(parent.getUseCaseFeatures().getUseCasesInfo().get(element.getRef()), this.getElement());
    }

    @JsonGetter
    public List<ExitLink> getExitLinks() {
        return element.getExits().stream().map(link -> new ExitLink(link, parent, false)).collect(Collectors.toList());
    }

    @JsonGetter
    public TransactionTypeEnum getTransactionType() {
        return element.getTransactionType();
    }
}
