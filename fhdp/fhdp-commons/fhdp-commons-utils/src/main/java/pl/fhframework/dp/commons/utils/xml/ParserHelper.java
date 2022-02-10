package pl.fhframework.dp.commons.utils.xml;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.fhframework.dp.commons.base.exception.ToolException;

import java.nio.charset.Charset;

/**
 * @author <a href="mailto:dariusz_skrudlik@skg.pl">Dariusz Skrudlik</a>
 * @version $Revision: 7534 $, $Date: 2011-06-09 12:37:40 +0200 (Cz, 09 cze 2011) $
 *          <p/>
 *          Klasa pośrednika w wywołaniu parsera dokumentów XML.
 *          Zawiera metody statyczne oraz deklarację metod parsujących.
 */
public abstract class ParserHelper {

    private static final Logger log = LoggerFactory.getLogger(ParserHelper.class);
    public static final String DEFAULT_ENCODING = "UTF-8";

    /**
     * Parsuje początek przekazanego xml'a w celu wykrycia kodowania jaki został
     * zadeklarowany w dokumencie.
     *
     * @param xmlTest -
     *                dokument xml
     * @return String - zadeklarowany encoding lub UTF-8 gdy brak deklaracji
     */
//    public static final String parseEncoding(String xmlTest) {
//        String encoding = "UTF-8";
//        String xml = xmlTest;
////        try {
//        if (xml != null && xml.indexOf("<?xml ") != -1) {
//            xml = xml.substring(6, xml.indexOf("?>"));
//
//            if (xml.indexOf("encoding") != -1) {
//                xml = xml.substring(xml.indexOf("encoding") + 8);
//
//                String qouta = testQouta(xml);
//
//                xml = xml.substring(xml.indexOf(qouta) + 1);
//                encoding = xml.substring(0, xml.indexOf(qouta));
//            }
//        }
////        } catch (Exception e) {
////            throw new ToolException(
////                    "Nieudana próba rozpoznania encoding'u dla: " + xml);
////        }
//
//        return encoding;
//    }

    /**
     * Check what type of qouta is used: " or '
     */
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

    /**
     * Parsuje początek przekazanego xml'a w celu wykrycia kodowania jaki został
     * zadeklarowany w dokumencie.
     *
     * @param xmlTest - dokument xml
     * @return String - zadeklarowany encoding lub UTF-8 gdy brak deklaracji
     * @throws ToolException
     */
    public static final String parseEncoding(String xmlTest) throws ToolException {
        return parseEncoding(xmlTest, DEFAULT_ENCODING);
    }

    /**
     * Parsuje początek przekazanego xml'a w celu wykrycia kodowania jaki został
     * zadeklarowany w dokumencie.
     *
     * @param xmlTest - dokument xml
     * @return String - zadeklarowany encoding lub UTF-8 gdy brak deklaracji
     * @throws ToolException
     */
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
            throw new ToolException("Nieudana próba rozpoznania encoding'u dla: " + xmlTest);
        }
        return StringUtils.isNotEmpty(defaultEncoding) ? defaultEncoding : DEFAULT_ENCODING;
    }

    /**
     * Parsuje początek przekazanego xml'a w celu wykrycia kodowania jaki został
     * zadeklarowany w dokumencie.
     *
     * @param xmlTest - dokument xml
     * @return String - zadeklarowany encoding lub UTF-8 gdy brak deklaracji
     * @throws ToolException
     */
    public static final String parseEncoding(byte[] xmlTest) throws ToolException {
        return parseEncoding(xmlTest, DEFAULT_ENCODING);
    }

    /**
     * Parsuje początek przekazanego xml'a w celu wykrycia kodowania jaki został
     * zadeklarowany w dokumencie.
     *
     * @param xmlTest - dokument xml
     * @param defaultEncoding - domyślny encoding
     * @return String - zadeklarowany encoding lub defaultEncoding (jeżeli
     * strona kodowa nie jest określona)
     * @throws ToolException
     */
    public static final String parseEncoding(byte[] xmlTest, String defaultEncoding) throws ToolException {
        return parseEncoding(getString(xmlTest), defaultEncoding);
    }

    /**
     * Funkcja zwraca String dla zadanego argumentu typu byte[] (stanowiacego
     * reprezentację binarną XML) w stronie kodowej określonej atrybutem
     * encoding W przypadku gdy encoding nie jest okreslony, konwersja jest
     * realizowana z wykorzystaniem ParserHelper.DEFAULT_ENCODING
     *
     * @param input argument wejściowy
     * @return String w stronie kodowej określonej atrybutem encoding lub
     * ParserHelper.DEFAULT_ENCODING (jeżeli strona kodowa nie jest określona)
     * @throws ToolException
     */
    public static String getString(byte[] input) throws ToolException {
        return getString(input, DEFAULT_ENCODING);
    }

    /**
     * Funkcja zwraca String dla zadanego argumentu typu byte[] (stanowiacego
     * reprezentację binarną XML) w stronie kodowej określonej atrybutem
     * encoding W przypadku gdy encoding nie jest okreslony, konwersja jest
     * realizowana z wykorzystaniem defaultEncoding
     *
     * @param input argument wejściowy
     * @param defaultEncoding
     * @return String w stronie kodowej określonej atrybutem encoding lub
     * defaultEncoding (jeżeli strona kodowa nie jest określona)
     * @throws ToolException
     */
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
