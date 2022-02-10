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

}
