package pl.fhframework.subsystems;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.fhframework.core.FhSubsystemException;
import pl.fhframework.core.io.FhResource;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.uc.IInitialUseCase;
import pl.fhframework.ReflectionUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

@Service
public class SubsystemManager {

    @Value("${initial.use.case:}")
    @Getter
    private String autostartedUseCase;

    @Getter
    @Value("#{'${system.usecases.disabled.urls}'.split(',')}")
    private List<String> systemUseCasesDisabledUrls;

    @Getter
    @Value("#{'${system.usecases.classes}'.split(',')}")
    private List<String> systemUseCases;

    private static Map<String, List<Subsystem>> qualifiedPackageToSubsystem = new HashMap<>();
    private static List<Subsystem> _subsystemInfos = new ArrayList<>();
    @Getter
    private static List<Subsystem> subsystemInfos = Collections.unmodifiableList(_subsystemInfos);

    private static List<Class<?>> _basicPackages = new ArrayList<>();
    @Getter
    private static List<Class<?>> basicPackages = Collections.unmodifiableList(_basicPackages);


    public static void loadAllSubsystems(Class<?>... basicPackagesIndicators) {
        for (Class<?> basicPackageIndicator : basicPackagesIndicators) {
            if (_basicPackages.contains(basicPackageIndicator)) {
                throw new FhSubsystemException("Multiple loading of one subsystem for the same basic package '" + basicPackageIndicator.getName() + "'!");
            }
            _basicPackages.add(basicPackageIndicator);
            loadStaticSubsystems(basicPackageIndicator);
        }
        loadStaticUseCasesMetadata();

        for (Class<?> basicPackageIndicator : basicPackagesIndicators) {
            loadDynamicSubsystemsWithUseCases(basicPackageIndicator);
        }
        FhLogger.info(SubsystemManager.class, "Done loading module.");
    }


    private static void loadStaticSubsystems(Class<?> basicPackageIndicator) {
        //Find all class which inherits Subsystem and initialize them
        ReflectionUtils.giveClassesTypeList(basicPackageIndicator, Subsystem.class).forEach(clazz -> {
            Subsystem subsystem = ReflectionUtils.createClassObject(clazz);
            //TODO:Set path in file system to subsystem package
//            subsystem.set...(ReflectionUtils.packagePathForClass(clazz));
            FhLogger.info(SubsystemManager.class, "Loaded subsystem '{}'", subsystem.getClass());
            _subsystemInfos.add(subsystem);
            String subsystemPackageName = subsystem.getBasePackage();
            List<Subsystem> subsystems = qualifiedPackageToSubsystem.get(subsystemPackageName);
            if (subsystems == null) {
                subsystems = new ArrayList<>();
                qualifiedPackageToSubsystem.put(subsystemPackageName, subsystems);
            }
            subsystems.add(subsystem);
        });
    }

    private static void loadStaticUseCasesMetadata() {
        for (Subsystem subsystem : _subsystemInfos) {
            if (subsystem.isStatic()) {
                ReflectionUtils.giveClassesTypeList(subsystem.getBasePackage(), IInitialUseCase.class).forEach(subsystem::addStaticUseCaseReference);//TODO:SSO
            }
        }
    }

    private static void loadDynamicSubsystemsWithUseCases(Class<?> basicPackageIndicator) {
        Set<String> knownSubsystems = new HashSet<>();
        _subsystemInfos.forEach(subsystem -> {
            knownSubsystems.add(subsystem.getBasePath().toExternalPath());
        });
        //Path directoryPath = Paths.get(ReflectionUtils.packagePathForClass(basicPackageIndicator));
        FhResource directoryPath = ReflectionUtils.basePath(basicPackageIndicator);
                //.resolve(basicPackageIndicator.getPackage().getName().replace('.', '/'));
        try {
            Files.walk(directoryPath.getExternalPath())
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".sys"))
                    .forEach(subsystemPath -> {
                        FhLogger.info(SubsystemManager.class, subsystemPath.toString());
                        Subsystem subsystem = DynamicSubsystemReader.instance.readSubsystemConfiguration(subsystemPath.toString());
                        String subsystemPackageName = subsystem.getBasePackage();
//                        if (!knownSubsystems.contains(subsystem.getBasePath().toAbsolutePath().toString())) {
                        _subsystemInfos.add(subsystem);
                        List<Subsystem> subsystemsForPackage = qualifiedPackageToSubsystem.get(subsystemPackageName);
                        if (subsystemsForPackage == null) {
                            subsystemsForPackage = new ArrayList<>();
                            qualifiedPackageToSubsystem.put(subsystemPackageName, subsystemsForPackage);
                        }
                        subsystemsForPackage.add(subsystem);
//                        } else {
//                            FhLogger.debug(this.getClass(), "Omitted subsystem '{}' because it is already loaded!", subsystem.getBasePath());
//                        }
                    });
        } catch (IOException e) {
            FhLogger.error("Error when loading package '{}'!", basicPackageIndicator.getName(), e);
            String vmUrlAttr = System.getProperty("url");
            if (vmUrlAttr == null || vmUrlAttr.length() == 0) {
                FhLogger.error("VM parameter -Durl is not set");
            }
            throw new FhSubsystemException(e);
        }
    }

    public String getAutostartedUseCase() {
        return autostartedUseCase;
    }

}