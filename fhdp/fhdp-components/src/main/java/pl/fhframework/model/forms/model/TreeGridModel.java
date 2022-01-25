package pl.fhframework.model.forms.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.fhframework.model.forms.PageModel;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Getter
@Setter
public class TreeGridModel<T> extends PageModel<T> {
    private List<T> rowsElements;

    public TreeGridModel(Function<Pageable, Page<T>> dataSource) {
        super(dataSource);
    }

    @Override
    public void doRefresh(Pageable pageable) {
        super.doRefresh(pageable);
        this.rowsElements = new ArrayList<T>(this.getPage().getContent());
    }

}