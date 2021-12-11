package pl.fhframework.dp.commons.fh.document.handling;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.dp.commons.fh.model.GenericFormModel;

import java.time.LocalDate;
import java.util.HashMap;

@Getter
@Setter
public class BaseDocumentFormModel<DTO, DOC> extends GenericFormModel<DTO> {


    @Override
    public DTO getEntity() {
        return super.getEntity();
    }

    @Override
    public void setEntity(DTO entity) {
        super.setEntity(entity);
    }

    private LocalDate referenceDate = LocalDate.now();
    private HashMap<String, String> parm;
    private String variant;
    private boolean hasTabs = true;
    private String documentTypeName;
    private String currentLocalAction;
    private DOC prevDoc;

    //TODO: move to descendants
//    public static String setSequenceNumberInInput(Collection<?> collection) {
//        return String.valueOf(collection.size()+1);
//    }
//
//    public Long setSequenceNumber(Object object, String sequenceNumberStr) {
//
//        if(object instanceof Wrapper) {
//            object = ((Wrapper) object).getElement();
//        }
//        if(object instanceof NestedWrapper) {
//            object = ((NestedWrapper) object).getElement();
//        }
//
//        Long sequenceNumber = new Long(sequenceNumberStr);
//        if(object!= null) {
//            for (Field field : object.getClass().getDeclaredFields()) {
//                field.setAccessible(true);
//
//                if(field.getName().equals("sequenceNumber")) {
//                    try {
//                        field.set(object, sequenceNumber);
//                        return sequenceNumber;
//                    } catch (IllegalAccessException e) {
//                        e.printStackTrace();
//                    }
//                }
//                else if(field.getName().equals("goodsItemNumber")) {
//                    try {
//                        field.set(object, sequenceNumber);
//                        return sequenceNumber;
//                    } catch (IllegalAccessException e) {
//                        e.printStackTrace();
//                    }
//                }
//                field.setAccessible(false);
//            }
//        }
//        return null;
//    }
}
