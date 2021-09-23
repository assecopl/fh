interface Extender {
    componentName: string;
    update?: (changedParams, params, obj) => void;
    create?: (params, obj) => void;
    destroy?: (params, obj) => void;
}

class ComponentExtender {
    private static __instance: ComponentExtender;
    private __store: Array<Extender>;
    static getInstance() {
        if (!ComponentExtender.__instance) {
            ComponentExtender.__instance = new ComponentExtender();
        }
        return ComponentExtender.__instance;
    }

    constructor() {
        this.__store = [];
    }

    addStaticExtender(ext: Extender) {
        this.__store.push(ext);
    }

    getStaticExtenders(componentName: string, fn?: 'update'|'create'|'destroy'|undefined) {
        if (!fn) {
            return this.__store.slice().filter((el: Extender) => el.componentName === componentName);
        } else {
            const hooks = this.__store.slice().filter((el: Extender) => el.componentName === componentName);
            return hooks.map((el: Extender) => el[fn]);
        }
    }
}

export {ComponentExtender};