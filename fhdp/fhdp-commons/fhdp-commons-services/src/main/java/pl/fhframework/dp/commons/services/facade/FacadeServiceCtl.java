package pl.fhframework.dp.commons.services.facade;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import pl.fhframework.dp.commons.base.exception.IAppMsgException;
import pl.fhframework.dp.commons.base.model.IPersistentObject;
import pl.fhframework.dp.commons.rest.*;
import pl.fhframework.dp.commons.utils.xml.TextUtils;
import pl.fhframework.dp.transport.dto.commons.NameValueDto;
import pl.fhframework.dp.transport.dto.commons.OperationDto;
import pl.fhframework.dp.transport.dto.commons.OperationStateResponseDto;
import pl.fhframework.dp.transport.service.IDtoService;
import pl.fhframework.dp.transport.service.IOperationDtoService;

import javax.persistence.PersistenceException;
import java.beans.Introspector;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class FacadeServiceCtl implements IFacadeService {

    @Autowired
    ApplicationContext appContext;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public EntityRestResponse list(EntityRestRequest request) {
        EntityRestResponse retList = new EntityRestResponse();
        try {
            if (request != null && request.getQuery() != null && request.getDtoName() != null && request.getToken() != null) {
                IDtoService iDtoService = this.getServiceFromDto(request.getDtoName());
                if (iDtoService != null) {
                    List list = iDtoService.listDto(request.getQuery());
                    retList.setList(list);
                } else {
                    retList.setValid(false);
                    retList.setMessage("Can't find service for DTO: " + request.getDtoName());
                }
            } else {
                retList.setValid(false);
                retList.setMessage("Lack of required data.");
            }
        } catch (Throwable ex) {
            catchException(retList, ex);
        } finally {
            if (!retList.isValid()) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            }
        }
        return retList;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public EntityRestResponse count(EntityRestRequest request) {
        EntityRestResponse ret = new EntityRestResponse();
        try {
            if (request != null && request.getQuery() != null && request.getDtoName() != null && request.getToken() != null) {
                IDtoService iDtoService = this.getServiceFromDto(request.getDtoName());
                if (iDtoService != null) {
                    Long count = iDtoService.listCount(request.getQuery());
                    ret.setCount(count);
                } else {
                    ret.setValid(false);
                    ret.setMessage("Can't find service for DTO: " + request.getDtoName());
                }
            } else {
                ret.setValid(false);
                ret.setMessage("No required data");
            }
        } catch (Throwable ex) {
            catchException(ret, ex);
        } finally {
            if (!ret.isValid()) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            }
        }
        return ret;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public EntityRestResponse getDto(EntityRestRequest request) {
        EntityRestResponse retList = new EntityRestResponse();
        try {
            if (request != null && request.getEntityKey() != null && request.getDtoName() != null) {
                IDtoService iDtoService = this.getServiceFromDto(request.getDtoName());
                if (iDtoService != null) {
                    Object dto = iDtoService.getDto(request.getEntityKey());
                    retList.setObject(dto);
                } else {
                    retList.setValid(false);
                    retList.setMessage("Can't find service for DTO: " + request.getDtoName());
                }
            } else {
                retList.setValid(false);
                retList.setMessage("No required data");
            }
        } catch (Throwable ex) {
            catchException(retList, ex);
        } finally {
            if (!retList.isValid()) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            }
        }
        return retList;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public EntityRestResponse deleteDto(EntityRestRequest request) {
        EntityRestResponse retList = new EntityRestResponse();
        try {
            if (request != null && request.getEntityKey() != null && request.getDtoName() != null) {
                IDtoService iDtoService = this.getServiceFromDto(request.getDtoName());
                if (iDtoService != null) {
                    iDtoService.deleteDto(request.getEntityKey());
                } else {
                    retList.setValid(false);
                    retList.setMessage("Can't find service for DTO: " + request.getDtoName());
                }
            } else {
                retList.setValid(false);
                retList.setMessage("No required data");
            }
        } catch (Throwable ex) {
            catchException(retList, ex);
        } finally {
            if (!retList.isValid()) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            }
        }
        return retList;
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public EntityRestResponse persistDto(EntityRestRequest request) {
        EntityRestResponse retList = new EntityRestResponse();
        try {
            if (request != null && request.getRequestData() != null && request.getDtoName() != null) {
                IDtoService iDtoService = this.getServiceFromDto(request.getDtoName());
                if (iDtoService != null) {
                    Object key = iDtoService.persistDto((IPersistentObject) request.getRequestData());
                    retList.setObject(key);
                } else {
                    retList.setValid(false);
                    retList.setMessage("Can't find service for DTO: " + request.getDtoName());
                }
            } else {
                retList.setValid(false);
                retList.setMessage("No required data");
            }
        } catch (Throwable ex) {
            catchException(retList, ex);
        } finally {
            if (!retList.isValid()) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            }
        }
        return retList;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public OperationRestResponse performOperation(OperationRestRequest request) {
        OperationRestResponse retList = new OperationRestResponse();
        try {
            if (request != null && request.getRequestData() != null && request.getOperationDtoServiceName() != null) {
                IOperationDtoService operationDtoService = this.getOperationDtoServiceFromDto(request.getOperationDtoServiceName());
                if (operationDtoService != null) {
                    Object key = operationDtoService.performOperation((OperationDto) request.getRequestData());
                    retList.setObject(key);
                } else {
                    retList.setValid(false);
                    retList.setMessage("Can't find service: " + request.getOperationDtoServiceName());
                }
            } else {
                retList.setValid(false);
                retList.setMessage("No required data");
            }
        } catch (Throwable ex) {
            catchException(retList, ex);
        } finally {
            if (!retList.isValid()) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            }
        }
        return retList;
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public OperationDataRestResponse getOperationData(OperationDataRestRequest request) {
        OperationDataRestResponse retList = new OperationDataRestResponse();
        try {
            if (request != null && request.getOperationDtoServiceName() != null) {
                IOperationDtoService operationDtoService = this.getOperationDtoServiceFromDto(request.getOperationDtoServiceName());
                if (operationDtoService != null) {
                    OperationDto key = operationDtoService.getOperationData(request.getRequestData(), request.getParamsMap());
                    retList.setObject(key);
                } else {
                    retList.setValid(false);
                    retList.setMessage("Can't find service: " + request.getOperationDtoServiceName());
                }
            } else {
                retList.setValid(false);
                retList.setMessage("No required data");
            }
        } catch (Throwable ex) {
            catchException(retList, ex);
        } finally {
            if (!retList.isValid()) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            }
        }
        return retList;
    }

    @Override
    public OperationStateRestResponse getOperationState(OperationStateRestRequest request) {
        OperationStateRestResponse ret = new OperationStateRestResponse();
        try {
            if (request != null && request.getRequestData() != null && request.getOperationDtoServiceName() != null) {
                log.debug("getOperationState from service {}", request.getOperationDtoServiceName());
                IOperationDtoService operationDtoService = this.getOperationDtoServiceFromDto(request.getOperationDtoServiceName());
                if (operationDtoService != null) {
                    OperationStateResponseDto key = operationDtoService.getOperationState(request.getRequestData());
                    ret.setObject(key);
                } else {
                    ret.setValid(false);
                    ret.setMessage("Can't find service: " + request.getOperationDtoServiceName());
                }
            } else {
                ret.setValid(false);
                ret.setMessage("No required data");
            }
        } catch (Throwable ex) {
            catchException(ret, ex);
        } finally {
            if (!ret.isValid()) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            }
        }
        return ret;
    }

    public EntityRestResponse listCodeList(CodelistRestRequest request) {
        EntityRestResponse retList = new EntityRestResponse();

        try {
            if (request != null && request.getToken() != null) {
                IDtoService iDtoService = getServiceFromDto(request.getDtoName());
                if (iDtoService != null) {
                    List list = iDtoService.listCodeList(request.getRequestData().getRefDataCode(), request.getRequestData().getText(), request.getRequestData().getOnDate() , request.getRequestData().getParm());
                    retList.setList(list);
                } else {
                    retList.setValid(false);
                    retList.setMessage("Can't get service for  DTO: " + request.getDtoName());
                }
            } else {
                retList.setValid(false);
                retList.setMessage("No required data");
            }
        } catch (Throwable var8) {
            this.catchException(retList, var8);
        } finally {
            if (!retList.isValid()) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            }

        }
        return retList;
    }

    @Override
    public EntityRestResponse getCode(CodelistRestRequest request) {
        EntityRestResponse retList = new EntityRestResponse();

        try {
            if (request != null && request.getToken() != null) {
                IDtoService iDtoService = getServiceFromDto(request.getDtoName());
                if (iDtoService != null) {
                    NameValueDto code = iDtoService.getCode(request.getRequestData().getRefDataCode(), request.getRequestData().getCode(), request.getRequestData().getOnDate(), request.getRequestData().getParm());
                    List<Object> list = new ArrayList<>();
                    if(code != null) {
                        list.add(code);
                    }
                    retList.setList(list);
                } else {
                    retList.setValid(false);
                    retList.setMessage("Can't get service for  DTO: " + request.getDtoName());
                }
            } else {
                retList.setValid(false);
                retList.setMessage("No required data");
            }
        } catch (Throwable var8) {
            this.catchException(retList, var8);
        } finally {
            if (!retList.isValid()) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            }

        }
        return retList;
    }

    @Override
    public EntityRestResponse countCodeList(CodelistRestRequest request) {
        EntityRestResponse ret = new EntityRestResponse();
        try {
            if (request != null && request.getDtoName() != null && request.getToken() != null) {
                IDtoService iDtoService = this.getServiceFromDto(request.getDtoName());
                if (iDtoService != null) {
                    Long count = iDtoService.countCodeList(request.getRequestData().getRefDataCode(), request.getRequestData().getText(), request.getRequestData().getOnDate() , request.getRequestData().getParm());
                    ret.setCount(count);
                } else {
                    ret.setValid(false);
                    ret.setMessage("Can't find service for DTO: " + request.getDtoName());
                }
            } else {
                ret.setValid(false);
                ret.setMessage("No required data");
            }
        } catch (Throwable ex) {
            catchException(ret, ex);
        } finally {
            if (!ret.isValid()) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            }
        }
        return ret;
    }

    protected IDtoService getServiceFromDto(String dtoName) {
        if (dtoName != null && dtoName.length() > 1) {
            String serviceName = dtoName;
            if (serviceName.startsWith("I")) { //temporarry
                serviceName = serviceName.substring(1);
            }

//            serviceName = serviceName.substring(0, 1).toLowerCase() + serviceName.substring(1);
            serviceName = Introspector.decapitalize(serviceName);
            if (!serviceName.endsWith("Service")) {
                serviceName = serviceName.replaceAll("ListDto", "Dto") + "Service";
            }
            return  (IDtoService) appContext.getBean(serviceName);
        }
        return null;
    }

    private IOperationDtoService getOperationDtoServiceFromDto(String operationDtoServiceName) {
        if (operationDtoServiceName != null && operationDtoServiceName.length() > 1) {
            String serviceName = operationDtoServiceName;
            if (serviceName.startsWith("I")) { //temporarry
                serviceName = serviceName.substring(1);
            }

            serviceName = TextUtils.decapitateFirstLetter(serviceName);

            return (IOperationDtoService) appContext.getBean(serviceName);
        }
        return null;
    }


    protected void catchException(BaseRestResponse response, Throwable ex) {
        String message;
        if (ex instanceof IAppMsgException) {
            message = ex.getMessage();
            LoggerFactory.getLogger(this.getClass()).error(message, ex);
        } else if(ex instanceof PersistenceException) {
            message = "Error saving to DB";
            if(ex.getCause() instanceof ConstraintViolationException) {
                message += " - Check uniqueness of ";
                if(ex.getCause().getCause() instanceof SQLException) {
                    String errorMessage = ex.getCause().getCause().getMessage();
                    if(errorMessage.contains("Key (tin, lrn)")) {
                        message += " - LRN and TIN";
                    } else {
                        message += " - " + ((ConstraintViolationException) ex.getCause()).getConstraintName();
                    }
                }
            }
            LoggerFactory.getLogger(this.getClass()).error(message, ex);
        } else {
            message = ExceptionUtils.getStackTrace(ex);
            LoggerFactory.getLogger(this.getClass()).error(message, ex);
        }

        response.setMessage(message);
        response.setValid(false);
    }
}
