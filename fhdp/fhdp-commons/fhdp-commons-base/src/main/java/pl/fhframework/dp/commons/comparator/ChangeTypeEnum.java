package pl.fhframework.dp.commons.comparator;

import pl.fhframework.dp.commons.base.model.IEnumDescription;

import java.io.Serializable;

public enum ChangeTypeEnum implements IEnumDescription, Serializable {
	ADDED("ADDED","Dodano"),
	DELETED("DELETED","Usunięto"),
	MODIFIED("MODIFIED","Zmodyfikowano");

	private static final long serialVersionUID = 1L;

	private String description = null;

	private ChangeTypeEnum(String value, String description) {
		this.description = description;
	}

	@Override
	public String getTypeName() {
		return this.name();
	}
}
