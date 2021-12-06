package pl.fhframework.compiler.core.services.meta;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.compiler.core.rules.dynamic.model.RuleType;
import pl.fhframework.compiler.core.rules.meta.RuleInfo;
import pl.fhframework.compiler.core.services.dynamic.model.Operation;
import pl.fhframework.compiler.core.services.dynamic.model.Service;
import pl.fhframework.core.FhException;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.compiler.core.dynamic.IClassInfo;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.modules.services.ServiceTypeEnum;
import pl.fhframework.subsystems.Subsystem;

import java.net.URL;
import java.nio.file.Path;

public class ServiceInfo extends RuleInfo {

    @Getter
    private IClassInfo serviceClassInfo;

    @Getter
    private String serviceName;

    @Getter
    @Setter
    private Service service;

    @Getter
    private Operation operation;

    public static ServiceInfo of(IClassInfo serviceClassInfo, String serviceName, Service service) {
        ServiceInfo serviceInfo = new ServiceInfo();
        serviceInfo.serviceClassInfo = serviceClassInfo;
        serviceInfo.serviceName = serviceName;
        serviceInfo.service = service;
        serviceInfo.ruleType = RuleType.BusinessRule;
        return serviceInfo;
    }

    public static ServiceInfo of(IClassInfo serviceClassInfo, String serviceName, Service service, Operation operation) {
        ServiceInfo serviceInfo = of(serviceClassInfo, serviceName, service);
        serviceInfo.operation = operation;
        serviceInfo.rule = operation.getRule();
        serviceInfo.name = operation.getRule().getLabel();
        serviceInfo.ruleType = operation.getRule().getRuleType();
        return serviceInfo;
    }

    public DynamicClassName getClassName() {
        return serviceClassInfo.getClassName();
    }

    public String getFullClassName() {
        return serviceClassInfo.getClassName().toFullClassName();
    }

    public String getBaseClassName() {
        return serviceClassInfo.getClassName().getBaseClassName();
    }

    public String getPackageName() {
        if (serviceClassInfo != null) {
            return serviceClassInfo.getClassName().getPackageName();
        }
        return null;
    }

    public Subsystem getSubsystem() {
        if (serviceClassInfo != null) {
            return serviceClassInfo.getSubsystem();
        }
        return null;
    }

    public URL getUrl() {
        return serviceClassInfo.getXmlFile().getResource().getURL();
    }

    public Path getWritablePath() {
        Path externalPath = serviceClassInfo.getXmlFile().getResource().getExternalPath();

        if(externalPath != null) {
          return externalPath;
        } else {
           throw new FhException("Service " + getBaseClassName() + " is located on read-only URL");
        }
    }

    public ServiceInfo() {
    }

    public ServiceTypeEnum getServiceType() {
        ServiceTypeEnum serviceTypeEnum = service.getServiceType();
        if (serviceTypeEnum == ServiceTypeEnum.RestService) {
            serviceTypeEnum = ServiceTypeEnum.DynamicService;
        }
        return serviceTypeEnum;
    }

    public String getServiceURL() {
        if (!StringUtils.isNullOrEmpty(service.getEndpointName())) {
            return service.getEndpointName();
        }
        if (!StringUtils.isNullOrEmpty(service.getEndpointUrl())) {
            return service.getEndpointUrl();
        }
        if (!StringUtils.isNullOrEmpty(service.getBaseUri())) {
            return service.getBaseUri();
        }
        return "";
    }
}
