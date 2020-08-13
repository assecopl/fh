package pl.fhframework.core.logging;

import java.util.Optional;

/**
 * Created by pawel.ruta on 2018-09-06.
 */
public interface ICodeRangeLogger {
    Optional<String> resolveCodeRangeMessage(Throwable exception);
}
