package pl.fhframework.dp.commons.ds.annotations;

public class IDSequence{
	int i = 0;
	String prefix = "x";
	
	public String getNexID(){
		i++;
		return prefix + i;
	}
}