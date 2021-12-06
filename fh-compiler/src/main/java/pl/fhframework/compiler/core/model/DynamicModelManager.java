package pl.fhframework.compiler.core.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import pl.fhframework.compiler.core.dynamic.AbstractDynamicClassAreaHandler;
import pl.fhframework.compiler.core.dynamic.DynamicClassArea;
import pl.fhframework.compiler.core.dynamic.DynamicClassFileDescriptor;
import pl.fhframework.compiler.core.dynamic.DynamicClassMetadata;
import pl.fhframework.compiler.core.dynamic.dependency.DependenciesContext;
import pl.fhframework.compiler.core.generator.EnumsTypeProvider;
import pl.fhframework.compiler.core.generator.EnumsTypeProviderSpring;
import pl.fhframework.compiler.core.generator.ICollapsePropertiesToMethodName;
import pl.fhframework.compiler.core.generator.JavaCodeFromXsdService;
import pl.fhframework.compiler.core.model.generator.DynamicModelClassJavaGenerator;
import pl.fhframework.compiler.core.model.meta.AttributeTag;
import pl.fhframework.compiler.core.model.meta.ClassTag;
import pl.fhframework.compiler.core.model.meta.RelationTag;
import pl.fhframework.ReflectionUtils;
import pl.fhframework.core.FhException;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.core.generator.GenerationContext;
import pl.fhframework.core.io.FhResource;
import pl.fhframework.core.maps.features.GeometryType;
import pl.fhframework.core.maps.features.json.FeatureClassRegistry;
import pl.fhframework.core.model.meta.ModelMetadataRegistry;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.subsystems.Subsystem;

import java.io.File;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Forms manager which creates new form instances from XML or compiled classes. Manages compiled
 * classes' cache.
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON) // must be a singleton
public class DynamicModelManager extends AbstractDynamicClassAreaHandler<DynamicModelMetadata> {
    public static final Class<?> ENUM_HINT_TYPE = IEnumHintType.class;
    public static final String ENUM_HINT_TYPE_NAME = "Enums...";

    @Autowired
    private DynamicModelXmlManager xmlReader;

    @Autowired
    private ClassModelReferenceFactory descriptorClassRefMapper;

    @Autowired(required = false)
    private DynamicModelListener dynamicModelListener;

    @Autowired(required = false)
    private FeatureClassRegistry featureClassRegistry;

    @Autowired
    private DynamicFeatureClassResolver featureClassResolver;

    @Autowired(required = false)
    private JavaCodeFromXsdService javaCodeFromXsdService;

    @Autowired
    private EnumsTypeProvider enumsTypeProvider;

    @Autowired
    private EnumsTypeProviderSpring enumsTypeProviderSpring;

    private Set<String> lookedUpXsds = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private static interface IEnumHintType extends ICollapsePropertiesToMethodName {}

    public DynamicModelManager() {
        super(DmoExtension.MODEL_FILENAME_EXTENSION, DynamicClassArea.MODEL, false);
    }

    @Override
    public List<Class<?>> listAreaStaticClasses(Subsystem subsystem) {
        ModelMetadataRegistry.INSTANCE.getStaticEntities(subsystem).forEach(aClass -> {
            if (dynamicModelListener != null) {
                dynamicModelListener.newClassLoaded(aClass, null);
            }
        });
        return Collections.emptyList();
    }

    @Override
    public DynamicModelMetadata readMetadata(DynamicClassFileDescriptor file) {
        DynamicModelMetadata metadata = new DynamicModelMetadata();
        DynamicClassName className = DynamicClassName.forXmlFile(file.getRelativePath(), DmoExtension.MODEL_FILENAME_EXTENSION);

        metadata.setDynamicClassName(className);
        metadata.setDynamicClass(xmlReader.readDynamicClassModel(file.getResource().toExternalPath()));
        metadata.getDynamicClass().setReference(descriptorClassRefMapper.createFrom(file));

        if (metadata.getDynamicClass().isXsdSchema()) {
            if (!javaCodeFromXsdService.isRegistered(metadata.getDynamicClassName())) {
                String xsdPackageId = metadata.getDynamicClassName().getPackageName() + "." + metadata.getDynamicClass().getXsdSubPackage() + ";" + String.join(";", metadata.getDynamicClass().getXsdSchemaPackage());
                if (!lookedUpXsds.contains(xsdPackageId)) {
                    lookedUpXsds.add(xsdPackageId);
                    FhResource sciezkaBazowa = file.getSubsystem().getBasePath();
                    FhResource path = sciezkaBazowa.resolve(Paths.get(
                            file.getSubsystem().getBasePackage().replace(".", File.separator),
                            metadata.getDynamicClass().getXsdSubPackage().replace(".", File.separator)).toString());

                    JavaCodeFromXsdService.Context context = JavaCodeFromXsdService.Context.builder().
                            subsystem(file.getSubsystem()).
                            packageName(file.getSubsystem().getBasePackage()).
                            subPackageName(metadata.getDynamicClass().getXsdSubPackage()).
                            xsdTempDir(Paths.get(path.toExternalPath())).
                            xsdFiles(metadata.getDynamicClass().getXsdSchemaPackage().stream().map(xsdFile -> Paths.get(path.toExternalPath(), xsdFile).toFile()).collect(Collectors.toList())).
                            build();
                    try {
                        javaCodeFromXsdService.generateCode(context);
                        javaCodeFromXsdService.storeClasses(context);
                    } catch (Exception e) {
                        throw new FhException(e);
                    }
                }
            }
        }
        metadata.setDependencies(provideDependencies(metadata.getDynamicClass()));
        return metadata;
    }

    private Set<DynamicClassName> provideDependencies(ClassTag classTag) {
        if (classTag.isXsdSchema()) {
            return javaCodeFromXsdService.provideDependencies(DynamicClassName.forClassName(classTag.getId()));
        }

        Set<DynamicClassName> data = new HashSet<>();
        data.addAll(getClassesOfRelations(classTag));
        data.addAll(getClassesOfAttributes(classTag));
        getParentDependency(classTag).ifPresent(data::add);
        if (classTag.isGeometryType(GeometryType.FeatureCollection)) {
            provideFeatureDependencies(classTag, data);
        }
        return data;
    }

    private void provideFeatureDependencies(ClassTag classTag, Set<DynamicClassName> data) {
        if (!StringUtils.isNullOrEmpty(classTag.getGeoTypeDiscriminator())) {
            data.add(DynamicClassName.forClassName(classTag.getGeoTypeDiscriminator()));
        }
        data.addAll(classTag.getFeaturesClass().stream().map(DynamicClassName::forClassName).collect(Collectors.toList()));
    }

    private Optional<DynamicClassName> getParentDependency(ClassTag classTag) {
        DynamicClassName dynamicClassName = null;
        if (!StringUtils.isNullOrEmpty(classTag.getParent())) {
            dynamicClassName = DynamicClassName.forClassName(classTag.getParent());
        }
        return Optional.ofNullable(dynamicClassName);
    }

    private Set<DynamicClassName> getClassesOfRelations(ClassTag classTag) {
        Set<DynamicClassName> data = new HashSet<>();
        if (!CollectionUtils.isEmpty(classTag.getRelationTags())) {
            for (RelationTag relationTag : classTag.getRelationTags()) {
                String oppositeClassTo = relationTag.getOppositeClassTo(classTag.getId());
                if (!Objects.equals(oppositeClassTo, classTag.getId())) {
                    DynamicClassName dynamicClassName = DynamicClassName.forClassName(oppositeClassTo);
                    data.add(dynamicClassName);
                }
            }
        }
        return data;
    }

    private Set<DynamicClassName> getClassesOfAttributes(ClassTag classTag) {
        Set<DynamicClassName> data = new HashSet<>();
        if (!CollectionUtils.isEmpty(classTag.getAttributeTags())) {
            for (AttributeTag attributeTag : classTag.getAttributeTags()) {
                if (!DynamicModelClassJavaGenerator.TYPE_MAPPER.containsKey(attributeTag.getType())) {
                    DynamicClassName dynamicClassName = DynamicClassName.forClassName(attributeTag.getType());
                    data.add(dynamicClassName);
                }
            }
        }
        return data;
    }

    @Override
    public String generateClass(DynamicModelMetadata metadata, String newClassPackage, String newClassName, GenerationContext xmlTimestampMethod, DependenciesContext dependenciesContext) {
        if (metadata.getDynamicClass().isXsdSchema()) {
            return javaCodeFromXsdService.getClassDescriptor(metadata.getDynamicClassName()).getJavaCode().toString();
        }

        DynamicModelClassJavaGenerator generator = new DynamicModelClassJavaGenerator(
                metadata.getDynamicClass(), newClassName, metadata.getDynamicClassName().getBaseClassName(),
                newClassPackage, xmlTimestampMethod, dependenciesContext);
        return generator.generateClass();
    }

    @Override
    public Instant getLastKnownTimestamp(DynamicClassFileDescriptor xmlFile, DynamicClassMetadata metadata) {
        if (((DynamicModelMetadata)metadata).getDynamicClass().isXsdSchema()) {
            return javaCodeFromXsdService.getClassDescriptor(metadata.getDynamicClassName()).getTimestamp();
        }
        return super.getLastKnownTimestamp(xmlFile, metadata);
    }

    @Override
    public void postLoad(DynamicClassFileDescriptor xmlFile, Class<?> clazz, DynamicClassMetadata metadata) {
        super.postLoad(xmlFile, clazz, metadata);
        if (dynamicModelListener != null) {
            dynamicModelListener.newClassLoaded(clazz, (DynamicModelMetadata) metadata);
        }
    }

    @Override
    public void postRegisterDynamicClass(DynamicModelMetadata metadata) {
        super.postRegisterDynamicClass(metadata);

        registerFeatureClass(metadata);
        enumsTypeProvider.refresh();
        enumsTypeProviderSpring.refresh();
    }


    @Override
    public void postUpdateDynamicClass(DynamicModelMetadata metadata) {
        super.postUpdateDynamicClass(metadata);

        registerFeatureClass(metadata);
        enumsTypeProvider.refresh();
        enumsTypeProviderSpring.refresh();
    }

    @Override
    public void postUnregisterDynamicClass(DynamicModelMetadata metadata) {
        super.postUnregisterDynamicClass(metadata);

        enumsTypeProvider.refresh();
        enumsTypeProviderSpring.refresh();
    }

    protected void registerFeatureClass(DynamicModelMetadata metadata) {
        if (featureClassRegistry != null && !metadata.getDynamicClass().isGeometryType(GeometryType.FeatureCollection)) {
            String geoTypeDiscriminator = metadata.getDynamicClass().getGeoTypeDiscriminator();
            if (StringUtils.isNullOrEmpty(geoTypeDiscriminator)) {
                geoTypeDiscriminator = metadata.getDynamicClassName().toFullClassName();
            }
            featureClassRegistry.registerFeatureClass(geoTypeDiscriminator, () -> featureClassResolver.getDynamicFeatureClass(metadata.getDynamicClassName()));
        }
    }

    @Override
    public Optional<Class<?>> getReadyClass(DynamicClassMetadata metadata) {
        ClassTag classTag = ((DynamicModelMetadata)metadata).getDynamicClass();
        if (classTag.isModifiedStatic() && !classTag.isModifiedStaticCode()) {
            return Optional.of(ReflectionUtils.getClassForName(classTag.getId()));
        }

        return super.getReadyClass(metadata);
    }
}
