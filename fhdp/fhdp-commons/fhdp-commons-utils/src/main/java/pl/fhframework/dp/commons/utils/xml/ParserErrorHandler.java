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

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * ParserErrorHandler
 *
 * @author dariuszs
 * @version $Revision: 7671 $, $Date: 2011-06-16 11:54:52 +0200 (Cz, 16 cze 2011) $
 */
class ParserErrorHandler implements ErrorHandler {

    protected ParserResult result;

    ParserErrorHandler() {
        result = new ParserResult();
    }

    public void error(SAXParseException arg0) throws SAXException {
        add(arg0);
    }

    public void fatalError(SAXParseException arg0) throws SAXException {
        add(arg0);
    }

    public void warning(SAXParseException arg0) throws SAXException {
        add(arg0);
    }

    public void other(Exception arg0) {
        if (arg0 instanceof SAXParseException) {
            add((SAXParseException) arg0);
        } else {
            result.add(new ParserMessage(arg0.getMessage(), null));
        }
    }

    protected void add(String errorMessage, int lineNumber, int columnNumber) {
        if (errorMessage != null && errorMessage.startsWith("cvc-") && errorMessage.indexOf(": ") > 0) {
            errorMessage = errorMessage.substring(errorMessage.indexOf(": ") + 2);
        }
        errorMessage = errorMessage.replaceAll("\"http:.+?\\.xsd\":", "");
        result.add(new ParserMessage(errorMessage, lineNumber, columnNumber));
    }

    protected void add(SAXParseException ex) {
        add(ex.getMessage(), ex.getLineNumber(), ex.getColumnNumber());
    }

    public static void main(String[] args) {
        ParserErrorHandler ph = new ParserErrorHandler();
        ph.add("cvc-complex-type.2.4.a: Invalid content was found starting with element 'Nadawca'. One of '{\"http://www.krakow.uc.gov.pl/Celina/CLN-XML/xsd/IE15w2r0.xsd\":Odbiorca, \"http://www.krakow.uc.gov.pl/Celina/CLN-XML/xsd/IE15w2r0.xsd\":GlownyZobowiazany, \"http://www.krakow.uc.gov.pl/Celina/CLN-XML/xsd/IE15w2r0.xsd\":UpowaznionyOdbiorca, \"http://www.krakow.uc.gov.pl/Celina/CLN-XML/xsd/IE15w2r0.xsd\":Transport, \"http://www.krakow.uc.gov.pl/Celina/CLN-XML/xsd/IE15w2r0.xsd\":TransportNaGranicy, \"http://www.krakow.uc.gov.pl/Celina/CLN-XML/xsd/IE15w2r0.xsd\":Towar}' is expected.; Linia: 9 Kolumna: 151", 1, 1);
        org.slf4j.LoggerFactory.getLogger(ParserErrorHandler.class).info(ph.getResult().getMessages().next().toString());
    }

    public ParserResult getResult() {
        return result;
    }
}
