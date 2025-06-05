package pl.fhframework.docs.menu;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.List;

/**
 * Created by pawel.ruta on 2018-08-09.
 */
@Builder
@Getter
public class DefaultAppMenuModel {
    @Singular
    List<StaticTableData> ucAttrs;

    @Getter
    @Builder
    public static class StaticTableData {
        String value1;

        String value2;

        String value3;
    }
}
