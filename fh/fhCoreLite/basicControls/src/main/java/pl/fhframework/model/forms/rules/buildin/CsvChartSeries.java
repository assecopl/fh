package pl.fhframework.model.forms.rules.buildin;

import pl.fhframework.core.rules.BusinessRule;
import pl.fhframework.model.forms.model.chart.ChartSeries;

import java.math.BigDecimal;
import java.util.*;

/**
 * Changes text CSV-like data into ChartSeries list. May be used for building charts in fast prototyping.
 * Input data should contain semicolon separated X axis values, semicolon separated series names and series values as CSV-like where values are separated with semicolon and series separated with pipe | character.
 * Example RULE.csvChartSeries('2015;2016;2017;2018', 'Series A;Series B', '10;20;30;40|15;30;35;12')
 */
@BusinessRule(categories = "chart")
public class CsvChartSeries {

    /**
     * Changes text CSV-like data into ChartSeries list. May be used for building charts in fast prototyping.
     * @param xValues semicolon separated X axis values
     * @param seriesNames semicolon separated series names
     * @param seriesCsvValues series number values as CSV-like where values are separated with semicolon and series separated with pipe | character
     * @return ChartSeries list
     */
    public List<ChartSeries> csvChartSeries(String xValues, String seriesNames, String seriesCsvValues) {
        String[] xValuesArray = xValues.split(";");
        String[] seriesNamesArray = seriesNames.split(";");

        String[] seriesCsvArray = seriesCsvValues.split("\\|");
        BigDecimal[][] seriesCsvValuesArray = new BigDecimal[seriesNamesArray.length][xValuesArray.length];
        List<ChartSeries> seriesList = new ArrayList<>();

        for (int s = 0; s < seriesNamesArray.length && s < seriesCsvArray.length; s++) {
            String[] seriesValuesArray = seriesCsvArray[s].split(";");
            ChartSeries series = new ChartSeries();
            series.setLabel(seriesNamesArray[s]);
            for (int x = 0; x < xValuesArray.length; x++) {
                BigDecimal value = null;
                if (x < seriesValuesArray.length && !seriesValuesArray[x].trim().isEmpty()) {
                    value = new BigDecimal(seriesValuesArray[x].trim());
                }
                series.getData().put(xValuesArray[x], value);
            }
            seriesList.add(series);
        }
        return seriesList;
    }
}
