package pl.fhframework.dp.commons.jaxb;



import org.apache.commons.lang3.StringUtils;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Created by Krzysiek Stuglik on 30.08.2019.
 */
public class CDATAAdapter extends XmlAdapter<String, String> {
    @Override
    public String unmarshal(String v) throws Exception {
        v = v.trim();
        if (v.startsWith("<![CDATA[")) {
            v = v.substring(9);
            int i = v.indexOf("]]>");
            if (i == -1) {
                throw new IllegalStateException(
                        "Argument starts with <![CDATA[ but cannot find pairing ]]>");
            }
            v = v.substring(0, i);
        }
        return v;
    }

    @Override
    public String marshal(String v) throws Exception {
        if (StringUtils.isNotBlank(v) && !v.contains("<![CDATA[")) {
            return "<![CDATA[" + v + "]]>";
        }
        return v;
    }

    public static boolean isCData(String textToCheck) {
        return StringUtils.isNotBlank(textToCheck)
                && textToCheck.startsWith("<![CDATA[") && textToCheck.endsWith("]]>");
    }
}
