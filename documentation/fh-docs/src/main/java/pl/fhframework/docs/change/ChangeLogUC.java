package pl.fhframework.docs.change;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.client.HttpClientErrorException;

import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.security.annotations.SystemFunction;
import pl.fhframework.core.uc.IInitialUseCase;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.url.UseCaseWithUrl;
import pl.fhframework.docs.DocsSystemFunction;
import pl.fhframework.docs.change.form.ChangeLogForm;
import pl.fhframework.docs.change.form.ChangeLogModel;
import pl.fhframework.docs.change.model.Change.Type;
import pl.fhframework.docs.change.service.ChangeService;
import pl.fhframework.docs.change.service.JiraSyncService;
import pl.fhframework.annotations.Action;
import pl.fhframework.model.forms.Component;
import pl.fhframework.model.forms.TablePaged;
import pl.fhframework.model.forms.messages.Messages;
import pl.fhframework.events.ViewEvent;

/**
 * Created by Adam Zareba on 08.02.2017.
 */
@UseCase
@UseCaseWithUrl(alias = "docs-change-log")
//@SystemFunction(DocsSystemFunction.FH_DOCUMENTATION_VIEW)
public class ChangeLogUC implements IInitialUseCase {

    private ChangeLogModel model;

    @Autowired
    private ChangeService changeService;

    @Autowired
    private JiraSyncService jiraSyncService;

    @Override
    public void start() {
        model = new ChangeLogModel(changeService);
        showForm(ChangeLogForm.class, model);
    }

    @Action
    public void onImprovementsPageChange(ViewEvent viewEvent) {
        Component formElement = viewEvent.getSourceObject();
        if (formElement instanceof TablePaged) {
            TablePaged table = (TablePaged) formElement;
            PageRequest pageable = table.getPageable();
            if (pageable != null) {
                model.setAllImprovements(changeService.findAllBy(Type.IMPROVEMENT, pageable));
            }
        }
    }

    @Action
    public void onBugFixesPageChange(ViewEvent viewEvent) {
        Component formElement = viewEvent.getSourceObject();
        if (formElement instanceof TablePaged) {
            TablePaged table = (TablePaged) formElement;
            PageRequest pageable = table.getPageable();
            if (pageable != null) {
                model.setAllBugFixes(changeService.findAllBy(Type.BUG, pageable));
            }
        }
    }

    @Action
    public void onUpdateButtonClick() {
        try {
            jiraSyncService.synchronizeJira();
        } catch (HttpClientErrorException ex) {
            FhLogger.error(ex.getMessage());
            Messages.showMessage(this.getUserSession(), "JIRA Synchronization Exception", "Can't synchronize with JIRA, try later or contact FH team.", Messages.Severity.ERROR);
        }
    }
}
