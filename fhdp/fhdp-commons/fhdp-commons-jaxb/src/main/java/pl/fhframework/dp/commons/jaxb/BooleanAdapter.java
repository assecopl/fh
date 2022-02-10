/*
 * DateAdapter.java
 * 
 * Prawa autorskie do oprogramowania i jego kodów źródłowych 
 * przysługują w pełnym zakresie wyłącznie SKG S.A.
 * 
 * All copyrights to software and its source code
 * belong fully and exclusively to SKG S.A.
 */
package pl.fhframework.dp.commons.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Formatowanie danych logicznych podczas serializacji do XML przez JAXB
 *
 * User: dariuszs
 * Date: 23.08.13
 * Time: 18:36
 *
 * @version $Revision: 44155 $, $Date: 2013-08-27 09:27:48 +0200 (Wt, 27 sie 2013) $
 */
public class BooleanAdapter extends XmlAdapter<String, Boolean> {

    public String marshal(Boolean bool) {
        return Boolean.TRUE.equals(bool) ? "1" : "0";
    }

    public Boolean unmarshal(String boolString) {
        return ("1".equals(boolString) || Boolean.parseBoolean(boolString));
    }
}

