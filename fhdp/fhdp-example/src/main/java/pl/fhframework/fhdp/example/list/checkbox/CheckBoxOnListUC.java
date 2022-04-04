package pl.fhframework.fhdp.example.list.checkbox;

import pl.fhframework.annotations.Action;
import pl.fhframework.core.uc.IInitialUseCase;
import pl.fhframework.core.uc.IUseCaseNoCallback;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.model.forms.AccessibilityEnum;
import pl.fhframework.validation.IValidationResults;

@UseCase
public class CheckBoxOnListUC implements IInitialUseCase {

    private CheckBoxOnTheListModel model;
    @Override
    public void start() {

        model = new CheckBoxOnTheListModel();
        model.getElements().add(
                new CheckBoxOnTheListModel.Wrapper(
                        new CheckBoxOnTheListModel.Element("Row 1", false, "Lorem"),
                        new CheckBoxOnTheListModel.Element("Row 1", false, "Lorem")
                )
        );
        model.getElements().add(
                new CheckBoxOnTheListModel.Wrapper(
                        new CheckBoxOnTheListModel.Element("$.test.property", false, "Lorem"),
                        new CheckBoxOnTheListModel.Element("Row 2", false, "Lorem ipsum")
                )
        );
        model.getElements().add(
                new CheckBoxOnTheListModel.Wrapper(
                        new CheckBoxOnTheListModel.Element("Row 3", true, ""),
                        new CheckBoxOnTheListModel.Element("Row 3", true, "")
                )
        );
        model.getElements().add(
                new CheckBoxOnTheListModel.Wrapper(
                        new CheckBoxOnTheListModel.Element("Row 4", false, ""),
                        new CheckBoxOnTheListModel.Element("Row 4", false, "et dolor?")
                )
        );
        model.getElements().add(
                new CheckBoxOnTheListModel.Wrapper(
                        new CheckBoxOnTheListModel.Element("Row whole crossed", false, ""),
                        new CheckBoxOnTheListModel.Element("Row whole crossed", false, "crossed?"),
                        true
                )
        );
        model.getElements().add(
                new CheckBoxOnTheListModel.Wrapper(
                        new CheckBoxOnTheListModel.Element("Row whole crossed[br/]with old/new", false, "new"),
                        new CheckBoxOnTheListModel.Element("Row whole crossed[br/]with old/new", false, "old"),
                        true
                )
        );

        model.getElements().add(
                new CheckBoxOnTheListModel.Wrapper(
                        new CheckBoxOnTheListModel.Element("Row whole crossed[br/]with old/new", null, "new"),
                        new CheckBoxOnTheListModel.Element("Row whole crossed[br/]with old/new", null, "old"),
                        false
                )
        );

        showForm(CheckBoxOnListForm.class, this.model);
    }
    @Action
    public void close() {
        exit();
    }

    @Action
    public void modifiedAction() {
        model.getSelectedElement().getElement().setModified(true);
        model.getSelectedElement().getElement().setAvailability(AccessibilityEnum.VIEW);
    }

    @Action
    public void checkModel() {
        runUseCase(CheckModelUC.class, model, new IUseCaseNoCallback() {
            @Override
            public void exitOnValidation(IValidationResults validationResultsList) {

            }
        });
    }

}
