package pl.fhframework.dp.commons.services.semaphore.mongo;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.mapping.MongoId;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SemaphoreDto {
	
	public final static String SEPARATOR = "_";
	
	@MongoId
	String id;
	String type;
	String key;
	String value;
	LocalDateTime lockTime;
	Long lock;

	public SemaphoreDto() {
		
	}
	
    public SemaphoreDto(String type, String key, String value, LocalDateTime lockTime) {
    	this.id = type + SEPARATOR + key; 
        this.type = type;
        this.key = key;
        this.value = value;
        this.lockTime = lockTime;
        this.lock = 0L;
    }

    public SemaphoreDto(Enum enumType, String key, String value, LocalDateTime lockTime) {
    	this.id = enumType.name() + SEPARATOR + key; 
        this.type = enumType.name();
        this.key = key;
        this.value = value;
        this.lockTime = lockTime;
        this.lock = 0L;
    }	
	
	
}
