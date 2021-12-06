package pl.fhframework.compiler.core.uc.dynamic.model.element.behaviour;

import pl.fhframework.compiler.core.uc.dynamic.model.element.LinkTypeEnum;
import pl.fhframework.compiler.core.uc.dynamic.model.element.UseCaseElement;
import pl.fhframework.compiler.core.uc.dynamic.model.element.attribute.Parameter;
import pl.fhframework.core.uc.dynamic.model.element.behaviour.Identifiable;

import java.util.List;

/**
 * Created by pawel.ruta on 2017-03-29.
 */
public interface Linkable<T extends Parental> extends WithParameters<T>, Identifiable {
    String getSourceId();

    String getSourcePort();
    void setSourcePort(String sourcePort);

    String getTargetId();
    void setTargetId(String targetId);

    String getTargetPort();
    void setTargetPort(String targetPort);

    String getName();

    LinkTypeEnum getType();

    List<Parameter> getParameters();

    UseCaseElement getTarget();

    void setTarget(UseCaseElement target);

    String getVertices();

    void setVertices(String vertices);
}
