package pl.fhframework.docs.uc;

import pl.fhframework.core.designer.IDocumentationUseCase;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.url.UseCaseWithUrl;
import pl.fhframework.docs.forms.component.ImageForm;
import pl.fhframework.docs.forms.component.model.ImageElement;
import pl.fhframework.annotations.Action;
import pl.fhframework.events.BreakLevelEnum;


/**
 * Use case supporting Image documentation
 */
@UseCase
public class ImageUC implements IDocumentationUseCase<ImageElement> {
    private ImageElement model;

    @Override
    public void start(ImageElement model) {
        this.model = model;
        showForm(ImageForm.class, model);
    }


    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void imageClicked() {
        ImageElement imageElement = model;
        imageElement.incrementCounter();
        imageElement.setOnClickedMessage("Component clicked " + imageElement.getCounter() + " times.");
    }

    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void imageSelectArea(String id){
        ImageElement imageElement = model;
        imageElement.setMap(id);
        imageElement.incrementAreaCounter();
        imageElement.setOnAreaClickedMessage("Component area clicked " + imageElement.getCounterArea() + " times.");
    }

}

