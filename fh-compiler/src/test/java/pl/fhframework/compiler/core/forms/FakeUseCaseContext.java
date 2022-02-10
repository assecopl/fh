package pl.fhframework.compiler.core.forms;

import pl.fhframework.UserSession;
import pl.fhframework.binding.IActionCallback;
import pl.fhframework.core.uc.IFormUseCaseContext;
import pl.fhframework.core.uc.IUseCase;
import pl.fhframework.core.uc.IUseCaseOutputCallback;

import java.lang.reflect.Method;

public class FakeUseCaseContext implements IFormUseCaseContext {

    @Override
    public boolean isSystemUseCase() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isTopStackUseCase() {
        throw new UnsupportedOperationException();
    }

    @Override
    public UserSession getUserSession() {
        return null;
    }

    @Override
    public void runAction(String actionName, String formTypeId, Object... attributesValue) {
    }

    @Override
    public boolean runAction(Method method, Object methodTarget, String actionName, Object... attributesValue) {
        return false;
    }

    @Override
    public void runAction(IActionCallback callback) {
    }

    @Override
    public IUseCase<?> getUseCase() {
        return new IUseCase<IUseCaseOutputCallback>() {};
    }

    @Override
    public String getRemoteServerName() {
        return null;
    }
}
