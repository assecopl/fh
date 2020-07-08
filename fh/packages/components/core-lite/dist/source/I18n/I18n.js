"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
Object.defineProperty(exports, "__esModule", { value: true });
var inversify_1 = require("inversify");
var I18n = /** @class */ (function () {
    function I18n() {
        this.supportedLanguages = [];
        this.strings = {};
        this.defaultStrings = {};
        this.selectedLanguage = null;
        /**
         * Components observing language change event
         * @type {LanguageChangeObserver[]}
         */
        this.observers = [];
    }
    I18n.prototype.subscribe = function (component) {
        this.observers.push(component);
    };
    I18n.prototype.unsubscribe = function (component) {
        this.observers = this.observers.filter(function (item) {
            if (item !== component) {
                return item;
            }
        });
    };
    I18n.prototype.isLanguageSupported = function (code) {
        return this.supportedLanguages.indexOf(code) !== -1;
    };
    I18n.prototype.registerStrings = function (code, strings, isCustom) {
        if (isCustom === void 0) { isCustom = false; }
        if (!this.isLanguageSupported(code)) {
            this.supportedLanguages.push(code);
            if (this.supportedLanguages.length === 1) {
                // this.defaultLanguage = code;
                this.selectedLanguage = code;
            }
        }
        if (isCustom) {
            this.strings[code] = Object.assign(this.strings[code] || {}, strings);
        }
        else {
            this.defaultStrings[code] = Object.assign(this.defaultStrings[code] || {}, strings);
        }
    };
    I18n.prototype.overrideStrings = function (code, strings) {
        this.registerStrings(code, strings, true);
    };
    I18n.prototype.selectLanguage = function (code) {
        if (!this.isLanguageSupported(code)) {
            console.warn('Selected language (' + code + ') is not supported');
        }
        else {
            this.selectedLanguage = code;
            this.observers.forEach(function (item) {
                item.languageChanged(code);
            });
        }
    };
    I18n.prototype.translateString = function (string, args, code) {
        var template;
        if (code) {
            template = ((this.strings[code] ? this.strings[code][string] : undefined)
                || (this.defaultStrings[code] ? this.defaultStrings[code][string] : undefined)
                || string);
        }
        else {
            template = ((this.strings[this.selectedLanguage] ? this.strings[this.selectedLanguage][string] : undefined)
                || (this.defaultStrings[this.selectedLanguage] ? this.defaultStrings[this.selectedLanguage][string] : undefined)
                || string);
        }
        if (template != null && args != null) {
            for (var i = 0; i < args.length; i++) {
                template = template.replace(new RegExp('\\{' + i + '\\}', 'g'), args[i]);
            }
        }
        return template;
    };
    I18n.prototype.__ = function (string, args, code) {
        if (args === void 0) { args = null; }
        if (code === void 0) { code = null; }
        return this.translateString(string, args, code);
    };
    I18n = __decorate([
        inversify_1.injectable()
    ], I18n);
    return I18n;
}());
exports.I18n = I18n;
//# sourceMappingURL=I18n.js.map