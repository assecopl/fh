package pl.fhframework.core.io;

/**
 * Created by Gabriel.Kurzac on 2016-09-29.
 */
public abstract class AttributeReader {
    public abstract  <T> T readAtrAsString(String atrName);
}
