package pl.fhframework.dp.commons.utils.lng;

import lombok.extern.slf4j.Slf4j;
import pl.fhframework.dp.commons.utils.conversion.BeanConversionUtil;


/**
 * @author <a href="mailto:jacek_borowiec@skg.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 28/12/2020
 */
@Slf4j
public class LngDescriptionUtil {
    public static String toJson(LngDescriptionHolder holder) {
       return BeanConversionUtil.toJson(holder);
    }

    public static String getDescription(String json, String lng) {
        if(!json.startsWith("{") && !json.contains("descriptions")) return json;
        try {
            LngDescriptionHolder holder = (LngDescriptionHolder) BeanConversionUtil.getFromJson(json, LngDescriptionHolder.class);
            for(LngDescription description: holder.getDescriptions()) {
                if(lng.toLowerCase().equals(description.getLng().toLowerCase())) {
                    return description.getText();
                }
            }
            return holder.getDescriptions().get(0).getText();
        } catch (Exception e) {
            log.error("Conversion error", e);
            return json;
        }

    }
}
