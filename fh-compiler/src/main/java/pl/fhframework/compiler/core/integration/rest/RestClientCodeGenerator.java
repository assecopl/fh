package pl.fhframework.compiler.core.integration.rest;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;
import pl.fhframework.compiler.core.dynamic.dependency.DependenciesContext;
import pl.fhframework.compiler.core.rules.dynamic.generator.DynamicRuleCodeBuilder;
import pl.fhframework.compiler.core.rules.dynamic.model.Rule;
import pl.fhframework.compiler.core.services.dynamic.generator.IServiceCodeGenerator;
import pl.fhframework.compiler.core.services.dynamic.model.*;
import pl.fhframework.compiler.core.uc.dynamic.model.element.attribute.ParameterDefinition;
import pl.fhframework.core.generator.GenerationContext;
import pl.fhframework.core.util.JacksonUtils;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.integration.RestResource;
import pl.fhframework.integration.core.dynamic.rest.RestUtils;

import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by pawel.ruta on 2018-04-09.
 */
public class RestClientCodeGenerator extends DynamicRuleCodeBuilder implements IServiceCodeGenerator {
    private static final String REST_URL_VAR = "rest$url";
    private static final String REST_URI_PARAMS_VAR = "rest$uriParams";
    private static final String REST_BUILDER_VAR = "rest$builder";
    private static final String REST_TEMPLATE_VAR = JacksonUtils.TEMPLATE_NAME;
    private static final String REST_ENTITY_VAR = "rest$entity";
    private static final String REST_HEADERS_VAR = "rest$headers";
    private static final String REST_RESPONE_VAR = "rest$respone";
    private static final String REST_BODY_VAR = "rest$reqBody";
    private static final String REST_RESOURCE_VAR = "rest$resource";

    public RestClientCodeGenerator() {

    }

    @Override
    protected void generateClassBody() {
        throw new UnsupportedOperationException("RestClientCodeGenerator generates only rest properties not a whole class");
    }

    @Override
    public void generateClassAnnotations(Service service, GenerationContext classSignatureSection, DependenciesContext dependenciesContext) {

    }

    @Override
    public void generateOperationAnnotations(Operation operation, Service service, GenerationContext methodSection, DependenciesContext dependenciesContext) {

    }

    @Override
    public void generateOperationSignature(Operation operation, Service service, GenerationContext methodSection, DependenciesContext dependenciesContext) {
        initialize(operation.getRule(), null, null, null, null, dependenciesContext);

        generateRuleMethodSignature(operation.getRule(), methodSection);
    }

    @Override
    public void generateOperationBody(Operation operation, Service service, GenerationContext methodSection, DependenciesContext dependenciesContext) {
        initialize(operation.getRule(), null, null, null, null, dependenciesContext);

        //generateOutputVar(operation.getRule(), methodSection);
        Type outputType = Void.class;
        ParameterDefinition outputParam = rule.getOutputParams().stream().findFirst().orElse(null);
        if (outputParam != null) {
            outputType = modelUtils.getType(outputParam);
        }

        Map<String, Type> contextVars = initVars(operation.getRule());
        RestProperties restProperties = operation.getRestProperties();


        //RestResource rest$resource = __restUtils.buildRestResource("TypiCode", "null", "/posts/{id}");
        methodSection.addLine("%s %s = %s.buildRestResource(\"%s\", \"%s\", \"%s\");", toTypeLiteral(RestResource.class), REST_RESOURCE_VAR, RestUtils.NAME, StringUtils.nullToEmpty(service.getEndpointName()), StringEscapeUtils.escapeJava(StringUtils.nullToEmpty(service.getEndpointUrl())), StringEscapeUtils.escapeJava(StringUtils.nullToEmpty(restProperties.getResourceUri())));

        //String rest$url = rest$resource.getUrl();
        methodSection.addLine("%s %s = %s.getUrl();", toTypeLiteral(String.class), REST_URL_VAR, REST_RESOURCE_VAR);

        // HttpHeaders
        //org.springframework.http.HttpHeaders rest$headers = new HttpHeaders();
        //rest$headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        //rest$headers.setContentType(MediaType.APPLICATION_JSON);
        methodSection.addLine("%s %s = new %s();", toTypeLiteral(HttpHeaders.class), REST_HEADERS_VAR, toTypeLiteral(HttpHeaders.class));
        //headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        methodSection.addLine("%s.setAccept(%s.asList(%s.APPLICATION_JSON));", REST_HEADERS_VAR, toTypeLiteral(Arrays.class), toTypeLiteral(MediaType.class));
        if (operation.getRestProperties().getHttpMethod() == RestMethodTypeEnum.POST_APP_FROM) {
            // Request to return JSON format
            methodSection.addLine("%s.setContentType(%s.APPLICATION_FORM_URLENCODED);", REST_HEADERS_VAR, toTypeLiteral(MediaType.class), REST_HEADERS_VAR);
        }
        else {
            // Request to return JSON format
            methodSection.addLine("%s.setContentType(%s.APPLICATION_JSON);", REST_HEADERS_VAR, toTypeLiteral(MediaType.class), REST_HEADERS_VAR);
        }

        //rest$headers.set("key1", JacksonUtils.stringifyUnlessString(value));
        List<RestParameter> headerParams = restProperties.getRestParameters().stream().filter(
                restParameter -> restParameter.getType() == RestParameterTypeEnum.Header).collect(Collectors.toList());

        //if (rest$resource.getBasicAuthentication() != null) {
        //    String auth = username + ":" + password;
        //    byte[] encodedAuth = Base64.encodeBase64(
        //    auth.getBytes(Charset.forName("US-ASCII")) );
        //    String authHeader = "Basic " + new String( encodedAuth );
        //    rest$headers.set("Authorization", authHeader);
        //}
        methodSection.addLine("if (%s.getBasicAuthentication() != null) {", REST_RESOURCE_VAR);
        methodSection.addLine("%s auth = %s.getBasicAuthentication().getUsername() + \":\" + %s.getBasicAuthentication().getPassword();", toTypeLiteral(String.class), REST_RESOURCE_VAR, REST_RESOURCE_VAR);
        methodSection.addLine("byte[] encodedAuth = %s.getEncoder().encode(auth.getBytes(%s.forName(\"US-ASCII\")));", toTypeLiteral(Base64.class), toTypeLiteral(Charset.class));
        methodSection.addLine("%s.set(\"Authorization\", \"Basic \" + new %s(encodedAuth));", REST_HEADERS_VAR, toTypeLiteral(String.class));
        methodSection.addLine("}");

        // if (rest$resource.getUsernameAndRolesAuthentication() != null) {
        //     rest$headers.add("userName", rest$resource.getUsernameAndRolesAuthentication().getUsername());
        //     rest$headers.add("userRoles", rest$resource.getUsernameAndRolesAuthentication().getRoles());
        // }
        methodSection.addLine("if (%s.getUsernameAndRolesAuthentication() != null) {", REST_RESOURCE_VAR);
        methodSection.addLineWithIndent(2,"%s.add(\"userName\", %s.getUsernameAndRolesAuthentication().getUsername());", REST_HEADERS_VAR, REST_RESOURCE_VAR);
        methodSection.addLineWithIndent(2,"%s.add(\"userRoles\", %s.getUsernameAndRolesAuthentication().getRoles());", REST_HEADERS_VAR, REST_RESOURCE_VAR);
        methodSection.addLine("}");

        for (RestParameter parameter : headerParams) {
            methodSection.addLineWithIndent(2, "%s.set(\"%s\", %s.writeValueAsText(%s));", REST_HEADERS_VAR, parameter.getResolvedName(), JacksonUtils.UTIL_NAME, parameter.getRef());
        }

        List<RestParameter> bodyParams = restProperties.getRestParameters().stream().filter(
                restParameter -> restParameter.getType() == RestParameterTypeEnum.Body).collect(Collectors.toList());
        String httpEntityType = toTypeLiteral(String.class);
        if (operation.getRestProperties().getHttpMethod() == RestMethodTypeEnum.POST_APP_FROM) {
            httpEntityType = String.format("%s<%s, %s>", toTypeLiteral(MultiValueMap.class), toTypeLiteral(String.class), toTypeLiteral(String.class));

            methodSection.addLine("%s<%s, %s> %s = new %s<>();", toTypeLiteral(MultiValueMap.class), toTypeLiteral(String.class), toTypeLiteral(String.class), REST_BODY_VAR, toTypeLiteral(LinkedMultiValueMap.class));
            for (RestParameter parameter : bodyParams) {
                methodSection.addLine("%s.add(\"%s\", %s.stringifyUnlessString(%s));", REST_BODY_VAR, parameter.getResolvedName(), JacksonUtils.UTIL_NAME, parameter.getRef());
            }

            //HttpEntity<String> rest$entity = new HttpEntity<String>(rest$reqBody, rest$headers);
            methodSection.addLine("%s<%s> %s = new %s<>(%s, %s);", toTypeLiteral(HttpEntity.class), httpEntityType,
                    REST_ENTITY_VAR, toTypeLiteral(HttpEntity.class), REST_BODY_VAR, REST_HEADERS_VAR);
        }
        else {
            if (!bodyParams.isEmpty()) {
                httpEntityType = getType(
                        operation.getRule().getInputParams().stream().map(ParameterDefinition.class::cast).filter(pd -> Objects.equals(pd.getName(), bodyParams.get(0).getRef())).findFirst().get(),
                        dependenciesContext);
            }

            //HttpEntity<String> rest$entity = new HttpEntity<String>(rest$reqBody, rest$headers);
            methodSection.addLine("%s<%s> %s = new %s<>(%s%s);", toTypeLiteral(HttpEntity.class), httpEntityType,
                    REST_ENTITY_VAR, toTypeLiteral(HttpEntity.class),
                    !bodyParams.isEmpty() ? bodyParams.get(0).getRef() + ", " : "", REST_HEADERS_VAR);
        }

        // URI (URL) parameters
        //Map<String, Object> rest$uriParams = new HashMap<>();
        //rest$uriParams.put("id", id);
        List<RestParameter> templateParams = restProperties.getRestParameters().stream().filter(
                restParameter -> restParameter.getType() == RestParameterTypeEnum.Template).collect(Collectors.toList());
        methodSection.addLine("%s<%s, %s> %s = new %s<>();", toTypeLiteral(Map.class), toTypeLiteral(String.class), toTypeLiteral(Object.class), REST_URI_PARAMS_VAR, toTypeLiteral(HashMap.class));
        for (RestParameter parameter : templateParams) {
            methodSection.addLine("%s.put(\"%s\", %s.writeValueAsText(%s));", REST_URI_PARAMS_VAR, parameter.getResolvedName(), JacksonUtils.UTIL_NAME, parameter.getRef());
        }

        //UriComponentsBuilder rest$builder = UriComponentsBuilder.fromUriString(rest$url);
        //rest$builder.queryParam("userId", userId);
        methodSection.addLine("%s %s = %s.fromUriString(%s);", toTypeLiteral(UriComponentsBuilder.class), REST_BUILDER_VAR,
                toTypeLiteral(UriComponentsBuilder.class), REST_URL_VAR);
        List<RestParameter> queryParams = restProperties.getRestParameters().stream().filter(
                restParameter -> restParameter.getType() == RestParameterTypeEnum.Query).collect(Collectors.toList());
        for (RestParameter parameter : queryParams) {
            methodSection.addLine("%s.queryParam(\"%s\", %s.writeValueAsText(%s));", REST_BUILDER_VAR, parameter.getResolvedName(), JacksonUtils.UTIL_NAME, parameter.getRef());
        }

        boolean isOutput = outputParam != null;

        //ResponseEntity<Object> rest$respone = rest$template.exchange(rest$builder.buildAndExpand(rest$uriParams).encode().toUri(), HttpMethod.GET, rest$entity, new oParameterizedTypeReference<Object>() {});
        methodSection.addLine("%s<%s> %s = %s.exchange(%s.buildAndExpand(%s).encode().toUri(), %s.%s, %s, new %s<%s>(){});",
                toTypeLiteral(ResponseEntity.class), toTypeLiteral(outputType), REST_RESPONE_VAR,
                REST_TEMPLATE_VAR, REST_BUILDER_VAR, REST_URI_PARAMS_VAR,
                toTypeLiteral(HttpMethod.class), restProperties.getHttpMethod().getHttpMethod().name(), REST_ENTITY_VAR, toTypeLiteral(ParameterizedTypeReference.class),toTypeLiteral(outputType));

        //return rest$respone.getBody();
        if (isOutput) {
            methodSection.addLine("return %s.getBody();", REST_RESPONE_VAR); // todo: http method
        }
        //generateReturn(null, operation.getRule(), contextVars, methodSection);
    }

    @Override
    public void addServices(GenerationContext fieldSection, DependenciesContext dependenciesContext, Set<String> fieldsBean) {
        if (!fieldsBean.contains(RestUtils.NAME)) {
            fieldSection.addLine();
            fieldSection.addLine("@%s", toTypeLiteral(Autowired.class));
            fieldSection.addLine("private %s %s;", toTypeLiteral(RestUtils.class), RestUtils.NAME);

            fieldsBean.add(RestUtils.NAME);
        }

        addJacksonService(fieldSection, fieldsBean);

        addRestTemplate(fieldSection, fieldsBean);
    }


    protected Map<String, Type> initVars(Rule rule) {
        Map<String, Type> contextVars = new LinkedHashMap<>();

        rule.getInputParams().forEach(parameterDefinition -> {
            contextVars.put(parameterDefinition.getName(), modelUtils.getType(parameterDefinition));
        });
        rule.getOutputParams().forEach(parameterDefinition -> {
            contextVars.put(parameterDefinition.getName(), modelUtils.getType(parameterDefinition));
        });

        return contextVars;
    }
}
