package pl.fhframework.dp.commons.fh.parameters.list;

import org.springframework.stereotype.Service;
import pl.fhframework.dp.commons.fh.declaration.list.searchtemplate.Column;
import pl.fhframework.dp.commons.fh.declaration.list.searchtemplate.ISearchTemplateCriteriaProvider;
import pl.fhframework.dp.transport.dto.parameters.SubstantiveParametersDto;

import java.util.List;

@Service
public class SearchTemplateCriteriaProviderSubstantiveParameters implements ISearchTemplateCriteriaProvider<SubstantiveParametersDto> {
//    @Autowired
//    private BaseAppMessageHelper baseAppMessageHelper;

    private String path = SubstantiveParametersDto.class.getName();

    //TODO: wyszukiwanie parametrów zrealizowane będzie w późniejszym etapie
    @Override
    public List<Column> getAvailableColumns() {

//        Map<String, Object> dictParamsNameDict = new HashMap<>();
//        dictParamsNameDict.put("codeListId", "nameDict");
//        dictParamsNameDict.put("referenceDate", LocalDate.now());
//
//        Map<String, Object> dictParamsTestDict = new HashMap<>();
//        dictParamsTestDict.put("codeListId", "testDict");
//        dictParamsTestDict.put("referenceDate", LocalDate.now());
//
//        List<Column> columns = new ArrayList<>();
//        columns.add(Column.buildComboColumn("state", getI18nPath("state"), MockStateEnum.class));
//        columns.add(Column.buildTextColumn("declaration.lrn", getI18nPath("lrn")));
//        columns.add(Column.buildTextColumn("processId", getI18nPath("processId")));
//        columns.add(Column.buildTextColumn("taskId", getI18nPath("taskId")));
//        columns.add(Column.buildTextColumn("mrn", getI18nPath("mrn")));
//        columns.add(Column.buildTextColumn("declaration.importer.identificationNumber", getI18nPath("importerIdentificationNumber")));
//        columns.add(Column.buildTextColumn("declaration.declarant.identificationNumber", getI18nPath("declarantIdentificationNumber")));
//        columns.add(Column.buildTextColumn("declaration.representative.identificationNumber", getI18nPath("representativeIdentificationNumber")));
//        columns.add(Column.buildTextColumn("declaration.CustomsOfficeOfImport.referenceNumber", getI18nPath("customsOfficeOfImportReferenceNumber")));
//        columns.add(Column.buildTextColumn("declaration.declarationType", getI18nPath("declarationType")));
//        columns.add(Column.buildTextColumn("declaration.additionalDeclarationType", getI18nPath("additionalDeclarationType")));
//        columns.add(Column.buildTextColumn("declaration.GoodsShipment.GoodsItem.Procedure.previousProcedure", getI18nPath("previousProcedure")));
//        columns.add(Column.buildTextColumn("declaration.goodsShipment.goodsItem.additionalProcedure.additionalProcedure", getI18nPath("additionalProcedure")));
//        columns.add(Column.buildTextColumn("id1String", getI18nPath("previousDocumentReferenceNumber")));
//        columns.add(Column.buildTextColumn("declaration.goodsShipment.goodsItem.previousDocument.type", getI18nPath("previousDocumentType")));
//        columns.add(Column.buildTextColumn("declaration.GoodsShipment.GoodsItem.SupportingDocument.referenceNumber", getI18nPath("supportingDocumentReferenceNumber")));
//        columns.add(Column.buildTextColumn("id1String", getI18nPath("supportingDocumentType")));
//        columns.add(Column.buildTextColumn("declaration.GoodsShipment.GoodsItem.TransportDocument.referenceNumber", getI18nPath("transportDocumentReferenceNumber")));
//        columns.add(Column.buildTextColumn("declaration.GoodsShipment.GoodsItem.TransportDocument.type", getI18nPath("transportDocumentType")));
//        columns.add(Column.buildTextColumn("declaration.GoodsShipment.GoodsItem.Authorisation.referenceNumber", getI18nPath("authorisationReferenceNumber")));
//        columns.add(Column.buildTextColumn("declaration.goodsShipment.goodsItem.authorisation.type", getI18nPath("authorisationType")));
//        columns.add(Column.buildTextColumn("declaration.GoodsShipment.GoodsItem.CommodityCode.harmonisedSystemSubheadingCode", getI18nPath("harmonisedSystemSubheadingCode")));
//        columns.add(Column.buildTextColumn("declaration.GoodsShipment.GoodsItem.CommodityCode.combinedNomenclatureCode", getI18nPath("combinedNomenclatureCode")));
//        columns.add(Column.buildTextColumn("declaration.GoodsShipment.GoodsItem.CommodityCode.taricCode", getI18nPath("tariCode")));
//        columns.add(Column.buildTextColumn("declaration.GoodsShipment.GoodsItem.Procedure.requestedProcedure", getI18nPath("requestedProcedure")));
//
//        columns.add(Column.buildDateColumn("creationDate", getI18nPath("creationDateFrom")));
//        columns.add(Column.buildDateColumn("creationDate", getI18nPath("creationDateTo")));
//        columns.add(Column.buildDateColumn("declaration.releaseDate", getI18nPath("releaseDateFrom")));
//        columns.add(Column.buildDateColumn("declaration.releaseDate", getI18nPath("releaseDateTo")));
//        columns.add(Column.buildDateColumn("declaration.declarationAcceptanceDate", getI18nPath("declarationAcceptanceDateFrom")));
//        columns.add(Column.buildDateColumn("declaration.declarationAcceptanceDate", getI18nPath("declarationAcceptanceDateTo")));
//        columns.add(Column.buildSelectOneColumn("signedElectronically", getI18nPath("signedElectronically"), MockEnum.class));
//        columns.add(Column.buildSelectOneColumn("fallback", getI18nPath("fallback"), MockEnum.class));
//
//        return columns;

        return null;
    }

    //TODO: wyszukiwanie parametrów zrealizowane będzie w późniejszym etapie
//    private String getI18nPath(String fieldName) {
//        String description = baseAppMessageHelper.getMessage(path + "." + fieldName);
//        if (!description.contains(" not found"))
//            return description;
//        else
//            return fieldName;
//    }
}
