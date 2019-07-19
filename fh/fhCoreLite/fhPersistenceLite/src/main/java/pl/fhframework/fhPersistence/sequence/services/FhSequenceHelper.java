package pl.fhframework.fhPersistence.sequence.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.fhframework.fhPersistence.anotation.WithoutConversation;
import pl.fhframework.fhPersistence.core.EntityManagerRepository;
import pl.fhframework.fhPersistence.core.model.sequence.FhSequence;

import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

/**
 * Created by pawel.ruta on 2018-02-07.
 */
@Service
public class FhSequenceHelper {
    @Autowired
    private EntityManagerRepository repository;

    @Transactional
    @WithoutConversation
    public Long sequenceNextValue(String name) {
        TypedQuery<FhSequence> query = repository.getEntityManager().createQuery("select seq from FhSequence seq where seq.name = ?1", FhSequence.class);
        query.setParameter(1, name);
        query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
        try {
            FhSequence sequence = query.getSingleResult();

            return sequence.next();
        }
        catch (NoResultException ex) {
            return null;
        }
    }

    @Transactional
    @WithoutConversation
    public void sequenceCreate(String sequenceName) {
        FhSequence fhSequence = new FhSequence();
        fhSequence.setName(sequenceName);
        repository.getEntityManager().persist(fhSequence);
    }
}
