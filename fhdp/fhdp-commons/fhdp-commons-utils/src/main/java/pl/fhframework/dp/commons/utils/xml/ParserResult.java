/*
 * ParserResult.java
 *
 */
package pl.fhframework.dp.commons.utils.xml;

import java.util.ArrayList;


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
