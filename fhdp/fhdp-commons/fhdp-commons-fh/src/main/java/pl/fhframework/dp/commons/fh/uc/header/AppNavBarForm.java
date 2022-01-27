package pl.fhframework.dp.commons.fh.uc.header;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.dp.commons.base.model.StyleEnum;
import pl.fhframework.model.forms.Form;

import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 2019-07-19
 */
public class AppNavBarForm extends Form<AppNavBarForm.Model> {

    public static enum Language {
        ENGLISH("en"),
        LITHUANIAN("lt"),
        POLISH("pl"),
        NO("no");

        private String value;

        private Language(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }

    @Getter @Setter
    public static class Model {
        private String login;
        private boolean guest;
        private String alternativeStylesheet;
        private String language;
        private String logoutURL;
        private String loginURL;
        private boolean fhCss;
        private boolean defaultCss;
        private List<String> cssIds;
        private StyleEnum selectedStyle;
        private List<StyleEnum> possibleStyles = Arrays.asList(StyleEnum.Default, StyleEnum.Alternate, StyleEnum.Contrast);
        private boolean translationEN;
        private boolean translationPL;
        private boolean translationLT;
        private boolean translationNO;

        private boolean languageDropdown;
        private boolean appSider;
        private boolean helpSider;
        private boolean operationSider;
        private boolean sessionClock;
        private boolean onlyContrastStyle;
        private boolean onlyContrastStyleChecked;

        private String counter = "<span id=\"sessionCounter\">--:--</span>";

        public Model() {
            this.language = Language.ENGLISH.getValue();
            this.onlyContrastStyleChecked = false;
        }

        public String getId(int idx) {
            return this.cssIds.size() > idx ? (String)this.cssIds.get(idx) : "";
        }
    }

}
