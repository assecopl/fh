package pl.fhframework.model.forms;

import lombok.Getter;
import lombok.Setter;

/**
 * This is not part of component element - this is managed by image
 */
@Getter
@Setter
public class ImageArea {
    private String id;
    private int xl, xp, yl, yp;

    public ImageArea(){}

    public ImageArea(String id, int xl, int yl, int xp, int yp){
        this.id = id;
        this.xl = xl;
        this.yl = yl;
        this.xp = xp;
        this.yp = yp;
    }
}
