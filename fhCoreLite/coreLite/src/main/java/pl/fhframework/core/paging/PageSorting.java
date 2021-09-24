package pl.fhframework.core.paging;

import com.fasterxml.jackson.annotation.JsonProperty;

import static org.springframework.data.domain.Sort.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PageSorting {

    private String sortBy;
    private Direction direction;
    private Integer currentPage;
    private Integer pageSize;

    public PageSorting(@JsonProperty(value = "currentPage") String currentPage, @JsonProperty(value = "pageSize") String pageSize, @JsonProperty(value = "sortBy") String sortBy, @JsonProperty(value = "direction") String direction) {
        this.currentPage = currentPage != null ? Integer.valueOf(currentPage): null;
        this.pageSize = pageSize != null ? Integer.valueOf(pageSize) : null;
        this.sortBy = sortBy;
        this.direction = direction != null ? Direction.valueOf(direction) : null;
    }
}
