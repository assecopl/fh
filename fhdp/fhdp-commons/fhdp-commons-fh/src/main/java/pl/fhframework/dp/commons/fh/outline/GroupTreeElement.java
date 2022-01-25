package pl.fhframework.dp.commons.fh.outline;

import lombok.Getter;


@Getter
public class GroupTreeElement<T> extends TreeElement<T> {

    private Integer start;
    private Integer end;
    private String groupedObjectName;

    public GroupTreeElement(Integer start, Integer size, String groupedObjectName, T obj) {
        super(null, null, obj);
        this.start = start;
        this.end = start + size;
        this.groupedObjectName = groupedObjectName;
    }

    @Override
    public String getLabel() {
        return String.format("<%d, %d>", start, end);
    }

}
