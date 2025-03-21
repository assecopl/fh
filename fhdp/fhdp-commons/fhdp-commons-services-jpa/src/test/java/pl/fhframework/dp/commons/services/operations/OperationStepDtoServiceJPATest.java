package pl.fhframework.dp.commons.services.operations;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;


import javax.xml.bind.JAXBException;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 30/09/2021
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
@Slf4j
public class OperationStepDtoServiceJPATest {

    @Autowired
    OperationStepDtoServiceJPA operationStepDtoServiceJPA;

//    @Test
    public void test() throws JAXBException {
    	int count = 4;
        for(int i=0;i<count; i++) {
        	String uuid = UUID.randomUUID().toString();
        	operationStepDtoServiceJPA.logOperationStepStart("msgKey", uuid, "MPID", uuid, "stepID", 1L);
        	operationStepDtoServiceJPA.logOperationStepFinish(uuid, uuid, "stepID");
        }
    }
    
    
    @Test
    public void testMT() throws JAXBException, InterruptedException {
    	
    	int count = 100;
    	int tCount = 20;
    	ConcurrentHashMap<Integer, Thread> tMap = new ConcurrentHashMap<>();
    	for(int t=0;t<tCount;t++) {
    		final Integer tID = t;
    	    Thread th = new Thread(new Runnable() {

    	        @Override
    	        public void run() {
    	            for(int i=0;i<=count; i++) {
    	            	String uuid = UUID.randomUUID().toString();
    	            	operationStepDtoServiceJPA.logOperationStepStart("msgKey", "PID", "MPID", uuid, "stepID", 1L);
    	            	operationStepDtoServiceJPA.logOperationStepFinish("PID", uuid, "stepID");
    	            }
    	            tMap.remove(tID);
    	        }
    	    });
        	tMap.put(tID, th);
        	th.run();

    	}
    	
    	while(tMap.size()>0) {
    		System.out.println("Active threads: " + tMap.size());
    		Thread.sleep(1000);
    	}
    	
    	
    	
    }


}