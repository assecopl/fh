package pl.fhframework.compiler.core.generator.model.usecase;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.fhframework.compiler.core.uc.dynamic.model.element.Finish;

@Getter
@Setter
@NoArgsConstructor
public class FinishMm extends ActionMm<Finish> {
    public FinishMm(Finish fin, UseCaseMm parent) {
        super(fin, parent);
    }

    public boolean isDiscardChanges() {
        return element.isDiscard();
    }
}
