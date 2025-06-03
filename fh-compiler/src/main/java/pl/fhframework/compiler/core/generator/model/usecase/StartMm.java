package pl.fhframework.compiler.core.generator.model.usecase;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.fhframework.compiler.core.uc.dynamic.model.element.Start;

@Getter
@Setter
@NoArgsConstructor
public class StartMm extends ActionMm<Start> {
    public StartMm(Start start, UseCaseMm parent) {
        super(start, parent);
    }
}
