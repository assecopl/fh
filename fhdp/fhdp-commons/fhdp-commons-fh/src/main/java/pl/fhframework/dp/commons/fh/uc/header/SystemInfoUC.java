package pl.fhframework.dp.commons.fh.uc.header;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import pl.fhframework.dp.commons.fh.uc.FhdpBaseUC;
import pl.fhframework.dp.commons.utils.net.HostNameUtil;
import pl.fhframework.ISystemUseCase;
import pl.fhframework.core.IApplicationInformation;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.event.EventRegistry;
import pl.fhframework.model.security.SystemUser;

@UseCase
@Getter
public class SystemInfoUC extends FhdpBaseUC implements ISystemUseCase {
    private static final String MENU_CONTAINER_ID = "systemInfo";

    @Autowired
    private EventRegistry eventRegistry;
    @Autowired
    private IApplicationInformation applicationInfo;

    private SystemInfoForm.Model model;
    private SystemInfoForm form;

    @Value("${fhdp.ip:false}")
    private boolean ipVisibility;


    @Override
    public void start() {
        model = new SystemInfoForm.Model();
        initModel();
        form = showForm(SystemInfoForm.class, model);
        postprocess();
    }

    protected void postprocess() {
       // System dependent.
    }

    protected void initModel() {
        SystemUser systemUser = getUserSession().getSystemUser();
        model.setUser(systemUser == null ? "[Gość]" : systemUser.getFullName());
        model.setVersion("unknown".equals(applicationInfo.getBranchName().toLowerCase())?"Trunk":applicationInfo.getBranchName());
        model.setSubversion(applicationInfo.getBuildNumber() + "; " + applicationInfo.getBuildTimestamp());

        if(ipVisibility) {
            model.setServerIP(String.format("IP: %s", HostNameUtil.host()));
        } else model.setServerIP("");

//        model.setRoles(systemUser != null && systemUser.getBusinessRoles() != null ? systemUser.getBusinessRoles().stream().map(IBusinessRole::getRoleName).collect(Collectors.joining(", ")) : null);
//        //Przechowujemy użytkownika i jednostki
//        PkwdUserDto user = null;
//        PkwdOrgUnitDto orgUnit = null;
//        if (systemUser != null) {
//            PkwdUserDtoQuery query = new PkwdUserDtoQuery();
//            query.setLogin(systemUser.getLogin());
//            query.setFirstRow(0);
//            query.setSize(10);
//            List<PkwdUserDto> list = userService.listDto(query, 0, 10);
//            if(list != null && !list.isEmpty()) {
//                user = list.get(0);
//                if(user.getBaseOrgUnitId() != null) {
//                    orgUnit = orgUnitService.getDto(user.getBaseOrgUnitId());
//                }
//            }
//        }
//        getUserSession().getAttributes().put(PkwdSessionAttributes.PKWD_USER, user);
//
//        if(orgUnit != null) {
//        getUserSession().getAttributes().put(PkwdSessionAttributes.ORG_UNIT, orgUnit);
//        model.setOrgUnitName(orgUnit.getName());
//        model.setOrgUnitCode(orgUnit.getOrgUnitCode20());
//        } else {
//            model.setOrgUnitName("-");
//            model.setOrgUnitCode("-");
//        }

    }

    @Override
    public void doAfterRefresh() {

    }

    @Override
    public String getContainerId() {
        return MENU_CONTAINER_ID;
    }

}
