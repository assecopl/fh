package pl.fhframework.binding;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Piotr on 2017-01-26.
 */
@Getter
public class RowNumberBindingContext {

    private String iterator;

    private int rowNumber;

    private IRowNumberOffsetSupplier offsetSupplier;

    public RowNumberBindingContext() {
    }

    public RowNumberBindingContext(String iterator, int rowNumber) {
        this.iterator = iterator;
        this.rowNumber = rowNumber;
    }

    public RowNumberBindingContext(String iterator, int rowNumber, IRowNumberOffsetSupplier offsetSupplier) {
        this.iterator = iterator;
        this.rowNumber = rowNumber;
        this.offsetSupplier = offsetSupplier;
    }
}
