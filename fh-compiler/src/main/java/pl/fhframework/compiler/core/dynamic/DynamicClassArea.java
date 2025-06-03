package pl.fhframework.compiler.core.dynamic;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Dynamic classes area
 */
@NoArgsConstructor
@Getter
public enum DynamicClassArea {

    // WARNING: ORDER OF THESE ELEMENTS IS IMPORTANT!
    // CLASSES ARE COMPILED IN THIS ORDER AND CANNOT DEPEND ON CLASSES FROM AREAS PLACES LATER ON THIS LIST.

    MODEL(true),
    RULE,
    SERVICE,
    FORM,
    USE_CASE,
    JR_REPORT;

    private boolean aspectWeavingNeeded = false;

    private boolean precompile = true;

    DynamicClassArea(boolean aspectWeavingNeeded) {
        this.aspectWeavingNeeded = aspectWeavingNeeded;
    }

    DynamicClassArea(boolean aspectWeavingNeeded, boolean precompile) {
        this.aspectWeavingNeeded = aspectWeavingNeeded;
        this.precompile = precompile;
    }
}
