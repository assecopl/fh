package pl.fhframework.dp.commons.validation;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.math.BigDecimal;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @created 28/03/2022
 */
@Getter @Setter
public class TestDto {
    @NotNull
    @Pattern(regexp = "\\S(.|\\r|\\n)*\\S|\\S")
    private String testRegexp;
    @DecimalMin(value = "1", inclusive = true)
    @Digits(integer = 16, fraction = 6)
    protected BigDecimal netMass;
}
