package pl.fhframework.core.i18n;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

public class PrioritizedMessageSource extends ReloadableResourceBundleMessageSource implements Comparable<MessageSource> {
    /**
     * Message source with higher priority will be computed before message with lower priority value.
     * I.E. Message with priority 5 will be computed before message with priority -1 because 5 > -1
     */
    @Getter
    @Setter
    private int priority;

    @Override
    public int compareTo(MessageSource other) {
        if (other instanceof PrioritizedMessageSource) {
            return ((PrioritizedMessageSource) other).getPriority() - this.priority;
        } else if (other instanceof Comparable<?>) {
            return 0;
        } else {
            return -this.priority;
        }
    }
}
