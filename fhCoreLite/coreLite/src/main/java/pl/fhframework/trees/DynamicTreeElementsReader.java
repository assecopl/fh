package pl.fhframework.trees;

import pl.fhframework.core.FhException;
import pl.fhframework.XmlAttributeReader;
import pl.fhframework.tools.loading.XMLReader;
import pl.fhframework.tools.loading.XMLReaderWorkContext;

import java.util.Stack;


public class DynamicTreeElementsReader extends XMLReader<ITreeElement, TreeRoot> {

    public static final DynamicTreeElementsReader instance = new DynamicTreeElementsReader();

    @Override
    protected void finalizeNewObjectSetup(String tag, String text, Stack<ITreeElement> readObjectsStack, XMLReaderWorkContext xmlReadersWorkContext) {
        if (tag.equalsIgnoreCase("Group") || tag.equalsIgnoreCase("Subsystem")) {
            readObjectsStack.pop();
        }
    }

    @Override
    protected void createNewObject(XmlAttributeReader xmlAttributeReader, String tagName, String namespaceURI, Stack<ITreeElement> readObjectsStack, XMLReaderWorkContext<TreeRoot> xmlReadersWorkContext) {
        tagName = tagName.toLowerCase();
        switch (tagName) {
            case "menu":
                if (!(readObjectsStack.peek() instanceof TreeRoot)) {
                    throw new FhException("Error in XML - 'menu' cannot be defined in scope of other element!");
                }
                break;
            case "group":
                if (!readObjectsStack.isEmpty() && readObjectsStack.peek() instanceof UseCasesGroup) {
                    UseCasesGroup aboveGroup = (UseCasesGroup) readObjectsStack.peek();
                    UseCasesGroup newGroup = new DynamicUseCasesGroup(xmlAttributeReader);
                    aboveGroup.addSubelement(newGroup);
                    readObjectsStack.push(newGroup);
                } else {
                    throw new FhException("Error in XML - 'Group' can be defined only in scope of Subsystem or other Group!");
                }
                break;
            case "usecase":
                if (readObjectsStack.isEmpty() || !(readObjectsStack.peek() instanceof IGroupingTreeElement)) {
                    throw new FhException("Error in XML - 'UseCase' can be defined only in scope of Subsystem or Group!");
                }
                ITreeElement newElement = new UseCaseInformation(xmlAttributeReader);
                IGroupingTreeElement aboveElement = (IGroupingTreeElement) readObjectsStack.peek();
                aboveElement.addSubelement(newElement);
                break;
            default:
                throw new FhException("No support for tag '" + tagName + "'!");
        }
    }


}
