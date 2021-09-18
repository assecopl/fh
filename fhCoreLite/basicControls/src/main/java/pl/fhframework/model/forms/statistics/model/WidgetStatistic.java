package pl.fhframework.model.forms.statistics.model;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.model.BaseEntity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Adam Zareba on 18.01.2017.
 */
@Entity
@Table(name = "WIDGET_STATISTIC")
public class WidgetStatistic implements Serializable, BaseEntity<Long> {

    @Id
    @Column(name = "ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Long id;

    @Column(name = "NAME", nullable = false, unique = true)
    @Getter
    @Setter
    private String name;

    @Column(name = "COUNT", nullable = false)
    @Getter
    @Setter
    private Integer count;

    @Override
    public Long getEntityId() {
        return getId();
    }

    @Override
    public void setEntityId(Long id) {
        setId(id);
    }
}
