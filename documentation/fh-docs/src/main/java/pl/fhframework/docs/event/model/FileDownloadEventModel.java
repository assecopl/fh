package pl.fhframework.docs.event.model;

import org.springframework.core.io.Resource;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileDownloadEventModel {

    private Resource modelBindingResource;
}
