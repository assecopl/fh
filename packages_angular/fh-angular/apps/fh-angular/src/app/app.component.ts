import {Component, Inject, OnInit} from '@angular/core';
import {FH} from "../../../../libs/fh-ng-core-lite/src/lib/Socket/FH";
import {CustomActionsManager} from "../../../../libs/fh-ng-core-lite/src/lib/service/custom-actions-manager.service";
import {DOCUMENTUtils} from "../../../../libs/fh-ng-core-lite/src/lib/service/DOCUMENTUtils";
import {DOCUMENT} from '@angular/common';


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent implements OnInit {

  constructor(private fh: FH,
              private customActionsManager: CustomActionsManager,
              private documentUtils:DOCUMENTUtils,
              @Inject(DOCUMENT) private document: Document) {


  }

  ngOnInit(): void {
    this.fh.init();
    this.customActionsManager.registerCallback('hideMenu', () => {
      this.documentUtils.toggleClassById('appLayout', 'menu-open', 'menu-closed');
    });

    this.customActionsManager.registerCallback('hideMenu', () => {
      this.documentUtils.toggleClassById('appLayout', 'menu-open', 'menu-closed');
    });

    this.customActionsManager.registerCallback('showMenu', () => {
      this.documentUtils.toggleClassById('appLayout', 'menu-closed', 'menu-open');
    });

    this.customActionsManager.registerCallback('hideSideBar', () => {
      this.documentUtils.toggleClassById('appLayout', 'sidebar-open', 'sidebar-closed');
    });

    this.customActionsManager.registerCallback('showSideBar', () => {
      this.documentUtils.toggleClassById('appLayout', 'sidebar-closed', 'sidebar-open');
    });

    this.customActionsManager.registerCallback('hideAdvancedSideBar', () => {
      this.documentUtils.toggleClassById('appLayout', 'advanced-sidebar-open', 'advanced-sidebar-closed');
    });

    this.customActionsManager.registerCallback('showAdvancedSideBar', () => {
      this.documentUtils.toggleClassById('appLayout', 'advanced-sidebar-closed', 'advanced-sidebar-open');
    });

    this.customActionsManager.registerCallback('hideAppSiderHelp', () => {
      this.documentUtils.toggleClassById('appSiderHelp', 'app-sider--open', 'app-sider--closed');
    });

    this.customActionsManager.registerCallback('showAppSiderHelp', () => {
      this.documentUtils.toggleClassById('appSiderHelp', 'app-sider--closed', 'app-sider--open');
    });

    this.customActionsManager.registerCallback('hideAppSiderDetails', () => {
      this.documentUtils.toggleClassById('appSiderDetails', 'app-sider--open');
    });

    this.customActionsManager.registerCallback('showAppSiderDetails', () => {
      this.documentUtils.toggleClassById('appSiderDetails', null, 'app-sider--open');
    });

    this.customActionsManager.registerCallback('hideButtons', () => {
      this.documentUtils.toggleClassById('buttonsForm', null, 'd-none');
    });

    this.customActionsManager.registerCallback('showButtons', () => {
      this.documentUtils.toggleClassById('buttonsForm', 'd-none');
    });

    this.customActionsManager.registerCallback('hideSearchButtons', () => {
      this.documentUtils.toggleClassById('searchButtonsForm', null, 'd-none');
    });

    this.customActionsManager.registerCallback('showSearchButtons', () => {
      this.documentUtils.toggleClassById('searchButtonsForm', 'd-none');
    });

    this.customActionsManager.registerCallback('hideHeaderSearchButton', () => {
      this.documentUtils.toggleClassByWrapper('headerSearchButton', null, 'd-none');
    });

    this.customActionsManager.registerCallback('showHeaderSearchButton', () => {
      this.documentUtils.toggleClassByWrapper('headerSearchButton', 'd-none');
    });

    this.customActionsManager.registerCallback('hideWrapperComponentById',  (componentId) => {
      const component = (this.document.querySelectorAll(`[id^='${componentId}']`)[0] as HTMLElement);
      this.documentUtils.toggleClassByWrapper(component.id, null, 'd-none');
    });

    this.customActionsManager.registerCallback('showWrapperComponentById',  (componentId) => {
      const component = (this.document.querySelectorAll(`[id^='${componentId}']`)[0] as HTMLElement);
      this.documentUtils.toggleClassByWrapper(component.id, 'd-none');
    });

    this.customActionsManager.registerCallback('dynamicPositionPaginationRow', () => {
      const element = this.document.querySelector('[id^="table-group"]');
      const wrapper = !!element ? (element.parentNode as HTMLElement): null;

      if(!!wrapper) {
        wrapper.addEventListener('scroll', () => {
          const paginationRow = (this.document.getElementsByClassName('toolsRow')[0] as HTMLElement);
          // paginationRow.style.left = `${this.scrollLeft}px`;
        })
      }
    })

    this.customActionsManager.registerCallback('dynamicStickyTableHeader',  (data) => {
      const ids = data.split(',');
      if(ids.length === 2) {
        const scrollcon = this.document.getElementById(ids[0]);
        if(!!scrollcon) {
          scrollcon.lastElementChild.addEventListener("scroll", () => {
            const baseContainer = this.document.querySelector(`[id^="${ids[1]}"`);
            if(!!baseContainer) {
              const ulTabs = baseContainer.getElementsByClassName('nav-tabs');
              if(!!ulTabs && ulTabs.length > 0) {
                const ulTab = (ulTabs[0] as HTMLElement);
                let height = ulTab.offsetHeight;
                const afterStyle = window.getComputedStyle(ulTab, '::after');
                if(!!afterStyle) {
                  const afterHeight = afterStyle.getPropertyValue("height").match(/\d+/g);
                  if(!!afterHeight && afterHeight.length > 0) {
                    height += parseFloat(afterHeight[0]);
                  }
                }
                const tabContents = baseContainer.getElementsByClassName('tab-content');
                if(!!tabContents && tabContents.length > 0) {
                  const activeTabs = tabContents[0].getElementsByClassName('active');
                  if(!!activeTabs && activeTabs.length > 0) {
                    const thElements = activeTabs[0].getElementsByTagName('th');

                    for(let i=0; i<thElements.length; i++) {
                      thElements[i].style.top = `${height}px`;
                    }
                  }
                }
              }
            }
          })
        }
      }
    })

    this.customActionsManager.registerCallback('enableTabsSeparated', () => {
      const component = (this.document.getElementsByClassName('tabs')[0] as HTMLElement);
      if(!!component)
        component.classList.add('tabs--separated');
    });

    this.customActionsManager.registerCallback('disableTabsSeparated', () => {
      const component = (this.document.getElementsByClassName('tabs')[0] as HTMLElement);
      if(!!component)
        component.classList.remove('tabs--separated');
    });

    this.customActionsManager.registerCallback('replaceHtmlStyleClass',  (data)=> {
      const component = (this.document.getElementsByTagName('html')[0] as HTMLElement);
      let split = data.split(',');
      // console.log("*** Split3: ", split);
      component.classList.remove(split[0]);
      component.classList.add(split[1]);
    });

    this.customActionsManager.registerCallback('replaceBodyStyleClass',  (data)=> {
      const component = (this.document.getElementsByTagName('body')[0] as HTMLElement);
      let split = data.split(',');
      // console.log("*** Split3: ", split);
      component.classList.remove(split[0]);
      component.classList.add(split[1]);
    });

    this.customActionsManager.registerCallback('fireScrollEvent',  (data)=> {
      const split = data.split(',');
      if(!!split && split.length > 0) {
        const childElement = (this.document.querySelector(`[id^="${split[0]}"]`) as HTMLElement);
        if(!!childElement) {
          let parentElement = (childElement.parentElement as HTMLElement);
          if(!!parentElement) {
            let isParentScroll = false;
            while(!isParentScroll) {
              if(!!parentElement){
                const parentOverflow = window.getComputedStyle(parentElement).overflow;
                if(parentOverflow==='auto') {
                  parentElement.scrollTop = 0;
                  isParentScroll = true;
                }
              } else {
                isParentScroll = true;
              }
              parentElement = (parentElement.parentElement as HTMLElement);
            }
          }
        }
      }
    });

    this.customActionsManager.registerCallback('setViewAvailabilityTable',  (data)=> {
      const tableId = String(data);
      if(!!tableId && tableId.length > 0) {
        const tableElement = this.document.getElementById(tableId);
        if(!!tableElement) {
          tableElement.classList.add('tables-disable-pointer-events');

          // disable all inputs
          const inputList = tableElement.getElementsByTagName('input');
          for(let i=0; i<inputList.length; i++) {
            inputList[i].disabled = true;
          }
        }
      }
    });

    this.customActionsManager.registerCallback('setEditAvailabilityTable',  (data)=> {
      const tableId = String(data);
      if(!!tableId && tableId.length > 0) {
        const tableElement = this.document.getElementById(tableId);
        if(!!tableElement) {
          tableElement.classList.remove('tables-disable-pointer-events');

          // enable all inputs
          const inputList = tableElement.getElementsByTagName('input');
          for(let i=0; i<inputList.length; i++) {
            inputList[i].disabled = false;
          }
        }
      }
    });

    this.customActionsManager.registerCallback('openPinup', function (url) {
      window.open(url, 'pinup', 'toolbar=0,location=0,menubar=0').focus();
    });

    this.customActionsManager.registerCallback('selectSearchBoxButton', (buttonID) => {
      this.documentUtils.toggleClassById(buttonID, null, 'search-box-button--selected');
    });

    this.customActionsManager.registerCallback('unSelectSearchBoxButton', (buttonID) => {
      this.documentUtils.toggleClassById(buttonID, 'search-box-button--selected');
    });
  }

  title = 'fh-angular';
}
