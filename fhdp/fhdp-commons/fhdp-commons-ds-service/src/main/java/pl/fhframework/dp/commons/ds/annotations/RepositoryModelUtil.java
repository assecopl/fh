package pl.fhframework.dp.commons.ds.annotations;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.util.Date;

public class RepositoryModelUtil {
	public static boolean isSimpleType(Class<?> typeClass) {
		if(String.class.equals(typeClass)) {
			return true;
		}
		if(Boolean.class.equals(typeClass)) {
			return true;
		}
		if(boolean.class.equals(typeClass)) {
			return true;
		}
		if(Date.class.equals(typeClass)) {
			return true;
		}
		if(Long.class.equals(typeClass)) {
			return true;
		}
		if(Integer.class.equals(typeClass)) {
			return true;
		}
		if(BigDecimal.class.equals(typeClass)) {
			return true;
		}
		if(Enum.class.isAssignableFrom(typeClass)) {
			return true;
		}
		if(XMLGregorianCalendar.class.equals(typeClass)) {
			return true;
		}

		return false;
	}
	
	public static String getSimpleType(Class<?> typeClass) {
		if(String.class.equals(typeClass)) {
			return "string";
		}
		if(Date.class.equals(typeClass)) {
			return "string";
		}
		if(Long.class.equals(typeClass)) {
			return "string";
		}
		if(Integer.class.equals(typeClass)) {
			return "string";
		}
		if(BigDecimal.class.equals(typeClass)) {
			return "string";
		}
		if(Enum.class.isAssignableFrom(typeClass)) {
			return "string";
		}
		if(boolean.class.isAssignableFrom(typeClass)) {
			return "boolean";
		}		
		if(Boolean.class.isAssignableFrom(typeClass)) {
			return "boolean";
		}		
		if(XMLGregorianCalendar.class.equals(typeClass)) {
			return "date";
		}
		
		return "string";
	}
	
	
}
