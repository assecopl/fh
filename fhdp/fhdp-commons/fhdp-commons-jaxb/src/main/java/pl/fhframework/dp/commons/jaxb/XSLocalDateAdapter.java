/*
 * Copyright (c) 2019. SKG S.A.
 */

package pl.fhframework.dp.commons.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDate;

/**
 * @author <a href="mailto:jacek_borowiec@skg.pl">Jacek Borowiec</a>
 * @version $Revision:  $, $Date:  $
 * @created 2019-01-16
 */
public class XSLocalDateAdapter extends XmlAdapter<String, LocalDate> {

    public LocalDate unmarshal(String v) throws Exception {
        return LocalDate.parse(v);
    }

    public String marshal(LocalDate v) throws Exception {
        if(v == null) return null;
        return v.toString();
    }
}
