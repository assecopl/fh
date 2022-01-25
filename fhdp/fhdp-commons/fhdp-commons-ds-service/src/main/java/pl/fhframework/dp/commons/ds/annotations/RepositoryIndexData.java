package pl.fhframework.dp.commons.ds.annotations;

public class RepositoryIndexData {
	protected boolean propertyIndex = true;
	protected String name;
	protected String propertyName;
	protected String type;
	protected String tag;
	protected String version;
	protected boolean unique = false;
	public boolean isUnique() {
		return unique;
	}
	public void setUnique(boolean unique) {
		this.unique = unique;
	}
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
	public String getPropertyName() {
		return propertyName;
	}
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}

	
	
	
}
