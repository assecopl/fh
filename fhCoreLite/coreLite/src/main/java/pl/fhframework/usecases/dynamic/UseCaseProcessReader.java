package pl.fhframework.usecases.dynamic;

import pl.fhframework.core.FhException;
import pl.fhframework.XmlAttributeReader;
import pl.fhframework.tools.loading.XMLReaderWorkContext;
import pl.fhframework.tools.loading.XMLReader;


import java.io.File;
import java.util.Stack;

public class UseCaseProcessReader extends XMLReader<IUseCaseProcessElement, UseCaseProcess> {
    public static final UseCaseProcessReader instance = new UseCaseProcessReader();

    @Override
    protected void createNewObject(XmlAttributeReader xmlAttributeReader, String tagName, String namespaceURI, Stack<IUseCaseProcessElement> readObjectsStack, XMLReaderWorkContext<UseCaseProcess> xmlContext) {
        tagName = tagName.toLowerCase();
        if (readObjectsStack.isEmpty()) {
            readObjectsStack.push(new UseCaseProcess(xmlAttributeReader));
        } else {
            IUseCaseProcessElement parent = readObjectsStack.peek();
            IUseCaseProcessElement createdElement;
            if (parent instanceof UseCaseProcess) {
                UseCaseProcess useCaseProcess = (UseCaseProcess) parent;
                createdElement = createNewUseCaseProcessObject(xmlAttributeReader, tagName, useCaseProcess, readObjectsStack, xmlContext);
                if (createdElement instanceof DynamicActionHandler.Start){
                    useCaseProcess.setStart(((DynamicActionHandler.Start) createdElement));
                } else if (createdElement instanceof DynamicActionHandler.Action){
                    useCaseProcess.addAction((DynamicActionHandler.Action) createdElement);
                } else if (createdElement instanceof DynamicDeclarationHandler.DeclareModel){
                    useCaseProcess.addModelDeclaration((DynamicDeclarationHandler.DeclareModel) createdElement);
                }
            } else if (parent instanceof DynamicActionHandler.Start) {
                createdElement = createNewActionObject(xmlAttributeReader, tagName, (DynamicActionHandler.Start) parent, readObjectsStack, xmlContext);
            } else if (parent instanceof DynamicActionHandler.Action) {
                createdElement = createNewActionObject(xmlAttributeReader, tagName, (DynamicActionHandler.Action) parent, readObjectsStack, xmlContext);
            } else if (parent instanceof DynamicDeclarationHandler.DeclareModel){
                createdElement = createNewModelDeclarationObject(xmlAttributeReader, tagName, (DynamicDeclarationHandler.DeclareModel) parent, readObjectsStack, xmlContext);
            } else if (parent instanceof DynamicActivityHandler.InitializeModel){
                createdElement = createNTimesNewTransitionToSubUseCaseObject(xmlAttributeReader, tagName, (DynamicActivityHandler.InitializeModel) parent, readObjectsStack, xmlContext);
            } else if (parent instanceof DynamicActivityHandler.VisitSubUseCase){
                createdElement = createNewTransitionToSubUseCaseObject(xmlAttributeReader, tagName, (DynamicActivityHandler.VisitSubUseCase) parent, readObjectsStack, xmlContext);
            }
            else {
                throw new FhException("No support for reading objects for context '" + parent.getClass().getName() + "' !");
            }
            if (createdElement == null){
                throw new FhException("Element was not created for '"+ tagName +" '"+ parent.getClass().getName()+"' !");
            }
            if (parent instanceof DynamicActionHandler.Action){
                DynamicActionHandler.Action action = (DynamicActionHandler.Action) parent;
                if (createdElement instanceof DynamicActivityHandler.Activity){
                    action.addActivity((DynamicActivityHandler.Activity) createdElement);
                }else if (createdElement instanceof DynamicDeclarationHandler.RequiredAttribute){
                    action.addRequiredAttribute((DynamicDeclarationHandler.RequiredAttribute) createdElement);
                }
            }
            readObjectsStack.push(createdElement);
        }
    }

    private IUseCaseProcessElement createNewUseCaseProcessObject(XmlAttributeReader xmlAttributeReader, String tagName, UseCaseProcess parent, Stack<IUseCaseProcessElement> readObjectsStack, XMLReaderWorkContext xmlContext) {
        switch (tagName) {
            case "declareModel":
                return new DynamicDeclarationHandler.DeclareModel(xmlAttributeReader);
            case "start":
                return new DynamicActionHandler.Start(xmlAttributeReader, parent);
            case "action":
                return new DynamicActionHandler.Action(xmlAttributeReader, parent);
            case "output":
                return new DynamicActionHandler.Output(xmlAttributeReader, parent);
            case "end":
                return new DynamicActionHandler.End(xmlAttributeReader, parent);
            default:
                throw new FhException("No support for tag '" + tagName + "' in context '" + parent.getClass().getName() + "'!");
        }
    }

    private IUseCaseProcessElement createNewModelDeclarationObject(XmlAttributeReader xmlAttributeReader, String tagName, DynamicDeclarationHandler.DeclareModel parent, Stack<IUseCaseProcessElement> readObjectsStack, XMLReaderWorkContext xmlContext) {
        switch (tagName) {
            //TODO: KKO where is this used?
            case "attribute":
                return new DynamicDeclarationHandler.DeclareAttribute(xmlAttributeReader);
            default:
                throw new FhException("No support for tag '" + tagName + "' in context '" + parent.getClass().getName() + "'!");
        }
    }

    private IUseCaseProcessElement createNewActionObject(XmlAttributeReader xmlAttributeReader, String tagName, DynamicActionHandler.Action parent, Stack<IUseCaseProcessElement> readObjectsStack, XMLReaderWorkContext xmlContext) {
        switch (tagName) {
            case "initializemodel":
                return new DynamicActivityHandler.InitializeModel(xmlAttributeReader, parent);
            case "readmodel":
                return new DynamicActivityHandler.ReadModel(xmlAttributeReader, parent);
            case "getmodel":
                return new DynamicActivityHandler.GetModel(xmlAttributeReader, parent);
            case "displayformcomponent":
                return new DynamicActivityHandler.DisplayFormComponent(xmlAttributeReader, parent);
            case "requiredattribute":
                return new DynamicDeclarationHandler.RequiredAttribute(xmlAttributeReader);
            case "visitsubusecase":
                return new DynamicActivityHandler.VisitSubUseCase(xmlAttributeReader, parent);
            case "goto":
                return new DynamicActivityHandler.GoTo(xmlAttributeReader, parent);
            case "code":
                return new DynamicActivityHandler.Code(xmlAttributeReader, parent);
            default:
                throw new FhException("No support for tag '" + tagName + "' in context '" + parent.getClass().getName() + "'!");
        }
    }

    private IUseCaseProcessElement createNewTransitionToSubUseCaseObject(XmlAttributeReader xmlAttributeReader, String tagName, DynamicActivityHandler.VisitSubUseCase parent, Stack<IUseCaseProcessElement> readObjectsStack, XMLReaderWorkContext xmlContext) {
        switch (tagName) {
            case "output":
                return new DynamicActionHandler.SubUseCaseOutput(xmlAttributeReader, parent);
            default:
                throw new FhException("No support for tag '" + tagName + "' in context '" + parent.getClass().getName() + "'!");
        }
    }

    private IUseCaseProcessElement createNTimesNewTransitionToSubUseCaseObject(XmlAttributeReader xmlAttributeReader, String tagName, DynamicActivityHandler.InitializeModel parent, Stack<IUseCaseProcessElement> readObjectsStack, XMLReaderWorkContext xmlContext) {
        switch (tagName) {
            case "adjustattribute":
                return new DynamicActivityHandler.AdjustAttribute(xmlAttributeReader, parent);
            default:
                throw new FhException("No support for tag '" + tagName + "' in context '" + parent.getClass().getName() + "'!");
        }
    }


    @Override
    protected void finalizeNewObjectSetup(String tag, String text, Stack<IUseCaseProcessElement> readObjectsStack, XMLReaderWorkContext xmlContext) {
        IUseCaseProcessElement container = readObjectsStack.pop();
        if (container instanceof DynamicActivityHandler.Code){
            ((DynamicActivityHandler.Code) container).setCode(text);
        }
    }

    public UseCaseProcess read(File dpuFilePath) {
        UseCaseProcess useCaseProcess = super.readObject(dpuFilePath.getAbsolutePath(), null);
        useCaseProcess.setDpuFilePath(dpuFilePath.getAbsolutePath());
        return useCaseProcess;
    }
}
