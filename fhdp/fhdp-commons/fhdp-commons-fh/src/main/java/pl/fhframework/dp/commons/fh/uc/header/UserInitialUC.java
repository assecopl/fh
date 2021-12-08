package pl.fhframework.dp.commons.fh.uc.header;

import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.dp.commons.fh.uc.FhdpBaseUC;
import pl.fhframework.ISystemUseCase;
import pl.fhframework.annotations.Action;
import pl.fhframework.core.IApplicationInformation;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.model.security.SystemUser;

@UseCase
public class UserInitialUC extends FhdpBaseUC implements ISystemUseCase {
    private static final String MENU_CONTAINER_ID = "userInitial";

    @Autowired
    private IApplicationInformation applicationInfo;

    private UserInitialForm.Model model;
    private UserInitialForm form;


    @Override
    public void start() {
        model = new UserInitialForm.Model();
        initModel();
        form = showForm(UserInitialForm.class, model);
    }

    @Override
    public void doAfterRefresh() {

    }


    private void initModel() {
        SystemUser systemUser = getUserSession().getSystemUser();
        model.setUserInitial(systemUser.getName().substring(0,1).concat(systemUser.getSurname().substring(0,1)));
    }

    @Override
    public String getContainerId() {
        return MENU_CONTAINER_ID;
    }

    @Action
    public void showUserRole() {
        showForm(AppNavSiderUserRole.class, model);
        super.toogleAppSiderHelp();
    }

    @Action
    public void closeAppSider() {
        super.appSiderHelpManagement(true);
    }

}