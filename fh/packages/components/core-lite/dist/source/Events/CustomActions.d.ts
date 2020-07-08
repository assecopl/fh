declare class CustomActions {
    private formsManager;
    private _callbacks;
    CustomActions(): void;
    readonly callbacks: {
        [key: string]: (data: string) => void;
    };
}
export { CustomActions };
