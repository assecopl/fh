package pl.fhframework.dp.commons.ds.repository.utils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.fhframework.dp.commons.ds.annotations.*;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;


public class RepositoryMongoUtil {

	private static final Logger logger = LoggerFactory.getLogger(RepositoryMongoUtil.class);
	
	public static String addNegation(Boolean negation, String expression) {
		if(negation!=null && negation) {
			return "{ $not: " + expression + " }";
		}
		return expression;
	}
	
	
	public static Object valueForQuery(Object value) {
		
		if(XMLGregorianCalendar.class.isAssignableFrom(value.getClass())) {
			return ((XMLGregorianCalendar)value).toGregorianCalendar().getTime();
		}
		
		return value;
	}

	public static String createConditionX(String selector, String value, ConditionTypeEnum type) {
		String cValue = "'" + value + "'";
		String cSelector = selector;
		
		boolean neg = false;
		
		switch(type) {
			case CONTAINS:
				return "{ " + cSelector + ": " + addNegation(neg, "{ $regex: /"+value+"/i }") + " }";
			case EQUALS:
				return "{ " + cSelector + ": " + addNegation(neg, "{ $eq: "+cValue+" }") + " }";
			case GRATER:
				return "{ " + cSelector + ": " + addNegation(neg, "{ $gt: "+cValue+" }") + " }";
			case GREATEROREQUAL:
				return "{ " + cSelector + ": " + addNegation(neg, "{ $gte: "+cValue+" }") + " }";
			case LOWER:
				return "{ " + cSelector + ": " + addNegation(neg, "{ $lt: "+cValue+" }") + " }";
			case LOWEROREQUAL:
				return "{ " + cSelector + ": " + addNegation(neg, "{ $lte: "+cValue+" }") + " }";
//			case NATIVE:
//				return "native("+ selector + ", 'lucene', "+cValue+")";
				
				
		}
		return selector + " = " + cValue;
	
	}
	
	public static Bson createCondition(String selector, Object value, ConditionTypeEnum type) {
		
		Object cValue = valueForQuery(value);
		
		if(isContainValue(cValue)) {
			String regex = String.valueOf(cValue);
			if(regex.startsWith("*")||regex.startsWith("%")) {
				regex = regex.substring(1);
			}
			if(regex.endsWith("*")||regex.endsWith("%")) {
				regex = regex.substring(0, regex.length()-1);
			}
			return Filters.regex(selector,"(?i)"+Pattern.quote(regex));		
		}
		
		switch(type) {
			case CONTAINS:
				String regex = String.valueOf(cValue);
				if(regex.startsWith("*")||regex.startsWith("%")) {
					regex = regex.substring(1);
				}
				if(regex.endsWith("*")||regex.endsWith("%")) {
					regex = regex.substring(0, regex.length()-1);
				}
				return Filters.regex(selector,"(?i)"+Pattern.quote(regex));				
			case EQUALS:
				return Filters.eq(selector, cValue);
			case GRATER:
				return Filters.gt(selector, cValue);
			case GREATEROREQUAL:
				return Filters.gte(selector, cValue);
			case LOWER:
				return Filters.lt(selector, cValue);
			case LOWEROREQUAL:
				return Filters.lte(selector, cValue);
//			case NATIVE:
//				return "native("+ selector + ", 'lucene', "+cValue+")";
				
				
		}
		return Filters.eq(selector, cValue);
	
	}
	
	

	public static boolean isContainValue(Object value) {
		
		if(value instanceof String && ((String) value).startsWith("*")) {
			return true;
		}
		
		return false;
	}


	public static String getContainValue(Object value) {
		String sValue = (String) value;
		return sValue.substring(1);
	}

	public static String getNativeValue(Object value) {
		String sValue = (String) value;
		return sValue.substring(1);
	}



	public static String createSelector(String path, String propertyName) {
		if(path==null||path.isEmpty()) {
			return ""+propertyName+"";
		}
		return ""+path+"."+propertyName+"";
	}



	protected static String getDateString(Object object, String format, String dateFormat, String dateTimeFormat) throws ParseException {
		Date date = null; 
		if(object instanceof Calendar) {
			date = ((Calendar)object).getTime();
		} else if(object instanceof Date) {
			date = (Date) object;
		} else if(object instanceof XMLGregorianCalendar) {
			date = ((XMLGregorianCalendar)object).toGregorianCalendar().getTime();
		} else if(object instanceof String) {
			String sValue = (String) object;
			if(dateFormat!=null && sValue.length()<=dateFormat.length()) {
				SimpleDateFormat parser = new SimpleDateFormat(dateFormat);
				date = parser.parse(sValue);
			} else if(dateTimeFormat!=null) {
				SimpleDateFormat parser = new SimpleDateFormat(dateTimeFormat);
				date = parser.parse(sValue);
			} else {
				return sValue;
			}
		}
		if(date!=null) {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			return sdf.format(date);
		}
		
		return null;
	}

	public static Long getDateLong(Object object) {
		Long date = null; 
		if(object instanceof Calendar) {
			date = ((Calendar)object).getTimeInMillis();
		} else if(object instanceof Date) {
			date = ((Date) object).getTime();
		} else if(object instanceof XMLGregorianCalendar) {
			date = ((XMLGregorianCalendar)object).toGregorianCalendar().getTimeInMillis();
		}
		
		return date;
	}

    public static ObjectMapper getObjectMapper() {
    	ObjectMapper mapper = new ObjectMapper();
    	mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    	mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    	mapper.setSerializationInclusion(Include.NON_NULL);
//    	mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
    	
//        mapper.registerModule(new JodaModule());
            SimpleModule testModule = new SimpleModule("MyModule", new Version(1, 0, 0, null, null, null));

            
            testModule.addSerializer(Date.class, new StdSerializer<Date>(Date.class) {
                private static final long serialVersionUID = 1L;

                @Override
                public void serialize(Date value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
                    try {
                        if (value == null) {
                            jgen.writeNull();
                        } else {
                            jgen.writeStartObject();
                            jgen.writeFieldName("$date");
//                            String isoDate = ISODateTimeFormat.dateTime().print(new DateTime(value));
//                            jgen.writeString(String.valueOf(value.getTime()));
                            jgen.writeNumber(value.getTime());
                            jgen.writeEndObject();
                        }
                    } catch (Exception ex) {
                    	ex.printStackTrace();
                        jgen.writeNull();
                    }
                }
            });

            testModule.addDeserializer(Date.class, new StdDeserializer<Date>(Date.class) {
                private static final long serialVersionUID = 1L;

                @Override
                public Date deserialize(JsonParser jp, DeserializationContext dc) throws IOException, JsonProcessingException {
                    JsonNode tree = jp.readValueAsTree();
//                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    try {
                    	if(tree.get("$date").textValue()!=null) {
                    		return ISODateTimeFormat.dateTime().parseDateTime(tree.get("$date").textValue()).toDate();
                    	}
                   		return new Date(tree.get("$date").longValue());
                    } catch (Throwable t) {
                        throw new IOException(t.getMessage(), t);
                    }
                }

            });

            
            testModule.addSerializer(XMLGregorianCalendar.class, new StdSerializer<XMLGregorianCalendar>(XMLGregorianCalendar.class) {
                private static final long serialVersionUID = 1L;

                @Override
                public void serialize(XMLGregorianCalendar value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
                    try {
                        if (value == null) {
                            jgen.writeNull();
                        } else {
                            jgen.writeStartObject();
                            jgen.writeFieldName("$date");
//                            String isoDate = ISODateTimeFormat.dateTime().print(new DateTime(value));
//                            jgen.writeString(String.valueOf(value.getTime()));
                            jgen.writeNumber(value.toGregorianCalendar().getTime().getTime());
                            jgen.writeEndObject();
                        }
                    } catch (Exception ex) {
                    	ex.printStackTrace();
                        jgen.writeNull();
                    }
                }
            });

            testModule.addDeserializer(XMLGregorianCalendar.class, new StdDeserializer<XMLGregorianCalendar>(XMLGregorianCalendar.class) {
                private static final long serialVersionUID = 1L;

                @Override
                public XMLGregorianCalendar deserialize(JsonParser jp, DeserializationContext dc) throws IOException, JsonProcessingException {
                    JsonNode tree = jp.readValueAsTree();
//                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    XMLGregorianCalendar result = null;
                    try {
                    	Date date = null;
                    	if(tree.get("$date").textValue()!=null) {
                    		date = ISODateTimeFormat.dateTime().parseDateTime(tree.get("$date").textValue()).toDate();
                    	}
                    	date =  new Date(tree.get("$date").longValue());
        				GregorianCalendar c = new GregorianCalendar();
        				c.setTime(date);
        				result = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);                    	
                    } catch (Throwable t) {
                        throw new IOException(t.getMessage(), t);
                    }
                    return result;
                }

            });
            

            testModule.addSerializer(GregorianCalendar.class, new StdSerializer<GregorianCalendar>(GregorianCalendar.class) {
                private static final long serialVersionUID = 1L;

                @Override
                public void serialize(GregorianCalendar value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
                    try {
                        if (value == null) {
                            jgen.writeNull();
                        } else {
                            jgen.writeStartObject();
                            jgen.writeFieldName("$date");
//                            String isoDate = ISODateTimeFormat.dateTime().print(new DateTime(value));
//                            jgen.writeString(String.valueOf(value.getTime()));
                            jgen.writeNumber(value.getTime().getTime());
                            jgen.writeEndObject();
                        }
                    } catch (Exception ex) {
                    	ex.printStackTrace();
                        jgen.writeNull();
                    }
                }
            });

            testModule.addDeserializer(GregorianCalendar.class, new StdDeserializer<GregorianCalendar>(GregorianCalendar.class) {
                private static final long serialVersionUID = 1L;

                @Override
                public GregorianCalendar deserialize(JsonParser jp, DeserializationContext dc) throws IOException, JsonProcessingException {
                    JsonNode tree = jp.readValueAsTree();
//                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    GregorianCalendar result = null;
                    try {
                    	Date date = null;
                    	if(tree.get("$date").textValue()!=null) {
                    		date = ISODateTimeFormat.dateTime().parseDateTime(tree.get("$date").textValue()).toDate();
                    	}
                    	date =  new Date(tree.get("$date").longValue());
        				GregorianCalendar c = new GregorianCalendar();
        				c.setTime(date);
        				result = c;                   	
                    } catch (Throwable t) {
                        throw new IOException(t.getMessage(), t);
                    }
                    return result;
                }

            });
            
            
            
            
            mapper.registerModule(testModule);
 	
    	return mapper;
    }

    public static BasicDBObject xmlToDBObject(byte[] xml) throws IOException {
	      XmlMapper xmlMapper = new XmlMapper();
	      JsonNode node = xmlMapper.readTree(xml);
	      String json = getObjectMapper().writeValueAsString(node); 
          BasicDBObject dbo = BasicDBObject.parse(json);
          return dbo;
    }



}
