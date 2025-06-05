package pl.fhframework.docs.forms.model.tv;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Adam Zareba on 25.01.2017.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class TvSeries {

    private Long id;
    private String name;
    private List<Season> seasons;
    private Country country;
}
