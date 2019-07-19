package pl.fhframework.core.uc.url.annotatedClasses;

import lombok.*;
import pl.fhframework.core.uc.url.UrlParam;

/**
 * Created by Piotr on 2017-06-30.
 */
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ParamWrapperNamed {

    public static final String NAME1 = "first";

    public static final String NAME2 = "last";

    @UrlParam(name = NAME1)
    private String firstName;

    @UrlParam(name = NAME2)
    private String lastName;
}
