package pl.fhframework.example;

import pl.fhframework.binding.ActionSignature;
import pl.fhframework.binding.CompiledActionBinding;
import pl.fhframework.binding.StaticBinding;
import pl.fhframework.model.forms.*;
import pl.fhframework.model.forms.attribute.FormModalSize;
import pl.fhframework.model.forms.attribute.FormType;
import pl.fhframework.model.forms.attribute.IconAlignment;

import java.util.LinkedHashSet;
import java.util.Set;

public class FhDemoForm extends Form<String> {
    public static final Set<ActionSignature> ____actions = new LinkedHashSet<>();
    static {
        ____actions.add(new ActionSignature("onClose"));
    }

    public FhDemoForm() {
        initComponents();
    }

    /** Initializes form components */
    private void initComponents() {
        this.setLabelModelBinding(new StaticBinding<>("FH Demo Use Case"));
        this.setDeclaredContainer("mainForm");
        this.setHideHeader(false);
        this.setFormType(FormType.STANDARD);
        this.setModalSize(FormModalSize.REGULAR);
        this.setXmlns("http://fh.asseco.com/form/1.0");
        this.setId("fHDemoForm");
        this.setInvisible(false);

        OutputLabel outputLabel = new OutputLabel(this);
        this.addSubcomponent(outputLabel);
        outputLabel.setGroupingParentComponent(this);
        initOutputLabel(outputLabel);

        Row row = new Row(this);
        this.addSubcomponent(row);
        row.setGroupingParentComponent(this);
        initRow(row);

        Button button = new Button(this);
        this.addSubcomponent(button);
        button.setGroupingParentComponent(this);
        initButton(button);

        Model model = new Model(this);
        this.setModelDefinition(model);
        initModel(model);
    }

    /** Initializes output label component */
    private void initOutputLabel(OutputLabel outputLabel) {
        outputLabel.setIconAlignment(IconAlignment.BEFORE);
        outputLabel.setValueBinding(new StaticBinding<>("[b]This is FH Demo use case...  [color=\'RED\'][icon=\'fas fa-thumbs-up\'][/color][/b]"));
        outputLabel.setWidth("md-12");
        outputLabel.setInvisible(false);
        outputLabel.setGroupingParentComponent(this);
    }

    /** Initializes row component */
    private void initRow(Row row) {
        row.setHeight("20px");
        row.setWidth("md-12");
        row.setInvisible(false);
        row.setGroupingParentComponent(this);
    }

    /** Initializes button component */
    private void initButton(Button button) {
        button.setOnClick(new CompiledActionBinding(
                "onClose", "onClose"));
        button.setLabelModelBinding(new StaticBinding<>("Close"));
        button.setWidth("md-2");
        button.setId("exitButton");
        button.setInvisible(false);
        button.setGroupingParentComponent(this);
    }

    /** Initializes form model */
    private void initModel(Model model) {
        model.setInvisible(false);
    }

}
