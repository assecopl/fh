package pl.fhframework.compiler.core.uc.dynamic.model.element;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Getter
@Setter
@NoArgsConstructor
@XmlRootElement(name = "RunSubUseCase")
@XmlAccessorType(XmlAccessType.FIELD)
@Deprecated
public class RunSubUseCase extends RunUseCase {
    @Override
    protected TransactionTypeEnum getDefaultTransactionType() {
        return TransactionTypeEnum.Current;
    }
}
