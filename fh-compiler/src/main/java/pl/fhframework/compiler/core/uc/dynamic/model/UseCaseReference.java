package pl.fhframework.compiler.core.uc.dynamic.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.fhframework.compiler.core.dynamic.DynamicClassArea;
import pl.fhframework.compiler.core.dynamic.DynamicClassFileDescriptor;
import pl.fhframework.compiler.core.dynamic.IClassInfo;
import pl.fhframework.compiler.core.uc.meta.DucExtension;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.core.io.FhResource;
import pl.fhframework.subsystems.Subsystem;

import java.io.File;
import java.nio.file.Paths;

@Getter
@Setter
@NoArgsConstructor
public class UseCaseReference implements IClassInfo, Cloneable {

    private String name;
    private String packageName = "";
    private String description;
    private Subsystem subsystem;
    private String artsDirectory;
    private String path;

    public UseCaseReference(UseCaseReference other) {
        this.name = other.name;
        this.packageName = other.packageName;
        this.description = other.description;
        this.subsystem = other.subsystem;
        this.artsDirectory = other.artsDirectory;
        this.path = other.path;
    }

    public void resolvePath() {
        FhResource sciezkaBazowa = subsystem.getBasePath();
        FhResource path = sciezkaBazowa.resolve(Paths.get(getPackageName().replace(".", File.separator)).toString());
        this.path = path.toExternalPath() + File.separator + getName() + "." + DucExtension.UC_FILENAME_EXTENSION;
    }

    public String getFullName() {
        if (packageName != null && name != null) {
            return packageName.concat(".").concat(name);
        }

        return null;
    }
    // required by use case designer diagram
    @Override
    public Object clone() {
        return new UseCaseReference(this);
    }

    @Override
    public DynamicClassFileDescriptor getXmlFile() {
        return null;
    }

    @Override
    public DynamicClassArea getArea() {
        return DynamicClassArea.USE_CASE;
    }

    @Override
    public DynamicClassName getClassName() {
        return DynamicClassName.forClassName(getFullName());
    }

    @Override
    public boolean isDynamic() {
        return true;
    }
}
