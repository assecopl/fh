package pl.fhframework.fhPersistence.core;

import javax.persistence.EntityManager;

public interface EntityManagerRepository {
   String beanName = "entityManagerRepository";

   EntityManager getEntityManager();

   void turnOffConvesation(Object source);

   void turnOffSessionConvesation(Object source);

   void turnOnConvesation(Object source);

   void turnOnSessionConvesation(Object source);

   boolean isConversation();
}
