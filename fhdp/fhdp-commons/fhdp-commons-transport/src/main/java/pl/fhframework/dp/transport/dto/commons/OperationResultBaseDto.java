package pl.fhframework.dp.transport.dto.commons;

import lombok.Getter;
import lombok.Setter;


import java.io.Serializable;

/**
 * @author <a href="mailto:dariusz_skrudlik@javiko.pl">Dariusz Skrudlik</a>
 * @version :  $, :  $
 * @created 2019-05-31
 */
@Getter @Setter
public class OperationResultBaseDto implements Serializable {

    private static final long serialVersionUID = 6470652337734973557L;

    private Long documentId;
    private String operationID;
    private String processID;
    private boolean ok = true;
    private Long resultCode; //1: Ok, 0: No action, <0: Error
    private String resultMessage;
//    private List<ValidationResultCT> validationResults = new ArrayList<>();
////    private Document document;
//
//    public boolean hasErrors() {
//        return validationResults.stream().anyMatch(validationResultDto -> validationResultDto.getType().equals(ValidationResultTypeST.ERROR));
//    }
//
//    public boolean hasSeriousErrors() {
//        return validationResults.stream().anyMatch(validationResultDto -> (validationResultDto.getType().equals(ValidationResultTypeST.ERROR)) && validationResultDto.getIsSerious());
//    }
}
