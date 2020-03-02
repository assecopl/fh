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
var FileUpload_pl_1 = require("./i18n/FileUpload.pl");
var FileUpload_en_1 = require("./i18n/FileUpload.en");
var fh_forms_handler_1 = require("fh-forms-handler");
var fh_forms_handler_2 = require("fh-forms-handler");
var FileUpload = /** @class */ (function (_super) {
    __extends(FileUpload, _super);
    function FileUpload(componentObj, parent) {
        var _this = _super.call(this, componentObj, parent) || this;
        _this.i18n.registerStrings('pl', FileUpload_pl_1.FileUploadPL);
        _this.i18n.registerStrings('en', FileUpload_en_1.FileUploadEN);
        _this.onUpload = _this.componentObj.onUpload;
        _this.fileIds = null;
        _this.extensions = _this.componentObj.extensions || '';
        _this.fileNames = _this.componentObj.fileNames || [];
        _this.label = _this.componentObj.label;
        _this.style = _this.componentObj.style;
        _this.inputHeight = _this.componentObj.height || null;
        _this.input = null;
        _this.labelHidden = _this.componentObj.labelHidden;
        _this.progressBar = null;
        _this.labelSpanElement = null;
        _this.pendingUploadHandle = null;
        _this.multiple = _this.componentObj.multiple;
        _this.presentationStyle = _this.style;
        return _this;
    }
    FileUpload.prototype.create = function () {
        var fileUpload = document.createElement('div');
        fileUpload.id = this.id;
        ['fc', 'fileUpload'].forEach(function (cssClass) {
            fileUpload.classList.add(cssClass);
        }.bind(this));
        this.inputFileButton = document.createElement('a');
        ['fc', 'button', 'btn', 'btn-' + this.style, 'btn-block'].forEach(function (cssClass) {
            this.inputFileButton.classList.add(cssClass);
        }.bind(this));
        if (this.inputHeight) {
            this.inputFileButton.style.height = this.inputHeight + 'px';
        }
        //btn-danger
        var inputFile = document.createElement('input');
        inputFile.setAttribute('id', this.componentObj.id + '_file');
        inputFile.type = 'file';
        if (this.multiple) {
            inputFile.multiple = true;
        }
        inputFile.name = 'file';
        this.inputFileButton.appendChild(inputFile);
        if (this.extensions) {
            var accept = '';
            if (this.extensions != null) {
                var extensionsArray = this.extensions.split(',');
                for (var i = 0, len = extensionsArray.length; i < len; i++) {
                    if (accept != '') {
                        accept += ',';
                    }
                    if (!extensionsArray[i].startsWith('.')) {
                        accept += '.';
                    }
                    accept += extensionsArray[i];
                }
            }
            inputFile.accept = accept;
        }
        this.setEffectiveLabelAndColor();
        var inputFileProgress = document.createElement('div');
        ['progress', 'progress-striped', 'active', 'd-none'].forEach(function (cssClass) {
            inputFileProgress.classList.add(cssClass);
        }.bind(this));
        var label = document.createElement('label');
        label.setAttribute('for', this.componentObj.id + '_file');
        this.inputFileButton.appendChild(label);
        var inputFileProgressBar = document.createElement('div');
        ['progress-bar'].forEach(function (cssClass) {
            inputFileProgressBar.classList.add(cssClass);
        }.bind(this));
        inputFileProgress.appendChild(inputFileProgressBar);
        this.progressBar = inputFileProgressBar;
        $(inputFile).click(function () {
            if (this.pendingUploadHandle != null) {
                this.pendingUploadHandle.abortRequest();
                event.stopPropagation();
                event.preventDefault();
                return false;
            }
        }.bind(this));
        this.inputFileButton.appendChild(inputFileProgress);
        fileUpload.appendChild(this.inputFileButton);
        var fileNameLabel = document.createElement('label');
        if (this.inputHeight) {
            fileNameLabel.style.height = this.inputHeight + 'px';
        }
        fileNameLabel.innerHTML = this.fileNames.join('<br />');
        fileNameLabel.classList.add('fileNames');
        fileUpload.appendChild(fileNameLabel);
        this.input = inputFile;
        this.component = fileUpload;
        this.focusableComponent = this.inputFileButton;
        inputFile.addEventListener('change', function (event) {
            if (!this.formsManager.ensureFunctionalityUnavailableDuringShutdown()) {
                return;
            }
            if (event.target.files.length > 0) {
                var files = event.target.files;
                var formData_1 = new FormData();
                $.each(files, function (i, file) {
                    formData_1.append('file', file);
                });
                formData_1.append('componentId', this.id);
                formData_1.append('formId', this.formId);
                formData_1.append('containerId', this.formsManager.findForm(this.formId).parentId);
                var error = false;
                for (var i = 0; i < event.target.files.length; i++) {
                    var file = event.target.files[i];
                    var fileNameSplit = file.name.split('.');
                    if (this.extensions && fileNameSplit.length == 1) {
                        fh_forms_handler_2.FhContainer.get('Events.NotificationEvent').fire({
                            level: 'error',
                            message: this.__('no file extension', [this.extensions]).innerText
                        });
                        error = true;
                    }
                    if (this.extensions && fileNameSplit.length > 1) {
                        var allowedExtensions = this.extensions.replace(new RegExp('\\.', 'g'), '').split(',');
                        var sentFileExtension = fileNameSplit[fileNameSplit.length - 1];
                        if (allowedExtensions.indexOf(sentFileExtension) === -1) {
                            fh_forms_handler_2.FhContainer.get('Events.NotificationEvent').fire({
                                level: 'error',
                                message: this.__('incorrect file extension', [this.extensions, sentFileExtension]).innerText
                            });
                            error = true;
                        }
                    }
                    if (file.size > this.componentObj.maxSize) {
                        fh_forms_handler_2.FhContainer.get('Events.NotificationEvent').fire({
                            level: 'error',
                            message: this.__('max file exceeded', [FileUpload.toDisplaySize(this.componentObj.maxSize)]).innerText
                        });
                        error = true;
                    }
                }
                if (error) {
                    this.input.value = null;
                    return;
                }
                this.pendingUploadHandle = this.fireHttpMultiPartEvent('onUpload', this.onUpload, this.util.getPath(this.processURL('fileUpload')), formData_1);
                this.setEffectiveLabelAndColor();
                this.pendingUploadHandle.promise
                    .then(function (data) {
                    this.pendingUploadHandle = null;
                    this.fileIds = data.ids;
                    this.changesQueue.queueAttributeChange('fileIds', this.fileIds);
                    if (this.onUpload) {
                        this.fireEventWithLock('onUpload', this.onUpload);
                    }
                    this.setEffectiveLabelAndColor();
                    this.input.value = null; // to allow uploading the same file again
                }.bind(this))
                    .fail(function (status) {
                    this.pendingUploadHandle = null;
                    this.input.value = null;
                    var level = 'error';
                    var msg;
                    if (status === 409) {
                        msg = this.__('max file exceeded').innerText;
                    }
                    else if (status === 400) {
                        msg = this.__('incorrect file extension').innerText;
                    }
                    else if (status === -1) {
                        level = 'warning';
                        msg = this.__('upload aborted').innerText;
                    }
                    else {
                        msg = this.__('upload error').innerText;
                    }
                    fh_forms_handler_2.FhContainer.get('Events.NotificationEvent').fire({
                        level: level,
                        message: msg
                    });
                    this.setEffectiveLabelAndColor();
                }.bind(this));
            }
        }.bind(this));
        $(inputFile).on('dragover', function dragover(e) {
            e.stopPropagation();
            e.preventDefault();
            fileUpload.classList.add('upload-active');
        });
        $(inputFile).on('dragleave', function dragleave(e) {
            e.stopPropagation();
            e.preventDefault();
            fileUpload.classList.remove('upload-active');
        });
        $(inputFile).on('drop', function () {
            fileUpload.classList.remove('upload-active');
        });
        this.hintElement = this.input;
        this.wrap(true);
        this.addStyles();
        this.display();
        this.component.style.height = 'auto';
        if (this.component.classList.contains('listButton')) {
            this.htmlElement.classList.add('listButtonWrapper');
        }
    };
    ;
    FileUpload.prototype.update = function (change) {
        _super.prototype.update.call(this, change);
        $.each(change.changedAttributes || [], function (name, newValue) {
            switch (name) {
                case 'label':
                    this.label = newValue;
                    this.setEffectiveLabelAndColor();
                    break;
                case 'fileNames':
                    if (!this.labelHidden) {
                        var fileNameLabel = this.component.querySelector('label.fileNames');
                        this.fileNames = newValue.join("<br />");
                        fileNameLabel.innerHTML = this.fileNames;
                    }
                    break;
                case 'extensions':
                    this.extensions = newValue;
                    break;
                case 'style':
                    this.component.classList.remove('btn-' + this.style);
                    this.component.classList.add('btn-' + newValue);
                    this.style = newValue;
                    break;
            }
            $(this.component).find('.progress-bar').hide(0).width(0);
            $(this.component).find('.progress-bar').parent().get(0).classList.add('d-none');
        }.bind(this));
    };
    ;
    FileUpload.prototype.extractChangedAttributes = function () {
        return this.changesQueue.extractChangedAttributes();
    };
    ;
    FileUpload.prototype.setPresentationStyle = function (presentationStyle) {
        var button = this.component.querySelector('.button');
        button.classList.remove('btn-' + this.style);
        switch (presentationStyle.toUpperCase()) {
            case 'BLOCKER':
            case 'ERROR':
                button.classList.add('btn-danger');
                break;
            case 'OK':
                button.classList.add('btn-primary');
                break;
            case 'SUCCESS':
                button.classList.add('btn-success');
                break;
            case 'INFO':
                button.classList.add('btn-info');
                break;
            case 'WARNING':
                button.classList.add('btn-warning');
                break;
            case 'DANGER':
                button.classList.add('btn-danger');
                break;
            default:
                button.classList.add('btn-primary');
                break;
        }
        this.presentationStyle = presentationStyle;
    };
    ;
    FileUpload.prototype.setEffectiveLabelAndColor = function () {
        var newSpanElement;
        if (this.pendingUploadHandle != null) {
            newSpanElement = this.__('abort');
        }
        else if (this.label != null && this.label !== '') {
            newSpanElement = document.createElement('span');
            newSpanElement.innerHTML = this.fhml.resolveValueTextOrEmpty(this.label);
        }
        else {
            newSpanElement = this.__('add file');
        }
        if (this.labelSpanElement != null) {
            this.inputFileButton.replaceChild(newSpanElement, this.labelSpanElement);
        }
        else {
            this.inputFileButton.appendChild(newSpanElement);
        }
        this.labelSpanElement = newSpanElement;
        if (this.pendingUploadHandle != null) {
            this.inputFileButton.classList.remove('btn-primary');
            this.inputFileButton.classList.add('btn-success');
        }
        else {
            this.inputFileButton.classList.add('btn-primary');
            this.inputFileButton.classList.remove('btn-success');
            if (this.progressBar != null) {
                this.progressBar.setAttribute('style', '');
            }
        }
    };
    ;
    FileUpload.toDisplaySize = function (size) {
        if (size === 0)
            return '0 B';
        var sizes = ['B', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];
        var i = Math.floor(Math.log(size) / Math.log(1024));
        return parseFloat((size / Math.pow(1024, i)).toFixed(2)) + ' ' + sizes[i];
    };
    ;
    FileUpload.prototype.setAccessibility = function (accessibility) {
        _super.prototype.setAccessibility.call(this, accessibility);
        this.inputFileButton.classList.remove('disabled');
        switch (accessibility) {
            case 'EDIT':
                this.input.disabled = false;
                break;
            case 'VIEW':
                this.inputFileButton.classList.add('disabled');
                this.input.disabled = true;
                break;
            case 'DEFECTED':
                this.inputFileButton.classList.add('disabled');
                this.input.disabled = true;
                this.inputFileButton.title = 'Broken control';
                break;
        }
    };
    ;
    /**
     * @Override
     */
    FileUpload.prototype.getDefaultWidth = function () {
        return 'lg-3,md-4,sm-5,xs-6';
    };
    return FileUpload;
}(fh_forms_handler_1.HTMLFormComponent));
exports.FileUpload = FileUpload;
//# sourceMappingURL=FileUpload.js.map