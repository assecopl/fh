package pl.fhframework;

import javax.xml.stream.XMLStreamReader;

/**
 * Created by Gabriel on 07.04.2016.
 */
//TODO: KKOZ class is not used in project. Is Deprecated?
@Deprecated
public abstract class StaxPlugin {

    //TODO:Powinna być pewna hierarchia dla wczytywania bazowych wartości np. bez sensu wczytywać w każdym plugine id, etykiete, x, y, itd.!!!!

    protected static String get(String nazwaAtrybutu, XMLStreamReader reader) {
        return reader.getAttributeValue("", nazwaAtrybutu);
    }
}
