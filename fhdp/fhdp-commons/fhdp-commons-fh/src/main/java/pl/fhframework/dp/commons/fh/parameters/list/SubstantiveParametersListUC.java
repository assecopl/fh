package pl.fhframework.dp.commons.fh.parameters.list;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import pl.fhframework.dp.commons.fh.document.list.searchtemplate.ISearchTemplateCriteriaProvider;
import pl.fhframework.dp.commons.fh.document.list.searchtemplate.SearchTemplateBuilderModel;
import pl.fhframework.dp.commons.fh.parameters.details.SubstantiveParametersDetailEditForm;
import pl.fhframework.dp.commons.fh.parameters.details.SubstantiveParametersDetailEditFormUC;
import pl.fhframework.dp.commons.fh.parameters.details.SubstantiveParametersDetailForm;
import pl.fhframework.dp.commons.fh.services.SubstantiveParametersService;
import pl.fhframework.dp.commons.fh.uc.GenericListUC;
import pl.fhframework.dp.transport.dto.parameters.SubstantiveParametersDto;
import pl.fhframework.dp.transport.dto.parameters.SubstantiveParametersDtoQuery;
import pl.fhframework.annotations.Action;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.uc.IUseCaseSaveCancelCallback;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.event.EventRegistry;
import pl.fhframework.model.forms.messages.Messages;

@Getter
@Setter
@UseCase
public class SubstantiveParametersListUC extends GenericListUC<SubstantiveParametersListModel, SubstantiveParametersListUC.Params, SubstantiveParametersDto> {

    @Autowired
    ApplicationContext context;

    @Autowired
    private EventRegistry eventRegistry;

    @Autowired
    private SubstantiveParametersService substantiveParametersService;

    @Value("${fhdp.parameters.search.box.buttons:false}")
    private boolean isSearchBoxButtons;

    private SubstantiveParametersListModel substantiveParametersListModel;

    private SearchTemplateBuilderModel searchTemplateBuilderModel;

    @Autowired
    private ISearchTemplateCriteriaProvider<SubstantiveParametersDto> searchCriteriaProvider;

    @Value("${fhdp.app.context:fhdp}")
    private String appContext;

    @Override
    public void start() {
        super.documentBars(false);
        super.start();

        showForm(getSearchFormId(), getListData());
        showForm(getSearchButtonsFormId(), getListData());
//        this.dynamicPaginationRow();
        clearQuery();
    }

    @Override
    protected void readData() {
        try {
            SubstantiveParametersDtoQuery query = substantiveParametersListModel.getSearchModel().getQuery();
            getListData().setList(substantiveParametersService.listDtoPaged(query));
            getListData().setSearchTemplateBuilderModel(substantiveParametersListModel.getSearchTemplateBuilderModel());
        } catch (Exception e) {
            e.printStackTrace();
            FhLogger.error(e);
            Messages.showErrorMessage(getUserSession(), "Błąd pobrania danych", e);
        }
    }

    @Override
    protected String getListFormId() {
        return "pl.fhframework.dp.commons.fh.parameters.list.SubstantiveParametersListForm";
    }

    protected String getSearchFormId() {
        return "pl.fhframework.dp.commons.fh.parameters.list.SubstantiveParametersCTListSearch";
    }

    protected String getSearchButtonsFormId() {
        return "pl.fhframework.dp.commons.fh.common.SearchButtons";
    }

    @Override
    protected SubstantiveParametersListModel initInternalListData() {
        substantiveParametersListModel = new SubstantiveParametersListModel(appContext);
        SubstantiveParametersDtoQuery query = new SubstantiveParametersDtoQuery();
        query.setSortProperty("id");
        substantiveParametersListModel.setQuery(query);
        substantiveParametersListModel.getSearchModel().setQuery(query);
        searchTemplateBuilderModel = prepareSearchCriteriaBuilderModel();
        substantiveParametersListModel.setSearchTemplateBuilderModel(searchTemplateBuilderModel);
        substantiveParametersListModel.setSearchBoxButtons(isSearchBoxButtons);
        return substantiveParametersListModel;
    }

    @Override
    protected Params getParamsForNew() {
        return null;
    }

    @Override
    protected Params getParamsForEdit() {
        return null;
    }

    @Override
    protected Class getEditFormClass() {
        return null;
    }

    private void closeDetailForm() {
        if(!super.isHiddenAppSiderDetails()) {
            hideForm(showForm(SubstantiveParametersDetailForm.class, substantiveParametersListModel));
        }
        super.appSiderDetailsManagement(true);
    }

    @Override
    @Action("search")
    public void search() {
        closeDetailForm();
        super.search();
    }

    @Override
    @Action(validate = false)
    public void clearQuery() {
        getListData().setQuery(new SubstantiveParametersDtoQuery());
        substantiveParametersListModel.setQuery(getListData().getQuery());
        substantiveParametersListModel.getSearchModel().setQuery(getListData().getQuery());
    }

    @Action
    public void onClickGlobalSearchBoxButton() {
        this.unSelectSearchBoxButton("allSearchBoxButton", "officeSearchBoxButton");
        this.selectSearchBoxButton("globalSearchBoxButton");
    }

    @Action
    public void onClickAllSearchBoxButton() {
        this.unSelectSearchBoxButton("globalSearchBoxButton", "officeSearchBoxButton");
        this.selectSearchBoxButton("allSearchBoxButton");
    }

    @Action
    public void onClickOfficeSearchBoxButton() {
        this.unSelectSearchBoxButton("allSearchBoxButton", "globalSearchBoxButton");
        this.selectSearchBoxButton("officeSearchBoxButton");
    }

    private void unSelectSearchBoxButton(String button1, String button2) {
        this.unSelectSearchBoxButton(button1);
        this.unSelectSearchBoxButton(button2);
    }

    private void unSelectSearchBoxButton(String data) {
        this.eventRegistry.fireCustomActionEvent("unSelectSearchBoxButton", data);
    }

    private void selectSearchBoxButton(String data) {
        this.eventRegistry.fireCustomActionEvent("selectSearchBoxButton", data);
    }

    protected SearchTemplateBuilderModel prepareSearchCriteriaBuilderModel() {
        SearchTemplateBuilderModel searchModel = new SearchTemplateBuilderModel(searchCriteriaProvider);
        searchModel.setComponentName("SubstantiveParametersDto");
        searchModel.prepareEmptyFormModel();
        return searchModel;
    }

    @Action
    public void showParameterDetails() {
        showForm(SubstantiveParametersDetailForm.class, substantiveParametersListModel);
        super.appSiderDetailsManagement(false);
    }

    public void appSiderDetails(boolean hidden) {
        super.appSiderDetailsManagement(hidden);
    }

    @Action
    public void edit() throws CloneNotSupportedException {
        SubstantiveParametersDetailEditForm.Model model = new SubstantiveParametersDetailEditForm.Model();

        if (substantiveParametersListModel.getSelectedSubstantiveParametersDto() == null) {
            substantiveParametersListModel.setSelectedSubstantiveParametersDto(substantiveParametersListModel.getLastStoredSubstantiveParametersDto());
        }

        //The copy is required because of changes are transferred even in case of cancellation the operation
        final SubstantiveParametersDto cloneOfDto = substantiveParametersListModel.getSelectedSubstantiveParametersDto().clone();

        model.setDto(cloneOfDto);

        runUseCase(SubstantiveParametersDetailEditFormUC.class, model, new IUseCaseSaveCancelCallback<SubstantiveParametersDetailEditForm.Model>() {
            @SneakyThrows
            @Override
            public void save(SubstantiveParametersDetailEditForm.Model one) {
                substantiveParametersService.persistDto(one.getDto());
                final SubstantiveParametersDto modifiedDto = one.getDto().clone();
                SubstantiveParametersListUC.super.init();
                appSiderDetails(true);
                // required to handle not hiding the details form
                substantiveParametersListModel.setSelectedSubstantiveParametersDto(modifiedDto);
                substantiveParametersListModel.setLastStoredSubstantiveParametersDto(modifiedDto);
                showParameterDetails();
            }

            @Override
            public void cancel() {
            }
        });
    }

    @Action(validate = false)
    public void cancel() {
        closeDetailForm();
    }

    //    @AllArgsConstructor
    @Setter
    @Getter
    public static class Params {
        //TODO: być może w przyszłosci będzie przydatne - jeżeli niepotrzebne - można z tego zrezygnować
        public Params() {
        }
    }
}
