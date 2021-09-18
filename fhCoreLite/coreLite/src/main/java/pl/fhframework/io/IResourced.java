package pl.fhframework.io;

import org.springframework.core.io.Resource;

import java.io.FileNotFoundException;
import java.util.List;

public interface IResourced {
    Resource getResource() throws FileNotFoundException;
    List<Resource> getResources() throws FileNotFoundException;
}
