package pl.fhframework.dp.transport.searchtemplate;


import pl.fhframework.dp.transport.dto.commons.BaseDtoQuery;

public class SearchTemplateDtoQuery extends BaseDtoQuery {

    private Long id;
    private String templateName;
    private String userName;
    private String componentName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }
}
