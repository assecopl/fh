export class Portal {
  constructor() {
    this.init();
    window['fhportal'] = this;
  }

  private MAX_VERSION = 2;
  private MIN_VERSION = 1;
  public static OBSERVER_CONFIG = { attributes: true, childList: true, subtree: true };
  public static OBSERVERS = [];

  private VERSION_STORE = {
    '1': this.version_1,
    '2': this.version_2
  }

  init() {
    if (!window['fhportal']) {
      const config = { attributes: true, childList: true, subtree: true };
      const observer = new MutationObserver(this.handleMutation);
      observer.observe(document.body, Portal.OBSERVER_CONFIG);
    }
  }

  version_1({sourceElement, portal, sourceId}) {
    const sourceCoords = sourceElement.getBoundingClientRect() as any;
    const portalCoords = portal.getBoundingClientRect() as any;
    if (portalCoords.x !== sourceCoords.x || portalCoords.y !== sourceCoords.y) {
      const newX = portalCoords.x - sourceCoords.x;
      const newY = portalCoords.y - sourceCoords.y;
      sourceElement.style.transform = `translate(${newX}px, ${newY}px)`;
      if (document.getElementById(sourceId).blur) {
        document.getElementById(sourceId).blur();
      }
    } else {
      setTimeout(() => {
        const sourceCoords = sourceElement.getBoundingClientRect() as any;
        const portalCoords = portal.getBoundingClientRect() as any;
        const newX = portalCoords.x - sourceCoords.x;
        const newY = portalCoords.y - sourceCoords.y;
        sourceElement.style.transform = `translate(${newX}px, ${newY}px)`;
        if (document.getElementById(sourceId).blur) {
          document.getElementById(sourceId).blur();
        }
      }, 500);
    }
    portal.setAttribute('used', 'true');
  }

  version_2({sourceElement, portal, sourceId}) {
    const replaceParentId = portal.getAttribute('replaceParentId')
    if (!!replaceParentId) {
      const oldParent = document.getElementsByClassName(replaceParentId)[0];
      if(!!oldParent) {
        oldParent.parentElement.appendChild(portal)
        oldParent.remove();
      }
    }

    portal.parentElement.appendChild(sourceElement);
    if (document.getElementById(sourceId) && document.getElementById(sourceId).blur) {
      document.getElementById(sourceId).blur();
    }
    portal.style.display = 'none';
    portal.setAttribute('used', 'true');
    const observer = new MutationObserver((mutationList, observer) => {
      if (!portal.parentElement.contains(sourceElement) && document.body.contains(portal)) {
        console.log("yoy nasty bastard!");
        portal.setAttribute('used', 'false');
      } else if (!document.body.contains(portal) || !document.body.contains(sourceElement)) {
        observer.disconnect();
        Portal.OBSERVERS[sourceId] = undefined;
      }
    });
    observer.observe(portal.parentElement, Portal.OBSERVER_CONFIG);
    if (Portal.OBSERVERS[sourceId]) {
      Portal.OBSERVERS[sourceId].disconnect();
    }
    Portal.OBSERVERS[sourceId] = observer;
  }

  processVersion(version: number, args: {[key: string]: any}) {
    const f = this.VERSION_STORE[`${version}`];
    console.log(version, args, f)
    if (f) {
      f(args)
    }
  }



  private handleMutation = (mutationList, observer) => {
    const portals = document.querySelectorAll('fh-portal[used="false"]');
    portals.forEach(portal => {
      try {
        const broken = portal.getAttribute('broken');
        if (!broken) {
          const searchClosestLike = portal.getAttribute('searchClosestLike');
          const searchParent = portal.getAttribute('searchParent');
          const sourceId = portal.getAttribute('objid');
          const classes = portal.getAttribute('classes');
          const wrapped = portal.getAttribute('wrapped');
          const layer = portal.getAttribute('layer');
          const v = portal.getAttribute('v');
          const removeWrapper = portal.getAttribute('removeWrapper');
          let isWrapped = false;
          let isRemoveWrapper = false;
          let isSearchClosest = false;
          let zIndex;
          let version;
          if (v !== undefined && Number(v) !== NaN && Number(v) >= this.MIN_VERSION && Number(v) <= this.MAX_VERSION) {
            version = Number(v);
          } else {
            version = this.MAX_VERSION;
          }
          if (searchClosestLike) {
            isSearchClosest = searchClosestLike === 'true';
          }
          if (wrapped) {
            isWrapped = wrapped === 'true';
          }
          if (removeWrapper) {
            isRemoveWrapper = removeWrapper === 'true';
          }
          if (layer !== undefined) {
            zIndex = parseInt(layer);
          }
          let properClasses;
          if (classes) {
            properClasses = classes.split(',');
          }

          let sourceElement;
          if (isSearchClosest) {
            if (!searchParent) {
                console.error('missing searchParent!')
            } else {
                const parent = portal.closest(searchParent);
                sourceElement = parent.querySelector(`[id^='${sourceId}']`)
            }
          } else {
            sourceElement = document.getElementById(sourceId);
          }
          if (isWrapped) {
            while (!sourceElement.parentElement.classList.contains('wrapper')) {
              sourceElement = sourceElement.parentElement;
            }
            sourceElement = sourceElement.parentElement;
          } else if(isRemoveWrapper) {
            let parentWrapper = document.getElementById(sourceElement.id);
            while (!parentWrapper.parentElement.classList.contains('wrapper')) {
              parentWrapper = parentWrapper.parentElement;
            }
            parentWrapper.parentElement.remove();
          }
          if (zIndex) {
            sourceElement.style.zIndex = zIndex;
          }
          if (properClasses) {
            properClasses.forEach(cls => {
              sourceElement.classList.add(cls);
            });
          }
          if (sourceElement) {
            console.log(version, {sourceElement, portal, sourceId, properClasses});
            this.processVersion(version, {sourceElement, portal, sourceId, properClasses})
          } else {
            console.error(`There is no element (id: ${sourceId}) that can be portalled`);
          }
        }
      } catch (e) {
        portal.setAttribute('broken', 'true');
        console.error(e);
      }
    })
  }
}