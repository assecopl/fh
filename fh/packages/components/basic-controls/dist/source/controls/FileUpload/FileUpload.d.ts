import { HTMLFormComponent } from 'fh-forms-handler';
declare class FileUpload extends HTMLFormComponent {
    private onUpload;
    private fileIds;
    private readonly extensions;
    private readonly fileNames;
    private readonly label;
    private readonly inputHeight;
    private readonly multiple;
    private labelHidden;
    private progressBar;
    private labelSpanElement;
    private readonly pendingUploadHandle;
    private inputFileButton;
    private readonly style;
    input: any;
    constructor(componentObj: any, parent: HTMLFormComponent);
    create(): void;
    update(change: any): void;
    extractChangedAttributes(): {};
    setPresentationStyle(presentationStyle: string): void;
    setEffectiveLabelAndColor(): void;
    static toDisplaySize(size: number): string;
    setAccessibility(accessibility: string): void;
    /**
     * @Override
     */
    getDefaultWidth(): string;
}
export { FileUpload };
