package pl.fhframework.dp.commons.utils.xml;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.fhframework.dp.commons.base.exception.ToolException;

import java.nio.charset.Charset;

public abstract class ParserHelper {

    private static final Logger log = LoggerFactory.getLogger(ParserHelper.class);
    public static final String DEFAULT_ENCODING = "UTF-8";


    private static final String testQouta(String xmlFragment) {
        if (xmlFragment.indexOf("'") != -1 && ((xmlFragment.indexOf("\"") == -1) ||
                (xmlFragment.indexOf("\"") != -1 && xmlFragment.indexOf("'") < xmlFragment.indexOf("\"")))) {
            return "'";
        }
        return "\"";
    }


    public static byte[] getBytes(String input) throws ToolException {
        return getBytes(input, DEFAULT_ENCODING);
    }

    public static byte[] getBytes(String input, String defaultEncoding) throws RuntimeException {
        if (input != null) {
            try {
                return input.getBytes(Charset.forName(parseEncoding(input, defaultEncoding)).name());
            } catch (Exception ex) {
                throw new RuntimeException("Error while converting message !", ex);
            }
        }

        return null;
    }


    public static final String parseEncoding(String xmlTest) throws ToolException {
        return parseEncoding(xmlTest, DEFAULT_ENCODING);
    }

    public static final String parseEncoding(String xmlTest, String defaultEncoding) throws ToolException {
        try {
            if (xmlTest != null
                    && xmlTest.contains("<?xml ")
                    && xmlTest.contains("?>")) {
                String xml = xmlTest.substring(6, xmlTest.indexOf("?>"));
                if (xml.contains("encoding")) {
                    xml = xml.substring(xml.indexOf("encoding") + 8);
                    String qouta = testQouta(xml);
                    xml = xml.substring(xml.indexOf(qouta) + 1);
                    return xml.substring(0, xml.indexOf(qouta));
                }
            }
        } catch (Exception e) {
            throw new ToolException("Failed to parse encoding for: " + xmlTest);
        }
        return StringUtils.isNotEmpty(defaultEncoding) ? defaultEncoding : DEFAULT_ENCODING;
    }

    public static final String parseEncoding(byte[] xmlTest) throws ToolException {
        return parseEncoding(xmlTest, DEFAULT_ENCODING);
    }

    public static final String parseEncoding(byte[] xmlTest, String defaultEncoding) throws ToolException {
        return parseEncoding(getString(xmlTest), defaultEncoding);
    }

    public static String getString(byte[] input) throws ToolException {
        return getString(input, DEFAULT_ENCODING);
    }

    public static String getString(byte[] input, String defaultEncoding) throws ToolException {
        if (input != null) {
            try {
                return new String(input, Charset.forName(parseEncoding(new String(input, Charset.forName(DEFAULT_ENCODING)), defaultEncoding)).name());
            } catch (Exception ex) {
                throw new ToolException("Error while converting message !", ex);
            }
        }
        return null;
    }

}
