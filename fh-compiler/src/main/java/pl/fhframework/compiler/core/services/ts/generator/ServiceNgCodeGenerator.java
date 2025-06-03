package pl.fhframework.compiler.core.services.ts.generator;

import pl.fhframework.compiler.core.generator.MetaModelService;
import pl.fhframework.compiler.core.generator.ModuleMetaModel;
import pl.fhframework.compiler.core.generator.model.service.OperationMm;
import pl.fhframework.compiler.core.generator.model.service.ServiceMm;
import pl.fhframework.compiler.core.generator.ts.TsDependency;
import pl.fhframework.compiler.core.rules.ts.generator.RuleMethodNgCodeGenerator;
import pl.fhframework.compiler.core.services.dynamic.model.RestMethodTypeEnum;
import pl.fhframework.compiler.core.services.dynamic.model.RestParameter;
import pl.fhframework.compiler.core.services.dynamic.model.RestProperties;
import pl.fhframework.compiler.core.tools.JavaNamesUtils;
import pl.fhframework.compiler.core.uc.dynamic.model.element.attribute.ParameterDefinition;
import pl.fhframework.compiler.core.uc.ts.generator.FhNgCore;
import pl.fhframework.core.FhException;
import pl.fhframework.core.generator.GenerationContext;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.modules.services.ServiceTypeEnum;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ServiceNgCodeGenerator extends RuleMethodNgCodeGenerator {
    private final ServiceMm service;
    private final String classBaseName;

    public ServiceNgCodeGenerator(ServiceMm service, ModuleMetaModel moduleMetaModel, MetaModelService metaModelService) {
        super(service, moduleMetaModel, metaModelService);
        this.service = service;
        classBaseName = getBaseName(this.service.getId());
    }

    @Override
    protected void generateClassBody() {
        generateClassSignature(service);

        generateServiceOperations(service);
    }

    private void generateClassSignature(ServiceMm service) {
        if (service.getServiceType() == ServiceTypeEnum.RestClient || service.getServiceType() == ServiceTypeEnum.RestService) {
            if (service.getServiceType() == ServiceTypeEnum.RestService) {
                addImport(FhNgCore.HelperComponent);
                classSignatureSection.addLine("@%s", FhNgCore.HelperComponent.getName());
            }
            addImport(FhNgCore.RestClient);
            classSignatureSection.addCode("@%s", FhNgCore.RestClient.getName());
            if (!StringUtils.isNullOrEmpty(service.getBaseUri())) {
                classSignatureSection.addCode("('%s')", service.getBaseUri());
            }
            classSignatureSection.addLineIfNeeded();
            addImport(FhNgCore.RestOutput);
        }
        else {
            makeInjectable();
        }

        classSignatureSection.addLine("export class %s", classBaseName);
    }

    private void generateServiceOperations(ServiceMm service) {
        service.getOperations().forEach(operation -> {
            if (operation.getRestProperties() != null) {
                generateRestClientMethod(operation, methodSection);
            }
            else {
                generateRuleMethod(operation.getId(), operation.getInputParams(), operation.getOutputParam(), methodSection); // todo;
            }
        });
    }

    private void generateRestClientMethod(OperationMm operation, GenerationContext methodSection) {
        methodSection.addLineIfNeeded();
        RestProperties restProperties = operation.getRestProperties();

        TsDependency httpMethod = getHttpMethod(restProperties.getHttpMethod());
        addImport(httpMethod);
        methodSection.addCode("@%s", httpMethod.getName());
        if (!StringUtils.isNullOrEmpty(restProperties.getResourceUri())) {
            methodSection.addCode("('%s')", restProperties.getResourceUri());
        }
        methodSection.addLineIfNeeded();

        String name = JavaNamesUtils.normalizeMethodName(getBaseName(operation.getId()));
        List<ParameterDefinition> parameterDefinitions = operation.getInputParams();
        parameterDefinitions.forEach(this::addImport);
        String parameters = getArgumentsSignature(parameterDefinitions, annotateRestParam(restProperties), Function.identity());
        String returnTypeStr = "void";
        ParameterDefinition outputParam = operation.getOutputParam();
        if (outputParam != null) {
            returnTypeStr = getType(outputParam);
        }

        methodSection.addLine("%s(%s): Promise<%s> {return %s}", name, parameters, returnTypeStr,  FhNgCore.RestOutput.getName());
    }

    private Function<String, String> annotateRestParam(RestProperties restProperties) {
        Map<String, RestParameter> paramsMap = restProperties.getRestParameters().stream().collect(Collectors.toMap(RestParameter::getRef, Function.identity()));
        return (name) -> {
            RestParameter restParameter = paramsMap.get(name);
            TsDependency paramType = getParamType(restParameter);
            addImport(paramType);
            return String.format("@%s%s %s", paramType.getName(), getParamAttributes(restParameter), name);
        };
    }

    private String getParamAttributes(RestParameter restParameter) {
        if (!StringUtils.isNullOrEmpty(restParameter.getName())) {
            return "('" + restParameter.getName() + "')";
        }
        return "";
    }

    private TsDependency getParamType(RestParameter restParameter) {
        switch (restParameter.getType()) {
            case Query:
                return FhNgCore.RequestParam;
            case Template:
                return FhNgCore.PathVariable;
            case Body:
                return FhNgCore.RequestBody;
            case Header:
                return FhNgCore.RequestHeader;
            default:
                throw new FhException("Unsupported REST parameter type: " + restParameter.getType().name());
        }
    }

    private TsDependency getHttpMethod(RestMethodTypeEnum httpMethod) {
        switch (httpMethod) {
            case GET:
                return FhNgCore.Get;
            case POST:
                return FhNgCore.Post;
            case PUT:
                return FhNgCore.Put;
            case DELETE:
                return FhNgCore.Delete;
            case POST_APP_FROM:
            default:
                FhLogger.error("Unsupported Http request '" + httpMethod.name() + "' in " + classBaseName);
                return FhNgCore.Post;
                // todo: uncomment
                //throw new FhException("Unsupported Http request: " + httpMethod.name());
        }
    }
}
