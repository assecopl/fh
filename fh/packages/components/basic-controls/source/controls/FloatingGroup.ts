import {HTMLFormComponent} from "fh-forms-handler";
import {WindowEventsListener} from "fh-forms-handler";
import {PanelGroup} from "./PanelGroup";

class FloatingGroup extends PanelGroup {
    private resizing: boolean;
    private drag: any;
    private dragging: boolean = false;
    private isDraggable: boolean = true;
    private dimensions: { width: number | number; height: number | number };
    private position: { top: number; left: number };
    private bound: { toggleMouseDown: any; resizeMouseDown: any; mouseMove: any; mouseUp: any };
    private hideHeader: any;
    private floatingState: any;
    private pinningMode: any;
    private onToggleFullScreen: any;
    private onTogglePin: any;
    private floatingOnly: any;
    private hideButtons: any;
    private resizeListener: WindowEventsListener;
    private headerClick : boolean;

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);

        this.forceHeader = true;
        this.onTogglePin = this.componentObj.onTogglePin;
        this.floatingState = this.componentObj.attributes.floatingState;
        this.pinningMode = this.componentObj.pinningMode || 'normal';
        this.onToggleFullScreen = this.componentObj.onToggleFullScreen;
        this.floatingOnly = !!this.componentObj.attributes.floatingOnly;
        this.hideButtons = !!this.componentObj.hideButtons;
        this.hideHeader = !!this.componentObj.hideHeader;
        this.isDraggable = !!this.componentObj.isDraggable;

        this.dragging = false;
        this.resizing = false;
        this.dimensions = {
            width: parseInt(this.componentObj.floatingWidth != null ? this.componentObj.floatingWidth : this.componentObj.width) || 350,
            height: parseInt(this.componentObj.floatingHeight != null ? this.componentObj.floatingHeight : this.componentObj.height) || 400
        };
        this.position = {
            top: parseInt(this.componentObj.top) || (window.innerHeight - this.dimensions.height) / 2,
            left: parseInt(this.componentObj.left) || (window.innerWidth - this.dimensions.width) / 2
        };

        if (this.dimensions.width < 0) {
            this.dimensions.width = window.innerWidth + this.dimensions.width;
        }
        if (this.dimensions.height < 0) {
            this.dimensions.height = window.innerHeight + this.dimensions.height;
        }
        if (this.position.left < 0) {
            this.position.left = window.innerWidth - this.dimensions.width + this.position.left;
        }
        if (this.position.top < 0) {
            this.position.top = window.innerHeight - this.dimensions.height + this.position.top;
        }


        this.drag = {startX: 0, startY: 0, posX: 0, posY: 0, width: 0, height: 0};
        this.bound = {
            toggleMouseDown: this.toggleMouseDown.bind(this),
            resizeMouseDown: this.resizeMouseDown.bind(this),
            mouseMove: this.mouseMove.bind(this),
            mouseUp: this.mouseUp.bind(this)
        }
    }

    create() {
        super.create();

        console.log("isDraggable", this.isDraggable)

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
                        } else if (this.isMaximized(this.floatingState)) {
                            this.floatingState = 'UNPINNED_MINIMIZED';
                        }
                    }
                    else if (this.isUnpinned(this.floatingState)) {
                        if (this.isMinimized(this.floatingState)) {
                            this.floatingState = 'PINNED_MINIMIZED';
                        } else if (this.isMaximized(this.floatingState)) {
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
                    } else if (this.isUnpinned(this.floatingState)) {
                        this.floatingState = 'UNPINNED_MINIMIZED';
                    }
                }
                else if (this.isMinimized(this.floatingState)) {
                    if (this.isPinned(this.floatingState)) {
                        this.floatingState = 'PINNED_MAXIMIZED';
                    } else if (this.isUnpinned(this.floatingState)) {
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
        } else if (this.pinningMode === 'invisible') {
            this.component.classList.add('pinningModeInvisible');
        }

        if(this.isDraggable){
            this.component.classList.add("fc-draggable");
        } else {
            this.component.classList.remove("fc-draggable");
        }

        this.resizeListener = new WindowEventsListener('resize', function () {
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

    togglePin(skipEvent = undefined, skipValueChange = undefined) {
        if (this.isUnpinned(this.floatingState)) {
            this.unpinGroup(skipValueChange);
        } else if (this.isPinned(this.floatingState)) {
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

    pinGroup(skipValueChange) {
        this.component.classList.remove('floating');
        this.removeInlineStyles();

        this.component.classList.remove('fullscreen');
        var fullscreenToggleIcon = this.groupToolbox.querySelector('.fullscreenToggle > span');
        if (fullscreenToggleIcon != null) {
            fullscreenToggleIcon.classList.remove('fa-expand');
            fullscreenToggleIcon.classList.add('fa-expand-arrows-alt');
        }
        if (!this.hideHeader) {
            this.component.querySelector('.card-header').removeEventListener('mousedown',
                this.bound.toggleMouseDown);
        }
        this.component.querySelector('.resizeHandle').removeEventListener('mousedown',
            this.bound.resizeMouseDown);
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

    unpinGroup(skipValueChange, skipClass = undefined) {
        if (!skipClass) {
            this.component.classList.add('floating');
        }
        this.addInlineStyles();

        if (!this.hideHeader) {
            this.component.querySelector('.card-header').addEventListener('mousedown',
                this.bound.toggleMouseDown);
        }
        this.component.querySelector('.resizeHandle').addEventListener('mousedown',
            this.bound.resizeMouseDown);
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

        let footer = this.component.querySelector('.panel-footer');
        if (footer) {
            let body = this.component.querySelector('.card-body');
            // 10px padding - 1px border top
            let offset = 10 - 1;
            body.style.height = 'calc(100% - 28px - ' + (footer.clientHeight + offset) + 'px)';
        }
    };

    toggleFullScreen(skipEvent, skipValueChange) {
        if (this.isMaximized(this.floatingState)) {
            if (this.isPinned(this.floatingState)) {
                this.unpinGroup(skipValueChange, true);
            }
            this.startFullScreen(skipValueChange);
        } else if (this.isMinimized(this.floatingState)) {
            this.endFullScreen(skipValueChange);
        }
        if (!skipEvent) {
            this.changesQueue.queueAttributeChange('floatingState', this.floatingState);
        }
        $(window).trigger('resize.floatingGroup');
    };

    isUnpinned(value) {
        return value === 'UNPINNED_MINIMIZED' || value === 'UNPINNED_MAXIMIZED';
    };

    isPinned(value) {
        return value === 'PINNED_MINIMIZED' || value === 'PINNED_MAXIMIZED';
    };

    isMaximized(value) {
        return value === 'UNPINNED_MAXIMIZED' || value === 'PINNED_MAXIMIZED';
    };

    isMinimized(value) {
        return value === 'PINNED_MINIMIZED' || value === 'UNPINNED_MINIMIZED';
    };

    startFullScreen(skipValueChange) {
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

    endFullScreen(skipValueChange) {
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

    update(change) {
        super.update(change);
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

    addInlineStyles() {
        if (this.component.classList.contains('floating') && !this.component.classList.contains(
                'fullscreen')) {
            this.component.style.width = this.dimensions.width + 'px';
            this.component.style.height = this.dimensions.height + 'px';
            this.component.style.top = this.position.top + 'px';
            this.component.style.left = this.position.left + 'px';
        }
    };

    removeInlineStyles() {
        this.component.style.width = '';
        this.component.style.height = (this.componentObj.height == null ? '' : this.componentObj.height);
        this.component.style.top = '';
        this.component.style.left = '';
    };

    toggleMouseDown(event) {
        this.headerClick = true;
        this.mouseDown(event);
    };

    resizeMouseDown(event) {
        this.resizing = true;
        this.mouseDown(event);
    };

    mouseDown(event) {
        event.preventDefault();

            this.drag.posX = this.position.left;
            this.drag.posY = this.position.top;
            this.drag.width = this.dimensions.width;
            this.drag.height = this.dimensions.height;
            this.drag.startX = event.clientX;
            this.drag.startY = event.clientY;
    };

    mouseMove(event) {

        if (!this.headerClick || this.isMaximized(this.floatingState)) {
            return;
        }


        if (!this.component.classList.contains('fullscreen') ) {
            event.preventDefault();
            var xMove = event.clientX - this.drag.startX;
            var yMove = event.clientY - this.drag.startY;

            if((xMove != 0 || yMove != 0) && this.isDraggable){
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

    mouseUp(event) {
        if (this.resizing) {
            $(window).trigger('resize.floatingGroup');
        }

        /**
         * If component is collapsible then params will be erase by parent method toggleCollapse
         * which fires after mouseUp.
         */
        if(this.headerClick && (!this.isCollapsible || this.resizing || this.isMaximized(this.floatingState))) {
            this.dragging = false;
            this.resizing = false;
            this.headerClick = false;
        }
    };

    extractChangedAttributes() {
        return this.changesQueue.extractChangedAttributes();
    };

    destroy(removeFromParent) {
        window.removeEventListener('mousemove', this.bound.mouseMove);
        window.removeEventListener('mouseup', this.bound.mouseUp);

        this.resizeListener.unregister();

        super.destroy(removeFromParent);
    }


    /**
     * @Override default collapse behaviour so we can set default values for resizing and dragging attributes
     * This method fires after mouseUp event
     */
    toggleCollapse() {
         if(!this.dragging) {
             super.toggleCollapse();
         }
        this.dragging = false;
        this.resizing = false;
        this.headerClick = false;
    }
}

export {FloatingGroup};
