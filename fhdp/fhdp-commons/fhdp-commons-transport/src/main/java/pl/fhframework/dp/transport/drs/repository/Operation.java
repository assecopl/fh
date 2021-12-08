package pl.fhframework.dp.transport.drs.repository;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
public class Operation implements Serializable {
	protected String name;
	protected String description;
	protected String uuid;
	protected boolean trackChanges = false;
}
