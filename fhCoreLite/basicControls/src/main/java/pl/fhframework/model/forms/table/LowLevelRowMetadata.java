package pl.fhframework.model.forms.table;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Adam Zareba on 25.01.2017.
 */
@Setter
@Getter
public class LowLevelRowMetadata {

    private Map<String, RowIteratorMetadata> iteratorData = new LinkedHashMap<>();

    private int[] iteratorsIndices;

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder("[");
        iteratorData.forEach((iter, data) -> output.append(String.format("\n   %s -> %s", iter, data.toString())));
        output.append("\n]");
        return output.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LowLevelRowMetadata)) return false;
        LowLevelRowMetadata that = (LowLevelRowMetadata) obj;
        return that.iteratorData.equals(this.iteratorData);
    }

    @Override
    public int hashCode() {
        return iteratorData.size();
    }
}
