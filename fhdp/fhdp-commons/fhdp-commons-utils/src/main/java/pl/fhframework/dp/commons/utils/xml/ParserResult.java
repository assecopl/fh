/*
 * ParserResult.java
 *
 * Prawa autorskie do oprogramowania i jego kodów źródłowych
 * przysługują w pełnym zakresie wyłącznie SKG S.A.
 *
 * All copyrights to software and its source code
 * belong fully and exclusively to SKG S.A.
 */
package pl.fhframework.dp.commons.utils.xml;

import java.util.ArrayList;

/**
 * ParserResult
 *
 * @author dariuszs
 * @version $Revision: 7671 $, $Date: 2011-06-16 11:54:52 +0200 (Cz, 16 cze 2011) $
 */
public class ParserResult {
    private ArrayList arr;

    ParserResult() {
        arr = new ArrayList();
    }

    public void add(ParserMessage smsg) {
        arr.add(smsg);
    }

    public java.util.Iterator getMessages() {
        return arr.iterator();
    }

    public int size() {
        return arr.size();
    }

    public void printOutAllErrors() {
        org.slf4j.LoggerFactory.getLogger(getClass()).info(toString());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        java.util.Iterator i = arr.iterator();
        while (i.hasNext()) {
            sb.append(((ParserMessage) i.next()).toString()).append("\n");
        }
        return sb.toString();
    }
}
