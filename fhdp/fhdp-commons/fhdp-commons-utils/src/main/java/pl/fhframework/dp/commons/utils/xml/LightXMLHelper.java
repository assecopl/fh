package pl.fhframework.dp.commons.utils.xml;
/*
 * LightXMLHelper.java
 *
 * Prawa autorskie do oprogramowania i jego kodów źródłowych
 * przysługują w pełnym zakresie wyłącznie SKG S.A.
 *
 * All copyrights to software and its source code
 * belong fully and exclusively to SKG S.A.
 */

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * LightXMLHelper light SAX XMLhelper implementation
 *
 * @author <a href="mailto:pawelk@skg.pl">Pawel Kasprzak</a>
 * @version $Revision: 1487 $, $Date: 2019-01-03 18:18:38 +0100 (czw) $
 */
public class LightXMLHelper {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(LightXMLHelper.class);
    private boolean namespaceAware = true;
    private SAXParserFactory factory;
    private SAXParser parser;
    private CustomHandler handler;
    private String lastError;
    private boolean created;
    private boolean reseted;

    public LightXMLHelper() {
        super();
        initalize();
    }

    public LightXMLHelper(boolean namespaceAware) {
        this.namespaceAware = namespaceAware;
        initalize();
    }

    private void initalize() {
        try {
            factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(namespaceAware);
            if (namespaceAware) {
                factory.setFeature("http://xml.org/sax/features/namespaces", true);
                factory.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
            }
            parser = factory.newSAXParser();
            handler = new CustomHandler();
        } catch (Exception ex) {
            logger.error("Initializacion exception", ex);
            lastError = ex.getMessage();
        }
    }

    public int load(byte[] message) {
        return load(new ByteArrayInputStream(message));
    }

    public int load(InputStream message) {
        try {
            if (parser == null) {
                return 0;
            }
            parser.reset();
            parser.parse(new InputSource(message), handler);
            created = true;
        } catch (Exception ex) {
            logger.error("Initialization exception", ex);
            lastError = ex.getMessage();
            return -1;
        }
        return 1;
    }

    public boolean isValidXML(byte[] messageData) {
        return load(messageData) == 1;
    }

    public boolean isValidXML(InputStream messageData) {
        return load(messageData) == 1;
    }

    public String getAttr(byte[] messageData, String nodeName, int nodeLevel, String attrName) {
        String attr = null;
        if (handler != null) {
            handler.reset();
            handler.setNodeName(nodeName);
            handler.setNodeLevel(nodeLevel);
            handler.setAttrName(attrName);
            if (load(messageData) == 1) {
                attr = handler.getAttrValue();
            }
        }

        return attr;
    }

    public String getLastError() {
        return lastError;
    }

    public String getDocumentName() {
        return handler != null ? handler.getDocumentName() : null;
    }

    public String getDocumentNamespace() {
        return handler != null ? handler.getDocumentNamespace() : null;
    }

    private class CustomHandler extends DefaultHandler {

        private String documentName;
        private String documentNamespace;
        private String nodeName;
        private int nodeLevel;
        private String attrName;
        private String attrValue;
        private int currentLevel;

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (currentLevel == 0) {
                this.documentName = !StringUtils.isEmpty(localName) ? localName : qName;
                if (!StringUtils.isEmpty(uri)) {
                    this.documentNamespace = uri;
                } else {
                    if (attributes != null && attributes.getLength() > 0) {
                        for (int i = 0; i < attributes.getLength(); i++) {
                            String name = !StringUtils.isEmpty(attributes.getLocalName(i)) ? attributes.getLocalName(i) : attributes.getQName(i);
                            if ("xmlns".equals(name)) {
                                this.documentNamespace = attributes.getValue(i);
                                break;
                            }
                        }
                    }
                }
            }
            if (!StringUtils.isEmpty(nodeName)
                    && !StringUtils.isEmpty(attrName)
                    && nodeLevel >= 0) {
                String name = !StringUtils.isEmpty(localName) ? localName : qName;
                if (currentLevel == nodeLevel) {
                    if ("*".equals(nodeName) || nodeName.equals(name)) {
                        if (!StringUtils.isEmpty(attributes.getValue(attrName))) {
                            this.attrValue = attributes.getValue(attrName);
                        }
                    }
                }
            }

            currentLevel++;
        }

        public void reset() {
            this.attrName = null;
            this.attrValue = null;
            this.documentName = null;
            this.nodeName = null;
            this.nodeLevel = 0;
            this.currentLevel = 0;
        }

        public String getDocumentName() {
            return documentName;
        }

        public String getDocumentNamespace() {
            return documentNamespace;
        }

        public String getNodeName() {
            return nodeName;
        }

        public void setNodeName(String nodeName) {
            this.nodeName = nodeName;
        }

        public int getNodeLevel() {
            return nodeLevel;
        }

        public void setNodeLevel(int nodeLevel) {
            this.nodeLevel = nodeLevel;
        }

        public String getAttrName() {
            return attrName;
        }

        public void setAttrName(String attrName) {
            this.attrName = attrName;
        }

        public String getAttrValue() {
            return attrValue;
        }
    }
}