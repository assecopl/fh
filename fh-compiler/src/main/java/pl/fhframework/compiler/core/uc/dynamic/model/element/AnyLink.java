package pl.fhframework.compiler.core.uc.dynamic.model.element;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.aspects.snapshots.model.ISnapshotEnabled;
import pl.fhframework.compiler.core.uc.dynamic.model.element.attribute.Parameter;
import pl.fhframework.compiler.core.uc.dynamic.model.element.behaviour.Linkable;
import pl.fhframework.compiler.core.uc.dynamic.model.element.behaviour.Parental;
import pl.fhframework.compiler.core.uc.dynamic.model.element.command.ActivityTypeEnum;

import java.util.List;

/**
 * Created by pawel.ruta on 2017-04-06.
 */
@Getter
@Setter
@AllArgsConstructor
public class AnyLink implements Linkable<Parental>, ISnapshotEnabled {
    private String id;

    private String sourceId;

    private String sourcePort;

    private String targetId;

    private String targetPort;

    private String label;

    private LinkTypeEnum linkType;

    public AnyLink(String id, String sourceId, String sourcePort, String targetId, String targetPort, String label) {
        this.id = id;
        this.sourceId = sourceId;
        this.sourcePort = sourcePort;
        this.targetId = targetId;
        this.targetPort = targetPort;
        this.label = label;
    }



    @Override
    public Parental getParent() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setParent(Parental parent) {
        throw new UnsupportedOperationException();
    }

    @Override
    public LinkTypeEnum getType() {
        return linkType;
    }

    @Override
    public List<Parameter> getParameters() {
        return null;
    }

    @Override
    public ActivityTypeEnum getActivityType() {
        return null;
    }

    @Override
    public String getTargetName() {
        return null;
    }

    @Override
    public String getCondition() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setCondition(String string) {
        throw new UnsupportedOperationException();
    }

    @Override
    public UseCaseElement getTarget() {
        return null;
    }

    @Override
    public void setTarget(UseCaseElement target) {

    }

    @Override
    public String getVertices() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setVertices(String vertices) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getName() {
        return label;
    }
}
