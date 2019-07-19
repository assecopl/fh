package pl.fhframework.subsystems;

import pl.fhframework.core.FhException;
import pl.fhframework.core.FhSubsystemException;
import pl.fhframework.core.events.ISubsystemLifecycleListener;
import pl.fhframework.core.io.FhResource;
import pl.fhframework.core.security.ISystemFunctionsMapper;
import pl.fhframework.XmlAttributeReader;
import pl.fhframework.tools.loading.XMLReader;
import pl.fhframework.tools.loading.XMLReaderWorkContext;

import java.util.Stack;

class DynamicSubsystemReader extends XMLReader<Subsystem, Subsystem> {
    static final DynamicSubsystemReader instance = new DynamicSubsystemReader();

    Subsystem readSubsystemConfiguration(String sysFileName) {
        return readSubsystemConfiguration(sysFileName);
    }

    Subsystem readSubsystemConfiguration(FhResource module) {
        Subsystem subsystem = super.readObject(module, null);
        subsystem.setConfigUrl(module);
        return subsystem;
    }

    @Override
    protected void createNewObject(XmlAttributeReader xmlAttributeReader, String tagName, String namespaceURI, Stack<Subsystem> readObjectsStack, XMLReaderWorkContext<Subsystem> XMLReaderWorkContext) {
        tagName = tagName.toLowerCase();
        switch (tagName) {
            case "subsystem":
                if (!readObjectsStack.isEmpty()) {
                    throw new FhSubsystemException("Error in XML file - 'subsystem' have to be root element!");
                }
                DynamicSubsystem dynamicSubsystem = new DynamicSubsystem(xmlAttributeReader, XMLReaderWorkContext.getFileResource());
                if (dynamicSubsystem.getLabel() == null) {
                    throw new FhException("Subsystem label must be defined in xml");
                }
                if (dynamicSubsystem.getProductUUID() == null) {
                    throw new FhException("Subsystem product UUID must be defined in xml");
                }
                readObjectsStack.push(dynamicSubsystem);
                break;
            case "lifecyclelistener":
                if (readObjectsStack.isEmpty()) {
                    throw new FhSubsystemException("Error in xml - 'lifecycleListener' has to be defined only under subsystem!");
                }
                DynamicSubsystem listenerSubsystem = (DynamicSubsystem) readObjectsStack.peek();
                String classAttr = xmlAttributeReader.getAttributeValue("class");
                if (classAttr != null) {
                    try {
                        listenerSubsystem.addlifecycleListener((Class<? extends ISubsystemLifecycleListener>) Class.forName(xmlAttributeReader.getAttributeValue("class")));
                    } catch (ClassNotFoundException e) {
                        throw new FhException("Lifecycle listener class not found: " + classAttr);
                    }
                } else {
                    throw new FhException("Must specify class attribute of lifecycleListener element in " + listenerSubsystem.getName() + " subsystem!");
                }
                break;
            case "group":
                //Do not process groups
                break;
            case "systemfunctionsmapper":
                if (readObjectsStack.isEmpty()) {
                    throw new FhSubsystemException("Error in xml - 'systemfunctionsmapper' has to be defined only under subsystem!");
                }
                DynamicSubsystem mapperSubsystem = (DynamicSubsystem) readObjectsStack.peek();
                String systemfunctions = xmlAttributeReader.getAttributeValue("ref");
                if (systemfunctions != null && !systemfunctions.isEmpty()) {
                    try {
                        Class<ISystemFunctionsMapper> systemFunctionsClass = (Class<ISystemFunctionsMapper>) Class.forName(systemfunctions);
                        mapperSubsystem.setSystemFunctionsMapper(systemFunctionsClass.newInstance());
                    } catch (Exception e) {
                        throw new FhSubsystemException("Mapper init failed for:" + systemfunctions, e);
                    }
                }
                break;
            case "usecase":
//                if (readObjectsStack.isEmpty()) {
//                    throw new FhSubsystemException("Error in xml - 'UseCase' has to be defined only under subsystem or his group!");
//                }
//                DynamicSubsystem subsystem = (DynamicSubsystem) readObjectsStack.peek();
//                subsystem.addUseCaseReference(xmlAttributeReader.getAttributeValue("ref"));
                break;
            default:
                throw new FhException("No support for tag '" + tagName + "'!");
        }
    }

    @Override
    protected void finalizeNewObjectSetup(String tag, String text, Stack<Subsystem> readObjectsStack, XMLReaderWorkContext XMLReaderWorkContext) {
        if (tag.equalsIgnoreCase("Subsystem")) {
            readObjectsStack.pop();
        }
    }
}
