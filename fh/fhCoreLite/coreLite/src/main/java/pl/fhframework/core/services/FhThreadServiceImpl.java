package pl.fhframework.core.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.fhframework.aop.services.IFhThreadService;
import pl.fhframework.core.rules.service.RulesService;
import pl.fhframework.core.services.service.FhServicesService;

/**
 * @author Paweł Ruta
 */
@Component
public class FhThreadServiceImpl implements IFhThreadService {
    @Autowired
    private RulesService rulesService;

    @Autowired
    private FhServicesService servicesService;

    @Override
    public void onThreadStart() {
        rulesService.startRuleLookupCache();
        servicesService.startServiceLookupCache();
    }

    @Override
    public void onThreadEnd() {
        servicesService.stopServiceLookupCache();
        rulesService.stopRuleLookupCache();
    }
}
