package pl.fhframework.docs.change.form;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import pl.fhframework.core.paging.ComparatorFunction;
import pl.fhframework.docs.change.model.Change;
import pl.fhframework.docs.change.model.Change.Type;
import pl.fhframework.docs.change.service.ChangeService;

import java.util.Date;
import java.util.List;
import java.util.function.Function;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Adam Zareba on 08.02.2017.
 */
@Getter
@Setter
public class ChangeLogModel {

    private static long DAY_IN_MS = 1000 * 60 * 60 * 24;

    private List<Change> newImprovements;
    private List<Change> newBugFixes;

    private Page<Change> allImprovements;
    private Page<Change> allBugFixes;

    public ChangeLogModel(ChangeService changeService) {
        // get changes information from last 7 days
        Date startDate = new Date(System.currentTimeMillis() - (7 * DAY_IN_MS));
        this.newImprovements = changeService.findAllBy(Type.IMPROVEMENT, startDate);
        this.newBugFixes = changeService.findAllBy(Type.BUG, startDate);
    }
}
