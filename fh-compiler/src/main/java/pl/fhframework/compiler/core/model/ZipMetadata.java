package pl.fhframework.compiler.core.model;

import lombok.Getter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "Files")
public class ZipMetadata {

    @Getter
    private List<ZipFileMetadata> zipFileMetadataList = new ArrayList<>();

    @Getter
    private List<ZipImageFileMetadata> zipImageFileMetadata = new ArrayList<>();

    @Getter
    private List<ZipMarkdownFileMetadata> zipMarkdownFileMetadata = new ArrayList<>();

    @XmlElement(name = "File")
    public void setZipFileMetadataList(List<ZipFileMetadata> zipFileMetadataList) {
        this.zipFileMetadataList = zipFileMetadataList;
    }

    @XmlElement(name = "Image")
    public void setZipImageFileMetadata(List<ZipImageFileMetadata> zipImageFileMetadata) {
        this.zipImageFileMetadata = zipImageFileMetadata;
    }

    @XmlElement(name = "Markdown")
    public void setZipMarkdownFileMetadata(List<ZipMarkdownFileMetadata> zipMarkdownFileMetadata) {
        this.zipMarkdownFileMetadata = zipMarkdownFileMetadata;
    }
}
