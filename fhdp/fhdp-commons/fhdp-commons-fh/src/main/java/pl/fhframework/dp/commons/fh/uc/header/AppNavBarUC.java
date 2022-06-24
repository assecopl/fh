package pl.fhframework.dp.commons.fh.uc.header;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import pl.fhframework.dp.commons.base.model.StyleEnum;
import pl.fhframework.dp.commons.fh.uc.FhdpBaseUC;
import pl.fhframework.dp.commons.fh.utils.FhUtils;
import pl.fhframework.ISystemUseCase;
import pl.fhframework.annotations.Action;
import pl.fhframework.app.config.DefaultApplicationConfigurer;
import pl.fhframework.app.menu.INavbar;
import pl.fhframework.app.menu.MenuService;
import pl.fhframework.core.rules.builtin.FhUserUtils;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.event.EventRegistry;

import java.util.Locale;
import java.util.Map;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 2019-07-19
 */
@UseCase
@Getter @Setter
public class AppNavBarUC extends FhdpBaseUC implements INavbar, ISystemUseCase {
    public static final String NAVBAR_CONTAINER_ID = "navbarForm";
    private AppNavBarForm.Model model = new  AppNavBarForm.Model();
    private static final String BOOTSWATCH_THEME_URL = "https://bootswatch.com/4/%s/bootstrap.css";
    @Autowired
    private MenuService menuService;
    @Autowired
    private EventRegistry eventRegistry;
    @Value("${server.servlet.context-path:/}")
    private String contextRoot;
    @Value("${server.logout.path:logout}")
    private String logoutPath;
    @Value("${server.login.path:login}")
    private String loginPath;
    @Value("${fh.application.default.css}")
    private String defaultCss;
    @Value("${fh.application.menu.hide}")
    private boolean menuHidden;
    @Value("${fhframework.language.default:pl}")
    private String defaultLanguage;

    @Value("${fhdp.notification.timeout:20000}")
    private String notificationTimeout;

    @Value("${fhdp.theme:}")
    private String defaultTheme;
    @Value("${fhdp.alternate.theme:theme--default}")
    private String alternateTheme;

    @Value("${translation.en:true}")
    private boolean translationEN;

    @Value("${translation.pl:true}")
    private boolean translationPL;

    @Value("${translation.lt:true}")
    private boolean translationLT;

    @Value("${translation.no:false}")
    private boolean translationNO;

    @Value("${fhdp.languageDropdown:true}")
    private boolean languageDropdown;

    @Value("${fhdp.appSider:true}")
    private boolean appSider;

    @Value("${fhdp.helpSider:true}")
    private boolean helpSider;

    @Value("${fhdp.operationSider:true}")
    private boolean operationSider;

    @Value("${fhdp.sessionClock:false}")
    private boolean sessionClock;

    @Value("${fhdp.onlyContrastStyle:false}")
    private boolean isOnlyContrastStyle;

    protected boolean isAppSiderOpen;
    protected boolean isOperationSiderOpen;
    protected boolean isHelpPageSiderOpen;

    @Autowired
    private DefaultApplicationConfigurer configurer;
    @Autowired
    private FhUserUtils userUtils;

    private Map<String, String> cssUrls;

    private String currentStyle = "Default";

    public AppNavBarUC() {
    }

    public void start() {
        this.model.setTranslationPL(translationPL);
        this.model.setTranslationEN(translationEN);
        this.model.setTranslationLT(translationLT);
        this.model.setTranslationNO(translationNO);

        this.model.setHelpSider(helpSider);
        this.model.setOperationSider(operationSider);
        this.model.setLanguageDropdown(languageDropdown);
        this.model.setAppSider(appSider);
        this.model.setSessionClock(sessionClock);
        this.model.setOnlyContrastStyle(isOnlyContrastStyle);

        this.model.setLogin(this.getUserSession().getSystemUser().getLogin());
        FhUtils.setCookieByKey("operationLabels", "false");
        FhUtils.setCookieByKey("operationPending", "false");
        FhUtils.setCookieByKey("narrowStyle", "false");
        FhUtils.setCookieByKey("notificationTimeout", this.notificationTimeout);

        if (this.contextRoot.endsWith("/")) {
            this.model.setLogoutURL(this.contextRoot + this.logoutPath);
            this.model.setLoginURL(this.contextRoot + this.loginPath);
        } else {
            this.model.setLogoutURL(this.contextRoot + "/" + this.logoutPath);
            this.model.setLoginURL(this.contextRoot + "/" + this.loginPath);
        }

        this.model.setGuest(this.userUtils.userHasRole("Guest"));
        this.setLanguage(this.defaultLanguage);
//        this.openDefaultStylesheet();
        if (this.menuHidden) {
            this.hideMenu();
        }
        super.hiddenAllBars();
        model.setSelectedStyle(StyleEnum.valueOf(currentStyle));
        this.showForm( AppNavBarForm.class, this.model);
    }

//    private void openDefaultStylesheet() {
//        this.cssUrls = this.configurer.getCssUrls();
//        this.model.setCssIds(new ArrayList(this.cssUrls.keySet()));
//        this.model.setFhCss(this.model.getCssIds().remove("fh"));
//        this.model.setDefaultCss(this.model.getCssIds().remove("default"));
//        if (Objects.equals("fh", this.defaultCss)) {
//            this.openFhStylesheet();
//        } else if (Objects.equals("default", this.defaultCss)) {
//            this.closeAlternativeStylesheet();
//        } else {
//            this.openStylesheet(this.defaultCss);
//        }
//
//    }

    @Action(value = "changeNarrowStyle", validate = false)
    public void changeNarrowStyle() {
        boolean isNarrowStyle = Boolean.parseBoolean(FhUtils.getCookieByKey("narrowStyle"));
        if(isNarrowStyle) {
            FhUtils.setCookieByKey("narrowStyle", "false");
            eventRegistry.fireCustomActionEvent("replaceHtmlStyleClass", "style--compressed,style--regular");
        } else {
            FhUtils.setCookieByKey("narrowStyle", "true");
            eventRegistry.fireCustomActionEvent("replaceHtmlStyleClass", "style--regular,style--compressed");
        }
    }

    @Action(validate = false)
    public void onChangeStyle() {
        if(!model.getSelectedStyle().equals(currentStyle)) {
//            eventRegistry.fireNotificationEvent(NotificationEvent.Level.WARNING, "Prev style: " + currentStyle);
            String themeToReplace = "";
            switch (currentStyle.toLowerCase()) {
                case "default":
                    themeToReplace = defaultTheme;
                    break;
                case "alternate":
                    themeToReplace = alternateTheme;
                    break;
                case "contrast":
                    themeToReplace = "theme--dark";
                    break;
            }
            currentStyle = model.getSelectedStyle().toString().toLowerCase();
//            eventRegistry.fireNotificationEvent(NotificationEvent.Level.SUCCESS, "New style: " + currentStyle);
            String newTheme = "";
            switch (currentStyle.toLowerCase()) {
                case "default":
                    newTheme = defaultTheme;
                    break;
                case "alternate":
                    newTheme = alternateTheme;
                    break;
                case "contrast":
                    newTheme = "theme--dark";
                    break;
            }
            String toReplace = themeToReplace + "," + newTheme;
            eventRegistry.fireCustomActionEvent("replaceBodyStyleClass", toReplace);
        }
    }

    @Action(validate = false)
    public void onChangeOnlyContrastStyle() {
        String themeToReplace = "";
        String newTheme = "";

        if(this.model.isOnlyContrastStyleChecked()) {
            this.model.setOnlyContrastStyleChecked(false);
            themeToReplace = "theme--dark";
            newTheme = defaultTheme;
        } else {
            this.model.setOnlyContrastStyleChecked(true);
            themeToReplace = defaultTheme;
            newTheme = "theme--dark";
        }

        String toReplace = themeToReplace + "," + newTheme;
        eventRegistry.fireCustomActionEvent("replaceBodyStyleClass", toReplace);
    }

    @Action(value = "changeOperationLabels", validate = false)
    public void changeOperationsLabels() {
        boolean isOperationsLabels = Boolean.parseBoolean(FhUtils.getCookieByKey("operationLabels"));

        if(isOperationsLabels) {
            FhUtils.setCookieByKey("operationLabels", "false");
            this.changeOperationsPendingTab(false);
        } else {
            FhUtils.setCookieByKey("operationLabels", "true");
            this.changeOperationsPendingTab(true);
        }
    }

    private void changeOperationsPendingTab(Boolean isSeparated) {
        boolean isOperationsPending = Boolean.parseBoolean(FhUtils.getCookieByKey("operationPending"));

        if(!isOperationsPending) {
            if(isSeparated)
                eventRegistry.fireCustomActionEvent("enableTabsSeparated");
            else
                eventRegistry.fireCustomActionEvent("disableTabsSeparated");
        }
    }

//    @Action
//    public void downloadUserLog() {
//        FhLogger.info(this.getClass(), "Flushing the log buffer out", new Object[0]);
//        URL log = LogUtils.getUserLogFile(this.getUserSession());
//        this.eventRegistry.fireDownloadEvent(new UrlResource(log));
//    }

    public String getContainerId() {
        return "navbarForm";
    }

    @Action
    public void setLanguageEnglish() {
        this.setLanguage(AppNavBarForm.Language.ENGLISH.getValue());
    }

    @Action
    public void setLanguageLithuanian() {
        this.setLanguage(AppNavBarForm.Language.LITHUANIAN.getValue());
    }

    @Action
    public void setLanguagePolish() {
        this.setLanguage(AppNavBarForm.Language.POLISH.getValue());
    }

    @Action
    public void setLanguageNo() {
        this.setLanguage(AppNavBarForm.Language.NO.getValue());
    }

    private void setLanguage(String lang) {
        this.getUserSession().setLanguage(this.getLocale(lang));
        this.model.setLanguage(lang);
    }

    @Action
    public void displayAppSider() {
        if(isAppSiderOpen) {
            isAppSiderOpen = false;
        } else {
            if(isOperationSiderOpen || isHelpPageSiderOpen) {
                closeAppSider();
            }
            isAppSiderOpen = true;
            showForm(AppNavSiderHelpForm.class, model);
        }
        super.toogleAppSiderHelp();
    }

    @Action
    public void closeAppSider() {
        isOperationSiderOpen = false;
        isAppSiderOpen = false;
        isHelpPageSiderOpen = false;

        super.appSiderHelpManagement(true);
    }


    @Action
    public void displayOperationSider() {
        if(isOperationSiderOpen) {
            isOperationSiderOpen = false;
        } else {
            if(isAppSiderOpen || isHelpPageSiderOpen) {
                closeAppSider();
            }
            isOperationSiderOpen = true;
            showForm(AppNavSiderOperationForm.class, model);
        }
        super.toogleAppSiderHelp();
    }

    @Action
    public void displayHelpPageSider() {
        if(isHelpPageSiderOpen) {
            isHelpPageSiderOpen = false;
        } else {
            if(isAppSiderOpen || isOperationSiderOpen) {
                closeAppSider();
            }
            isHelpPageSiderOpen = true;
            showForm(AppNavSiderHelpPageForm.class, model);
        }
        super.toogleAppSiderHelp();
    }

    @Action
    public void logout() {
        eventRegistry.fireRedirectEvent(logoutPath, false);
    }

//    @Action
//    public void openFhStylesheet() {
//        String style = this.contextRoot + (this.contextRoot.endsWith("/") ? "" : "/") + "css/fh.css";
//        this.model.setAlternativeStylesheet(style);
//        this.eventRegistry.fireStylesheetChangeEvent(style);
//    }
//
//    @Action
//    public void openStylesheet(String id) {
//        this.model.setAlternativeStylesheet((String)this.cssUrls.get(id));
//        this.eventRegistry.fireStylesheetChangeEvent(this.model.getAlternativeStylesheet());
//    }
//
//    @Action
//    public void closeAlternativeStylesheet() {
//        this.model.setAlternativeStylesheet((String)null);
//        this.eventRegistry.fireStylesheetChangeEvent((String)null);
//    }
//
//    @Action
//    public void toggleMenu() {
//        if (this.menuService.isHidden()) {
//            this.menuService.show();
//        } else {
//            this.menuService.hide();
//        }
//
//    }

    private Locale getLocale(String languageTag) {
        return !StringUtils.isNullOrEmpty(languageTag) ? Locale.forLanguageTag(languageTag) : Locale.getDefault();
    }

    public void openMenu() {
        throw new IllegalStateException("Not implemented.");
    }

    public void closeMenu() {
        throw new IllegalStateException("Not implemented.");
    }

    public void showMenu() {
        this.eventRegistry.fireCustomActionEvent("showMenu");
    }

    public void hideMenu() {
        this.eventRegistry.fireCustomActionEvent("hideMenu");
    }

    @Override
    public void doAfterRefresh() {

    }
}
