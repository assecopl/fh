package pl.fhframework.compiler.core.model.generator;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.springframework.core.io.Resource;
import pl.fhframework.compiler.core.generator.AbstractJavaClassCodeGenerator;
import pl.fhframework.compiler.core.generator.DynamicClassCompiler;
import pl.fhframework.compiler.core.model.meta.AttributeTag;
import pl.fhframework.compiler.core.model.meta.ClassTag;
import pl.fhframework.compiler.core.model.meta.RelationTag;
import pl.fhframework.ReflectionUtils;
import pl.fhframework.aspects.snapshots.model.ISnapshotEnabled;
import pl.fhframework.compiler.core.dynamic.dependency.DependenciesContext;
import pl.fhframework.compiler.core.dynamic.dependency.DependencyResolution;
import pl.fhframework.core.FhException;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.core.generator.GenerationContext;
import pl.fhframework.core.generator.ModelElement;
import pl.fhframework.core.generator.ModelElementType;
import pl.fhframework.core.maps.features.GeometryType;
import pl.fhframework.core.maps.features.IFeature;
import pl.fhframework.core.maps.features.geometry.IGeometry;
import pl.fhframework.core.maps.features.json.FeatureClassDiscriminator;
import pl.fhframework.core.uc.dynamic.model.element.attribute.TypeMultiplicityEnum;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.model.forms.PageModel;

import javax.persistence.*;
import javax.validation.Valid;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class DynamicModelClassJavaGenerator extends AbstractJavaClassCodeGenerator {

    private static final String BASIC_ENTITY_CLASS = "pl.fhframework.fhPersistence.core.BasePersistentObject";

    private static final String BASIC_FEATURE_ENTITY_CLASS = "pl.fhframework.fhPersistence.maps.features.PersistentMapFeature";

    private static final String BASIC_FEATURE_COLLECTION_ENTITY_CLASS = "pl.fhframework.fhPersistence.maps.features.PersistentFeatureCollection";

    private ClassTag classTag;
    private GenerationContext xmlTimestampMethod;
    private DependenciesContext dependenciesContext;
    private boolean jpaClass;

    public DynamicModelClassJavaGenerator(ClassTag classTag, String targetClassName, String baseClassName, String packageName, GenerationContext xmlTimestampMethod, DependenciesContext dependenciesContext) {
        super(packageName, targetClassName, baseClassName);
        this.classTag = classTag;
        this.xmlTimestampMethod = xmlTimestampMethod;
        this.dependenciesContext = dependenciesContext;
        this.jpaClass = isJpaClass(classTag);
    }

    public static final Map<String, Type> TYPE_MAPPER = new HashMap<>();
    public static final Map<Type, String> TYPE_MAPPER_REVERSE = new HashMap<>();
    public static final Map<String, String> TYPE_NEW_INSTANCE = new HashMap<>();

    static {
        TYPE_MAPPER.put("Short", Short.class);
        TYPE_MAPPER_REVERSE.put(Short.class, "Short");
        TYPE_MAPPER.put("Integer", Integer.class);
        TYPE_MAPPER_REVERSE.put(Integer.class, "Integer");
        TYPE_MAPPER.put("Boolean", Boolean.class);
        TYPE_MAPPER_REVERSE.put(Boolean.class, "Boolean");
        TYPE_MAPPER.put("Float", Float.class);
        TYPE_MAPPER_REVERSE.put(Float.class, "Float");
        TYPE_MAPPER.put("Long", Long.class);
        TYPE_MAPPER_REVERSE.put(Long.class, "Long");
        TYPE_MAPPER.put("Double", Double.class);
        TYPE_MAPPER_REVERSE.put(Double.class, "Double");
        TYPE_MAPPER.put("BigDecimal", BigDecimal.class);
        TYPE_MAPPER_REVERSE.put(BigDecimal.class, "BigDecimal");
        TYPE_MAPPER.put("Date", LocalDate.class);   // need to support JPA 2.0 as well (remove converter)
        TYPE_MAPPER_REVERSE.put(LocalDate.class, "Date");   // need to support JPA 2.0 as well (remove converter)
        TYPE_MAPPER.put("Timestamp", Date.class);
        TYPE_MAPPER_REVERSE.put(Date.class, "Timestamp");
        TYPE_MAPPER.put("LocalDateTime", LocalDateTime.class);
        TYPE_MAPPER_REVERSE.put(LocalDateTime.class, "LocalDateTime");
        TYPE_MAPPER.put("String", String.class);
        TYPE_MAPPER_REVERSE.put(String.class, "String");
        TYPE_MAPPER.put("Resource", Resource.class);
        TYPE_MAPPER_REVERSE.put(Resource.class, "Resource");

        TYPE_NEW_INSTANCE.put("Short", "0");
        TYPE_NEW_INSTANCE.put("Integer", "0");
        TYPE_NEW_INSTANCE.put("Boolean", "false");
        TYPE_NEW_INSTANCE.put("Float", "0.0f");
        TYPE_NEW_INSTANCE.put("Long", "0l");
        TYPE_NEW_INSTANCE.put("Double", "0.0");
        TYPE_NEW_INSTANCE.put("BigDecimal", String.format("%s.ZERO", BigDecimal.class.getName()));
        TYPE_NEW_INSTANCE.put("Date", String.format("%s.now()", LocalDate.class.getName()));
        TYPE_NEW_INSTANCE.put("Timestamp", String.format("new %s()", Date.class.getName()));
        TYPE_NEW_INSTANCE.put("LocalDateTime", String.format("new %s()", LocalDateTime.class.getName()));
        TYPE_NEW_INSTANCE.put("String", String.format("new %s()", String.class.getName()));
    }

    @Override
    protected void generateClassBody() {
        generateClassSignature(classTag);

        if (classTag.isEnumeration()) {
            constructorSignatureSection.addLine("%s()", targetClassName);
        }
        else {
            constructorSignatureSection.addLine("public %s()", targetClassName);
        }

        // add static method returning XML timestamps
        if (xmlTimestampMethod != null) {
            methodSection.addSection(xmlTimestampMethod, 0);
        }

        if (classTag.isEnumeration()) {
            processConstants();
        }
        else {
            processAttributes(fieldSection, classTag.getAttributeTags());
            processRelations(fieldSection, classTag);
        }

        if (classTag.isGeometryType(GeometryType.FeatureCollection)) {
            processFeaturesClass(methodSection, classTag.getFeaturesClass(), classTag.getRelationTags());
        }

        if (Boolean.TRUE.equals(classTag.getPersisted())) {
            addDefaultGetters(fieldSection, getOverridedAttributes(classTag));
        }
    }

    private List<String> getOverridedAttributes(ClassTag classTag) {
        return classTag.getAttributeTags().stream().
                filter(attributeTag ->  !StringUtils.isNullOrEmpty(attributeTag.getJsonProperty())).map(AttributeTag::getJsonProperty).collect(Collectors.toList());
    }

    private void generateClassSignature(ClassTag classTag) {
        if (jpaClass) {
            classSignatureSection.addLine("@%s", toTypeLiteral(Entity.class));
            classSignatureSection.addLine("@%s(name = \"SEQUENCE_GENERATOR\", sequenceName = \"FH_DEFAULT_SEQ_ID\")", toTypeLiteral(SequenceGenerator.class));
        }
        if (classTag.isGeometryType()) {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("@%s(", toTypeLiteral(FeatureClassDiscriminator.class)));
            boolean discriminatorKey = false;
            if (!StringUtils.isNullOrEmpty(classTag.getGeoTypeDiscriminatorField())) {
                sb.append(String.format("classDiscriminatorKey = \"%s\"", classTag.getGeoTypeDiscriminatorField()));
                discriminatorKey = true;
            }
            if (classTag.isGeometryType(GeometryType.FeatureCollection)) {
                if (!StringUtils.isNullOrEmpty(classTag.getGeoTypeDiscriminator())) {
                    if (discriminatorKey) {
                        sb.append(", ");
                    }
                    sb.append(String.format("featureClass = \"%s\"", classTag.getGeoTypeDiscriminator()));
                }
            }
            else {
                if (discriminatorKey) {
                    sb.append(", ");
                }
                sb.append(String.format("featureClass = \"%s\"", StringUtils.isNullOrEmpty(classTag.getGeoTypeDiscriminatorField()) ? classTag.getId() : classTag.getGeoTypeDiscriminator()));
            }
            sb.append(")");
            classSignatureSection.addLine(sb.toString());
        }
        classSignatureSection.addLine("@%s(generator = %s.class)", toTypeLiteral(JsonIdentityInfo.class), toTypeLiteral(ObjectIdGenerators.IntSequenceGenerator.class));

        if (classTag.isEnumeration()) {
            classSignatureSection.addLine("public enum %s", this.targetClassName);
        }
        else if (classTag.isModifiedStatic()) { // extends static class
            classSignatureSection.addLine("public class %s extends %s", this.targetClassName, classTag.getName());
        } else if (!StringUtils.isNullOrEmpty(classTag.getParent())) { // extends parent class (does not matter if it's persistable or not
            classSignatureSection.addLine("public class %s extends %s", this.targetClassName, getResolvedClassName(classTag.getParent()));
        } else if (classTag.isGeometryType()) { // extends base entity class for persistable dynamic class
            if (classTag.isGeometryType(GeometryType.FeatureCollection)) {
                classSignatureSection.addLine("public class %s extends %s", this.targetClassName, BASIC_FEATURE_COLLECTION_ENTITY_CLASS);
            } else {
                classSignatureSection.addLine("public class %s extends %s", this.targetClassName, BASIC_FEATURE_ENTITY_CLASS);
            }
        } else if (Boolean.TRUE.equals(classTag.getPersisted())) { // extends base entity class for persistable dynamic class
            classSignatureSection.addLine("public class %s extends %s", this.targetClassName, BASIC_ENTITY_CLASS);
        } else {
            classSignatureSection.addLine("public class %s implements %s, java.io.Serializable", this.targetClassName, toTypeLiteral(ISnapshotEnabled.class));
        }
    }

    private void processAttributes(GenerationContext fieldSection, List<AttributeTag> attributeTags) {
        for (AttributeTag attributeTag : attributeTags) {
            String fieldName = attributeTag.getName().replaceAll(" ", "_");
            if (!fieldName.matches("[a-zA-Z_].*")) {
                fieldName = "_" + fieldName;
            }

            String fieldType = attributeTag.getType();
            Type returnedType = TYPE_MAPPER.get(fieldType);

            String returnedTypeLiteral;
            if (returnedType == null) {
                returnedTypeLiteral = dependenciesContext.resolve(DynamicClassName.forClassName(fieldType)).getFullClassName();
            }
            else {
                returnedTypeLiteral = toTypeLiteral(returnedType);
            }

            fieldSection.addLine();
            if (jpaClass) {
                if (attributeTag.getMultiplicity() != null && attributeTag.getMultiplicity() != TypeMultiplicityEnum.Element) {
                    throw new FhException("Entity class cannot have collection of simple type. Attribute " + attributeTag.getName());
                }
                fieldSection.addLine("@%s", toTypeLiteral(Column.class));
                if (returnedType == null) { // it's enum
                    fieldSection.addLine("@%s(%s.STRING)", toTypeLiteral(Enumerated.class), toTypeLiteral(EnumType.class));
                }
                else if (ReflectionUtils.isAssignablFrom(LocalDate.class, returnedType)) {
                    fieldSection.addLine("@%s(converter = pl.fhframework.jpa.converter.LocalDateAttributeConverter.class)", toTypeLiteral(Convert.class));
                } else if (ReflectionUtils.isAssignablFrom(LocalDateTime.class, returnedType)) {
                    fieldSection.addLine("@%s(converter = pl.fhframework.jpa.converter.LocalDateTimeAttributeConverter.class)", toTypeLiteral(Convert.class));
                }
                else if (ReflectionUtils.isAssignablFrom(Date.class, returnedType)) {
                    fieldSection.addLine("@%s(%s.TIMESTAMP)", toTypeLiteral(Temporal.class), toTypeLiteral(TemporalType.class));
                }
            }
            fieldSection.addLine("@%s(\"%s\")", toTypeLiteral(JsonProperty.class), StringUtils.isNullOrEmpty(attributeTag.getJsonProperty()) ? attributeTag.getName() : attributeTag.getJsonProperty());
            /*if (attributeTag.isMandatory()) {
                if (returnedType == String.class) {
                    fieldSection.addLine("@%s", toTypeLiteral(NotEmpty.class));
                }
                else {
                    fieldSection.addLine("@%s", toTypeLiteral(NotNull.class));
                }
            }*/
            String typeLiteral = null;
            if (attributeTag.getMultiplicity() == TypeMultiplicityEnum.Collection) {
                typeLiteral = String.format("java.util.List<%s>", returnedTypeLiteral);
            } else if (attributeTag.getMultiplicity() == TypeMultiplicityEnum.MultiplePageable) {
                typeLiteral = String.format("%s<%s>", PageModel.class.getName(), returnedTypeLiteral);
            } else {
                typeLiteral = String.format("%s", returnedTypeLiteral);
            }
            fieldSection.addLine("private %s %s;", typeLiteral, fieldName);

            generateSetterMethod(fieldName, typeLiteral);
            generateGetterMethod(fieldName, fieldType, typeLiteral);
        }
        if (classTag.isGeometryType() && !classTag.isGeometryType(GeometryType.FeatureCollection)) {
            if (getOverridedAttributes(classTag).contains("clientId")) {
                addJsonIgnore(methodSection);
                generateDefaultGetterMethod("clientId", toTypeLiteral(String.class));
            }
            if (StringUtils.isNullOrEmpty(classTag.getParent())) {
                generateAllowedGeometries(classTag.getGeometryTypes());
            }
        }
    }

    private void processConstants() {
        for (String constant : classTag.getConstants()) {
            fieldSection.addLineWithIndent(1, "%s,", constant);
        }
        fieldSection.addLineWithIndent(1, ";");
    }


    private void addDefaultGetters(GenerationContext fieldSection, List<String> ovveriededAttrs) {
        if (ovveriededAttrs.contains("id")) {
            addJsonIgnore(methodSection);
        }
        generateDefaultGetterMethod("id", "Long");
        if (ovveriededAttrs.contains("entityId")) {
            addJsonIgnore(methodSection);
        }
        generateDefaultGetterMethod("entityId", "Long");
        if (ovveriededAttrs.contains("version")) {
            addJsonIgnore(methodSection);
        }
        generateDefaultGetterMethod("version", "Long");
    }

    private void addJsonIgnore(GenerationContext generationContext) {
        generationContext.addLine("@%s", toTypeLiteral(JsonIgnore.class));
    }

    private void processRelations(GenerationContext fieldSection, ClassTag classTag) {
        for (RelationTag relationTag : classTag.getRelationTags()) {
            processRelation(fieldSection, relationTag);
        }
    }

    private void processRelation(GenerationContext fieldSection, RelationTag relationTag) {
        switch (relationTag.relationType()) {
            case ONE_TO_ONE: {
                if (isProcessingSourceClass(relationTag)) {
                    generateObjectRelation(fieldSection, relationTag.getTarget(), relationTag.getTargetRoleName(), OneToOne.class, null, Boolean.TRUE.equals(relationTag.getComposition()), isBidirectional(relationTag), true);
                }
                if (isProcessingTargetClass(relationTag) && isBidirectional(relationTag)) {
                    generateObjectRelation(fieldSection, relationTag.getSource(), relationTag.getSourceRoleName(), OneToOne.class, relationTag.getTargetRoleName(), false, true, false);
                }
                break;
            }
            case ONE_TO_MANY: {
                if (isProcessingSourceClass(relationTag)) {
                    generateListRelation(fieldSection, relationTag.getTarget(), relationTag.getTargetRoleName(), OneToMany.class, isBidirectional(relationTag) ? relationTag.getSourceRoleName() : null, Boolean.TRUE.equals(relationTag.getComposition()), isBidirectional(relationTag), true);
                }
                if (isProcessingTargetClass(relationTag) && isBidirectional(relationTag)) {
                    generateObjectRelation(fieldSection, relationTag.getSource(), relationTag.getSourceRoleName(), ManyToOne.class, null, false, true, false);
                }
                break;
            }
            case MANY_TO_ONE: {
                if (isProcessingSourceClass(relationTag)) {
                    generateObjectRelation(fieldSection, relationTag.getTarget(), relationTag.getTargetRoleName(), ManyToOne.class, null, Boolean.TRUE.equals(relationTag.getComposition()), isBidirectional(relationTag), true);
                }
                if (isProcessingTargetClass(relationTag) && isBidirectional(relationTag)) {
                    generateListRelation(fieldSection, relationTag.getSource(), relationTag.getSourceRoleName(), OneToMany.class, relationTag.getTargetRoleName(), false, true, false);
                }
                break;
            }
            case MANY_TO_MANY: {
                if (isProcessingSourceClass(relationTag)) {
                    generateListRelation(fieldSection, relationTag.getTarget(), relationTag.getTargetRoleName(), ManyToMany.class, null, Boolean.TRUE.equals(relationTag.getComposition()), isBidirectional(relationTag), true);
                }
                if (isProcessingTargetClass(relationTag) && isBidirectional(relationTag)) {
                    generateListRelation(fieldSection, relationTag.getSource(), relationTag.getSourceRoleName(), ManyToMany.class, relationTag.getTargetRoleName(), false, true, false);
                }
                break;
            }
            default: {
                throw new IllegalArgumentException("Not supported relation type");
            }
        }
    }

    private boolean isBidirectional(RelationTag relationTag) {
        return Boolean.TRUE.equals(relationTag.getBidirectional());
    }

    private boolean isProcessingTargetClass(RelationTag relationTag) {
        return relationTag.getTarget().equals(this.classTag.getId());
    }

    private boolean isProcessingSourceClass(RelationTag relationTag) {
        return relationTag.getSource().equals(this.classTag.getId());
    }

    private void generateObjectRelation(GenerationContext fieldSection, String fullClassName, String relationName, Class<? extends Annotation> annotation, String mappedBy, boolean composition, boolean biDirect, boolean owner) {
        fieldSection.addLine();
        String resolvedClassName = getResolvedClassName(fullClassName);

        if (mappedBy != null) {
            fieldSection.addLine("@%s(mappedBy = \"%s\"%s)", annotation.getName(), mappedBy, getCompositionStr(annotation, composition, true));
        } else {
            fieldSection.addLine("@%s(%s)", annotation.getName(), getCompositionStr(annotation, composition, false));
        }
        fieldSection.addLine("@%s", toTypeLiteral(Valid.class));
        fieldSection.addLine("private %s %s;", resolvedClassName, relationName);

        generateSetterMethod(relationName, resolvedClassName);
        generateObjectGetMethod(relationName, resolvedClassName);
    }

    private void generateListRelation(GenerationContext fieldSection, String fullClassName, String relationName, Class<? extends Annotation> annotation, String mappedBy, boolean composition, boolean biDirect, boolean owner) {
        fieldSection.addLine();
        String resolvedClassName = getResolvedClassName(fullClassName);

        if (mappedBy != null) {
            fieldSection.addLine("@%s(mappedBy = \"%s\"%s)", annotation.getName(), mappedBy, getCompositionStr(annotation, composition, true));
        } else {
            fieldSection.addLine("@%s(%s)", annotation.getName(), getCompositionStr(annotation, composition, false));
        }
        fieldSection.addLine("@%s", toTypeLiteral(Valid.class));
        fieldSection.addLine("private %s<%s> %s = new %s<>();"
                , toTypeLiteral(List.class), resolvedClassName, relationName, toTypeLiteral(ArrayList.class));

        generateListSetterMethod(relationName, resolvedClassName);
        generateListGetMethod(relationName, resolvedClassName);
    }

    private String getCompositionStr(Class<? extends Annotation> annotation, boolean composition, boolean comma) {
        if (composition) {
            if (OneToOne.class.equals(annotation) || OneToMany.class.equals(annotation)) {
                return String.format("%scascade = javax.persistence.CascadeType.ALL, orphanRemoval = true", comma ? ", " : "");
            }
            return String.format("%scascade = javax.persistence.CascadeType.ALL", comma ? ", " : "");
        }

        return "";
    }

    private void generateListGetMethod(String fieldName, String fieldType) {
        String getterName = ReflectionUtils.getObjectGetterName(fieldName);

        methodSection.addLine("@%s(type=%s.%s)", toTypeLiteral(ModelElement.class), toTypeLiteral(ModelElementType.class), ModelElementType.RELATION.name());
        methodSection.addLine("public %s<%s> %s() {", toTypeLiteral(List.class), fieldType, getterName);
        methodSection.addLineWithIndent(1, "return this.%s;", fieldName);
        methodSection.addLine("}", fieldType, getterName);
        methodSection.addLine();
    }

    private void generateObjectGetMethod(String fieldName, String fieldType) {
        String getterName = ReflectionUtils.getObjectGetterName(fieldName);
        methodSection.addLine("@%s(type=%s.%s)", toTypeLiteral(ModelElement.class), toTypeLiteral(ModelElementType.class), ModelElementType.RELATION.name());
        methodSection.addLine("public %s %s() {", fieldType, getterName);
        methodSection.addLineWithIndent(1, "return this.%s;", fieldName);
        methodSection.addLine("}");
        methodSection.addLine();
    }

    private void generateGetterMethod(String fieldName, String fieldType, String typeLiteral) {
        Type returnedType = TYPE_MAPPER.get(fieldType);
        Class<?> rawClass = ReflectionUtils.getRawClass(returnedType);
        String getterName = ReflectionUtils.getGetterName(fieldName, rawClass);

        methodSection.addLine("@%s(type=%s.%s)", toTypeLiteral(ModelElement.class), toTypeLiteral(ModelElementType.class), ModelElementType.BUSINESS_PROPERTY.name());
        methodSection.addLine("public %s %s() {", typeLiteral, getterName);
        methodSection.addLineWithIndent(1, "return this.%s;", fieldName);
        methodSection.addLine("}");
        methodSection.addLine();
    }

    private void generateDefaultGetterMethod(String fieldName, String fieldType) {
        Type returnedType = TYPE_MAPPER.get(fieldType);
        Class<?> rawClass = ReflectionUtils.getRawClass(returnedType);
        String getterName = ReflectionUtils.getGetterName(fieldName, rawClass);

        methodSection.addLine("@%s(type=%s.%s)", toTypeLiteral(ModelElement.class), toTypeLiteral(ModelElementType.class), ModelElementType.OTHER_PROPERTY.name());
        methodSection.addLine("public %s %s() {", toTypeLiteral(returnedType), getterName);
        methodSection.addLineWithIndent(1, "return super.%s();", getterName);
        methodSection.addLine("}");
        methodSection.addLine();
    }

    private void generateSetterMethod(String fieldName, String fieldType) {
        String setterName = ReflectionUtils.getSetterName(fieldName);
        methodSection.addLine("public void %s(%s %s) {", setterName, fieldType, fieldName);
        methodSection.addLineWithIndent(1, "this.%s = %s;", fieldName, fieldName);
        methodSection.addLine("}");
        methodSection.addLine();
    }

    private void generateAllowedGeometries(Set<GeometryType> geometryTypes) {
        methodSection.addLine("public %s<%s<? extends %s>> allowedGeometries() {", toTypeLiteral(List.class), toTypeLiteral(Class.class), toTypeLiteral(IGeometry.class));
        methodSection.addLineWithIndent(1, "return %s.asList(%s);", toTypeLiteral(Arrays.class),
                geometryTypes.stream().map(geometryType -> geometryType.getImplClass() + ".class").collect(Collectors.joining(", ")));
        methodSection.addLine("}");
        methodSection.addLine();
    }

    private void generateListSetterMethod(String fieldName, String fieldType) {
        String setterName = ReflectionUtils.getSetterName(fieldName);

        methodSection.addLine("public void %s(%s<%s> %s) {", setterName, toTypeLiteral(List.class), fieldType, fieldName);
        methodSection.addLineWithIndent(1, "this.%s = %s;", fieldName, fieldName);
        methodSection.addLine("}");
        methodSection.addLine();
    }

    private String getResolvedClassName(String className) {
        DependencyResolution resolve = dependenciesContext.resolve(DynamicClassName.forClassName(className));
        return resolve.getFullClassName();
    }

    private boolean isJpaClass(ClassTag classTag) {
        return DynamicClassCompiler.isCoreLiteTarget() && Objects.equals(Boolean.TRUE, classTag.getPersisted());
    }

    private void processFeaturesClass(GenerationContext methodSection, Set<String> featuresClass, List<RelationTag> featuresFields) {
        methodSection.addLine("@%s(type=%s.%s)", toTypeLiteral(ModelElement.class), toTypeLiteral(ModelElementType.class), ModelElementType.BUSINESS_PROPERTY.name());
        methodSection.addLine("public %s<%s> allFeatures() {", toTypeLiteral(List.class), toTypeLiteral(IFeature.class));
        methodSection.addLineWithIndent(1, "return joinLists(%s);", featuresFields.stream().map(tag -> "get" + StringUtils.firstLetterToUpper(tag.getTargetRoleName()) + "()").collect(Collectors.joining(", ")));
        methodSection.addLine("}");
        methodSection.addLine();

        methodSection.addLine("public void addFeature(%s feature) {", toTypeLiteral(IFeature.class));
        featuresFields.forEach(field -> {
            methodSection.addLineWithIndent(1, "if (is%s(feature)) {", DynamicClassName.forClassName(field.getTarget()).getBaseClassName());
            methodSection.addLineWithIndent(2, "get%s().add((%s) feature);", StringUtils.firstLetterToUpper(field.getTargetRoleName()), getResolvedClassName(field.getTarget()));
            methodSection.addLineWithIndent(1, "}");
        });
        methodSection.addLine("}");
        methodSection.addLine();

        methodSection.addLine("public void removeFeature(%s feature) {", toTypeLiteral(IFeature.class));
        featuresFields.forEach(field -> {
            methodSection.addLineWithIndent(1, "if (is%s(feature)) {", DynamicClassName.forClassName(field.getTarget()).getBaseClassName());
            methodSection.addLineWithIndent(2, "get%s().remove(feature);", StringUtils.firstLetterToUpper(field.getTargetRoleName()));
            methodSection.addLineWithIndent(1, "}");
        });
        methodSection.addLine("}");
        methodSection.addLine();

        featuresClass.forEach(featureClass -> processFeatureClass(methodSection, featureClass));
    }

    private void processFeatureClass(GenerationContext methodSection, String featureClass) {
        DynamicClassName className = DynamicClassName.forClassName(featureClass);
        String resolvedClassName = getResolvedClassName(featureClass);

        generateAsFeature(methodSection, resolvedClassName, className.getBaseClassName());
        generateIsFeature(methodSection, resolvedClassName, className.getBaseClassName());
    }

    private void generateGetFeaturesList(GenerationContext methodSection, String resolvedClassName, String baseClassName) {
        methodSection.addLine("public %s<%s> %sList() {", toTypeLiteral(List.class), resolvedClassName, StringUtils.firstLetterToLower(baseClassName));
        methodSection.addLineWithIndent(1, "return allFeatures().stream().filter(%s.class::isInstance).map(%s.class::cast).collect(%s.toList());", resolvedClassName, resolvedClassName, toTypeLiteral(Collectors.class));
        methodSection.addLine("}");
        methodSection.addLine();
    }

    private void generateAsFeature(GenerationContext methodSection, String resolvedClassName, String baseClassName) {
        methodSection.addLine("public %s as%s(%s feature) {", resolvedClassName, baseClassName, toTypeLiteral(IFeature.class));
        methodSection.addLineWithIndent(1, "return (%s) feature;", resolvedClassName);
        methodSection.addLine("}");
        methodSection.addLine();
    }

    private void generateIsFeature(GenerationContext methodSection, String resolvedClassName, String baseClassName) {
        methodSection.addLine("public boolean is%s(%s feature) {", baseClassName, toTypeLiteral(IFeature.class));
        methodSection.addLineWithIndent(1, "return (feature instanceof %s);", resolvedClassName);
        methodSection.addLine("}");
        methodSection.addLine();
    }
}
