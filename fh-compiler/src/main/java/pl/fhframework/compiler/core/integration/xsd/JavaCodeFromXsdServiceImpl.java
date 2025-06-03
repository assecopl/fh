package pl.fhframework.compiler.core.integration.xsd;

import com.sun.codemodel.*;
import com.sun.tools.xjc.BadCommandLineException;
import com.sun.tools.xjc.api.ErrorListener;
import com.sun.tools.xjc.api.S2JJAXBModel;
import com.sun.tools.xjc.api.SchemaCompiler;
import com.sun.tools.xjc.api.XJC;
import com.sun.xml.bind.api.impl.NameConverter;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;
import pl.fhframework.compiler.core.dynamic.DynamicClassArea;
import pl.fhframework.compiler.core.dynamic.DynamicClassFileDescriptor;
import pl.fhframework.compiler.core.dynamic.DynamicClassRepository;
import pl.fhframework.compiler.core.generator.JavaCodeFromXsdService;
import pl.fhframework.compiler.core.model.ClassModelReference;
import pl.fhframework.compiler.core.model.ClassType;
import pl.fhframework.compiler.core.model.DmoExtension;
import pl.fhframework.compiler.core.model.DynamicModelXmlManager;
import pl.fhframework.compiler.core.model.meta.ClassTag;
import pl.fhframework.core.FhException;

import pl.fhframework.core.dynamic.DynamicClassName;

import pl.fhframework.core.io.FhResource;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.util.FileUtils;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.subsystems.Subsystem;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class JavaCodeFromXsdServiceImpl implements JavaCodeFromXsdService {
    @Autowired
    private DynamicModelXmlManager xmlManager;

    @Autowired
    private DynamicClassRepository dynamicClassRepository;

    @Autowired
    private ClassNameResolver classNameResolver;

    private Map<DynamicClassName, ClassDescriptor> xsdClasses = new ConcurrentHashMap<>();

    @Override
    public void generateCode(Context context) throws IOException, BadCommandLineException {
        System.setProperty("jdk.xml.maxOccurLimit", "0");

        // Setup schema compiler
        SchemaCompiler sc = XJC.createSchemaCompiler();
        InputStream bindinStr = JavaCodeFromXsdServiceImpl.class.getResourceAsStream("/fhXsdBinding.xml");
        InputSource binding = new InputSource(bindinStr);
        binding.setSystemId(JavaCodeFromXsdServiceImpl.class.getResource("/fhXsdBinding.xml").toString());
        sc.getOptions().addBindFile(binding);

        FhXjcPlugin fhXjcPlugin = new FhXjcPlugin(FhXjcPlugin.Mode.SnapshotEnabledDynamic, context);
        sc.getOptions().getAllPlugins().add(fhXjcPlugin);

        String[] xjcArgs = new String[]{"-" + FhXjcPlugin.PLUGIN_OPTION_NAME, "-episode", "target.episode"};
        sc.getOptions().parseArgument(xjcArgs, 0);
        sc.getOptions().parseArgument(xjcArgs, 1);

        sc.getOptions().setNameConverter(new NameConverter.Standard() {
            @Override
            public String toClassName(String className) {
                return classNameResolver.toClassName(context.getPackageName(), StringUtils.firstLetterToUpper(className));
            }

            @Override
            public String toPackageName(String nsUri) {
                String subPackage = super.toPackageName(nsUri);
                return context.getFullPackage() + (subPackage != null ? "." + subPackage : "");
            }

            @Override
            public String toVariableName(String name) {
                return StringUtils.firstLetterToLower(name);
            }

            @Override
            public String toPropertyName(String name) {
                return StringUtils.firstLetterToUpper(name);
            }
        }, fhXjcPlugin);

        sc.setErrorListener(new ErrorListener() {
            @Override
            public void error(SAXParseException exception) {
                throw new FhException(exception);
            }

            @Override
            public void fatalError(SAXParseException exception) {
                throw new FhException(exception);
            }

            @Override
            public void warning(SAXParseException exception) {
                FhLogger.errorSuppressed(exception);
            }

            @Override
            public void info(SAXParseException exception) {
                FhLogger.info(this.getClass(), exception.toString());
            }
        });

        for (File file : context.getXsdFiles()) {
            parseSchema(sc, file);
        }

        S2JJAXBModel model = sc.bind();

        moveXsdFiles(context);

        context.getClassesMap().values().forEach(classDescriptor -> {
            classDescriptor.setTimestamp(context.getXsdTimestamps().computeIfAbsent(
                Paths.get(getPath(context.getSubsystem(), context.getFullPackage(), classDescriptor.getXdsFile().toString())),
                FileUtils::getLastModified));
            fhXjcPlugin.addXmlTimeStampMethod(classDescriptor.getXsdModel(), classDescriptor.getXsdModel().owner(), classDescriptor.getDynamicClassName(), classDescriptor.getTimestamp());
        });

        JCodeModel jCodeModel = model.generateCode(null, null);

        StringCodeWriter codeWriter = new StringCodeWriter(context);
        jCodeModel.build(codeWriter);
    }

    @Override
    public void registerClasses(Context context) {
        List<String> xsdsPackage = context.getXsdFiles().stream().map(file ->
                context.getXsdTempDir().relativize(file.toPath()).toString()
        ).collect(Collectors.toList());

        context.getClassesMap().values().forEach(classDescriptor -> {
            xsdClasses.put(classDescriptor.getDynamicClassName(), classDescriptor);

            ClassTag classTag = new ClassTag();
            classTag.setId(classDescriptor.getDynamicClassName().toFullClassName());
            classTag.setName(classDescriptor.getDynamicClassName().getBaseClassName());

            ClassModelReference cmr = ClassModelReference.builder().
                    name(classDescriptor.getDynamicClassName().getBaseClassName()).
                    packageName(classDescriptor.getDynamicClassName().getPackageName()).
                    subsystem(context.getSubsystem()).build().toBuilder().build();
            cmr.setPath(getPath(context.getSubsystem(), classDescriptor.getDynamicClassName().getPackageName(), classTag.getName() + "." + DmoExtension.MODEL_FILENAME_EXTENSION));
            cmr.setClassType(ClassType.DYNAMIC);
            classTag.setReference(cmr);

            classTag.setXsdSchema(classDescriptor.getXdsFile().toString());
            classTag.setXsdSchemaPackage(xsdsPackage);
            classTag.setXsdDefinition(classDescriptor.getXsdDefinition());
            classTag.setXsdSubPackage(context.getSubPackageName());
            classTag.setEnumeration(classDescriptor.getXsdEnumModel() != null);
            xmlManager.updateFile(classTag, classTag.getReference().getPath());

            if (dynamicClassRepository.isRegisteredDynamicClass(classDescriptor.getDynamicClassName())) {
                dynamicClassRepository.updateDynamicClass(classDescriptor.getDynamicClassName());
            }
            else {
                dynamicClassRepository.registerDynamicClassFile(getDescriptor(classTag.getReference()), DynamicClassArea.MODEL);
            }
        });
    }

    @Override
    public void storeClasses(Context context) {
        context.getClassesMap().values().forEach(classDescriptor -> xsdClasses.put(classDescriptor.getDynamicClassName(), classDescriptor));
    }

    @Override
    public ClassDescriptor getClassDescriptor(DynamicClassName className) {
        return xsdClasses.get(className);
    }

    @Override
    public Set<DynamicClassName> provideDependencies(DynamicClassName className) {
        if (!isRegistered(className)) {
            throw new FhException("Unknown XSD type: " + className.toFullClassName());
        }
        return provideDependencies(getClassDescriptor(className).getXsdModel());
    }

    Set<DynamicClassName> provideDependencies(JDefinedClass jDefinedClass) {
        if (jDefinedClass == null) {
            return Collections.emptySet();
        }

        Set<DynamicClassName> dependencies = new HashSet<>();
        if (jDefinedClass._extends() instanceof JDefinedClass) {
            addDependency((JDefinedClass) jDefinedClass._extends(), dependencies);
        }

        Iterable<JClass> definedClassesIt = jDefinedClass::_implements;
        StreamSupport.stream(definedClassesIt.spliterator(), false).
                filter(JDefinedClass.class::isInstance).map(JDefinedClass.class::cast).forEach(jClass -> addDependency(jClass, dependencies));

        jDefinedClass.fields().values().forEach(jFieldVar -> {
            if (jFieldVar.type() instanceof JDefinedClass) {
                addDependency((JDefinedClass) jFieldVar.type(), dependencies);
            }
            else if (jFieldVar.type() instanceof JClass) {
                ((JClass)jFieldVar.type()).getTypeParameters().forEach(jClass -> {
                    if (jClass instanceof JDefinedClass) {
                        addDependency((JDefinedClass) jClass, dependencies);
                    }
                });

            }
        });
        return dependencies;
    }

    @Override
    public boolean isRegistered(DynamicClassName dynamicClassName) {
        return xsdClasses.containsKey(dynamicClassName);
    }

    private void addDependency(JDefinedClass definedClass, Set<DynamicClassName> dependencies) {
        dependencies.add(DynamicClassName.forClassName(definedClass.getPackage().name(), definedClass.name()));
    }

    private void moveXsdFiles(Context context) {
        context.getXsdFiles().forEach(file -> {
            File newFile = new File(getPath(context.getSubsystem(), context.getFullPackage(), context.getXsdTempDir().relativize(file.toPath()).toString()));
            if (!newFile.getParentFile().exists()) {
                newFile.getParentFile().mkdirs();
            }
            try {
                Files.move(file.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new FhException(e);
            }
        });
    }

    private String getPath(Subsystem subsystem, String packageName, String name) {
        FhResource sciezkaBazowa = subsystem.getBasePath();
        FhResource path = sciezkaBazowa.resolve(Paths.get(packageName.replace(".", File.separator)).toString());
        return path.toExternalPath() + File.separator + name;
    }

    private void parseSchema(SchemaCompiler sc, File schemaFile) throws FileNotFoundException {
        // Setup SAX InputSource
        InputStream isr = new FileInputStream(schemaFile);
        InputSource is = new InputSource(isr);
        is.setSystemId(schemaFile.toURI().toString());
        // Parse & build
        sc.parseSchema(is);
    }

    private DynamicClassFileDescriptor getDescriptor(ClassModelReference reference) {
        Path path = Paths.get(reference.getPath());
        String relativePath = Paths.get(reference.getSubsystem().getBasePath().getFile().getAbsolutePath())
                .relativize(path.toAbsolutePath()).toFile().getPath();
        return DynamicClassFileDescriptor.forPath(path, relativePath, reference.getSubsystem());
    }

    @Getter
    @Setter
    private static class StringCodeWriter extends CodeWriter {
        private final Context context;

        public StringCodeWriter(Context context) {
            this.context = context;
        }

        @Override
        public OutputStream openBinary(JPackage pkg, String fileName) {
            String baseClassName = FilenameUtils.getBaseName(fileName);
            if ("ObjectFactory".equals(baseClassName)) {
                return context.getObjectFactoryCode();
            }
            else if ("package-info".equals(baseClassName)) {
                return context.getPackageInfoCode();
            }
            DynamicClassName dcn = DynamicClassName.forClassName(pkg.name(), baseClassName);
            return context.getClassesMap().get(dcn).getJavaCode();
        }

        @Override
        public void close() {

        }
    }
}

@Component
class BeanClassNameResolver implements JavaCodeFromXsdService.ClassNameResolver {
    @Autowired
    private DynamicClassRepository dynamicClassRepository;

    @Override
    public String toClassName(String packageName, String className) {
        return dynamicClassRepository.getNextSimpleClassName(DynamicClassName.forClassName(packageName, className));
    }
}
