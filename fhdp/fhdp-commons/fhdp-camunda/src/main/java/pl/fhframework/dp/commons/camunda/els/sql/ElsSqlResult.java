package pl.fhframework.dp.commons.camunda.els.sql;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author <a href="mailto:jacek_borowiec@skg.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 10/08/2021
 */
@Getter @Setter
public class ElsSqlResult {
    private List<Header> columns;
    private List<JsonNode> rows;

    @Getter @Setter
    public static class Header {
        private String name;
        private String type;
    }
}
