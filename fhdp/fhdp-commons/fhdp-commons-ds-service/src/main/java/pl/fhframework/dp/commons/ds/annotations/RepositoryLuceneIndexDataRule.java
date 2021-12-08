package pl.fhframework.dp.commons.ds.annotations;

import java.util.ArrayList;
import java.util.List;

public class RepositoryLuceneIndexDataRule {
	protected String name;
	List<RepositoryLuceneIndexDataProperty> properties = new ArrayList<>();
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<RepositoryLuceneIndexDataProperty> getProperties() {
		return properties;
	}
	public void setProperties(List<RepositoryLuceneIndexDataProperty> properties) {
		this.properties = properties;
	}
	
	
	
}
