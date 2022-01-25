package pl.fhframework.compiler.core.rules.meta;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.compiler.core.generator.MethodDescriptor;
import pl.fhframework.compiler.core.rules.dynamic.model.Rule;
import pl.fhframework.compiler.core.rules.dynamic.model.RuleType;
import pl.fhframework.core.FhException;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.compiler.core.dynamic.IClassInfo;
import pl.fhframework.compiler.core.dynamic.RuntimeErrorDescription;
import pl.fhframework.subsystems.Subsystem;

import java.net.URL;
import java.nio.file.Path;
import java.util.List;

public class RuleInfo {

    @Getter
    protected IClassInfo classInfo;

    @Getter
    protected Rule rule;

    @Getter
    protected String name;

    @Getter
    protected RuleType ruleType;

    @Getter
    @Setter
    protected List<RuntimeErrorDescription> errorDescriptions;

    @Getter
    protected String trimLevel;

    @Getter
    private MethodDescriptor descriptor;

    public static RuleInfo of(IClassInfo classInfo, String name, RuleType ruleType) {
        RuleInfo ruleInfo = new RuleInfo();
        ruleInfo.classInfo = classInfo;
        ruleInfo.name = name;
        ruleInfo.ruleType = ruleType;
        return ruleInfo;
    }

    public static RuleInfo of(IClassInfo classInfo, String name, RuleType ruleType, MethodDescriptor descriptor) {
        RuleInfo ruleInfo = of(classInfo, name, ruleType);
        ruleInfo.descriptor = descriptor;
        return ruleInfo;
    }

    public DynamicClassName getClassName() {
        return classInfo.getClassName();
    }

    public String getFullClassName() {
        return classInfo.getClassName().toFullClassName();
    }

    public String getBaseClassName() {
        return classInfo.getClassName().getBaseClassName();
    }

    public String getPackageName() {
        if (classInfo != null) {
            return classInfo.getClassName().getPackageName();
        }
        return null;
    }

    public Subsystem getSubsystem() {
        if (classInfo != null) {
            return classInfo.getSubsystem();
        }
        return null;
    }

    public URL getUrl() {
        return classInfo.getXmlFile().getResource().getURL();
    }

    public Path getWritablePath() {
        Path externalPath = classInfo.getXmlFile().getResource().getExternalPath();

        if(externalPath != null) {
          return externalPath;
        } else {
           throw new FhException("Rule " + getBaseClassName() + " is located on read-only URL");
        }
    }

    public RuleInfo() {
    }

    public static RuleInfo of(Rule rule, List<RuntimeErrorDescription> errorDescriptions, String trimLevel) {
        RuleInfo ruleInfo = new RuleInfo();
        ruleInfo.rule = rule;
        ruleInfo.errorDescriptions = errorDescriptions;
        ruleInfo.trimLevel = trimLevel;

        return ruleInfo;
    }
}
