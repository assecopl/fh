package pl.fhframework.dp.commons.jaxb;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RawMessageHelper {

    private static final Logger logger = LoggerFactory.getLogger(RawMessageHelper.class);

    /**
     * Wstawia zadany ciąg znaków we wskazanym miejscu
     */
    public static String insert(String source, String target, int index) {
        String sourceBegin = source.substring(0, index);
        String sourceEnd = source.substring(index);

        StringBuilder result = new StringBuilder(sourceBegin.length() + target.length() + sourceEnd.length());
        result.append(sourceBegin);
        result.append(target);
        result.append(sourceEnd);

        return result.toString();
    }

    /**
     * Ekstrahuje ze strSource podciąg znaków za strFrom a przed strTo. Jeśli
     * nie ma strFrom w strSource to zwracany jest pusty string. Jeśli jest
     * strFrom w strSource a nie ma strTo, to zwracany jest podciąg zaczynający
     * się za strFrom do końca strSource.
     *
     * @param strSource
     * @param strFrom
     * @param strTo
     * @param lastIndexOfStrTo kontroluje czy wyszukiwanie jest greedy, false
     * powoduje wyszukanie najkrótszego spełniającego podciągu true powoduje
     * wyszukanie najdłuższego spełniającego podciągu
     * @return
     */
    public static String extractFromTo(String strSource, String strFrom, String strTo, boolean lastIndexOfStrTo) {
        String strRet = "";
        if (strSource != null) {
            int startIndex = strSource.indexOf(strFrom);

            if (startIndex > -1) {
                startIndex = startIndex + strFrom.length();
                int endIndex = strTo.equals("") ? -1
                        : (lastIndexOfStrTo ? strSource.lastIndexOf(strTo) : strSource.indexOf(strTo, startIndex));

                if (endIndex > -1) {
                    strRet = strSource.substring(startIndex, endIndex);
                } else { // to the end
                    strRet = strSource.substring(startIndex);
                }
            }
        }
        return strRet;
    }

    public static String extractFromTo(String strSource, String strFrom, String strTo) {
        return extractFromTo(strSource, strFrom, strTo, false);
    }

    /**
     * Metoda pobiera najszerszy podciąg znaków pasujący do wzorca (strFrom -
     * strTo)
     *
     * @param strSource
     * @param strFrom
     * @param strTo
     * @return
     */
    public static String extractFromToExtend(String strSource, String strFrom, String strTo) {
        return extractFromTo(strSource, strFrom, strTo, true);
    }

    /**
     * Metoda pobiera najwęższy ciąg znaków pasujący do wzorca (strFrom - strTo)
     *
     * @param strSource
     * @param strFrom
     * @param strTo
     * @return
     */
    public static String extractFromToNarrow(String strSource, String strFrom, String strTo) {
        return extractFromTo(strSource, strFrom, strTo, false);
    }

    public static String stripNS(String xmlName) {
        int colon = xmlName.indexOf(':');

        if (colon > 0 && colon < xmlName.length() - 1) { // xmlName jest w postaci "nsPrefix:tagName" 
            return xmlName.substring(colon + 1);
        }

        return xmlName;
    }

    private static Pattern xmlTagPattern = Pattern.compile("<[:A-Z_a-z].*?>", Pattern.DOTALL); // zgodnie z http://www.w3.org/TR/REC-xml/#NT-NameStartChar pomijając znaki narodowe

    /**
     * Wyciąga pierwszy znaczący tag z wejścia pomijając tagi sterujące
     * (<?xml ...> lub <?xml-stylesheet ...>, ...), komentarze itp. Wynik w
     * postaci <element atr1="fjadkl" ...>
     *
     * @param input
     * @return pierwszy znaczący tag lub <BRAK-ROOT-TAG> jeśli nie ma
     */
    static String getRootStartTag(String input) {
        if (input != null) {
            input = removeBOM(input);

            Matcher matcher = xmlTagPattern.matcher(input);
            if (matcher.find()) {
                return matcher.group();
            }
        }
        return "<BRAK-ROOT-TAG>";
    }

    private static String xmlSpace = "[\\u0020\\u0009\\u000D\\u000A]"; // zgodnie z http://www.w3.org/TR/REC-xml/#NT-S
    private static Pattern xmlSpaceAndRestPattern = Pattern.compile(xmlSpace + ".*");

    /**
     * Ekstrahuje nazwę root-tagu w dokumencie
     *
     * @param strRawData zawartość dokumentu
     * @param stripNS czy usunąć namespace prefix
     * @return
     */
    public static String extractXMLName(String strRawData, boolean stripNS) {
        String xmlName = getRootStartTag(strRawData);

        // get the tag name only
        xmlName = extractFromTo(xmlName, "<", ">");
        xmlName = xmlSpaceAndRestPattern.matcher(xmlName).replaceAll("");

        // strip ns if present
        if (stripNS) {
            xmlName = stripNS(xmlName);
        }

        return xmlName;
    }

    public static String extractXMLName(String strRawData) {
        return extractXMLName(strRawData, true);
    }

    public static String stripRootNSFromXML(String strRawData, boolean withAttr) {
        String xmlName = extractXMLName(strRawData, false);
        if (xmlName.indexOf(':') > 0) {
            String xmlNameNoNS = stripNS(xmlName);
            String ns = xmlName.replace(":" + xmlNameNoNS, "");
            if (StringUtils.isNotBlank(ns) && withAttr) {
                strRawData = strRawData.replace("xmlns:" + ns + "=", "xmlns=");
            }
            strRawData = strRawData.replace("<" + xmlName, "<" + xmlNameNoNS);
            strRawData = strRawData.replace("</" + xmlName, "</" + xmlNameNoNS);
        }
        return strRawData;
    }

    public static String stripRootNSFromXML(String strRawData) {
        return stripRootNSFromXML(strRawData, true);
    }

    private static Pattern xmlSpacePattern = Pattern.compile(xmlSpace);

    /**
     * Ekstrahuje namespace root-tagu w dokumencie niezależnie od formy
     * definicji, tj.
     * <element xmlns="namespace"> lub <ns:element xmlns:ns="namespace">
     *
     * @param strRawData
     * @return
     */
    public static String extractXMLNamespace(String strRawData) {
        strRawData = getRootStartTag(strRawData);
        String nsPrefix = extractFromToNarrow(strRawData, "<", ":");
        String strNamespace;

        // jeśli nsPrefix jest niepusty i nie zawiera spacji, tzn. że wysłano dokument w notacji "<ns1:ElementGlowny ...."
        if (!"".equals(nsPrefix) && (nsPrefix != null && !xmlSpacePattern.matcher(nsPrefix).find())) {
            strNamespace = extractFromTo(strRawData, "xmlns:" + nsPrefix + "=\"", "\"");
        } else {
            strNamespace = extractFromTo(strRawData, "xmlns=\"", "\"");
        }

        return strNamespace;
    }

    public static String extractXMLMessageTypeFromNamespace(InputStream inputStream) {
        String namespace = "";
        try {
            namespace = extractXMLNamespace(IOUtils.toString(inputStream, "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return namespace.substring(namespace.lastIndexOf("/") + 1, namespace.lastIndexOf("."));
    }

    public static String extractSelfRef(String strRawData) {
        //String xmlName = extractXMLName(strRawData);
        String selfRef = extractFromTo(strRawData, "<MesIdeMES19>", "</");
        if ("".equals(selfRef)) {
            selfRef = extractFromTo(strRawData, "NrWlasny=\"", "\"");
        }
        if ("".equals(selfRef)) {
            selfRef = extractFromTo(strRawData, "NrWlasny-LRN=\"", "\"");
        }
        // DPDZ
        if ("".equals(selfRef)) {
            selfRef = extractFromTo(strRawData, "NumerWlasny-LRN=\"", "\"");
        }
        return selfRef;
    }

    public static String extractRefNo(String strRawData) {
        //String xmlName = extractXMLName(strRawData);
        String selfRef = extractFromTo(strRawData, "MRN=\"", "\"");
        /*if ("".equals(selfRef)){
			selfRef = extractFromTo(strRawData,"NrWlasny-LRN=\"","\"");
		}*/
        return selfRef;
    }

    /**
     * Wycina z komunikatu wszystkie przestrzenie nazw z wyjątkiem przestrzeni
     * dla elementu "root"
     */
    public static String cutUnusedNamespaces(String rawData) {
        String rootNamespace = extractXMLNamespace(rawData);
        //cut internal strings xmlns:
        int nsIndex = rawData.indexOf(rootNamespace) + rootNamespace.length();
        if (nsIndex < 0) {
            return rawData;
        }
        int upIndex = nsIndex + rawData.substring(nsIndex).indexOf(">"); //locate endtag
        if (upIndex < 0) {
            return rawData;
        }
        String strRet = new String(rawData.substring(0, upIndex));
        if (strRet.endsWith("/")) {
            upIndex--;
            strRet = rawData.substring(0, upIndex);
        }// one tag /> ended
        String strRest = rawData.substring(upIndex);
        if (!"".equals(rootNamespace)) {
            nsIndex = strRet.indexOf("xmlns:");
            String strN = extractFromTo(strRet, "xmlns:", "/>");
            strN = extractFromTo(strN, "", ">");
            if (!"".equals(strN)) {
                strRet = strRet.replace("xmlns:" + strN, "");
                strN = "xmlns:" + strN + " ";
                String nsRootString = "";
                String strNs = extractFromTo(strN, "xmlns:", "\" ");
                String prevNs = "";//to avoid endless loop
                while (!"".equals(strNs) && !prevNs.equals(strNs)) {
                    strN = strN.replace("xmlns:" + strNs + "\" ", "");
                    String[] ns = strNs.split("=");//other ns's used
                    if ((strNs.contains(rootNamespace)) || strRest.contains("<" + ns[0] + ":")) {
                        nsRootString += "xmlns:" + strNs + "\" ";
                    }
                    prevNs = strNs;
                    strNs = extractFromTo(strN, "xmlns:", "\" ");
                }
                strN = nsRootString + strN;
                strRet = insert(strRet, strN, nsIndex);
            }
        }
        return strRet + strRest;

    }


    public static boolean hasBOM(String input) {
        try {
            return hasBOM(input != null ? ParserHelper.getBytes(input) : null);
        } catch ( JAXBException ex) {
            throw new RuntimeException("Error while determining BOM existance !", ex);
        }
    }

    public static boolean hasBOM(byte[] input) {
        try {
            if (input == null) {
                throw new NullPointerException("Given input is null !");
            }
            BOMInputStream bomInputStream = new BOMInputStream(new ByteArrayInputStream(input));
            return bomInputStream.hasBOM();
        } catch (IOException | NullPointerException ex) {
            throw new RuntimeException("Error while determining BOM existance !", ex);
        }
    }

    public static String removeBOM(String input) {
        try {
            if (!hasBOM(input)) {
                return input;
            }
            return ParserHelper.getString(removeBOM(input != null ? ParserHelper.getBytes(input) : null));
        } catch (JAXBException ex) {
            logger.error("Error while removing BOM, return input", ex);
            return input;
        }
    }

    public static byte[] removeBOM(byte[] input) {
        try {
            if (!hasBOM(input)) {
                return input;
            }
            BOMInputStream bomInputStream = new BOMInputStream(new ByteArrayInputStream(input));
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            IOUtils.copy(bomInputStream, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (Exception ex) {
            logger.error("Error while removing BOM, return input", ex);
            return input;
        }
    }

    public static String addEols(String xml) {
        return xml.replace("><", ">\r\n<");
    }
}
