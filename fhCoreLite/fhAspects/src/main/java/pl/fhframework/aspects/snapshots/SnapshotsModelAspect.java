package pl.fhframework.aspects.snapshots;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.FieldSignature;
import org.aspectj.lang.reflect.MethodSignature;
import pl.fhframework.aspects.ApplicationContextHolder;

/**
 * @author Paweł Ruta
 */
@Aspect
public class SnapshotsModelAspect {
    private static final ThreadLocal<Boolean> disabled = new ThreadLocal<>();

    @Before("execution(pl.fhframework.aspects.snapshots.model.ISnapshotEnabled+.new(..)) && " +
            "!within(pl.fhframework.aspects.snapshots.SnapshotsModelAspect) && " +
            "!cflow(call(@pl.fhframework.aspects.snapshots.model.SkipSnapshot * *(..)))")
    public void objectPreInit(JoinPoint pointcut) throws Throwable {
        ModelListener sm = getModelListener();
        if (sm != null) {
            sm.onPreInitialization(pointcut.getTarget());
        }
    }

    @After("execution(pl.fhframework.aspects.snapshots.model.ISnapshotEnabled+.new(..)) && " +
            "!within(pl.fhframework.aspects.snapshots.SnapshotsModelAspect) && " +
            "!cflow(call(@pl.fhframework.aspects.snapshots.model.SkipSnapshot * *(..)))")
    public void objectInit(JoinPoint pointcut) throws Throwable {
        ModelListener sm = getModelListener();
        if (sm != null) {
            sm.onInitialization(pointcut.getTarget());
        }
    }

    @Around("get(* pl.fhframework.aspects.snapshots.model.ISnapshotEnabled+.*) && " +
            "!get(@pl.fhframework.aspects.snapshots.model.SkipSnapshot * *) && " +
            "!within(pl.fhframework.aspects.snapshots.SnapshotsModelAspect) && " +
            "!cflow(call(@pl.fhframework.aspects.snapshots.model.SkipSnapshot * *(..)))")
    public Object getFiledAspect(ProceedingJoinPoint pointcut) throws Throwable {
        try {
            if (isEnabled()) {
                if (pointcut.getSignature() instanceof FieldSignature) {
                    ModelListener sm = getModelListener();
                    if (sm != null) {
                        sm.gettingFieldCalled(pointcut.getTarget(),
                                ((FieldSignature) pointcut.getSignature()).getField(),
                                ((FieldSignature) pointcut.getSignature()).getFieldType());
                    }
                }
            }
            return pointcut.proceed();
        } catch (Exception e) {
            getModelListener().exceptionOnLazy(e);

            throw e;
        }
    }

    private boolean hasDynamicFeatures(Object target) {
        ModelListener sm = getModelListener();
        if (sm != null) {
            return sm.hasDynamicFeatures(target);
        }

        return false;
    }

    @Before("set(* pl.fhframework.aspects.snapshots.model.ISnapshotEnabled+.*) && " +
            "!set(@pl.fhframework.aspects.snapshots.model.SkipSnapshot * *) && " +
            "!within(pl.fhframework.aspects.snapshots.SnapshotsModelAspect) && " +
            "!cflow(call(@pl.fhframework.aspects.snapshots.model.SkipSnapshot * *(..)))")
    public void setFiledAspect(JoinPoint pointcut) throws Throwable {
        if (isEnabled()) {
            if (pointcut.getSignature() instanceof FieldSignature) {
                ModelListener sm = getModelListener();
                if (sm != null) {
                    sm.settingFieldCalled(pointcut.getTarget(),
                            ((FieldSignature) pointcut.getSignature()).getField(),
                            ((FieldSignature) pointcut.getSignature()).getFieldType(),
                            pointcut.getArgs()[0]);
                }
            }
        }
    }

    @Around("(execution(* pl.fhframework.core.model.BaseEntity+.get*()) || " +
            "execution(* pl.fhframework.core.model.BaseEntity+.is*())) && " +
            "!@annotation(pl.fhframework.aspects.snapshots.model.SkipSnapshot) && " +
            "!within(pl.fhframework.aspects.snapshots.SnapshotsModelAspect) && " +
            "!cflow(call(@pl.fhframework.aspects.snapshots.model.SkipSnapshot * *(..)))")
    public Object getMethodAspect(ProceedingJoinPoint pointcut) throws Throwable {
        if (pointcut.getTarget() == null) {
            return pointcut.proceed();
        }
        try {
            if (isEnabled()) {
                if (pointcut.getSignature() instanceof MethodSignature) {
                    ModelListener sm = getModelListener();
                    if (sm != null && sm.isDataModel(pointcut.getTarget())) {
                        return sm.getMethodCalled(pointcut.getTarget(),
                                ((MethodSignature) pointcut.getSignature()).getMethod(),
                                ((MethodSignature) pointcut.getSignature()).getReturnType());
                    }

                }
            }
            return pointcut.proceed();
        } catch (Exception e) {
            getModelListener().exceptionOnLazy(e);

            throw e;
        }
    }

    @Around("execution(* pl.fhframework.core.model.BaseEntity+.set*(..)) && " +
            "!@annotation(pl.fhframework.aspects.snapshots.model.SkipSnapshot) && " +
            "!within(pl.fhframework.aspects.snapshots.SnapshotsModelAspect) && " +
            "!cflow(call(@pl.fhframework.aspects.snapshots.model.SkipSnapshot * *(..)))")
    public void setMethodAspect(ProceedingJoinPoint pointcut) throws Throwable {
        if (pointcut.getTarget() == null) {
            pointcut.proceed();
            return;
        }

        if (isEnabled()) {
            if (pointcut.getSignature() instanceof MethodSignature) {
                ModelListener sm = getModelListener();
                if (sm != null && sm.isDataModel(pointcut.getTarget())) {
                    sm.setMethodCalled(pointcut.getTarget(),
                            ((MethodSignature) pointcut.getSignature()).getMethod(),
                            ((MethodSignature) pointcut.getSignature()).getMethod().getParameterTypes()[0],
                            pointcut.getArgs()[0]);

                    return;
                }
            }
        }
        pointcut.proceed();
    }

    @After("execution(pl.fhframework.core.model.BaseEntity+.new(..)) && " +
            "!within(pl.fhframework.aspects.snapshots.SnapshotsModelAspect) && " +
            "!cflow(call(@pl.fhframework.aspects.snapshots.model.SkipSnapshot * *(..)))")
    public void dynamicObjectInit(JoinPoint pointcut) throws Throwable {
        if (hasDynamicFeatures(pointcut.getTarget())) {
            if (pointcut.getSourceLocation().getWithinType().equals(pointcut.getTarget().getClass())) {
                ModelListener sm = getModelListener();
                if (sm != null) {
                    sm.newDynamicObjectInit(pointcut.getTarget());
                }
            }
        }
    }

    protected ModelListener getModelListener() {
        if (ApplicationContextHolder.getApplicationContext() != null) {
            ModelListenerFactory smf =
                    ApplicationContextHolder.getApplicationContext().getBean(ModelListenerFactory.class);
            if (smf != null) {
                return smf.getModelListener();
            }
        }

        return null;
    }

    public static void turnOff() {
        disabled.set(Boolean.TRUE);
    }

    public static void turnOn() {
        disabled.set(null);
    }

    private boolean isEnabled() {
        return !Boolean.TRUE.equals(disabled.get());
    }
}
