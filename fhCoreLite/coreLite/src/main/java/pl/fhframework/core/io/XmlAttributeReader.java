package pl.fhframework.core.io;

import javax.xml.stream.XMLStreamReader;

/**
 * Created by Gabriel.Kurzac on 2016-09-29.
 */
public class XmlAttributeReader extends AttributeReader {
    /**
     * Wraped reader instance
     */
    private XMLStreamReader reader;


    public XmlAttributeReader(XMLStreamReader reader) {
        this.reader = reader;
    }

    /**
     * Reads attribute value for given atribut name
     * @param attrName Atribute name
     * @return Returns value of given attribute or throws an error if given attribute doesn't exists
     */
    @Override
    public String readAtrAsString(String attrName) {
        return reader.getAttributeValue("", attrName);
    }



//    public int getNumberOrDefault(String attributeName, int defaultValue) {
//        String textValue = getAttributeValue(attributeName);
//        if (textValue !=null){
//            try{
//                return Integer.parseInt(textValue);
//            }catch (NumberFormatException nfe){
//                throw new RuntimeException("Incorrect number value in "+reader.getLocation().getLineNumber()+":"+reader.getLocation().getColumnNumber());
//            }
//        }else{
//            return defaultValue;
//        }
//    }
}
