package pl.fhframework.fhPersistence.snapshots;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;
import pl.fhframework.aspects.snapshots.SnapshotsManager;

/**
 * @author Paweł Ruta
 */
@Aspect
@Component
public class SnapshotsManagerAspect {
    @Autowired
    SnapshotsManager snapshotManager;

    @Before("@annotation(pl.fhframework.aspects.snapshots.model.CreateSnapshot)")
    public void createSnapshotAspect(JoinPoint pointcut) throws Throwable {
        snapshotManager.createSnapshot(pointcut.getTarget());
    }

    @Before("@annotation(pl.fhframework.aspects.snapshots.model.DropSnapshot)")
    public void dropSnapshotAspect(JoinPoint pointcut) throws Throwable {
        snapshotManager.dropSnapshot(pointcut.getTarget());
    }

    @Before("@annotation(pl.fhframework.aspects.snapshots.model.RestoreSnapshot)")
    public void restoreSnapshotAspect(JoinPoint pointcut) throws Throwable {
        snapshotManager.restoreSnapshot(pointcut.getTarget());
        snapshotManager.dropSnapshot(pointcut.getTarget());
    }
}
