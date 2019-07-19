import {FileUploadPL} from './i18n/FileUpload.pl';
import {FileUploadEN} from './i18n/FileUpload.en';
import {HTMLFormComponent} from 'fh-forms-handler';
import {NotificationEvent} from "fh-forms-handler";
import {FhContainer} from "fh-forms-handler";

class FileUpload extends HTMLFormComponent {
    private onUpload: string;
    private fileIds: String[];
    private readonly extensions: String;
    private readonly fileNames: String[];
    private readonly label: string;
    private readonly inputHeight: number;
    private input: any;
    private readonly multiple: boolean;
    private labelHidden: boolean;
    private progressBar: HTMLElement;
    private labelSpanElement: HTMLElement;
    private readonly pendingUploadHandle: any;
    private inputFileButton: HTMLAnchorElement;

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);

        this.i18n.registerStrings('pl', FileUploadPL);
        this.i18n.registerStrings('en', FileUploadEN);

        this.onUpload = this.componentObj.onUpload;
        this.fileIds = null;
        this.extensions = this.componentObj.extensions || '';
        this.fileNames = this.componentObj.fileNames || [];
        this.label = this.componentObj.label;
        this.inputHeight = this.componentObj.height || null;
        this.input = null;
        this.labelHidden = this.componentObj.labelHidden;
        this.progressBar = null;
        this.labelSpanElement = null;
        this.pendingUploadHandle = null;
        this.multiple = this.componentObj.multiple;
    }

    create() {
        let fileUpload = document.createElement('div');
        fileUpload.id = this.id;
        ['fc', 'fileUpload'].forEach(function (cssClass) {
            fileUpload.classList.add(cssClass);
        }.bind(this));

        this.inputFileButton = document.createElement('a');
        ['fc', 'button', 'btn', 'btn-primary', 'btn-block'].forEach(function (cssClass) {
            this.inputFileButton.classList.add(cssClass);
        }.bind(this));
        if (this.inputHeight) {
            this.inputFileButton.style.height = this.inputHeight + 'px';
        }
        //btn-danger

        let inputFile = document.createElement('input');
        inputFile.setAttribute('id', this.componentObj.id + '_file');
        inputFile.type = 'file';
        if (this.multiple) {
            inputFile.multiple = true;
        }
        inputFile.name = 'file';
        this.inputFileButton.appendChild(inputFile);

        if (this.extensions) {
            let accept = '';
            if (this.extensions != null) {
                let extensionsArray = this.extensions.split(',');
                for (let i = 0, len = extensionsArray.length; i < len; i++) {
                    if (accept != '') {
                        accept += ',';
                    }
                    if (!(<any>extensionsArray[i]).startsWith('.')) {
                        accept += '.';
                    }
                    accept += extensionsArray[i];
                }
            }
            inputFile.accept = accept;
        }

        this.setEffectiveLabelAndColor();

        let inputFileProgress = document.createElement('div');
        ['progress', 'progress-striped', 'active', 'd-none'].forEach(function (cssClass) {
            inputFileProgress.classList.add(cssClass);
        }.bind(this));

        let label = document.createElement('label');
        label.setAttribute('for', this.componentObj.id + '_file');
        this.inputFileButton.appendChild(label);

        let inputFileProgressBar = document.createElement('div');
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

        let fileNameLabel = document.createElement('label');
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
                let files: FileList = event.target.files;
                let formData = new FormData();
                $.each(files, function (i, file) {
                    formData.append('file', file);
                });
                formData.append('componentId', this.id);
                formData.append('formId', this.formId);
                formData.append('containerId', this.formsManager.findForm(this.formId).parentId);

                let error = false;


                for (let i = 0; i < event.target.files.length; i++) {
                    let file = event.target.files[i];

                    let fileNameSplit = file.name.split('.');

                    if (this.extensions && fileNameSplit.length == 1) {
                        FhContainer.get<NotificationEvent>('Events.NotificationEvent').fire({
                            level: 'error',
                            message: this.__('no file extension', [this.extensions])
                        });
                        error = true;
                    }

                    if (this.extensions && fileNameSplit.length > 1) {
                        let allowedExtensions = this.extensions.replace(new RegExp('\\.', 'g'),
                            '').split(',');
                        let sentFileExtension = fileNameSplit[fileNameSplit.length - 1];
                        if (allowedExtensions.indexOf(sentFileExtension) === -1) {
                            FhContainer.get<NotificationEvent>('Events.NotificationEvent').fire({
                                level: 'error',
                                message: this.__('incorrect file extension', [this.extensions, sentFileExtension])
                            });
                            error = true;
                        }
                    }

                    if (file.size > this.componentObj.maxSize) {
                        FhContainer.get<NotificationEvent>('Events.NotificationEvent').fire({
                            level: 'error',
                            message: this.__('max file exceeded', [FileUpload.toDisplaySize(this.componentObj.maxSize)])
                        });
                        error = true;
                    }

                }

                if (error) {
                    this.input.value = null;
                    return;
                }

                this.pendingUploadHandle = this.fireHttpMultiPartEvent('onUpload', this.onUpload, this.util.getPath(this.processURL('fileUpload')), formData);
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
                        let level = 'error';
                        let msg;
                        if (status === 409) {
                            msg = this.__('max file exceeded');
                        } else if (status === 400) {
                            msg = this.__('incorrect file extension');
                        } else if (status === -1) {
                            level = 'warning';
                            msg = this.__('upload aborted');
                        } else {
                            msg = this.__('upload error');
                        }


                        FhContainer.get<NotificationEvent>('Events.NotificationEvent').fire({
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

    update(change) {
        super.update(change);

        $.each(change.changedAttributes || [], function (name, newValue) {
            switch (name) {
                case 'label':
                    this.label = newValue;
                    this.setEffectiveLabelAndColor();
                    break;
                case 'fileNames':
                    if (!this.labelHidden) {
                        let fileNameLabel = this.component.querySelector('label.fileNames');
                        this.fileNames = newValue.join("<br />");
                        fileNameLabel.innerHTML = this.fileNames;
                    }
                    break;
                case 'extensions':
                    this.extensions = newValue;
                    break;
            }
            $(this.component).find('.progress-bar').hide(0).width(0);
            $(this.component).find('.progress-bar').parent().get(0).classList.add('d-none');
        }.bind(this));
    };

    extractChangedAttributes() {
        return this.changesQueue.extractChangedAttributes();
    };

    setPresentationStyle(presentationStyle: string) {
        let button = this.component.querySelector('.button');

        button.classList.remove('btn-danger');
        button.classList.remove('btn-primary');

        switch (presentationStyle) {
            case 'BLOCKER':
            case 'ERROR':
                button.classList.add('btn-danger');
                break;
            case 'OK':
                button.classList.add('btn-primary');
                break;
            case 'INFO':
                button.classList.add('btn-info');
                break;
            case 'WARNING':
                button.classList.add('btn-danger');
                break;
            default:
                button.classList.add('btn-primary');
                break;
        }

        this.presentationStyle = presentationStyle;
    };

    setEffectiveLabelAndColor() {
        let newSpanElement;

        if (this.pendingUploadHandle != null) {
            newSpanElement = this.__('abort');
        } else if (this.label != null && this.label !== '') {
            newSpanElement = document.createElement('span');
            newSpanElement.innerHTML = this.fhml.resolveValueTextOrEmpty(this.label);
        } else {
            newSpanElement = this.__('add file');
        }

        if (this.labelSpanElement != null) {
            this.inputFileButton.replaceChild(newSpanElement, this.labelSpanElement);
        } else {
            this.inputFileButton.appendChild(newSpanElement);
        }
        this.labelSpanElement = newSpanElement;

        if (this.pendingUploadHandle != null) {
            this.inputFileButton.classList.remove('btn-primary');
            this.inputFileButton.classList.add('btn-success');
        } else {
            this.inputFileButton.classList.add('btn-primary');
            this.inputFileButton.classList.remove('btn-success');
            if (this.progressBar != null) {
                this.progressBar.setAttribute('style', '');
            }
        }
    };

    static toDisplaySize(size: number) {
        if (size === 0) return '0 B';
        let sizes = ['B', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];
        let i = Math.floor(Math.log(size) / Math.log(1024));
        return parseFloat((size / Math.pow(1024, i)).toFixed(2)) + ' ' + sizes[i];
    };

    setAccessibility(accessibility: string) {
        super.setAccessibility(accessibility);

        this.inputFileButton.classList.remove('disabled');
        switch (accessibility) {
            case 'EDIT':
                this.input.disabled = this.getViewMode() != 'NORMAL';
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

    /**
     * @Override
     */
    public getDefaultWidth() {
        return 'lg-3,md-4,sm-5,xs-6';

    }
}

export {FileUpload};