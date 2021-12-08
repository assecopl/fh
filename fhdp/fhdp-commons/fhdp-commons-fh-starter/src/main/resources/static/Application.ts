import * as $ from 'jquery';
import "reflect-metadata";
import {FhApplication} from "fhdp-fh-starter";
import {FHDPCharts} from "fhdp-charts";
import {addTranslationForCookiePolitics} from "fhdp-extenders";
import {initFhCallbacks} from "./FhApplicationCallbacks";
import './Application.css';

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
        additionalModules: [FHDPCharts],
        extensionsConfig: {
            extendFHML: true,
            enableErrorBelowField: false,
            enableFHMLinTooltips: false,
            enableSessionCounterRules: true,
            enableCookieAlert: true,
        }
    })
    initFhCallbacks();
    addTranslationForCookiePolitics('pl', {warning: "Uwaga", message: "Nasza strona używa ciasteczek. Używając jej akceptujesz ten fakt.", close: "Zamknij"});

    fhInstance.init();
});
