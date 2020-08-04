import * as $ from 'jquery';
import "reflect-metadata";
import {FhContainer, I18n} from "fh-forms-handler";
import './Module.css';

import {Connector, SocketHandler, FH, ApplicationLock, CustomActions, Util} from "fh-forms-handler";
import {FormsHandler} from "fh-forms-handler";
import {FhModule} from "fh-forms-handler";
import {BasicControls} from "fh-basic-controls";
import {ChartsControls} from "fh-charts-controls";
import {ModulePL} from "./Module.pl";
import {ModuleEN} from "./Module.en";

class FhApplication {
    static registerModule(module: { new(): FhModule }) {
        new module().init();
    }

    static registerCallback(name: string, callback: () => void) {
        let customActions = FhContainer.get<CustomActions>('CustomActions');

        if (customActions == null) {
            return new Error('CustomActions is not registered.');
        }

        customActions.callbacks[name] = callback;
    }

    static init(context: string = 'socketForms') {
        let util = FhContainer.get<Util>('Util');

        let i18n = FhContainer.get<I18n>('I18n');
        i18n.registerStrings('pl', ModulePL);
        i18n.registerStrings('en', ModuleEN);

        let connector = FhContainer.get<(target: string, reconnectCallback: () => void, openCallback: () => void) => Connector>("Connector")(
            util.getPath(context), () => {
                FhContainer.get<ApplicationLock>('ApplicationLock')
                    .createInfoDialog(i18n.__('error.connection_lost'), null, null, null, null, false);
            }, () => {
                FhContainer.get<ApplicationLock>('ApplicationLock')
                    .closeInfoDialog();
            }
        );
        FhContainer.rebind<Connector>("Connector").toConstantValue(connector);
        FhContainer.get<SocketHandler>('SocketHandler').addConnector(connector);
        FhContainer.get<FH>('FH').init();
    }
}

$(function () {
    FhApplication.registerModule(FormsHandler);
    FhApplication.registerModule(BasicControls);
    FhApplication.registerModule(ChartsControls);

    FhApplication.registerCallback('hideMenu', function () {
        let menu = document.getElementById('menuForm');
        menu.classList.add('d-none');

        let main = document.getElementById('mainForm');
        main.classList.remove('col-md-9', 'col-lg-9', 'col-xl-10');
    });

    FhApplication.registerCallback('showMenu', function () {
        let menu = document.getElementById('menuForm');
        menu.classList.remove('d-none');

        let main = document.getElementById('mainForm');
        main.classList.add('col-md-9', 'col-lg-9', 'col-xl-10');
    });

    // Kalendarz
    FhApplication.registerCallback('calendarInit', function () {
        let cells = document.querySelectorAll('.fc.table.table-hover.table-bordered.table-striped td');

        for (let i = 0; i < cells.length; i++) {
            let cell = cells[i];
            let dayNumber: HTMLElement = cell.querySelector('span.day-number span.fhml-tag-color');

            // @ts-ignore
            let btnOpacity;
            if (dayNumber.style.cssText == 'color: rgb(230, 230, 230);' ||
                dayNumber.style.cssText == 'color: rgb(160, 160, 160);' ||
                dayNumber.style.cssText == 'color: rgb(210, 110, 110);') {
                // @ts-ignore
                cell.style.background = '#f8f9fa';
                btnOpacity = 0.7;
            } else if (dayNumber.style.cssText == 'color: rgb(255, 255, 255);') {
                // @ts-ignore
                cell.style.background = '#f3f2ea';
                btnOpacity = 1.0;
            } else {
                // @ts-ignore
                cell.style.background = '#fff';
                btnOpacity = 1.0;
            }

            let buttons = cell.querySelectorAll('button');
            for (let i = 0; i < buttons.length; i++) {
                buttons[i].style.opacity = btnOpacity;
            }

            cell.addEventListener('click', function () {
                dayNumber.click();
            });
        }
    });

    FhApplication.init();
});
