package pl.fhframework.dp.commons.base.semafor;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 27/08/2020
 */
public interface ISemaphoreService {
    /**
     * Semaphore set up for given time.
     * Subsequent lock for time > 0 extends semaphore validity. if seconds = 0, semaphore is unlocked immediately..
     * <p/>
     * Transaction = REQUIRES_NEW (zawsze nowa)
     *
     * @param type   - semaphore type
     * @param key    - key, i.e. doc ID, process instance ID etc
     * @param value  - value (e.g. process instance guid)
     * @param seconds - how long semaphore is reserved
     * @return SemaforStatusEnum.InValid - if semaphore is already locked by another value.
     */
    public SemaphoreStatusEnum lockSemaphore(Enum type, final String key, final String value, final int seconds);

    /**
     * Unlocking active semaphore.
     *
     * <p/>
     * Transaction = REQUIRED
     * <p/>
     * <b> Warning: when transaction is rolled back, semaphore remains locked!
     *     you can release it by locking with 0 seconds if you want.
     * </b>
     */
    public SemaphoreStatusEnum unlockSemaphore(Enum type, String key, String value);
}
