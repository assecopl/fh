package pl.fhframework.dp.commons.fh.document.handling;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.fhframework.dp.transport.enums.IDocType;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 01/11/2021
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BaseDocumentParams<DTO, DOC> {
    public DTO docDto;
    public DOC prevDoc;
    public IDocType docType;
    public String redirectUrl;
    public String variant;
}
