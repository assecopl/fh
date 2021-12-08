
package pl.fhframework.dp.transport.prints;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter @Setter
public class PrintRequestType {

    protected byte[] source;
    protected String sourceType;
    protected byte[] template;
    protected List<FormatType> outputFormat;
    protected List<ParameterType> parameter;
    protected byte[] bundleDefault;
    protected String localeLanguage;
    protected String localeCountry;
}
