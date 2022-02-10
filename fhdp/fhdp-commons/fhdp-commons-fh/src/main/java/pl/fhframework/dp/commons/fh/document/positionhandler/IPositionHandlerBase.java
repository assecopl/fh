package pl.fhframework.dp.commons.fh.document.positionhandler;

import pl.fhframework.core.uc.IUseCase;

public interface IPositionHandlerBase<MODEL, POSITION> {
    void onSelectedItem(Object parent);
    void onAddItem(IUseCase useCase, MODEL model);
    void onEditItem(IUseCase useCase, MODEL model);
    void setSelectedItem(POSITION POSITION);
}
