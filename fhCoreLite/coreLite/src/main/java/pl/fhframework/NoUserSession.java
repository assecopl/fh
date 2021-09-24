package pl.fhframework;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Getter
@Setter
@org.springframework.stereotype.Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class NoUserSession extends Session {
    public NoUserSession(SessionDescription description) {
        super(description);
    }

    @Override
    public boolean isUserContext() {
        return false;
    }
}
