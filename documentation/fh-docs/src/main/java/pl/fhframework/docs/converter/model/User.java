package pl.fhframework.docs.converter.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode(of = "username")
public class User {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private int age;
    private LocalDateTime creationDate;

}
