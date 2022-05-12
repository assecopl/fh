import './source/Module.css';
import {EmbeddedView} from './source/controls/EmbeddedView';
import {FileUploadPortletFhDP} from './source/controls/FileUploadPortletFhDP';
import {ComboFhDP} from "./source/controls/inputs/ComboFhDP";
import {DictionaryComboFhDP} from "./source/controls/inputs/DictionaryComboFhDP";
import {InputDateFhDP} from "./source/controls/inputs/InputDateFhDP";
import {InputNumberFhDP} from "./source/controls/inputs/InputNumberFhDP";
import {InputTextFhDP} from "./source/controls/inputs/InputTextFhDP";
import {InputTimestampFhDP} from "./source/controls/inputs/InputTimestampFhDP";
import {SelectComboMenuFhDP} from "./source/controls/inputs/SelectComboMenuFhDP";
import {BaseEvent, FhContainer, FhModule, I18n} from "fh-forms-handler";
import {PanelFhDP} from "./source/controls/PanelFhDP";
import {PanelHeaderFhDP} from "./source/controls/PanelHeaderFhDP";
import {FHMLStandalone} from "./source/controls/FHMLStandalone";
import {TabContainerFhDP} from './source/controls/tabs/TabContainerFhDP';
import {TreeElementFhDP} from './source/controls/TreeElementFhDP';
import {ShutdownEventLT} from "./source/i18n/ShutdownEvent.lt"
import {ConnectorLT} from "./source/i18n/Connector.lt";
import {ApplicationLockLT} from "./source/i18n/ApplicationLock.lt";
import {FileUploadLT} from "./source/i18n/FileUpload.lt"
import {FileUploadRU} from "./source/i18n/FileUpload.ru"
import {TreeFhDP} from './source/controls/TreeFhDP';
import {TablePagedLT} from "./source/i18n/TablePaged.lt"
import {HtmlViewExtended} from "./source/controls/HtmlViewExtended"
import {HTMLRawViewFhDP} from "./source/controls/HTMLRawViewFhDP"
import {CheckBoxFhDP} from './source/controls/inputs/CheckBoxFhDP';
import {NotificationEventFhDP} from './source/controls/NotificationEventFhDP';
import {XMLViewerFhDP} from './source/controls/XMLViewerFhDP';
import {RegionPickerFhDP} from './source/controls/RegionPickerFhDP';
import * as pack from './package.json';
import {MSReportsView} from './source/controls/MSReportsView';
import {InputTimeFhDP} from './source/controls/inputs/InputTimeFhDP';
import {TimerFhDP} from './source/controls/TimerFhDP';

class FhDPControls extends FhModule {

    static skipFileUploadPortlet = false;

    protected registerComponents() {
        const i18n = FhContainer.get<I18n>('I18n');
        //Register language modules
        // console.log('HERE:',i18n.selectedLanguage);
        i18n.supportedLanguages.push('pl');
        i18n.supportedLanguages.push('lt');
        i18n.registerStrings('lt', ShutdownEventLT, true);
        i18n.registerStrings('lt', ConnectorLT, true);
        i18n.registerStrings('lt', ApplicationLockLT, true);
        i18n.registerStrings('lt', FileUploadLT, true);
        i18n.registerStrings('ru', FileUploadRU, true);
        i18n.registerStrings('lt', TablePagedLT, true);
        let language = (window.navigator as any).userLanguage || window.navigator.language;
        if (i18n.supportedLanguages.indexOf(language) > -1) {
            i18n.selectLanguage(language);
        } else {
            try {
                if (window.top === window) {
                    language = document.documentElement.lang;
                    language = language.split("-");
                } else {
                    language = window.top.document.documentElement.lang;
                    language = language.split("-");
                }
                if (i18n.supportedLanguages.indexOf(language[0]) > -1) {
                    i18n.selectLanguage(language[0]);
                } else {
                    i18n.selectLanguage(i18n.supportedLanguages[0]);
                }
            } catch (e) {
                i18n.selectLanguage(i18n.supportedLanguages[0]);
            }
        }

        FhContainer.bind<(componentObj: any, parent: any) => EmbeddedView>("EmbeddedView")
            .toFactory<EmbeddedView>(() => {
                return (componentObj: any, parent: any) => {
                    return new EmbeddedView(componentObj, parent);
                };
            });

        FhContainer.bind<(componentObj: any, parent: any) => PanelFhDP>("PanelFhDP")
            .toFactory<PanelFhDP>(() => {
                return (componentObj: any, parent: any) => {
                    return new PanelFhDP(componentObj, parent);
                };
            });

        FhContainer.bind<(componentObj: any, parent: any) => PanelHeaderFhDP>("PanelHeaderFhDP")
            .toFactory<PanelHeaderFhDP>(() => {
                return (componentObj: any, parent: any) => {
                    return new PanelHeaderFhDP(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => ComboFhDP>("ComboFhDP")
            .toFactory<ComboFhDP>(() => {
                return (componentObj: any, parent: any) => {
                    return new ComboFhDP(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => DictionaryComboFhDP>("DictionaryComboFhDP")
            .toFactory<DictionaryComboFhDP>(() => {
                return (componentObj: any, parent: any) => {
                    return new DictionaryComboFhDP(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => InputDateFhDP>("InputDateFhDP")
            .toFactory<InputDateFhDP>(() => {
                return (componentObj: any, parent: any) => {
                    return new InputDateFhDP(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => InputNumberFhDP>("InputNumberFhDP")
            .toFactory<InputNumberFhDP>(() => {
                return (componentObj: any, parent: any) => {
                    return new InputNumberFhDP(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => InputTextFhDP>("InputTextFhDP")
            .toFactory<InputTextFhDP>(() => {
                return (componentObj: any, parent: any) => {
                    return new InputTextFhDP(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => InputTimestampFhDP>("InputTimestampFhDP")
            .toFactory<InputTimestampFhDP>(() => {
                return (componentObj: any, parent: any) => {
                    return new InputTimestampFhDP(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => InputTimeFhDP>("InputTimeFhDP")
            .toFactory<InputTimeFhDP>(() => {
                return (componentObj: any, parent: any) => {
                    return new InputTimeFhDP(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => SelectComboMenuFhDP>("SelectComboMenuFhDP")
            .toFactory<SelectComboMenuFhDP>(() => {
                return (componentObj: any, parent: any) => {
                    return new SelectComboMenuFhDP(componentObj, parent);
                };
            });

        FhContainer.bind<(componentObj: any, parent: any) => FHMLStandalone>("FHMLStandalone")
            .toFactory<FHMLStandalone>(() => {
                return (componentObj: any, parent: any) => {
                    return new FHMLStandalone(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => TabContainerFhDP>("TabContainerFhDP")
        .toFactory<TabContainerFhDP>(() => {
            return (componentObj: any, parent: any) => {
                return new TabContainerFhDP(componentObj, parent);
            };
        });

        FhContainer.bind<(componentObj: any, parent: any) => TreeFhDP>("TreeFhDP")
        .toFactory<TreeFhDP>(() => {
            return (componentObj: any, parent: any) => {
                return new TreeFhDP(componentObj, parent);
            };
        });

        FhContainer.bind<(componentObj: any, parent: any) => TreeElementFhDP>("TreeElementFhDP")
        .toFactory<TreeElementFhDP>(() => {
            return (componentObj: any, parent: any) => {
                return new TreeElementFhDP(componentObj, parent);
            };
        });
        FhContainer.rebind<(componentObj: any, parent: any) => InputDateFhDP>("InputDate")
            .toFactory<InputDateFhDP>(() => {
                return (componentObj: any, parent: any) => {
                    return new InputDateFhDP(componentObj, parent);
                };
            });

        FhContainer.rebind<(componentObj: any, parent: any) => InputTimestampFhDP>("InputTimestamp")
            .toFactory<InputTimestampFhDP>(() => {
                return (componentObj: any, parent: any) => {
                    return new InputTimestampFhDP(componentObj, parent);
                };
            });
        FhContainer.rebind<(componentObj: any, parent: any) => HtmlViewExtended>("HtmlView")
            .toFactory<HtmlViewExtended>(() => {
                return (componentObj: any, parent: any) => {
                    return new HtmlViewExtended(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => HTMLRawViewFhDP>("HTMLRawViewFhDP")
            .toFactory<HTMLRawViewFhDP>(() => {
                return (componentObj: any, parent: any) => {
                    return new HTMLRawViewFhDP(componentObj, parent);
                };
            });
        FhContainer.rebind<(componentObj: any, parent: any) => CheckBoxFhDP>("CheckBox")
            .toFactory<CheckBoxFhDP>(() => {
                return (componentObj: any, parent: any) => {
                    return new CheckBoxFhDP(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => CheckBoxFhDP>("CheckBoxFhDP")
            .toFactory<CheckBoxFhDP>(() => {
                return (componentObj: any, parent: any) => {
                    return new CheckBoxFhDP(componentObj, parent);
                };
            });

        FhContainer.bind<(componentObj: any, parent: any) => XMLViewerFhDP>("XMLViewerFhDP")
            .toFactory<XMLViewerFhDP>(() => {
                return (componentObj: any, parent: any) => {
                    return new XMLViewerFhDP(componentObj, parent);
                };
            });

        FhContainer.bind<(componentObj: any, parent: any) => FileUploadPortletFhDP>("FileUploadPortletFhDP")
            .toFactory<FileUploadPortletFhDP>(() => {
                return (componentObj: any, parent: any) => {
                    return new FileUploadPortletFhDP(componentObj, parent);
                };
            });

        FhContainer.bind<(componentObj: any, parent: any) => RegionPickerFhDP>("RegionPickerFhDP")
            .toFactory<RegionPickerFhDP>(() => {
                return (componentObj: any, parent: any) => {
                    return new RegionPickerFhDP(componentObj, parent);
                };
            });

        FhContainer.bind<(componentObj: any, parent: any) => MSReportsView>("MSReportsView")
            .toFactory<MSReportsView>(() => {
                return (componentObj: any, parent: any) => {
                    return new MSReportsView(componentObj, parent);
                };
            });

        FhContainer.bind<(componentObj: any, parent: any) => TimerFhDP>("TimerFhDP")
                .toFactory<TimerFhDP>(() => {
                    return (componentObj: any, parent: any) => {
                        return new TimerFhDP(componentObj, parent);
                    };
                });
        
        FhContainer.rebind<BaseEvent>('Events.NotificationEvent').to(NotificationEventFhDP).inRequestScope();

        console.log(`FhDP-controlls version: ${pack.version}`)
    }
}

export {FhDPControls, EmbeddedView, PanelFhDP, PanelHeaderFhDP, ComboFhDP, DictionaryComboFhDP, InputDateFhDP,
    InputNumberFhDP, InputTextFhDP, InputTimestampFhDP, SelectComboMenuFhDP, TabContainerFhDP, CheckBoxFhDP,
    InputTimeFhDP, MSReportsView, TimerFhDP}
