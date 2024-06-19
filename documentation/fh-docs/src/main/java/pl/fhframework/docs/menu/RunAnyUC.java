package pl.fhframework.docs.menu;

import pl.fhframework.core.uc.ICustomUseCase;
import pl.fhframework.core.uc.IUseCaseOutputCallback;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.url.UseCaseUrl;
import pl.fhframework.annotations.Action;

/**
 * Created by pawel.ruta on 2018-08-10.
 */
@UseCase
public class RunAnyUC implements ICustomUseCase {
    private A a;
    private B b;
    private C c;
    private int val;

    public RunAnyUC(A a, B b, ExitA exitA, ExitB exitB, C c, int val) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.val = val;
        registerCallback(ExitA.class, exitA);
        registerCallback(ExitB.class, exitB);
    }

    @Override
    public void start() {
        showForm(RunAnyForm.class, String.format("A with of(): %s, B with new(): %s, C with new(int): %s, val is int: %s", a, b, c, val));
    }

    @Action
    public void back() {
        UseCaseUrl useCaseUrl = new UseCaseUrl();
        useCaseUrl.setUseCaseAlias("docs-menu-app");
        getUserSession().getUseCaseContainer().runInitialUseCase(useCaseUrl);
    }

    @Action
    public void exitA1() {
        exit(ExitA.class).exitA1(A.of(), 5);
    }

    @Action
    public void exitA2() {
        exit(ExitA.class).exitA2();
    }

    @Action
    public void exitB1() {
        exit(ExitB.class).exitB1(new C(7));
    }

    @Action
    public void exitB2() {
        exit(ExitB.class).exitB2(3);
    }

    public static class A {
        public static A of() {
            return new A();
        }
    }

    public static class B {
    }

    public static class C {
        public C(int val) {
        }
    }

    public interface ExitA extends IUseCaseOutputCallback {
        void exitA1(A a, int i);

        void exitA2();
    }

    public interface ExitB extends IUseCaseOutputCallback {
        void exitB1(C c);

        void exitB2(int i);
    }
}
