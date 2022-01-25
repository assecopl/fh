package pl.fhframework.dp.commons.fh.uc.stub;


import pl.fhframework.dp.commons.fh.uc.FhdpBaseUC;
import pl.fhframework.annotations.Action;
import pl.fhframework.core.uc.IInitialUseCase;
import pl.fhframework.core.uc.UseCase;

@UseCase
public class StubUC extends FhdpBaseUC implements IInitialUseCase {

    @Override
    public void start() {
        super.sideBarManagement(true);
        showForm(StubForm.class, "AA");
    }

    @Action
    public void close() {
        exit();
    }
}
