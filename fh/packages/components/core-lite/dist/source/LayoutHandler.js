"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
Object.defineProperty(exports, "__esModule", { value: true });
var inversify_1 = require("inversify");
var LayoutHandler = /** @class */ (function () {
    function LayoutHandler() {
        this.prefix = "fh-layout-";
        this.currentMainLayout = LayoutHandler_1.mainLayout;
        this.targetLayout = LayoutHandler_1.mainLayout;
    }
    LayoutHandler_1 = LayoutHandler;
    /**
     * Functions that finds specific container in target layout
     * Created for bakward compatibility with JS function getElementById.
     * @param containerId
     * @param jqueryObject
     * @return HTML DOM Object or null*
     */
    LayoutHandler.prototype.getLayoutContainer = function (containerId, jqueryObject) {
        if (jqueryObject === void 0) { jqueryObject = false; }
        var container = $("#" + this.targetLayout + " #" + containerId);
        if (container.length == 0) {
            var containerFallBack = $("#" + containerId);
            if (containerFallBack.length == 0) {
                return null;
            }
            else {
                return (jqueryObject ? containerFallBack : containerFallBack[0]);
            }
        }
        else {
            return (jqueryObject ? container : container[0]);
        }
    };
    /**
     * Functions that finds specific container in current layout
     * Created for bakward compatibility with JS function getElementById.
     * @param containerId
     * @param jqueryObject
     * @return HTML DOM Object or null*
     *
     */
    LayoutHandler.prototype.getCurrentLayoutContainer = function (containerId, jqueryObject) {
        if (jqueryObject === void 0) { jqueryObject = false; }
        var container = $("#" + this.currentMainLayout + " #" + containerId);
        if (container.length == 0) {
            var containerFallBack = $("#" + containerId);
            if (containerFallBack.length == 0) {
                return null;
            }
            else {
                return (jqueryObject ? containerFallBack : containerFallBack[0]);
            }
        }
        else {
            return (jqueryObject ? container : container[0]);
        }
    };
    LayoutHandler.prototype.getCurrentMainLayout = function () {
        return this.currentMainLayout;
    };
    /**
     * Function that prepare age for layout processing. If layout will be changed function hides all layouts.
     * @param layout
     */
    LayoutHandler.prototype.startLayoutProcessing = function (layout) {
        if (this.currentMainLayout != layout) {
            $(".fh-layout-div").addClass("d-none");
            this.targetLayout = this.prefix + layout;
        }
        if (ENV_IS_DEVELOPMENT) {
            console.log("startLayoutProcessing", layout, this.currentMainLayout, this.targetLayout);
        }
    };
    /**
     * Fuction that block layout change. Used when another UC shows form on modal element.
     * Used in Form.ts;
     * @param isModal
     */
    LayoutHandler.prototype.blockLayoutChangeForModal = function () {
        this.targetLayout = this.currentMainLayout;
        if (ENV_IS_DEVELOPMENT) {
            console.log("blockLayoutChangeForModal", this.currentMainLayout, this.targetLayout);
        }
    };
    /**
     * Fuction that block layout change. Used when UC is in design mode
     * Used in Form.ts;
     */
    LayoutHandler.prototype.blockLayoutChangeForDesigner = function () {
        this.targetLayout = LayoutHandler_1.mainLayout;
        if (ENV_IS_DEVELOPMENT) {
            console.log("blockLayoutChangeForDesigner", this.currentMainLayout, this.targetLayout);
        }
    };
    /**
     * Function that finish layout processing. Moves exist contetnt from one layout to another.
     * Moving designer components is not implemented.
     */
    LayoutHandler.prototype.finishLayoutProcessing = function () {
        if (this.currentMainLayout != this.targetLayout) {
            if (ENV_IS_DEVELOPMENT) {
                console.log("Ustawiam currentLayout");
            }
            //TODO Maybay we do not have to moves mainForm content on layout change.To discuss.
            var currentMainForm = this.getCurrentLayoutContainer("mainForm", true);
            var currentMenuForm = this.getCurrentLayoutContainer("menuForm", true);
            var currentNavbarForm = this.getCurrentLayoutContainer("navbarForm", true);
            var targetMainForm = this.getLayoutContainer("mainForm", true);
            var targetMenuForm = this.getLayoutContainer("menuForm", true);
            var targetNavbarForm = this.getLayoutContainer("navbarForm", true);
            currentMainForm.contents().appendTo(targetMainForm);
            currentMenuForm.contents().appendTo(targetMenuForm);
            currentNavbarForm.contents().appendTo(targetNavbarForm);
            currentMainForm.html("");
            currentMenuForm.html("");
            currentNavbarForm.html("");
            this.currentMainLayout = this.targetLayout;
        }
        $("#" + this.targetLayout).removeClass("d-none");
        if (ENV_IS_DEVELOPMENT) {
            console.log("finishLayoutProcessing", this.currentMainLayout, this.targetLayout);
        }
    };
    var LayoutHandler_1;
    LayoutHandler.mainLayout = "standard";
    LayoutHandler = LayoutHandler_1 = __decorate([
        inversify_1.injectable()
    ], LayoutHandler);
    return LayoutHandler;
}());
exports.LayoutHandler = LayoutHandler;
//# sourceMappingURL=LayoutHandler.js.map