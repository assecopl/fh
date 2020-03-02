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
var PanelGroup_1 = require("./PanelGroup");
var FloatingGroup = /** @class */ (function (_super) {
    __extends(FloatingGroup, _super);
    function FloatingGroup(componentObj, parent) {
        var _this = _super.call(this, componentObj, parent) || this;
        _this.dragging = false;
        _this.isDraggable = true;
        _this.forceHeader = true;
        _this.onTogglePin = _this.componentObj.onTogglePin;
        _this.floatingState = _this.componentObj.attributes.floatingState;
        _this.pinningMode = _this.componentObj.pinningMode || 'normal';
        _this.onToggleFullScreen = _this.componentObj.onToggleFullScreen;
        _this.floatingOnly = !!_this.componentObj.attributes.floatingOnly;
        _this.hideButtons = !!_this.componentObj.hideButtons;
        _this.hideHeader = !!_this.componentObj.hideHeader;
        _this.isDraggable = !!_this.componentObj.isDraggable;
        _this.dragging = false;
        _this.resizing = false;
        _this.dimensions = {
            width: parseInt(_this.componentObj.floatingWidth != null ? _this.componentObj.floatingWidth : _this.componentObj.width) || 350,
            height: parseInt(_this.componentObj.floatingHeight != null ? _this.componentObj.floatingHeight : _this.componentObj.height) || 400
        };
        _this.position = {
            top: parseInt(_this.componentObj.top) || (window.innerHeight - _this.dimensions.height) / 2,
            left: parseInt(_this.componentObj.left) || (window.innerWidth - _this.dimensions.width) / 2
        };
        if (_this.dimensions.width < 0) {
            _this.dimensions.width = window.innerWidth + _this.dimensions.width;
        }
        if (_this.dimensions.height < 0) {
            _this.dimensions.height = window.innerHeight + _this.dimensions.height;
        }
        if (_this.position.left < 0) {
            _this.position.left = window.innerWidth - _this.dimensions.width + _this.position.left;
        }
        if (_this.position.top < 0) {
            _this.position.top = window.innerHeight - _this.dimensions.height + _this.position.top;
        }
        _this.drag = { startX: 0, startY: 0, posX: 0, posY: 0, width: 0, height: 0 };
        _this.bound = {
            toggleMouseDown: _this.toggleMouseDown.bind(_this),
            resizeMouseDown: _this.resizeMouseDown.bind(_this),
            mouseMove: _this.mouseMove.bind(_this),
            mouseUp: _this.mouseUp.bind(_this)
        };
        return _this;
    }
    FloatingGroup.prototype.create = function () {
        _super.prototype.create.call(this);
        if (this.hideHeader) {
            $(this.component).find('.card-header').remove();
        }
        if (!this.hideButtons) {
            var pinToggle = document.createElement('span');
            if (!this.floatingOnly) {
                pinToggle.classList.add('pinToggle');
                var pinIcon = document.createElement('i');
                pinIcon.classList.add('fa');
                pinIcon.classList.add('fa-window-restore');
                pinToggle.appendChild(pinIcon);
                this.groupToolbox.insertBefore(pinToggle, this.groupToolbox.firstChild);
                pinToggle.addEventListener('click', function (event) {
                    event.stopPropagation();
                    if (this.isPinned(this.floatingState)) {
                        if (this.isMinimized(this.floatingState)) {
                            this.floatingState = 'UNPINNED_MINIMIZED';
                        }
                        else if (this.isMaximized(this.floatingState)) {
                            this.floatingState = 'UNPINNED_MINIMIZED';
                        }
                    }
                    else if (this.isUnpinned(this.floatingState)) {
                        if (this.isMinimized(this.floatingState)) {
                            this.floatingState = 'PINNED_MINIMIZED';
                        }
                        else if (this.isMaximized(this.floatingState)) {
                            this.floatingState = 'PINNED_MINIMIZED';
                        }
                    }
                    this.togglePin(false, true);
                    if (this.onTogglePin) {
                        this.fireEvent('onTogglePin', this.onTogglePin);
                    }
                }.bind(this));
            }
            var fullscreenToggle = document.createElement('span');
            fullscreenToggle.classList.add('fullscreenToggle');
            var fullscreenIcon = document.createElement('span');
            fullscreenIcon.classList.add('fas');
            fullscreenIcon.classList.add('fa-expand-arrows-alt');
            fullscreenToggle.appendChild(fullscreenIcon);
            this.groupToolbox.insertBefore(fullscreenToggle, this.groupToolbox.firstChild);
            fullscreenToggle.addEventListener('click', function (event) {
                event.stopPropagation();
                if (this.isMaximized(this.floatingState)) {
                    if (this.isPinned(this.floatingState)) {
                        this.floatingState = 'PINNED_MINIMIZED';
                    }
                    else if (this.isUnpinned(this.floatingState)) {
                        this.floatingState = 'UNPINNED_MINIMIZED';
                    }
                }
                else if (this.isMinimized(this.floatingState)) {
                    if (this.isPinned(this.floatingState)) {
                        this.floatingState = 'PINNED_MAXIMIZED';
                    }
                    else if (this.isUnpinned(this.floatingState)) {
                        this.floatingState = 'UNPINNED_MAXIMIZED';
                    }
                }
                this.toggleFullScreen(false, true);
                if (this.onToggleFullScreen) {
                    this.fireEvent('onToggleFullScreen', this.onToggleFullScreen);
                }
            }.bind(this));
        }
        var resizeHandle = document.createElement('div');
        resizeHandle.classList.add('resizeHandle');
        this.component.appendChild(resizeHandle);
        if (this.floatingOnly) {
            this.unpinGroup(false, true);
        }
        this.togglePin(true, true);
        this.toggleFullScreen(true, true);
        if (this.pinningMode === 'button') {
            this.component.classList.add('pinningModeButton');
        }
        else if (this.pinningMode === 'invisible') {
            this.component.classList.add('pinningModeInvisible');
        }
        if (this.isDraggable) {
            this.component.classList.add("fc-draggable");
        }
        else {
            this.component.classList.remove("fc-draggable");
        }
        this.resizeListener = new fh_forms_handler_1.WindowEventsListener('resize', function () {
            var component = $(this.component);
            if (component.offset().left + component.width() > $(window).width()) {
                var left = Math.max(0, $(window).width() - component.width());
                component.css('left', left);
                this.position.left = left;
            }
            if (component.position().top + component.height() > $(window).height()) {
                var top = Math.max(0, $(window).height() - component.height());
                component.css('top', top);
                this.position.top = top;
            }
        }.bind(this));
    };
    ;
    FloatingGroup.prototype.togglePin = function (skipEvent, skipValueChange) {
        if (skipEvent === void 0) { skipEvent = undefined; }
        if (skipValueChange === void 0) { skipValueChange = undefined; }
        if (this.isUnpinned(this.floatingState)) {
            this.unpinGroup(skipValueChange);
        }
        else if (this.isPinned(this.floatingState)) {
            this.pinGroup(skipValueChange);
        }
        if (!skipEvent) {
            this.changesQueue.queueAttributeChange('floatingState', this.floatingState);
        }
        //Clear events data after change floatingState
        this.dragging = false;
        this.resizing = false;
        this.headerClick = false;
    };
    ;
    FloatingGroup.prototype.pinGroup = function (skipValueChange) {
        this.component.classList.remove('floating');
        this.removeInlineStyles();
        if (this.pinningMode == 'button') {
            this.component.classList.add('btn');
            this.component.classList.add('btn-primary');
            this.component.classList.remove('card');
            this.component.classList.remove('card-default');
        }
        this.component.classList.remove('fullscreen');
        var fullscreenToggleIcon = this.groupToolbox.querySelector('.fullscreenToggle > span');
        if (fullscreenToggleIcon != null) {
            fullscreenToggleIcon.classList.remove('fa-expand');
            fullscreenToggleIcon.classList.add('fa-expand-arrows-alt');
        }
        if (!this.hideHeader) {
            this.component.querySelector('.card-header').removeEventListener('mousedown', this.bound.toggleMouseDown);
        }
        this.component.querySelector('.resizeHandle').removeEventListener('mousedown', this.bound.resizeMouseDown);
        window.removeEventListener('mousemove', this.bound.mouseMove);
        window.removeEventListener('mouseup', this.bound.mouseUp);
        if (!skipValueChange) {
            if (this.isMaximized(this.floatingState)) {
                this.floatingState = 'PINNED_MAXIMIZED';
            }
            else if (this.isMinimized(this.floatingState)) {
                this.floatingState = 'PINNED_MINIMIZED';
            }
        }
    };
    ;
    FloatingGroup.prototype.unpinGroup = function (skipValueChange, skipClass) {
        if (skipClass === void 0) { skipClass = undefined; }
        if (!skipClass) {
            this.component.classList.add('floating');
        }
        this.addInlineStyles();
        if (this.pinningMode == 'button') {
            this.component.classList.remove('btn');
            this.component.classList.remove('btn-primary');
            this.component.classList.add('card');
            this.component.classList.add('card-default');
        }
        if (!this.hideHeader) {
            this.component.querySelector('.card-header').addEventListener('mousedown', this.bound.toggleMouseDown);
        }
        this.component.querySelector('.resizeHandle').addEventListener('mousedown', this.bound.resizeMouseDown);
        window.addEventListener('mousemove', this.bound.mouseMove);
        window.addEventListener('mouseup', this.bound.mouseUp);
        if (!skipValueChange) {
            if (this.isMaximized(this.floatingState)) {
                this.floatingState = 'UNPINNED_MAXIMIZED';
            }
            else if (this.isMinimized(this.floatingState)) {
                this.floatingState = 'UNPINNED_MINIMIZED';
            }
        }
        var footer = this.component.querySelector('.panel-footer');
        if (footer) {
            var body = this.component.querySelector('.card-body');
            // 10px padding - 1px border top
            var offset = 10 - 1;
            body.style.height = 'calc(100% - 28px - ' + (footer.clientHeight + offset) + 'px)';
        }
    };
    ;
    FloatingGroup.prototype.toggleFullScreen = function (skipEvent, skipValueChange) {
        if (this.isMaximized(this.floatingState)) {
            if (this.isPinned(this.floatingState)) {
                this.unpinGroup(skipValueChange, true);
            }
            this.startFullScreen(skipValueChange);
        }
        else if (this.isMinimized(this.floatingState)) {
            this.endFullScreen(skipValueChange);
        }
        if (!skipEvent) {
            this.changesQueue.queueAttributeChange('floatingState', this.floatingState);
        }
        $(window).trigger('resize.floatingGroup');
    };
    ;
    FloatingGroup.prototype.isUnpinned = function (value) {
        return value === 'UNPINNED_MINIMIZED' || value === 'UNPINNED_MAXIMIZED';
    };
    ;
    FloatingGroup.prototype.isPinned = function (value) {
        return value === 'PINNED_MINIMIZED' || value === 'PINNED_MAXIMIZED';
    };
    ;
    FloatingGroup.prototype.isMaximized = function (value) {
        return value === 'UNPINNED_MAXIMIZED' || value === 'PINNED_MAXIMIZED';
    };
    ;
    FloatingGroup.prototype.isMinimized = function (value) {
        return value === 'PINNED_MINIMIZED' || value === 'UNPINNED_MINIMIZED';
    };
    ;
    FloatingGroup.prototype.startFullScreen = function (skipValueChange) {
        this.component.classList.add('fullscreen');
        this.removeInlineStyles();
        var fullscreenToggleIcon = this.groupToolbox.querySelector('.fullscreenToggle > span');
        if (fullscreenToggleIcon != null) {
            fullscreenToggleIcon.classList.remove('fa-expand-arrows-alt');
            fullscreenToggleIcon.classList.add('fa-expand');
        }
        if (!skipValueChange) {
            if (this.isPinned(this.floatingState)) {
                this.floatingState = 'UNPINNED_MAXIMIZED';
            }
            else if (this.isUnpinned(this.floatingState)) {
                this.floatingState = 'PINNED_MAXIMIZED';
            }
        }
    };
    ;
    FloatingGroup.prototype.endFullScreen = function (skipValueChange) {
        this.component.classList.remove('fullscreen');
        this.addInlineStyles();
        var fullscreenToggleIcon = this.groupToolbox.querySelector('.fullscreenToggle > span');
        if (fullscreenToggleIcon != null) {
            fullscreenToggleIcon.classList.remove('fa-expand');
            fullscreenToggleIcon.classList.add('fa-expand-arrows-alt');
        }
        this.togglePin();
        if (!skipValueChange) {
            if (this.isPinned(this.floatingState)) {
                this.floatingState = 'UNPINNED_MINIMIZED';
            }
            else if (this.isUnpinned(this.floatingState)) {
                this.floatingState = 'PINNED_MINIMIZED';
            }
        }
    };
    ;
    FloatingGroup.prototype.update = function (change) {
        _super.prototype.update.call(this, change);
        $.each(change.changedAttributes, function (name, newValue) {
            switch (name) {
                case 'floatingState':
                    // if(this.isUnpinned(newValue)) {
                    //     this.unpinGroup();
                    // } else if(this.isPinned(newValue)) {
                    //     this.pinGroup();
                    // }
                    // if(this.isMaximized(newValue)) {
                    //     this.startFullScreen();
                    // } else if(this.isMinimized(newValue)) {
                    //     this.endFullScreen();
                    // }
                    this.floatingState = newValue;
                    this.togglePin(true, true);
                    this.toggleFullScreen(true, true);
                    break;
            }
        }.bind(this));
    };
    ;
    FloatingGroup.prototype.addInlineStyles = function () {
        if (this.component.classList.contains('floating') && !this.component.classList.contains('fullscreen')) {
            this.component.style.width = this.dimensions.width + 'px';
            this.component.style.height = this.dimensions.height + 'px';
            this.component.style.top = this.position.top + 'px';
            this.component.style.left = this.position.left + 'px';
        }
    };
    ;
    FloatingGroup.prototype.removeInlineStyles = function () {
        this.component.style.width = '';
        this.component.style.height = (this.componentObj.height == null ? '' : this.componentObj.height);
        this.component.style.top = '';
        this.component.style.left = '';
    };
    ;
    FloatingGroup.prototype.toggleMouseDown = function (event) {
        this.headerClick = true;
        this.mouseDown(event);
    };
    ;
    FloatingGroup.prototype.resizeMouseDown = function (event) {
        this.resizing = true;
        this.mouseDown(event);
    };
    ;
    FloatingGroup.prototype.mouseDown = function (event) {
        event.preventDefault();
        this.drag.posX = this.position.left;
        this.drag.posY = this.position.top;
        this.drag.width = this.dimensions.width;
        this.drag.height = this.dimensions.height;
        this.drag.startX = event.clientX;
        this.drag.startY = event.clientY;
    };
    ;
    FloatingGroup.prototype.mouseMove = function (event) {
        if (!this.headerClick || this.isMaximized(this.floatingState)) {
            return;
        }
        if (!this.component.classList.contains('fullscreen')) {
            event.preventDefault();
            var xMove = event.clientX - this.drag.startX;
            var yMove = event.clientY - this.drag.startY;
            if ((xMove != 0 || yMove != 0) && this.isDraggable) {
                this.dragging = true;
            }
            var newX = this.drag.posX + (this.dragging ? xMove : 0);
            var newY = this.drag.posY + (this.dragging ? yMove : 0);
            var newWidth = this.drag.width + (this.resizing ? xMove : 0);
            var newHeight = this.drag.height + (this.resizing ? yMove : 0);
            var leftBorder = newX;
            var rightBorder = leftBorder + newWidth;
            var topBorder = newY;
            var bottomBorder = topBorder + newHeight;
        }
        if (this.dragging && this.isDraggable) {
            if (leftBorder >= 0 && rightBorder <= window.innerWidth) {
                this.position.left = leftBorder;
                this.component.style.left = this.position.left + 'px';
            }
            if (topBorder >= 0 && bottomBorder <= window.innerHeight) {
                this.position.top = topBorder;
                this.component.style.top = this.position.top + 'px';
            }
        }
        if (this.resizing) {
            if (leftBorder >= 0 && rightBorder <= window.innerWidth) {
                this.dimensions.width = newWidth;
                this.component.style.width = this.dimensions.width + 'px';
            }
            if (topBorder >= 0 && bottomBorder <= window.innerHeight) {
                this.dimensions.height = newHeight;
                this.component.style.height = this.dimensions.height + 'px';
            }
        }
    };
    ;
    FloatingGroup.prototype.mouseUp = function (event) {
        if (this.resizing) {
            $(window).trigger('resize.floatingGroup');
        }
        /**
         * If component is collapsible then params will be erase by parent method toggleCollapse
         * which fires after mouseUp.
         */
        if (this.headerClick && (!this.isCollapsible || this.resizing || this.isMaximized(this.floatingState))) {
            this.dragging = false;
            this.resizing = false;
            this.headerClick = false;
        }
    };
    ;
    FloatingGroup.prototype.extractChangedAttributes = function () {
        return this.changesQueue.extractChangedAttributes();
    };
    ;
    FloatingGroup.prototype.destroy = function (removeFromParent) {
        window.removeEventListener('mousemove', this.bound.mouseMove);
        window.removeEventListener('mouseup', this.bound.mouseUp);
        this.resizeListener.unregister();
        _super.prototype.destroy.call(this, removeFromParent);
    };
    /**
     * @Override default collapse behaviour so we can set default values for resizing and dragging attributes
     * This method fires after mouseUp event
     */
    FloatingGroup.prototype.toggleCollapse = function () {
        if (!this.dragging) {
            _super.prototype.toggleCollapse.call(this);
        }
        this.dragging = false;
        this.resizing = false;
        this.headerClick = false;
    };
    return FloatingGroup;
}(PanelGroup_1.PanelGroup));
exports.FloatingGroup = FloatingGroup;
//# sourceMappingURL=FloatingGroup.js.map