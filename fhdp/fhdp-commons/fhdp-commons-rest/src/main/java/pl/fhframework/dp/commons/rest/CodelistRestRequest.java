package pl.fhframework.dp.commons.rest;

import pl.fhframework.dp.transport.dto.commons.CodelistRequestDto;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 04.09.2019
 */
public class CodelistRestRequest extends BaseRestRequest<CodelistRequestDto> {
    private String dtoName;

    public String getDtoName() {
        return dtoName;
    }

    public void setDtoName(String dtoName) {
        this.dtoName = dtoName;
    }

    public void setDtoClass(Class dtoClass) {
        if (dtoClass != null) {
            this.dtoName = dtoClass.getSimpleName();
        }

    }

    public CodelistRestRequest(String token, CodelistRequestDto requestData) {
        super(token, requestData);
    }

    public CodelistRestRequest() {
    }
}
