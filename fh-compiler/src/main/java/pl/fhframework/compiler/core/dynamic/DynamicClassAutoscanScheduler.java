package pl.fhframework.compiler.core.dynamic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.subsystems.ModuleRegistry;
import pl.fhframework.subsystems.Subsystem;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Dynamic class external creation / deletion scan scheduler.
 */
@Service
public class DynamicClassAutoscanScheduler {

    @Autowired
    private DynamicClassRepository dynamicClassRepository;

    @Value("#{'${fhframework.dynamic.autoscan.modules:}'.split(',')}")
    private List<String> autoscannedModules;

    @Value("#{'${fhframework.dynamic.autoscan.areas:}'.split(',')}")
    private List<String> autoscannedAreas;

    @Scheduled(fixedDelayString = "${fhframework.dynamic.autoscan.delay:60000}")
    public void autoscanForNewDynamicClasses() {
        Set<DynamicClassArea> autoScanAreas = new HashSet<>();

        for (String autoscannedModuleName : autoscannedModules) {
            if (autoscannedModuleName.isEmpty()) {
                continue;
            }
            Subsystem module = ModuleRegistry.getByName(autoscannedModuleName);
            if (module == null) {
                FhLogger.error("Non-existing module passed to fhframework.dynamic.autoscan.modules property: {}",
                        autoscannedModuleName);
                continue;
            }
            for (String autoscannedAreaName : autoscannedAreas) {
                if (autoscannedAreaName.isEmpty()) {
                    continue;
                }
                DynamicClassArea area;
                try {
                    area = DynamicClassArea.valueOf(autoscannedAreaName);
                    autoScanAreas.add(area);
                } catch (Exception e) {
                    FhLogger.error("Invalid area name passed to fhframework.dynamic.autoscan.areas property: {}",
                            autoscannedAreaName);
                    continue;
                }

                dynamicClassRepository.autoscanForNewDynamicClasses(module, area);
            }
        }
        for (DynamicClassArea area : autoScanAreas) {
            dynamicClassRepository.getAreaHandler(area).postAllLoad(dynamicClassRepository);
        }

    }

}
