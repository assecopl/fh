package pl.fhframework.model.forms;

import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.function.Function;

/**
 * Created by k.czajkowski on 20.03.2017.
 */
public class PageModel<T> {

    protected Function<Pageable, Page<T>> dataSource;

    @Getter
    private Page<T> page;

    @Getter
    private boolean isRefreshNeeded = true;

    @Getter
    private boolean isResetNeeded = true;

    public void refreshNeeded() {
        isRefreshNeeded = true;
    }

    public void doNotRefresh() {
        isRefreshNeeded = false;
    }

    public void resetPage() {
        isResetNeeded = true;
        refreshNeeded();
    }

    public void doNotResetPage() {
        isResetNeeded = false;
    }

    public PageModel(Function<Pageable, Page<T>> dataSource) {
        this.dataSource = dataSource;
    }

    public void doRefresh(Pageable pageable) {
        page = dataSource.apply(pageable);
        isRefreshNeeded = false;
        isResetNeeded = false;
    }
}
