package pl.fhframework.core.logging;

import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Created by pawel.ruta on 2018-03-01.
 */
@Service
public class CodeRangeLoggerLite implements ICodeRangeLogger {

    public Optional<String> resolveCodeRangeMessage(Throwable exception) {
        return Optional.empty();
    }
}
