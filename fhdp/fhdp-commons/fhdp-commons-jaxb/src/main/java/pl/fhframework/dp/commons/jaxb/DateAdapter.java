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
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * User: dariuszs
 * Date: 23.08.13
 * Time: 18:36
 *
 * @version $Revision: 44155 $, $Date: 2013-08-27 09:27:48 +0200 (Wt, 27 sie 2013) $
 */
public class DateAdapter extends XmlAdapter<String, Date> {

    // the desired format
    private String pattern = "yyyy-MM-dd";

    public String marshal(Date date) throws Exception {
        return date != null ? new SimpleDateFormat(pattern).format(date) : null;
    }

    public Date unmarshal(String dateString) throws Exception {
        return dateString != null ? new SimpleDateFormat(pattern).parse(dateString) : null;
    }
}

