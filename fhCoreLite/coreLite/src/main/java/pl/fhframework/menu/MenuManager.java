package pl.fhframework.menu;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import pl.fhframework.core.i18n.MessageService;
import pl.fhframework.model.security.SystemUser;
import pl.fhframework.subsystems.Subsystem;
import pl.fhframework.trees.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by mateusz.zaremba
 */
@Service
public class MenuManager {

    @Autowired
    private Environment environment;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private MessageService messageService;

    @Autowired
    private SubsystemElementsLiteTree subsystemElementsBaseTree;

    public List<MenuElement> getMenuModel(SystemUser loggedUser) {
        List<ITreeElement> treeElements = subsystemElementsBaseTree.getAllAvailableTreeElements(loggedUser, Arrays.asList(environment.getActiveProfiles()));
        return createSubMenuElementsList(treeElements);
    }

    public ITreeElement getSubsystemMenuModel(Subsystem subsystem) {
        return SubsystemElementsLiteTree.getDynamicGroupsAndSubsystemsHierarchy(subsystem);
    }

    protected List<MenuElement> createSubMenuElementsList(List<ITreeElement> elements) {
        List<MenuElement> menuElements = new LinkedList<>();
        for (ITreeElement element : elements) {
            MenuElement newElement;
            if (element instanceof UseCasesGroup) {
                newElement = new MenuElement(translateLabel(element.getLabel()), element.getRef(), element.getIcon(), true, element.getActivityToken(), null);
                newElement.setChildren(createSubMenuElementsList(element.getSubelements()));
            } else {
                newElement = new MenuElement(translateLabel(element.getLabel()), element.getRef(), element.getIcon(), false, element.getActivityToken(), ((UseCaseInformation) element).getInputFactory());
            }
            menuElements.add(newElement);
        }
        return menuElements;
    }

    public void export(Subsystem subsystem, ITreeElement root) {
        subsystemElementsBaseTree.exportMenu(subsystem, new TreeElementXmlUtil().convertToXml(root, true));
    }

    public String convertTreeToXml(ITreeElement root, boolean pretty) {
        return new TreeElementXmlUtil().convertToXml(root, pretty);
    }



    protected String translateLabel(String label) {
        if (label != null && label.length() > 2 && label.startsWith("$.")) {
            return messageService.getAllBundles().getMessage(label.substring(2));
        } else {
            return label;
        }
    }
}