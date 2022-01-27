package pl.fhframework.dp.transport.dto.changes;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import pl.fhframework.dp.commons.base.comparator.ChangeTypeEnum;
import pl.fhframework.dp.commons.base.comparator.annotations.ComparableClass;
import pl.fhframework.dp.commons.base.comparator.annotations.ComparableCollection;
import pl.fhframework.dp.commons.base.comparator.annotations.ComparableField;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Slf4j
public abstract class ObjectDataComparatorBase<CHANGE, DTO> {

	final static String BEAUTIFY_TRUE = "$.common.true";
	final static String BEAUTIFY_FALSE = "$.common.false";
	final static String ADDED = "$.compare.added";
	final static String DELETED = "$.compare.deleted";
//	final static String BEAUTIFY_DATE = "yyyy-MM-dd";
//    final static String BEAUTIFY_DATETIME = "yyyy-MM-dd HH:mm";
	final static DateTimeFormatter dtf = DateTimeFormatter.ofPattern(getDateFormat());
	final static DateTimeFormatter dtmf = DateTimeFormatter.ofPattern(getDateTimeFormat());

	/**
	 *
	 */
	public static class ObjectDataInfo {
		public Object before;
		public Object after;
		public final String rootName;
		public String xPath;
		public ComparableField cf;
		public ComparableClass cc;
		public ComparableCollection cl;
		public boolean recursive = false;
		public String[] packagesToCompare;

		public boolean firstLevel = false;

		public ObjectDataInfo(String rootName){
			this.rootName = rootName;
		}

		public boolean ignoreAdded(){
			return (cf!=null && Arrays.asList(cf.ignore()).contains(ChangeTypeEnum.ADDED));
		}

		public boolean ignoreDeleted(){
			return (cf!=null && Arrays.asList(cf.ignore()).contains(ChangeTypeEnum.DELETED));
		}

		public boolean ignoreModified(){
			return (cf!=null && Arrays.asList(cf.ignore()).contains(ChangeTypeEnum.MODIFIED));
		}
	}

	public List<CHANGE> compareDeclarationObjects(DTO before, DTO after, boolean recursive, String[] packagesToCompare) {
		ObjectDataInfo obDataInfo = new ObjectDataInfo("/");
		obDataInfo.before = before;
		obDataInfo.after = after;
		obDataInfo.xPath = "";
		obDataInfo.recursive = recursive;
		obDataInfo.packagesToCompare = packagesToCompare;

		// On the highest level compare only annotated fields
		obDataInfo.firstLevel = true;

		obDataInfo.cc = getAnnotation(ComparableClass.class);

		List<CHANGE> changes = new LinkedList<>();
		mergeCollections(changes, compareObject(obDataInfo));

		afterMergeCollections(changes);
//		log.debug("List of changes : ");
//		for (CHANGE change : changes) {
//			log.debug("Change on [{}] from [{}] to [{}] ",  change.getPointer(), change.getValueBefore(),
//					change.getValueAfter() );
//		}

		return changes;

	}

	protected abstract void afterMergeCollections(List<CHANGE> changes);

	protected abstract ComparableClass getAnnotation(Class<ComparableClass> comparableClassClass);


	private Collection<CHANGE> mergeCollections(Collection<CHANGE> oldObj, Collection<CHANGE> newObj) {
		if (oldObj != null && newObj != null) {
			oldObj.addAll(newObj);
			return oldObj;
		} else
			return null;
	}

	public List<CHANGE> compareObject(ObjectDataInfo obInfo) {

		Object newObj = obInfo.after;
		Object oldObj = obInfo.before;

		String rootName = obInfo.rootName;
		String xPath = obInfo.xPath;
		String xPathPrefix = getRootXpathPrefix() + xPath;

		List<CHANGE> changes = new LinkedList<>();

		if (oldObj == null && newObj == null)
			return null;
		else if (oldObj == null) {
			if (!obInfo.ignoreAdded()){
				if (newObj instanceof CompareSummaryData){
					changes.add(newChange(xPathPrefix, ADDED, ((CompareSummaryData)newObj).buildSummary()));
				} else {
					registerFields(newObj, changes, xPathPrefix, true);
				}
			}
		} else if (newObj == null) {
			if (!obInfo.ignoreDeleted()){
				if (oldObj instanceof CompareSummaryData){
					changes.add(newChange(xPathPrefix, ((CompareSummaryData)oldObj).buildSummary(), DELETED));
				} else {
					registerFields(oldObj, changes, xPathPrefix, false);
				}
			}
		} else {
			if (newObj.getClass().getAnnotation(ComparableClass.class) == null && !obInfo.recursive) {
				String msg = "Objects cannot be compared (missing annotation @ComparableClass) for class "+(newObj!=null?newObj.getClass():"null")+"!!";
				log.warn(msg);
				throw new IllegalArgumentException(msg);

			}

			log.debug("Objects can be compared (class "+(newObj!=null?newObj.getClass():"null")+")!!");

			Class<? extends Object> clazz = newObj.getClass();

			ArrayList<Field> allFields = new ArrayList<Field>();
			Class<? extends Object> ccc=clazz;
			while (ccc!=null){
				if (ccc.getAnnotation(ComparableClass.class)==null && !obInfo.recursive){
					ccc=null;
				}else{
					allFields.addAll(Arrays.asList(ccc.getDeclaredFields()));
					ccc = ccc.getSuperclass();
				}
			}

			for (Field field : allFields) {
				field.setAccessible(true);
				boolean compareAsField = false;
				boolean compareAsCollection = false;
				if (obInfo.recursive){
					Object fieldClass = field.getType();
					if (fieldClass.equals(List.class)){
						compareAsCollection = true;
					} else {
						compareAsField = true;
					}
				}
				if (field.isAnnotationPresent(ComparableField.class) || (compareAsField && !obInfo.firstLevel)) {
					try {
						ObjectDataInfo obInfoField = new ObjectDataInfo(rootName);
						obInfoField.recursive = obInfo.recursive;
						obInfoField.packagesToCompare = obInfo.packagesToCompare;

						ComparableField cf = field.getAnnotation(ComparableField.class);
						obInfoField.cf = cf;

						Class cl = field.getType();

						boolean processAsClass = ! obInfo.recursive;
						if (obInfo.recursive && obInfo.packagesToCompare != null){
							processAsClass = Arrays.stream(obInfo.packagesToCompare).anyMatch(e -> cl.getPackage()!= null && cl.getName().startsWith(e));
						}

						if(cl.isEnum() || field.isEnumConstant()) {
							processAsClass = false;
						}

						if (field.getType().isAnnotationPresent(ComparableClass.class) || processAsClass) {
							log.debug("Fields are of Comparable Class !!");

							ComparableClass cc = field.getType().getAnnotation(ComparableClass.class);
							obInfoField.cc = cc;

							String cfXPath = (cf==null || StringUtils.isBlank(cf.Xpath()))? (cc==null ? field.getName() :cc.Xpath()):cf.Xpath();

							obInfoField.before = field.get(oldObj);
							obInfoField.after = field.get(newObj);
							obInfoField.xPath = xPath + cfXPath+ getXpathSeparator();
							List<CHANGE> llc= compareObject(obInfoField);

							if(llc!=null){
								changes.addAll(llc);
							}
						} else {
							log.debug("Comparing field : " + field.getName());

							String path = null;
							if (cf == null){
								path = xPathPrefix + "" + field.getName();
							} else if (cf.rawXpath() == false) {
								path = xPathPrefix + "" + cf.Xpath();
							} else {
								path = xPathPrefix + field.getName();
							}
							if (cf != null && !cf.inSchema()) path += " (obj)";

							Object valueAfter = null;
							Object valueBefore = null;

							String nm =field.getType().getName();
							if (nm.equals("double")){
								valueAfter = field.getDouble(newObj);
								valueBefore = field.getDouble(oldObj);
							}else if (nm.equals("int")){
								valueAfter = field.getInt(newObj);
								valueBefore = field.getInt(oldObj);
							}else if (nm.equals("boolean")){
								valueAfter = field.getBoolean(newObj);
								valueBefore = field.getBoolean(oldObj);
							}else if (nm.equals("short")){
								valueAfter = field.getShort(newObj);
								valueBefore = field.getShort(oldObj);
							}else if (nm.equals("long")){
								valueAfter = field.getLong(newObj);
								valueBefore = field.getLong(oldObj);
							}else if (nm.equals("byte")){
								valueAfter = field.getByte(newObj);
								valueBefore = field.getByte(oldObj);
							}else if (nm.equals("java.time.LocalDate")){
								valueAfter = field.get(newObj);
								valueBefore = field.get(oldObj);
							}else{
								valueAfter = field.get(newObj);
								valueBefore = field.get(oldObj);
							}

							//dla unikniecia zmian u zrodla
							Object valueBeforeI = valueBefore;
							Object valueAfterI = valueAfter;

							String valueBeforeStr = "";
							if (valueBefore!=null){
								if (valueBefore instanceof BigDecimal && obInfoField.cf!=null && obInfoField.cf.precision()>-1){
									valueBeforeI = ((BigDecimal)valueBefore).setScale(obInfoField.cf.precision());
									valueBeforeStr = ((BigDecimal)valueBeforeI).toPlainString();
								} else {
									valueBeforeStr = valueBefore.toString().trim();
								}
							}
							String valueAfterStr = "";
							if (valueAfter!=null){
								if (valueAfter instanceof BigDecimal && obInfoField.cf!=null && obInfoField.cf.precision()>-1){
									valueAfterI = ((BigDecimal)valueAfter).setScale(obInfoField.cf.precision());
									valueAfterStr = ((BigDecimal)valueAfterI).toPlainString();
								} else {
									valueAfterStr = valueAfter.toString().trim();
								}
							}
							//mz:ponizej mozna zrobic enhancement zeby wołać beautify jako argument z jednym SimpleDateFormat dla danego wątku coby troche przyspieszyc
							if ((valueAfter == null && valueBefore == null) || valueBeforeStr.equals(valueAfterStr))
								continue;
							else if (valueAfter == null) {
								if (!obInfoField.ignoreDeleted()){
									CHANGE change = newChange(path, beautify(valueBeforeI, field.getName()), "");
									changes.add(change);
								}

							} else if (valueBefore == null) {
									if (!obInfoField.ignoreAdded()){
										CHANGE change = newChange(path, "", beautify(valueAfterI, field.getName()));
										changes.add(change);
									}

							} else if (!valueBefore.equals(valueAfter) /*&& !valueBeforeStr.equals(valueAfterStr)*/) {
								if (!obInfoField.ignoreModified()){
                                    // true jeśli equals zwraca false, ale compareTo zwraca 0//MB:ponizsze chyba teraz do pominiecia po zmianach dla *I
                                    boolean notEqualButSameAfterComparison = false;
                                    if (valueBefore instanceof BigDecimal && valueAfter instanceof BigDecimal) {
                                        notEqualButSameAfterComparison = (0 == ((BigDecimal) valueBefore).compareTo((BigDecimal) valueAfter));
                                    }
                                    if (valueBefore instanceof Date && valueAfter instanceof Date) {
                                        notEqualButSameAfterComparison = (0 == ((Date) valueBefore).compareTo((Date) valueAfter));
                                    }
                                    if (!notEqualButSameAfterComparison) {
										CHANGE change = newChange(path, beautify(valueBeforeI, field.getName()), beautify(valueAfterI, field.getName()));
                                        changes.add(change);
                                    }
								}
							}
						}
					} catch (IllegalAccessException iae) {
						log.error("Can't access value of field : " + field.getName());
					}
				} else if (field.isAnnotationPresent(ComparableCollection.class) || (compareAsCollection && !obInfo.firstLevel)) {
					try {

						ObjectDataInfo obInfoColl = new ObjectDataInfo(rootName);
						obInfoColl.recursive = obInfo.recursive;
						obInfoColl.packagesToCompare = obInfo.packagesToCompare;

						log.debug("Comparing collection : " + field.getName());
						ComparableCollection coll = field.getAnnotation(ComparableCollection.class);
						//String path = xPathPrefix + coll.Xpath();

						Collection<Object> newValue = (Collection<Object>) field.get(newObj);
						Collection<Object> oldValue = (Collection<Object>) field.get(oldObj);
						obInfoColl.before = oldValue;
						obInfoColl.after = newValue;
						obInfoColl.xPath = xPath + (coll==null ? field.getName() : coll.Xpath());
						obInfoColl.cl = coll;

						mergeCollections(changes, compareObjectsCollection(oldValue, newValue, obInfoColl/*path*/));

					} catch (IllegalAccessException iae) {
						log.error("Can't access value of field : " + field.getName());
					}
				}
			}
		}
		return changes;
	}

	private void registerFields(Object obj, List<CHANGE> changes, String xPathPrefix, boolean added) {
		Field[] fields = obj.getClass().getDeclaredFields();
		for(Field field: fields) {
			try {
				if(field.getName().equals("serialVersionUID")) continue;
				field.setAccessible(true);
				if(field.get(obj) != null) {
					Object value = field.get(obj);
					String className = value.getClass().getName();
					if(value.toString().startsWith(className + "@")) {
						registerFields(value, changes, xPathPrefix + field.getName() + getXpathSeparator(), added);
					} else {
						if(added) {
							changes.add(newChange(xPathPrefix + field.getName(), ADDED, beautify(field.get(obj), field.getName())));
						} else {
							changes.add(newChange(xPathPrefix + field.getName(), beautify(field.get(obj), field.getName()), DELETED));
						}
					}
				}
			} catch (IllegalAccessException e) {
				log.error("Can't access value of field : " + field.getName());
			}
		}
	}

	protected abstract String getXpathSeparator();

	protected abstract String getRootXpathPrefix();

	protected abstract CHANGE newChange(String xPath, String operation, String summary);

	private static Class<?> getMethodReturnClass(Method m){
		java.lang.reflect.Type returnType = m.getGenericReturnType();
		if (returnType instanceof Class<?>) {
		    return (Class<?>)returnType;
		} /*else if(returnType instanceof ParameterizedType) {
		    return getClass(((ParameterizedType)returnType).getRawType());
		}*/
		return null;
	}

	private List<CHANGE> compareObjectsCollection(Collection<Object> oldObjCollection,
												  Collection<Object> newObjCollection, ObjectDataInfo obInfo) {

		String rootName = obInfo.rootName;
		String xPath = obInfo.xPath;
		String xPathPrefix = rootName + xPath;

		List<CHANGE> changes = new LinkedList<>();

		boolean useIterationCompare = true;

		boolean oldIsEmpty = oldObjCollection == null || oldObjCollection.isEmpty();
		boolean newIsEmpty = newObjCollection == null || newObjCollection.isEmpty();
		Collection<Object> checkObjCollection = oldIsEmpty ? newObjCollection : oldObjCollection;

		if (!oldIsEmpty && !newIsEmpty) {
			Object t = checkObjCollection.iterator().next();
		}

		Comparator<Object> comparator = new Comparator<Object>() {

			public int compare(Object firstObj, Object secondObj) {
				int retValue = 0;

				try {
					Method method1 = null;
					Method method2 = null;

					try {
						method1 = firstObj.getClass().getMethod("getNumber", Integer.class);
						method2 = secondObj.getClass().getMethod("getNumber", Integer.class);

					} catch (NoSuchMethodException nsme) {
						method1 = null;
						method2 = null;
					}
//					if (method1 == null) {
//						try {
//							method1 = firstObj.getClass().getMethod("getSequenceNumber");
//							method2 = secondObj.getClass().getMethod("getSequenceNumber");
//
//						} catch (NoSuchMethodException nsme) {
//							method1 = null;
//							method2 = null;
//						}
//					}
//                    if (method1 == null) {
//                        try {
//                            method1 = firstObj.getClass().getMethod("getSequenceNo");
//                            method2 = secondObj.getClass().getMethod("getSequenceNo");
//
//                        } catch (NoSuchMethodException nsme) {
//                            method1 = null;
//                            method2 = null;
//                        }
//                    }
					if (method1 == null) {
						try {
							method1 = firstObj.getClass().getMethod("goodsItemNumber");
							method2 = secondObj.getClass().getMethod("goodsItemNumber");

						} catch (NoSuchMethodException nsme) {
							method1 = null;
							method2 = null;
						}
					}
					if (method1 == null) {
						try {
							method1 = firstObj.getClass().getMethod("getId");
							method2 = secondObj.getClass().getMethod("getId");
						} catch (NoSuchMethodException nsme) {
							method1 = null;
							method2 = null;
						}
						if (method1 == null){
                        	return 1;
						}
                        Class<?> cl = getMethodReturnClass(method1);

						if (Long.class.equals(cl)){
							Long firstId = (Long) method1.invoke(firstObj);
							Long secondId = (Long) method2.invoke(secondObj);

							if (firstId != null && secondId != null){
								retValue = firstId.compareTo(secondId);
							} else if(firstId == null && secondId != null){
								retValue = 1;
							} else if(firstId != null){
								retValue = -1;
							} else {
								retValue = -1;//0 eliminuje z listy
							}

						} else if (BigDecimal.class.equals(cl)) {
							BigDecimal firstId = (BigDecimal) method1.invoke(firstObj);
							BigDecimal secondId = (BigDecimal) method2.invoke(secondObj);

							if (firstId != null && secondId != null){
								retValue = firstId.compareTo(secondId);
							} else if(firstId == null && secondId != null){
								retValue = 1;
							} else if(firstId != null){
								retValue = -1;
							} else {
								retValue = -1;//0 eliminuje z listy
							}

						} else {
							throw new Exception("[compareObjectsCollection] Unhandled getId returnType:"+cl.getName());
						}

					} else {
						Integer firstId = (Integer) method1.invoke(firstObj);
						Integer secondId = (Integer) method2.invoke(secondObj);
						if (firstId != null && secondId != null){
							retValue = firstId.compareTo(secondId);
						} else if(firstId == null && secondId != null){
							retValue = 1;
						} else if(firstId != null){
							retValue = -1;
						}
					}

				} catch (Exception e) {
					log.error("[compareObject] Exception, firstObjectClass: "+firstObj.getClass().getName()+", Error : " + e.getMessage(), e);
					//log.debug("Full stack trace : ", e);
				}
				return retValue;
			}
		};

		Iterator<Object> oldIter = getIteratorForCompareObjectsCollection(oldObjCollection, comparator);
		Iterator<Object> newIter = getIteratorForCompareObjectsCollection(newObjCollection, comparator);

		Object oldObj = null;
		Object newObj = null;

		if (useIterationCompare) {

			int counter = 0;
			while (oldIter.hasNext() || newIter.hasNext()) {
				counter++;
				if (oldIter.hasNext())
					oldObj = oldIter.next();
				else
					oldObj = null;

				if (newIter.hasNext())
					newObj = newIter.next();
				else
					newObj = null;

				ObjectDataInfo newObjectDataInfo = new ObjectDataInfo(rootName);
				newObjectDataInfo.recursive = obInfo.recursive;
				newObjectDataInfo.packagesToCompare = obInfo.packagesToCompare;
				newObjectDataInfo.before = oldObj;
				newObjectDataInfo.after = newObj;
				newObjectDataInfo.xPath = xPath + "[" + counter + "]" + getXpathSeparator();

				mergeCollections(changes, compareObject(newObjectDataInfo));
			}
		} else {
			// przy okazji mamy zagwarantowane, że obydwie kolekcje są nie nulami z badania useIterationCompare
			int counter = 0;
			int counterExist = 0;
			while (oldIter.hasNext()){
				counter++;
				oldObj = oldIter.next();
				if (!newObjCollection.contains(oldObj)){ // było a nie ma

					ObjectDataInfo newObjectDataInfo = new ObjectDataInfo(rootName);
					newObjectDataInfo.recursive = obInfo.recursive;
					newObjectDataInfo.packagesToCompare = obInfo.packagesToCompare;
					newObjectDataInfo.before = oldObj;
					newObjectDataInfo.after = null; // usunięcie
					newObjectDataInfo.xPath = xPath + "[" + counter + "]" + getXpathSeparator();
					mergeCollections(changes, compareObject(newObjectDataInfo));
				} else {
					counterExist++;
				}
			}
			while (newIter.hasNext()){
				newObj = newIter.next();
				if (!oldObjCollection.contains(newObj)){ // nie było, a jest
					counterExist++;

					ObjectDataInfo newObjectDataInfo = new ObjectDataInfo(rootName);
					newObjectDataInfo.recursive = obInfo.recursive;
					newObjectDataInfo.packagesToCompare = obInfo.packagesToCompare;
					newObjectDataInfo.before = null;
					newObjectDataInfo.after = newObj; // dodanie
					newObjectDataInfo.xPath = xPath + "[" + counterExist + "]" + getXpathSeparator();
					mergeCollections(changes, compareObject(newObjectDataInfo));
				}
			}
		}
		return changes;
	}

	private Iterator<Object> getIteratorForCompareObjectsCollection(Collection<Object> collection, Comparator<Object> comparator) {

		int valuesWithNullCounter = 0;

		if (collection != null) {

			for (Object t : collection) {

				try {
					Method getIdMethod = t.getClass().getMethod("getId");

					Object idValue = getIdMethod.invoke(t);

					if (idValue == null) {
						valuesWithNullCounter++;
					}

				} catch (Exception e) {
					continue;
				}
			}
		}

		if (collection != null && valuesWithNullCounter == collection.size()) { // wszystkie id'ki maja null'e, wiec nie mozemy uzyc treeSet, bo comparator nam nie zadziala
			return collection.iterator();

		} else {
			TreeSet<Object> treeSet = new TreeSet<>(comparator);

			if(collection != null) {
				treeSet.addAll(collection); //mz: tu lecial NPE
			}

			return treeSet.iterator();
		}
	}

	protected String beautify(Object o, SimpleDateFormat sdf, String fieldName) {
		if (o == null) {
			return null;
		}
		if (o instanceof Boolean) {
			Boolean b = (Boolean) o;
			if (b != null && b.booleanValue() == true) {
				return BEAUTIFY_TRUE;
			} else {
				return BEAUTIFY_FALSE;
			}
			//}else if(o instanceof DateTime){
			//    if(sdf==null){
			//        return (new SimpleDateFormat(BEAUTIFY_DATETIME)).format((java.util.Date) o);
			//    }else{
			//        return sdf.format((java.util.Date) o);
			//    }
		} else if (o instanceof LocalDate) {
			if (sdf == null) {
				return (((LocalDate) o).format(dtf));
			} else {
				return sdf.format((Date) o);
			}

		} else if (o instanceof LocalDateTime) {
			if (sdf == null) {
				return (((LocalDateTime) o).format(dtmf));
			} else {
				return sdf.format((Date) o);
			}

		} else if (o instanceof Enum) {
			o = "enum." + o.getClass().getTypeName() + "." + o.toString();
		}
		return o.toString();
	}

	protected static String getDateFormat() {
		return "dd.MM.yyyy";
	}

	protected static String getDateTimeFormat() {
		return "dd.MM.yyyy HH:mm:ss";
	}

	private String beautify(Object o, String fieldName){
		return beautify(o,null, fieldName);
	}

	protected String camelCase(String xpath) {
		char[] xpathArray = xpath.toCharArray();
		xpathArray[0] = Character.toUpperCase(xpathArray[0]);
		for(int i = 1; i < xpathArray.length; i++)
		{
			if(xpathArray[i-1] == '.')
				xpathArray[i] = Character.toUpperCase(xpathArray[i]);
		}
		return new String(xpathArray);
	}
}
