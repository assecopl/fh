package pl.fhframework.compiler.core.generator.model.usecase;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.uc.meta.UseCaseActionInfo;

@Getter
@Setter
public class StartInfo extends ActionInfo {
    public StartInfo(UseCaseActionInfo actionInfo) {
        super(actionInfo);
    }
}
