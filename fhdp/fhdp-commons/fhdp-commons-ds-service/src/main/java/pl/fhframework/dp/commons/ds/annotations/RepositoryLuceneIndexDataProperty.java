package pl.fhframework.dp.commons.ds.annotations;

public class RepositoryLuceneIndexDataProperty {
	protected boolean propertyIndex = true;
	protected String name;
	protected String function;
	protected String propertyName;
	protected String type;
	protected Long weight = null;
	protected Boolean analyzed = null;
	protected Boolean regexp = null;
	protected Boolean ordered = null;
	protected Double boost = null;
//	protected List<String> paths = new ArrayList<>();
	
	public boolean isPropertyIndex() {
		return propertyIndex;
	}
	public void setPropertyIndex(boolean propertyIndex) {
		this.propertyIndex = propertyIndex;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPropertyName() {
		return propertyName;
	}
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}
	public Long getWeight() {
		return weight;
	}
	public void setWeight(Long weight) {
		this.weight = weight;
	}
	public Boolean getAnalyzed() {
		return analyzed;
	}
	public void setAnalyzed(Boolean analyzed) {
		this.analyzed = analyzed;
	}
	public Boolean getOrdered() {
		return ordered;
	}
	public void setOrdered(Boolean ordered) {
		this.ordered = ordered;
	}
	public Double getBoost() {
		return boost;
	}
	public void setBoost(Double boost) {
		this.boost = boost;
	}
//	public List<String> getPaths() {
//		return paths;
//	}
//	public void setPaths(List<String> paths) {
//		this.paths = paths;
//	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Boolean getRegexp() {
		return regexp;
	}
	public void setRegexp(Boolean regexp) {
		this.regexp = regexp;
	}
	public String getFunction() {
		return function;
	}
	public void setFunction(String function) {
		this.function = function;
	}
	
}
