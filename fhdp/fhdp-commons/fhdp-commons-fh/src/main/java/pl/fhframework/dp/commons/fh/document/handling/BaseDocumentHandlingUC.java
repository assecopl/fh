package pl.fhframework.dp.commons.fh.document.handling;


import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import pl.fhframework.dp.commons.fh.helper.AESCypher;
import pl.fhframework.dp.commons.fh.outline.*;

import pl.fhframework.dp.commons.fh.uc.FhdpBaseUC;
import pl.fhframework.dp.commons.fh.uc.IGenericListOutputCallback;
import pl.fhframework.dp.commons.fh.uc.header.AppSiderService;
import pl.fhframework.dp.commons.fh.uc.header.ButtonsBarService;
import pl.fhframework.dp.commons.fh.uc.header.SideBarService;
import pl.fhframework.dp.commons.fh.utils.FhUtils;
import pl.fhframework.dp.commons.utils.xml.TextUtils;
import pl.fhframework.dp.commons.validation.ValidatorProviderFhdp;

import pl.fhframework.dp.transport.dto.commons.OperationResultBaseDto;
import pl.fhframework.dp.transport.dto.commons.OperationStateResponseDto;
import pl.fhframework.dp.transport.dto.commons.OperationStepDto;
import pl.fhframework.dp.transport.dto.operations.OperationDtoQuery;
import pl.fhframework.dp.transport.enums.IDocType;
import pl.fhframework.dp.transport.enums.PerformerEnum;
import pl.fhframework.WebSocketFormsHandler;
import pl.fhframework.annotations.Action;
import pl.fhframework.core.i18n.IUseCase18nListener;
import pl.fhframework.core.i18n.MessageService;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.uc.*;
import pl.fhframework.dp.commons.fh.outline.GroupTreeElement;
import pl.fhframework.dp.commons.fh.outline.IndexedTreeElement;
import pl.fhframework.dp.commons.fh.outline.OutlineService;
import pl.fhframework.dp.commons.fh.outline.TreeElement;
import pl.fhframework.event.EventRegistry;
import pl.fhframework.event.dto.NotificationEvent;
import pl.fhframework.model.forms.Form;
import pl.fhframework.validation.IValidationResults;
import pl.fhframework.validation.ValidationPhase;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.bind.JAXBException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

@UseCase
@Getter @Setter
public  abstract class BaseDocumentHandlingUC<MODEL extends BaseDocumentHandlingFormModel, C extends IGenericListOutputCallback, OUTLINE extends BaseDocumentHandlingOutlineForm<MODEL>> extends FhdpBaseUC
        implements IUseCase<C>, IUseCase18nListener {

    @Value("${fhdp.timerTimeout:1}")
    protected int timerTimeout;

    protected static final String PENDING_OPERATION_TAB_ID = "pendingOperation";
    protected MODEL model;
    protected IDocumentHandlingForm form;
    protected OUTLINE outlineForm;

    protected IDocumentHandler documentHandler;
    //    protected Params parameters;
    protected IDocType documentType;
    protected List<String> additionalOutlineNames = new ArrayList<>();
    protected String redirectUrl;

    protected OperationStatusCheckForm.Model pendingOperationFormModel;

    protected boolean run = true;
    Thread t;

    protected final WebSocketFormsHandler formsHandler;

    protected final OutlineService outlineService;
    protected final ApplicationContext context;
    protected final EventRegistry eventRegistry;
    protected final SideBarService sideBarService;
    protected final AppSiderService appSiderService;

    protected final ButtonsBarService buttonsBarService;

    protected final MessageService messageService;
    protected final ValidationPhase validationPhase;
    protected final ValidatorProviderFhdp validatorProviderFhdp;

    public BaseDocumentHandlingUC(WebSocketFormsHandler formsHandler,
                                  OutlineService outlineService,
                                  ApplicationContext context,
                                  EventRegistry eventRegistry,
                                  SideBarService sideBarService,
                                  AppSiderService appSiderService,
                                  ButtonsBarService buttonsBarService,
                                  MessageService messageService,
                                  ValidationPhase validationPhase,
                                  ValidatorProviderFhdp validatorProviderFhdp) {
        this.formsHandler = formsHandler;
        this.outlineService = outlineService;
        this.context = context;
        this.eventRegistry = eventRegistry;
        this.sideBarService = sideBarService;
        this.appSiderService = appSiderService;
        this.buttonsBarService = buttonsBarService;
        this.messageService = messageService;
        this.validationPhase = validationPhase;
        this.validatorProviderFhdp = validatorProviderFhdp;
    }

    protected void commonStart() {
        try {

            //Panels. TODO:set state of all panels
            super.documentBars(false);
            super.searchButtonsManagement(true);
            //outline

            DocumentHandlingMappingHelper dhmh = new DocumentHandlingMappingHelper(model.getSpecificDocFormModel());
            model.setSearchMap(dhmh.flatMap());

//            form = showForm(DeclarationHandlingForm.class, model, model.getSpecificDeclarationFormModel().getVariant());
//            outlineForm = showForm(DeclarationHandlingOutlineForm.class, model, model.getSpecificDeclarationFormModel().getVariant());

            form = showMainForm();
            outlineForm = showOutlineForm();
            initLeftMenu();
            initDocumentHandlingForm();
            initOperationPendingState();
        } catch (BeansException e) {
            FhLogger.error("{}{}", e.getMessage(), e);
        }
    }

    protected void addAdditionalOutlineName(String name){
        this.additionalOutlineNames.add(name);
    }

    protected abstract OUTLINE showOutlineForm();

    protected abstract IDocumentHandlingForm showMainForm();

    protected abstract MODEL newModel();

    protected void initOperationPendingState() {
        boolean operationPending = isOperationPending();
        if(operationPending) {
            FhUtils.setCookieByKey("operationPending", "true");
            waitForOperationFinish();
        } else {
            FhUtils.setCookieByKey("operationPending", "false");
            DynamicTab tab = form.getDynamicTab(PENDING_OPERATION_TAB_ID);
            if(tab != null) {
                form.removeDynamicTab(tab);
            }
            model.setActiveTabIndex(0);
            model.setTimerTimeout(0);
            documentHandler.initToolbar();
        }
    }

    protected abstract boolean isOperationPending();


    protected void waitForOperationFinish() {
        documentHandler.initOperationPendingToolbar();
        //Add dynamic tab
        int index = -1;
        DynamicTab tab = form.getDynamicTab(PENDING_OPERATION_TAB_ID);
        if(tab != null) {
            index = form.indexOfTab(tab);
        }
        if(index <= 0) {
            tab = new DynamicTab();
            tab.setTabId(PENDING_OPERATION_TAB_ID);
            tab.setLabel(messageService.getAllBundles().getMessage("document.ct.tabs.pendingOperation"));
            tab.setFormReference("pl.fhframework.dp.commons.fh.document.handling.OperationStatusCheckForm");
            pendingOperationFormModel = new OperationStatusCheckForm.Model();
            pendingOperationFormModel.setOperationGUID(getOperationGUID());
            pendingOperationFormModel.setDocumentHandler(documentHandler);
            pendingOperationFormModel.setInternal(true);
            tab.setModel(pendingOperationFormModel);
            form.addDynamicTab(tab);
            model.setActiveTabIndex(form.indexOfTab(tab));
            //Find operation handler
            String operationName = getOperationCode() + AbstractDocumentHandler.OP_BEAN_SUFFIX;
            operationName = TextUtils.decapitateFirstLetter(operationName);
            BaseOperationHandler operationHandler = getContext().getBean(operationName, BaseOperationHandler.class);
            documentHandler.setOperationHandler(operationHandler);
            model.setTimerTimeout(this.timerTimeout);
            model.setActiveTabIndex(index);
        } else {
            model.setTimerTimeout(this.timerTimeout);
            model.setActiveTabIndex(form.indexOfTab(tab));
        }
    }

    /**
     * Returns code of the current operation
     * @return
     */
    protected abstract String getOperationCode();

    /**
     * Returns GUID of current operation
     * @return
     */
    protected abstract String getOperationGUID();

    @Action(validate = false)
    protected void checkTask() {
        FhLogger.debug("Calling service...");
        OperationStateResponseDto state = documentHandler.checkOperationState(pendingOperationFormModel.getOperationGUID());
        List<OperationStepDto> steps = state.getSteps();
        Collections.sort(steps);
        pendingOperationFormModel.getOperationStateResponse().setSteps(steps);
        pendingOperationFormModel.getOperationStateResponse().setFinished(state.isFinished());
        if(state.isFinished()) {
            refreshView();
        }
    }

    protected void initLeftMenu(){
        try {
            List<ElementCT> elementCTS = outlineService.generateOutline(getDocumentType().getTypeName());
            List<TreeElement<ElementCT>> leftMenu = outlineService.buildLeftMenu(elementCTS);
            model.setDocLeftMenu(leftMenu);
            documentHandler.initLeftMenu(leftMenu);
        } catch (JAXBException e) {
            FhLogger.error("{}{}", e.getMessage(), e);
        }
    }

    protected void selectFirstMessage() {
    }

    @Action(value = "onTabChangeMessages", validate = false)
    public void onTabChangeMessages(int messagesTabIndex) {
        super.buttonsFormManagement(messagesTabIndex != 0);
    }


    public String getLeftMenuId() {
        return "leftMenuTreeRoot";
    }

    public String getLeftMenuMessageId() {
        return "messagesLeftMenuTreeRoot";
    }

    public String getLeftMenuMessageGroupId() {
        return "teOutline";
    }

    public void refreshView(String info) {
        super.searchButtonsManagement(true);
        Long id = getModel().getDocId();
        //Entity
        documentHandler.refreshEntity(id);
        //History
        getHistory(id);
        //Variant
        getModel().getSpecificDocFormModel().setVariant(documentHandler.resolveVariant(model.getSpecificDocFormModel()));

        initDocumentHandlingForm();
        initOperationPendingState();
        initLeftMenu();
        setOperationPagedModel();
        eventRegistry.fireNotificationEvent(NotificationEvent.Level.INFO, info);
    }

    protected abstract void setOperationPagedModel();

    protected abstract void getHistory(Long id);


    public void refreshView() {
        // TO DO: language handling
        // the below key should be used
        // message.operation.result
        refreshView(messageService.getAllBundles().getMessage("fhdp.document.ct.actions.refresh.manual"));
    }

    @Action(validate = false)
    public void refreshManual() {
        refreshView(messageService.getAllBundles().getMessage("fhdp.document.ct.actions.refresh.manual"));
    }

    @Action(validate = false)
    public void openPinup() throws InvalidAlgorithmParameterException, NoSuchPaddingException,
            IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeySpecException,
            BadPaddingException, InvalidKeyException, UnsupportedEncodingException {
        String id = AESCypher.encrypt(model.getPinupCypherPassword(), model.getDocId().toString());
        String url = model.getPinupUrl()
            + "?id=" + id
            + "&lng=" + getUserSession().getLanguage().toLanguageTag();
        eventRegistry.fireCustomActionEvent("openPinup", url);
    }

    protected void initDocumentHandlingForm() {
        form.initMainTabContainer();
        if (!model.getDocLeftMenu().isEmpty()) {
            initRightPanel();
        }
    }

    protected abstract void initRightPanel();

    @Action(value = "onTabChange", validate = false)
    public void onTabChange() {
//        //todo - trzeba odświeżyć dane???
//        //model.getData().refreshNeeded();
//        DeclarationHandlingForm.DynamicTab dt = form.getActiveDynamicTab(model.get);
//        if (dt != null) {
//            dt.getModel().getTabChangeObservable().notifyObservers();
//        }
    }

    public void openTreeBranch(String treeId, String elementId) {
        String input = String.format("{\"parentId\": \"%s\", \"nestionId\": \"%s\"}", treeId, elementId);
        eventRegistry.fireCustomActionEvent("openTreeBranch", input);
    }

    @Action(value = "onLeftPanelClick", validate = false)
    public void onLeftPanelClick(TreeElement<ElementCT> element) {
        if(element == null) return;
        if (element instanceof GroupTreeElement) {
            GroupTreeElement<ElementCT> groupElement = (GroupTreeElement<ElementCT>) element;
            documentHandler.setSelectedGroup(groupElement.getStart(), groupElement.getEnd(), groupElement.getGroupedObjectName());
            form.initDeclRightPanel(model.getSpecificDocFormModel(), element.getObj().getForm(), documentHandler.resolveVariant(model.getSpecificDocFormModel()));
            return;
        }
        if (element instanceof IndexedTreeElement) {
            documentHandler.setSelectedObject(((IndexedTreeElement<ElementCT>) element).getIndex(), ((IndexedTreeElement<ElementCT>) element).getIndexedObjectName());
        } else {
            documentHandler.resetSelectedGroup();
        }
        form.initDeclRightPanel(model.getSpecificDocFormModel(), element.getObj().getForm(), documentHandler.resolveVariant(model.getSpecificDocFormModel()));
    }


    @Action(value = "cancel", validate = false)
    public void onCancel() {
        if ("History".equals(model.getSpecificDocFormModel().getVariant()) || "Messages".equals(model.getSpecificDocFormModel().getVariant())) { //todo enum, porządek z wariantami
            model.getSpecificDocFormModel().setVariant("View");
            super.appSiderHelpManagement(true);
            super.searchButtonsManagement(true);
            documentHandler.prepareActionLocal("buttonsManagement");
        } else {
            if(t != null && t.isAlive()) {
                run = false;
                t.interrupt();
            }
            if(redirectUrl == null) {
                exit().cancel();
            } else {
                OperationResultBaseDto result = new OperationResultBaseDto();
                result.setOperationID(getDocumentType().getTypeName());
                exit().save(result);
            }
        }
    }

    @Action(value = "validate", validate = false)
    public void validateManual() {
        validate(LocalValidationModeEnum.document);
    }

    @Action(value = "save", validate = false)
    public void onSave() {
        saveWithoutValidation();
    }

    public abstract Long save();

    public abstract void saveWithoutValidation();

    @Action(validate = false) // todo validate true
    public void saveAfterControl(){
        if(validate(LocalValidationModeEnum.form)) {
            OperationResultBaseDto result = new OperationResultBaseDto();
            exit().save(result);
        }
    }

    /**
     * Method for local validation. It is used to validate data before issuing performOperation.
     * @param mode Validation can be performed on current form (e.g. directive),
     *             on document object (JSR) or both.
     *
     * @return true - OK.
     */
    @SneakyThrows
    protected boolean validate(LocalValidationModeEnum mode) {
        getUserSession().getValidationResults().clearValidationErrors();
        if(mode.equals(LocalValidationModeEnum.all) || mode.equals(LocalValidationModeEnum.form)) {
            validationPhase.validateModel((Form) form, getUserSession().getValidationResults());
            IValidationResults results = getUserSession().getValidationResults();
            FhLogger.info("ValidationResults: {}", results.getValidationErrors().size());
        }
        validateExt(mode);
        return !(getUserSession().getValidationResults().hasAtLeastErrors() || documentHasErrors());
    }

    protected abstract void validateExt(LocalValidationModeEnum mode);


    protected boolean documentHasErrors() {
        return false;
    }

    @Action(value = "action", validate = false)
    public void action(String actionName, boolean validate) {
        documentHandler.runAction(actionName, validate);
    }

    @Action(value = "actionLocal", validate = false)
    public void actionLocal(String actionName) {
        getModel().getSpecificDocFormModel().setCurrentLocalAction(actionName);
        if(documentHandler.prepareActionLocal(actionName)) {
            getModel().getSpecificDocFormModel().setVariant("Edit");
            initDocumentHandlingForm();
        }
    }

    @Action(value = "performItemAction", validate = false)
    public void performItemAction(String actionName, Object docFormModel) {
        documentHandler.performItemAction(actionName, docFormModel);
    }

    @Action(value = "performItemActionWithParm1", validate = false)
    public void performItemAction(String actionName, Object docFormModel, Object parm1) {
        documentHandler.performItemAction(actionName, docFormModel, parm1);
    }

    @Action(value = "performItemActionWithParams", validate = false)
    public void performItemActionWithParams(String actionName, Object docFormModel, Object param1, Object param2) {
        documentHandler.performItemActionWithParams(actionName, docFormModel, param1, param2);
    }

    @Action(value = "performItemActionParent", validate = false)
    public void performItemActionParent(String actionName, Object docFormModel, Object docFormModelParent) {
        documentHandler.performItemAction(actionName, docFormModel, null, docFormModelParent);
    }

    @Action(validate = false)
    public void showHistory(){
        showForm("pl.fhframework.dp.commons.fh.document.handling.DocumentHandlingSearchButtons", model);
        super.searchButtonsManagement(false);

        model.setOperationQuery(new OperationDtoQuery());
        setOperationPagedModel();
        model.getSpecificDocFormModel().setVariant("History");
        if(model.getSpecificDocFormModel().getCurrentLocalAction() != null) {
            super.buttonsFormManagement(true);
        }
    }

//    protected abstract OperationPagedTableSource setOperationPagedTableSource();

    @Action("clearHistoryQuery")
    public void clearHistoryQuery() {
        model.setOperationQuery(new OperationDtoQuery());
        setOperationPagedModel();
    }

    @Action("filterOperations")
    public void filterOperations() {
        setOperationFilteredPagedModel();
    }

    protected abstract void setOperationFilteredPagedModel();

//    protected abstract PageModel<OperationCT> getFilteredPagedModel();

    @Action("clearPerformer")
    public void clearPerformer() {
        if(!getModel().getOperationQuery().getPerformerType().equals(PerformerEnum.USER)) {
            getModel().getOperationQuery().setPerformer("");
        }
    }

    @Action(validate = false)
    public void showMessages(){
        model.getSpecificDocFormModel().setVariant("Messages");
        selectFirstMessage();
    }



    @Action (validate = false)
    public void showOperationDetails() {
//        showForm(OperationDetailsForm.class, model);
//        super.appSiderDetailsManagement(false);
        performShowOperationDetails();
    }

    protected abstract void performShowOperationDetails();


    @Action(validate = false)
    public void closeOperationDetails() {
        super.appSiderDetailsManagement(true);
    }

    @Override
    public void onSessionLanguageChange() {
        if(getActiveForm() != null) {
            getActiveForm().onSessionLanguageChange(getUserSession().getLanguage().toLanguageTag());
            getActiveForm().refreshView();
        }
        initLeftMenu();
    }

    public void afterPerformOperation(OperationResultBaseDto result) {
        if(result.isOk()) {
            if(result.getOperationID() != null) {
                OperationStatusCheckForm.Model resultModel = new OperationStatusCheckForm.Model();
                model.setProcessId(result.getProcessID());
                resultModel.setOperationGUID(result.getOperationID());
                resultModel.setDocumentHandler(documentHandler);
                OperationStateResponseDto state = documentHandler.checkOperationState(result.getOperationID());
                if(!state.isFinished()) {
                    runUseCase(OperationStatusCheckUC.class, resultModel, new IUseCaseSaveCancelCallback<OperationStatusCheckForm.Model>() {
                        @Override
                        public void save(OperationStatusCheckForm.Model one) {
                            refreshView();
                        }

                        @Override
                        public void cancel() {
                            refreshView();
                        }
                    });
                }
            } else {
                refreshView();
            }
        } else {
//            showErrors(result);
        }
    }



    @Action(validate = false)
    public void searchTree() {
        Map<String, String> searchMap = getModel().getSearchMap();
        String searchParam = getModel().getSearchParam();
        List<String> pointers = new ArrayList<String>();

        List<String> resultList = new ArrayList<>();

        if (searchParam.length() >= 3) {
            searchMap.forEach((key, value) -> {
                if (value.toLowerCase().contains(searchParam.toLowerCase())) {
                    resultList.add(key);
                }
            });

            OutlineService.OutlineMapping mappings = outlineService.findMappings(getDocumentType().getTypeName());
            for (String pointer : resultList) {
                ElementCT el = outlineService.findElementFromPointer(pointer, mappings);
                if (el != null && el.getId() != null) {
                    pointers.add(el.getId());
                }
            }

            getModel().setSearchPointers(pointers);
        }
    }
}
