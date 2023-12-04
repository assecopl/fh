import * as $ from 'jquery';
import "reflect-metadata";
import {FhApplication} from "fhdp-fh-starter";
import {initFhCallbacks} from "./FhApplicationCallbacks";
import './Application.css';
import {FhContainer} from 'fh-forms-handler';
import {DictionaryLookup} from "./DictionaryLookup";

function closeTooltipByBodyEvent() {
    const body = document.getElementsByTagName("body")[0];
    const tooltip = document.getElementsByClassName("tooltip");

    while(tooltip.length > 0){
        tooltip[0].parentNode.removeChild(tooltip[0]);
    }
    body.removeEventListener("mousedown", closeTooltipByBodyEvent);
}

$(function () {
    const fhInstance = FhApplication.getInstance({
        registerStandardModules: true,
        registerChartsControls: true,
        registerFHDPControls: true,
        extensionsConfig: {

            extendFHML: true,
            enableErrorBelowField: false,
            enableFHMLinTooltips: false,
            enableSessionCounterRules: true,
            enableCookieAlert: true,
        }
    })
    initFhCallbacks();

    FhContainer.bind<(componentObj: any, parent: any) => DictionaryLookup>("DictionaryLookupFhDP")
        .toFactory<DictionaryLookup>(() => {
            return (componentObj: any, parent: any) => {
                return new DictionaryLookup(componentObj, parent);
            };
        });

    fhInstance.init();
});
