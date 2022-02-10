/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.fhframework.dp.commons.fh.utils.rest.facade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.fhframework.dp.transport.dto.commons.OperationDto;
import pl.fhframework.dp.transport.dto.commons.OperationStateRequestDto;
import pl.fhframework.dp.transport.service.IDtoService;
import pl.fhframework.dp.transport.service.IOperationDtoService;

import java.lang.reflect.Proxy;
import java.time.LocalDate;
import java.util.HashMap;

@Service
public class FacadeClientFactory {

   @Autowired
   FacadeRestClient facadeRestClient;

      public <S extends IDtoService> S createServiceProxy(Class<S> iDtoService) {

        return (S) Proxy.newProxyInstance(
                iDtoService.getClassLoader(),
                new Class[]{iDtoService},
                (proxy, method, methodArgs) -> {
                    if (method.getName().equals("listDto")) {
                        return facadeRestClient.list(methodArgs[0], iDtoService);
                    } else if (method.getName().equals("listCount")) {
                        return facadeRestClient.count(methodArgs[0], iDtoService);
                    } else if (method.getName().equals("getDto")) {
                        return facadeRestClient.getDto(methodArgs[0], iDtoService);
                    } else if (method.getName().equals("listCount")) {
                        return facadeRestClient.count(methodArgs[0], iDtoService);
                    } else if (method.getName().equals("persistDto")) {
                        return facadeRestClient.persistDto(methodArgs[0], iDtoService);
                    } else if (method.getName().equals("deleteDto")) {
                        facadeRestClient.deleteDto(methodArgs[0], iDtoService);
                    } else if (method.getName().equals("listCodeList")) {
                        return facadeRestClient.listCodeList((String) methodArgs[0], (String) methodArgs[1], (LocalDate) methodArgs[2], (HashMap) methodArgs[3], iDtoService);
                    } else if (method.getName().equals("countCodeList")) {
                        return facadeRestClient.countCodeList((String) methodArgs[0], (String) methodArgs[1], (LocalDate) methodArgs[2], (HashMap) methodArgs[3], iDtoService);
                    } else if (method.getName().equals("getCode")) {
                        return facadeRestClient.getCode((String) methodArgs[0], (String) methodArgs[1], (LocalDate) methodArgs[2], (HashMap) methodArgs[3], iDtoService);
                    } else {
                        throw new UnsupportedOperationException(
                                "Unsupported method: " + method.getName());
                    }
                    return null;
                });
    }

    public <S extends IOperationDtoService> S createOperationServiceProxy(Class<S> iOperationDtoService) {

        return (S) Proxy.newProxyInstance(
                iOperationDtoService.getClassLoader(),
                new Class[]{iOperationDtoService},
                (proxy, method, methodArgs) -> {
                    if (method.getName().equals("performOperation")) {
                        return facadeRestClient.performOperation((OperationDto) methodArgs[0], iOperationDtoService);
                    } else if (method.getName().equals("getOperationData")) {
                        return facadeRestClient.getOperationData((Long) methodArgs[0], (HashMap) methodArgs[1], iOperationDtoService);
                    } else if (method.getName().equals("getOperationState")) {
                        return facadeRestClient.getOperationState((OperationStateRequestDto) methodArgs[0], iOperationDtoService);
                    } else {
                        throw new UnsupportedOperationException(
                                "Unsupported method: " + method.getName());
                    }
                });
    }

}
