package pl.fhframework.compiler.core.generator;

import com.sun.codemodel.JDefinedClass;
import com.sun.tools.xjc.model.CEnumLeafInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.subsystems.Subsystem;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Path;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface JavaCodeFromXsdService {

    void generateCode(Context context) throws Exception;

    void registerClasses(Context context);

    void storeClasses(Context context);

    ClassDescriptor getClassDescriptor(DynamicClassName className);

    Set<DynamicClassName> provideDependencies(DynamicClassName forClassName);

    boolean isRegistered(DynamicClassName dynamicClassName);

    @Getter
    @Setter
    @Builder
    class Context {
        private Subsystem subsystem;

        private String packageName;

        private String subPackageName;

        private Path xsdTempDir;

        private List<File> xsdFiles;

        @Builder.Default
        private Map<DynamicClassName, ClassDescriptor> classesMap = new HashMap<>();

        @Builder.Default
        private ByteArrayOutputStream objectFactoryCode = new ByteArrayOutputStream();

        @Builder.Default
        private ByteArrayOutputStream packageInfoCode = new ByteArrayOutputStream();

        @Builder.Default
        private Map<Path, Instant> xsdTimestamps = new HashMap<>();

        public String getFullPackage() {
            return packageName + (StringUtils.isNullOrEmpty(subPackageName) ? "" : ("." + subPackageName));
        }
    }

    @Getter
    @Setter
    class ClassDescriptor {
        private DynamicClassName dynamicClassName;

        private ByteArrayOutputStream javaCode = new ByteArrayOutputStream();

        private String xsdDefinition;

        private JDefinedClass xsdModel;

        private CEnumLeafInfo xsdEnumModel;

        private Path xdsFile;

        private Instant timestamp;

        public ClassDescriptor(DynamicClassName dynamicClassName) {
            this.dynamicClassName = dynamicClassName;
        }
    }

    interface ClassNameResolver {
        String toClassName(String packageName, String className);
    }
}
