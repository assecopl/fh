export interface ITreeComponent {
    id?: number;
    label: string;
    icon?: string;
    children?: ITreeComponent[];
    useCase?: any;
}
