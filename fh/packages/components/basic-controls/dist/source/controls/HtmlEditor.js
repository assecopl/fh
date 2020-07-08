"use strict";
var __extends = (this && this.__extends) || (function () {
    var extendStatics = function (d, b) {
        extendStatics = Object.setPrototypeOf ||
            ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
            function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
        return extendStatics(d, b);
    };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
var fh_forms_handler_1 = require("fh-forms-handler");
require("summernote/dist/summernote-bs4");
require("summernote/lang/summernote-pl-PL");
var HtmlEditor = /** @class */ (function (_super) {
    __extends(HtmlEditor, _super);
    function HtmlEditor(componentObj, parent) {
        var _this = _super.call(this, componentObj, parent) || this;
        _this.onChange = _this.componentObj.onChange;
        _this.code = _this.componentObj.text;
        return _this;
    }
    HtmlEditor.prototype.create = function () {
        var textAreaElement = document.createElement('div');
        textAreaElement.id = this.id + "_editor";
        this.component = textAreaElement;
        this.htmlElement = this.component;
        this.contentWrapper = this.htmlElement;
        this.wrap(false, false);
        this.display();
        // noinspection JSIgnoredPromiseFromCall
        this.i18n.subscribe(this);
        this.displayLanguage = this.getDisplayLanguageCode(this.i18n.selectedLanguage);
        this.initEditor();
    };
    ;
    HtmlEditor.prototype.initEditor = function () {
        // @ts-ignore
        this.editor = $('#' + this.id + '_editor').summernote({
            lang: this.displayLanguage,
            height: 250,
            toolbar: [
                ['style', ['style']],
                ['font', ['bold', 'italic', 'underline', 'clear']],
                ['fontname', ['fontname']],
                ['color', ['color']],
                ['para', ['ul', 'ol', 'paragraph']],
                ['height', ['height']],
                ['table', ['table']],
                ['insert', ['media', 'link', 'hr']]
            ],
            disableDragAndDrop: true,
            callbacks: {
                onChange: function (contents, $editable) {
                    this.onChangeEvent.call(this, contents, $editable);
                }.bind(this)
            }
        });
        // @ts-ignore
        this.editor.summernote('code', this.code);
    };
    HtmlEditor.prototype.update = function (change) {
        _super.prototype.update.call(this, change);
        if (change.changedAttributes) {
            $.each(change.changedAttributes, function (name, newValue) {
                if (name === 'text') {
                    this.editor.summernote('code', newValue);
                }
            }.bind(this));
        }
    };
    HtmlEditor.prototype.onChangeEvent = function (content) {
        this.changesQueue.queueAttributeChange('text', content);
        this.fireEventWithLock('onChange', this.onChange);
    };
    HtmlEditor.prototype.getDisplayLanguageCode = function (code) {
        var result = '';
        switch (code) {
            case "en":
                result = 'en-US';
                break;
            case "pl":
                result = 'pl-PL';
                break;
        }
        return result;
    };
    HtmlEditor.prototype.languageChanged = function (code) {
        this.displayLanguage = this.getDisplayLanguageCode(code);
        // @ts-ignore
        this.code = this.editor.summernote('code');
        // @ts-ignore
        $('#' + this.id + "_editor").summernote('destroy');
        this.initEditor();
    };
    HtmlEditor.prototype.destroy = function (removeFromParent) {
        // noinspection JSIgnoredPromiseFromCall
        this.i18n.unsubscribe(this);
        // @ts-ignore
        $(this.id + "_editor").summernote('destroy');
        _super.prototype.destroy.call(this, removeFromParent);
    };
    ;
    return HtmlEditor;
}(fh_forms_handler_1.HTMLFormComponent));
exports.HtmlEditor = HtmlEditor;
//# sourceMappingURL=HtmlEditor.js.map