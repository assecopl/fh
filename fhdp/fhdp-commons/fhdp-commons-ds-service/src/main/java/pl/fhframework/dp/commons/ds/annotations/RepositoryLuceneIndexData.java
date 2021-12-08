package pl.fhframework.dp.commons.ds.annotations;

import java.util.ArrayList;
import java.util.List;

public class RepositoryLuceneIndexData {
	protected String name;
	protected String type;
	protected String tag;
	protected String indexType;
	protected String version;
	protected String indexingLane;
	protected long indexinglaneinterval;
	protected String[] paths = null;
	protected String nrt = null;
	protected Boolean indexOriginalTerm = null;
	protected String analyserClass = null;
	protected String codec = null;

	List<RepositoryLuceneIndexDataRule> rules = new ArrayList<>();
	List<RepositoryLuceneIndexDataAggreg> aggregates = new ArrayList<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public List<RepositoryLuceneIndexDataRule> getRules() {
		return rules;
	}

	public void setRules(List<RepositoryLuceneIndexDataRule> rules) {
		this.rules = rules;
	}

	public String getIndexingLane() {
		return indexingLane;
	}

	public void setIndexingLane(String indexingLane) {
		this.indexingLane = indexingLane;
	}

	public long getIndexinglaneinterval() {
		return indexinglaneinterval;
	}

	public void setIndexinglaneinterval(long indexinglaneinterval) {
		this.indexinglaneinterval = indexinglaneinterval;
	}

	public String[] getPaths() {
		return paths;
	}

	public void setPaths(String[] paths) {
		this.paths = paths;
	}

	public List<RepositoryLuceneIndexDataAggreg> getAggregates() {
		return aggregates;
	}

	public void setAggregates(List<RepositoryLuceneIndexDataAggreg> aggregates) {
		this.aggregates = aggregates;
	}

	public String getIndexType() {
		return indexType;
	}

	public void setIndexType(String indexType) {
		this.indexType = indexType;
	}

	public String getNrt() {
		return nrt;
	}

	public void setNrt(String nrt) {
		this.nrt = nrt;
	}

	public Boolean getIndexOriginalTerm() {
		return indexOriginalTerm;
	}

	public void setIndexOriginalTerm(Boolean indexOriginalTerm) {
		this.indexOriginalTerm = indexOriginalTerm;
	}

	public String getAnalyserClass() {
		return analyserClass;
	}

	public void setAnalyserClass(String analyserClass) {
		this.analyserClass = analyserClass;
	}

	public String getCodec() {
		return codec;
	}

	public void setCodec(String codec) {
		this.codec = codec;
	}
	
	
	
}
