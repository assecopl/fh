/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.fhframework.dp.commons.fh.parameters.list;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import pl.fhframework.dp.commons.fh.declaration.list.searchtemplate.SearchTemplateBuilderModel;
import pl.fhframework.dp.commons.fh.model.GenericListPagedModel;
import pl.fhframework.dp.transport.dto.parameters.*;
import pl.fhframework.dp.transport.dto.parameters.SubstantiveParametersDto;
import pl.fhframework.dp.transport.dto.parameters.SubstantiveParametersDtoQuery;
import pl.fhframework.dp.transport.dto.parameters.SubstantiveParametersUnit;
import pl.fhframework.dp.transport.dto.parameters.SubstantiveParametersValueTypeEnum;
import pl.fhframework.dp.transport.enums.IDeclarationType;
import pl.fhframework.core.model.Model;
import pl.fhframework.model.forms.AccessibilityEnum;
import pl.fhframework.model.forms.PageModel;

import java.time.LocalDate;
import java.util.List;

@Model
@Getter
@Setter
public class SubstantiveParametersListModel extends GenericListPagedModel<SubstantiveParametersDto, SubstantiveParametersDtoQuery> {

    @Autowired
    ApplicationContext context;

    @Override
    public PageModel<SubstantiveParametersDto> getList() {
        return super.getList();
    }

    @Override
    public void setList(PageModel<SubstantiveParametersDto> list) {
        super.setList(list);
    }

    @Override
    public SubstantiveParametersDto getSelectedElement() {
        return super.getSelectedElement();
    }

    @Override
    public void setSelectedElement(SubstantiveParametersDto selectedElement) {
        super.setSelectedElement(selectedElement);
    }

    @Override
    public SubstantiveParametersDtoQuery getQuery() {
        return super.getQuery();
    }

    @Override
    public void setQuery(SubstantiveParametersDtoQuery query) {
        super.setQuery(query);
    }

    private Resource fileResource;
    private SubstantiveParametersSearchModel searchModel = new SubstantiveParametersSearchModel(getQuery());
    private SearchTemplateBuilderModel searchTemplateBuilderModel;
    private IDeclarationType declarationType;
    private List<IDeclarationType> declarationTypes;
    private boolean isResultHidden;
    private String listTitle = "test";
    public boolean isSearchBoxButtons;
    private String appContext;
    private SubstantiveParametersDto lastStoredSubstantiveParametersDto;
    private LocalDate referenceDate = LocalDate.now();

    private SubstantiveParametersDto selectedSubstantiveParametersDto;

    private final AccessibilityEnum DEFAULT_CONTROLS_STATE = AccessibilityEnum.VIEW;

    public SubstantiveParametersListModel(String appContext) {
        this.appContext = appContext;
    }

    /**
     * Required to handle not hiding the details form after save
     * @return
     * @throws CloneNotSupportedException
     */
    public SubstantiveParametersDto getSelectedSubstantiveParametersDto() {
        if (selectedSubstantiveParametersDto == null && lastStoredSubstantiveParametersDto != null) {
            selectedSubstantiveParametersDto = lastStoredSubstantiveParametersDto;
        }
        return selectedSubstantiveParametersDto;
    }

    public AccessibilityEnum checkControlVisibility(SubstantiveParametersValueTypeEnum valueTypes, String controlType) {
        switch (valueTypes) {
            case BOOL: {
                if (controlType.equals("CheckBox")) {
                    return DEFAULT_CONTROLS_STATE;
                }
                break;
            }
            case INTEGER:
            case DECIMAL: {
                if (controlType.equals("InputNumber")) {
                    return DEFAULT_CONTROLS_STATE;
                }
                break;
            }
            case ALPHANUMERIC: {
                if (controlType.equals("InputText")) {
                    return DEFAULT_CONTROLS_STATE;
                }
                break;
            }
            case DATE:
            case DATETIME: {
                if (controlType.equals("InputDate")) {
                    return DEFAULT_CONTROLS_STATE;
                }
                break;
            }
            case COLLECTION: {
                if (controlType.equals("Repeater")) {
                    return DEFAULT_CONTROLS_STATE;
                }
                break;
            }
        }
        return AccessibilityEnum.HIDDEN;
    }

    /**
     * Checks whether information about the unit should be shown or not
     * @return
     */
    public AccessibilityEnum checkControlVisibility(SubstantiveParametersUnit value) {
        if (value != null) {
            return DEFAULT_CONTROLS_STATE;
        }
        return AccessibilityEnum.HIDDEN;
    }

    /**
     * Checks if the caller is iMDAS and hides connected forms controls accordingly
     * @return
     */
    public AccessibilityEnum checkControlVisibility() {
        if (appContext.equals("iMDAS")) {
            return AccessibilityEnum.HIDDEN;
        }
        return DEFAULT_CONTROLS_STATE;
    }

    public AccessibilityEnum checkScopeVisibility(SubstantiveParametersDto value, String status) {
        if (appContext.equals("iMDAS")) {
            return AccessibilityEnum.HIDDEN;
        }

        boolean isSuccess = status.equals("success");
        if (value.isPerOffice() == isSuccess) {
            return AccessibilityEnum.HIDDEN;
        }

        return DEFAULT_CONTROLS_STATE;
    }
}
