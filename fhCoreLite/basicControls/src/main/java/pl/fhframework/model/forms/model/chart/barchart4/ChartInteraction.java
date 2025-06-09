package pl.fhframework.model.forms.model.chart.barchart4;

import lombok.Data;

@Data
public class ChartInteraction {
    /**
     * Określa tryb interakcji. Przykładowe wartości:
     * "nearest", "index", "dataset", "point", itp.
     */
    private String mode = "nearest";

    /**
     * Czy interakcja wymaga przecięcia się z punktem.
     */
    private boolean intersect = true;
}
