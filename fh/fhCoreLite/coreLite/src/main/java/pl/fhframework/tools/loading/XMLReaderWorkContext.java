package pl.fhframework.tools.loading;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.io.FhResource;

import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamReader;


public class XMLReaderWorkContext<ROOT_TYPE> {

    @Getter
    private FhResource fileResource;

    private XMLStreamReader reader;

    @Getter
    @Setter
    private ROOT_TYPE root;

    public XMLReaderWorkContext(FhResource fileResource, XMLStreamReader reader, ROOT_TYPE root){
        this.fileResource = fileResource;
        this.reader = reader;
        this.root = root;
    }

    public Location getCurrentLocation(){
        return reader.getLocation();
    }
}
