package pl.fhframework.docs.change.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import pl.fhframework.docs.change.model.Change;
import pl.fhframework.docs.change.model.Change.Type;

import java.util.Date;
import java.util.List;

/**
 * Service for returning information about changes in application releases.
 * Created by Adam Zareba on 08.02.2017.
 */
public interface ChangeService {

    /**
     * Returns Page collection of changes in application for given pageRequest and task type.
     *
     * @param type     type of task
     * @param pageable request for page
     * @return collection  of changes
     */
    Page<Change> findAllBy(Type type, PageRequest pageable);

    /**
     * Returns collection of changes in application for given task type and start date.
     *
     * @param type      type of task
     * @param untilDate minimum date of task for querying
     * @return collection  of changes
     */
    List<Change> findAllBy(Type type, Date untilDate);

    /**
     * Adds given collection of changes.
     *
     * @param changesToAdd changes to be added
     */
    void addChanges(List<Change> changesToAdd);
}
