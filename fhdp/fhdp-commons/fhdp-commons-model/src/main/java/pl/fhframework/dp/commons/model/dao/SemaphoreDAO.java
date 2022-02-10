package pl.fhframework.dp.commons.model.dao;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import pl.fhframework.dp.commons.base.semafor.SemaphoreStatusEnum;
import pl.fhframework.dp.commons.model.entities.Semaphore;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 28/08/2020
 */
@Service
@Getter @Setter
@Slf4j
public class SemaphoreDAO {

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired()
    private PlatformTransactionManager transactionManager;

    public SemaphoreStatusEnum lockSemaphore(Enum type, String key, String value, int seconds) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        return transactionTemplate.execute(new TransactionCallback<SemaphoreStatusEnum>() {
            @Override
            public SemaphoreStatusEnum doInTransaction(TransactionStatus transactionStatus) {
                SemaphoreStatusEnum semaforStatusEnum = SemaphoreStatusEnum.Invalid;
                try {
                    semaforStatusEnum = internalLockSemaphore(type.name(), key, value, seconds);
                    if (SemaphoreStatusEnum.Invalid.equals(semaforStatusEnum)) {
                        transactionStatus.setRollbackOnly();
                    }
                } catch (Exception e) {
                    log.warn(e.getMessage());
                    transactionStatus.setRollbackOnly();
                    semaforStatusEnum = SemaphoreStatusEnum.Invalid;
                } finally {
                    if (seconds > 0 && !SemaphoreStatusEnum.isValid(semaforStatusEnum)) {
                        log.warn("Locked semaphore {}/{}", type, key);
                    }
                }
                return semaforStatusEnum;

            }
        });
    }

    public SemaphoreStatusEnum unlockSemaphore(Enum type, String key, String value) {
        return internalLockSemaphore(type.name(), key, value, 0);
    }

    public SemaphoreStatusEnum internalLockSemaphore(String type, String key, String value, int seconds) {

        LocalDateTime currentDate = fetchCurrentDate();
        LocalDateTime validityDate = currentDate.plusSeconds(seconds);

        SemaphoreStatusEnum result = SemaphoreStatusEnum.Invalid;

        Semaphore semafor = null;
        try {

            semafor = load(type, key, value, currentDate);

            if (semafor != null) {
                result = SemaphoreStatusEnum.ValidProlonged;
                if (semafor.getLockTime() == null || semafor.getLockTime().isBefore(currentDate) || !value.equals(semafor.getValue())) {
                    semafor.setValue(value);
                    result = SemaphoreStatusEnum.ValidNew;
                }
                semafor.setLockTime(seconds > 0 ? validityDate : null);
                entityManager.persist(semafor);
                entityManager.flush();
            } else if (seconds > 0 && load(type, key, null, null) == null) {
                semafor = new Semaphore(type, key, value, validityDate);
                entityManager.persist(semafor);
                entityManager.flush();
                result = SemaphoreStatusEnum.ValidNew;
            }
        } catch (PersistenceException pe) {
            result = SemaphoreStatusEnum.Invalid;
            if (semafor != null) entityManager.detach(semafor);
            entityManager.clear();
            log.error("** lockSemafor  PersistenceException, {} ", pe); //ignore - means that already exists (locked)
        } catch (Exception e) {
            result = SemaphoreStatusEnum.Invalid;
            if (semafor != null) entityManager.detach(semafor);
            entityManager.clear();
            log.error("** lockSemafor  Exception, {} ", e);
        }

        return result;
    }

    private Semaphore load(String type, String key, String value, LocalDateTime date) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Semaphore> criteria = cb.createQuery(Semaphore.class);
        Root<Semaphore> semaforRoot = criteria.from(Semaphore.class);

        Predicate whereExpresion = cb.and(
                cb.equal(semaforRoot.get("type"), type),
                cb.equal(semaforRoot.get("key"), key));
        if (value != null) {
            whereExpresion = cb.and(whereExpresion, cb.
                    or(cb.equal(semaforRoot.get("value"), value),
                            cb.or(cb.lessThan(semaforRoot.get("lockTime"), date),
                                    cb.isNull(semaforRoot.get("lockTime")))));
        }
        criteria.where(whereExpresion);
        TypedQuery<Semaphore> query = entityManager.createQuery(criteria);
        query.setLockMode(LockModeType.PESSIMISTIC_WRITE); // FOR UPDATE
//        query.setHint("javax.persistence.lock.timeout", 0); // NOWAIT
        //TODO: w logu pojawia się: java.sql.SQLException: ORA-00054: zasób zajęty, a zlecono uzyskanie z NOWAIT lub upłynął limit czasu - trzeba by coś z tym zrobić
        List<Semaphore> resultList = query.getResultList();
        return resultList.isEmpty() ? null : resultList.get(0);
    }

    public LocalDateTime fetchCurrentDate() {
        return LocalDateTime.now();
    }

}
