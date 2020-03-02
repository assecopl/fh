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
var marked = require("marked");
var highlightjs = require("highlightjs");
var MdFileViewer = /** @class */ (function (_super) {
    __extends(MdFileViewer, _super);
    function MdFileViewer(componentObj, parent) {
        var _this = _super.call(this, componentObj, parent) || this;
        _this.resourceBasePath = null;
        _this.marked = null;
        _this.mapId = '';
        _this.source = _this.componentObj.src;
        _this.resourceBasePath = _this.componentObj.resourceBasePath;
        _this.mapElement = null;
        return _this;
    }
    MdFileViewer.prototype.create = function () {
        var viewer = document.createElement('div');
        viewer.id = this.id;
        viewer.classList.add("fc");
        viewer.classList.add("md-file-viewer");
        this.loadMdFile(this.source);
        this.component = viewer;
        this.hintElement = this.component;
        // if (this.width) {
        this.wrap(false);
        // } else {
        //     this.htmlElement = this.component;
        //     this.contentWrapper = this.htmlElement;
        // }
        this.addStyles();
        this.display();
    };
    ;
    /**
     * Function that loads rexternal md files an parse it to HTML.
     * @param relativeUrl (relative url of md file)
     * @param resourceBasePath (path in resources url of md file)
     */
    MdFileViewer.prototype.loadMdFile = function (relativeUrl, resourceBasePath) {
        if (resourceBasePath === void 0) { resourceBasePath = null; }
        if (resourceBasePath && !relativeUrl.includes(resourceBasePath)) {
            relativeUrl = resourceBasePath + relativeUrl;
        }
        $.get({
            crossDomain: true,
            xhrFields: {
                withCredentials: true
            },
            url: this.util.getPath(relativeUrl),
            success: function (data) {
                this.component.innerHTML = marked(data, { renderer: this.markedStyleHandler() });
                this.addHrefHandler();
            }.bind(this)
        }).catch();
    };
    /**
     * Handle logic of inside links.
     * If href attribute has .md extensions it will be open inside this container.
     */
    MdFileViewer.prototype.addHrefHandler = function () {
        var collection = this.component.getElementsByTagName("a");
        $.each(collection, function (key, elem) {
            var href = elem.getAttribute("href");
            if (href.includes(".md")) {
                elem.addEventListener('click', function (event) {
                    event.stopPropagation();
                    event.preventDefault();
                    if (this.util.isUrlRelative(href)) {
                        this.loadMdFile(href, this.resourceBasePath);
                    }
                    else {
                        this.loadMdFile(href);
                    }
                }.bind(this));
            }
            else {
                elem.setAttribute('target', '_blank');
            }
        }.bind(this));
    };
    MdFileViewer.prototype.display = function () {
        _super.prototype.display.call(this);
        if (this.mapElement) {
            this.htmlElement.appendChild(this.mapElement);
        }
    };
    ;
    MdFileViewer.prototype.update = function (change) {
        _super.prototype.update.call(this, change);
        if (change.changedAttributes) {
            $.each(change.changedAttributes, function (name, newValue) {
                switch (name) {
                    case 'src':
                        this.src = newValue;
                        this.component.src = this.src;
                        break;
                }
            }.bind(this));
        }
    };
    ;
    MdFileViewer.prototype.wrap = function (skipLabel) {
        _super.prototype.wrap.call(this, skipLabel);
    };
    ;
    /**
     * Create renderer that extends default behaviour of  "marked" renderer.
     * Ads Bootstrap 4 and Highlight.js
     *
     */
    MdFileViewer.prototype.markedStyleHandler = function () {
        var renderer = new marked.Renderer();
        renderer.heading = function (text, level) {
            return '<h' + level + ' class="md-inpage-anchor">'
                + text
                + '</h' + level + '>';
        };
        renderer.code = function (code, language) {
            var valid = !!(language && highlightjs.getLanguage(language));
            var highlighted = valid ? highlightjs.highlight(language, code).value : code;
            return '<pre><code class="hljs lang-' + language + '">'
                + highlighted
                + '</code></pre>';
        };
        renderer.table = function (header, body) {
            return '<table class="table table-bordered table-striped" >'
                + '<thead class="thead-default">'
                + header
                + '</thead>'
                + '<tbody>'
                + body
                + '</tbody>'
                + '</table>';
        };
        renderer.blockquote = function (quote) {
            return '<blockquote class="blockquote">' + quote + '</blockquote>';
        };
        return renderer;
    };
    return MdFileViewer;
}(fh_forms_handler_1.HTMLFormComponent));
exports.MdFileViewer = MdFileViewer;
//# sourceMappingURL=MdFileViewer.js.map