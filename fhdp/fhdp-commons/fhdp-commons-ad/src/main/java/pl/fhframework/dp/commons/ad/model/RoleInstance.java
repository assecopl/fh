package pl.fhframework.dp.commons.ad.model;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.security.model.IBusinessRole;
import pl.fhframework.core.security.model.IRoleInstance;

import java.time.LocalDate;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 09.01.2020
 */
@Getter @Setter
public class RoleInstance implements IRoleInstance {
    private Long id;
    private LocalDate assignmentTime;
    private LocalDate validFrom;
    private LocalDate validTo;
    private IBusinessRole businessRole;
}
