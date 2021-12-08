/*
 * SchemaParser.java
 * 
 * Prawa autorskie do oprogramowania i jego kodów źródłowych 
 * przysługują w pełnym zakresie wyłącznie SKG S.A.
 * 
 * All copyrights to software and its source code
 * belong fully and exclusively to SKG S.A.
 */
package pl.fhframework.dp.commons.utils.xml;


import org.xml.sax.SAXException;
import pl.fhframework.dp.commons.utils.file.FileHelper;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.InputStream;

/**
 * SchemaParser
 *
 * @author dariuszs
 * @version $Revision: 7671 $, $Date: 2011-06-16 11:54:52 +0200 (Cz, 16 cze 2011) $
 */
public class SchemaParser {

    private static final String xmldsigCoreSchema;
    private static final String DEFAULT_ENCODING = "utf8";

    static {
        try {
            InputStream in = ParserHelper.class.getResourceAsStream("/xsd/xmldsig-core-schema.xsd");
            xmldsigCoreSchema = FileHelper.readInputStream(in, DEFAULT_ENCODING);
            in.close();
        } catch (Exception e) {
            org.slf4j.LoggerFactory.getLogger(SchemaParser.class).warn(e.getMessage(), e);
            throw new RuntimeException("caught exception in static block: " + e.getMessage());
        }
    }

    static public final StreamSource getXmldsigCoreSchema() {
        return new StreamSource(new java.io.StringReader(xmldsigCoreSchema));
    }

    static public final Schema getSchema(InputStream in, boolean attachXmldsig) throws IOException, SAXException {
        return getSchema(in, DEFAULT_ENCODING, attachXmldsig);
    }

    static public final Schema getSchema(InputStream in, String encoding, boolean attachXmldsig) throws IOException, SAXException {
        Schema schema;
        try {
            String xsd = FileHelper.readInputStream(in, encoding);
            in.close();
            //wycięcia BOM'a
            if (xsd.indexOf("<") > 0) {
                xsd = xsd.substring(xsd.indexOf("<"));
            }
            if (attachXmldsig) {
                StreamSource ss[] = new StreamSource[2];
                ss[0] = getXmldsigCoreSchema();
                ss[1] = new StreamSource(new java.io.StringReader(xsd)); //new StreamSource(in);
                schema = compileSchema(ss);
            } else {
                StreamSource ss[] = {new StreamSource(new java.io.StringReader(xsd))};
                schema = compileSchema(ss);
            }
        } finally {
        }
        return schema;
    }

    static public final Schema getSchema(StreamSource xsd, boolean atachXmldsig) throws SAXException {
        Schema schema;
        if (atachXmldsig) {
            StreamSource ss[] = {getXmldsigCoreSchema(), xsd};
            schema = compileSchema(ss);
        } else {
            StreamSource ss[] = {xsd};
            schema = compileSchema(ss);
        }
        return schema;
    }

    static public final Schema compileSchema(StreamSource ss[]) throws SAXException {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            factory.setFeature(javax.xml.XMLConstants.FEATURE_SECURE_PROCESSING, true);
            return factory.newSchema(ss);
    }

    public static ParserResult parse(Schema schema, InputStream xmlStream) {
        ParserErrorHandler parserErrorHandler = new ParserErrorHandler();

        try {
            Validator v = schema.newValidator();
            v.setErrorHandler(parserErrorHandler);
            v.validate(new StreamSource(xmlStream));
        } catch (Exception e) {
            parserErrorHandler.other(e);
        }

        return parserErrorHandler.getResult();
    }
}
