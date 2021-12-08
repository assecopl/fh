package pl.fhframework.dp.commons.fh.messages;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import pl.fhframework.dp.commons.fh.outline.TreeElement;
import pl.fhframework.dp.transport.enums.MessageDirectionEnum;
import pl.fhframework.dp.transport.msg.MessageDto;
import pl.fhframework.core.i18n.MessageService;
import pl.fhframework.core.services.FhService;
import pl.fhframework.model.forms.GroupingComponent;
import pl.fhframework.model.forms.TreeElementFhDP;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@FhService
public class MessageSearchOutlineService {

    @Autowired
    private MessageService messageService;

    @Value("${fhdp.direction:true}")
    public boolean isDirection;

    public MessageSelectedOutline buildMessagesTreeMenu(List<MessageDto> messageDtoList, String repositoryId) {
        List<TreeElement<MessageDto>> leftMenuElements = new ArrayList<>();

        List<TreeElement<MessageDto>> mainNodes = new ArrayList<>();

        TreeElement<MessageDto> selectedTreeElement = null;

        if(messageDtoList != null) {
            for (MessageDto message : messageDtoList) {
                if(message.getMetadata().getResponseTo() == null) {
                    TreeElement<MessageDto> newTreeElement = new TreeElement<>(
                        generateMessageTreeElementName(message), generateMessageTreeElementIcon(message), message);
                    if(message.getRepositoryId().equals(repositoryId)){
                        selectedTreeElement = newTreeElement;
                    }
                    mainNodes.add(newTreeElement);
                }
            }

            for(TreeElement<MessageDto> mainNode: mainNodes) {
                leftMenuElements.add(buildMessageTree(mainNode, messageDtoList));
            }
        }

        return new MessageSelectedOutline(leftMenuElements, selectedTreeElement);
    }

    TreeElement<MessageDto> buildMessageTree(TreeElement<MessageDto> root, List<MessageDto> fullList) {

        List<MessageDto> currentNodeChildren = new ArrayList<>();

        for (MessageDto message : fullList) {
            if(root.getObj().getId().equals(message.getMetadata().getResponseTo())) {
                currentNodeChildren.add(message);
            }
        }

        if(currentNodeChildren.size() == 0) {
            return root;
        } else {
            for (MessageDto currentChild : currentNodeChildren) {
                root.addChild(buildMessageTree(new TreeElement<>(
                    generateMessageTreeElementName(currentChild),
                    generateMessageTreeElementIcon(currentChild), currentChild), fullList));
            }
        }
        return root;
    }

    public String generateMessageTreeElementName(MessageDto message) {

        if(message == null) {
            return null;
        }

        String treeElementName = message.getName();
        String direction = null;

        if(message.getDirection() != null && MessageDirectionEnum.OUT.equals(message.getDirection())) {
            return String.format("%s  %s", treeElementName, "[className='ml-1'][icon='fas fa-sign-out-alt'][/className]");
        }

        return String.format("%s %s", "[icon='fas fa-sign-in-alt']", treeElementName);
    }

    private String generateMessageTreeElementIcon(MessageDto message) {
        if(message == null) {
            return null;
        }

        if(message.getDirection() != null) {
            switch (message.getDirection()) {
                case OUT: {
                    return "fas fa-sign-out-alt";
                }
                case IN: {
                    return "fas fa-sign-in-alt";
                }
                default:
                    return "fas fa-sign-in-alt";
            }
        } else return null;
    }

    public static TreeElementFhDP findTreeElementByLabel(GroupingComponent<TreeElementFhDP> tree, String label) {
        TreeElementFhDP parent;

        for (TreeElementFhDP el : tree.getSubcomponents()) {
            if (el.getLabel().equals(label)) {
                parent = el;
            } else {
                parent = findTreeElementByLabel(el, label);
            }
            if (parent != null) {
                return parent;
            }
        }
        return null;
    }
}
