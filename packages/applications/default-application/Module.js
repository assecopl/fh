"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var $ = require("jquery");
require("reflect-metadata");
var fh_forms_handler_1 = require("fh-forms-handler");
require("./Module.css");
var fh_forms_handler_2 = require("fh-forms-handler");
var fh_basic_controls_1 = require("fh-basic-controls");
var fh_charts_controls_1 = require("fh-charts-controls");
var Module_pl_1 = require("./Module.pl");
var Module_en_1 = require("./Module.en");
var FhApplication = /** @class */ (function () {
    function FhApplication() {
    }
    FhApplication.registerModule = function (module) {
        new module().init();
    };
    FhApplication.registerCallback = function (name, callback) {
        var customActions = fh_forms_handler_1.FhContainer.get('CustomActions');
        if (customActions == null) {
            return new Error('CustomActions is not registered.');
        }
        customActions.callbacks[name] = callback;
    };
    FhApplication.init = function (context) {
        if (context === void 0) { context = 'socketForms'; }
        var util = fh_forms_handler_1.FhContainer.get('Util');
        var i18n = fh_forms_handler_1.FhContainer.get('I18n');
        i18n.registerStrings('pl', Module_pl_1.ModulePL);
        i18n.registerStrings('en', Module_en_1.ModuleEN);
        var connector = fh_forms_handler_1.FhContainer.get("Connector")(util.getPath(context), function () {
            fh_forms_handler_1.FhContainer.get('ApplicationLock')
                .createInfoDialog(i18n.__('error.connection_lost'), null, null, null, null, false);
        }, function () {
            fh_forms_handler_1.FhContainer.get('ApplicationLock')
                .closeInfoDialog();
        });
        fh_forms_handler_1.FhContainer.rebind("Connector").toConstantValue(connector);
        fh_forms_handler_1.FhContainer.get('SocketHandler').addConnector(connector);
        fh_forms_handler_1.FhContainer.get('FH').init();
    };
    return FhApplication;
}());
$(function () {
    FhApplication.registerModule(fh_forms_handler_2.FormsHandler);
    FhApplication.registerModule(fh_basic_controls_1.BasicControls);
    FhApplication.registerModule(fh_charts_controls_1.ChartsControls);
    FhApplication.registerCallback('hideMenu', function () {
        var menu = document.getElementById('menuForm');
        menu.classList.add('d-none');
    });
    FhApplication.registerCallback('callUcAction', function (actionName) {
        fh_forms_handler_1.FhContainer.get('ServiceManagerUtil').callAction('callUcAction', actionName);
    });
    FhApplication.registerCallback('showMenu', function () {
        var menu = document.getElementById('menuForm');
        menu.classList.remove('d-none');
    });
    // Kalendarz
    FhApplication.registerCallback('calendarInit', function () {
        var cells = document.querySelectorAll('.fc.table.table-hover.table-bordered.table-striped td');
        var _loop_1 = function (i) {
            var cell = cells[i];
            var dayNumber = cell.querySelector('span.day-number span.fhml-tag-color');
            // @ts-ignore
            var btnOpacity = void 0;
            if (dayNumber.style.cssText == 'color: rgb(230, 230, 230);' ||
                dayNumber.style.cssText == 'color: rgb(160, 160, 160);' ||
                dayNumber.style.cssText == 'color: rgb(210, 110, 110);') {
                // @ts-ignore
                cell.style.background = '#f8f9fa';
                btnOpacity = 0.7;
            }
            else if (dayNumber.style.cssText == 'color: rgb(255, 255, 255);') {
                // @ts-ignore
                cell.style.background = '#f3f2ea';
                btnOpacity = 1.0;
            }
            else {
                // @ts-ignore
                cell.style.background = '#fff';
                btnOpacity = 1.0;
            }
            var buttons = cell.querySelectorAll('button');
            for (var i_1 = 0; i_1 < buttons.length; i_1++) {
                buttons[i_1].style.opacity = btnOpacity;
            }
            cell.addEventListener('click', function () {
                dayNumber.click();
            });
        };
        for (var i = 0; i < cells.length; i++) {
            _loop_1(i);
        }
    });
    FhApplication.init();
});
//# sourceMappingURL=Module.js.map