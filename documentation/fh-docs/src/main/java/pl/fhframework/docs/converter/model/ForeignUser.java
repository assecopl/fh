package pl.fhframework.docs.converter.model;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForeignUser extends User {
    private String systemInfo;
    private String companyName;

    public ForeignUser(String username, String email, String firstName, String lastName, int age, LocalDateTime creationDate, String systemInfo, String companyName) {
        super(username, email, firstName, lastName, age, creationDate);
        this.systemInfo = systemInfo;
        this.companyName = companyName;
    }
}
