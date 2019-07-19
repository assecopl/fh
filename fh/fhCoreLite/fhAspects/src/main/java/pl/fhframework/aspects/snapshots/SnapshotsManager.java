package pl.fhframework.aspects.snapshots;


/**
 */
public interface SnapshotsManager {
    void createSnapshot(Object owner);

    void dropSnapshot(Object owner);

    void restoreSnapshot(Object owner);
}
