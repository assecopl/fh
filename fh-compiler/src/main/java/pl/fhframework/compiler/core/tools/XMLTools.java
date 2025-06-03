package pl.fhframework.compiler.core.tools;

import pl.fhframework.core.FhException;
import pl.fhframework.core.logging.FhLogger;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;

/**
 * Commont tools for XML parsing
 */
public class XMLTools {
    public static XMLStreamReader createXMLReader(Path xmlFilePath) {
        try {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            return factory.createXMLStreamReader(new FileInputStream(xmlFilePath.toFile()));
        } catch (FileNotFoundException exc) {
            FhLogger.error("File not found '{}'", xmlFilePath.toAbsolutePath());
            throw new FhException(exc);
        } catch (XMLStreamException exc) {
            FhLogger.error("Exception while parsing a file '{}'", xmlFilePath.toAbsolutePath());
            throw new FhException(exc);
        }
    }

//    public static void parseXML(Path xmlFilePath, Consumer<? super Path> newTagHandler, BiConsumer<? super Path, String> endTagHandler){
//        XMLStreamReader reader = createXMLReader(xmlFilePath);
//        final XmlAttributeReader attributeReader = new XmlAttributeReader(reader);
//        Map<String, ObjectCreator> tagNameToObjectCreator = getTagNameToObjectCreator();
//        Stack<INPUT_OBJECT_TYPE> readedStack = new Stack<>();
//        if (root != null) readedStack.add(root);
//        try {
//            String text = "";
//            while (reader.hasNext()) {
//                int Event = reader.next();
//                switch (Event) {
//                    case XMLStreamConstants.START_ELEMENT: {
//                        text = "";
//                        String tag = reader.getLocalName();
//                        try {
//                            createNewObject(attributeReader, tag, readedStack, context);
//                        } catch (Exception exc) {
//                            FhLogger.error(e);
//                            throw new XMLStreamException("Error in handling beggining of tag '" + tag + "'!", reader.getLocation(), exc);
//                        }
//                        if (root == null && !readedStack.isEmpty()) {
//                            root = (ROOT_TYPE) readedStack.peek();
//                        }
//                        break;
//                    }
//                    case XMLStreamConstants.CHARACTERS: {
//                        text += reader.getText().trim();
//                        break;
//                    }
//                    case XMLStreamConstants.END_ELEMENT: {
//                        String tag = reader.getLocalName();
//                        try {
//                            finalizeCreationOfNewObject(tag, text, readedStack, context);
//                        } catch (Exception exc) {
//                            FhLogger.error(e);
//                            throw new XMLStreamException("Error in finalization of tag '" + tag + "'!", reader.getLocation(), exc);
//                        }
//                        text = "";
//                        break;
//                    }
//                }
//            }
//        } catch (XMLStreamException e) {
//            FhLogger.error("Error in reading file '{}' w {} {}", fileName, e.getLocalizedMessage(), e.getLocation().getLineNumber(), e);
//        }
//    }
}
