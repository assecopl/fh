package pl.fhframework.model.forms.widgets.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.model.BaseEntity;

@Entity
@Table(name = "WIDGET_INFO")
@Getter
@Setter
public class WidgetInfo implements Serializable, BaseEntity<Long> {

    @Id
    @Column(name = "ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "REFERENCE", nullable = false)
    private String reference;

    @Column(name = "USER_ID", nullable = false)
    private long userId;

    @Column(name = "POS_X", nullable = false)
    private int posX;

    @Column(name = "POS_Y", nullable = false)
    private int posY;

    @Column(name = "SIZE_X", nullable = false)
    private int sizeX;

    @Column(name = "SIZE_Y", nullable = false)
    private int sizeY;

    @Override
    public Long getEntityId() {
        return getId();
    }

    @Override
    public void setEntityId(Long id) {
        setId(id);
    }
}