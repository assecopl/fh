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
var fh_forms_handler_2 = require("fh-forms-handler");
var SplitContainer = /** @class */ (function (_super) {
    __extends(SplitContainer, _super);
    function SplitContainer(componentObj, parent) {
        var _this = _super.call(this, componentObj, parent) || this;
        _this.sizeleft = _this.componentObj.sizeLeft || null;
        _this.sizeright = _this.componentObj.sizeRight || null;
        _this.fixed = null;
        _this.resizing = false;
        _this.startX = 0;
        _this.startBasis = 0;
        _this.bothShown = true;
        return _this;
    }
    SplitContainer.prototype.create = function () {
        var container = document.createElement('div');
        container.id = this.id;
        container.classList.add('fc');
        container.classList.add('splitContainer');
        this.component = container;
        this.wrap(true);
        this.addStyles();
        this.createPanelsAndDivider();
    };
    ;
    SplitContainer.prototype.createPanelsAndDivider = function () {
        if (this.componentObj.subelements && this.componentObj.subelements.length > 0) {
            var divider = document.createElement('div');
            divider.classList.add('divider');
            var hideLeft_1 = document.createElement('div');
            ['hideLeft', 'fa', 'fa-caret-left'].forEach(function (cssClass) {
                hideLeft_1.classList.add(cssClass);
            });
            hideLeft_1.addEventListener('click', this.hideLeft.bind(this));
            divider.appendChild(hideLeft_1);
            var showBoth_1 = document.createElement('div');
            ['showBoth', 'fa', 'fa-pause'].forEach(function (cssClass) {
                showBoth_1.classList.add(cssClass);
            });
            showBoth_1.addEventListener('click', this.showBoth.bind(this));
            divider.appendChild(showBoth_1);
            var hideRight_1 = document.createElement('div');
            ['hideRight', 'fa', 'fa-caret-right'].forEach(function (cssClass) {
                hideRight_1.classList.add(cssClass);
            });
            hideRight_1.addEventListener('click', this.hideRight.bind(this));
            divider.appendChild(hideRight_1);
            this.divider = divider;
            divider.addEventListener('mousedown', function (event) {
                if (this.bothShown) {
                    this.resizing = true;
                    this.startX = event.clientX;
                    if (this.fixed.classList.contains('fluid')) {
                        $(this.fixed).css('flex-basis', $(this.fixed).width()).removeClass('fluid');
                    }
                    this.startBasis = parseInt(this.fixed.style.flexBasis);
                    if (this.fixed.style.flexBasis.endsWith('%')) {
                        this.startBasis = this.startBasis / 100.0 * this.component.offsetWidth;
                    }
                    this.divider.style.opacity = 1;
                }
            }.bind(this));
            window.addEventListener('mousemove', this.windowListenerMouseMove = function (event) {
                if (this.resizing) {
                    var xMove = event.clientX - this.startX;
                    var basis = this.startBasis;
                    var newBasis = basis;
                    if (this.fixed == this.contentWrapper.firstChild) {
                        newBasis = basis + xMove;
                    }
                    else {
                        newBasis = basis - xMove;
                    }
                    if (this.contentWrapper.clientWidth - 100 - this.divider.clientWidth > newBasis
                        && newBasis > 100) {
                        this.fixed.style.flexBasis = newBasis + 'px';
                    }
                }
            }.bind(this));
            window.addEventListener('mouseup', this.windowListenerMouseUp = function () {
                if (this.resizing) {
                    this.resizing = false;
                    this.divider.style.removeProperty('opacity');
                    $(window).trigger('resize.splitContainer');
                }
            }.bind(this));
            this.addSideComponent(this.componentObj.subelements[0], 'left');
            this.contentWrapper.appendChild(divider);
            this.addSideComponent(this.componentObj.subelements[1], 'right');
            if (!this.fixed) {
                this.fixed = this.contentWrapper.firstChild;
            }
            this.resizeListener = new fh_forms_handler_2.WindowEventsListener('resize resize.mainContainer resize.floatingGroup', function () {
                var rightDivider = this.fixed.offsetWidth + this.divider.offsetWidth;
                var maxRightPosition = this.component.offsetWidth;
                if (rightDivider > maxRightPosition) {
                    this.fixed.style.flexBasis = (maxRightPosition - this.divider.offsetWidth - 100) + 'px';
                    this.hideRight();
                }
            }.bind(this));
        }
    };
    ;
    SplitContainer.prototype.addSideComponent = function (componentObj, side) {
        componentObj.width = 'md-12';
        var component = this.fh.createComponent(componentObj, this);
        this.components.push(component);
        component.create();
        var panel = document.createElement('div');
        panel.classList.add('splitPanel');
        var sideSize = this['size' + side];
        if (sideSize && sideSize != 'auto') {
            panel.style.flexBasis = sideSize;
            this.fixed = panel;
        }
        else {
            panel.classList.add('fluid');
        }
        var row = document.createElement('div');
        // row.classList.add('row');
        // row.classList.add('eq-row');
        row.appendChild(component.htmlElement);
        panel.appendChild(row);
        this.contentWrapper.appendChild(panel);
        if (typeof component.container !== "undefined") {
            component.container = row;
        }
    };
    ;
    SplitContainer.prototype.hideLeft = function () {
        this.bothShown = false;
        this.component.classList.add('hiddenLeft');
        this.component.classList.remove('hiddenRight');
        $(window).trigger('resize.splitContainer');
    };
    ;
    SplitContainer.prototype.hideRight = function () {
        this.bothShown = false;
        this.component.classList.remove('hiddenLeft');
        this.component.classList.add('hiddenRight');
        $(window).trigger('resize.splitContainer');
    };
    ;
    SplitContainer.prototype.showBoth = function () {
        this.bothShown = true;
        this.component.classList.remove('hiddenLeft');
        this.component.classList.remove('hiddenRight');
        $(window).trigger('resize.splitContainer');
    };
    ;
    SplitContainer.prototype.destroy = function (removeFromParent) {
        if (this.resizeListener != null) {
            this.resizeListener.unregister();
        }
        if (this.windowListenerMouseUp != null) {
            window.removeEventListener('mouseup', this.windowListenerMouseUp);
        }
        if (this.windowListenerMouseMove != null) {
            window.removeEventListener('mousemove', this.windowListenerMouseMove);
        }
        _super.prototype.destroy.call(this, removeFromParent);
    };
    return SplitContainer;
}(fh_forms_handler_1.HTMLFormComponent));
exports.SplitContainer = SplitContainer;
//# sourceMappingURL=SplitContainer.js.map