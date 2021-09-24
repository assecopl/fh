package pl.fhframework.usecases.dynamic;


import lombok.Getter;
import lombok.Setter;

import pl.fhframework.core.FhException;
import pl.fhframework.core.FhUseCaseException;
import pl.fhframework.XmlAttributeReader;

import java.util.*;

public class UseCaseProcess implements IUseCaseProcessElement {
    @Getter
    private String id;

    @Getter
    @Setter
    private String label;

    @Getter
    private String ref;

    @Getter
    private boolean dynamic;

    @Getter
    @Setter
    private String basicUseCaseId;
    @Getter
    @Setter
    private String image;
    @Getter
    @Setter
    private String description;

    @Getter
    @Setter
    private String dpuFilePath;

    @Getter
    @Setter
    private DynamicActionHandler.Start start;

    private HashMap<String, DynamicActionHandler.Action> actionIdToAction = new HashMap<>();
    private List<DynamicActionHandler.Action> _actionListCache;

    @Getter
    private List<DynamicDeclarationHandler.DeclareModel> declarations = new ArrayList<>();


    public UseCaseProcess(XmlAttributeReader xmlAttributeReader) {
        this.id = xmlAttributeReader.getAttributeValue("id");
        this.label = xmlAttributeReader.getAttributeValue("label");
        this.image = xmlAttributeReader.getAttributeValue("image");
        this.description = xmlAttributeReader.getAttributeValue("description");
        this.basicUseCaseId = xmlAttributeReader.getAttributeValue("basedOn");
        this.dynamic = true;
    }

    public String getPackage() {
        String id = getId();
        if (id == null || id.isEmpty()) {
            return "";
        }
        int dotIndex = id.lastIndexOf('.');
        if (dotIndex < 0) {
            return "";
        } else {
            return id.substring(0, dotIndex);
        }
    }

    public String getName() {
        String id = getId();
        if (id == null || id.isEmpty()) {
            return "";
        }
        int dotIndex = id.lastIndexOf('.');
        if (dotIndex < 0) {
            return id;
        } else {
            if (dotIndex == id.length() - 1)
                throw new FhUseCaseException("Incorrect sub use case identifier");
            return id.substring(dotIndex + 1);
        }
    }



    public String getXml() {
        throw new FhException("Not implemented");
    }

    public void addAction(DynamicActionHandler.Action action) {
        actionIdToAction.put(action.getId(), action);
        _actionListCache = null;
    }

    public void removeAction(DynamicActionHandler.Action action) {
        actionIdToAction.remove(action.getId());
        _actionListCache = null;
    }

    public List<DynamicActionHandler.Action> getActions() {
        if (_actionListCache == null) {
            _actionListCache = Collections.unmodifiableList(new ArrayList<>(actionIdToAction.values()));

        }
        return _actionListCache;
    }

    public boolean isInitial(){
        return start!=null && start.getRequiredParameters().isEmpty();
    }

    public DynamicActionHandler.Action getAction(String id) {
        return actionIdToAction.get(id);
    }

    public void addModelDeclaration(DynamicDeclarationHandler.DeclareModel declaration) {
        this.declarations.add(declaration);
    }
}
