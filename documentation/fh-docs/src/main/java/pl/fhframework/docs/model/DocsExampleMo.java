package pl.fhframework.docs.model;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.model.Model;

import javax.print.Doc;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by pawel.ruta on 2017-06-21.
 */
@Model
@Getter
@Setter
public class DocsExampleMo {
    private String stringField;

    private Date dateField;

    private Long longField;

    private Double doubleField;

    private BigDecimal bigDecimalField;

    private DocsExampleMo parent;

    private List<DocsExampleMo> childs = new LinkedList<>();

    public void addChild(DocsExampleMo child) {
        child.setParent(this);
        childs.add(child);
    }

    public void removeChild(DocsExampleMo child) {
        if (childs.remove(child)) {
            child.setParent(null);
        }
    }
}
