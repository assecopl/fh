package pl.fhframework.dp.commons.model.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "fhdp_search_template")
@Getter
@Setter
public class SearchTemplate extends BasePersistentObject {
    @Column(name = "template_name")
    private String templateName;
    @Column(name = "user_name")
    private String userName;
    @Column(name = "component_name")
    private String componentName;
    @Column(name = "template_definition_json", length = 4000)
    private String templateDefinitionJson;

}

