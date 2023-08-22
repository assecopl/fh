package pl.fhframework.docs.application.cache;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adam Zareba on 02.02.2017.
 */
@Getter
@Setter
public class ApplicationCacheModel {
    List<StaticTableData> appProps = new ArrayList<>();

    List<StaticTableData> cacheProps = new ArrayList<>();

    List<StaticTableData> jgroupsProps = new ArrayList<>();
}

@Getter
@Setter
class StaticTableData {
    String value1;

    String value2;

    String value3;

    String value4;

    String value5;

    String value6;

    String value7;

    public StaticTableData() {
    }

    public StaticTableData(String value1, String value2) {
        this.value1 = value1;
        this.value2 = value2;
    }

    public StaticTableData(String value1, String value2, String value3) {
        this.value1 = value1;
        this.value2 = value2;
        this.value3 = value3;
    }
}
