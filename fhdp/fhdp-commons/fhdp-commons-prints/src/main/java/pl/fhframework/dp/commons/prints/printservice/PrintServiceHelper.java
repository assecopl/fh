package pl.fhframework.dp.commons.prints.printservice;

import java.io.InputStream;
import org.apache.commons.lang.StringUtils;

public class PrintServiceHelper {

    public static String getTemplateName(PrintEngineEnum printEngine, String templateName) {
        if (printEngine != null
                && StringUtils.isNotBlank(templateName)) {
            return templateName.replaceAll("(\\." + printEngine.getTemplateExtension() + "$)", "") + "." + printEngine.getTemplateExtension();
        }
        return null;
    }

    public static InputStream getTemplate(PrintEngineEnum printEngine, String templateName) {
        if (printEngine != null
                && StringUtils.isNotBlank(templateName)) {
            return PrintServiceHelper.class.getResourceAsStream(printEngine.getTemplateLocation() + "/" + getTemplateName(printEngine, templateName));
        }
        return null;
    }

    public static String getTemplatePath(PrintEngineEnum printEngine, String templateName) {
        if (printEngine != null
                && StringUtils.isNotBlank(templateName)) {
            return PrintServiceHelper.class.getResource(printEngine.getTemplateLocation() + "/" + getTemplateName(printEngine, templateName)) != null ? PrintServiceHelper.class.getResource(printEngine.getTemplateLocation() + "/" + getTemplateName(printEngine, templateName)).getPath() : null;
        }
        return null;
    }

    public static boolean templateExists(PrintEngineEnum printEngine, String templateName) {
        if (printEngine != null
                && StringUtils.isNotBlank(templateName)) {
            return getTemplate(printEngine, templateName) != null;
        }
        return false;
    }

    public static InputStream getFont(PrintEngineEnum printEngine, String fontName) {
        if (printEngine != null
                && StringUtils.isNotBlank(fontName)) {
            String resourceName = printEngine.getFontLocation() + "/" + fontName;
            return PrintServiceHelper.class.getResourceAsStream(resourceName);
        }
        return null;
    }

    public static InputStream getImage(PrintEngineEnum printEngine, String imageName) {
        if (printEngine != null
                && StringUtils.isNotBlank(imageName)) {
            String resourceName = printEngine.getImageLocation() + "/" + imageName;
            return PrintServiceHelper.class.getResourceAsStream(resourceName);
        }
        return null;
    }
    
    public static InputStream getConfiguration(PrintEngineEnum printEngine, String configFile) {
        if (printEngine != null
                && StringUtils.isNotBlank(configFile)) {
            String resourceName = printEngine.getConfigurationLocation()+ "/" + configFile;
            return PrintServiceHelper.class.getResourceAsStream(resourceName);
        }
        return null;
    }
}
