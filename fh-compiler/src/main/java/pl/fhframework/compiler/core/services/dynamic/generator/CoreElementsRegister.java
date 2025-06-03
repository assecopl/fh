package pl.fhframework.compiler.core.services.dynamic.generator;

import pl.fhframework.compiler.core.generator.JavaCodeGeneratorFactory;
import pl.fhframework.core.events.ISubsystemLifecycleListener;
import pl.fhframework.core.services.meta.ServiceType;
import pl.fhframework.core.services.meta.ServiceTypeFactory;
import pl.fhframework.modules.services.ServiceTypeEnum;
import pl.fhframework.subsystems.Subsystem;

/**
 * Created by pawel.ruta on 2018-04-09.
 */
public class CoreElementsRegister implements ISubsystemLifecycleListener {
    @Override
    public void onSubsystemStart(Subsystem subsystem) {
        JavaCodeGeneratorFactory.registerCodeGenerator(ServiceTypeEnum.DynamicService, FhServiceCodeBuilder.class);

        ServiceTypeFactory.registerServiceType(new ServiceType(ServiceTypeEnum.DynamicService, "User designed"));
    }
}
