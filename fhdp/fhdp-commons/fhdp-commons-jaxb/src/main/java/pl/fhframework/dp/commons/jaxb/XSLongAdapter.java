package pl.fhframework.dp.commons.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author <a href="mailto:jacek_borowiec@skg.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 06/04/2021
 */
public class XSLongAdapter extends XmlAdapter<String, Long> {
    @Override
    public String marshal(Long id) throws Exception {
        if(id==null) return "" ;
        return id.toString();
    }
    @Override
    public Long  unmarshal(String id) throws Exception {
        return  Long.parseLong(id);
    }
}
