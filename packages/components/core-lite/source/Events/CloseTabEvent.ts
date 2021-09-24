import {BaseEvent} from "./BaseEvent";

class CloseTabEvent extends BaseEvent {
    public fire(data: { uuid: string }) {
        // @ts-ignore
        let openedExternalUseCases: Map<string, Window> = window.openedExternalUseCases;

        if (!openedExternalUseCases) return;

        if (openedExternalUseCases.has(data.uuid)) {
            let tab = openedExternalUseCases.get(data.uuid);
            tab.close();

            openedExternalUseCases.delete(data.uuid);
        }
    }
}

export {CloseTabEvent};
