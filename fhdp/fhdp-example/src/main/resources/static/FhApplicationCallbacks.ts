import {FhApplication} from "fhdp-fh-starter";

export const initFhCallbacks = () => {

  FhApplication.getInstance().registerCallback('hideMenu', function () {
    FhApplication.getInstance().createCallbackById('appLayout', 'menu-open', 'menu-closed');
  });

  FhApplication.getInstance().registerCallback('showMenu', function () {
      FhApplication.getInstance().createCallbackById('appLayout', 'menu-closed', 'menu-open');
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

}