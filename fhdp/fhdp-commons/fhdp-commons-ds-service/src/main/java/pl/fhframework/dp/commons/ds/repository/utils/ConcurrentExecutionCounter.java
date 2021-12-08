package pl.fhframework.dp.commons.ds.repository.utils;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class ConcurrentExecutionCounter {
	
	protected static ReentrantLock lock = new ReentrantLock();
	protected static ConcurrentHashMap<String, Date> dates = new ConcurrentHashMap<>();
	protected static ConcurrentHashMap<String, Long> counters = new ConcurrentHashMap<>();
//	protected long counter = 0;
//	protected long counter10 = 0;
//	protected Date date = null;
//	protected Date date10 = null;
	
	public static void incrementCounter(String id, long max) {
		try {
			lock.lock();
			
			if(dates.get(id)==null) {
				dates.put(id, new Date());
			}
			Date current = new Date();
			
			Long counter = counters.get(id);
			
			if(counter==null) {
				counter = 0L;
			}
			
			counter++;
			
//			long diff10 = current.getTime() - date10.getTime();
//			long diffSeconds10 = diff10 / 1000; 
			if(counter>=max) {
				
				long diff = current.getTime() - dates.get(id).getTime();
				System.out.println("######## Counter " + id + ": " + counter + " - " + diff + "ms (" + (counter*1000/diff)+"/s)");
				
				counter = 0L;
				dates.put(id, new Date());
			}			
			
			counters.put(id, counter);
			
//			long diffSeconds = diff / 1000; 
//			if(diffSeconds>=60) {
//				
//				System.out.println("######## Speed: " + counter + "/min");
//				
//				date = current;
//				counter = 0;
//			}
			
			
			
		} finally {
			lock.unlock();
		}
	}	
	

}
