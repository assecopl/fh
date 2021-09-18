package pl.fhframework.core.uc.url.annotatedClasses;

import lombok.*;

/**
 * Created by Piotr on 2017-06-30.
 */
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ParamWrapperNoAnnotation {

    public static final String NAME1 = "firstName";

    public static final String NAME2 = "lastName";

    private String firstName;

    private String lastName;
}
