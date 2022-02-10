package pl.fhframework.dp.commons.ad.model;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.security.model.IRoleInstance;
import pl.fhframework.core.security.model.IUserAccount;

import java.util.List;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 09.01.2020
 */
@Getter
@Setter
public class UserAccount implements IUserAccount {
    private Long id;
    private String login;
    private String firstName;
    private String lastName;
    private String password;
    private String email;
    private Boolean blocked = false;
    private String blockingReason;
    private Boolean deleted = false;
    private List<IRoleInstance> roles;

    @Override
    public void addRole(IRoleInstance roleInstance) {

    }

    @Override
    public void removeRole(IRoleInstance roleInstance) {

    }
}
