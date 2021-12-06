package pl.fhframework.compiler.core.integration.xsd;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.sun.codemodel.*;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.model.Model;
import com.sun.tools.xjc.outline.Outline;
import com.sun.xml.xsom.impl.util.SchemaWriter;
import org.xml.sax.ErrorHandler;
import pl.fhframework.aspects.snapshots.model.ISnapshotEnabled;
import pl.fhframework.compiler.core.generator.JavaCodeFromXsdService;
import pl.fhframework.compiler.core.tools.JavaNamesUtils;
import pl.fhframework.core.FhException;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.core.generator.GeneratedDynamicClass;
import pl.fhframework.core.services.FhService;
import pl.fhframework.fhPersistence.core.BasePersistentObject;

import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.io.StringWriter;
import java.net.URL;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;

public class FhXjcPlugin extends Plugin {
    public static final String PLUGIN_OPTION_NAME = "XFhExtension";

    private Mode mode;

    private JavaCodeFromXsdService.Context context;

    public FhXjcPlugin(Mode mode, JavaCodeFromXsdService.Context context) {
        this.mode = mode;
        this.context = context;
    }

    @Override
    public String getOptionName() {
        return PLUGIN_OPTION_NAME;
    }

    @Override
    public String getUsage() {
        return "  -" + PLUGIN_OPTION_NAME + "    :  xjc plugin";
    }

    @Override
    public boolean run(Outline outline, Options opt, ErrorHandler errorHandler) {
        outline.getClasses().stream().forEach(classOutline -> {
            DynamicClassName dcn = DynamicClassName.forClassName(classOutline.implClass.getPackage().name(), classOutline.implClass.name());
            JavaCodeFromXsdService.ClassDescriptor classDescriptor = context.getClassesMap().computeIfAbsent(dcn, JavaCodeFromXsdService.ClassDescriptor::new);
            classDescriptor.setXsdModel(classOutline.implClass);
            StringWriter out = new StringWriter();
            SchemaWriter sw = new SchemaWriter(out);
            classOutline.getTarget().getSchemaComponent().visit(sw);
            classDescriptor.setXsdDefinition(out.toString());

            try {
                classDescriptor.setXdsFile(context.getXsdTempDir().relativize(
                        Paths.get(new URL(classOutline.getTarget().getLocator().getSystemId()).toURI())));
            } catch (Exception e) {
                throw new FhException(e);
            }

            if (mode == Mode.SnapshotEnabled) {
                classOutline.implClass._implements(ISnapshotEnabled.class);
            }
            else if (mode == Mode.DynamicEntity || mode == Mode.Entity) {
                JClass superClass = classOutline.implClass._extends();
                if (Objects.equals(superClass._package().name(), "java.lang") && Objects.equals(superClass.name(), "Object")) {
                    classOutline.implClass._extends(outline.getCodeModel().directClass(BasePersistentObject.class.getName()));
                }
            }
            if (mode == Mode.DynamicEntity || mode == Mode.SnapshotEnabledDynamic) {
                JAnnotationUse annotationUse = classOutline.implClass.annotate(GeneratedDynamicClass.class);
                annotationUse.param("value", DynamicClassName.forClassName(classOutline.implClass.getPackage().name(), classOutline.implClass.name()).toFullClassName());
            }
            if (mode != Mode.None) {
                JAnnotationUse annotationUse = classOutline.implClass.annotate(JsonIdentityInfo.class);
                annotationUse.param("generator", ObjectIdGenerators.IntSequenceGenerator.class);

                // todo:
                classOutline.implClass.fields().values().stream().forEach(jFieldVar -> {
                    if (jFieldVar.type() instanceof JClass) {
                        JClass type = (JClass) jFieldVar.type();
                        if (type.erasure() == type && !type._package().name().startsWith("java.")) {
                            if (!(type instanceof JDefinedClass) || ((JDefinedClass) type).getClassType() != ClassType.ENUM) {
                                jFieldVar.annotate(OneToOne.class);
                            }
                        }
                        else if ((type.erasure()).getBaseClass(Collection.class) != null) {
                            jFieldVar.annotate(OneToMany.class);
                        }
                    }
                });
            }
        });

        outline.getEnums().stream().forEach(enumOutline -> {
            DynamicClassName dcn = DynamicClassName.forClassName(enumOutline.clazz.getPackage().name(), enumOutline.clazz.name());
            JavaCodeFromXsdService.ClassDescriptor classDescriptor = context.getClassesMap().computeIfAbsent(dcn, JavaCodeFromXsdService.ClassDescriptor::new);
            classDescriptor.setXsdEnumModel(enumOutline.target);
            classDescriptor.setXsdModel(enumOutline.clazz);

            StringWriter out = new StringWriter();
            SchemaWriter sw = new SchemaWriter(out);
            enumOutline.getTarget().getSchemaComponent().visit(sw);
            classDescriptor.setXsdDefinition(out.toString());

            try {
                classDescriptor.setXdsFile(context.getXsdTempDir().relativize(
                        Paths.get(new URL(enumOutline.getTarget().getLocator().getSystemId()).toURI())));
            } catch (Exception e) {
                throw new FhException(e);
            }

            if (mode == Mode.DynamicEntity || mode == Mode.SnapshotEnabledDynamic) {
                JAnnotationUse annotationUse = enumOutline.clazz.annotate(GeneratedDynamicClass.class);
                annotationUse.param("value", DynamicClassName.forClassName(enumOutline.clazz.getPackage().name(), enumOutline.clazz.name()).toFullClassName());
            }

        });

        // ObjectFactory
        String factoryName = outline.getClasses().iterator().next().implClass.getPackage().name() + ".ObjectFactory";
        JDefinedClass objFactory = outline.getCodeModel()._getClass(factoryName);
        JAnnotationUse annotationUse = objFactory.annotate(FhService.class);
        annotationUse.param("value", "ObjectFactory" + JavaNamesUtils.camelName(objFactory.getPackage().name()));

        return true;
    }

    void addXmlTimeStampMethod(JDefinedClass implClass, JCodeModel codeModel, DynamicClassName dcn, Instant timestamp) {
        JClass map = codeModel.ref(HashMap.class).narrow(Arrays.asList(
                codeModel.ref(DynamicClassName.class),
                codeModel.ref(Instant.class)));
        JMethod method = implClass.method(JMod.PUBLIC | JMod.STATIC,
                map, "getXmlTimestamps");
        method.body().decl(map, "timestamps", JExpr._new(map));
        method.body().invoke(JExpr.ref("timestamps"), "put").
                arg(new JBlock().staticInvoke(codeModel.ref(DynamicClassName.class), "forClassName").arg(JExpr.lit(dcn.toFullClassName()))).
                arg(new JBlock().staticInvoke(codeModel.ref(Instant.class), "ofEpochMilli").arg(JExpr.lit(timestamp.toEpochMilli())));
        method.body()._return(JExpr.ref("timestamps"));
    }

    @Override
    public void postProcessModel(Model model, ErrorHandler errorHandler) {
        super.postProcessModel(model, errorHandler);
    }

    enum Mode {
        SnapshotEnabledDynamic,
        SnapshotEnabled,
        DynamicEntity,
        Entity,
        None
    }
}
