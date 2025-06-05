package pl.fhframework.docs.i18n.forms;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.docs.i18n.ExampleI18nEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class InternationalizationModel {

    private String welcomeMessage;
    private List<String> tableMsg = new ArrayList<>();
    private String messageFromModel;
    private int activeBtn;
    private String argA;
    private List<ExampleI18nEnum> exampleI18nEnums = Arrays.asList(ExampleI18nEnum.values());
}
