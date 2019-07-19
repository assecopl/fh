package pl.fhframework.core.uc.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * @author Paweł Domański pawel.domanski@asseco.pl
 * @desc Helper class that resolve use case layouts
 */
@Service
public class UseCaseLayoutService {

    public static String mainLayout = "standard";
    public static String splitChar = ",";

    @Value("${fhframework.layout.templates}")
    private String templates;

    private UseCaseLayoutService() {
    }

    public String getDefaultLayout() {
        return UseCaseLayoutService.mainLayout;
    }

    public Boolean validateLayout(String layout) {

        String temp = this.templates + UseCaseLayoutService.splitChar + UseCaseLayoutService.mainLayout;
        String[] layouts = temp.split(UseCaseLayoutService.splitChar);
        return Arrays.stream(layouts).anyMatch(layout::equals);
    }

    public String[] getTemplatesArray(){
        return this.templates.split(",");
    }

}
