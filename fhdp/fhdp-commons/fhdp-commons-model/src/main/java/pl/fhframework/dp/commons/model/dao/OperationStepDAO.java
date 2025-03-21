package pl.fhframework.dp.commons.model.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import pl.fhframework.dp.commons.model.entities.OperationStep;
import pl.fhframework.dp.transport.dto.operations.OperationStepDtoQuery;


@Service
@Getter @Setter
@Slf4j
public class OperationStepDAO {

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired()
    private PlatformTransactionManager transactionManager;

    public List<OperationStep> findByQuery(OperationStepDtoQuery query) {
        CriteriaBuilder cBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<OperationStep> criteria = cBuilder.createQuery(OperationStep.class);


        
        Root<OperationStep> opStep = criteria.from(OperationStep.class);
        
        
        List<Predicate> predicateList = new ArrayList<>();
        Predicate[] predicates;        
        
        buildQuery(predicateList, query, cBuilder, opStep);
        
        if(predicateList.size()>0) {
	        predicates = predicateList.toArray(new Predicate[0]);
	        criteria.where(predicates);
        }
        
        TypedQuery<OperationStep> jpaQuery = entityManager.createQuery(criteria);
        
        return jpaQuery.getResultList();
    }

    public Long countByQuery(OperationStepDtoQuery query) {
        CriteriaBuilder cBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = cBuilder.createQuery(Long.class);


        
        Root<OperationStep> opStep = criteria.from(OperationStep.class);
        
        
        List<Predicate> predicateList = new ArrayList<>();
        Predicate[] predicates;        
        
        buildQuery(predicateList, query, cBuilder, opStep);
        
        if(predicateList.size()>0) {
	        predicates = predicateList.toArray(new Predicate[0]);
	        criteria.where(predicates);
        }
        
        return entityManager.createQuery(criteria).getSingleResult();
    }    
    
    
	protected void buildQuery(List<Predicate> predicateList, OperationStepDtoQuery query, CriteriaBuilder cBuilder, Root<OperationStep> opStep) {
		if (query.getOperationGUID() != null) {
			predicateList.add(cBuilder.equal(opStep.get("operationGUID"), query.getOperationGUID()));
        }

        if (query.getDocID() != null) {
        	predicateList.add(cBuilder.equal(opStep.get("docID"), query.getDocID()));
        }

        if(query.getMasterProcessId() != null) {
        	predicateList.add(cBuilder.equal(opStep.get("masterProcessId"), query.getMasterProcessId()));
        }

        if(query.getProcessId() != null) {
        	predicateList.add(cBuilder.equal(opStep.get("processId"), query.getProcessId()));
        }

        if(query.getStepId() != null) {
        	predicateList.add(cBuilder.equal(opStep.get("stepId"), query.getStepId()));
        }
	}    

}
