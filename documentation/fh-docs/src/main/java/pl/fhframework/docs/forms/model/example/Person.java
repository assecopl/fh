package pl.fhframework.docs.forms.model.example;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of={"id"})
public class Person {

    private Long id;
    private String name;
    private String surname;
    private String city;
    private String gender;
    private String status;
    private String citizenship;
    private String drivingLicenseCategory;
    private Date birthDate;
    private List<Address> addresses;
}
