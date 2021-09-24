import {HTMLFormComponent} from "fh-forms-handler";
import {WindowEventsListener} from "fh-forms-handler";

class SplitContainer extends HTMLFormComponent {
    private bothShown: boolean;
    private divider: HTMLDivElement;
    private sizeleft: any;
    private sizeright: any;
    private fixed: any;
    private resizing: any;
    private startX: any;
    private startBasis: any;
    private resizeListener: WindowEventsListener;
    private windowListenerMouseMove: any;
    private windowListenerMouseUp: any;

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);

        this.sizeleft = this.componentObj.sizeLeft || null;
        this.sizeright = this.componentObj.sizeRight || null;
        this.fixed = null;

        this.resizing = false;
        this.startX = 0;
        this.startBasis = 0;
        this.bothShown = true;
    }

    create() {
        let container = document.createElement('div');
        container.id = this.id;
        container.classList.add('fc');
        container.classList.add('splitContainer');

        this.component = container;
        this.wrap(true);
        this.addStyles();
        this.createPanelsAndDivider();
    };

    createPanelsAndDivider() {
        if (this.componentObj.subelements && this.componentObj.subelements.length > 0) {

            let divider = document.createElement('div');
            divider.classList.add('divider');

            let hideLeft = document.createElement('div');
            ['hideLeft', 'fa', 'fa-caret-left'].forEach(function (cssClass) {
                hideLeft.classList.add(cssClass);
            });
            hideLeft.addEventListener('click', this.hideLeft.bind(this));
            divider.appendChild(hideLeft);

            let showBoth = document.createElement('div');
            ['showBoth', 'fa', 'fa-pause'].forEach(function (cssClass) {
                showBoth.classList.add(cssClass);
            });
            showBoth.addEventListener('click', this.showBoth.bind(this));
            divider.appendChild(showBoth);

            let hideRight = document.createElement('div');
            ['hideRight', 'fa', 'fa-caret-right'].forEach(function (cssClass) {
                hideRight.classList.add(cssClass);
            });
            hideRight.addEventListener('click', this.hideRight.bind(this));
            divider.appendChild(hideRight);

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
                    let xMove = event.clientX - this.startX;
                    let basis = this.startBasis;
                    let newBasis = basis;

                    if (this.fixed == this.contentWrapper.firstChild) {
                        newBasis = basis + xMove;
                    } else {
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

            this.resizeListener = new WindowEventsListener('resize resize.mainContainer resize.floatingGroup',
                function () {
                    let rightDivider = this.fixed.offsetWidth + this.divider.offsetWidth;
                    let maxRightPosition = this.component.offsetWidth;
                    if (rightDivider > maxRightPosition) {
                        this.fixed.style.flexBasis = (maxRightPosition - this.divider.offsetWidth - 100) + 'px';
                        this.hideRight();
                    }
                }.bind(this));
        }
    };

    addSideComponent(componentObj, side) {
        componentObj.width = 'md-12';
        let component = this.fh.createComponent(componentObj, this);

        this.components.push(component);
        component.create();

        let panel = document.createElement('div');
        panel.classList.add('splitPanel');

        let sideSize = this['size' + side];
        if (sideSize && sideSize != 'auto') {
            panel.style.flexBasis = sideSize;
            this.fixed = panel;
        } else {
            panel.classList.add('fluid');
        }

        let row = document.createElement('div');
        // row.classList.add('row');
        // row.classList.add('eq-row');

        row.appendChild(component.htmlElement);
        panel.appendChild(row);
        this.contentWrapper.appendChild(panel);
        if (typeof component.container !== "undefined") {
            component.container = row;
        }
    };

    hideLeft() {
        this.bothShown = false;
        this.component.classList.add('hiddenLeft');
        this.component.classList.remove('hiddenRight');
        $(window).trigger('resize.splitContainer');
    };

    hideRight() {
        this.bothShown = false;
        this.component.classList.remove('hiddenLeft');
        this.component.classList.add('hiddenRight');
        $(window).trigger('resize.splitContainer');
    };

    showBoth() {
        this.bothShown = true;
        this.component.classList.remove('hiddenLeft');
        this.component.classList.remove('hiddenRight');
        $(window).trigger('resize.splitContainer');
    };

    destroy(removeFromParent) {
        if (this.resizeListener != null) {
            this.resizeListener.unregister();
        }
        if (this.windowListenerMouseUp != null) {
            window.removeEventListener('mouseup', this.windowListenerMouseUp);
        }
        if (this.windowListenerMouseMove != null) {
            window.removeEventListener('mousemove', this.windowListenerMouseMove);
        }
        super.destroy(removeFromParent);
    }
}

export {SplitContainer}
