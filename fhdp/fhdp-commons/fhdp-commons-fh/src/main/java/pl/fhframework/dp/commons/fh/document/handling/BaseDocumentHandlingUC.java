package pl.fhframework.dp.commons.fh.document.handling;


import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
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

import javax.xml.bind.JAXBException;
import java.util.*;

@UseCase
@Getter @Setter
public  abstract class BaseDocumentHandlingUC<MODEL extends BaseDocumentHandlingFormModel, C extends IGenericListOutputCallback<OperationResultBaseDto>, OUTLINE extends BaseDocumentHandlingOutlineForm<MODEL>> extends FhdpBaseUC
        implements IUseCase<C>, IUseCase18nListener {

//TODO: move to successors
//    @Value("${fhdp.validationResult:false}")
//    protected boolean validationResult;
//
//    @Value("${fhdp.declarationAlert:false}")
//    protected boolean declarationAlert;

    @Value("${fhdp.timerTimeout:1000}")
    protected int timerTimeout;

    //TODO: move to descendants
//    public static final String HANDLER_BEAN_PREFIX = "declarationHandler";
    protected static final String PENDING_OPERATION_TAB_ID = "pendingOperation";
    //TODO: move to successors
//    protected static final String VALIDATION_RESULT_TAB_ID = "validationResult";
//    protected static final String DECLARATION_ALERT_TAB_ID = "declarationAlert";
    //TODO: move to successors
//    protected ValidationResultForm.Model validationResultFormModel;
//    protected DeclarationAlertForm.Model declarationAlertFormModel;
    protected MODEL model;
    protected IDocumentHandlingForm form;
    protected OUTLINE outlineForm;

    protected IDocumentHandler documentHandler;
    //    protected Params parameters;
    protected IDocType documentType;
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
    //TODO: move to successors
//    protected final DeclarationAlertService alertDtoService;
    protected final ButtonsBarService buttonsBarService;
    //TODO: move to successors
//    protected final DeclarationMessageService declarationMessageService;
    protected final MessageService messageService;
    protected final ValidationPhase validationPhase;
    protected final ValidatorProviderFhdp validatorProviderFhdp;

    public BaseDocumentHandlingUC(WebSocketFormsHandler formsHandler,
                                  OutlineService outlineService,
                                  ApplicationContext context,
                                  EventRegistry eventRegistry,
//                                     DeclarationAlertService alertDtoService,
                                  SideBarService sideBarService,
                                  AppSiderService appSiderService,
                                  ButtonsBarService buttonsBarService,
//                                     DeclarationMessageService declarationMessageService,
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
//        this.declarationMessageService = declarationMessageService;
        this.messageService = messageService;
        this.validationPhase = validationPhase;
        this.validatorProviderFhdp = validatorProviderFhdp;
//        this.alertDtoService = alertDtoService;
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
            //TODO: move to successors
//            initDeclarationMessages();
            initLeftMenu();
            initDocumentHandlingForm();
            initOperationPendingState();
            //TODO: move to successors
//            initValidationResult();
//            initDeclarationAlert();
        } catch (BeansException e) {
            FhLogger.error("{}{}", e.getMessage(), e);
        }
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

//TODO: move to successors!!
//    protected void initDeclarationAlert() {
//        if(declarationAlert) {
//            int index = -1;
//            DynamicTab tab = form.getDynamicTab(DECLARATION_ALERT_TAB_ID);
//            if(tab != null) {
//                index = form.indexOfTab(tab);
//            }
//
//            AlertDtoQuery query = new AlertDtoQuery();
//            List<AlertDto> list = new ArrayList<>();
//            if (model.getDocId() != null) {
//                query.setDeclarationId(model.getDocId());
//                list = alertDtoService.listDto(query);
//            }
//
//            if(list.size() > 0) {
//                if(index < 0) {
//                    tab = new DynamicTab();
//                    tab.setTabId(DECLARATION_ALERT_TAB_ID);
//                    String label = "[className='tab-badge']" + list.size() + "[/className]";
//                    tab.setLabel(messageService.getAllBundles().getMessage("declaration.ct.tabs.declarationAlert") + label);
//                    tab.setFormReference("pl.fhframework.dp.commons.fh.declaration.handling.DeclarationAlertForm");
//                    declarationAlertFormModel = new DeclarationAlertForm.Model();
//                    declarationAlertFormModel.setDeclarationAlertResult(new DeclarationAlertPagedTableSource(list).createPagedModel());
//                    tab.setModel(declarationAlertFormModel);
//                    form.addDynamicTab(tab);
//                    model.setActiveTabIndex(form.indexOfTab(tab));
//                } else {
//                    tab.getTab().setAvailability(AccessibilityEnum.EDIT);
//                    declarationAlertFormModel.setDeclarationAlertResult(new DeclarationAlertPagedTableSource(list).createPagedModel());
//                }
//            } else if(index > 0) {
//                tab.getTab().setAvailability(AccessibilityEnum.HIDDEN);
//            }
//        }
//    }

//TODO: move to successors!!

//    protected void initValidationResult() {
//        if(validationResult) {
//            int index = -1;
//            DeclarationHandlingForm.DynamicTab tab = form.getDynamicTab(VALIDATION_RESULT_TAB_ID);
//            if(tab != null) {
//                index = form.indexOfTab(tab);
//            }
//            List<ValidationResultCT> validationResultCTS = getValidationResults();
//            if(validationResultCTS != null && !validationResultCTS.isEmpty()) {
//                if(index < 0) {
//                    tab = new DeclarationHandlingForm.DynamicTab();
//                    tab.setTabId(VALIDATION_RESULT_TAB_ID);
//                    String label = "[className='tab-badge']" + getValidationResults().size() + "[/className]";
//                    tab.setLabel(messageService.getAllBundles().getMessage("declaration.ct.tabs.validationResult") + label);
//                    tab.setFormReference("pl.fhframework.dp.commons.fh.declaration.handling.ValidationResultForm");
//                    validationResultFormModel = new ValidationResultForm.Model();
////                    validationResultFormModel.setValidationResults(new ValidationResultPagedTableSource(model.getDeclarationCTDto()).createPagedModel());
////                    validationResultFormModel.setSelectedValidationResult(new ValidationResultPagedTableSource(model.getDeclarationCTDto()).createSelectValidationResult());
//                    validationResultFormModel.setValidationResults(createValidationResultPagedTableSource().createPagedModel());
//                    validationResultFormModel.setSelectedValidationResult(createValidationResultPagedTableSource().createSelectValidationResult());
//                    tab.setModel(validationResultFormModel);
//                    form.addDynamicTab(tab);
//                    model.setActiveTabIndex(form.indexOfTab(tab));
//                } else {
//                    tab.getTab().setAvailability(AccessibilityEnum.EDIT);
//                    validationResultFormModel.setValidationResults(createValidationResultPagedTableSource().createPagedModel());
//                    validationResultFormModel.setSelectedValidationResult(createValidationResultPagedTableSource().createSelectValidationResult());
////                    validationResultFormModel.getValidationResults().clear();
////                    validationResultFormModel.getValidationResults()
////                            .addAll(model.getDeclarationCTDto().getValidationResults());
//                }
//
//            } else if(index > 0) {
//                tab.getTab().setAvailability(AccessibilityEnum.HIDDEN);
////                form.removeDynamicTab(tab);
//            }
//        }
//    }

//    protected abstract ValidationResultPagedTableSource createValidationResultPagedTableSource();
//
//    protected abstract List<ValidationResultCT> getValidationResults();

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
            tab.setLabel(messageService.getAllBundles().getMessage("declaration.ct.tabs.pendingOperation"));
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
        } else {
            model.setTimerTimeout(0);
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
    }

    protected void initLeftMenu(){
        try {
            List<ElementCT> elementCTS = outlineService.generateOutline(getDocumentType().getTypeName());
            List<TreeElement<ElementCT>> leftMenu = outlineService.buildLeftMenu(elementCTS);
            //TODO: move to successors
//            List<TreeElement<DocMessageDto>> declarationMessagesLeftMenu = outlineService.buildMessagesLeftMenu(model.getMessages());

            model.setDocLeftMenu(leftMenu);
            //TODO: move to successors
//            model.setDeclarationMessagesLeftMenu(declarationMessagesLeftMenu);

//            selectFirstMessage();

            documentHandler.initLeftMenu(leftMenu);
        } catch (JAXBException e) {
            FhLogger.error("{}{}", e.getMessage(), e);
        }
    }

    protected void selectFirstMessage() {
        //TODO: move to descendants
//        if (!model.getDeclarationMessagesLeftMenu().isEmpty()) {
//            String elementId = String.format("%s[0]", getLeftMenuMessageGroupId());
//            this.openTreeBranch(this.getLeftMenuMessageId(), elementId);
//        }
    }

    @Action(value = "onTabChangeMessages", validate = false)
    public void onTabChangeMessages(int messagesTabIndex) {
        super.buttonsFormManagement(messagesTabIndex != 0);
    }

    //TODO: move to successors
//    protected void initDeclarationMessages() {
//
//        List<DocMessageDto> messageDtos = new ArrayList<>();
//
//        if(model.getDocId() != null) {
//            DocMessageDtoQuery messageDtoQuery = new DocMessageDtoQuery();
//            messageDtoQuery.setDocId(model.getDocId());
//            messageDtoQuery.setSortProperty("stored");
//            messageDtoQuery.setWithContent(true);
//            messageDtoQuery.setSize(600);
//            messageDtos = declarationMessageService.listDto(messageDtoQuery);
//        }
//        model.setMessages(messageDtos);
//    }

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
        //TODO: move to successors
//        initDeclarationMessages();
        initLeftMenu();
        //TODO: move to successors
//        initValidationResult();
//        initDeclarationAlert();
        setOperationPagedModel();
        eventRegistry.fireNotificationEvent(NotificationEvent.Level.INFO, info);
    }

    protected abstract void setOperationPagedModel();

    protected abstract void getHistory(Long id);


    public void refreshView() {
        // TO DO: language handling
        // the below key should be used
        // message.operation.result
        refreshView(messageService.getAllBundles().getMessage("declaration.ct.actions.refresh.manual"));
        //"declaration.ct.actions.refresh.operation"));
    }

    @Action(validate = false)
    public void refreshManual() {
        refreshView(messageService.getAllBundles().getMessage("declaration.ct.actions.refresh.manual"));
    }

    @Action(validate = false)
    public void openPinup() {
        String url = model.getPinupUrl()
            + "?id=" + model.getDocId()
            + "&lng=" + getUserSession().getLanguage().toLanguageTag();
        eventRegistry.fireCustomActionEvent("openPinup", url);
    }

//    private void showErrors(OperationResultBaseDto result) {
//        ValidationMessagesForm.Model model = new ValidationMessagesForm.Model();
//        model.setMessages(result.getValidationResults());
//        runUseCase(ValidationMessagesUC.class, model, new IUseCaseCloseCallback() {
//            @Override
//            public void close() {
//                refreshView();
//            }
//        });
//    }

    protected void initDocumentHandlingForm() {
        form.initMainTabContainer();
        if (!model.getDocLeftMenu().isEmpty()) {
            //TODO: move to successors
//            form.initDeclRightPanel(model.getSpecificDeclarationFormModel(),
//                model.getDeclarationLeftMenu().get(0).getObj().getForm(),
//                declarationHandler.resolveVariant(model.getSpecificDeclarationFormModel()));
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

    //TODO: move to successors
//    @Action(value = "onDeclarationMessageFormLeftPanelClick", validate = false)
//    public void onDeclarationMessageFormLeftPanelClick(TreeElement<DocMessageDto> element) {
//        //TODO: lazy load!!!
////        if(element.getObj().getMessageContent() == null) {
////            // load message content
////            String msgId = element.getObj().getDoc().getId();
////            element.getObj().setMessageContent(messageRepositoryService.getMessageContent(msgId));
////        }
//        DeclarationMessageFormModel messageFormModel = new DeclarationMessageFormModel(element.getObj());
//        form.initDeclRightPanel(
//            messageFormModel,
//            declarationHandler.getMessagesForm(),
//            declarationHandler.resolveVariant(model.getSpecificDeclarationFormModel()),
//            "messagesGroup"
//        );
//        showForm(this.getDeclarationMessageButtonsForm(), messageFormModel);
//    }

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
        //TODO: move to successors
//        if(mode.equals(LocalValidationModeEnum.all) || mode.equals(LocalValidationModeEnum.declaration)) {
//            clearLocalValidationResult();
//            Object declForValidation = declarationHandler.getIEx15Declaration(model.getDeclaration());
//            BeanClearUtil.clearObject(declForValidation);
//            Set<ConstraintViolation<Object>> resultSet = validatorProviderFhdp.getValidator(null)
//                .validate(declForValidation);
//            for(ConstraintViolation<Object> v: resultSet ) {
//                ValidationResultCT vr = new ValidationResultCT();
//                vr.setType(ValidationResultTypeST.ERROR);
//                vr.setCode("33");
//                vr.setSource(ValidationSourceFhdpEnum.JSR303.name());
//                vr.setPointer("Declaration." + v.getPropertyPath().toString());
//                vr.setDescription(v.getMessage());
//                getValidationResults().add(vr);
//            }
//            getValidationResults().sort(Comparator.comparing(ValidationResultCT::getPointer));
//        }
//        initValidationResult();
        return !(getUserSession().getValidationResults().hasAtLeastErrors() || documentHasErrors());
    }

    protected abstract void validateExt(LocalValidationModeEnum mode);
    //TODO: move to successors
//    protected void clearLocalValidationResult() {
//        getValidationResults()
//            .removeIf(validationResultDto -> validationResultDto.getSource().equals(ValidationSourceFhdpEnum.JSR303.name()));
//    }

    protected boolean documentHasErrors() {
        //TODO: move to successors
//        getValidationResults()
//            .stream()
//            .anyMatch(validationResultDto -> validationResultDto.getType().equals(ValidationResultTypeST.ERROR));
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

    //TODO: move to descendants
//    public String getDeclarationMessageButtonsForm() {
//        return "pl.fhframework.dp.commons.fh.declaration.message.DeclarationMessageButtonsForm";
//    }

    @Action (validate = false)
    public void showOperationDetails() {
//        showForm(OperationDetailsForm.class, model);
//        super.appSiderDetailsManagement(false);
        performShowOperationDetails();
    }

    protected abstract void performShowOperationDetails();

    //TODO: move to successors
//    @Action(validate = false)
//    public void showValidationResultDetails() {
//        showForm(ValidationResultDetailsForm.class, validationResultFormModel);
//        super.appSiderDetailsManagement(false);
//    }

    @Action(validate = false)
    public void closeOperationDetails() {
        super.appSiderDetailsManagement(true);
    }

    @Override
    public void onSessionLanguageChange() {
        getActiveForm().onSessionLanguageChange(getUserSession().getLanguage().toLanguageTag());
        getActiveForm().refreshView();
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
                            exit().cancel();
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

    //TODO: move to successors
//    @Action
//    public void downloadDeclarationMessage(DocMessageDto messageDto) {
//        declarationMessageService.downloadDeclarationMessageFile(messageDto, eventRegistry);
//    }


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

            try {
                List<ElementCT> result = outlineService.generateOutline(getDocumentType().getTypeName());
                for (String pointer : resultList) {
                    ElementCT el = outlineService.findElementFromPointer(pointer);
                    if (el != null && el.getId() != null) {
                        pointers.add(el.getId());
                    }
                }
            } catch (JAXBException e) {
                e.printStackTrace();
            }

            getModel().setSearchPointers(pointers);
        }
    }
}
