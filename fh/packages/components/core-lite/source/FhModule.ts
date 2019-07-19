abstract class FhModule {
    public init() {
        this.registerComponents();
    }
    protected abstract registerComponents();
}

export {FhModule}