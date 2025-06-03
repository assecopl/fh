package pl.fhframework.compiler.core.model.ts.generator;

import pl.fhframework.compiler.core.generator.AbstractNgClassCodeGenerator;
import pl.fhframework.compiler.core.generator.ModuleMetaModel;
import pl.fhframework.compiler.core.generator.MetaModelService;
import pl.fhframework.compiler.core.generator.model.data.AttributeMm;
import pl.fhframework.compiler.core.generator.model.data.ClassMm;
import pl.fhframework.compiler.core.generator.model.data.RelationMm;
import pl.fhframework.compiler.core.uc.dynamic.model.element.attribute.ParameterDefinition;
import pl.fhframework.compiler.core.uc.ts.generator.FhNgCore;
import pl.fhframework.core.FhException;
import pl.fhframework.core.generator.GenerationContext;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.util.StringUtils;

import java.util.List;

public class ClassNgCodeGenerator extends AbstractNgClassCodeGenerator {
    private final ClassMm classMm;

    private String classBaseName;

    public ClassNgCodeGenerator(ClassMm classMm, ModuleMetaModel moduleMetaModel, MetaModelService metaModelService) {
        super(moduleMetaModel, moduleMetaModel.getDependency(classMm.getId()), classMm, metaModelService);
        this.classMm = classMm;
        classBaseName = getBaseName(this.classMm.getId());
    }

    @Override
    protected void generateClassBody() {
        generateClassSignature(classMm);

        generateConstructor(classMm);

        if (classMm.isEnumeration()) {
            processConstants(classMm);
        }
        else {
            processAttributes(fieldSection, classMm.getAttributes());
            processRelations(fieldSection, classMm);
        }

        // todo:
        /*if (ClassMm.isGeometryType(GeometryType.FeatureCollection)) {
            processFeaturesClass(methodSection, ClassMm.getFeaturesClass(), ClassMm.getRelationTags());
        }*/
    }

    private void generateClassSignature(ClassMm classMm) {
        if (!classMm.isEnumeration()) {
            addImport(FhNgCore.SnapshotEnabled);
            classSignatureSection.addLine("@%s", FhNgCore.SnapshotEnabled.getName());
        }

        if (classMm.isEnumeration()) {
            classSignatureSection.addLine("export enum %s", classBaseName);
        }
        else if (classMm.isServerClassExtension()) { // extends static class
            throw new FhException("Extension of Java class in Typescript is unsuporrted");
        } else if (!StringUtils.isNullOrEmpty(classMm.getParent())) { // extends parent class (does not matter if it's persistable or not
            addImport(classMm.getParent());
            classSignatureSection.addLine("export class %s extends %s", classBaseName, getType(classMm.getParent()));
        } else if (classMm.isSpatial()) { // extends base entity class for persistable dynamic class
            FhLogger.warn(String.format("Spatial type is unsuporrted in Typescript %s", classBaseName));
            // todo: uncomment
            //throw new FhException("Spatial type is unsuporrted in Typescript ");
            classSignatureSection.addLine("export class %s", classBaseName);
        } else if (classMm.isPersistable()) {
            // todo:
            classSignatureSection.addLine("export class %s", classBaseName);
        } else {
            classSignatureSection.addLine("export class %s", classBaseName);
        }

    }

    private void generateConstructor(ClassMm classMm) {
        if (!classMm.isEnumeration()) {
            constructorSignatureSection.addLine("constructor(init?: Partial<%s>)", classBaseName);
            if (!StringUtils.isNullOrEmpty(classMm.getParent())) {
                constructorSection.addLine("super(init);");
            }
            constructorSection.addLine("Object.assign(this, init);");
        }
    }


    private void processAttributes(GenerationContext fieldSection, List<AttributeMm> attributes) {
        for (AttributeMm attribute : attributes) {
            ParameterDefinition type = new ParameterDefinition(attribute.getType(), getFieldName(attribute.getName()), attribute.getMultiplicity());
            addImport(type);
            fieldSection.addLine("%s: %s = undefined;", attribute.getName(), getType(type));
        }
    }

    private void processRelations(GenerationContext fieldSection, ClassMm classMm) {
        for (RelationMm relation : classMm.getRelations()) {
            ParameterDefinition type = new ParameterDefinition(relation.getType(), getFieldName(relation.getName()),
                    relation.getMultiplicity());
            addImport(type);
            fieldSection.addLine("%s: %s = undefined;", type.getName(), getType(type));
        }
    }

    private void processConstants(ClassMm classMm) {
        for (String constant : classMm.getConstants()) {
            fieldSection.addLineWithIndent(1, "%s,", constant);
        }
    }
}
