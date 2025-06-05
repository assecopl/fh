package pl.fhframework.docs.dynamic_content;

import pl.fhframework.core.security.annotations.SystemFunction;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.IInitialUseCase;
import pl.fhframework.core.uc.url.UseCaseWithUrl;
import pl.fhframework.core.util.ComponentsUtils;
import pl.fhframework.docs.DocsSystemFunction;
import pl.fhframework.docs.dynamic_content.model.DynamicContentModel;
import pl.fhframework.annotations.Action;
import pl.fhframework.binding.AdHocActionBinding;
import pl.fhframework.binding.AdHocModelBinding;
import pl.fhframework.binding.CompiledBinding;
import pl.fhframework.model.forms.*;
import pl.fhframework.model.forms.Component;
import pl.fhframework.model.forms.messages.Messages;
import pl.fhframework.events.ViewEvent;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by krzysztof.kobylarek on 2016-12-28.
 */
@UseCase
@UseCaseWithUrl(alias = "docs-dynamic-content")
//@SystemFunction(DocsSystemFunction.FH_DOCUMENTATION_VIEW)
public class DynamicContentUC implements IInitialUseCase {

    private DynamicContentForm dynamicContentForm = null;
    private DynamicContentModel model = new DynamicContentModel();
    private static String PARENT_GROUP_NAME = "placementGroup";
    private static final AtomicInteger ID_SEQ = new AtomicInteger(1);

    @Override
    public void start() {
        dynamicContentForm = showForm(DynamicContentForm.class, model);
    }

    @Action
    public void onDynamicContentAddComponent(ViewEvent viewEvent) {
        PanelGroup panelGroup = (PanelGroup) ComponentsUtils.find(viewEvent.getSourceForm(), PARENT_GROUP_NAME);
        panelGroup.addSubcomponent(createComponent(viewEvent.getSourceForm(), panelGroup));
    }

    @Action
    public void onDynamicContentRemoveComponent(ViewEvent viewEvent) {
        PanelGroup panelGroup = (PanelGroup) ComponentsUtils.find(viewEvent.getSourceForm(), PARENT_GROUP_NAME);
        if (panelGroup.getSubcomponents().size() > 0) {
            panelGroup.getSubcomponents().remove(panelGroup.getSubcomponents().size() - 1);
        } else {
            Messages.showInfoMessage(getUserSession(), "Placement group is empty");
        }

    }

    @Action
    public void onDynamicContentAdd3Components(ViewEvent viewEvent) {
        PanelGroup panelGroup = (PanelGroup) ComponentsUtils.find(viewEvent.getSourceForm(), PARENT_GROUP_NAME);
        for (int i = 0; i < 3; ++i) {
            panelGroup.addSubcomponent(createComponent(viewEvent.getSourceForm(), panelGroup));
        }
    }

    @Action
    public void onDynamicContentAdd_1Begining_2Middle_Components(ViewEvent viewEvent) {
        PanelGroup panelGroup = (PanelGroup) ComponentsUtils.find(viewEvent.getSourceForm(), PARENT_GROUP_NAME);
        {
            FormElement component = createComponent(viewEvent.getSourceForm(), panelGroup);
            ((LinkedList) panelGroup.getSubcomponents()).addFirst(component);
        }
        {
            FormElement component = createComponent(viewEvent.getSourceForm(), panelGroup);
            int size = ((LinkedList) panelGroup.getSubcomponents()).size();
            ((LinkedList) panelGroup.getSubcomponents()).add((int) Math.ceil(size / 2), component);
        }
        {
            FormElement component = createComponent(viewEvent.getSourceForm(), panelGroup);
            ((LinkedList) panelGroup.getSubcomponents()).addLast(component);
        }
    }

    @Action
    public void onDynamicContentAdd_1Begining_2End_Components(ViewEvent viewEvent) {
        PanelGroup panelGroup = (PanelGroup) ComponentsUtils.find(viewEvent.getSourceForm(), PARENT_GROUP_NAME);
        {
            FormElement component = createComponent(viewEvent.getSourceForm(), panelGroup);
            ((LinkedList) panelGroup.getSubcomponents()).addFirst(component);
        }
        {
            FormElement component = createComponent(viewEvent.getSourceForm(), panelGroup);
            ((LinkedList) panelGroup.getSubcomponents()).addLast(component);
        }
        {
            FormElement component = createComponent(viewEvent.getSourceForm(), panelGroup);
            ((LinkedList) panelGroup.getSubcomponents()).addLast(component);
        }
    }

    @Action
    public void onDynamicContentRemove3Components(ViewEvent viewEvent) {
        PanelGroup panelGroup = (PanelGroup) ComponentsUtils.find(viewEvent.getSourceForm(), PARENT_GROUP_NAME);
        if (panelGroup.getSubcomponents().size() == 0) {
            Messages.showInfoMessage(getUserSession(), "Placement group is empty");
        } else if (panelGroup.getSubcomponents().size() <= 3) {
            panelGroup.getSubcomponents().removeAll(panelGroup.getSubcomponents());
        } else {
            LinkedList temp = new LinkedList(panelGroup.getSubcomponents());
            for (int i = 0; i < 3; i++) {
                panelGroup.getSubcomponents().remove(temp.pollLast());
            }
        }
    }

    @Action
    public void onDynamicContentRemoveAll(ViewEvent viewEvent) {
        PanelGroup panelGroup = (PanelGroup) ComponentsUtils.find(viewEvent.getSourceForm(), PARENT_GROUP_NAME);
        if (panelGroup.getSubcomponents() != null && panelGroup.getSubcomponents().size() > 0) {
            panelGroup.getSubcomponents().clear();
        } else {
            Messages.showInfoMessage(getUserSession(), "Placement group is empty");
        }
    }

    @Action
    public void onDynamicContentChangeComponentType(ViewEvent viewEvent) {
        ;
    }

    @Action
    public void onAddGroup(ViewEvent viewEvent) {
        Form<?> form = viewEvent.getSourceForm();
        PanelGroup panelGroup = (PanelGroup) ComponentsUtils.find(form, PARENT_GROUP_NAME);

        PanelGroup newPanelGroup = new PanelGroup(viewEvent.getSourceForm());
        newPanelGroup.init();

        newPanelGroup.getSubcomponents().add(createComponent(form, panelGroup, Optional.of(DynamicContentModel.ChosenType.OUTPUTLABEL)));
        newPanelGroup.getSubcomponents().add(createComponent(form, panelGroup, Optional.of(DynamicContentModel.ChosenType.OUTPUTLABEL)));
        newPanelGroup.getSubcomponents().add(createComponent(form, panelGroup, Optional.of(DynamicContentModel.ChosenType.INPUTTEXT)));

        panelGroup.getSubcomponents().add(newPanelGroup);
    }

    private static String generateId(Component component) {
        return component.getClass().getSimpleName() + ID_SEQ.getAndIncrement();
    }

    private FormElement createComponent(Form form, IGroupingComponent<Component> parent, Optional<DynamicContentModel.ChosenType> type) {
        FormElement component = null;
        if (type.isPresent()) {
            switch (type.get()) {
                case OUTPUTLABEL:
                    OutputLabel outputLabel = new OutputLabel(form);
                    outputLabel.setId(generateId(outputLabel));
                    outputLabel.setValueBindingAdHoc("I'm OutputLabel " + outputLabel.getId());
                    outputLabel.setWidth("md-4");
                    component = outputLabel;
                    break;
                case SELECT_ONE_MENU:
                    SelectOneMenu selectOneMenu = new SelectOneMenu(form);
                    selectOneMenu.setId(generateId(selectOneMenu));
                    selectOneMenu.setListBinding(new CompiledBinding<>(List.class, model::getSelectOneMenuValues));
                    selectOneMenu.setModelBindingAdHoc("{selectOneMenuValue}");
                    selectOneMenu.setLabelModelBindingAdHoc("I'm SelectOneMenu " + selectOneMenu.getId());
                    selectOneMenu.setOnChange(new AdHocActionBinding("onChangeEvent(this)", form, selectOneMenu));
                    selectOneMenu.setWidth("md-4");
                    selectOneMenu.refreshElementToForm();
                    selectOneMenu.init();
                    component = selectOneMenu;
                    break;
                case CHECKBOX:
                    CheckBox checkBox = new CheckBox(form);
                    checkBox.setId(generateId(checkBox));
                    checkBox.setModelBindingAdHoc("{checkboxValue}");
                    checkBox.setLabelModelBindingAdHoc("I'm CheckBox" + checkBox.getId());
                    checkBox.setOnChange(new AdHocActionBinding("onChangeEvent(this)", form, checkBox));
                    checkBox.refreshElementToForm();
                    checkBox.init();
                    component = checkBox;
                    break;
                case INPUTTEXT:
                    InputText inputText = new InputText(form);
                    inputText.setId(generateId(inputText));
                    inputText.setModelBindingAdHoc("{inputTextValue}");
                    inputText.setLabelModelBindingAdHoc("I'm InputText " + inputText.getId());
                    inputText.setOnInput(new AdHocActionBinding("onInputEvent(this)", form, inputText));
                    inputText.setWidth("md-4");
                    inputText.refreshElementToForm();
                    inputText.init();
                    component = inputText;
                    break;
                case REPEATER:
                    Repeater repeater = new Repeater(form);
                    repeater.setWidth("md-12");
                    repeater.setCollection(new CompiledBinding<>(List.class, model::getIterations));
                    repeater.setIterator("iter");
                    repeater.setInteratorComponentFactory((thisRepeater, rowNumberOffset, index) -> {
                        OutputLabel outputLabel2 = new OutputLabel(form);
                        outputLabel2.setId(generateId(outputLabel2));
                        outputLabel2.setValueBinding(
                                new CompiledBinding<>(String.class,
                                        () -> "I'm OutpuLabel inside Repeater (iteration " + model.getIterations().get(index).getLabel() + ") + " + outputLabel2.getId()));
                        outputLabel2.setGroupingParentComponent(repeater);
                        outputLabel2.setWidth("md-4");
                        outputLabel2.init();
                        return Arrays.asList(outputLabel2);
                    });

                    repeater.setGroupingParentComponent(parent);
                    repeater.init();
                    component = repeater;
                    break;
                case GROUP_IN_REPEATER:
                    Repeater repeaterAndGroup = new Repeater(form);
                    repeaterAndGroup.setCollection(new AdHocModelBinding<>(form, repeaterAndGroup, "{iterations}"));
                    repeaterAndGroup.setIterator("iter");
                    repeaterAndGroup.setGroupingParentComponent(parent);
                    repeaterAndGroup.setWidth("md-12");
                    repeaterAndGroup.init();

                    OutputLabel outputLabel_repeaterWithGroup = new OutputLabel(form);
                    outputLabel_repeaterWithGroup.setId(generateId(outputLabel_repeaterWithGroup));
                    outputLabel_repeaterWithGroup.setValueBindingAdHoc("I'm OutpuLabel inside Repeater (iteration {iter.label}) " + outputLabel_repeaterWithGroup.getId());
                    outputLabel_repeaterWithGroup.setWidth("md-4");
                    repeaterAndGroup.addSubcomponent(outputLabel_repeaterWithGroup);
                    outputLabel_repeaterWithGroup.setGroupingParentComponent(repeaterAndGroup);
                    outputLabel_repeaterWithGroup.init();

                    PanelGroup panelGroup_repeaterWithPanelGroup = new PanelGroup(form);
                    panelGroup_repeaterWithPanelGroup.setLabelModelBinding(panelGroup_repeaterWithPanelGroup.createAdHocModelBinding("I'm PanelGroup replicated inside Repeater"));
                    panelGroup_repeaterWithPanelGroup.setGroupingParentComponent(repeaterAndGroup);
                    panelGroup_repeaterWithPanelGroup.init();

                    InputText inputText_grupa_repeaterInGroup = new InputText(form);
                    //inputText_grupa_repeaterInGroup.setBinding("");
                    inputText_grupa_repeaterInGroup.setId(generateId(inputText_grupa_repeaterInGroup));
                    inputText_grupa_repeaterInGroup.setModelBindingAdHoc("-");
                    inputText_grupa_repeaterInGroup.setLabelModelBindingAdHoc("I'm InputText added to PanelGroup replicated by Repeater (iteration {iter.label}) " + inputText_grupa_repeaterInGroup.getId());
                    inputText_grupa_repeaterInGroup.setGroupingParentComponent(panelGroup_repeaterWithPanelGroup);
                    inputText_grupa_repeaterInGroup.setWidth("md-4");
                    inputText_grupa_repeaterInGroup.init();
                    panelGroup_repeaterWithPanelGroup.addSubcomponent(inputText_grupa_repeaterInGroup);

                    repeaterAndGroup.addSubcomponent(panelGroup_repeaterWithPanelGroup);

                    component = repeaterAndGroup;
                    break;
                case GROUP:
                    PanelGroup runtimePanelGroup = new PanelGroup(form);
                    runtimePanelGroup.setLabelModelBinding(runtimePanelGroup.createAdHocModelBinding("I'm PanelGroup created in runtime"));
                    runtimePanelGroup.init();

                    InputText inputText_runtimeGroup = new InputText(form);
                    inputText_runtimeGroup.setId(generateId(inputText_runtimeGroup));
                    inputText_runtimeGroup.setModelBindingAdHoc("-");
                    inputText_runtimeGroup.setLabelModelBindingAdHoc("I'm InputText added to PanelGroup created in runtime " + inputText_runtimeGroup.getId());
                    inputText_runtimeGroup.setGroupingParentComponent(runtimePanelGroup);
                    inputText_runtimeGroup.setWidth("md-4");
                    inputText_runtimeGroup.init();

                    runtimePanelGroup.getSubcomponents().add(inputText_runtimeGroup);
                    runtimePanelGroup.setGroupingParentComponent(parent);

                    component = runtimePanelGroup;
                    break;
                case REPEATER_IN_REPEATER:
                    Repeater outerRepeater = new Repeater(form);
                    outerRepeater.setWidth("md-12");
                    outerRepeater.setCollection(new CompiledBinding<>(List.class, model::getIterations));
                    outerRepeater.setIterator("iter");

                    outerRepeater.setInteratorComponentFactory((thisOuterRepeater, rowNumberOffset, index) -> {

                        OutputLabel outputLabel1_outerRepeater = new OutputLabel(form);
                        outputLabel1_outerRepeater.setId(generateId(outputLabel1_outerRepeater));
                        outputLabel1_outerRepeater.setValueBinding(
                                new CompiledBinding<>(String.class,
                                        () -> index + ". I'm OutputLabel in Repeater with another nested Repeater (iteration "
                                                + model.getIterations().get(index).getLabel() // get element from collection
                                                + " ) " + outputLabel1_outerRepeater.getId()));
                        outputLabel1_outerRepeater.setWidth("md-4");
                        outputLabel1_outerRepeater.setGroupingParentComponent(outerRepeater);
                        outputLabel1_outerRepeater.init();

                        Repeater innerRepeater = new Repeater(form);
                        innerRepeater.setWidth("md-12");
                        innerRepeater.setCollection(new CompiledBinding<>(List.class, () -> model.getIterations().get(index).getIterations()));
                        innerRepeater.setIterator("iter2");
                        innerRepeater.setInteratorComponentFactory((thisInnerRepeater, rowNumberOffsetInner, indexInner) -> {
                            OutputLabel outputLabel1_innerRepeater = new OutputLabel(form);
                            outputLabel1_innerRepeater.setId(generateId(outputLabel1_innerRepeater));
                            outputLabel1_innerRepeater.setWidth("md-4");
                            outputLabel1_innerRepeater.setValueBinding(
                                    new CompiledBinding<>(String.class,
                                            () -> index + "." + indexInner + ". I'm OutputLabel of Repeater nested inside Repeater (iteration "
                                                    + model.getIterations().get(index).getIterations().get(indexInner).getLabel()  // get element from inner collection
                                                    + ") " + outputLabel1_innerRepeater.getId()));
                            outputLabel1_innerRepeater.setGroupingParentComponent(innerRepeater);
                            outputLabel1_innerRepeater.init();
                            return Arrays.asList(outputLabel1_innerRepeater);
                        });
                        innerRepeater.setGroupingParentComponent(outerRepeater);
                        innerRepeater.init();

                        return Arrays.asList(outputLabel1_outerRepeater, innerRepeater);
                    });
                    outerRepeater.setGroupingParentComponent(parent);
                    outerRepeater.init();

                    component = outerRepeater;
                    break;
            }
        }

        return component;
    }

    private FormElement createComponent(Form<DynamicContentModel> form, IGroupingComponent parent) {
        return createComponent(form, parent, form.getModel().getComponentChosenType());
    }

    @Action
    public void onInputEvent(ViewEvent viewEvent) {
        Messages.showInfoMessage(getUserSession(), "onInput event triggered");
    }

    @Action
    public void onChangeEvent(ViewEvent viewEvent) {
        Messages.showInfoMessage(getUserSession(), "onChange event triggered");
    }
}
