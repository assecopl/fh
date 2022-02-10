package pl.fhframework.dp.commons.ds.annotations;

import java.util.List;


public interface QueryHelperInterface {
	public void buildLuceneQuery(IDSequence sequence, Object queryObject, String parent, List<String> tables, List<String> cWheres, boolean addExposed, boolean descendantRelation, String root, boolean descToRoot);
	public void buildLuceneQueryExposed(Object queryObject, String tableId, String path, List<String> wheres, String[] paths, boolean addJoins, boolean flat, String prefix);
	public void buildMongoQuery(Object value, String string, List filter);
	public void buildElasticSearchQuery(Object value, String string, List filter);
}
