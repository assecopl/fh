export interface ITree {
  id?: number;
  label: string;
  icon?: string;
  children?: ITree[];
  useCase?: any;
}
