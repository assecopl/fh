package pl.fhframework.dp.commons.utils.xml;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 27/09/2020
 */
public class TextUtils {
    public static String decapitateFirstLetter(String src) {
        if(StringUtils.isEmpty(src)) return src;
        else return src.substring(0, 1).toLowerCase() + src.substring(1);
    }

    public static String capitateFirstLetter(String src) {
        if(StringUtils.isEmpty(src)) return src;
        else return src.substring(0, 1).toUpperCase() + src.substring(1);
    }

    public static String camelCase(String src) {
        return WordUtils.capitalizeFully(src).replace(" ", "");
    }
}
