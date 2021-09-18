package pl.fhframework.core.rules.dynamic.model.dataaccess;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort;
import pl.fhframework.aspects.snapshots.model.SkipSnapshot;
import pl.fhframework.core.rules.dynamic.blockly.BlocklyBlock;
import pl.fhframework.core.rules.dynamic.model.RuleElement;
import pl.fhframework.core.rules.dynamic.model.Statement;
import pl.fhframework.core.rules.dynamic.model.StatementWithList;
import pl.fhframework.core.rules.service.RuleConsts;

import javax.xml.bind.annotation.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

@Getter
@Setter
@XmlRootElement(name = "SortBy", namespace = RuleConsts.RULE_XSD)
@XmlAccessorType(XmlAccessType.FIELD)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class SortBy extends StatementWithList implements DataAccess {
    @XmlTransient
    @JsonIgnore
    public static final String TAG_NAME = "SortByBlock";

    @XmlElementRef
    private List<Statement> statements = new LinkedList<>();

    @SkipSnapshot
    @XmlTransient
    @JsonIgnore
    private String sortOrderExp;

    @Override
    public BlocklyBlock convertToBlockly(Function<String, String> formatter) {
        BlocklyBlock block = new BlocklyBlock();
        block.setId(this.getOrGenerateId());
        block.setType(SortBy.TAG_NAME);
        block.setX(this.getX());
        block.setY(this.getY());

        addStatements(block, formatter);

        return block;
    }

    public static RuleElement convertFromBlockly(BlocklyBlock parsedBlock) {
        SortBy sortBy = new SortBy();
        sortBy.setId(parsedBlock.getId());
        sortBy.setX(parsedBlock.getX());
        sortBy.setY(parsedBlock.getY());

        return sortBy;
    }

    @Override
    public void processValueChange(String name, String value) {
    }

    public static SortBy of(SortField... sortFields) {
        SortBy sortBy = new SortBy();
        sortBy.getStatements().addAll(Arrays.asList(sortFields));

        return sortBy;
    }

    public static SortBy of(Sort sort, String iter, SortBy defaultSort) {
        if (sort == null) {
            return defaultSort;
        }

        SortBy sortBy = new SortBy();

        sort.forEach(order -> {
            for (String property : order.getProperty().split(",")) {
                sortBy.getStatements().add(
                        SortField.of(String.format("%s.%s", iter, property.trim()), order.getDirection() == Sort.Direction.ASC ?
                                SortDirectionEnum.Asc.getDirection() :
                                SortDirectionEnum.Desc.getDirection()));
            }
        });

        if (sortBy.getStatements().size() == 0) {
            return defaultSort;
        }

        return sortBy;
    }
}