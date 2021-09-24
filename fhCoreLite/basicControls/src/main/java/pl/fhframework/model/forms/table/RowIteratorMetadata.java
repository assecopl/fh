package pl.fhframework.model.forms.table;

import java.util.concurrent.atomic.AtomicInteger;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Adam Zareba on 25.01.2017.
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RowIteratorMetadata {

    private int index; // index in this iterator's collection

    private Object businessObject;

    private AtomicInteger rowSpan;

    private boolean firstOccurrence; // first occurrence in row span, !firstOccurrence => don't add component

    public RowIteratorMetadata getCopy() {
        return new RowIteratorMetadata(index, businessObject, rowSpan, firstOccurrence);
    }

    @Override
    public String toString() {
        return String.format("{ index: %d  /  rowSpan: %-2d  /  first: %5s}", index, rowSpan.get(), firstOccurrence);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RowIteratorMetadata that = (RowIteratorMetadata) o;
        if (index != that.index) return false;
        if (firstOccurrence != that.firstOccurrence) return false;
        if ((rowSpan == null) != (that.rowSpan == null)) return false;
        if (rowSpan != null) return rowSpan.get() == that.rowSpan.get();
        return true;
    }

    @Override
    public int hashCode() {
        return index;
    }
}
