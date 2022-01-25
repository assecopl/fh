 package pl.fhframework.compiler.core.uc.dynamic.model.element;

 import lombok.Getter;
 import lombok.NoArgsConstructor;
 import lombok.Setter;
 import pl.fhframework.ReflectionUtils;
 import pl.fhframework.compiler.core.uc.dynamic.model.element.detail.QueryInput;

 import javax.xml.bind.annotation.*;

 @Getter
@Setter
@NoArgsConstructor
@XmlRootElement(name = "StoreAccess")
@XmlAccessorType(XmlAccessType.FIELD)
public class StoreAccess extends UseCaseElement {
    @XmlAttribute
    private String select;

    @XmlElement(name = "Query", type = QueryInput.class)
    protected QueryInput query = new QueryInput();

    public String getQueryClause() {
        return query.getQuery();
    }

    public void setQueryClause(String query) {
        this.query.setQuery(query);
    }

    @Override
    public UseCaseElement copy() {
        StoreAccess storeAccess = ReflectionUtils.createClassObject(this.getClass());
        storeAccess.select = this.select;
        storeAccess.query = (QueryInput) this.query.clone();
        storeAccess.label = this.label;
        storeAccess.description = this.description;
        storeAccess.id = this.id;
        storeAccess.posX = this.posX;
        storeAccess.posY = this.posY;
        return storeAccess;
    }
}
