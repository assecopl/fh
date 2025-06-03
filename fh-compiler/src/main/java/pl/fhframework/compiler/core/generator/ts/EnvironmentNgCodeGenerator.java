package pl.fhframework.compiler.core.generator.ts;

import pl.fhframework.compiler.core.dynamic.DynamicClassArea;
import pl.fhframework.compiler.core.generator.AbstractNgClassCodeGenerator;
import pl.fhframework.compiler.core.generator.Dependency;
import pl.fhframework.compiler.core.generator.MetaModelService;
import pl.fhframework.compiler.core.generator.ModuleMetaModel;
import pl.fhframework.compiler.core.generator.model.service.ServiceMm;
import pl.fhframework.compiler.core.tools.JavaNamesUtils;
import pl.fhframework.compiler.core.uc.ts.generator.FhNgCore;
import pl.fhframework.core.generator.GenerationContext;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.modules.services.ServiceTypeEnum;

public class EnvironmentNgCodeGenerator extends AbstractNgClassCodeGenerator {
    private ModuleMetaModel moduleMetaModel;

    public EnvironmentNgCodeGenerator(ModuleMetaModel moduleMetaModel, MetaModelService metaModelService) {
        super(moduleMetaModel, Dependency.builder().
                module(moduleMetaModel.getModule()).
                name(String.format("environment", moduleMetaModel.getModule().getBasePackage(), JavaNamesUtils.getClassName(moduleMetaModel.getModule().getName()))).build(),
                null, metaModelService);
        this.moduleMetaModel = moduleMetaModel;
    }

    @Override
    protected void generateClassBody() {
        generateEnvironment();
    }

    private void generateEnvironment() {
        globalSection.addLine();
        globalSection.addLine("export const environment = {");
        globalSection.addLineWithIndent(1, "production: false,");
        globalSection.addLineWithIndent(1, "ngCore: { ", FhNgCore.Fh.getName());
        if (!StringUtils.isNullOrEmpty(moduleMetaModel.getInitialPrimaryUc())) {
            Dependency initialUcDep = moduleMetaModel.getDependency(moduleMetaModel.getInitialPrimaryUc());
            String initialUcStr = "app.fh." + initialUcDep.getName().substring(initialUcDep.getModule().getBasePackage().length() + 1);
            TsDependency initialUc = getTsDependency(Dependency.builder().name(initialUcStr).type(DynamicClassArea.USE_CASE).module(initialUcDep.getModule()).build());
            addImport(initialUc);
            globalSection.addLineWithIndent(2, "initialPrimaryUc: %s,", initialUc.getName());
        }
        globalSection.addLineWithIndent(2, "restClients: {");
        //globalSection.addLineWithIndent(3, "baseUrl: 'http://localhost:8090',");
        globalSection.addSectionWithoutLine(generateRestClients(), 3);
        globalSection.addLineWithIndent(2, "},");
        globalSection.addLineWithIndent(2, "endpoints: {");
        globalSection.addSectionWithoutLine(generateEndpoints(), 3);
        globalSection.addLineWithIndent(2, "},");
        globalSection.addLineWithIndent(1,"},");
        globalSection.addLine("};");
        globalSection.addLine();
    }

    private GenerationContext generateRestClients() {
        GenerationContext context = new GenerationContext();
        moduleMetaModel.getMetadata(DynamicClassArea.SERVICE).stream().map(ServiceMm.class::cast).forEach(service -> {
            if (service.getServiceType() == ServiceTypeEnum.RestClient || service.getServiceType() == ServiceTypeEnum.RestService) {
                context.addLine("%s: {", getBaseName(service.getId()));
                if (service.getServiceType() == ServiceTypeEnum.RestService) {
                    context.addLineWithIndent(1, "baseUrl: '{{REST_SERVICE_BASE_URL}}',");
                }
                else {
                    if (!StringUtils.isNullOrEmpty(service.getEndpointName())) {
                        context.addLineWithIndent(1, "endpoint: '%s',", service.getEndpointName());
                    }
                    if (!StringUtils.isNullOrEmpty(service.getEndpointUrl())) {
                        context.addLineWithIndent(1, "baseUrl: '%s',", service.getEndpointUrl());
                    }
                }
                if (!StringUtils.isNullOrEmpty(service.getBaseUri())) {
                    context.addLineWithIndent(1, "resourceUri: '%s',", service.getBaseUri());
                }
                context.addLine("},", getBaseName(service.getId()));
            }
        });
        return context;
    }

    private GenerationContext generateEndpoints() {
        GenerationContext context = new GenerationContext();
        moduleMetaModel.getEndpoints().forEach(endpoint -> {
            context.addLine("%s: {", endpoint.getName());
            context.addLineWithIndent(1, "baseUrl: '%s',", endpoint.getUrl());
            context.addLine("},");
        });
        return context;
    }
}
