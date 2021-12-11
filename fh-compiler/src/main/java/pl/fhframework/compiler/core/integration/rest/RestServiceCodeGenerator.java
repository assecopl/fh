package pl.fhframework.compiler.core.integration.rest;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import pl.fhframework.compiler.core.dynamic.dependency.DependenciesContext;
import pl.fhframework.compiler.core.generator.JavaCodeGeneratorFactory;
import pl.fhframework.compiler.core.rules.dynamic.generator.DynamicRuleCodeBuilder;
import pl.fhframework.compiler.core.rules.dynamic.model.TransactionTypeEnum;
import pl.fhframework.compiler.core.services.dynamic.generator.IServiceCodeGenerator;
import pl.fhframework.compiler.core.services.dynamic.model.Operation;
import pl.fhframework.compiler.core.services.dynamic.model.RestParameter;
import pl.fhframework.compiler.core.services.dynamic.model.RestParameterTypeEnum;
import pl.fhframework.compiler.core.services.dynamic.model.Service;
import pl.fhframework.compiler.core.uc.dynamic.model.element.attribute.ParameterDefinition;
import pl.fhframework.core.generator.GenerationContext;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.modules.services.ServiceTypeEnum;
import pl.fhframework.fhPersistence.anotation.WithoutConversation;

import java.util.Objects;
import java.util.Set;

/**
 * Created by pawel.ruta on 2018-04-09.
 */
public class RestServiceCodeGenerator extends DynamicRuleCodeBuilder implements IServiceCodeGenerator {
    private IServiceCodeGenerator localService;

    private Operation operation;

    public RestServiceCodeGenerator() {
        localService = (IServiceCodeGenerator) JavaCodeGeneratorFactory.getCodeGenerator(ServiceTypeEnum.DynamicService);
    }

    @Override
    protected void generateClassBody() {
        throw new UnsupportedOperationException("RestClientCodeGenerator generates only rest properties not a whole class");
    }

    @Override
    public void generateClassAnnotations(Service service, GenerationContext classSignatureSection, DependenciesContext dependenciesContext) {
        classSignatureSection.addLine("@%s", toTypeLiteral(RestController.class));
        if (!StringUtils.isNullOrEmpty(service.getBaseUri())) {
            classSignatureSection.addLine("@%s(path = \"%s\")", toTypeLiteral(RequestMapping.class), service.getBaseUri());
        }
        localService.generateClassAnnotations(service, classSignatureSection, dependenciesContext);
    }

    @Override
    public void generateOperationAnnotations(Operation operation, Service service, GenerationContext methodSection, DependenciesContext dependenciesContext) {
        if (!StringUtils.isNullOrEmpty(operation.getRestProperties().getResourceUri())) {
            methodSection.addLine("@%s(method = %s.%s, path = \"%s\", produces = org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE)", toTypeLiteral(RequestMapping.class), toTypeLiteral(RequestMethod.class), operation.getRestProperties().getHttpMethod().getHttpMethod().name(), operation.getRestProperties().getResourceUri());
        }
        else {
            methodSection.addLine("@%s(method = %s.%s, produces = org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE)", toTypeLiteral(RequestMapping.class), toTypeLiteral(RequestMethod.class), operation.getRestProperties().getHttpMethod().getHttpMethod().name());
        }
        if (operation.getRule().getTransactionType() == TransactionTypeEnum.None) {
            methodSection.addLine("@%s", toTypeLiteral(WithoutConversation.class));
        }
        localService.generateOperationAnnotations(operation, service, methodSection, dependenciesContext);
    }

    @Override
    public void generateOperationSignature(Operation operation, Service service, GenerationContext methodSection, DependenciesContext dependenciesContext) {
        this.operation = operation;

        initialize(operation.getRule(), null, null, null, null, dependenciesContext);

        generateRuleMethodSignature(operation.getRule(), methodSection);
    }

    @Override
    protected String getParamAnnotations(ParameterDefinition param, String paramJavaName) {
        RestParameter restParameter = operation.getRestProperties().getRestParameters().stream().filter(restParam -> Objects.equals(restParam.getRef(), param.getName())).findAny().get();
        Class<?> restParamClass;
        switch (restParameter.getType()) {
            case Template:
                restParamClass = PathVariable.class;
                break;
            case Query:
                restParamClass = RequestParam.class;
                break;
            case Body:
                restParamClass = RequestBody.class;
                break;
            case Header:
                restParamClass = RequestHeader.class;
                break;
            default:
                throw new IllegalArgumentException(String.format("Unknown HTTP parameter type '%s'", restParameter.getType().toString()));
        }
        if (restParameter.getType() != RestParameterTypeEnum.Body) {
            return String.format("%s @%s(required = false, name = \"%s\")", super.getParamAnnotations(param, paramJavaName), toTypeLiteral(restParamClass), restParameter.getResolvedName());
        }

        return String.format("%s @%s(required = false)", super.getParamAnnotations(param, paramJavaName), toTypeLiteral(restParamClass));
    }

    @Override
    public void generateOperationBody(Operation operation, Service service, GenerationContext methodSection, DependenciesContext dependenciesContext) {
        initialize(operation.getRule(), null, null, null, null, dependenciesContext);

        localService.generateOperationBody(operation, service, methodSection, dependenciesContext);
    }

    @Override
    public void addServices(GenerationContext fieldSection, DependenciesContext dependenciesContext, Set<String> fieldsBean) {
        localService.addServices(fieldSection, dependenciesContext, fieldsBean);
    }
}
