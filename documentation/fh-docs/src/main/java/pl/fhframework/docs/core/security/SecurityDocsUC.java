package pl.fhframework.docs.core.security;

import org.springframework.beans.factory.annotation.Autowired;

import pl.fhframework.core.security.AuthorizationManager;
import pl.fhframework.core.security.model.IBusinessRole;
import pl.fhframework.core.security.annotations.SystemFunction;
import pl.fhframework.core.uc.IInitialUseCase;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.url.UseCaseWithUrl;
import pl.fhframework.docs.DocsSystemFunction;
import pl.fhframework.docs.i18n.Language;
import pl.fhframework.annotations.Action;
import pl.fhframework.model.forms.messages.Messages;

import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@UseCase
@UseCaseWithUrl(alias = "docs-security")
//@SystemFunction(DocsSystemFunction.FH_DOCUMENTATION_VIEW)
public class SecurityDocsUC implements IInitialUseCase {

    private static final String MODULE_UUID = "0d4e165b-16e0-4da3-b6e3-7ef42987da1a";

    private SecurityDocsModel model;

    @Autowired(required = false)
    private AuthorizationManager authorizationManager;

    @Override
    public void start() {
        model = new SecurityDocsModel();
        showForm(SecurityDocs.class, model);
    }

    @Action
    void hiddenAction() {
        if(getUserSession().hasAccessTo(DocsSystemFunction.FUN_HIDDEN_MESSAGE.getName(), MODULE_UUID)) {
            Messages.showInfoMessage(getUserSession(), "Somehow you have access");
        } else {
            Messages.showInfoMessage(getUserSession(), "You don't have access");
        }
    }

    @SystemFunction("constrainTest")
    @Action
    void constrainAction() {
        if(getUserSession().hasAccessTo(DocsSystemFunction.FUN_CONSTRAIN_TEST.getName(), MODULE_UUID)) {
            Messages.showInfoMessage(getUserSession(), "Somehow you have access");
        } else {
            Messages.showInfoMessage(getUserSession(), "You don't have access");
        }
    }

    @Action
    void changeLanguage() {
        final Locale plLang = Locale.forLanguageTag(Language.PL.getValue());
        final Locale engLang = Locale.forLanguageTag(Language.ENG.getValue());
        if(plLang.equals(getUserSession().getLanguage())) {
            getUserSession().setLanguage(engLang);
        } else {
            getUserSession().setLanguage(plLang);
        }
    }

    @Action
    void clearCache() {
        if(authorizationManager != null) {
            authorizationManager.clearPermissions(getUserSession().getSystemUser().getBusinessRoles());
            final Set<String> roleName = getUserSession().getSystemUser().getBusinessRoles().stream()
                    .map(IBusinessRole::getRoleName)
                    .collect(Collectors.toSet());
            Messages.showInfoMessage(getUserSession(), String.format("Cached clear for: %s", String.join(", ", roleName)));
        } else {
            Messages.showInfoMessage(getUserSession(), "Provide authorization manager implementation");
        }
    }

}
