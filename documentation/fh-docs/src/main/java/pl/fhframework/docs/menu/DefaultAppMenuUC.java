package pl.fhframework.docs.menu;

import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.core.i18n.MessageService;
import pl.fhframework.core.security.annotations.SystemFunction;
import pl.fhframework.core.uc.IInitialUseCase;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.instance.ConstructorUseCaseInputFactory;
import pl.fhframework.core.uc.instance.NullUseCaseInputFactory;
import pl.fhframework.core.uc.instance.OfUseCaseInputFactory;
import pl.fhframework.core.uc.url.UseCaseWithUrl;
import pl.fhframework.docs.DocsSystemFunction;
import pl.fhframework.annotations.Action;

/**
 * Created by pawel.ruta on 2018-08-09.
 */
@UseCase
@UseCaseWithUrl(alias = "docs-menu-app")
//@SystemFunction(DocsSystemFunction.FH_DOCUMENTATION_VIEW)
public class DefaultAppMenuUC implements IInitialUseCase {
    @Autowired
    private MessageService messageService;

    @Override
    public void start() {
        showForm(DefaultAppMenuForm.class,
                DefaultAppMenuModel.builder().
                        ucAttr(DefaultAppMenuModel.StaticTableData.builder().value1("ref").value2($("fh.docs.appmenu.menuxml.ucattrs3_1")).value3("").build()).
                        ucAttr(DefaultAppMenuModel.StaticTableData.builder().value1("label").value2($("fh.docs.appmenu.menuxml.ucattrs3_2")).value3("").build()).
                        ucAttr(DefaultAppMenuModel.StaticTableData.builder().value1("cloudExposed").value2($("fh.docs.appmenu.menuxml.ucattrs3_3")).value3("false").build()).
                        ucAttr(DefaultAppMenuModel.StaticTableData.builder().value1("inputFactory").value2($("fh.docs.appmenu.menuxml.ucattrs3_4")).value3(NullUseCaseInputFactory.NAME).build()).
                        build());
    }

    @Action
    public void runNull() {
        getUserSession().getUseCaseContainer().runInitialUseCase("pl.fhframework.docs.menu.RunAnyUC", NullUseCaseInputFactory.NAME);
    }

    @Action
    public void runNew() {
        getUserSession().getUseCaseContainer().runInitialUseCase("pl.fhframework.docs.menu.RunAnyUC", ConstructorUseCaseInputFactory.NAME);
    }

    @Action
    public void runOf() {
        getUserSession().getUseCaseContainer().runInitialUseCase("pl.fhframework.docs.menu.RunAnyUC", OfUseCaseInputFactory.NAME);
    }

    private String $(String key) {
        return messageService.getAllBundles().getMessage(key);
    }
}
