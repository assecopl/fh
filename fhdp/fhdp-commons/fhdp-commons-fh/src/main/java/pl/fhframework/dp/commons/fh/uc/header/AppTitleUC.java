
package pl.fhframework.dp.commons.fh.uc.header;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import pl.fhframework.dp.commons.fh.uc.FhdpBaseUC;
import pl.fhframework.ISystemUseCase;
import pl.fhframework.core.IApplicationInformation;
import pl.fhframework.core.i18n.MessageService;
import pl.fhframework.core.uc.UseCase;

@UseCase
public class AppTitleUC extends FhdpBaseUC implements ISystemUseCase {
    @Autowired
    private IApplicationInformation applicationInfo;

    @Autowired
    MessageService messageService;

    @Value("${build.environment:development}")
    private String environment;

    @Value("${fhdp.visibilityAppName:false}")
    private boolean visibilityAppName;

    @Value("${fhdp.versionVisibility:false}")
    private boolean versionVisibility;

    @Value("${fhdp.appName:true}")
    private boolean appName;

    private final String productionEnvironment = "production";

    private static final String MENU_CONTAINER_ID = "appTitle";

    private AppTitleForm.Model model;
    private AppTitleForm form;

    @Override
    public void start() {
        model = new AppTitleForm.Model();
        initModel();
        form = showForm(AppTitleForm.class, model);
    }

    private void initModel() {
        String env;

        model.setSubVersion(applicationInfo.getBuildNumber() + "; " + applicationInfo.getBuildTimestamp());

        if (visibilityAppName) {
            model.setAppName(messageService.getAllBundles().getMessage("app.title"));
        } else model.setAppName("");

        if (environment.equals(productionEnvironment)) {
            env = "";
        } else env = environment;

        if(versionVisibility) {
            model.setVersion(String.format("v %s.%s %s", applicationInfo.getBranchName(), applicationInfo.getBuildNumber(), env));
        } else {
            model.setVersion("");
        }
    }

    @Override
    public void doAfterRefresh() {

    }

    @Override
    public String getContainerId() {
        return MENU_CONTAINER_ID;
    }

}