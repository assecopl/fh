package pl.fhframework.dp.commons.ds.annotations;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RepositoryModelScanner {

	protected List<Class<?>> list = new ArrayList<>();
	protected List<String> cndList = new ArrayList<>();
	protected Map<Class<?>, String> cnds = new HashMap<>();
	protected Map<Class<?>, String> types = new HashMap<>();
	protected List<RepositoryIndexData> indexes = new ArrayList<>();
	protected List<RepositoryLuceneIndexData> indexesLucene = new ArrayList<>();
	protected Map<String, Long> lanes = new HashMap<>();

	protected String header = "<japis = 'urn:japis.repository'>";
	protected String base = "nt:unstructured";
	protected String listType = "japis:list";

	
	
	
	public String getHeader() {
		return header;
	}


	public void setHeader(String header) {
		this.header = header;
	}


	public String getBase() {
		return base;
	}

	
	

	public String getListType() {
		return listType;
	}


	public void setListType(String listType) {
		this.listType = listType;
	}


	public void setBase(String base) {
		this.base = base;
	}


	public List<String> getCndList() {
		return cndList;
	}


	public List<RepositoryIndexData> getIndexes() {
		return indexes;
	}
	public List<RepositoryLuceneIndexData> getIndexesLucene() {
		return indexesLucene;
	}

	public Map<String, Long> getLanes() {
		return lanes;
	}


	public String scan(Class<?> nodeClass) {



		if (nodeClass.isAnnotationPresent(RepositoryNode.class)) {
			
			List<String> containers = new ArrayList<>();
			
			String nodeType = nodeClass.getAnnotation(RepositoryNode.class).type();
			String nodeBaseType = nodeClass.getAnnotation(RepositoryNode.class).base();
			String nodeNS = nodeClass.getAnnotation(RepositoryNode.class).ns();
			boolean anyProp = nodeClass.getAnnotation(RepositoryNode.class).anyProperty();
			if (nodeType != null && !nodeType.isEmpty()) {
				
				if("nt:unstructured".equals(nodeType)) {
					return "nt:unstructured";
				}
				
				if (nodeNS.isEmpty()) {
					nodeNS = header;
				}
				if (nodeBaseType.isEmpty()) {
					nodeBaseType = base;
				}
				types.put(nodeClass, nodeType);
				
				for(RepositoryDescendantPropertyIndex dp : nodeClass.getAnnotationsByType(RepositoryDescendantPropertyIndex.class)) {
					String idxName = dp.name();
					String idxSubType = dp.subtype();
					String idxTag = dp.tag();
					String propertyName = dp.propertyName();
					if(idxTag.isEmpty()) {
						idxTag = idxName;
					}

					RepositoryIndexData idx = new RepositoryIndexData();
					idx.setName(idxName);
					idx.setPropertyName(propertyName);
					idx.setPropertyIndex(true);
					idx.setType(nodeType);
					idx.setTag(idxTag);
					idx.setUnique(false);
					indexes.add(idx);
				}				
				
				String cnd = nodeNS + "\n" + "[" + nodeType + "] " + " > " + nodeBaseType + "\n";
				for (Field field : nodeClass.getDeclaredFields()) {
					if (!field.isAnnotationPresent(RepositoryIgnore.class)) {
						String propertyName = field.getName();
						if (field.isAnnotationPresent(RepositoryField.class)) {
							String annPropName = field.getAnnotation(RepositoryField.class).value();
							if (!annPropName.isEmpty()) {
								propertyName = annPropName;
							}
						}
						
						if (field.isAnnotationPresent(RepositoryFieldType.class)) {
							String fType = field.getAnnotation(RepositoryFieldType.class).type();
							boolean isProperty = field.getAnnotation(RepositoryFieldType.class).property();
							cnd = cnd + (isProperty?" - ":" + ") + propertyName + " (" + fType
							+ ")" + "\n";

						} else {
						
				            String container = null;
				            if (field.isAnnotationPresent(RepositoryContainer.class)) {
				            	container = field.getAnnotation(RepositoryContainer.class).value();
				            }						
							if (RepositoryModelUtil.isSimpleType(field.getType())) {
								cnd = cnd + " - " + propertyName + " (" + RepositoryModelUtil.getSimpleType(field.getType())
										+ ")" + "\n";
							} else {
								boolean typeAdded = false;
								if(container!=null) {
									String cName = container;
									if(cName.contains("/")) {
										cName = cName.substring(0, cName.indexOf("/"));
									}
									if(!containers.contains(cName)) {
										cnd = cnd + " + " + cName + " ( "+getListType()+" )" + "\n";
										containers.add(cName);
									}
									typeAdded = true;
								}
									if (List.class.isAssignableFrom(field.getType())) {
										ParameterizedType listType = (ParameterizedType) field.getGenericType();
										Class<?> elementClass = (Class<?>) listType.getActualTypeArguments()[0];
										if (RepositoryModelUtil.isSimpleType(elementClass)) {
											if(!typeAdded) {
												cnd = cnd + " - " + propertyName + " ("
														+ RepositoryModelUtil.getSimpleType(elementClass) + ") multiple" + "\n";
											}
										} else {
//											String fType = scan(elementClass);
											String fType = types.get(elementClass);
											if (fType == null) {
												fType = scan(elementClass);
											}											
											if (fType != null) {
												if(!typeAdded) {
			//										cnd = cnd + " + " + "*" + " (" + fType + ")" + " multiple" + "\n";
													cnd = cnd + " + " + propertyName + " ( "+getListType()+" )" + "\n";
												}
											}
		
										}
									} else {
										String fType = types.get(field.getType());
										if (fType == null) {
											fType = scan(field.getType());
										}
										if (fType != null) {
											if(!typeAdded) {
												cnd = cnd + " + " + propertyName + " (" + fType + ")" + "\n";
											}
										}
									}
	
							}
						}
						if (field.isAnnotationPresent(RepositoryPropertyIndex.class)) {
							String idxName = field.getAnnotation(RepositoryPropertyIndex.class).name();
							String idxSubType = field.getAnnotation(RepositoryPropertyIndex.class).subtype();
							String idxTag = field.getAnnotation(RepositoryPropertyIndex.class).tag();
							String idxVersion = field.getAnnotation(RepositoryPropertyIndex.class).version();
							boolean idxUnique =  field.getAnnotation(RepositoryPropertyIndex.class).unique();
							if(idxName.isEmpty()) {
								idxName = nodeClass.getName() + "_" + field.getName();
							}
							if(idxTag.isEmpty()) {
								idxTag = idxName;
							}

							RepositoryIndexData idx = new RepositoryIndexData();
							idx.setName(idxName);
							idx.setPropertyName(field.getName());
							idx.setPropertyIndex(true);
							idx.setType(nodeType);
							idx.setTag(idxTag);
							idx.setVersion(idxVersion);
							idx.setUnique(idxUnique);
							indexes.add(idx);
							if(!idxSubType.isEmpty()) {
								//[mix:fresh_crkidmd_identyfikator] mixin
								String idxMixType = "mix:" + idxSubType +"_"+ nodeType.replaceAll(":", "_");
								String idxCnd = "["+idxMixType+"] mixin\n";
								cndList.add(idxCnd);
								RepositoryIndexData idxs = new RepositoryIndexData();
								idxs.setName(idxName+"_"+idxSubType);
								idxs.setPropertyName(field.getName());
								idxs.setPropertyIndex(true);
								idxs.setType(idxMixType);
								idxs.setTag(idxTag+idxSubType);
								indexes.add(idxs);
							}

						}
					}
				}
				
				if(anyProp) {
					cnd = cnd + "\n" + "- * (undefined)";
				}
				
				cnds.put(nodeClass, cnd);
				list.add(nodeClass);
				cndList.add(cnd);
//    	        System.out.println(cnd);
				return nodeType;
			}
		} else {
			System.out.println("Non Repo class: " + nodeClass);
		}

		return null;

	}

	
	public void scanLuceneAggregates(Class<?> nodeClass, List<RepositoryLuceneIndexDataAggreg> aggregates, List<Class<?>> scanned, String indexName, String mixinType) {
		if(scanned.contains(nodeClass)) {
			return;
		}
		scanned.add(nodeClass);
		if (nodeClass.isAnnotationPresent(RepositoryNode.class)) {
			String nodeType = nodeClass.getAnnotation(RepositoryNode.class).type();
			String nodeBaseType = nodeClass.getAnnotation(RepositoryNode.class).base();
			String nodeNS = nodeClass.getAnnotation(RepositoryNode.class).ns();
			if (nodeType != null && !nodeType.isEmpty()) {
				if (nodeNS.isEmpty()) {
					nodeNS = header;
				}
				if (nodeBaseType.isEmpty()) {
					nodeBaseType = base;
				}
				
				RepositoryLuceneIndexDataAggreg lag = null;
				
				for(RepositoryLuceneIndexAggregate li : nodeClass.getAnnotationsByType(RepositoryLuceneIndexAggregate.class)) {
					if(lag==null) {
						lag = new RepositoryLuceneIndexDataAggreg();
						lag.setName(nodeType);
						if(mixinType!=null && !mixinType.isEmpty()) {
							lag.setName(mixinType);
						}						
					}
					String path = li.path();
					boolean relative = li.relative();
					String pType = li.primaryType();
					
					RepositoryLuceneIndexDataAggregInclude inc = new RepositoryLuceneIndexDataAggregInclude();
					if(relative) {
						inc.setRelative(relative);
					}
					inc.setPath(path);
					inc.setPrimaryType(pType);
					lag.getIncludes().add(inc);
					
					
				}		
				if(lag!=null) {
					aggregates.add(lag);
				}
				
				for (Field field : nodeClass.getDeclaredFields()) {
					if (!field.isAnnotationPresent(RepositoryIgnore.class)) {
						String propertyName = field.getName();
						if (field.isAnnotationPresent(RepositoryField.class)) {
							String annPropName = field.getAnnotation(RepositoryField.class).value();
							if (!annPropName.isEmpty()) {
								propertyName = annPropName;
							}
						}
						if (RepositoryModelUtil.isSimpleType(field.getType())) {

						} else {
							scanLuceneAggregates(field.getType(), aggregates, scanned, indexName, mixinType);
						}

					}
				}

				
				
			}
		}
	}	
	
	
	
	public void scanLuceneRules(Class<?> nodeClass, List<RepositoryLuceneIndexDataRule> rules, List<Class<?>> scanned, String indexName, String mixinType) {
		if(scanned.contains(nodeClass)) {
			return;
		}
		scanned.add(nodeClass);
		if (nodeClass.isAnnotationPresent(RepositoryNode.class)) {
			String nodeType = nodeClass.getAnnotation(RepositoryNode.class).type();
			String nodeBaseType = nodeClass.getAnnotation(RepositoryNode.class).base();
			String nodeNS = nodeClass.getAnnotation(RepositoryNode.class).ns();
			if (nodeType != null && !nodeType.isEmpty()) {
				if (nodeNS.isEmpty()) {
					nodeNS = header;
				}
				if (nodeBaseType.isEmpty()) {
					nodeBaseType = base;
				}
				
				
				RepositoryLuceneIndexDataRule rule = new RepositoryLuceneIndexDataRule();
				rule.setName(nodeType);
				if(mixinType!=null && !mixinType.isEmpty()) {
					rule.setName(mixinType);
				}
				for (Field field : nodeClass.getDeclaredFields()) {
					if (!field.isAnnotationPresent(RepositoryIgnore.class)) {
						String propertyName = field.getName();
						if (field.isAnnotationPresent(RepositoryField.class)) {
							String annPropName = field.getAnnotation(RepositoryField.class).value();
							if (!annPropName.isEmpty()) {
								propertyName = annPropName;
							}
						}
						
						///// BLOKADA rekurencji, zagniezdzone klasy trzeba wymienic jawnie w indeksie 
						
						
//						if (RepositoryModelUtil.isSimpleType(field.getType())) {
//						} else if (List.class.isAssignableFrom(field.getType())) {
//							ParameterizedType listType = (ParameterizedType) field.getGenericType();
//							Class<?> cls = (Class<?>) listType.getActualTypeArguments()[0];
//							scanLuceneRules(cls, rules, scanned, indexName);
//						} else {
//							scanLuceneRules(field.getType(), rules, scanned, indexName);
//						}
						if (field.isAnnotationPresent(RepositoryLuceneIndexProperty.class)) {
							RepositoryLuceneIndexDataProperty prop = new RepositoryLuceneIndexDataProperty();
							long weight = field.getAnnotation(RepositoryLuceneIndexProperty.class).weight();
							boolean propertyIndex =  field.getAnnotation(RepositoryLuceneIndexProperty.class).propertyIndex();
							String ordered = field.getAnnotation(RepositoryLuceneIndexProperty.class).ordered();
							String analyzed = field.getAnnotation(RepositoryLuceneIndexProperty.class).analyzed();
							String boost = field.getAnnotation(RepositoryLuceneIndexProperty.class).boost();
							String type = field.getAnnotation(RepositoryLuceneIndexProperty.class).type();
							String[] indexes = field.getAnnotation(RepositoryLuceneIndexProperty.class).indexes();
							prop.setName(propertyName);
							prop.setPropertyName(propertyName);
							prop.setWeight(weight);
							prop.setPropertyIndex(propertyIndex);
							prop.setType(type);

							if(!analyzed.isEmpty()) {
								prop.setAnalyzed(Boolean.parseBoolean(analyzed));
							}
							if(!ordered.isEmpty()) {
								prop.setOrdered(Boolean.parseBoolean(ordered));
							}
							if(!boost.isEmpty()) {
								try {
									prop.setBoost(Double.parseDouble(boost));
								} catch(Exception e) {
									e.printStackTrace();
								}
							}
							rule.getProperties().add(prop);
						}
					}
				}
				
				for(RepositoryLuceneIndexDescendantProperty dp : nodeClass.getAnnotationsByType(RepositoryLuceneIndexDescendantProperty.class)) {
					RepositoryLuceneIndexDataProperty prop = new RepositoryLuceneIndexDataProperty();
					long weight = dp.weight();
					boolean propertyIndex =  dp.propertyIndex();
					String ordered = dp.ordered();
					String analyzed = dp.analyzed();
					String regexp = dp.regexp();
					String boost = dp.boost();
					String type = dp.type();
					String[] indexes = dp.indexes();
					String name = dp.name();
					String propertyName = dp.path();
					String function = dp.function();
					prop.setName(name);
					prop.setPropertyName(propertyName);
					prop.setFunction(function);
					prop.setWeight(weight);
					prop.setPropertyIndex(propertyIndex);
					prop.setType(type);

					if(!analyzed.isEmpty()) {
						prop.setAnalyzed(Boolean.parseBoolean(analyzed));
					}
					if(!regexp.isEmpty()) {
						prop.setRegexp(Boolean.parseBoolean(regexp));
					}
					if(!ordered.isEmpty()) {
						prop.setOrdered(Boolean.parseBoolean(ordered));
					}
					if(!boost.isEmpty()) {
						try {
							prop.setBoost(Double.parseDouble(boost));
						} catch(Exception e) {
							e.printStackTrace();
						}
					}
					rule.getProperties().add(prop);					
					
				}				
				
				if(rule.getProperties().size()>0) {
					rules.add(rule);
				}
				
				
			}
		}
	}
	
//	public void scanLucene(Class<?> nodeClass) {
//
//		String header = "<crkidmd = 'urn:pl.gov.mf.kud.crkid.repository.metadata'>";
//		String base = "nt:unstructured";
//
//		if (nodeClass.isAnnotationPresent(RepositoryLuceneIndex.class)) {
//			String liName = nodeClass.getAnnotation(RepositoryLuceneIndex.class).name();
//			String liTag = nodeClass.getAnnotation(RepositoryLuceneIndex.class).tag();
//			String liVersion = nodeClass.getAnnotation(RepositoryLuceneIndex.class).version();
//			String[] liTypes = nodeClass.getAnnotation(RepositoryLuceneIndex.class).types();
//			String lane = nodeClass.getAnnotation(RepositoryLuceneIndex.class).indexinglane();
//			if(lane!=null&&!lane.isEmpty()&&!lane.endsWith("async")) {
//				lane = lane + "-async";
//			}
//			Long laneInterval = nodeClass.getAnnotation(RepositoryLuceneIndex.class).indexinglaneinterval();
//			
//			RepositoryLuceneIndexData idxd = new RepositoryLuceneIndexData();
//			idxd.setName(liName);
//			idxd.setTag(liTag);
//			idxd.setVersion(liVersion);
//			idxd.setIndexingLane(lane);
//			idxd.setIndexinglaneinterval(laneInterval);
//			if(lane!=null && !lane.isEmpty()) {
//				lanes.put(lane, laneInterval);
//			}
//			
//			for(String t: liTypes) {
//				RepositoryLuceneIndexDataRule rule = new RepositoryLuceneIndexDataRule();
//				rule.setName(t);
//				RepositoryLuceneIndexDataProperty prop = new RepositoryLuceneIndexDataProperty();
//				prop.setName("primaryType");
//				prop.setPropertyName("jcr:primaryType");
//				prop.setPropertyIndex(true);
//				rule.getProperties().add(prop);
//			}
//			
//			List<Class<?>> scanned = new ArrayList<>();
//		
//				for (Field field : nodeClass.getDeclaredFields()) {
//					if (!field.isAnnotationPresent(RepositoryIgnore.class)) {
//						String propertyName = field.getName();
//						if (field.isAnnotationPresent(RepositoryField.class)) {
//							String annPropName = field.getAnnotation(RepositoryField.class).value();
//							if (!annPropName.isEmpty()) {
//								propertyName = annPropName;
//							}
//						}
//						if (RepositoryModelUtil.isSimpleType(field.getType())) {
//
//						} else {
//							scanLuceneRules(field.getType(), idxd.rules, scanned);
//						}
//						if (field.isAnnotationPresent(RepositoryLuceneIndexProperty.class)) {
//							//TODO: na razie nie ma potrzeby
//						}
//					}
//				}
//				
//				indexesLucene.add(idxd);				
//				
//			}
//	}
	
	
	public void scanLucene(Class<?> nodeClass) {

		String header = "<crkidmd = 'urn:pl.gov.mf.kud.crkid.repository.metadata'>";
		String base = "nt:unstructured";

//		if (nodeClass.isAnnotationPresent(RepositoryLuceneIndex.class)) {
			for(RepositoryLuceneIndex li : nodeClass.getAnnotationsByType(RepositoryLuceneIndex.class)) {
				String liName = li.name();
				String liTag = li.tag();
				String liIndexTpe = li.indexType();
				String liVersion = li.version();
				String[] liTypes = li.types();
				String[] liPaths = li.paths();
				String lane = li.indexinglane();
				String nrt = li.nrt();
				String indexOriginalTerm = li.indexOriginalTerm();
				String analyserClass = li.analyserClass();
				String codec = li.codec();
				if(lane!=null&&!lane.isEmpty()&&!lane.endsWith("async")) {
					lane = lane + "-async";
				}
				Long laneInterval = li.indexinglaneinterval();
				
				RepositoryLuceneIndexData idxd = new RepositoryLuceneIndexData();
				idxd.setName(liName);
				idxd.setTag(liTag);
				idxd.setVersion(liVersion);
				idxd.setIndexType(liIndexTpe);
				idxd.setIndexingLane(lane);
				idxd.setIndexinglaneinterval(laneInterval);
				idxd.setNrt(nrt);
				if(!indexOriginalTerm.isEmpty()) {
					idxd.setIndexOriginalTerm(Boolean.parseBoolean(indexOriginalTerm));
				}
				if(!analyserClass.isEmpty()) {
					idxd.setAnalyserClass(analyserClass);
				}
				if(!codec.isEmpty()) {
					idxd.setCodec(codec);
				}
				
				
				
				if(lane!=null && !lane.isEmpty()) {
					lanes.put(lane, laneInterval);
				}
				if(liPaths.length>0) {
					idxd.setPaths(liPaths);
				}
				
				
				for(String t: liTypes) {
					RepositoryLuceneIndexDataRule rule = new RepositoryLuceneIndexDataRule();
					rule.setName(t);
					RepositoryLuceneIndexDataProperty prop = new RepositoryLuceneIndexDataProperty();
					prop.setName("primaryType");
					prop.setPropertyName("jcr:primaryType");
					prop.setPropertyIndex(true);
					rule.getProperties().add(prop);
				}
				
				List<Class<?>> scanned = new ArrayList<>();
				List<Class<?>> scannedAggs = new ArrayList<>();
			
					for (Field field : nodeClass.getDeclaredFields()) {
						if (!field.isAnnotationPresent(RepositoryIgnore.class)) {
							String propertyName = field.getName();
							if (field.isAnnotationPresent(RepositoryField.class)) {
								String annPropName = field.getAnnotation(RepositoryField.class).value();
								if (!annPropName.isEmpty()) {
									propertyName = annPropName;
								}
							}
							String mixinType = null;
							if(field.isAnnotationPresent(RepositoryLuceneIndexMixinType.class)) {
								mixinType = field.getAnnotation(RepositoryLuceneIndexMixinType.class).value();
							}
							if (RepositoryModelUtil.isSimpleType(field.getType())) {
	
							} else {
								scanLuceneRules(field.getType(), idxd.rules, scanned, liName, mixinType);
								scanLuceneAggregates(field.getType(), idxd.aggregates, scannedAggs, liName, mixinType);
							}
							if (field.isAnnotationPresent(RepositoryLuceneIndexProperty.class)) {
								//TODO: na razie nie ma potrzeby
							}
						}
					}
					
					indexesLucene.add(idxd);				
				}
//			}
	}
	
//	public class IDSequence{
//		int i = 0;
//		String prefix = "x";
//		
//		public String getNexID(){
//			i++;
//			return prefix + i;
//		}
//	}
//	
//	public void buildLuceneQuery(IDSequence sequence, Object queryObject, String parent, List<String> tables, List<String> wheres) {
//		Class<?> nodeClass = queryObject.getClass();		
//		if (nodeClass.isAnnotationPresent(RepositoryNode.class)) {
//			String nodeType = nodeClass.getAnnotation(RepositoryNode.class).type();
//			String nodeBaseType = nodeClass.getAnnotation(RepositoryNode.class).base();
//			String nodeNS = nodeClass.getAnnotation(RepositoryNode.class).ns();
//			if (nodeType != null && !nodeType.isEmpty()) {
//				if (nodeNS.isEmpty()) {
//					nodeNS = header;
//				}
//				if (nodeBaseType.isEmpty()) {
//					nodeBaseType = base;
//				}
//				final String id = sequence.getNexID();
//				String tableJoin = "inner join ["+nodeType+"] as "+id+" on ISCHILDNODE("+id+","+parent+")";
//				tables.add(tableJoin);
//
//				for (Field field : nodeClass.getDeclaredFields()) {
//					if (!field.isAnnotationPresent(RepositoryIgnore.class)) {
//			            field.setAccessible(true);
//						String propertyName = field.getName();
//						if (field.isAnnotationPresent(RepositoryField.class)) {
//							String annPropName = field.getAnnotation(RepositoryField.class).value();
//							if (!annPropName.isEmpty()) {
//								propertyName = annPropName;
//							}
//						}
//						if (RepositoryModelUtil.isSimpleType(field.getType())) {
//							if (field.isAnnotationPresent(RepositoryLuceneIndexProperty.class)) {
//				            	try {
//									Object value = field.get(queryObject);
//									if(value!=null) {								
//										String whereLine = "["+id+"].[value] = '"+value+"'";
//										wheres.add(whereLine);
//									}
//				            	} catch(Exception e) {
//				            		e.printStackTrace();
//				            	}
//							}
//						} else {
//			            	try {
//								Object value = field.get(queryObject);
//								if(value!=null) {								
//									List<String> childTables = new ArrayList<String>();
//									List<String> childWheres = new ArrayList<String>();
//									buildLuceneQuery(sequence, value, id, childTables, childWheres);
//									if(childWheres.size()>0) {
//										wheres.addAll(childWheres);
//										tables.addAll(childTables);
//									}
//								}
//			            	} catch(Exception e) {
//			            		e.printStackTrace();
//			            	}
//						}
//
//					}
//				}
//				
//				
//			}
//		}
//	}	
//	
//	public String buildLuceneQuery(Object queryObject, String selector, String parent, String...parents) {
//		Class<?> nodeClass = queryObject.getClass();
//		if (nodeClass.isAnnotationPresent(RepositoryLuceneIndex.class)) {
//			String liName = nodeClass.getAnnotation(RepositoryLuceneIndex.class).name();
//			String liTag = nodeClass.getAnnotation(RepositoryLuceneIndex.class).tag();
//			String[] liTypes = nodeClass.getAnnotation(RepositoryLuceneIndex.class).types();		
//		
//			List<String> tables = new ArrayList<String>();
//			List<String> wheres = new ArrayList<String>();
//			IDSequence sequence = new IDSequence();
//			
//			String query = "select * from ["+parent+"] as "+selector+"";
//			String id;
//			String pid = selector;
//			for(String p : parents) {
//				id = sequence.getNexID();
//				String tableJoin = "inner join ["+p+"] as "+id+" on ISCHILDNODE("+id+","+pid+")";
//				tables.add(tableJoin);
//				pid = id;
//			}
//			
//			for (Field field : nodeClass.getDeclaredFields()) {
//				if (!field.isAnnotationPresent(RepositoryIgnore.class)) {
//					if (RepositoryModelUtil.isSimpleType(field.getType())) {
//					} else {
//		            	try {
//		                    field.setAccessible(true);
//							Object value = field.get(queryObject);
//							if(value!=null) {								
//								List<String> childTables = new ArrayList<String>();
//								List<String> childWheres = new ArrayList<String>();
//								buildLuceneQuery(sequence, value, pid, childTables, childWheres);
//								if(childWheres.size()>0) {
//									wheres.addAll(childWheres);
//									tables.addAll(childTables);
//								}
//							}
//		            	} catch(Exception e) {
//		            		e.printStackTrace();
//		            	}
//					}
//
//				}
//			}			
////			buildLuceneQuery(sequence, queryObject, pid, tables, wheres);
//			if(wheres.size()>0){
//				for(String tJoin : tables) {
//					query = query + "\n" + tJoin;
//				}
//				query = query + "\nwhere";
//				String and = " ";
//				
//				for(String wLine : wheres) {
//					query = query + "\n" + and + wLine;
//					if("".equals(and)) {
//						and = " and ";
//					}
//				}
//				query = query + "\noption(index tag "+liTag+")";
//				return query;
//				
////				"select * from [crkid:document] as d"
////						+ " inner join [crkidmd:document] as m on ISCHILDNODE(m,d)"
////						+ " inner join [crkidmd:odbiorca] as x1 on ISCHILDNODE(x1,m)"
////						+ " inner join [crkidmd:podmiot] as x2 on ISCHILDNODE(x2,x1)"
////						+ " inner join [crkidmd:instytucja] as x3 on ISCHILDNODE(x3,x2)"
////						+ " inner join [crkidmd:id] as i on ISCHILDNODE(i,x3)"
////						+ " where [i].[value] = 'Value_20181128162146_0_0' and [i].[typ] = 'Typ_20181128162146_0_0'"
////						+ " option(index tag crkid_wyszukaj_dokument)"				
//			}
//		}		
//
//		return null;
//		
//	}
	
	
}
