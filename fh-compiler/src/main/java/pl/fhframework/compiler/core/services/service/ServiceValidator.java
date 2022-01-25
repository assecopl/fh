package pl.fhframework.compiler.core.services.service;

import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.compiler.core.rules.dynamic.model.Rule;
import pl.fhframework.compiler.core.rules.dynamic.model.TransactionTypeEnum;
import pl.fhframework.compiler.core.rules.service.RulesServiceExtImpl;
import pl.fhframework.compiler.core.services.dynamic.model.*;
import pl.fhframework.core.i18n.MessageService;
import pl.fhframework.core.rules.dynamic.model.Expression;
import pl.fhframework.core.rules.dynamic.model.dataaccess.From;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.integration.IEndpointAccess;
import pl.fhframework.integration.IntegrationUtils;
import pl.fhframework.model.PresentationStyleEnum;
import pl.fhframework.modules.services.ServiceTypeEnum;
import pl.fhframework.validation.IValidationResults;
import pl.fhframework.validation.ValidationResults;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by pawel.ruta on 2017-06-30.
 */
@org.springframework.stereotype.Service
public class ServiceValidator {
    @Autowired
    private RulesServiceExtImpl rulesService;

    @Autowired
    private MessageService messageService;

    @Autowired(required = false)
    private IEndpointAccess endpointAccess;

    public void validate(Service service, IValidationResults validationResults, Map<String, String> registerredPaths) {
        if (service.getServiceType() == ServiceTypeEnum.RestService) {
            if (!StringUtils.isNullOrEmpty(service.getBaseUri())) {
                String pathOwner = registerredPaths.get(IntegrationUtils.cleanUri(service.getBaseUri()));
                if (pathOwner != null && !Objects.equals(pathOwner, service.getId())) {
                    validationResults.addCustomMessage(null, "serviceUrl", String.format($("service.s.is.already.using.base.uri.s"), pathOwner, service.getBaseUri()), PresentationStyleEnum.ERROR);
                }
            }
        }

        service.getOperations().forEach(operationOhter -> {
            IValidationResults operationValRes = new ValidationResults();
            validate(operationOhter, service, operationValRes, registerredPaths, true);
            if (operationValRes.hasAtLeastErrors()) {
                validationResults.addCustomMessage(service, "Operations", String.format($("operation.s.has.errors"), operationOhter.getRule().getLabel()), PresentationStyleEnum.ERROR);
            }
        });
    }

    public void validate(Operation operation, Service service, IValidationResults validationResults, Map<String, String> registerredPaths) {
        validate(operation, service, validationResults, registerredPaths, false);

        IValidationResults operationValRes = new ValidationResults();
        service.getOperations().forEach(operationOhter -> {
            if (!Objects.equals(operationOhter.getRule().getId(), operation.getRule().getId())) {
                validate(operationOhter, service, operationValRes, registerredPaths, true);
            }
        });
        if (operationValRes.hasAtLeastErrors()) {
            validationResults.addCustomMessage(service, "Operations", $("other.operation.has.errors.correct.them.before.using.service"), PresentationStyleEnum.ERROR);
        }
    }

    private void validate(Operation operation, Service service, IValidationResults validationResults, Map<String, String> registerredPaths, boolean fullCheck) {
        validateOperation(operation, service, validationResults, registerredPaths, fullCheck);

        service.getOperations().forEach(operationOhter -> {
            if (!Objects.equals(operationOhter.getRule().getId(), operation.getRule().getId())) {
                if (service.getServiceType() == ServiceTypeEnum.RestService && Objects.equals(operationOhter.getRestProperties().getResourceUri(), operation.getRestProperties().getResourceUri())) {
                    validationResults.addCustomMessage(operation.getRestProperties(), "resourceUri", String.format($("operation.s.is.already.using.resource.uri.s"), operationOhter.getRule().getLabel(), StringUtils.nullToEmpty(operation.getRestProperties().getResourceUri())), PresentationStyleEnum.ERROR);
                }
            }
        });
    }

    private void validateOperation(Operation operation, Service service, IValidationResults validationResults, Map<String, String> registerredPaths, boolean fullCheck) {
        Rule rule = operation.getRule();
        rulesService.validate(rule, validationResults);
        if (rule.getTransactionType() == null || rule.getTransactionType() == TransactionTypeEnum.None) {
            List<String> dataAccessExp = Arrays.asList(Expression.DATAWRITE_REGEX, Expression.DATAREFRESH_NAME, Expression.DATADELETE_NAME);
            if (rule.findElements(From.class).stream().anyMatch(From::isDbQuery) ||
                    rule.findElements(Expression.class).stream().map(Expression::getTagName).anyMatch(dataAccessExp::contains)) {
                validationResults.addCustomMessage(rule, "Transaction Type", $("operation.cannot.contain.database.access.blocks.when.transaction.type.is.none"), PresentationStyleEnum.ERROR);
            }
        }
        if (service.getServiceType().isRest()) {
            String fullUri = null;
            if (service.getServiceType() == ServiceTypeEnum.RestClient) {
                String url = null;
                if (!StringUtils.isNullOrEmpty(service.getEndpointName())) {
                    if (endpointAccess != null) {
                        url = endpointAccess.findOneUrlByName(service.getEndpointName());
                        if (StringUtils.isNullOrEmpty(url)) {
                            validationResults.addCustomMessage(null, "Service Endpoint", String.format($("endpoint.s.is.undefined"), service.getEndpointName()), PresentationStyleEnum.ERROR);
                        }
                    }
                } else {
                    url = service.getEndpointUrl();
                }
                fullUri = StringUtils.nullToEmpty(url) + StringUtils.nullToEmpty(operation.getRestProperties().getResourceUri());
                String fullExampleUrl = fullUri.replaceAll("\\{.*?\\}", "foo");
                if (!IntegrationUtils.isValidUrl(fullExampleUrl)) {
                    validationResults.addCustomMessage(operation.getRestProperties(), "resourceUri", String.format($("resource.url.has.invalid.syntax.s"), fullExampleUrl), PresentationStyleEnum.ERROR);
                }
                else if (!IntegrationUtils.isAbsolute(fullExampleUrl)) {
                    validationResults.addCustomMessage(operation.getRestProperties(), "resourceUri", $("resolved.url.should.be.absolute"), PresentationStyleEnum.ERROR);
                }

            } else if (service.getServiceType() == ServiceTypeEnum.RestService) {
                fullUri = StringUtils.nullToEmpty(service.getBaseUri()) + StringUtils.nullToEmpty(operation.getRestProperties().getResourceUri());
                String fullExampleUrl = fullUri.replaceAll("\\{.*?\\}", "foo");
                if (StringUtils.isNullOrEmpty(fullUri)) {
                    validationResults.addCustomMessage(operation.getRestProperties(), "resourceUri", $("resource.url.is.empty"), PresentationStyleEnum.ERROR);
                } else if (!IntegrationUtils.isValidUrl("http://dummy.url" + fullExampleUrl)) {
                    validationResults.addCustomMessage(operation.getRestProperties(), "resourceUri", String.format($("resource.url.has.invalid.syntax.s"), fullUri), PresentationStyleEnum.ERROR);
                } else if (IntegrationUtils.isAbsolute(fullExampleUrl)) {
                    validationResults.addCustomMessage(operation.getRestProperties(), "resourceUri", $("resource.url.should.be.relative"), PresentationStyleEnum.ERROR);
                }
                long bodyParamCount = operation.getRestProperties().getRestParameters().stream().filter(restParameter -> restParameter.getType() == RestParameterTypeEnum.Body).count();
                if (bodyParamCount > 0 && (operation.getRestProperties().getHttpMethod() != null && operation.getRestProperties().getHttpMethod() != RestMethodTypeEnum.POST && operation.getRestProperties().getHttpMethod() != RestMethodTypeEnum.PUT)) {
                    validationResults.addCustomMessage(null, "REST parameters", String.format($("s.service.cannot.receive.body.parameter"), operation.getRestProperties().getHttpMethod().toString()), PresentationStyleEnum.ERROR);
                } else if (bodyParamCount > 1) {
                    validationResults.addCustomMessage(null, "REST parameters", $("service.cannot.receive.multiply.body.parameters.wrap.them.with.class"), PresentationStyleEnum.ERROR);
                }
                String pathOwner = registerredPaths.get(IntegrationUtils.cleanUri(fullUri));
                if (!StringUtils.isNullOrEmpty(operation.getRestProperties().getResourceUri()) && pathOwner != null && !Objects.equals(pathOwner, service.getId())) {
                    validationResults.addCustomMessage(null, "serviceUrl", String.format($("service.s.is.already.using.base.uri.s"), pathOwner, fullUri), PresentationStyleEnum.ERROR);
                }
            }

            Pattern templateParamPattern = Pattern.compile("\\{(.*?)\\}");
            Matcher paramMatcher = templateParamPattern.matcher(fullUri);
            while (paramMatcher.find()) {
                String paramName = paramMatcher.group(1);
                if (operation.getRestProperties().getRestParameters().stream().noneMatch(restParameter -> Objects.equals(restParameter.getResolvedName(), paramName))) {
                    validationResults.addCustomMessage(operation.getRestProperties(), "resourceUri", String.format($("template.prameter.s.has.not.been.definied"), paramName), PresentationStyleEnum.ERROR);
                }
            }
            List<String> restParamNames = operation.getRestProperties().getRestParameters().stream().map(RestParameter::getResolvedName).collect(Collectors.toList());
            Set<String> restParamNamesDuplicates = restParamNames.stream().filter(name -> Collections.frequency(restParamNames, name) > 1).collect(Collectors.toSet());
            for (String duplicateParam : restParamNamesDuplicates) {
                validationResults.addCustomMessage(null, "REST parameters", String.format($("non.unique.definition.of.prameter.s"), duplicateParam), PresentationStyleEnum.ERROR);
            }

            if (fullCheck) {
                if (operation.getRestProperties().getHttpMethod() == null) {
                    validationResults.addCustomMessage(operation.getRestProperties(), "httpMethod", $("luna.designer.rule.value_is_required"), PresentationStyleEnum.ERROR);
                }
            }
        }
    }

    private String $(String key) {
        return messageService.getAllBundles().getMessage(key);
    }
}
