package pl.fhframework.dp.commons.prints.printservice;

/**
 * PrintEngineEnum
 *
 * @author <a href="mailto:pawel_kasprzak@skg.pl">Paweł Kasprzak</a>
 * @version $Revision: $, $Date: $
 */
public enum PrintEngineEnum {
    FOP("fop", "xsl"),
    JASPER("jasper", "jrxml");

    private final String BASE_LOCATION = "/META-INF";
    private final String TEMPLATES_LOCATION = "templates";
    private final String FONTS_LOCATION = "fonts";
    private final String IMAGES_LOCATION = "images";
    private final String CONFIGURATION_LOCATION = "configuration";
    private final String sourceLocation;
    private final String templateExtension;

    private PrintEngineEnum(String sourceFolder, String templateExtension) {
        this.sourceLocation = sourceFolder;
        this.templateExtension = templateExtension;
    }

    public String getTemplateExtension() {
        return templateExtension;
    }

    public String getTemplateLocation() {
        return BASE_LOCATION + "/" + sourceLocation + "/" + TEMPLATES_LOCATION;
    }

    public String getImageLocation() {
        return BASE_LOCATION + "/" + sourceLocation + "/" + IMAGES_LOCATION;
    }

    public String getFontLocation() {
        return BASE_LOCATION + "/" + sourceLocation + "/" + FONTS_LOCATION;
    }

    public String getConfigurationLocation() {
        return BASE_LOCATION + "/" + sourceLocation + "/" + CONFIGURATION_LOCATION;
    }

}
