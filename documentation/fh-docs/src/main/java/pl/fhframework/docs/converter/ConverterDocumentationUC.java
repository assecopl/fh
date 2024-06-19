package pl.fhframework.docs.converter;

import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.core.security.annotations.SystemFunction;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.IInitialUseCase;
import pl.fhframework.core.uc.url.UseCaseWithUrl;
import pl.fhframework.docs.DocsSystemFunction;
import pl.fhframework.docs.converter.model.ConverterDocumentationModel;
import pl.fhframework.docs.converter.model.ForeignUser;
import pl.fhframework.docs.converter.model.User;
import pl.fhframework.docs.converter.service.UserService;

import java.util.List;

/**
 * Created by Amadeusz Szkiladz on 12.12.2016.
 */
@UseCase
@UseCaseWithUrl(alias = "docs-type-convertion")
//@SystemFunction(DocsSystemFunction.FH_DOCUMENTATION_VIEW)
public class ConverterDocumentationUC implements IInitialUseCase {
    private ConverterDocumentationForm converterDocumentationForm;

    @Autowired
    private UserService userService;

    private ConverterDocumentationModel converterDocumentationModel = new ConverterDocumentationModel();

    @Override
    public void start() {
        List<User> allUsers = userService.findAll();
        List<ForeignUser> allForeignUsers = userService.findAllForeignUsers();
        converterDocumentationModel.setUsers(allUsers);
        converterDocumentationModel.setSelectedUser(allUsers.get(0));
        converterDocumentationModel.setForeignUsers(allForeignUsers);
        converterDocumentationModel.setSelectedForeignUser(allForeignUsers.get(0));
        converterDocumentationForm = showForm(ConverterDocumentationForm.class, converterDocumentationModel, "b");
    }
}
