package pl.fhframework.model;

/**
 * Basic interface for map points.
 */
public interface PointMapTemplate {

    String DEFAULT_IMAGE_PATH = "http://openlayers.org/en/v3.18.2/examples/data/dot.png";

    String DEFAULT_SELECTED_IMAGE_PATH = "https://openlayers.org/en/v3.18.2/examples/data/icon.png";

    long getX();

    long getY();

    long getId();

    default float getAnchorX() {
        return 0.5f;
    }

    default float getAnchorY() {
        return 10.0f;
    }

    default String getImagePath() {
        return DEFAULT_IMAGE_PATH;
    }

    default String getContent() {
        return String.valueOf(getId());
    }
}
