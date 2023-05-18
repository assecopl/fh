package pl.fhframework.dp.commons.utils.conversion;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
public class BeanClearUtil {

  /**
   * Nested object is expected, java primitive as well as collections and java objects are not supported
   * @param object
   */
  public static void clearObject(Object object) throws IllegalAccessException {
    /**
     * The nested object is handled only, primitive and simple object types are not supported
     */
    if (object == null) {
      throw new NullPointerException();
    } else if (isSimpleObjectType(object) || isArray(object) || isMap(object)) {
      throw new Error("Object type is not supported");
    }

    clear(object);
  }

  /**
   * Goes through the object and sets its parent's node to null if all children are null too
   * @param object
   * @return
   * @throws IllegalAccessException
   */
  private static boolean clear(Object object) throws IllegalAccessException {
    /**
     * Notifies a caller that the current field/parent can be cleared
     */
    boolean isEmpty = true;

    Field[] fields =  object.getClass().getDeclaredFields();
    for (Field field: fields) {
      field.setAccessible(true);
      if(field.getType().isEnum()) continue;
      if(field.getType().isArray()) continue;
      /**
       * Checks a type of the field as well as its value, returns true on the right is the field has a value as fallows:
       * different from null for objects
       * different from 0 for simple types
       */
      ImmutablePair<Boolean, Boolean> simpleFiledType = isSimpleFieldType(field, object);
      if (simpleFiledType.left) {
        if (!simpleFiledType.right) {
          /**
           * The current property is not empty/null, so the parent needs to be notified
           */
          isEmpty = false;
        }
      } else {
        if(!handleObject(field.get(object))) {
          isEmpty = false;
        } else {
          if(!isFinal(field)) {
            field.set(object, null);
          }
        }
      }
    }
    return isEmpty;
  }

  private static boolean isFinal(Field field) {
    return (field.getModifiers() & Modifier.FINAL) == Modifier.FINAL;
  }

  /**
   * Handles arrays/maps and calls the main method, dedicated to nested objects
   * @param object
   * @return
   */
  private static boolean handleObject(Object object) throws IllegalAccessException {
    /**
     * In case of nested objects with empty properties, it is possible that the received object can be null
     */
    if(object == null) {
      return true;
    }
    /**
     * Notifies a caller that the current field/parent can be cleared
     */
    boolean isEmpty = true;

    if (isArray(object)) {
      ArrayList array = (ArrayList) object;
      if (!handleArray(array)) {
        /**
         * At least one property of the array, is not null, so the array cannot be null too
         */
        isEmpty = false;
      }
    } else if (isMap(object)) {
      HashMap map = (HashMap) object;
      if (!handleMap(map)) {
        /**
         * At least one property of the map, is not null, so the map cannot be null too
         */
        isEmpty = false;
      }
    } else {
      if (!clear(object)) {
        /**
         * At least one property of the object, is not null, so the object cannot be null too
         */
        isEmpty = false;
      }
    }
    return isEmpty;
  }

  /**
   * Checks the type of a field, is is simple, which means primitive or Java object a value is checked too
   * @param field
   * @param object
   * @return
   */
  private static ImmutablePair<Boolean, Boolean> isSimpleFieldType(Field field, Object object) throws IllegalAccessException {
    for (Map.Entry<FieldTypes, Class<?>> set: primitiveWrapperMap.entrySet()) {
      if (field.getType().isAssignableFrom(set.getValue())) {
        return ImmutablePair.of(true, isValueEmpty(set.getKey(), field, object));
      }
    }
    return ImmutablePair.of(false, false);
  }

  /**
   * Checks a java object type
   * @param object
   * @return
   * @throws IllegalAccessException
   */
  private static Boolean isSimpleObjectType(Object object) throws IllegalAccessException {
    for (Map.Entry<FieldTypes, Class<?>> set: primitiveWrapperMap.entrySet()) {
      if (object.getClass().isAssignableFrom(set.getValue())) {
        return true;
      }
    }
    return false;
  }

  /**
   * Checks a value of the field, field can be primitive or java object type
   * @return
   */
  private static Boolean isValueEmpty(FieldTypes fieldType, Field field, Object object) throws IllegalAccessException {
    /**
     * Objects, which look like empty, contain private static property serialVersionUID, which is not empty
     * This is workaround because of the above
     */
    if (field.getName() == "serialVersionUID") {
      return true;
    }

    switch (fieldType) {
      case BOOLEAN:
        return field.getBoolean(object) == false;
      case BYTE:
        return field.getByte(object) == 0;
      case CHAR:
        return field.getChar(object) == 0;
      case INT:
        return field.getInt(object) == 0;
      case LONG:
        return field.getLong(object) == 0;
      case SHORT:
        return field.getShort(object) == 0;
      case DOUBLE:
        return field.getDouble(object) == 0;
      case FLOAT:
        return field.getFloat(object) == 0;
      default:
        return field.get(object) == null;
    }
  }


  /**
   * Handles array objects
   * @param array
   * @throws IllegalAccessException
   */
  private static boolean handleArray(ArrayList array) throws IllegalAccessException {
    boolean isEmpty = true;
    for (int i = 0; i < array.size(); i ++) {
      Object arrayElement = array.get(i);
      if(arrayElement != null && isSimpleObjectType(arrayElement)) {
        /**
         * At least one property of the array, is not null, so the array cannot be null too
         */
        isEmpty = false;
      } else {
        if (!handleObject(arrayElement)) {
          /**
           * At least one property of the array, is not null, so the array cannot be null too
           */
          isEmpty = false;
        } else {
          arrayElement = null;
        }
      }
    }
    return isEmpty;
  }

  /**
   * Handles a map object
   * @param map
   * @return
   * @throws IllegalAccessException
   */
  private static boolean handleMap(HashMap map) throws IllegalAccessException {
    boolean isEmpty = true;
    Iterator iterator = map.entrySet().iterator();
    while(iterator.hasNext()) {
      Object mapElement = iterator.next();
      Object mapElementEntry = ((Map.Entry) mapElement).getValue();
      if(mapElement != null && isSimpleObjectType(mapElementEntry)) {
        /**
         * At least one property of the map, is not null, so the map cannot be null too
         */
        isEmpty = false;
      } else {
        if (!handleObject(mapElementEntry)) {
          /**
           * At least one property of the map, is not null, so the map cannot be null too
           */
          isEmpty = false;
        } else {
          mapElementEntry = null;
        }
      }
    }
    return isEmpty;
  }

  /**
   * Keeps all types, which from iteration point of view are treated as simple
   */
  private static final Map<FieldTypes, Class<?>> primitiveWrapperMap =
      new HashMap<FieldTypes, Class<?>> () {{
        put(FieldTypes.BOOLEAN, boolean.class);
        put(FieldTypes.BYTE, byte.class);
        put(FieldTypes.CHAR, char.class);
        put(FieldTypes.DOUBLE, double.class);
        put(FieldTypes.FLOAT, float.class);
        put(FieldTypes.INT, int.class);
        put(FieldTypes.LONG, long.class);
        put(FieldTypes.SHORT, short.class);
        put(FieldTypes.BYTE_OBJ, Byte.class);
        put(FieldTypes.BOOLEAN_OBJ, Boolean.class);
        put(FieldTypes.CHARACTER, Character.class);
        put(FieldTypes.DOUBLE_OBJ, Double.class);
        put(FieldTypes.FLOAT_OBJ, Float.class);
        put(FieldTypes.INTEGER, Integer.class);
        put(FieldTypes.LONG_OBJ, Long.class);
        put(FieldTypes.SHORT_OBJ, Short.class);
        put(FieldTypes.BIGINTEGER, BigInteger.class);
        put(FieldTypes.STRING, String.class);
        put(FieldTypes.BIGDECIMAL, BigDecimal.class);
        put(FieldTypes.LOCALDATE, LocalDate.class);
        put(FieldTypes.LOCALDATETIME, LocalDateTime.class);
      }};

  private enum FieldTypes {
    BOOLEAN,
    BYTE,
    CHAR,
    DOUBLE,
    FLOAT,
    INT,
    LONG,
    SHORT,
    BOOLEAN_OBJ,
    BYTE_OBJ,
    CHARACTER,
    DOUBLE_OBJ,
    FLOAT_OBJ,
    INTEGER,
    LONG_OBJ,
    SHORT_OBJ,
    BIGINTEGER,
    STRING,
    BIGDECIMAL,
    LOCALDATE,
    LOCALDATETIME,
    ENUM
  }

  /**
   * Checks all kind of ArrayList types and returns true/false accordingly
   * @param object
   * @return
   */
  private static boolean isArray(Object object) {
    if(object instanceof Array || object instanceof ArrayList || object instanceof List ||
       object instanceof Collection) {
      return true;
    }
    return false;
  }

  /**
   * Checks all kind of HashMap types and returns true/false accordingly
   * @param object
   * @return
   */
  private static boolean isMap(Object object) {
    if(object instanceof Map) {
      return true;
    }
    return false;
  }
}


