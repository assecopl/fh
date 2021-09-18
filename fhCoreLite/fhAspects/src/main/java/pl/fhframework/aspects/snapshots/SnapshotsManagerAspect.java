package pl.fhframework.aspects.snapshots;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Paweł Ruta
 */
//@Aspect
//@Component
public class SnapshotsManagerAspect {
    @Autowired
    SnapshotsManager snapshotManager;

//    @Around("@within(pl.fhframework.model.bazowe.snapshots.CreateSnapshot) || @annotation(pl.fhframework.model.bazowe.snapshots" +
//            ".CreateSnapshot)")
    public Object createSnapshotAspect(ProceedingJoinPoint pointcut) throws Throwable {
        snapshotManager.createSnapshot(pointcut.getTarget().getClass().getName());

        return pointcut.proceed();
    }

//    @Around("@within(pl.fhframework.model.bazowe.snapshots.DropSnapshot) || @annotation(pl.fhframework.model.bazowe.snapshots" +
//            ".DropSnapshot)")
    public Object dropSnapshotAspect(ProceedingJoinPoint pointcut) throws Throwable {
        snapshotManager.dropSnapshot(pointcut.getTarget().getClass().getName());

        return pointcut.proceed();
    }

//    @Around("@within(pl.fhframework.model.bazowe.snapshots.RestoreSnapshot) || @annotation(pl.fhframework.model.bazowe.snapshots" +
//            ".RestoreSnapshot)")
    public Object restoreSnapshotAspect(ProceedingJoinPoint pointcut) throws Throwable {
        snapshotManager.restoreSnapshot(pointcut.getTarget().getClass().getName());

        return pointcut.proceed();
    }
}
