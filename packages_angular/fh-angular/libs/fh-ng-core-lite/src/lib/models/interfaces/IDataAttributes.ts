import {Accessibility, IconAligmentType} from "../CommonTypes";

export interface IDataAttributes {
    accessibility: Accessibility,
    designDeletable: boolean,
    formId: string,
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
    subelements?: any[],
    [name: string]: any
}
