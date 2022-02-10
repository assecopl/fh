package pl.fhframework.compiler.core.model;

import org.springframework.stereotype.Service;
import pl.fhframework.compiler.core.dynamic.DynamicClassFileDescriptor;
import pl.fhframework.core.io.FhResource;
import pl.fhframework.subsystems.Subsystem;

import java.io.File;

/**
 * Helper factory to create class model reference which contains information about xml file.
 */
@Service
public class ClassModelReferenceFactory {

    /**
     * Creation method.
     *
     * @param dynamicClassFileDescriptor - file descriptor
     */
    public ClassModelReference createFrom(DynamicClassFileDescriptor dynamicClassFileDescriptor) {
        ClassModelReference ref = new ClassModelReference();

        Subsystem foundSubsystem = dynamicClassFileDescriptor.getSubsystem();

        ref.setSubsystem(foundSubsystem);
        ref.setClassType(ClassType.DYNAMIC);
        FhResource resource = dynamicClassFileDescriptor.getResource();

        String packageWithFileName = dynamicClassFileDescriptor.getRelativePath();
        String fileName = resource.getFilename();

        if (packageWithFileName.endsWith(fileName)) {
            String packageWithFileNameAsString = packageWithFileName.replace(File.separator, ".");
            ref.setPackageName(createNameFrom((packageWithFileNameAsString), packageWithFileNameAsString.length() - (fileName.length() + File.separator.length())));
        }
        ref.setName(createNameFrom(fileName, fileName.length() - (DmoExtension.MODEL_FILENAME_EXTENSION.length() + 1)));
        ref.setPath(resource.getExternalPath().toString());
        return ref;
    }

    private String createNameFrom(String fileName, int endIndex) {
        return fileName.substring(0, endIndex);
    }

}
