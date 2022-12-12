package pl.fhframework.core.i18n;

import org.springframework.context.MessageSource;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MessagesSourceComparator implements Comparator<MessageSource> {
    private final List<MessageSource> messagesSourcesInOriginalOrder;

    public MessagesSourceComparator(List<MessageSource> messagesSourcesInOriginalOrder) {
        this.messagesSourcesInOriginalOrder = messagesSourcesInOriginalOrder;
    }

    @Override
    @SuppressWarnings("unchecked")
    public int compare(MessageSource o1, MessageSource o2) {
        int result = 0;
        if (o1 instanceof Comparable<?>) {
            result = ((Comparable<MessageSource>) o1).compareTo(o2);
        } else if (o2 instanceof Comparable<?>) {
            result = -((Comparable<MessageSource>) o2).compareTo(o1);
        }

        if (result == 0) {
            return messagesSourcesInOriginalOrder.indexOf(o1) - messagesSourcesInOriginalOrder.indexOf(o2);
        } else {
            return result;
        }
    }

    public static List<MessageSource> getSortedMessages(List<MessageSource> messageSourcesToSort) {
        Comparator<MessageSource> comparator = new MessagesSourceComparator(messageSourcesToSort);

        return messageSourcesToSort.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }
}
