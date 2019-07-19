package pl.fhframework.trees;

import pl.fhframework.core.FhFormException;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.tools.loading.XmlUtils;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;


public class TreeElementXmlUtil {

    public String convertToXml(ITreeElement root, boolean prettyFormat) {
        StringBuilder xmlSB = new StringBuilder();
        xmlSB.append("<menu>");
        writeChildren(xmlSB, root.getSubelements());
        xmlSB.append("</menu>");
        String xml = xmlSB.toString();
        if (prettyFormat) {
            xml = prettyPrintXml(xml);
        }
        return xml;
    }

    private void writeChildren(StringBuilder xml, List<ITreeElement> children) {
        for (ITreeElement e : children) {
            if (e.isGrouping()) {
                writeElem("group", xml, e);
            } else {
                writeElem("usecase", xml, e);
            }
        }
    }

    private void writeElem(String tag, StringBuilder xml, ITreeElement e) {
        xml.append("<").append(tag).append(" ");

        xml.append(formatXmlAttribute("ref", e.getRef()));
        xml.append(" ");

        xml.append(formatXmlAttribute("label", e.getLabel()));
        xml.append(" ");

        //xml.append(formatXmlAttribute("description", e.get()));
        //xml.append(" ");

        xml.append(formatXmlAttribute("icon", e.getIcon()));
        xml.append(" ");

        //xml.append(formatXmlAttribute("coords", e.get()));
        //xml.append(" ");

        xml.append(formatXmlAttribute("mode", convertModesToStr(e.getModes())));
        xml.append(" ");

        if (e instanceof UseCaseInformation) {
            xml.append(formatXmlAttribute("cloudExposed", Boolean.toString(((UseCaseInformation) e).isCloudExposed())));
            xml.append(" ");
            if (!StringUtils.isNullOrEmpty(((UseCaseInformation) e).getInputFactory())){
                xml.append(formatXmlAttribute("inputFactory", ((UseCaseInformation) e).getInputFactory()));
                xml.append(" ");
            }
        }

        if (e.getSubelements().isEmpty()) {
            xml.append("/>");
        } else {
            xml.append(">");

            writeChildren(xml, e.getSubelements());

            xml.append("</").append(tag).append(">");//end
        }
    }

    private String formatXmlAttribute(String name, String value) {
        if (value == null) {
            return "";
        } else {
            return String.format("%s=\"%s\"", name, XmlUtils.encodeAttribute(value));
        }
    }

    private String convertModesToStr(List<String> modes) {
        if (modes == null) {
            return null;
        } else {
            String res = "";
            for (String m : modes) {
                if (!res.isEmpty()) {
                    res += ",";
                }
                res += m;
            }
            return res;
        }
    }

    //pl.fhframework.tools.loading.FormWriter.prettyPrintXml()
    private static String prettyPrintXml(String input) {
        Source xmlInput = new StreamSource(new StringReader(input));
        StringWriter stringWriter = new StringWriter();
        StreamResult xmlOutput = new StreamResult(stringWriter);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.STANDALONE, "no"); // possible bug in implementation. Without this declaration there is no new line after XML_DECLARATION
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.transform(xmlInput, xmlOutput);
        } catch (TransformerException e) {
            throw new FhFormException("Error parsing saved form", e);
        }

        return xmlOutput.getWriter().toString();
    }

}
