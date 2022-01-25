import "reflect-metadata";
import {
    ApplicationLock,
    Connector,
    CustomActions,
    FH,
    FhContainer,
    FhModule,
    FormsHandler,
    I18n,
    ServiceManagerUtil,
    SocketHandler,
    Util
} from "fh-forms-handler";
import {BasicControls} from "fh-basic-controls";
import {ChartsControls} from "fh-charts-controls";
import {FhDPControls} from "fhdp-controls";
import {DynamicRelocatorExclusionRule, DynamicRelocatorExtensionRule, getCookie, FHDPExtender} from 'fhdp-extenders';

export interface LiferayConfig {
    /** Defines if liferay is enabled or not */
    enabled: boolean;
    /** Local websocket addres For debug */
    localPumaWsURL: string;
    /** Remote websocket address */
    fhBaseUrl: string;
    /** Remote context path */
    fhContextPath: string;
}

export interface ExtenderConfig {
    extendFHML?: boolean;
    mobileMaxWidth?: number;
    enableMobileNavbar?: boolean;
    enableErrorBelowField?: boolean;
    enableDynamicFooterPosition?: boolean;
    enableCurrentMenuElementHighlight?: boolean;
    currentMenuElementHighlighBottomBorderStyle?: string;
    enableFHMLinTooltips?: boolean;
    enableSessionCounterRules?: boolean;
    enableCookieAlert?: boolean;
    additionalRules?: Array<DynamicRelocatorExclusionRule | DynamicRelocatorExtensionRule>;
}

export interface FhApplicationConfig {
    /** Defines registration of standard modules (FormsHandler and BasicControls) */
    registerStandardModules?: boolean;
    /** Defines registration of Charts module */
    registerChartsControls?: boolean;
    /** Defines registration of FHDP controls module*/
    registerFHDPControls?: boolean;
    /** Defines registration of additional modules*/
    additionalModules?: ({ new(): FhModule})[];
    /** Defines translations for connection lost message Default contains messages for polish and english languages */
    connectionLostTranslations?: {[key: string]: string};
    /** Defines config for liferay If empty it will act as normal application */
    liferay?: LiferayConfig;
    /** Defines config for FHDPExtender If empty it will act as normal fh app without extensions */
    extensionsConfig?: ExtenderConfig
}

type ConnectorType = (target: string, reconnectCallback: () => void, openCallback: () => void) => Connector;

class FhApplication {
    /** Instance of FhApplication */
    private static __instance: FhApplication;

    /** Current config */
    private __config: FhApplicationConfig;

    /** Instance of Liferay config */
    private __liferay?: LiferayConfig;

    /** Instance of connector */
    private __connector?: Connector;

    /**
     * Private and used internally
     * @param config main configuration
     */
    private constructor(config: FhApplicationConfig) {
        this.__config = config;
        if (config.registerStandardModules) {
            this.registerModule(FormsHandler);
            this.registerModule(BasicControls);
        }
        if (config.registerChartsControls) {
            this.registerModule(ChartsControls);
        }
        if (config.registerFHDPControls) {
            this.registerModule(FhDPControls);
        }
        if (config.additionalModules) {
            config.additionalModules.forEach(mod => {
                this.registerModule(mod);
            })
        }
        if (config.liferay && config.liferay.enabled) {
            this.__liferay = config.liferay;
        }


    }

    /**
     * At first use must be called with `config` param, to properly init instance
     * Later uses can be without param, and it will return same instance as one inited at begin
     * @param config main configuration
     */
    static getInstance(config?: FhApplicationConfig) {
        if (!FhApplication.__instance && config) {
            FhApplication.__instance = new FhApplication(config);
        } else if (!FhApplication.__instance && !config) {
            console.error('Init instance first with config');
        }
        return FhApplication.__instance;
    }

    /**
     * Getter for connection lost translations
     */
    private get currentConnectionLostTranslation(): string {
        let i18n = FhContainer.get<I18n>('I18n');
        return i18n.translateString('connectionLostRetry', undefined, i18n.selectedLanguage);
    }

    private isJSON(data:string) {
        try {
            return JSON.parse(data);
        } catch (e) {
            return false;
        }
    }

    /**
     * Getter for connector;
     * @returns Connector
     */
    getConnector(): Connector {
        return this.__connector;
    }

    /**
     * Registers new module Its alternative function to config node `additionalModules`
     * @param module an module that will be registered in fh app
     */
    registerModule(module: { new(): FhModule}) {
        new module().init();
    }

    /**
     * Registers callback for backend calls
     * @param name callback name (key)
     * @param callback actual callback function
     */
    registerCallback(name: string, callback: (...args) => void) {
        let customActions = FhContainer.get<CustomActions>('CustomActions');

        if (customActions == null) {
            return new Error('CustomActions is not registered.');
        }

        customActions.callbacks[name] = callback;
    }

    /**
     * Function for initialise extenders
     */
    private initExtenders() {
        const i18n = FhContainer.get<I18n>('I18n');
        new FHDPExtender({
            i18n,
            ...this.__config.extensionsConfig
        });
    }

    /**
     * Adds translation string
     */
    public addTranslation(lang, key, translation) {
        let i18n = FhContainer.get<I18n>('I18n');
        i18n.registerStrings(
            lang,
            {
                [key]: translation
            },
            true
        );
    }

    /**
     * get translation string
     */
    public translate(key, args) {
        let i18n = FhContainer.get<I18n>('I18n');
        return i18n.translateString(key, args, i18n.selectedLanguage);
    }

    /**
     * Initialise translations for connection lost
     */
    private initConnectionLostTranslations() {
        let i18n = FhContainer.get<I18n>('I18n');
        i18n.registerStrings(
            'pl',
            {
                connectionLostRetry: 'Połączenie z serwerem zostało przerwane. Ponawiam próbę połączenia...'
            },
            true
        );
        i18n.registerStrings(
            'en',
            {
                connectionLostRetry: 'Connection with server is broken. Trying to reconnect...'
            },
            true
        );
        if (this.__config.connectionLostTranslations) {
            Object.keys(this.__config.connectionLostTranslations).forEach(iso => {
                i18n.registerStrings(
                    iso,
                    {
                        connectionLostRetry: this.__config.connectionLostTranslations[iso]
                    },
                    true
                );
            })
        }
    }

    /**
     * Main init function
     * @param context app context for sockets Default 'socketForms'
     */
    init(context: string = 'socketForms') {
        let util = FhContainer.get<Util>('Util');
        let i18n = FhContainer.get<I18n>('I18n');
        if (this.__config.extensionsConfig) {
            this.initExtenders();
        }
        this.initConnectionLostTranslations();

        let contextPath: string;
        if (this.__liferay) {
            if (this.__liferay.fhBaseUrl != null) {
                contextPath = `${this.__liferay.fhBaseUrl.replace('http:/', 'ws:/').replace('https:/', 'wss:/')}${this.__liferay.fhContextPath}/socketForms`;
            } else {
                contextPath = this.__liferay.localPumaWsURL;
            }
        } else {
            contextPath = util.getPath(context);
        }

        this.__connector = FhContainer.get<ConnectorType>("Connector")(
            contextPath, () => {
                FhContainer.get<ApplicationLock>('ApplicationLock')
                    .createInfoDialog(this.currentConnectionLostTranslation);
            }, () => {
                FhContainer.get<ApplicationLock>('ApplicationLock')
                    .closeInfoDialog();
            }
        );

        if (this.__liferay && (window as any).Liferay) {
            this.__connector.incomingMessageCallback = function (data) {
                const isJSON = (data:any) => {
                    try {
                        return JSON.parse(data);
                    } catch (e) {
                        return false;
                    }
                }
                const json = isJSON(data);
                console.log('incomingMessageCallback');
                if ((window as any).Liferay.Session && (!json || json.eventType !== 'onTimer')) {
                    (window as any).Liferay.Session.extend();
                }
            };
            this.__connector.outcomingMessageCallback = function (data) {
                const isJSON = (data:any) => {
                    try {
                        return JSON.parse(data);
                    } catch (e) {
                        return false;
                    }
                }
                const json = isJSON(data);
                console.log('outcomingMessageCallback');
                if ((window as any).Liferay.Session && (!json || json.eventType !== 'onTimer')) {
                    (window as any).Liferay.Session.extend();
                }
                // after init discard any query parameters from url to prevent repeated actions (e.g. redirect from external page with action in parameter)
                if (json && json.command == 'Init') {
                    history.replaceState(history.state, document.title, location.origin + location.pathname);
                    if (parent) {
                        parent.history.replaceState(parent.history.state, parent.document.title, parent.location.origin + parent.location.pathname);
                    }
                }
            };
        }

        FhContainer.rebind<Connector>("Connector").toConstantValue(this.__connector);
        FhContainer.get<SocketHandler>('SocketHandler').addConnector(this.__connector);

        try {
            const cookie = getCookie();
            if(cookie && cookie['GUEST_LANGUAGE_ID']) {
                i18n.selectLanguage(cookie['GUEST_LANGUAGE_ID']);
                console.log("LANGUAGE SELECTED ON START(GUEST_LANGUAGE_ID): ", cookie['GUEST_LANGUAGE_ID'])
            } else if (cookie && cookie['USERLANG']) {
                i18n.selectLanguage(cookie['USERLANG']);
                console.log("LANGUAGE SELECTED ON START: ", cookie['USERLANG'])
            }
        } catch (e) {
            console.warn(e);
        }
        if (this.__liferay) {
            FhContainer.get<FH>('FH').initExternal(this.__liferay.localPumaWsURL);
        } else {
            FhContainer.get<FH>('FH').init();
        }
        this.registerCallback('callUcAction', (actionName) => {
            FhContainer.get<ServiceManagerUtil>('ServiceManagerUtil').callAction('callUcAction', actionName);
        });
    }

    /**
     * Edits classlist of element provided by id
     * @param id Id of DOM element
     * @param remove class name that should be removed from element
     * @param add class name that should be added in element
     */
    createCallbackById(id: string, remove?: string, add?: string) {
        const element = document.getElementById(id);
        if(!!element) {
            if(!!remove) {
                element.classList.remove(remove);
            }
            if(!!add) {
                element.classList.add(add);
            }
        }
    }

    /**
     * Edits classlist of element wrapper provided by id
     * @param id Id of DOM element
     * @param remove class name that should be removed from element wrapper
     * @param add class name that should be added in element wrapper
     */
    createCallbackByWrapper(id: string, remove?: string, add?: string) {
        const element = document.getElementById(id);
        if(!!element) {
            const wrapper = (element.parentNode as HTMLElement);
            if(!!wrapper) {
                if(!!remove) {
                    wrapper.classList.remove(remove);
                }
                if(!!add) {
                    wrapper.classList.add(add);
                }
            }
        }
    }
}

export {FhApplication};
