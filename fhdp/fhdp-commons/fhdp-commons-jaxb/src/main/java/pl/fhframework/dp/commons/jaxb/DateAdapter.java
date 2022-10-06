package pl.fhframework.dp.commons.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.text.SimpleDateFormat;
import java.util.Date;

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

