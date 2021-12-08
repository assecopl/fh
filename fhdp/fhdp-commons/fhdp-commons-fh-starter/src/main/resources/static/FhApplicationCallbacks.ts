import {FhApplication} from "fhdp-fh-starter";

export const initFhCallbacks = () => {

  FhApplication.getInstance().registerCallback('hideMenu', function () {
    FhApplication.getInstance().createCallbackById('appLayout', 'menu-open', 'menu-closed');
  });

  FhApplication.getInstance().registerCallback('showMenu', function () {
      FhApplication.getInstance().createCallbackById('appLayout', 'menu-closed', 'menu-open');
  });

  FhApplication.getInstance().registerCallback('hideSideBar', function () {
      FhApplication.getInstance().createCallbackById('appLayout', 'sidebar-open', 'sidebar-closed');
  });

  FhApplication.getInstance().registerCallback('showSideBar', function () {
      FhApplication.getInstance().createCallbackById('appLayout', 'sidebar-closed', 'sidebar-open');
  });

  FhApplication.getInstance().registerCallback('hideAdvancedSideBar', function () {
      FhApplication.getInstance().createCallbackById('appLayout', 'advanced-sidebar-open', 'advanced-sidebar-closed');
  });

  FhApplication.getInstance().registerCallback('showAdvancedSideBar', function () {
      FhApplication.getInstance().createCallbackById('appLayout', 'advanced-sidebar-closed', 'advanced-sidebar-open');
  });

  FhApplication.getInstance().registerCallback('hideAppSiderHelp', function () {
      FhApplication.getInstance().createCallbackById('appSiderHelp', 'app-sider--open', 'app-sider--closed');
  });

  FhApplication.getInstance().registerCallback('showAppSiderHelp', function () {
      FhApplication.getInstance().createCallbackById('appSiderHelp', 'app-sider--closed', 'app-sider--open');
  });

  FhApplication.getInstance().registerCallback('hideAppSiderDetails', function () {
      FhApplication.getInstance().createCallbackById('appSiderDetails', 'app-sider--open');
  });

  FhApplication.getInstance().registerCallback('showAppSiderDetails', function () {
      FhApplication.getInstance().createCallbackById('appSiderDetails', null, 'app-sider--open');
  });

  FhApplication.getInstance().registerCallback('hideButtons', function () {
      FhApplication.getInstance().createCallbackById('buttonsForm', null, 'd-none');
  });

  FhApplication.getInstance().registerCallback('showButtons', function () {
      FhApplication.getInstance().createCallbackById('buttonsForm', 'd-none');
  });

  FhApplication.getInstance().registerCallback('hideSearchButtons', function () {
      FhApplication.getInstance().createCallbackById('searchButtonsForm', null, 'd-none');
  });

  FhApplication.getInstance().registerCallback('showSearchButtons', function () {
      FhApplication.getInstance().createCallbackById('searchButtonsForm', 'd-none');
  });

  FhApplication.getInstance().registerCallback('hideHeaderSearchButton', function () {
      FhApplication.getInstance().createCallbackByWrapper('headerSearchButton', null, 'd-none');
  });

  FhApplication.getInstance().registerCallback('showHeaderSearchButton', function () {
      FhApplication.getInstance().createCallbackByWrapper('headerSearchButton', 'd-none');
  });

  FhApplication.getInstance().registerCallback('hideWrapperComponentById', function (componentId) {
      const component = (document.querySelectorAll(`[id^='${componentId}']`)[0] as HTMLElement);
      FhApplication.getInstance().createCallbackByWrapper(component.id, null, 'd-none');
  });

  FhApplication.getInstance().registerCallback('showWrapperComponentById', function (componentId) {
      const component = (document.querySelectorAll(`[id^='${componentId}']`)[0] as HTMLElement);
      FhApplication.getInstance().createCallbackByWrapper(component.id, 'd-none');
  });

  FhApplication.getInstance().registerCallback('dynamicPositionPaginationRow', function () {
      const element = document.querySelector('[id^="table-group"]');
      const wrapper = !!element ? (element.parentNode as HTMLElement): null;

      if(!!wrapper) {
          wrapper.addEventListener('scroll', function() {
              const paginationRow = (document.getElementsByClassName('toolsRow')[0] as HTMLElement);
              paginationRow.style.left = `${this.scrollLeft}px`;
          })
      }
  })

  FhApplication.getInstance().registerCallback('dynamicStickyTableHeader', function (data) {
      const ids = data.split(',');
      if(ids.length === 2) {
          const scrollcon = document.getElementById(ids[0]);
          if(!!scrollcon) {
              scrollcon.lastElementChild.addEventListener("scroll", function() {
                  const baseContainer = document.querySelector(`[id^="${ids[1]}"`);
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

  FhApplication.getInstance().registerCallback('enableTabsSeparated', function () {
      const component = (document.getElementsByClassName('tabs')[0] as HTMLElement);
      if(!!component)
          component.classList.add('tabs--separated');
  });

  FhApplication.getInstance().registerCallback('disableTabsSeparated', function () {
      const component = (document.getElementsByClassName('tabs')[0] as HTMLElement);
      if(!!component)
          component.classList.remove('tabs--separated');
  });

  FhApplication.getInstance().registerCallback('replaceHtmlStyleClass', function (data) {
      const component = (document.getElementsByTagName('html')[0] as HTMLElement);
      let split = data.split(',');
      // console.log("*** Split3: ", split);
      component.classList.remove(split[0]);
      component.classList.add(split[1]);
  });

  FhApplication.getInstance().registerCallback('replaceBodyStyleClass', function (data) {
          const component = (document.getElementsByTagName('body')[0] as HTMLElement);
          let split = data.split(',');
          // console.log("*** Split3: ", split);
          component.classList.remove(split[0]);
          component.classList.add(split[1]);
      });
  
  FhApplication.getInstance().registerCallback('fireScrollEvent', function (data) {
      const split = data.split(',');
      if(!!split && split.length > 0) {
          const childElement = (document.querySelector(`[id^="${split[0]}"]`) as HTMLElement);
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

  FhApplication.getInstance().registerCallback('setViewAvailabilityTable', function (data) {
    const tableId = String(data);
    if(!!tableId && tableId.length > 0) {
        const tableElement = document.getElementById(tableId);
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

  FhApplication.getInstance().registerCallback('setEditAvailabilityTable', function (data) {
      const tableId = String(data);
      if(!!tableId && tableId.length > 0) {
          const tableElement = document.getElementById(tableId);
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

  FhApplication.getInstance().registerCallback('openPinup', function (url) {
      window.open(url, 'pinup', 'toolbar=0,location=0,menubar=0').focus();
  });

  FhApplication.getInstance().registerCallback('selectSearchBoxButton', function (buttonID) {
      FhApplication.getInstance().createCallbackById(buttonID, null, 'search-box-button--selected');
  });

  FhApplication.getInstance().registerCallback('unSelectSearchBoxButton', function (buttonID) {
      FhApplication.getInstance().createCallbackById(buttonID, 'search-box-button--selected');
  });
}