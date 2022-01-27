package pl.fhframework.dp.commons.utils.lng;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 28/12/2020
 */
@Slf4j
public class LngDescriptionUtilTest {

    //@Test
    public void toJson() {
        String json = createJson();
        log.info("Json: {}", json);
    }

    //@Test
    public void getDescription() {
        String json = createJson();
        String txt = LngDescriptionUtil.getDescription(json, "lt");
        log.info("Text: {}", txt);
    }

    private String createJson() {
        LngDescriptionHolder holder = new LngDescriptionHolder();
        LngDescription dsc1 = new LngDescription();
        dsc1.setLng("en");
        dsc1.setText("The quick brown fox jumps over the lazy dog.");
        holder.getDescriptions().add(dsc1);
        LngDescription dsc2 = new LngDescription();
        dsc2.setLng("lt");
        dsc2.setText("Greita rudoji lapė šokinėja per tingų šunį.");
        holder.getDescriptions().add(dsc2);
        String json = LngDescriptionUtil.toJson(holder);
        return json;
    }
}