package pl.fhframework.tools.loading;

import pl.fhframework.core.FhException;
import pl.fhframework.core.io.FhResource;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.XmlAttributeReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Stack;

/**
 * Created by Gabriel.Kurzac on 2016-06-29.
 */
public abstract class XMLReader<READ_OBJECT_TYPE, ROOT_TYPE extends READ_OBJECT_TYPE> {

    //protected abstract  Map<String, ObjectCreator> getTagaName2ObjectCreator();
    protected abstract void createNewObject(XmlAttributeReader xmlAttributeReader, String tagName, String namespaceURI, Stack<READ_OBJECT_TYPE> readObjectsStack, XMLReaderWorkContext<ROOT_TYPE> XMLReaderWorkContext);

    protected abstract void finalizeNewObjectSetup(String tag, String text, Stack<READ_OBJECT_TYPE> readObjectsStack, XMLReaderWorkContext XMLReaderWorkContext);

    public ROOT_TYPE readObject(String filePath, ROOT_TYPE root) {
        return readObject(FhResource.get(Paths.get(filePath)), root);
    }

    public ROOT_TYPE readObject(FhResource fileResource, ROOT_TYPE root) {
        return readObject(fileResource.getContent(), fileResource, root);
    }

    public ROOT_TYPE readObject(byte[] content, FhResource fileResource, ROOT_TYPE root) {
        // read whole file - work around for self-closing ZipFileInflaterInputStream in case of jar urls
        try {
            XMLStreamReader reader = buildReader(new ByteArrayInputStream(content));
            try {
                return readObject(reader, fileResource, root);
            } finally {
                try {
                    reader.close();
                } catch (XMLStreamException e) {
                    FhLogger.error(e); // just log
                }
            }
        } catch (XMLStreamException e) {
            throw new FhException(String.format("Error during parsing file '%s' at %s %d",
                    fileResource, e.getLocalizedMessage(),
                    e.getLocation() != null ? e.getLocation().getLineNumber() : null), e.getNestedException() != null ? e.getNestedException() : e);
        }
    }

    protected ROOT_TYPE readObject(XMLStreamReader reader, FhResource fileUrl, ROOT_TYPE root) throws XMLStreamException {
        XMLReaderWorkContext<ROOT_TYPE> XMLReaderWorkContext = new XMLReaderWorkContext<ROOT_TYPE>(fileUrl, reader, root);
        final XmlAttributeReader xmlAttributeReader = new XmlAttributeReader(reader);
        //Map<String, ObjectCreator> tagNameToObjectCreator = getTagaName2ObjectCreator();
        Stack<READ_OBJECT_TYPE> readObjectsStack = new Stack<>();
        if (root != null) readObjectsStack.add(root);
        String text = "";
        while (reader.hasNext()) {
            int Event = reader.next();
            switch (Event) {
                case XMLStreamConstants.START_ELEMENT: {
                    text = "";
                    String tag = reader.getLocalName();
                    try {
                        createNewObject(xmlAttributeReader, tag, reader.getNamespaceURI(), readObjectsStack, XMLReaderWorkContext);
                    } catch (Exception exc) {
                        FhLogger.error("Error during hadling tag '{}' at {} of {}", tag, reader.getLocation(), fileUrl, exc);
                        throw new XMLStreamException("Error during hadling tag '" + tag + "'!", reader.getLocation(), exc);
                    }
                    if (root == null && !readObjectsStack.isEmpty()) {
                        root = (ROOT_TYPE) readObjectsStack.peek();
                    }
                    break;
                }
                case XMLStreamConstants.CHARACTERS:
                case XMLStreamConstants.CDATA: {
                    text += reader.getText();
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    String tag = reader.getLocalName();
                    try {
                        finalizeNewObjectSetup(tag, text, readObjectsStack, XMLReaderWorkContext);
                    } catch (Exception exc) {
                        FhLogger.error("Error during finalization of tag '{}' at {} of {}", tag, reader.getLocation(), fileUrl, exc);
                        throw new XMLStreamException("Error during finalization of tag '" + tag + "'!", reader.getLocation(), exc);
                    }
                    text = "";
                    break;
                }
            }
        }
        return root;
    }

    private XMLStreamReader buildReader(InputStream input) throws XMLStreamException {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        return factory.createXMLStreamReader(input);
    }

    protected boolean isInheritanceAllowed(Class<?> elementClass, Class<?>[] allowedDerivedClasses) {
        for (Class<?> allowedClass : allowedDerivedClasses) {
            if (allowedClass.isAssignableFrom(elementClass)) {
                return true;
            }
        }

        return false;
    }
}
