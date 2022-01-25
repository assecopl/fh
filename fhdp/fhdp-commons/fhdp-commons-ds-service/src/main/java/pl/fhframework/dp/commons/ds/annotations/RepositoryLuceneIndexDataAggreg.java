package pl.fhframework.dp.commons.ds.annotations;

import java.util.ArrayList;
import java.util.List;

public class RepositoryLuceneIndexDataAggreg {
	protected String name;
	List<RepositoryLuceneIndexDataAggregInclude> includes = new ArrayList();
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<RepositoryLuceneIndexDataAggregInclude> getIncludes() {
		return includes;
	}
	public void setIncludes(List<RepositoryLuceneIndexDataAggregInclude> includes) {
		this.includes = includes;
	}
	
	
}
