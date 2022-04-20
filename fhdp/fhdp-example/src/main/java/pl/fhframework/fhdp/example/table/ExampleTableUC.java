package pl.fhframework.fhdp.example.table;

import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.annotations.Action;
import pl.fhframework.core.uc.IInitialUseCase;
import pl.fhframework.core.uc.IUseCaseSaveCancelCallback;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.event.EventRegistry;
import pl.fhframework.model.forms.AccessibilityEnum;

import java.util.ArrayList;
import java.util.List;

@UseCase
public class ExampleTableUC implements IInitialUseCase {

    @Autowired
    EventRegistry eventRegistry;

    private ExampleTableModel model;

    @Override
    public void start() {
        this.initDefaultModel();
        showForm(ExampleTableForm.class, model);
    }

    private void initDefaultModel() {
        this.model = new ExampleTableModel();
        List<ExampleTableModel.AttributeElement> attributeElementList = new ArrayList<>();
        attributeElementList.add(new ExampleTableModel.AttributeElement(1, "Test 1"));
        attributeElementList.add(new ExampleTableModel.AttributeElement(2, "Test 2"));

        List<ExampleTableModel.AttributeElement> attributeElementList2 = new ArrayList<>();
        attributeElementList2.add(new ExampleTableModel.AttributeElement(1, "Test 1"));

        List<ExampleTableModel.ListElements> listElementsList = new ArrayList<>();
        listElementsList.add(
                new ExampleTableModel.ListElements(1, "Element 1", "Description element 1", attributeElementList));
        listElementsList.add(new ExampleTableModel.ListElements(2, "Element 2", "Description element 2",
                attributeElementList2));

        this.model.setListElements(listElementsList);
    }

    @Action
    public void close() {
        exit();
    }

    @Action
    public void addBaseList() {
        ExampleTableModel.ListElements listElements = new ExampleTableModel.ListElements();
        this.model.setSelectedListElement(listElements);
        this.openBaseModal(false);
    }

    @Action
    public void editBaseList() {
        this.openBaseModal(true);
    }

    @Action
    public void viewBaseListByRow(ExampleTableModel.ListElements listElements) {
        this.model.setSelectedListElement(listElements);
        this.openBaseModal(true);
    }

    @Action(value = "viewBaseListByRowAccessibility", validate = false)
    public void viewBaseListByRow(ExampleTableModel.ListElements listElements, String accessibilityEnum) {
        this.model.setAccessibilityEnum(AccessibilityEnum.valueOf(accessibilityEnum));
        viewBaseListByRow(listElements);
    }

    @Action
    public void removeBaseList(ExampleTableModel.ListElements listElements) {
        model.getListElements().remove(listElements);
    }

    @Action
    public void addAttributes(ExampleTableModel.ListElements listElements) {
        this.model.setSelectedListElement(listElements);
        ExampleTableModel.AttributeElement attributeElement = new ExampleTableModel.AttributeElement();
        this.model.setSelectedAttributeElement(attributeElement);
        this.openAtttributeModal(false);
    }

    @Action
    public void editAttributes(ExampleTableModel.ListElements listElements) {
        this.model.setSelectedListElement(listElements);
        this.openAtttributeModal(true);
    }

    @Action
    public void viewAttributesListByRow(ExampleTableModel.AttributeElement attributeElement, ExampleTableModel.ListElements listElements) {
        this.model.setSelectedListElement(listElements);
        this.model.setSelectedAttributeElement(attributeElement);
        this.openAtttributeModal(true);
    }

    @Action(value = "viewAttributesListByRowAccessibility", validate = false)
    public void viewAttributesListByRow(ExampleTableModel.AttributeElement attributeElement, ExampleTableModel.ListElements listElements, String accessibilityEnum) {
        this.model.setAccessibilityEnum(AccessibilityEnum.valueOf(accessibilityEnum));
        viewAttributesListByRow(attributeElement, listElements);
    }

    @Action
    public void removeAttributes(ExampleTableModel.AttributeElement attributeElement, ExampleTableModel.ListElements listElements) {
        this.model.setSelectedListElement(listElements);
        model.getSelectedListElement().getAttributeElementList().remove(attributeElement);
    }

    private void openBaseModal(boolean isEdit) {
        runUseCase(ExampleTableBaseUC.class, model, new IUseCaseSaveCancelCallback<ExampleTableModel>() {
            @Override
            public void save(ExampleTableModel one) {
                if(!isEdit)
                    model.getListElements().add(model.getSelectedListElement());
            }

            @Override
            public void cancel() {
                model.setAccessibilityEnum(AccessibilityEnum.EDIT);
            }
        });
    }

    private void openAtttributeModal(boolean isEdit) {
        runUseCase(ExampleTableAttributeUC.class, model, new IUseCaseSaveCancelCallback<ExampleTableModel>() {
            @Override
            public void save(ExampleTableModel one) {
                if(!isEdit)
                    model.getSelectedListElement().getAttributeElementList().add(one.getSelectedAttributeElement());
            }

            @Override
            public void cancel() {

            }
        });
    }

}
