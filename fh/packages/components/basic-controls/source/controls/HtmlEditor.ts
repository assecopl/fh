import {HTMLFormComponent} from "fh-forms-handler";
import 'summernote/dist/summernote-bs4';
import 'summernote/lang/summernote-pl-PL';
import {LanguageChangeObserver} from "fh-forms-handler";

class HtmlEditor extends HTMLFormComponent implements LanguageChangeObserver {
    protected onChange: string;
    protected editor: string;
    protected code: string;
    protected displayLanguage: string;

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);

        this.onChange = this.componentObj.onChange;
        this.code = this.componentObj.text;

    }

    create() {
        let textAreaElement = document.createElement('div');
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

    initEditor() {
        // @ts-ignore
        this.editor = $('#' + this.id + '_editor').summernote({
            lang: this.displayLanguage,
            height: 250,
            toolbar:[
                ['style',['style']],
                ['font',['bold','italic','underline','clear']],
                ['fontname',['fontname']],
                ['color',['color']],
                ['para',['ul','ol','paragraph']],
                ['height',['height']],
                ['table',['table']],
                ['insert',['media','link','hr']]
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
    }

    update(change) {
        super.update(change);

        if (change.changedAttributes) {
            $.each(change.changedAttributes, function (name, newValue) {
                if (name === 'text') {
                    this.editor.summernote('code', newValue);
                }
            }.bind(this));
        }
    }

    onChangeEvent(content) {
        this.changesQueue.queueAttributeChange('text', content);
        this.fireEventWithLock('onChange', this.onChange);
    }

    getDisplayLanguageCode(code: string) {
        let result = '';
        switch (code) {
            case "en":
                result = 'en-US';
                break;
            case "pl":
                result = 'pl-PL';
                break;
        }
        return result;
    }

    languageChanged(code: string) {
        this.displayLanguage = this.getDisplayLanguageCode(code);

        // @ts-ignore
        this.code = this.editor.summernote('code');

        // @ts-ignore
        $('#' + this.id + "_editor").summernote('destroy');

        this.initEditor();
    }

    destroy(removeFromParent) {
        // noinspection JSIgnoredPromiseFromCall
        this.i18n.unsubscribe(this);

        // @ts-ignore
        $(this.id + "_editor").summernote('destroy');

        super.destroy(removeFromParent);
    };
}

export {HtmlEditor};
