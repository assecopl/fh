package pl.fhframework.compiler.core.integration.xsd;

import pl.fhframework.core.rules.builtin.DateUtils;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.GregorianCalendar;

public class LocalDateXmlAdapter extends XmlAdapter<String, LocalDate> {
    @Override
    public LocalDate unmarshal(String date) throws Exception {
        if (date == null) {
            return null;
        }
        XMLGregorianCalendar result = DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(date);
        return DateUtils.ldFromDate(result.toGregorianCalendar().getTime());
    }

    @Override
    public String marshal(LocalDate date) throws Exception {
        if (date == null) {
            return null;
        }
        return DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(GregorianCalendar.from(date.atStartOfDay(ZoneId.systemDefault()))).toXMLFormat();
    }
}
