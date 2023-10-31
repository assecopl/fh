import {IconAligmentType} from "../CommonTypes";

export interface IDataAttributes {
  accessibility: string,
    designDeletable: boolean,
    formId: string,
    height?: string;
    hintType: string,
    icon: string,
    iconAlignment: IconAligmentType,
    id: string,
    inlineStyle?: string,
    invisible: boolean,
    onClick: string,
    push: boolean,
    type: string,
    value: string,
    width: string,
    style?: string,
    title?: string,
    ariaLabel?: string,
    formType?: string,
    subelements?: any[],
    activeTabIndex?: number,
    [name: string]: any
}
