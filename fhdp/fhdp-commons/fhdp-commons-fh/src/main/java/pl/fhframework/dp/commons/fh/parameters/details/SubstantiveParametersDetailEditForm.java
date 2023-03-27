package pl.fhframework.dp.commons.fh.parameters.details;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.dp.transport.dto.parameters.SubstantiveParametersDto;
import pl.fhframework.dp.transport.dto.parameters.SubstantiveParametersValueTypeEnum;
import pl.fhframework.model.forms.AccessibilityEnum;
import pl.fhframework.model.forms.Form;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SubstantiveParametersDetailEditForm extends Form<SubstantiveParametersDetailEditForm.Model> {

    public SubstantiveParametersDetailEditForm() {
    }

    @Getter
    @Setter
    public static class Model {
        SubstantiveParametersDto dto;
        private String title;
        private String performButtonLabel;
        private List<String> substantiveParametersTagList;
        private boolean isAddTagClicked;
        private String name;

        public Model() {}

        private final AccessibilityEnum DEFAULT_CONTROLS_STATE = AccessibilityEnum.EDIT;

        public AccessibilityEnum checkControlVisibility(SubstantiveParametersValueTypeEnum valueTypes, String controlType) {
            switch (valueTypes) {
                case BOOL: {
                    if (controlType.equals("CheckBox"))
                        return DEFAULT_CONTROLS_STATE;
                    break;
                }
                case INTEGER:
                case DECIMAL: /*{
                    if (controlType.equals("InputNumber")) {
                        return DEFAULT_CONTROLS_STATE;
                    }
                    break;
                }*/
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
                case ONE_OF: {
                    if (controlType.equals("SelectOneMenu")) {
                        return DEFAULT_CONTROLS_STATE;
                    }
                    break;
                }
            }
            return AccessibilityEnum.HIDDEN;
        }

        public AccessibilityEnum checkControlVisibility() {
            if (isAddTagClicked) {
                return DEFAULT_CONTROLS_STATE;
            }
            return AccessibilityEnum.HIDDEN;
        }

//        /**
//         * Custom validation
//         */
//
//        OperationCT operationDto;
//
//        private ValidationResultCT selectedMessage;
//
//        private boolean hasErrors;
//
//        private boolean annotationRequired = false;
//
//        private List<ValidationResultCT> messages = new ArrayList();
//
//
//        public void setMessages(List<ValidationResultCT> messages) {
//            this.messages = messages;
//            checkErrors();
//        }
//
//        public void addMessages(List<ValidationResultCT> messages) {
//            this.getMessages().addAll(messages);
//            checkErrors();
//        }
//
//        public boolean checkErrors(){
//            if(messages != null){
//                hasErrors = messages.stream().anyMatch(m -> ValidationResultTypeST.ERROR == m.getType());
//            } else {
//                hasErrors = false;
//            }
//            return hasErrors;
//        }
    }
}
