package pl.fhframework.dp.commons.fh.messages;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.dp.commons.fh.outline.TreeElement;
import pl.fhframework.dp.transport.msg.MessageDto;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class MessageSelectedOutline {

    List<TreeElement<MessageDto>> leftMenuElements = new ArrayList<>();
    TreeElement<MessageDto> selectedTreeElement;

    public MessageSelectedOutline(List<TreeElement<MessageDto>> leftMenuElements, TreeElement<MessageDto> selectedTreeElement) {
        this.leftMenuElements = leftMenuElements;
        this.selectedTreeElement = selectedTreeElement;
    }
}
