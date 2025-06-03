package pl.fhframework.compiler.core.integration;

import pl.fhframework.compiler.core.generator.JavaCodeGeneratorFactory;
import pl.fhframework.core.events.ISubsystemLifecycleListener;
import pl.fhframework.core.services.meta.ServiceType;
import pl.fhframework.core.services.meta.ServiceTypeFactory;
import pl.fhframework.compiler.core.integration.rest.RestClientCodeGenerator;
import pl.fhframework.compiler.core.integration.rest.RestServiceCodeGenerator;
import pl.fhframework.modules.services.ServiceTypeEnum;
import pl.fhframework.subsystems.Subsystem;

/**
 * Created by pawel.ruta on 2018-04-09.
 */
public class IntegrationElementsRegister implements ISubsystemLifecycleListener {
    @Override
    public void onSubsystemStart(Subsystem subsystem) {
        JavaCodeGeneratorFactory.registerCodeGenerator(ServiceTypeEnum.RestClient, RestClientCodeGenerator.class);
        JavaCodeGeneratorFactory.registerCodeGenerator(ServiceTypeEnum.RestService, RestServiceCodeGenerator.class);

        ServiceTypeFactory.registerServiceType(new ServiceType(ServiceTypeEnum.RestClient, "REST Client")); // todo: key to resource type label
        //ServiceTypeFactory.registerServiceType(new ServiceType(ServiceTypeEnum.RestService, "REST Service")); // todo: key to resource type label
    }
}
