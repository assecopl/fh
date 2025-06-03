package pl.fhframework.compiler.forms;

import lombok.Getter;
import pl.fhframework.compiler.core.dynamic.DynamicClassArea;
import pl.fhframework.compiler.core.generator.AbstractNgClassCodeGenerator;
import pl.fhframework.compiler.core.generator.MetaModelService;
import pl.fhframework.compiler.core.generator.ModuleMetaModel;
import pl.fhframework.compiler.core.generator.model.form.FormMm;
import pl.fhframework.compiler.core.uc.dynamic.model.element.attribute.ParameterDefinition;
import pl.fhframework.compiler.core.uc.ts.generator.AdHocTsClassCodeGenerator;
import pl.fhframework.compiler.core.uc.ts.generator.AngularCore;
import pl.fhframework.compiler.core.uc.ts.generator.FhNgCore;
import pl.fhframework.core.uc.dynamic.model.element.attribute.TypeMultiplicityEnum;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.model.forms.Form;

import java.util.List;
import java.util.Optional;

public class FormsNgClassGenerator extends AbstractNgClassCodeGenerator { // non-spring class - it is also used in non-spring context
    public static final String MODEL_CLASS_NAME = "Model";

    public static final String EVENTS_INTERFACE_NAME = "Events";

    private FormMm form;

    @Getter
    private String classBaseName;

    public <M, F extends Form<M>> FormsNgClassGenerator(FormMm form, ModuleMetaModel moduleMetaModel, MetaModelService metaModelService) {
        super(moduleMetaModel, moduleMetaModel.getDependency(form.getId()), form, metaModelService); // todo: currently id is name (without package)
        this.form = form;
        this.classBaseName = getBaseName(form.getId());
    }

    @Override
    protected void generateClassBody() {
        generateClassDeclaration();

        generateConstructor();

        generateNgInit();

        generateFields();

        generateModel();

        generateEvents();
    }

    protected void generateClassDeclaration() {
        addImport(FhNgCore.Form);
        addImport(AngularCore.Component);

        classSignatureSection.addLine("@%s", FhNgCore.Form.getName());
        classSignatureSection.addLine("@%s({", AngularCore.Component.getName());
        classSignatureSection.addLineWithIndent(1, "selector: '%s',", classBaseName.toLowerCase());
        classSignatureSection.addLineWithIndent(1, "templateUrl: './%s.form.html',", classBaseName);
        classSignatureSection.addLineWithIndent(1, "styleUrls: ['./%s.form.css']", classBaseName);
        classSignatureSection.addLine("})", AngularCore.Component.getName());

        String modelType = String.format("%s.%s", classBaseName, MODEL_CLASS_NAME);
        if (!form.isInternalModel()) {
            addImport(form.getModelType());
            modelType = getBaseName(form.getModelType());
        }

        addImport(FhNgCore.IForm);
        addImport(AngularCore.OnInit);

        classSignatureSection.addLine("export class %s extends %s<%s, %s.%s> implements %s, %s.%s",
                classBaseName, FhNgCore.IForm.getName(),
                modelType,
                classBaseName, EVENTS_INTERFACE_NAME,
                AngularCore.OnInit.getName(),
                classBaseName, EVENTS_INTERFACE_NAME);
    }

    protected void generateConstructor() {
        addImport(AngularCore.FormBuilder);
        String rules = provideInjection(form.getDependencies(), DynamicClassArea.RULE);
        constructorSignatureSection.addLine("constructor(formBuilder: %s%s)", AngularCore.FormBuilder.getName(), StringUtils.isNullOrEmpty(rules) ? "" : ", " + rules);
        constructorSection.addLine("super(formBuilder);");
    }

    private void generateNgInit() {
        methodSection.addLine("ngOnInit(): void {");
        methodSection.addLine("}");
    }

    private void generateFields() {
    }

    private void generateModel() {
        if (form.isInternalModel()) {
            AdHocTsClassCodeGenerator modelClass = new AdHocTsClassCodeGenerator(moduleMetaModel, null, getMetaModelService());
            modelClass.getClassSignatureSection().addLineWithIndent(1, "export class %s", MODEL_CLASS_NAME);

            form.getInternalModel().forEach(property -> {
                ParameterDefinition field = new ParameterDefinition(property.getType(), getFieldName(property.getName()), TypeMultiplicityEnum.from(property.getMultiplicity()));
                addImport(field);
                modelClass.getFieldSection().addLine("%s: %s = undefined;", field.getName(), getType(field));
            });

            getNamespaceSection().addLineIfNeeded();
            getNamespaceSection().addLine(modelClass.generateClass(false).trim());

            addImports(modelClass);
        }
    }

    private void generateEvents() {
        AdHocTsClassCodeGenerator eventsInterface = new AdHocTsClassCodeGenerator(moduleMetaModel, null, getMetaModelService());
        addImport(FhNgCore.FormEvents);
        eventsInterface.getClassSignatureSection().addLineWithIndent(1, "export interface %s extends %s", EVENTS_INTERFACE_NAME, FhNgCore.FormEvents.getName());

        form.getEvents().forEach(event -> {
            List<ParameterDefinition> arguments = event.getArguments();
            generateMethodSignature(eventsInterface.getMethodSection(), event.getActionName(), arguments,null, true);

            getMethodSection().addLineIfNeeded();
            generateMethodSignature(getMethodSection(), event.getActionName(), arguments,null);
            getMethodSection().addLine("}");
        });

        getNamespaceSection().addLineIfNeeded();
        getNamespaceSection().addLine(eventsInterface.generateClass(false).trim());

        addImports(eventsInterface);
    }

    @Override
    protected Optional<String> getNamespaceName() {
        return Optional.of(classBaseName);
    }
}
