package pl.fhframework.usecases;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

@Deprecated
public class MetadataUC {
    /**
     * Qualified class name
     */
    @Getter
    private String useCaseId;

    @Getter
    private String label;


    @Getter
    private String image;
    @Getter
    private int coords;
    @Getter
    private String description;
    @Getter
    @JsonIgnore
    private boolean dynamic;



    public MetadataUC(String useCaseId, String label, String image, int coords, String description, boolean dynamic){
        this.useCaseId = useCaseId;
        this.label = label;
        this.image = image;
        this.coords = coords;
        this.description = description;
        this.dynamic = dynamic;
    }


}
