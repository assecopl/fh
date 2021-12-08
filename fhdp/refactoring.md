# ICDTS -> FHDP

FH document procesing


## Globalnie

**Zamiana tekstowa**
```
pl.asseco.icdts -> pl.fhframework.dp
icdts.commons.mrs -> pl.fhframework.dp.commons.mrs
```
## Moduły
```
icdts-*	fhdp-*
```
#### Wyjątki
```
icdts-cxf-jar -> fhdp-cxf-bom
```
## Nazwy klas
```
IcdtsCamundaConfig -> FhdpCamundaConfig
ICDTSRepositoryService -> FhdpRepositoryService
TextUtilsICDTS -> TextUtils
ICDTSBaseUC -> FhdpBaseUC
ValidatorProviderICDTS -> ValidatorProviderFhdp
ValidationSourceIcdtsEnum -> ValidationSourceFhdpEnum
MsgErrorInformationHandlerICDTS -> MsgErrorInformationHandlerFhdp
NotificationErrorInformationHandlerIcdts -> NotificationErrorInformationHandlerFhdp
ICDTSObjectMapper -> FhdpObjectMapper
ParameterMessageInterpolatorICDTS -> ParameterMessageInterpolatorFhdp
IParmVariant -> IParamVariant

```
## Parametry
```
icdts.security.*    ->  fhdp.security.*

icdts.notification.timeout -> fhdp.notification.timeout
icdts.theme -> icdts.theme
icdts.alternate.theme -> fhdp.alternate.theme
icdts.languageDropdown -> fhdp.languageDropdown
icdts.appSider -> fhdp.appSider
icdts.helpSider -> fhdp.helpSider
icdts.operationSider -> fhdp.operationSider
icdts.sessionClock -> fhdp.sessionClock
icdts.onlyContrastStyle -> fhdp.onlyContrastStyle

icdts.visibilityAppName -> fhdp.visibilityAppName
icdts.versionVisibility -> fhdp.versionVisibility
icdts.appName -> fhdp.appName

icdts.pinup.url -> fhdp.pinup.url

icdts.validationResult -> fhdp.validationResult
icdts.declarationAlert -> fhdp.declarationAlert
icdts.timerTimeout -> fhdp.timerTimeout
icdts.closeDeclarationList -> fhdp.closeDeclarationList
icdts.search.hideSearchByDataBtn -> fhdp.search.hideSearchByDataBtn
icdts.search.hideSearchByTemplateBtn -> fhdp.search.hideSearchByTemplateBtn
icdts.search.hideSearchBySpecificBtn:false -> fhdp.search.hideSearchBySpecificBtn:false

icdts.search.hideTreeSearchInDeclaration -> fhdp.search.hideTreeSearchInDeclaration

icdts.direction -> fhdp.direction

icdts.parameters.search.box.buttons -> fhdp.parameters.search.box.buttons
icdts.app.context -> fhdp.app.context
icdts.ip -> fhdp.ip

icdts.timeZone -> fhdp.timeZone
icdts.mail.from -> fhdp.mail.from
icdts.hostNameProperty -> fhdp.hostNameProperty
```
## Kontrolki

Wszystkie kontrolki z końcówką *ICDTS zmieniono na *FhDP.
```
PanelHeaderICDTS -> PanelHeaderFhDP
TimerICDTS -> TimerFhDP
TabContainerICDTS -> TabContainerFhDP
TreeFhDP -> TreeFhDP
TreeElementICDTS -> TreeElementFhDP
InputTextICDTS -> InputTextFhDP
InputTimestampICDTS -> InputTimestampFhDP
XMLViewerICDTS -> XMLViewerFhDP
DictionaryComboICDTS -> DictionaryComboFhDP
DictionaryComboParameterICDTS -> DictionaryComboParameterFhDP
InputDateICDTS -> InputDateFhDP
```
## Elementy do częściowego rozszerzenia krajowego
```
pl.fhframework.dp.commons.base.model.MessageConstants - elementy specyficzne dla PL
pl.fhframework.dp.commons.comparator.ChangeTypeEnum - polskie opisy
pl.fhframework.dp.commons.comparator.annotations.ComparableField - metody specyficzne dla PL
pl.fhframework.dp.commons.ds.repository.client.DRSDirectClient - processing flags
pl.fhframework.dp.commons.fh.declaration.list.searchtemplate.SearchTemplateBuilderService - elementy specyficzne dla PL
pl.fhframework.dp.commons.fh.declaration.list.searchtemplate.name.ChooseSearchTemplateNameUC - j.w.
pl.fhframework.dp.commons.fh.parameters.list.SubstantiveParametersListModel - iMDAS specific code
pl.fhframework.dp.commons.fh.uc.header.AppNavBar*
pl.fhframework.dp.commons.services.listeners.SetStateExecutionListener
pl.fhframework.dp.commons.services.listeners.SetStateListener
pl.fhframework.dp.commons.services.listeners.SetStateUtil
pl.fhframework.dp.commons.services.msg.DeliveredResponseHandler - funkcjonalność specyficzna dla PL
pl.fhframework.dp.commons.services.msg.UndeliveredMessageService - j.w.
pl.fhframework.dp.transport.dto.alerts.AlertCodeEnum - elementy specyficzne dla LT.
pl.fhframework.dp.transport.dto.commons.AdmDecisionDto - PL
pl.fhframework.dp.transport.dto.commons.AdmDecisionEnum - PL
pl.fhframework.dp.transport.dto.commons.ChangeDto - PL
pl.fhframework.dp.transport.dto.commons.ChangeValueDto - PL
pl.fhframework.dp.transport.dto.commons.CommunicationLanguageEnum - LT
pl.fhframework.dp.transport.dto.commons.CorrectionDto - PL
pl.fhframework.dp.transport.dto.commons.CorrectionStateEnum - PL
pl.fhframework.dp.transport.dto.commons.DirectiveControlMethodEnum - PL
pl.fhframework.dp.transport.dto.commons.GoodsItemDataDto - PL
pl.fhframework.dp.transport.dto.commons.GoodsItemInvalidateDto - PL
pl.fhframework.dp.transport.dto.commons.GoodsItemPaymentsFlagsDto - PL
pl.fhframework.dp.transport.dto.commons.GoodsItemQuotaDto - PL?
pl.fhframework.dp.transport.dto.commons.GoodsItemRegulateDto - PL
pl.fhframework.dp.transport.dto.commons.InvalidateMessageDto - PL
pl.fhframework.dp.transport.dto.commons.IWdzPodz - PL
pl.fhframework.dp.transport.dto.commons.IWdzPodzItem - PL
pl.fhframework.dp.transport.dto.commons.NameValueDto - do zbadania
pl.fhframework.dp.transport.dto.commons.OsozZefir* - PL
pl.fhframework.dp.transport.dto.commons.PaymentMethodEnum - PL
pl.fhframework.dp.transport.dto.commons.PaymentSelection - PL
pl.fhframework.dp.transport.dto.commons.PodStatus - PL
pl.fhframework.dp.transport.dto.commons.ProcessingFlags - PL
pl.fhframework.dp.transport.dto.commons.QuotaItemStatus - PL?
pl.fhframework.dp.transport.dto.commons.QuotaStatusResult - PL?
pl.fhframework.dp.transport.dto.commons.QuotaVerificationEnum - PL?
pl.fhframework.dp.transport.dto.commons.QuotaVerificationSecEnum - PL?
pl.fhframework.dp.transport.dto.commons.SentToOsozStatusEnum - PL
pl.fhframework.dp.transport.dto.commons.SentToZefirStatusEnum - PL
pl.fhframework.dp.transport.dto.commons.VerificationList* - PL
pl.fhframework.dp.transport.dto.commons.WdzPodz* - PL
pl.fhframework.dp.transport.dto.parameters.SubstantiveParametersComponentsEnum
pl.fhframework.dp.transport.dto.parameters.SubstantiveParametersParameterDefinition - PL
pl.fhframework.dp.transport.enums.MockEnum
pl.fhframework.dp.transport.enums.MockStateEnum

pl.fhframework.dp.commons.services.msg.MessageDeliveryResponseController -???
```
## Elementy do przeniesienia do rozszerzeń krajowych
```
fhdp-commons-esb
```

## Elementy do pozostawienia w eu-cdm-common
**icdts-camunda**
```
pl.asseco.icdts.commons.camunda.drs.client.DRSRestClient
pl.asseco.icdts.commons.camunda.drs.client.DrsRestTemplateConfig
```
**icdts-commons-base**
```
pl.asseco.icdts.commons.base.model.MessageConstants
```

**icdts-commons-ds-service**
```
pl.asseco.icdts.commons..ds.repository.client.DRSDirectClient
```
**icdts-commons-fh**
```
pl.asseco.icdts.commons.fh.dataProviders.*
pl.asseco.icdts.commons.fh.declaration.handling.ConsignmentItemPagedTableSource
pl.asseco.icdts.commons.fh.declaration.handling.ConsignmentItemPagedTableSourceWrapper
pl.asseco.icdts.commons.fh.declaration.handling.DeclarationCTParams
pl.asseco.icdts.commons.fh.declaration.handling.DeclarationFormModel
pl.asseco.icdts.commons.fh.declaration.handling.DeclarationHandlerBase
pl.asseco.icdts.commons.fh.declaration.handling.DeclarationHandlingFormModel
pl.asseco.icdts.commons.fh.declaration.DeclarationHandlingOutlineForm
pl.asseco.icdts.commons.fh.declaration.DeclarationHandlingOutlineForm.frm
pl.asseco.icdts.commons.fh.declaration.DeclarationHandlingUC
pl.asseco.icdts.commons.fh.declaration.DeclarationHandlingForm
pl.asseco.icdts.commons.fh.declaration.DeclarationHandlingForm.frm
pl.asseco.icdts.commons.fh.declaration.handling.GoodsItemPagedTableSource
pl.asseco.icdts.commons.fh.declaration.handling.GoodsItemPagedWrapperTableSource
pl.asseco.icdts.commons.fh.declaration.handling.HouseConsignmentItemPagedTableSource
pl.asseco.icdts.commons.fh.declaration.handling.HouseConsignmentItemPagedTableSourceWrapper
pl.asseco.icdts.commons.fh.declaration.handling.HouseConsignmentPagedTableSource
pl.asseco.icdts.commons.fh.declaration.handling.HouseConsignmentPagedTableSourceWrapper
pl.asseco.icdts.commons.fh.declaration.handling.OperationHandler
pl.asseco.icdts.commons.fh.declaration.handling.OperationDetailsForm
pl.asseco.icdts.commons.fh.declaration.handling.OperationDetailsForm.frm
pl.asseco.icdts.commons.fh.declaration.handling.OperationPagedTableSource
pl.asseco.icdts.commons.fh.declaration.handling.OperationDataBaseForm
pl.asseco.icdts.commons.fh.declaration.handling.OperationDataBaseForm.frm
pl.asseco.icdts.commons.fh.declaration.handling.OperationDataBaseUC
pl.asseco.icdts.commons.fh.declaration.handling.ValidationMessagesForm
pl.asseco.icdts.commons.fh.declaration.handling.ValidationMessagesForm.frm
pl.asseco.icdts.commons.fh.declaration.handling.ValidationMessagesUC
pl.asseco.icdts.commons.fh.declaration.handling.ValidationResultDetailsForm
pl.asseco.icdts.commons.fh.declaration.handling.ValidationResultDetailsForm.frm
pl.asseco.icdts.commons.fh.declaration.handling.ValidationResultForm
pl.asseco.icdts.commons.fh.declaration.handling.ValidationResultForm.frm
pl.asseco.icdts.commons.fh.declaration.handling.ValidationResultPagedTableSource

pl.asseco.icdts.commons.fh.declaration.list.DeclarationCTListBaseUC
pl.asseco.icdts.commons.fh.declaration.list.DeclarationCTList.frm
pl.asseco.icdts.commons.fh.declaration.list.DeclarationCTListModel
pl.asseco.icdts.commons.fh.declaration.list.DeclarationCTListSearch
pl.asseco.icdts.commons.fh.declaration.list.DeclarationCTListSearch.frm
pl.asseco.icdts.commons.fh.declaration.list.DeclarationCTListSearchButtons.frm
pl.asseco.icdts.commons.fh.declaration.list.DeclarationCTListSearchByData
pl.asseco.icdts.commons.fh.declaration.list.DeclarationCTListSearchByData.frm
pl.asseco.icdts.commons.fh.declaration.list.DeclarationCTListSearchBySpecific
pl.asseco.icdts.commons.fh.declaration.list.DeclarationCTListSearchBySpecific.frm
pl.asseco.icdts.commons.fh.declaration.list.DeclarationCTListSearchByTemplate.frm
pl.asseco.icdts.commons.fh.declaration.list.DeclarationListConfig
pl.asseco.icdts.commons.fh.declaration.list.ExampleDeclarationCTListUC
pl.asseco.icdts.commons.fh.declaration.list.SearchModel

pl.asseco.icdts.commons.fh.declaration.list.loaderDeclarationListLoaderBase

pl.asseco.icdts.commons.fh.declaration.preview.DeclarationPreviewUC

pl.asseco.icdts.commons.fh.importers.file.IMessageFileImporter
pl.asseco.icdts.commons.fh.importers.file.MessageFileImporterService

pl.asseco.icdts.commons.fh.services.DeclarationCTHistoryDtoService
pl.asseco.icdts.commons.fh.services.DeclarationCTService

pl.asseco.icdts.commons.fh.uc.header.FileUploaderUC
```
**icdts-commons-model**
```
pl.asseco.icdts.commons.model.entities.Declaration
pl.asseco.icdts.commons.model.repositories.DeclarationESRepository
pl.asseco.icdts.commons.model.repositories.DeclarationJPARepository
```
**icdts-commons-mrs-xx**

**icdts-commons-services**
```
pl.asseco.icdts.commons.services.declaration.DeclarationCTDtoService
pl.asseco.icdts.commons.services.declaration.DeclarationCTHistoryDtoService
pl.asseco.icdts.commons.services.declaration.HistoryOfOperations
pl.asseco.icdts.commons.services.declaration.PrePersistService
pl.asseco.icdts.commons.services.listeners.SetStateUtil - refactor to implement ISetStateService
pl.asseco.icdts.commons.services.msg.*
pl.fhframework.dp.commons.services.operations.BaseOperationService
```

**icdts-commons-transport**
```
pl.asseco.icdts.transport.dto.commons.DeclarationBaseOperationDto
pl.asseco.icdts.transport.dto.commons.OperationDto
pl.asseco.icdts.transport.dto.commons.OperationGetMessageDto
pl.asseco.icdts.transport.dto.commons.OperationResultDto
pl.asseco.icdts.transport.dto.commons.DeclarationCancelProcessOperationDto
pl.asseco.icdts.transport.dto.commons.IPrePersist
pl.asseco.icdts.transport.dto.declaration.DeclarationCTDto
pl.asseco.icdts.transport.dto.declaration.DeclarationCTDtoQuery
pl.asseco.icdts.transport.dto.declaration.DeclarationCTGoodsItemDtoQuery
pl.asseco.icdts.transport.dto.declaration.DeclarationCTHistoryDto
pl.asseco.icdts.transport.dto.declaration.DeclarationCTHistoryDtoQuery
pl.asseco.icdts.transport.service.IAcceptDeclWaitOperationService
pl.asseco.icdts.transport.service.IDeclarationCTDtoService
pl.asseco.icdts.transport.service.IDeclarationCTHistoryDtoService
pl.asseco.icdts.transport.service.IFinalizeDraftOperationService
pl.asseco.icdts.transport.service.ITestActionOperationService - do examples
pl.asseco.icdts.transport.dto.comparator.ObjectDataComparator
pl.asseco.icdts.transport.dto.comparator.CompareSummaryData
```

## Polskie opisy
```
pl.fhframework.dp.commons.jaxb.*
pl.fhframework.dp.commons.services.parameters.SubstantiveParametersService
pl.fhframework.dp.commons.services.searchTemplate.SearchTemplateQueryBuilder
pl.fhframework.dp.commons.fh.declaration.list.searchtemplate.SearchTemplateBuilderService
pl.fhframework.dp.transport.service.IDtoService
pl.fhframework.dp.commons.services.parameters.SubstantiveParametersService
```

## Nie do ruszenia
Spróbować sparametryzować.
####Nazwy kolekcji w mongo
```
icdts_document -> parametr drs.document.collection.name
icdts_document_content -> parametr drs.document.content.collection.name
icdts_history_document -> parametr drs.history.document.collection.name
icdts_history_document_content -> parametr drs.history.document.content.collection.name
```
###Tabele w bazie danych
```
icdts_declaration, icdts_declaration_uk_ulrn, seq_icdts_declaration - idzie do template
icdts_max_id - usunięte (jest w mongo)
icdts_search_template -> fhdp_search_template
icdts_semaphores -> fhdp_semaphores
```

