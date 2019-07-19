package pl.fhframework.usecases.dynamic;

import pl.fhframework.core.FhUseCaseException;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.InicjujacyPrzypadekUzycia;
import pl.fhframework.annotations.Action;
import pl.fhframework.subsystems.Subsystem;
import pl.fhframework.usecases.DynamicSubUseCase;
import pl.fhframework.usecases.DynamicSubUseCaseWithTwoOutputs;
import pl.fhframework.usecases.DynamicSubUseCaseWithOneOutput;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

class JavaCodeGeneratorForDynamicUseCase {
    private UseCaseProcess useCaseProcess;
    private String _package;
    private String useCaseClassName;
    private String basicUseCaseClassIdentifier;
    private Path rootPath;
    private Subsystem subSystem;

    JavaCodeGeneratorForDynamicUseCase(UseCaseProcess useCaseProcess, Subsystem subSystem) {
        this.subSystem = subSystem;
        this.useCaseProcess = useCaseProcess;
        this._package = useCaseProcess.getPackage();
        this.useCaseClassName = useCaseProcess.getName();
        this.basicUseCaseClassIdentifier = useCaseProcess.getBasicUseCaseId();
        this.rootPath = Paths.get(useCaseProcess.getDpuFilePath()).getParent();
        String rootPathText = (new File(rootPath.toUri())).toString();
        rootPathText = rootPathText.substring(0, rootPathText.lastIndexOf(_package.replace('.', File.separatorChar)));
        this.rootPath = Paths.get(rootPathText);

    }

    String getJavaCode() {

        String javaCode = "";
        if (!_package.isEmpty()) {
            javaCode += "package " + _package + ";" + "\n";
        }
        javaCode += "import " + Action.class.getName() + ";\n\n";
        //TODO:Add imports
        javaCode += "public class " + useCaseClassName;
        if (this.useCaseProcess.isInitial()) {
            if (basicUseCaseClassIdentifier != null && !basicUseCaseClassIdentifier.isEmpty()) {
                //TODO: Add here something - because if basic use case is not static it will not compile
                javaCode += " extends " + basicUseCaseClassIdentifier;
            } else {
                javaCode += " extends " + InicjujacyPrzypadekUzycia.class.getName();
            }
        } else {
            DynamicActionHandler.Start start = this.useCaseProcess.getStart();
            if (start == null) {
                if (basicUseCaseClassIdentifier != null && !basicUseCaseClassIdentifier.isEmpty()) {
                    javaCode += " extends " + basicUseCaseClassIdentifier;
                } else {
                    throw new FhUseCaseException("No support for start action for sub use case process '" + useCaseProcess.getId() + "'");
                }
            } else {
                if (start.getRequiredParameters().size() > 1) {
                    throw new FhUseCaseException("No support for sub-use cases, which needs more than one parameter on start!!!");
                }
                if (start.getRequiredParameters().size() == 0) {
                    throw new FhUseCaseException("No support for sub-use cases, which does not have input parameter!!!");
                }
                javaCode += " extends ";
                int outputCount = 0;
                for (DynamicActionHandler.Action action : this.useCaseProcess.getActions()) {
                    if (action instanceof DynamicActionHandler.Output) {
                        outputCount++;
                    }
                }
                switch (outputCount) {
                    case 0:
                        javaCode += DynamicSubUseCase.class.getName();
                        break;
                    case 1:
                        javaCode += DynamicSubUseCaseWithOneOutput.class.getName();
                        break;
                    case 2:
                        javaCode += DynamicSubUseCaseWithTwoOutputs.class.getName();
                        break;
                    default:
                        throw new FhUseCaseException("No support for sub-use cases, which have more than 2 outputs(" + outputCount + ")!");
                }
                javaCode += "<" + start.getRequiredParameters().get(0).getType() + ">";
                //javaCode += "<Object>";//TODO:We could get it from input type parameters
            }
        }
        javaCode += "{\n";

        for (DynamicDeclarationHandler.DeclareModel declareModel : useCaseProcess.getDeclarations()) {
            javaCode += "\n" + getDeclarationJavaCode(declareModel);
        }

        //if (useCaseProcess.isInitial()) {
        if (useCaseProcess.getStart() != null) {
            javaCode += "\n" + getActionJavaCode(useCaseProcess.getStart());
        } else {
            if (basicUseCaseClassIdentifier == null || basicUseCaseClassIdentifier.isEmpty()) {
                throw new FhUseCaseException("No support for start action for process '" + useCaseProcess.getId() + "'");
            }
        }
        //}
        int outputNumber = 1;
        for (DynamicActionHandler.Action action : useCaseProcess.getActions()) {
            javaCode += "\n";
            if (action instanceof DynamicActionHandler.End) {

            } else if (action instanceof DynamicActionHandler.Output) {
                javaCode += getOutputJavaCode((DynamicActionHandler.Output) action, outputNumber++);
            } else {
                javaCode += getActionJavaCode(action);
            }
        }

        javaCode += "\n}";

        return javaCode;
    }

    private String getDeclarationJavaCode(DynamicDeclarationHandler.DeclareModel declaration) {
        return "private " + declaration.getType() + " " + declaration.getId() + ";";
    }

    private String getOutputJavaCode(DynamicActionHandler.Output output, int outputNumber) {
        String javaCode = getActionHeaderJavaCode(output);
        javaCode += "{\n";
        String result = output.result;
        if ("void".equals(result)) {
            result = "null";
        }
        javaCode += "useCaseReturn" + outputNumber + "(" + result + ");";
        javaCode += "\n}";
        return javaCode;
    }

    private String getActionHeaderJavaCode(DynamicActionHandler.Action action) {
        String javaCode = "void " + action.getId();
        javaCode += "(" + getActionParametersJavaCode(action) + ")";
        if (action instanceof DynamicActionHandler.Start) {
            javaCode = "@Override\npublic " + javaCode;
        } else {
            javaCode = "@" + Action.class.getName() + "\npublic " + javaCode;
        }
        return javaCode;
    }

    private String getActionJavaCode(DynamicActionHandler.Action action) {
        String javaCode = getActionHeaderJavaCode(action);
        javaCode += "{";
        String actionBody = "\n" + getActionBodyJavaCode(action);
        javaCode += actionBody.replace("\n", "\n    ");
        javaCode += "\n}\n\n";
        return javaCode;
    }

    private String getActionParametersJavaCode(DynamicActionHandler.Action action) {
        String javaCode = "";
        for (DynamicDeclarationHandler.RequiredAttribute requiredAttribute : action.getRequiredParameters()) {
            if (!javaCode.isEmpty()) javaCode += ", ";
            javaCode += requiredAttribute.getType() + " " + requiredAttribute.getId();
        }
        return javaCode;
    }

    private String getActionBodyJavaCode(DynamicActionHandler.Action action) {
        String javaCode = "";
        for (DynamicActivityHandler.Activity activity : action.getActivities()) {
            javaCode += "\n" + getJavaCode(activity);
        }
        if (!javaCode.isEmpty()) {
            javaCode = javaCode.substring(1);
        }
        return javaCode;
    }

    private String getJavaCode(DynamicActivityHandler.Activity activity) {
        if (activity instanceof DynamicActivityHandler.DisplayFormComponent) {
            return getFormComponentDisplayJavaCode((DynamicActivityHandler.DisplayFormComponent) activity);
        } else if (activity instanceof DynamicActivityHandler.VisitSubUseCase) {
            return getTransitionToSubUseCaseJavaCode((DynamicActivityHandler.VisitSubUseCase) activity);
        } else if (activity instanceof DynamicActivityHandler.GoTo) {
            return getAnotherActionJavaCode((DynamicActivityHandler.GoTo) activity);
        } else if (activity instanceof DynamicActivityHandler.Code){
            return getJavaCodeForActivityCode(((DynamicActivityHandler.Code) activity));
        }
        return "";
    }

    private String getAnotherActionJavaCode(DynamicActivityHandler.GoTo activity) {
        return activity.getRef().substring(activity.getRef().lastIndexOf('.') + 1) + "();";
    }

    private String getJavaCodeForActivityCode(DynamicActivityHandler.Code code){
        return code.getCode();
    }

    private String getTransitionToSubUseCaseJavaCode(DynamicActivityHandler.VisitSubUseCase activity) {

        if (activity.getOutputs().size() == 0) {
            return "runUseCase(\"" + activity.getRef() + "\", null, null);";
        } else if (activity.getOutputs().size() > 2) {
            return FhLogger.class.getName() + ".error(\"GENERATION PROBLEM: No support for " + activity.getOutputs().size() + " outputs!\")";
        } else {
            String outputsJavaCode = "";
            for (DynamicActionHandler.SubUseCaseOutput output : activity.getOutputs()) {
                DynamicActionHandler.Action targetAction = activity.getUseCaseProcess().getAction(output.id);
                if (targetAction == null) {
                    outputsJavaCode += ", atr -> {" + FhLogger.class.getName() + ".error(\"GENERATION PROBLEM: No action '" + output.id + "'\");}";
                } else {
                    if (targetAction.getRequiredParameters().size() > 1) {
                        outputsJavaCode += ", atr -> {" + FhLogger.class.getName() + ".error(\"GENERATION PROBLEM: Output action '" + output.id + "' needs more than one parameter(" + targetAction.getRequiredParameters().size() + ")!\");}";
                    } else if (targetAction.getRequiredParameters().size() == 0) {
                        outputsJavaCode += ", atr -> this." + output.id + "()";
                    } else if (targetAction.getRequiredParameters().size() == 1) {
                        outputsJavaCode += ", atr -> this." + output.id + "(atr)";
                    }
                }

            }
            return "runUseCase(\"" + activity.getRef() + "\", null" + outputsJavaCode + ");";
        }

    }

    private String getFormComponentDisplayJavaCode(DynamicActivityHandler.DisplayFormComponent activity) {
        Class<?> formComponentJavaClass;
        try {
            formComponentJavaClass = Class.forName(activity.getRef());
            if (formComponentJavaClass != null) {
                return "showForm(" + activity.getRef() + ".class, null);";
            }
        } catch (ClassNotFoundException ignored) {
        }

        return "showForm(\"" + activity.getRef() + "\", null);";
        //TODO: Model binding

    }

    private String getStartActionJavaCode(DynamicActionHandler.Start action) {
        String javaCode = "protected void start () {\n";
        javaCode += getActionBodyJavaCode(action);
        javaCode += "\n}";
        return javaCode;
    }


}
