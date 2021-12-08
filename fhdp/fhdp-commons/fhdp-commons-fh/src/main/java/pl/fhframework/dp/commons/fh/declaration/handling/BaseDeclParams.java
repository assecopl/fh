package pl.fhframework.dp.commons.fh.declaration.handling;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.fhframework.dp.transport.enums.IDeclarationType;

/**
 * @author <a href="mailto:jacek_borowiec@skg.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 01/11/2021
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BaseDeclParams<DTO, DECL> {
    public DTO declarationDto;
    public DECL prevDeclaration;
    public IDeclarationType declarationType;
    public String redirectUrl;
    public String variant;
}
