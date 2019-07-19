package pl.fhframework.trees;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;
import pl.fhframework.core.cloud.config.ExposedMenuElement;
import pl.fhframework.core.cloud.config.ExposedUseCaseDefinition;
import pl.fhframework.core.cloud.event.LocalServerDefinitionChangedEvent;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.security.AuthorizationManager;
import pl.fhframework.model.security.SystemUser;
import pl.fhframework.subsystems.Subsystem;
import pl.fhframework.subsystems.SubsystemManager;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class SubsystemElementsLiteTree extends SubsystemElementsTree {

    protected List<ITreeElement> _allTreeElements = new ArrayList<>();
    protected List<ITreeElement> _allTreeElementsView = Collections.unmodifiableList(_allTreeElements);
    protected List<TreeRoot> treeRoots = new ArrayList<>();

    @Autowired
    protected AuthorizationManager authorizationManager;
    @Autowired
    protected ApplicationEventPublisher eventPublisher;

    protected SubsystemElementsLiteTree() {
        _allTreeElements.addAll(getStaticGroupsAndSubsystemsHierarchy(this.getClass()));
        treeRoots.addAll(getDynamicGroupsAndSubsystemsHierarchy());
        _allTreeElements.addAll(mergeRoots(treeRoots));

    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        getAllTreeElements();
        readAllowedSystemFunctions();
    }

    protected void readAllowedSystemFunctions() {
        treeRoots.forEach(
                root -> readAllowedSystemFunctions(root.getSubelements())
        );
    }

    /**
     * Reads and refreshes information about allowed system function and system roles for use cases from tree.
     * If useCaseName parameter is given than only that use case information is refreshed.
     * If useCaseName parameter is null than all use cases are refreshed.
     * Method works recursively.
     * @param treeElements tree elements
     */
    protected void readAllowedSystemFunctions(List<ITreeElement> treeElements) {
        for (ITreeElement treeElement : treeElements) {
            if (treeElement instanceof UseCaseInformation) {
                setAllowedSystemFunctionsFor((UseCaseInformation) treeElement);
                setAllowedSystemRolesFor((UseCaseInformation) treeElement);
            } else {
                readAllowedSystemFunctions(treeElement.getSubelements());
            }
        }
    }

    protected void setAllowedSystemFunctionsFor(UseCaseInformation useCaseInformation) {
        try {
            Class clazz = Class.forName(useCaseInformation.getRef());
            if (clazz != null) {
                useCaseInformation.setAllowedSystemFunctions(
                        UseCaseInformation.getAllowedSystemFunctions(clazz)
                );
            }
        } catch (ClassNotFoundException e) {
            FhLogger.error(e);
        }
    }

    protected void setAllowedSystemRolesFor(UseCaseInformation useCaseInformation) {
        try {
            Class clazz = Class.forName(useCaseInformation.getRef());
            if (clazz != null) {
                useCaseInformation.setAllowedSystemRoles(
                        UseCaseInformation.getAllowedSystemRoles(clazz)
                );
            }
        } catch (ClassNotFoundException e) {
            // FhLogger.error(e);
        }
    }

    public void aktualizujHierarchieDynamicznychGrupIPodsystemow() {
        synchronized (this) {
            _allTreeElements.clear();
            _allTreeElements.addAll(getStaticGroupsAndSubsystemsHierarchy(this.getClass()));
            treeRoots = getDynamicGroupsAndSubsystemsHierarchy();
            _allTreeElements.addAll(mergeRoots(treeRoots));
            readAllowedSystemFunctions();
        }
    }

    public List<ITreeElement> getAllTreeElements() {
        if (requiresRefresh()) {
            aktualizujHierarchieDynamicznychGrupIPodsystemow();
        }
        synchronized (this) {//TODO: Reads can run in parallel, but synchronization is only during dynamic elements update
            return _allTreeElementsView;
        }
    }

    public List<ITreeElement> getAllAvailableTreeElements(SystemUser systemUser, List<String> activeProfiles) {
        return filterAvailableTreeElements(getAllTreeElements(), systemUser, activeProfiles);
    }

    public void updateCloudServerMenu(String cloudServerName, AtomicBoolean activityToken, List<ExposedMenuElement> newRootElements, List<ExposedUseCaseDefinition> serverUseCases) {
        synchronized (this) {
            // removing all server's elements from root
            _allTreeElements.removeIf(elem -> cloudServerName.equals(elem.getCloudServerName()));
            // adding new server's elements
            _allTreeElements.addAll(DynamicUseCasesGroup.newCloudInstances(cloudServerName, activityToken, newRootElements, serverUseCases));
        }
    }

    protected List<ITreeElement> filterAvailableTreeElements(List<ITreeElement> allTreeElements, SystemUser systemUser, List<String> activeProfiles) {
        List<ITreeElement> result = new ArrayList<>();
        for (ITreeElement element : allTreeElements) {
            if (element instanceof UseCaseInformation) {
                if (((UseCaseInformation) element).availableFor(authorizationManager, systemUser, activeProfiles)) {
                    result.add(element);
                }
            } else if (element instanceof DynamicUseCasesGroup) {
                DynamicUseCasesGroup current = (DynamicUseCasesGroup) element;
                List<ITreeElement> allSubelements = current.getSubelements();
                if (current.isAvailableForMode(activeProfiles)) {
                    List<ITreeElement> available = filterAvailableTreeElements(allSubelements, systemUser, activeProfiles);
                    if (available.size() > 0) {
                        DynamicUseCasesGroup usdg = new DynamicUseCasesGroup(current.label, current.getDescription(), current.getIcon(), current.getPosition(), current.getModes(), available, current.getCloudServerName(), current.getActivityToken());
                        result.add(usdg);
                    }
                }
            }
        }
        return result;
    }

    protected boolean requiresRefresh() {
        for (Subsystem subsystem : SubsystemManager.getSubsystemInfos()) {
            if (subsystem.requiresUpdate()) {
                return true;
            }
        }

        for (TreeRoot treeRoot : treeRoots) {
            if (!treeRoot.getSubsystem().isStatic() && requiresUpdate(treeRoot)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void exportMenu(Subsystem subsystem, String xml) {
        super.exportMenu(subsystem, xml);
        //update ourself menu structure, for cloud usage
        aktualizujHierarchieDynamicznychGrupIPodsystemow();
        eventPublisher.publishEvent(new LocalServerDefinitionChangedEvent(subsystem));
    }

    protected Optional<UseCaseInformation> findUseCaseInformation(List<ITreeElement> treeElements, String useCaseName) { // TODO
        for (ITreeElement treeElement : treeElements) {
            if (treeElement instanceof UseCaseInformation && treeElement.getRef().equals(useCaseName)) {
                return Optional.of(((UseCaseInformation) treeElement));
            } else {
                Optional<UseCaseInformation> result = findUseCaseInformation(treeElement.getSubelements(), useCaseName);
                if (result.isPresent()) {
                    return result;
                }
            }
        }
        return Optional.empty();
    }

}
