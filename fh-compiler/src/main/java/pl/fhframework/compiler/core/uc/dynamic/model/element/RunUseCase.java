package pl.fhframework.compiler.core.uc.dynamic.model.element;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.CollectionUtils;
import pl.fhframework.ReflectionUtils;
import pl.fhframework.compiler.core.uc.dynamic.model.element.behaviour.Child;
import pl.fhframework.compiler.core.uc.dynamic.model.element.behaviour.Parental;
import pl.fhframework.compiler.core.uc.dynamic.model.element.detail.UseCaseExit;

import javax.xml.bind.annotation.*;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@XmlRootElement(name = "RunUseCase")
@XmlAccessorType(XmlAccessType.FIELD)
public class RunUseCase extends UseCaseElement implements Parental {
    @XmlAttribute
    private String ref;

    @XmlAttribute
    private TransactionTypeEnum transactionType;

    @XmlElements({
            @XmlElement(name = "Exit", type = UseCaseExit.class)
    })
    private List<UseCaseExit> exits = new LinkedList<>();

    @XmlTransient
    private boolean dynamicRef;

    public void addExit(UseCaseExit exit) {
        exits.add(exit);
        exit.setParent(this);
    }

    @Override
    public UseCaseElement copy() {
        RunUseCase runUseCase = ReflectionUtils.createClassObject(this.getClass());
        if(!CollectionUtils.isEmpty(this.exits)){
            this.exits.forEach(exit -> runUseCase.addExit((UseCaseExit) exit.clone()));
        }
        runUseCase.label = this.label;
        runUseCase.description = this.description;
        runUseCase.id = this.id;
        runUseCase.posX = this.posX;
        runUseCase.posY = this.posY;
        runUseCase.size = this.size;
        runUseCase.transactionType = this.transactionType;

        return runUseCase;
    }

    @Override
    public boolean removeChild(Child child) {
        ((UseCaseExit) child).setParent(null);
        return exits.remove(child);
    }

    @Override
    public void addChild(Child child) {
        addExit((UseCaseExit) child);
    }

    public UseCaseExit getExit(String fromId) {
        return getExits().stream().filter(useCaseExit -> fromId.equals(useCaseExit.getFrom())).findFirst().orElse(null);
    }

    protected TransactionTypeEnum getDefaultTransactionType() {
        return TransactionTypeEnum.New;
    }

    public TransactionTypeEnum getTransactionType() {
        if (transactionType == null) {
            setTransactionType(getDefaultTransactionType());
        }
        return transactionType;
    }
}
