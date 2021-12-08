package pl.fhframework.dp.commons.fh.utils.rest.facade;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import pl.fhframework.dp.commons.rest.*;
import pl.fhframework.dp.transport.dto.commons.*;
import pl.fhframework.SessionManager;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.event.EventRegistry;
import pl.fhframework.event.dto.NotificationEvent;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author <a href="mailto:jacek_borowiec@skg.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 26/03/2020
 */
@Service
public class FacadeRestClient {

    @Value("${rest.facade.url:}")
    @Setter
    private String url;
    //TODO: agree the value of token
    private String token = "TOKEN";
    @Autowired
    EventRegistry eventRegistry;


    public List<Object> list(Object query, Class listDtoClass) {
        EntityRestRequest request = new EntityRestRequest();
        request.setToken(token);
        request.setQuery(query);
        request.setDtoName(listDtoClass.getSimpleName());
        EntityRestResponse response = doRequest("list", request);
        return validateResponseAndReturn(response, () -> response.getList() != null ? response.getList() : new ArrayList<>(), ArrayList::new);
    }

    public Long count(Object query, Class listDtoClass) {
        EntityRestRequest request = new EntityRestRequest();
        request.setToken(token);
        request.setQuery(query);
        request.setDtoName(listDtoClass.getSimpleName());
        EntityRestResponse response = doRequest("count", request);
        return validateResponseAndReturn(response, () -> response.getCount() != null ? response.getCount() : 0L, () -> -1L);
    }

    public Object getDto(Object key, Class listDtoClass) {
        EntityRestRequest request = new EntityRestRequest();
        request.setEntityKey(key);
        request.setDtoName(listDtoClass.getSimpleName());
        EntityRestResponse response = doRequest("getDto", request);
        return validateResponseAndReturn(response, () -> response.getObject(), () -> null);
    }

    //returns key of persisted object if success
    public Object persistDto(Object dto, Class listDtoClass) {
        EntityRestRequest request = new EntityRestRequest();
        request.setRequestData(dto);
        request.setDtoName(listDtoClass.getSimpleName());
        EntityRestResponse response = doRequest("persistDto", request);
        return validateResponseAndReturn(response, () -> response.getObject(), () -> null);
    }

    public void deleteDto(Object key, Class listDtoClass) {
        EntityRestRequest request = new EntityRestRequest();
        request.setEntityKey(key);
        request.setDtoName(listDtoClass.getSimpleName());
        EntityRestResponse response = doRequest("deleteDto", request);
        validateResponse(response);
    }

    public List<NameValueDto> listCodeList(String code, String text, LocalDate onDate, HashMap parm, Class sClass) {
        if (StringUtils.isNullOrEmpty(text))
            return new ArrayList<>();

        CodelistRequestDto dto = CodelistRequestDto.builder().refDataCode(code).text(text).onDate(onDate).parm(parm).build();
        CodelistRestRequest request = new CodelistRestRequest();
        request.setRequestData(dto);
        request.setDtoClass(sClass); //TODO: is needed?
        request.setToken(token);
        EntityRestResponse response = doRequest("listCodeList", request);
        return validateResponseAndReturn(response, () -> response.getList() != null ? getFromObjectList(response.getList()) : new ArrayList<>(), ArrayList::new);
    }

    public Long countCodeList(String code, String text, LocalDate onDate, HashMap parm, Class sClass) {
        CodelistRequestDto dto = CodelistRequestDto.builder().refDataCode(code).text(text).onDate(onDate).parm(parm).build();
        CodelistRestRequest request = new CodelistRestRequest();
        request.setRequestData(dto);
        request.setDtoClass(sClass); //TODO: is needed?
        request.setToken(token);
        EntityRestResponse response = doRequest("countCodeList", request);
        return validateResponseAndReturn(response, () -> response.getCount() != null ? response.getCount() : 0L, () -> -1L);
    }

    public NameValueDto getCode(String refDataCode, String code, LocalDate onDate, HashMap parm, Class sClass) {
        CodelistRequestDto dto = CodelistRequestDto.builder().refDataCode(refDataCode).code(code).onDate(onDate).parm(parm).build();
        CodelistRestRequest request = new CodelistRestRequest();
        request.setRequestData(dto);
        request.setDtoClass(sClass); //TODO: is needed?
        request.setToken(token);
        EntityRestResponse response = doRequest("getCode", request);
        return (NameValueDto) validateResponseAndReturn(response, () -> response.getList() != null ? getFromObjectList(response.getList()).get(0) : null, () -> null);
    }

    public Object performOperation(OperationDto operation, Class operationDtoServiceClass) {
        operation.setStartTime(LocalDateTime.now());
        operation.setPerformer(SessionManager.getUserLogin());
        OperationRestRequest request = new OperationRestRequest();
        request.setRequestData(operation);
        request.setOperationDtoServiceName(operationDtoServiceClass.getSimpleName());
        String uri = UriComponentsBuilder
                .fromUriString(url)
                .pathSegment("performOperation")
                .encode()
                .toUriString();
        ResponseEntity<OperationRestResponse> ret = FacadeRestTemplateConfig.
                restTemplate.postForEntity(uri, request, OperationRestResponse.class);
        OperationRestResponse response = ret.getBody();
        if(response.isValid()) {
            return response.getOperationResultDto();
        } else {
            OperationResultBaseDto errorResponse = new OperationResultBaseDto();
            errorResponse.setOk(false);
            errorResponse.setResultMessage(response.getMessage());
            return errorResponse;
        }
    }

    public Object getOperationData(Long id, HashMap<String, String> paramsMap, Class operationDtoServiceClass) {
        OperationDataRestRequest request = new OperationDataRestRequest();
//        request.setToken(SecurityUtils.getToken());
        request.setRequestData(id);
        request.setParamsMap(paramsMap);
        request.setOperationDtoServiceName(operationDtoServiceClass.getSimpleName());
        String uri = UriComponentsBuilder
                .fromUriString(url)
                .pathSegment("getOperationData")
                .encode()
                .toUriString();
        ResponseEntity<OperationDataRestResponse> ret = FacadeRestTemplateConfig.
                restTemplate.postForEntity(uri, request, OperationDataRestResponse.class);
        OperationDataRestResponse response = ret.getBody();
        return response.getObject();
    }

    public OperationStateResponseDto getOperationState(OperationStateRequestDto data, Class operationDtoServiceClass) {
        OperationStateRestRequest request = new OperationStateRestRequest();
        request.setRequestData(data);
        request.setOperationDtoServiceName(operationDtoServiceClass.getSimpleName());
        String uri = UriComponentsBuilder
                .fromUriString(url)
                .pathSegment("getOperationState")
                .encode()
                .toUriString();
        ResponseEntity<OperationStateRestResponse> ret = FacadeRestTemplateConfig.
                restTemplate.postForEntity(uri, request, OperationStateRestResponse.class);
        OperationStateRestResponse response = ret.getBody();
        if(response.isValid()) {
            return response.getOperationStateResponseDto();
        } else {
            OperationStateResponseDto errorResponse = new OperationStateResponseDto();
            errorResponse.setIncident(true);
            errorResponse.setStackTrace(response.getStackTrace());
            return errorResponse;
        }
    }

    private EntityRestResponse doRequest(String pathSegment, Object request) {
        String uri = UriComponentsBuilder
                .fromUriString(url)
                .pathSegment(pathSegment)
                .encode()
                .toUriString();
        ResponseEntity<EntityRestResponse> ret = FacadeRestTemplateConfig.
                restTemplate.postForEntity(uri, request, EntityRestResponse.class);
        return ret.getBody();
    }

    private List<NameValueDto> getFromObjectList(List<?> objectList) {
        return (List<NameValueDto>) objectList;
    }


    private <RET> RET validateResponseAndReturn(EntityRestResponse response, Supplier<RET> whenValid, Supplier<RET> whenInvalid) {
        if (response.isValid()) {
            return whenValid.get();
        } else {
            //TODO: i18n
            eventRegistry.fireNotificationEvent(NotificationEvent.Level.ERROR, response.getMessage());
//            eventRegistry.fireNotificationEvent(NotificationEvent.Level.ERROR, response.getMessageKey());
            return whenValid.get();
        }
    }
    private void validateResponse(EntityRestResponse response) {
        if (!response.isValid()) {
            //TODO: i18n
            eventRegistry.fireNotificationEvent(NotificationEvent.Level.ERROR, response.getMessage());
//            eventRegistry.fireNotificationEvent(NotificationEvent.Level.ERROR, response.getMessageKey());
        }
    }
}
