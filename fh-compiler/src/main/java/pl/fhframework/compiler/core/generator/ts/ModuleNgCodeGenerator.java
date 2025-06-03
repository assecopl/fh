package pl.fhframework.compiler.core.generator.ts;

import pl.fhframework.compiler.core.generator.AbstractNgClassCodeGenerator;
import pl.fhframework.compiler.core.generator.Dependency;
import pl.fhframework.compiler.core.generator.ModuleMetaModel;
import pl.fhframework.compiler.core.generator.MetaModelService;
import pl.fhframework.compiler.core.uc.ts.generator.AngularCore;
import pl.fhframework.compiler.core.uc.ts.generator.FhNgCore;

import java.util.stream.Collectors;

public class ModuleNgCodeGenerator extends AbstractNgClassCodeGenerator {
    private ModuleMetaModel moduleMetaModel;

    public ModuleNgCodeGenerator(ModuleMetaModel moduleMetaModel, MetaModelService metaModelService) {
        super(moduleMetaModel, Dependency.builder()
                        .module(moduleMetaModel.getModule())
                        .name(String.format("%s.FhNgModule", moduleMetaModel.getModule().getBasePackage()))
                        //.name(String.format("%s.%sModule", moduleMetaModel.getModule().getBasePackage(), JavaNamesUtils.getClassName(moduleMetaModel.getModule().getName())))
                        .build(),
                null, metaModelService);
        this.moduleMetaModel = moduleMetaModel;
    }

    @Override
    protected void generateClassBody() {
        generateRoutes();

        generateNgModule();

        generateFhModule();

        generateClassSignature();
    }


    private void generateRoutes() {
        addImport(AngularCore.Routes);
        classSignatureSection.addLine("const routes: %s = [", AngularCore.Routes.getName());
        addImport(FhNgCore.StandardLayout);
        moduleMetaModel.getForms().forEach(form -> {
            addImport(moduleMetaModel.getDependency(form.getId()));
            // todo: layout
            classSignatureSection.addLineWithIndent(1, "{path: '%s/view', component: %s, outlet: %s.MAIN_FORM},",
                    getBaseName(form.getId()), getBaseName(form.getId()), FhNgCore.StandardLayout.getName());
        });
        classSignatureSection.addLine("];");
    }

    private void generateNgModule() {
        classSignatureSection.addLineIfNeeded();
        addImport(AngularCore.NgModule);
        classSignatureSection.addLine("@%s({", AngularCore.NgModule.getName());
        classSignatureSection.addLineWithIndent(1, "imports: [");
        addImport(AngularCore.BrowserModule);
        classSignatureSection.addLineWithIndent(2, "%s,", AngularCore.BrowserModule.getName());
        addImport(AngularCore.FormsModule);
        classSignatureSection.addLineWithIndent(2, "%s,", AngularCore.FormsModule.getName());
        addImport(AngularCore.ReactiveFormsModule);
        classSignatureSection.addLineWithIndent(2, "%s,", AngularCore.ReactiveFormsModule.getName());
        addImport(AngularCore.NgbModule);
        classSignatureSection.addLineWithIndent(2, "%s,", AngularCore.NgbModule.getName());
        addImport(FhNgCore.NgBasicControlsModule);
        classSignatureSection.addLineWithIndent(2, "%s,", FhNgCore.NgBasicControlsModule.getName());
        addImport(FhNgCore.NgCoreModule);
        classSignatureSection.addLineWithIndent(2, "%s,", FhNgCore.NgCoreModule.getName());
        addImport(FhNgCore.NgDeafultAppModule);
        classSignatureSection.addLineWithIndent(2, "%s,", FhNgCore.NgDeafultAppModule.getName());
        addImport(AngularCore.RouterModule);
        classSignatureSection.addLineWithIndent(2, "%s.forRoot(routes),", AngularCore.RouterModule.getName());
        classSignatureSection.addLineWithIndent(1, "],");

        String forms = moduleMetaModel.getForms().stream().map(form -> getBaseName(form.getId())).collect(Collectors.joining(", "));
        classSignatureSection.addLineWithIndent(1, "declarations: [%s],", forms);
        classSignatureSection.addLineWithIndent(1, "exports: [%s, %s],", AngularCore.RouterModule.getName(), forms);
        classSignatureSection.addLine("})");
    }

    private void generateFhModule() {
        addImport(FhNgCore.FhModule);
        classSignatureSection.addLine("@%s({", FhNgCore.FhModule.getName());
        moduleMetaModel.getUseCases().forEach(uc -> addImport(moduleMetaModel.getDependency(uc.getId())));
        classSignatureSection.addLineWithIndent(1, "usecases: [%s]", moduleMetaModel.getUseCases().stream().map(uc -> getBaseName(uc.getId())).collect(Collectors.joining(", ")));
        classSignatureSection.addLine("})");
    }

    private void generateClassSignature() {
        classSignatureSection.addLine("export class %s", getBaseName(getClassDependency().getName()));
    }
}
