"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
Object.defineProperty(exports, "__esModule", { value: true });
var inversify_1 = require("inversify");
var Util = /** @class */ (function () {
    function Util() {
    }
    Util.prototype.countProperties = function (object) {
        var count = 0;
        if (typeof object != 'undefined') {
            for (var prop in object) {
                if (object.hasOwnProperty(prop)) {
                    count++;
                }
            }
        }
        return count;
    };
    Util.prototype.getPath = function (resource) {
        if (resource.startsWith('http://') || resource.startsWith('https://') || resource.startsWith('data:')) {
            return resource;
        }
        if (typeof fhBaseUrl === 'undefined' && typeof contextRoot === 'undefined') {
            return resource;
        }
        var resolvedUrl = '';
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
    };
    Util.prototype.areArraysEqualsSkipOrder = function (arrayA, arrayB) {
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
    };
    Util.prototype.showDialog = function (title, message, closeButtonLabel, closeButtonClass, closeCallback) {
        var modalDialogId = 'messageDialog-' + new Date().getTime();
        var modalDialog = $.parseHTML("<div class=\"modal fade\" id=\"" + modalDialogId + "\" role=\"dialog\">\n                <div class=\"modal-dialog\">\n                    <div class=\"modal-content\">\n                        <div class=\"modal-header\">\n                            <h5 class=\"modal-title\"></h5>\n                        </div>\n                        <div class=\"modal-body\">\n                            <div class=\"modal-body-message\"></div>\n                            <br>\n                            <button style=\"width: 40%; margin-left: 30%; margin-right: 30%;\"\n                                    class=\"modal-body-button btn\" type=\"button\" data-dismiss=\"modal\"></button>\n                        </div>\n                    </div>\n                </div>\n            </div>");
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
        $('#' + modalDialogId).modal({ backdrop: 'static', keyboard: false });
    };
    /**
     *
     * @param url
     */
    Util.prototype.isUrlRelative = function (url) {
        var r = new RegExp('^(?:[a-z]+:)?//', 'i');
        return !r.test(url);
    };
    Util.prototype.scrollToComponent = function (formElementId, animateDuration) {
        if (animateDuration === void 0) { animateDuration = null; }
        var component = $("#" + formElementId);
        /**
         * Check if we are inside scrolled container.
         */
        var parent = component.closest('.hasHeight');
        var offset = 0;
        var tempId = false;
        if (parent.length > 0) {
            /**
             * Set temporary id for calculation
             */
            if (parent[0].id == "") {
                parent[0].id = btoa("scrollToComponentTempId");
                tempId = true;
            }
            /**
             * Scroll inside container
             */
            offset = this.getOffsetTop(component[0], parent[0].id);
            if (tempId) {
                parent[0].id = "";
                tempId = false;
            }
        }
        else {
            /**
             * Scroll whole page
             */
            offset = component.offset().top;
            parent = $('html, body');
        }
        if (offset >= 0) {
            if (offset > 25) {
                offset -= 25;
            }
            else {
                offset = 0;
            }
            if (animateDuration) {
                parent.animate({
                    scrollTop: offset
                }, animateDuration);
            }
            else {
                parent.scrollTop(offset);
            }
        }
    };
    /**
     * Count offsetTop if there are more containers with relative position inside parent container.
     * @param element
     * @param parentId
     */
    Util.prototype.getOffsetTop = function (element, parentId) {
        var offsetTop = 0;
        while (element && element.id != parentId) {
            offsetTop += element.offsetTop;
            // console.log("getOfsetTop", element.id, element, offsetTop);
            element = element.offsetParent;
        }
        return offsetTop;
    };
    Util = __decorate([
        inversify_1.injectable()
    ], Util);
    return Util;
}());
exports.Util = Util;
//# sourceMappingURL=Util.js.map