package pl.fhframework.docs.change.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.fhframework.core.io.FhResource;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.paging.ComparatorFunction;
import pl.fhframework.docs.change.model.Change;
import pl.fhframework.docs.change.model.Change.Type;
import pl.fhframework.ReflectionUtils;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Adam Zareba on 08.02.2017.
 */
@Service
public class ChangeServiceImpl implements ChangeService {

    /**
     * Resource with filepath to CSV file with data
     */
    //@Value("classpath:documentation/change/changes.csv")
    //private Resource changesFile;
    private final static String CHANGES_RESOURCE_LOCATION = "documentation/change/changes.csv";

    private List<Change> changes;

    private enum SortedProperty {
        Title((firstChange, secondChange) -> firstChange.getTitle().compareTo(secondChange.getTitle())),
        Description((firstChange, secondChange) -> firstChange.getDescription().compareTo(secondChange.getDescription()));

        private ComparatorFunction<Change> comparator;

        SortedProperty(ComparatorFunction<Change> comparator) {
            this.comparator = comparator;
        }
    }

    /**
     * Initialization method for service. Reads data from CSV file to the application.
     */
    @PostConstruct
    public void init() throws ParseException {
        changes = new ArrayList<>();

        FhResource changesFile = ReflectionUtils.basePath(getClass()).resolve(CHANGES_RESOURCE_LOCATION);
        if (!changesFile.exists()) {
            return;
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String line;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(changesFile.getInputStream(), "UTF8"))) {
            int rows = 1;
            while ((line = reader.readLine()) != null) {
                if (!line.isEmpty() && !line.trim().startsWith("#")) {
                    String[] changeArray = line.split("\\|");

                    if (changeArray.length == 7) {
                        Change change = new Change(
                                changeArray[0],
                                changeArray[1],
                                simpleDateFormat.parse(changeArray[2]),
                                Type.valueOf(changeArray[3]),
                                changeArray[4],
                                changeArray[5].split(","),
                                changeArray[6]
                        );

                        changes.add(change);
                    } else {
                        FhLogger.error("Wrong data in change.csv file! Provided data is corrupted or un-parsable in row {}", rows);
                    }
                }

                rows++;
            }
        } catch (IOException exception) {
            FhLogger.error(exception);
        }
    }

    @Override
    public Page<Change> findAllBy(Type type, PageRequest pageable) {
        int startingPosition = pageable.getPageNumber() * pageable.getPageSize();
        List<Change> typedChangesCopy = new ArrayList<>(changes.stream().filter(change -> type.equals(change.getType())).collect(Collectors.toList()));
        List<Change> filteredTypedChanges = new LinkedList<>();

        if (pageable.getSort() != null && pageable.getSort().isSorted()) {
            Iterator<Sort.Order> orderIterator = pageable.getSort().iterator();
            Sort.Order order = orderIterator.next();

            if (order != null) {
                SortedProperty sortedProperty = SortedProperty.valueOf(order.getProperty());
                Collections.sort(typedChangesCopy, (first, second) -> sortedProperty.comparator.compare(first, second, order.getDirection()));
            }
        }

        for (int i = startingPosition; i < Integer.min(startingPosition + pageable.getPageSize(), typedChangesCopy.size()); i++) {
            filteredTypedChanges.add(typedChangesCopy.get(i));
        }

        return new PageImpl<>(filteredTypedChanges, pageable, typedChangesCopy.size());
    }

    @Override
    public List<Change> findAllBy(Type type, Date untilDate) {
        if (untilDate != null) {
            return changes.stream().filter(change -> type.equals(change.getType()) && change.getVersion().after(untilDate)).collect(Collectors.toList());
        } else {
            return changes.stream().filter(change -> type.equals(change.getType())).collect(Collectors.toList());
        }
    }

    @Override
    public void addChanges(List<Change> changesToAdd) {
        changes.addAll(changesToAdd);
        FhResource changesFile = ReflectionUtils.basePath(getClass()).resolve(CHANGES_RESOURCE_LOCATION);
        Path targetPath = changesFile.getExternalPath();
        try {
            Files.createDirectories(targetPath.getParent());
            Files.write(targetPath, appendChanges(changesToAdd).getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        } catch (IOException e) {
            FhLogger.error(e);
        }
    }

    private String appendChanges(List<Change> changes) {
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (Change change : changes) {
            sb.append(change.getTitle()).append("|")
                    .append(change.getDescription()).append("|")
                    .append(simpleDateFormat.format(change.getVersion())).append("|")
                    .append(change.getType()).append("|")
                    .append(change.getTicketUrl()).append("|");

            String separator = "";
            for (String author : change.getAuthors()) {
                sb.append(separator + author);
                separator = ",";
            }
            sb.append("|")
                    .append(change.getFunctionalArea())
                    .append("\n");
        }
        return sb.toString();
    }
}
