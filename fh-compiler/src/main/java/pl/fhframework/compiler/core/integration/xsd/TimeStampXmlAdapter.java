package pl.fhframework.compiler.core.integration.xsd;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.GregorianCalendar;

public class TimeStampXmlAdapter extends XmlAdapter<String, Date> {
    @Override
    public Date unmarshal(String timestamp) throws Exception {
        if (timestamp == null) {
            return null;
        }
        XMLGregorianCalendar result = DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(timestamp);
        return result.toGregorianCalendar().getTime();
    }

    @Override
    public String marshal(Date timestamp) throws Exception {
        if (timestamp == null) {
            return null;
        }
        return DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(GregorianCalendar.from(ZonedDateTime.ofInstant(timestamp.toInstant(),
                        ZoneId.systemDefault()))).toXMLFormat();
    }
}
