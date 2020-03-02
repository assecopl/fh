"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
Object.defineProperty(exports, "__esModule", { value: true });
var $ = require("jquery");
var inversify_inject_decorators_1 = require("inversify-inject-decorators");
require("bootstrap/js/dist/modal");
require("bootstrap/js/dist/tooltip");
require("jquery-ui/ui/widgets/dialog");
var inversify_1 = require("inversify");
var I18n_1 = require("../I18n/I18n");
var FhContainer_1 = require("../FhContainer");
var ApplicationLock_en_1 = require("../I18n/ApplicationLock.en");
var ApplicationLock_pl_1 = require("../I18n/ApplicationLock.pl");
var lazyInject = inversify_inject_decorators_1.default(FhContainer_1.FhContainer).lazyInject;
var ApplicationLock = /** @class */ (function () {
    function ApplicationLock() {
        this.rids = [];
        this.i18n.registerStrings('en', ApplicationLock_en_1.ApplicationLockEN);
        this.i18n.registerStrings('pl', ApplicationLock_pl_1.ApplicationLockPL);
        var lock = document.createElement('div');
        lock.id = 'applicationLock';
        lock.classList.add('modal-backdrop');
        // faded backdrop is causing problems in frequent event firing - duplicates and doesn't remove modal-backdrops
        //modal.classList.add('fade');
        lock.tabIndex = -1;
        var frame = document.createElement('div');
        frame.id = 'applicationLockFrame';
        frame.classList.add('align-self-center');
        var spinner = document.createElement('div');
        spinner.classList.add('spinner-grow');
        spinner.classList.add('text-light');
        var sr = document.createElement('span');
        sr.classList.add('sr-only');
        sr.innerText = 'Loading...';
        spinner.appendChild(sr);
        frame.appendChild(spinner);
        lock.appendChild(frame);
        this.lockElement = lock;
        document.body.appendChild(this.lockElement);
    }
    ApplicationLock_1 = ApplicationLock;
    ApplicationLock.prototype.enable = function (requestId) {
        if (!this.rids.length) {
            $(this.lockElement).css('opacity', 0);
            $(this.lockElement).show();
            if (this.opacityTimer == null) {
                this.opacityTimer = setTimeout(function () {
                    if (this.rids.length) {
                        $(this.lockElement).css('opacity', 1);
                    }
                }.bind(this), 1000);
            }
        }
        if (requestId) {
            this.rids.push(requestId);
        }
    };
    ApplicationLock.prototype.disable = function (requestId) {
        if (this.rids.indexOf(requestId) > -1) {
            this.rids.splice(this.rids.indexOf(requestId), 1);
            if (!this.rids.length) {
                $(this.lockElement).hide();
                if (this.opacityTimer != null) {
                    clearTimeout(this.opacityTimer);
                    this.opacityTimer = null;
                }
            }
        }
    };
    ApplicationLock.prototype.isActive = function () {
        return this.rids.length;
    };
    // TODO: refactor
    ApplicationLock.prototype.createErrorDialog = function (data, callback, withClose) {
        var _this = this;
        if (callback === void 0) { callback = undefined; }
        if (withClose === void 0) { withClose = true; }
        var dialog = document.createElement('div');
        dialog.classList.add('fh-error-dialog');
        var close = document.createElement('span');
        close.classList.add('close');
        close.innerHTML = '&times;';
        close.addEventListener('click', function () {
            ApplicationLock_1.closeErrorDialog(dialog);
        }.bind(this));
        var content = document.createElement('div');
        content.classList.add('content');
        if (data && data.length > 0) {
            var wrapper = document.createElement('div');
            var modalHeader = this.createElement('div', "modal-header");
            var h5 = this.createElement('h5', "modal-title");
            h5.innerText = this.i18n.__("error.title");
            modalHeader.appendChild(h5);
            wrapper.appendChild(modalHeader);
            var modalBody = this.createElement('div', 'modal-body');
            var row = this.createElement('div', 'row eq-row');
            modalBody.appendChild(row);
            var spacer = this.createElement('div', 'fc wrapper col-md-12 spacer');
            spacer.appendChild(document.createElement('div'));
            row.appendChild(spacer);
            var innerRow = this.createElement('div', 'fc wrapper col-md-12');
            var innerRow2 = document.createElement('div');
            innerRow.appendChild(innerRow2);
            var innerRow3_1 = this.createElement('div', 'fc wrapper inline');
            innerRow2.appendChild(innerRow3_1);
            row.appendChild(innerRow);
            data.forEach(function (entry) {
                var date = entry.timestamp.year + '.';
                date += (entry.timestamp.monthValue < 10 ? '0' : '') + entry.timestamp.monthValue + '.';
                date += (entry.timestamp.dayOfMonth < 10 ? '0' : '') + entry.timestamp.dayOfMonth;
                var time = (entry.timestamp.hour < 10 ? '0' : '') + entry.timestamp.hour + ':';
                time += (entry.timestamp.minute < 10 ? '0' : '') + entry.timestamp.minute;
                var text = date + ' ' + time;
                text += ' ' + entry.message;
                var span = _this.createElement('span', 'fc outputLabel');
                span.appendChild(document.createTextNode(text));
                innerRow3_1.appendChild(span);
                innerRow3_1.appendChild(document.createElement('p'));
            });
            var closeDiv = this.createElement('div', 'fc wrapper inline col-md-4 form-group');
            var closeButton = this.createElement('button', 'fc button btn btn-primary btn-block');
            closeButton.appendChild(document.createTextNode(this.i18n.__("close.label")));
            closeButton.addEventListener('click', function () {
                ApplicationLock_1.closeErrorDialog(dialog);
            }.bind(this));
            closeDiv.appendChild(closeButton);
            row.appendChild(closeDiv);
            wrapper.appendChild(modalBody);
            content.appendChild(wrapper);
        }
        if (withClose) {
            dialog.appendChild(close);
        }
        dialog.appendChild(content);
        if (typeof callback === 'function') {
            callback(content, data);
        }
        $(dialog).dialog({ modal: true, width: 600 }).siblings('.ui-dialog-titlebar').remove();
        $(dialog).dialog("moveToTop");
        return dialog;
    };
    ApplicationLock.prototype.createElement = function (name, cssClass, callback) {
        if (callback === void 0) { callback = undefined; }
        var element = document.createElement(name);
        cssClass.split(' ').forEach(function (cssCls) { return element.classList.add(cssCls); });
        if (callback) {
            element.addEventListener('click', callback);
        }
        return element;
    };
    ApplicationLock.closeErrorDialog = function (dialog) {
        if (document.body.contains(dialog)) {
            $(dialog).dialog("close");
        }
    };
    ApplicationLock.prototype.createInfoDialog = function (info, button1Name, button1OnClick, button2Name, button2OnClick, withClose) {
        if (button1Name === void 0) { button1Name = undefined; }
        if (button1OnClick === void 0) { button1OnClick = undefined; }
        if (button2Name === void 0) { button2Name = undefined; }
        if (button2OnClick === void 0) { button2OnClick = undefined; }
        if (withClose === void 0) { withClose = true; }
        if (this.reconnectInfo) {
            ApplicationLock_1.closeErrorDialog(this.reconnectInfo);
            this.reconnectInfo = null;
        }
        this.reconnectInfo = this.createErrorDialog([], function (content) {
            ApplicationLock_1.appendDialogElements(content, info, button1Name, button1OnClick, button2Name, button2OnClick);
        }, withClose);
    };
    ApplicationLock.prototype.closeInfoDialog = function () {
        if (this.reconnectInfo) {
            ApplicationLock_1.closeErrorDialog(this.reconnectInfo);
            this.reconnectInfo = null;
        }
    };
    ApplicationLock.appendDialogElements = function (content, info, button1Name, button1OnClick, button2Name, button2OnClick) {
        var wrapper = document.createElement('div');
        var htmlDialog = '<div class="modal-header">' +
            '<h5 class="modal-title">Informacja</h5>' +
            '</div>' +
            '<div class="modal-body">' +
            '<div class="row eq-row">' +
            '<div class="fc wrapper col-md-12 spacer"><div></div>' +
            '</div>' +
            '<div class="fc wrapper col-md-4 spacer"><div></div>' +
            '</div>' +
            '<div class="fc wrapper col-md-12">' +
            '<div>' +
            '<div class="fc wrapper inline">' +
            '<span class="fc outputLabel">' + info + '</span><p></p>' +
            '</div>' +
            '</div>' +
            '</div>';
        if (button1Name) {
            htmlDialog +=
                '<div class="fc wrapper inline col-md-4 form-group">' +
                    '<button id="Button_282" class="fc button btn btn-primary btn-block" onclick="' + button1OnClick + '">' + button1Name + '</button>' +
                    '</div>';
        }
        if (button2Name) {
            htmlDialog +=
                '<div class="fc wrapper inline col-md-4 form-group">' +
                    '<button id="Button_283" class="fc button btn btn-primary btn-block" onclick="' + button2OnClick + '">' + button2Name + '</button>' +
                    '</div>';
        }
        htmlDialog +=
            '</div>';
        wrapper.innerHTML = htmlDialog;
        content.appendChild(wrapper);
    };
    var ApplicationLock_1;
    __decorate([
        lazyInject('I18n'),
        __metadata("design:type", I18n_1.I18n)
    ], ApplicationLock.prototype, "i18n", void 0);
    ApplicationLock = ApplicationLock_1 = __decorate([
        inversify_1.injectable(),
        __metadata("design:paramtypes", [])
    ], ApplicationLock);
    return ApplicationLock;
}());
exports.ApplicationLock = ApplicationLock;
//# sourceMappingURL=ApplicationLock.js.map