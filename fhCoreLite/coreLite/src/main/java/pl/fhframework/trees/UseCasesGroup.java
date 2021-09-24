package pl.fhframework.trees;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public abstract class UseCasesGroup implements IGroupingTreeElement {

//    @Getter
//    private String id;
    @Getter
    @Setter
    String label;
    @Getter
    @Setter(AccessLevel.PACKAGE)
    private String containerId;
    //@Getter
    //private Class<? extends IGroupingTreeElement> kontener;
    @Getter
    @Setter
    private String description;
    @Getter
    @Setter
    private String icon;
    @Getter
    @Setter
    private int position;
    @Getter
    private String cloudServerName;
    @Getter
    private boolean active = true;

    @Getter
    private AtomicBoolean activityToken;

    @Getter
    @Setter
    @JsonIgnore
    private List<String> modes;



    //private List<ITreeElement> _subElements = new ArrayList<>();
    @Getter
    private List<ITreeElement> subelements = new ArrayList<>();

    public UseCasesGroup(String label, Class<? extends IGroupingTreeElement> container, String description, String icon, int position, String mode) {
//        this.id = this.getClass().getName();
        this.label = label;
        if (container!=null){
            this.containerId = container.getName();
        }
        //this.container = container;
        this.description = description;
        this.icon = icon;
        this.position = position;
        if(mode != null) {
            this.modes = Arrays.asList(mode.split(","));
        }
        this.activityToken = new AtomicBoolean(true);
    }

    public UseCasesGroup(String label, Class<? extends IGroupingTreeElement> container, String description, String icon, int position, List<String> modes) {
//        this.id = this.getClass().getName();
        this.label = label;
        if (container!=null){
            this.containerId = container.getName();
        }
        //this.container = container;
        this.description = description;
        this.icon = icon;
        this.position = position;
        this.modes = modes;
        this.activityToken = new AtomicBoolean(true);
    }

    public UseCasesGroup(String label, Class<? extends IGroupingTreeElement> container, String description, String icon, int position, String mode, List<ITreeElement> subElements) {
        this(label, container, description, icon, position, mode);
        subelements.addAll(subElements);
    }

    public UseCasesGroup(String label, Class<? extends IGroupingTreeElement> container, String description, String icon, int position, List<String> modes, List<ITreeElement> subElements, String cloudServerName, AtomicBoolean activityToken) {
        this(label, container, description, icon, position, modes);
        subelements.addAll(subElements);
        this.cloudServerName = cloudServerName;
        this.activityToken = activityToken;
    }


    @Override
    public void addSubelement(ITreeElement newElement) {
        subelements.add(newElement);
    }

    @Override
    public void sortSubelements(Comparator<ITreeElement> comparer) {
        subelements.sort(comparer);
    }

    @Override
    public String getRef() {
        return null;
    }

    @Override
    public void setRef(String ref) {
        //empty
    }

    @Override
    public boolean isDynamic() {
        return false;
    }

    @Override
    public boolean isLeaf() {
        return false;
    }
}
