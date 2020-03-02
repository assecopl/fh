declare class CustomActions {
    private formsManager;
    private _callbacks;
    CustomActions(): void;
    get callbacks(): {
        [key: string]: (data: string) => void;
    };
}
export { CustomActions };
