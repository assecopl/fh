package pl.fhframework.dp.commons.services.semaphore;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.fhframework.dp.commons.base.semafor.ISemaphoreService;
import pl.fhframework.dp.commons.base.semafor.SemaphoreStatusEnum;
import pl.fhframework.dp.commons.model.dao.SemaphoreDAO;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 28/08/2020
 */
@Slf4j
@Service
public class SemaphoreService implements ISemaphoreService {

    @Autowired
    private SemaphoreDAO semaphoreDAO;


    /**
     * Semaphore set up for given time.
     * Subsequent lock for time > 0 extends semaphore validity. if seconds = 0, semaphore is unlocked immediately..
     * <p/>
     * Transaction = REQUIRES_NEW
     *
     * @param type   - semaphore type
     * @param key    - key, i.e. doc ID, process instance ID etc
     * @param value  - value (e.g. process instance guid)
     * @param seconds - how long semaphore is reserved
     * @return SemaforStatusEnum.InValid - if semaphore is already locked by another value.
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public SemaphoreStatusEnum lockSemaphore(Enum type, String key, String value, int seconds) {
        return semaphoreDAO.lockSemaphore(type, key, value, seconds);
    }

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
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public SemaphoreStatusEnum unlockSemaphore(Enum type, String key, String value) {
        return semaphoreDAO.unlockSemaphore(type, key, value);
    }
}
