package pl.fhframework.compiler.core.model.generator.spring;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.springframework.core.io.Resource;
import pl.fhframework.compiler.core.generator.AbstractJavaClassCodeGenerator;
import pl.fhframework.compiler.core.generator.MetaModelService;
import pl.fhframework.compiler.core.generator.ModuleMetaModel;
import pl.fhframework.compiler.core.generator.model.data.ClassMm;
import pl.fhframework.compiler.core.model.SimpleType;
import pl.fhframework.compiler.core.tools.JavaNamesUtils;
import pl.fhframework.core.FhException;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.core.uc.dynamic.model.element.attribute.TypeMultiplicityEnum;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.model.forms.PageModel;

import javax.persistence.*;
import javax.validation.Valid;
import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class ModelClassSpringGenerator extends AbstractJavaClassCodeGenerator {
    public static final Map<String, Class<?>> TYPE_MAPPER = new HashMap<>();

    static {
        TYPE_MAPPER.put("Short", Short.class);
        TYPE_MAPPER.put("Integer", Integer.class);
        TYPE_MAPPER.put("Boolean", Boolean.class);
        TYPE_MAPPER.put("Float", Float.class);
        TYPE_MAPPER.put("Long", Long.class);
        TYPE_MAPPER.put("Double", Double.class);
        TYPE_MAPPER.put("BigDecimal", BigDecimal.class);
        TYPE_MAPPER.put("Date", LocalDate.class);   // need to support JPA 2.0 as well (remove converter)
        TYPE_MAPPER.put("Timestamp", Date.class);
        TYPE_MAPPER.put("LocalDateTime", LocalDateTime.class);
        TYPE_MAPPER.put("String", String.class);
        TYPE_MAPPER.put("Resource", Resource.class);
    }

    private final ModuleMetaModel moduleMetaModel;

    private final MetaModelService metaModelService;

    private final ClassMm classMm;

    public ModelClassSpringGenerator(ClassMm classMm, ModuleMetaModel moduleMetaModel, MetaModelService metaModelService) {
        super(DynamicClassName.forClassName(classMm.getId()).getPackageName(),
                DynamicClassName.forClassName(classMm.getId()).getBaseClassName(),
                DynamicClassName.forClassName(classMm.getId()).getBaseClassName());
        this.classMm = classMm;
        this.moduleMetaModel = moduleMetaModel;
        this.metaModelService = metaModelService;
    }

    @Override
    protected void generateClassBody() {
        generateClassSignature();

        //generateConstructor();

        generateFields();
    }

    private void generateClassSignature() {
        // todo: GeometryType
        if (classMm.isPersistable()) {
            importClass("lombok.Getter", "lombok.Setter"); // only compile time
            importClass(Entity.class, SequenceGenerator.class);
            classSignatureSection.addLine("@Getter");
            classSignatureSection.addLine("@Setter");
            classSignatureSection.addLine("@%s", toTypeShortLiteral(Entity.class));
            classSignatureSection.addLine("@%s(name = \"SEQUENCE_GENERATOR\", sequenceName = \"seq_%s\")", toTypeShortLiteral(SequenceGenerator.class), targetClassName.toLowerCase());
        }
        importClass(JsonIdentityInfo.class, ObjectIdGenerators.class);
        classSignatureSection.addLine("@%s(generator = %s.class)", toTypeShortLiteral(JsonIdentityInfo.class), toTypeShortLiteral(ObjectIdGenerators.IntSequenceGenerator.class));

        if (classMm.isEnumeration()) {
            classSignatureSection.addLine("public enum %s", this.targetClassName);
        } else {
            classSignatureSection.addCode("public class %s", this.targetClassName);
            if (!StringUtils.isNullOrEmpty(classMm.getParent())) {
                importClass(classMm.getParent());
                classSignatureSection.addCode(" extends %s", getBaseName(classMm.getParent()));
            }
            classSignatureSection.addLineIfNeeded();
        }

    }

    private void generateConstructor() {
        if (classMm.isEnumeration()) {
            constructorSignatureSection.addLine("%s()", targetClassName);
        } else {
            constructorSignatureSection.addLine("public %s()", targetClassName);
        }
    }

    private void generateFields() {
        if (classMm.isSpatial()) {
            // todo:
            fieldSection.addCode("// geometry type is not supported");
            return;
        }
        if (classMm.isEnumeration()) {
            generateConstants();
        } else {
            generateAttributes();
            generateRelations();
        }
        if (classMm.isPersistable()) {
            generateId();
        }
    }

    private void generateConstants() {
        for (String constant : classMm.getConstants()) {
            fieldSection.addLineWithIndent(1, "%s,", constant);
        }
        fieldSection.addLineWithIndent(1, ";");
    }

    private void generateAttributes() {
        classMm.getAttributes().forEach(attribute -> {
            String fieldName = JavaNamesUtils.getFieldName(attribute.getName());

            String fieldType = attribute.getType();
            ClassMm fieldClass = moduleMetaModel.getMetadata(fieldType);
            if (SimpleType.isSimpleType(fieldType)) {
                fieldType = TYPE_MAPPER.get(fieldType).getName();
            }
            importClass(fieldType);

            fieldSection.addLine();
            if (classMm.isPersistable()) {
                if (attribute.getMultiplicity() != null && attribute.getMultiplicity() != TypeMultiplicityEnum.Element) {
                    throw new FhException("Entity class cannot have collection of simple type. Attribute " + attribute.getName());
                }
                importClass(Column.class);
                fieldSection.addLine("@%s", toTypeShortLiteral(Column.class));
                if (fieldClass != null && fieldClass.isEnumeration()) { // it's enum
                    importClass(Enumerated.class, EnumType.class);
                    fieldSection.addLine("@%s(%s.STRING)", toTypeShortLiteral(Enumerated.class), toTypeShortLiteral(EnumType.class));
                } else if (Objects.equals(attribute.getType(), SimpleType.TIMESTAMP.getType())) {
                    importClass(Temporal.class, TemporalType.class);
                    fieldSection.addLine("@%s(%s.TIMESTAMP)", toTypeShortLiteral(Temporal.class), toTypeShortLiteral(TemporalType.class));
                }
            }
            if (!StringUtils.isNullOrEmpty(attribute.getJsonPropertyName())) {
                importClass(JsonProperty.class);
                fieldSection.addLine("@%s(\"%s\")", toTypeShortLiteral(JsonProperty.class), attribute.getJsonPropertyName());
            }
            String typeLiteral;
            if (attribute.getMultiplicity() == TypeMultiplicityEnum.Collection) {
                importClass(List.class);
                typeLiteral = String.format("List<%s>", getBaseName(fieldType));
            } else if (attribute.getMultiplicity() == TypeMultiplicityEnum.MultiplePageable) {
                importClass(PageModel.class);
                typeLiteral = String.format("%s<%s>", toTypeShortLiteral(PageModel.class), getBaseName(fieldType));
            } else {
                typeLiteral = getBaseName(fieldType);
            }
            fieldSection.addLine("private %s %s;", typeLiteral, fieldName);
        });
    }

    private void generateRelations() {
        classMm.getRelations().forEach(relation -> {
            switch (relation.getRelationType()) {
                case ONE_TO_ONE: {
                    generateElementRelation(relation.getTarget(), relation.getTargetRoleName(), OneToOne.class, !relation.isOwner() ? relation.getSourceRoleName() : null, relation.isComposition());
                    break;
                }
                case ONE_TO_MANY: {
                    generateListRelation(relation.getTarget(), relation.getTargetRoleName(), OneToMany.class, relation.getSourceRoleName(), relation.isComposition());
                    break;
                }
                case MANY_TO_ONE: {
                    generateElementRelation(relation.getTarget(), relation.getTargetRoleName(), ManyToOne.class, null, relation.isComposition());
                    break;
                }
                case MANY_TO_MANY: {
                    generateListRelation(relation.getTarget(), relation.getTargetRoleName(), ManyToMany.class, relation.isOwner() ? relation.getSourceRoleName() : null, relation.isComposition());
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Not supported relation type");
                }
            }
        });
    }

    private void generateElementRelation(String fullClassName, String relationName, Class<? extends Annotation> annotation, String mappedBy, boolean composition) {
        fieldSection.addLine();
        importClass(annotation, Valid.class);

        generateReleationAnnotations(annotation, mappedBy, composition);
        importClass(fullClassName);
        fieldSection.addLine("private %s %s;", getBaseName(fullClassName), relationName);
    }

    private void generateListRelation(String fullClassName, String relationName, Class<? extends Annotation> annotation, String mappedBy, boolean composition) {
        fieldSection.addLine();
        importClass(annotation, Valid.class);

        generateReleationAnnotations(annotation, mappedBy, composition);
        importClass(List.class, ArrayList.class);
        importClass(fullClassName);
        fieldSection.addLine("private %s<%s> %s = new %s<>();",
                toTypeShortLiteral(List.class), getBaseName(fullClassName), relationName, toTypeShortLiteral(ArrayList.class));
    }

    private void generateReleationAnnotations(Class<? extends Annotation> annotation, String mappedBy, boolean composition) {
        fieldSection.addCode("@%s(", toTypeShortLiteral(annotation));
        boolean comma = false;
        if (mappedBy != null) {
            fieldSection.addCode("mappedBy = \"%s\"", mappedBy);
            comma = true;
        }
        fieldSection.addLine("%s)",  getCompositionStr(annotation, composition, comma));
        fieldSection.addLine("@%s", toTypeShortLiteral(Valid.class));
    }

    private String getCompositionStr(Class<? extends Annotation> annotation, boolean composition, boolean comma) {
        if (composition) {
            importClass(CascadeType.class);
            if (OneToOne.class.equals(annotation) || OneToMany.class.equals(annotation)) {
                return String.format("%scascade = %s.ALL, orphanRemoval = true", comma ? ", " : "", toTypeShortLiteral(CascadeType.class));
            }
            return String.format("%scascade = %s.ALL", comma ? ", " : "", toTypeShortLiteral(CascadeType.class));
        }

        return "";
    }

    private void generateId() {
        importClass(Id.class, Column.class, GeneratedValue.class, GenerationType.class, Version.class);
        fieldSection.addLine();
        fieldSection.addLine("@Id");
        fieldSection.addLine("@Column(nullable = false)");
        fieldSection.addLine("@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = \"SEQUENCE_GENERATOR\")");
        fieldSection.addLine("private Long id;");
        fieldSection.addLine();
        fieldSection.addLine("@Version");
        fieldSection.addLine("@Column(nullable = false)");
        fieldSection.addLine("private Long version;");
    }

    @Override
    protected boolean addGeneratedDynamicClass() {
        return false;
    }
}
