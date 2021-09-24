package pl.fhframework.trees;


import pl.fhframework.core.cloud.config.ExposedMenuElement;
import pl.fhframework.core.cloud.config.ExposedUseCaseDefinition;
import pl.fhframework.XmlAttributeReader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class DynamicUseCasesGroup extends UseCasesGroup {

    public DynamicUseCasesGroup() {
        super(null, null, null, null, 0, null, new ArrayList<>());
        this.setContainerId(this.getClass().getName());
    }

    DynamicUseCasesGroup(XmlAttributeReader xmlAttributeReader) {
        super(xmlAttributeReader.getAttributeValue("label"), null, xmlAttributeReader.getAttributeValue("description"), xmlAttributeReader.getAttributeValue("icon"), xmlAttributeReader.getNumberOrDefault("coords", 0), xmlAttributeReader.getAttributeValue("mode"));
        this.setContainerId(this.getClass().getName());
    }

    DynamicUseCasesGroup(String label, String description, String image, int position, String mode, List<ITreeElement> subElements){
        super(label, null, description, image, position, mode, subElements);
        this.setContainerId(this.getClass().getName());
    }

    DynamicUseCasesGroup(String label, String description, String image, int position, List<String> modes, List<ITreeElement> subElements, String cloudServerName, AtomicBoolean activityToken){
        super(label, null, description, image, position, modes, subElements, cloudServerName, activityToken);
        this.setContainerId(this.getClass().getName());
    }

    public static DynamicUseCasesGroup newCloudInstance(String cloudServerName, AtomicBoolean activityToken, ExposedMenuElement menuElement, List<ExposedUseCaseDefinition> serverUseCases) {
        List<ITreeElement> subelements = newCloudInstances(cloudServerName, activityToken, menuElement.getSubelements(), serverUseCases);

        return new DynamicUseCasesGroup(menuElement.getLabel(), null, null, 0, Collections.emptyList(), subelements, cloudServerName, activityToken);
    }

    public static List<ITreeElement> newCloudInstances(String cloudServerName, AtomicBoolean activityToken, List<ExposedMenuElement> menuElements, List<ExposedUseCaseDefinition> serverUseCases) {
        return menuElements.stream().map(subelement -> {
            if (subelement.getType() == ExposedMenuElement.ExposedMenuElementType.USE_CASE) {
                return UseCaseInformation.newCloudInstance(cloudServerName, activityToken, subelement, serverUseCases);
            } else {
                return DynamicUseCasesGroup.newCloudInstance(cloudServerName, activityToken, subelement, serverUseCases);
            }
        }).collect(Collectors.toList());
    }

    @Override
    public boolean isDynamic() {
        return true;
    }
}
