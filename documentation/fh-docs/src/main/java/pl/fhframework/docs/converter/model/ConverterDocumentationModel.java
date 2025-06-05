package pl.fhframework.docs.converter.model;

import org.springframework.stereotype.Component;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
public class ConverterDocumentationModel {
    private List<User> users;
    private User selectedUser;
    private User typedUser;

    private List<ForeignUser> foreignUsers;
    private ForeignUser selectedForeignUser;
}
