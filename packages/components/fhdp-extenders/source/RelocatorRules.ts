// import {FhContainer, FHML} from "fh-forms-handler";
import {ComponentExtender} from "fh-forms-handler";
import {addRule, excludeRule} from "./extenders/DynamicRelocator"

export let BORDER_WINDOW_WIDTH = 768;

export const SET_BORDER_WINDOW_WIDHT = (width: number) => {
    BORDER_WINDOW_WIDTH = width;
}

const MOBILE_MENU_CLICK_HANDLER = (ev) => {
    if (document.body.offsetWidth < BORDER_WINDOW_WIDTH) {
    }
}

const componentExtender = ComponentExtender.getInstance();

export const initMobileNavbarRules = () => {
    addRule(
        '#navbarFormDesktop',
        async (mutation) => !!document.getElementById('navbarFormDesktop') && mutation.target.id === 'navbarFormDesktop',
        async (mutation) => {
            if (document.body.offsetWidth < BORDER_WINDOW_WIDTH) {
                mutation.target.style.display = 'none';
                document.getElementById('fhFooterContent').style.textAlign = 'left';
                document.getElementById('version').style.textAlign = 'right';
            } else {
                mutation.target.style.display = 'inline-block';
                document.getElementById('fhFooterContent').style.textAlign = 'center';
                document.getElementById('version').style.textAlign = 'center';
            }
        }
    );
    
    addRule(
        '#navbarFormMobile',
        async (mutation) => !!document.getElementById('navbarFormMobile') && mutation.target.id === 'navbarFormMobile',
        async (mutation) => {
            if (document.body.offsetWidth < BORDER_WINDOW_WIDTH) {
                mutation.target.style.display = 'flex';
                mutation.target.style.flexDirection = 'column-reverse';

                let prevScrollpos = window.pageYOffset;
                window.onscroll = () => {
                  const useerMenuMobile = (document.querySelector('#menuFormMobile') as any);
                  const mainMenuMobile = (document.querySelector('#mainMenuRowMobile') as any);
                  if (!(window as any).menuClickedLock && useerMenuMobile && useerMenuMobile.offsetParent) {
                    (window as any).menuClickedLock = true;
                    (document.querySelector('#mobileUserMenuButton') as any).click();
                    setTimeout(() => {
                        (window as any).menuClickedLock = false;
                    }, 800);
                  }
                  if (!(window as any).menuClickedLock && mainMenuMobile && mainMenuMobile.offsetParent) {
                    (window as any).menuClickedLock = true;
                    (document.querySelector('#mobileMainMenuButton') as any).click();
                    setTimeout(() => {
                        (window as any).menuClickedLock = false;
                    }, 800);
                  }
                  const currentScrollPos = window.pageYOffset;
                  if ((window as any).scrollCounter === true) {
                      let cs = document.querySelector('.counter-scroll') as any;
                      if (!cs) {
                        cs = document.createElement('span') as any;
                        cs.style.position = 'fixed';
                        cs.style.left = 0;
                        cs.style.top = 0;
                        cs.style.fontSize = 15;
                        cs.style.zIndex = 200;
                        cs.classList.add('counter-scroll');
                        document.body.appendChild(cs);
                      }
                      cs.innerText = window.pageYOffset;
                      console.log(window.pageYOffset);
                  }
                  if (prevScrollpos > currentScrollPos || currentScrollPos <= 0) {
                    (document.querySelector(".header-wrapper") as any).style.top = "0";
                  } else {
                    (document.querySelector(".header-wrapper") as any).style.top = "-86px";
                  }
                  prevScrollpos = currentScrollPos;
                }
                (window as any).handlerImplemented = true;
            } else {
                mutation.target.style.display = 'none';
                mutation.target.style.flexDirection = 'unset';
                window.onscroll = () => {}
                (document.querySelector(".header-wrapper") as any).style.top = "0";
                (window as any).handlerImplemented = false;
            }
        }
    );
}

export const initErrorBelowFieldRule = () => {
    addRule(
        '.form-control.border-danger',
        async (mutation) => {
            const selector = document.querySelector('.form-control.border-danger')
            if (!!selector) {
                const title = selector.getAttribute('title');
                if (!!title && title !== null && title !== 'null') {
                    return true;
                }
            }
            return false;
        },
        async (mutation) => {
            const allData = Array.from(document.querySelectorAll('.form-control.border-danger') as any);
            for (const fl of allData) {
                const field = fl as any;
                const title = field.getAttribute('title');
                if (!!title && title !== null && title !== 'null') {
                    field.removeAttribute('title');
                    let errorSpan = field.parentElement.parentElement.querySelector('.bap-error-span');
                    if (!errorSpan) {
                        errorSpan = document.createElement('span');
                        errorSpan.classList.add('bap-error-span');
                        field.parentElement.parentElement.appendChild(errorSpan);
                    }
                    errorSpan.innerText = title;
                }
            }
        }
    )

    addRule(
        '.bap-error-span',
        async (mutation) => {
            return !!document.querySelector('.bap-error-span') &&
                   !document.querySelector('.bap-error-span').parentElement.querySelector('.form-control').classList.contains('border-danger')

        },
        async (mutation) => {
            const allElements = Array.from(document.querySelectorAll('.bap-error-span') as any);
            for (const el of allElements) {
                const element = el as any;
                if (!element.parentElement.querySelector('.form-control').classList.contains('border-danger')) {
                    element.remove();
                }
            }
        }
    );
}
export const initDynamicFooterPositionRule = () => {
    addRule(
        '#fh-layout-standard',
        async (mutation) => {
            return document.getElementById('fh-layout-standard').offsetHeight + 90 + document.getElementById('fhFooter').offsetHeight > document.body.offsetHeight
        },
        async (muataion) => {
            let mobileOffset = 0;
            if (document.body.offsetWidth < BORDER_WINDOW_WIDTH) {
                mobileOffset = document.getElementById('fhFooter').offsetHeight;
            }
            document.documentElement.style.setProperty('--footer-position', `${document.getElementById('fh-layout-standard').offsetHeight + mobileOffset}px`)
        }
    )

    addRule(
        '#fh-layout-standard',
        async (mutation) => {
            return document.getElementById('fh-layout-standard').offsetHeight + document.getElementById('fhFooter').offsetHeight + Number(getComputedStyle(document.getElementById('fhFooter')).marginTop.replace('px', '')) + 90 <= document.body.offsetHeight
        },
        async (muataion) => {
            let mobileOffset = 0;
            if (document.body.offsetWidth < BORDER_WINDOW_WIDTH) {
                mobileOffset = 16;
            }
            document.documentElement.style.setProperty('--footer-position', `${(document.body.offsetHeight + mobileOffset) - document.getElementById('fhFooter').offsetHeight - Number(getComputedStyle(document.getElementById('fhFooter')).marginTop.replace('px', ''))}px`)
        }
    )
}

export const initCurrentMenuElementHighlightRule = (borderBottomStyle: string) => {
    const handlerFunc = (params, obj) => {
        const cel = obj.container.querySelector(`${obj.id} .current-menu-el:not(.highlighted)`);
        if (cel && getComputedStyle(cel.parentElement.parentElement.parentElement).display !== 'none') {
            cel.classList.add('highlighted');
            cel.parentElement.style.setProperty('border-bottom', borderBottomStyle, 'important');
        }
        const ncel = obj.container.querySelector(`${obj.id} .not-current-menu-el`);
        if (ncel) {
            ncel.parentElement.style.borderBottom = '';
            ncel.classList.remove('highlighted');
        }
    };
    componentExtender.addStaticExtender({
        componentName: 'Button',
        create: handlerFunc,
        update: (chParams, params, obj) => {
            handlerFunc(params, obj);
        }

    });
}

export const initHomeAutoSelectRule = () => {
    addRule(
        '.--home',
        async (mutation) => mutation.target.querySelector('.--home') && localStorage.getItem('homeClicked') !== 'true',
        async (mutation) => {
            localStorage.setItem('homeClicked', 'true');
            let el = document.querySelector('.--home');
            while (el.tagName !== 'BUTTON' && !!el.parentElement) {
                el = el.parentElement;
            }
            if (el.tagName === 'BUTTON') {
                (el as HTMLElement).click();
            }
            window.onbeforeunload = () => {
                localStorage.removeItem('homeClicked');
            }
        }
    )
}