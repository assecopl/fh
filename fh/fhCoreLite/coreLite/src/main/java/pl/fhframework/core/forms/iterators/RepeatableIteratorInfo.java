package pl.fhframework.core.forms.iterators;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Piotr on 2017-03-15.
 */
@AllArgsConstructor
@NoArgsConstructor
public class RepeatableIteratorInfo implements IRepeatableIteratorInfo {

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String collectionBinding;

}
