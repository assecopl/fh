import {injectable} from "inversify";

declare var contextRoot: string;
declare var fhBaseUrl: string;
declare const $ :any;

@injectable()
class Util {

    public countProperties(object) {
        var count = 0;
        if (typeof object != 'undefined') {
            for (var prop in object) {
                if (object.hasOwnProperty(prop)) {
                    count++;
                }
            }
        }
        return count;
    }

    public getPath(resource) {
        if (resource.startsWith('http://') || resource.startsWith('https://')) {
            return resource;
        }

        if (typeof fhBaseUrl === 'undefined' && typeof contextRoot === 'undefined') {
            return resource;
        }
        let resolvedUrl = '';
        if (typeof fhBaseUrl !== 'undefined') {
            resolvedUrl = fhBaseUrl;
            if (resolvedUrl.indexOf('/', resolvedUrl.length - 1) !== -1) {
                resolvedUrl = resolvedUrl.substr(0, resolvedUrl.length - 1);
            }
        }
        if (resource.startsWith('/')) {
            return resolvedUrl + resource;
        }
        if (contextRoot !== 'undefined') {
            if (!contextRoot.startsWith('/')) {
                contextRoot = '/' + contextRoot;
            }
            if (contextRoot.indexOf('/', contextRoot.length - 1) !== -1) {
                contextRoot = contextRoot.substr(0, contextRoot.length - 1);
            }
            resolvedUrl = resolvedUrl + contextRoot;
        }
        if (!resource.startsWith('/')) {
            resource = '/' + resource;
        }

        return resolvedUrl + resource;
    }

    public areArraysEqualsSkipOrder(arrayA, arrayB) {
        if (!arrayA || !arrayB) {
            return false;
        }

        if (arrayA.length != arrayB.length) {
            return false;
        }

        for (var i = 0; i < arrayA.length; i++) {
            if (arrayB.indexOf(arrayA[i]) == -1) {
                return false;
            }
        }

        return true;
    }

    public showDialog(title, message, closeButtonLabel, closeButtonClass, closeCallback) {
        var modalDialogId = 'messageDialog-' + new Date().getTime();
        var modalDialog = $.parseHTML(
            `<div class="modal fade" id="${modalDialogId}" role="dialog">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title"></h5>
                        </div>
                        <div class="modal-body">
                            <div class="modal-body-message"></div>
                            <br>
                            <button style="width: 40%; margin-left: 30%; margin-right: 30%;"
                                    class="modal-body-button btn" type="button" data-dismiss="modal"></button>
                        </div>
                    </div>
                </div>
            </div>`
        );
        $('body').append(modalDialog);
        $(modalDialog).find('.modal-title').html(title);
        $(modalDialog).find('.modal-body-message').html(message);
        $(modalDialog).find('.modal-body-button').text(closeButtonLabel);
        if (closeButtonClass != null) {
            $(modalDialog).find('.modal-body-button').addClass(closeButtonClass);
        }

        $(modalDialog).on('hidden.bs.modal', function () {
            $(modalDialog).remove();
            if (closeCallback != null) {
                closeCallback();
            }
        });

        $('#' + modalDialogId).modal({backdrop: 'static', keyboard: false});
    }

    /**
     *
     * @param url
     */
    isUrlRelative(url:string):boolean {
        const r = new RegExp('^(?:[a-z]+:)?//', 'i');
        return !r.test(url);
    }

}

export {Util};