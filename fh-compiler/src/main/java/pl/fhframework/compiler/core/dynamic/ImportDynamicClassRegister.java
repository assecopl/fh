package pl.fhframework.compiler.core.dynamic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import pl.fhframework.compiler.core.generator.FhServicesTypeProvider;
import pl.fhframework.compiler.core.generator.RulesTypeProvider;
import pl.fhframework.compiler.core.dynamic.enums.DynamicClassFileExtensionEnum;
import pl.fhframework.compiler.core.util.ZipUtils;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.subsystems.Subsystem;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class ImportDynamicClassRegister {

    @Autowired
    private DynamicClassRepository dynamicClassRepository;

    @Autowired
    private RulesTypeProvider rulesTypeProvider;

    @Autowired
    private FhServicesTypeProvider fhServicesTypeProvider;

    /**
     * Import dynamic class file to application and register.
     *
     * @param packagePath Full package path.
     * @param fileName    File name.
     * @param subsystem   Application Subsystem.
     * @param classArea   Class area. Determined by file extension.
     * @param file        File to import.
     * @return Path to the location where the file was copied.
     */
    public Path importDynamicClassFromFile(String packagePath, String fileName, Subsystem subsystem, DynamicClassArea classArea, File file) {

        DynamicClassName newClassName = DynamicClassName.forClassName(packagePath, fileName);
        boolean updateFile = false;
        if (dynamicClassRepository.isRegisteredDynamicClass(newClassName)) {
            updateFile = true;
        }
        String extension = '.' + DynamicClassFileExtensionEnum.getByDynamicClassArea(classArea).getFileExtension();
        Path relativePath = Paths.get(packagePath.replace(".", File.separator), fileName + extension);
        Path fullPath = ZipUtils.copyFileToRepository(file, relativePath.toString(), subsystem);
        if (updateFile) {
            dynamicClassRepository.updateDynamicClass(newClassName);
        } else {
            dynamicClassRepository.registerDynamicClassFile(
                    DynamicClassFileDescriptor.forPath(
                            fullPath, relativePath.toString(), subsystem),
                    classArea);
        }
        refreshRepository(classArea);
        return fullPath;
    }

    /**
     * Import dynamic class file from zip to application and register.
     *
     * @param importResource Zip file.
     * @param subsystem      Application Subsystem.
     * @param classArea      Class area. Determined by file extension.
     * @param file           File to import.
     * @param importingDcn
     * @return Path to the location where the file was copied.
     */
    public Path importDynamicClassFromZip(Resource importResource, Subsystem subsystem, DynamicClassArea classArea, File file, Set<DynamicClassName> importingDcn) throws DynamicClassRepository.DependenciesNotResolvableException {
        DynamicClassName newClassName = DynamicClassName.forClassName(
                file.getParentFile().getPath().replaceAll(Pattern.quote(File.separator), "."),
                file.getName().split("\\.")[0]);
        boolean updateFile = dynamicClassRepository.isRegisteredDynamicClass(newClassName);

        Path fullPath = ZipUtils.copyZipFileToRepository(importResource, file.getPath(), subsystem, updateFile);

        DynamicClassRepository.DynamicClassRepositoryEntry entry;

        if (updateFile) {
            entry = dynamicClassRepository.toDynamicClassRepositoryEntry(newClassName);
            dynamicClassRepository.areAllDependenciesResolvable(dynamicClassRepository.getAreaHandler(classArea).readMetadata(entry.getXmlFile()), importingDcn);
            dynamicClassRepository.updateDynamicClass(newClassName);
        } else {
            entry = dynamicClassRepository.toDynamicClassRepositoryEntry(
                    DynamicClassFileDescriptor.forPath(fullPath, file.getPath(), subsystem),
                    classArea);
            dynamicClassRepository.areAllDependenciesResolvable(dynamicClassRepository.getAreaHandler(classArea).readMetadata(entry.getXmlFile()), importingDcn);
            dynamicClassRepository.registerDynamicClassFile(
                    DynamicClassFileDescriptor.forPath(fullPath, file.getPath(), subsystem),
                    classArea);
        }

        dynamicClassRepository.refreshAuthorizationInfo(subsystem, newClassName);
        refreshRepository(classArea);

        return fullPath;
    }

    private void refreshRepository(DynamicClassArea classArea) {
        switch (classArea) {
            case RULE:
                rulesTypeProvider.refresh();
                break;
            case USE_CASE:
                //TODO: refresh?
                break;
            case FORM:
                //TODO: refresh?
                break;
            case MODEL:
                //TODO: refresh?
                break;
            case SERVICE:
                fhServicesTypeProvider.refresh();
                break;
        }
    }
}

