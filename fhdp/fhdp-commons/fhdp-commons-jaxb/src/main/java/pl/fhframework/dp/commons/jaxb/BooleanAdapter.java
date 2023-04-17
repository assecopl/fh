package pl.fhframework.dp.commons.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class BooleanAdapter extends XmlAdapter<String, Boolean> {

    public String marshal(Boolean bool) {
        if(bool == null) {
            return null;
        }
        else {
            return Boolean.TRUE.equals(bool) ? "1" : "0";
        }
    }

    public Boolean unmarshal(String boolString) {
        if(boolString == null) {
            return null;
        } else {
            return ("1".equals(boolString) || Boolean.parseBoolean(boolString));
        }
    }
}

