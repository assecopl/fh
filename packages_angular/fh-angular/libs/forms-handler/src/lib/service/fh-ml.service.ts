import {Injectable, Pipe, SecurityContext} from '@angular/core';
import {DomSanitizer} from '@angular/platform-browser';
import {FhngComponent} from "../models/componentClasses/FhngComponent";

@Injectable({
  providedIn: 'root',
})
@Pipe({name: 'fhml'})
export class FhMLService {
  private supportedTags: any[];
  private escapedMap: {
    '&': string;
    '<': string;
    '>': string;
    '"': string;
    '`': string;
  };

  constructor(private sanitizer: DomSanitizer,
              private component: FhngComponent) {
    this.supportedTags = [];
    this.escapedMap = {
      // not escaping ' = / as they are fhml input and output characters
      '&': '&amp;',
      '<': '&lt;',
      '>': '&gt;',
      '"': '&quot;',
      '`': '&#x60;',
    };

    this.registerTag('b', function (component, match, contents) {
      const span = document.createElement('span');
      span.classList.add('fhml');
      span.classList.add('fhml-tag-b');

      span.innerHTML = contents;

      return span.outerHTML;
    });
    this.registerTag('i', function (component, match, contents) {
      const span = document.createElement('span');
      span.classList.add('fhml');
      span.classList.add('fhml-tag-i');

      span.innerHTML = contents;

      return span.outerHTML;
    });
    this.registerTag('u', function (component, match, contents) {
      const span = document.createElement('span');
      span.classList.add('fhml');
      span.classList.add('fhml-tag-u');

      span.innerHTML = contents;

      return span.outerHTML;
    });
    this.registerTag('lt', function (component, match, contents) {
      const span = document.createElement('span');
      span.classList.add('fhml');
      span.classList.add('fhml-tag-lt');

      span.innerHTML = contents;

      return span.outerHTML;
    });
    this.registerTag(
        'size',
        (component, match, size, contents) => {
          const span = document.createElement('span');
          span.classList.add('fhml');
          span.classList.add('fhml-tag-size');
          span.style.fontSize = size + 'px';

          span.innerHTML = contents;


          return span.outerHTML;
        },
        '([0-9]+)'
    );
    this.registerTag(
        'color',
        function (component, match, color, contents) {
          const span = document.createElement('span');
          span.classList.add('fhml');
          span.classList.add('fhml-tag-color');
          span.style.color = color;

          span.innerHTML = contents;

          return span.outerHTML;
        },
        '(#?[a-z0-9]+)'
    );
    this.registerTag(
        'icon',
        function (component, match, iconName) {
          const icon = document.createElement('i');
          icon.classList.add('fhml');
          icon.classList.add('fhml-tag-icon');
          iconName.split(' ').forEach(function (cssClass) {
            icon.classList.add(cssClass);
          });

          return icon.outerHTML;
        },
        '([a-z -]+)',
        true
    );

    const additionalTags: TagMetaType[] = [
      {
        //List tag
        tag: 'sup',
        tagConstructor: (component, match, contents) => {
          var ul = document.createElement('sup');
          ul.classList.add('fhml-sup');
          ul.innerHTML = contents;

          return ul.outerHTML;
        },
        noContent: false,
      },
      {
        //List tag
        tag: 'h1',
        tagConstructor: (component, match, contents) => {
          var ul = document.createElement('h1');
          ul.classList.add('fhml-h1');
          ul.innerHTML = contents;

          return ul.outerHTML;
        },
        noContent: false,
      },
      {
        //List tag
        tag: 'h2',
        tagConstructor: (component, match, contents) => {
          var ul = document.createElement('h2');
          ul.classList.add('fhml-h2');
          ul.innerHTML = contents;

          return ul.outerHTML;
        },
        noContent: false,
      },
      {
        //List tag
        tag: 'h3',
        tagConstructor: (component, match, contents) => {
          var ul = document.createElement('h3');
          ul.classList.add('fhml-h3');
          ul.innerHTML = contents;

          return ul.outerHTML;
        },
        noContent: false,
      },
      {
        //List tag
        tag: 'h4',
        tagConstructor: (component, match, contents) => {
          var ul = document.createElement('h4');
          ul.classList.add('fhml-h4');
          ul.innerHTML = contents;

          return ul.outerHTML;
        },
        noContent: false,
      },
      {
        //List tag
        tag: 'h5',
        tagConstructor: (component, match, contents) => {
          var ul = document.createElement('h5');
          ul.classList.add('fhml-h5');
          ul.innerHTML = contents;

          return ul.outerHTML;
        },
        noContent: false,
      },
      {
        //List tag
        tag: 'h6',
        tagConstructor: (component, match, contents) => {
          var ul = document.createElement('h6');
          ul.classList.add('fhml-h6');
          ul.innerHTML = contents;

          return ul.outerHTML;
        },
        noContent: false,
      },
      {
        //New line tag
        tag: 'br/',
        tagConstructor: (component, match, contents) => {
          var br = document.createElement('br');

          return br.outerHTML;
        },

        noContent: true,
      },

      {
        //Text with custom css class
        tag: 'className',
        tagConstructor: (component, match, classes: any, contents: any) => {
          var span = document.createElement('span');
          span.classList.add('fhml');
          classes.split(',').forEach((cssClass) => {
            span.classList.add(cssClass);
          });
          span.innerHTML = contents;

          return span.outerHTML;
        },

        attr: '([a-zA-Z0-9\-\, ]+)'
      },
      {
        //Text with custom css class
        tag: 'className',
        tagConstructor: (component, match, classes: any, contents: any) => {
          var span = document.createElement('span');
          span.classList.add('fhml');
          classes.split(',').forEach((cssClass) => {
            span.classList.add(cssClass);
          });
          span.innerHTML = contents;

          return span.outerHTML;
        },

        attr: '([a-zA-Z0-9\-\, ]+)'
      },

      {
        //text strikethrough
        tag: 'del',
        tagConstructor: (component, match, contents) => {
          var del = document.createElement('del');
          del.classList.add('fhml');
          del.innerHTML = contents;
          return del.outerHTML;
        },
        noContent: false,
      },

      {
        //text strikethrough
        tag: 's',
        tagConstructor: (component, match, contents) => {
          var del = document.createElement('del');
          del.classList.add('fhml');
          del.innerHTML = contents;

          return del.outerHTML;
        },

        noContent: false,
      },

      {
        //text highlight
        tag: 'mark',

        tagConstructor: (component, match, contents) => {
          var mark = document.createElement('mark');
          mark.classList.add('fhml');
          mark.innerHTML = contents;

          return mark.outerHTML;
        },

        noContent: false,
      },

      {
        //A section that is quoted from another source
        tag: 'blockquote',

        tagConstructor: (component, match, contents) => {
          var blockquote = document.createElement('blockquote');
          blockquote.classList.add('fhml');
          blockquote.classList.add('blockquote');

          blockquote.innerHTML = contents;

          return blockquote.outerHTML;
        },

        noContent: false,
      },

      {
        //A section that is quoted from another source
        tag: 'q',
        tagConstructor: (component, match, contents) => {
          var blockquote = document.createElement('blockquote');
          blockquote.classList.add('fhml');
          blockquote.classList.add('blockquote');

          blockquote.innerHTML = contents;

          return blockquote.outerHTML;
        },
        noContent: false,
      },

      {
        //Blocquote footer / footer
        tag: 'bqFooter',
        tagConstructor: (component, match, contents) => {
          var blockquote = document.createElement('footer');
          blockquote.classList.add('fhml');
          blockquote.classList.add('blockquote-footer');

          blockquote.innerHTML = contents;

          return blockquote.outerHTML;
        },
        noContent: false,
      },
      {
        //Blocquote footer / footer
        tag: 'small',
        tagConstructor: (component, match, contents) => {
          var small = document.createElement('small');
          small.classList.add('fhml');
          small.classList.add('fhml-small');
          small.innerHTML = contents;

          return small.outerHTML;
        },
        noContent: false,
      },

      {
        //List tag
        tag: 'ul',
        tagConstructor: (component, match, contents) => {
          var ul = document.createElement('ul');
          ul.classList.add('fhml');
          ul.innerHTML = contents;

          return ul.outerHTML;
        },
        noContent: false,
      },

      {
        //List element tag
        tag: 'li',
        tagConstructor: (component, match, contents) => {
          var li = document.createElement('li');
          li.classList.add('fhml');
          li.innerHTML = contents;
          return li.outerHTML;
        },
        noContent: false,
      },
      {
        //List element tag
        tag: 'counter',
        tagConstructor: (component, match, contents) => {
          var li = document.createElement('span');
          li.classList.add('badge');
          li.classList.add('badge-danger');
          if (contents <= 0) {
            li.classList.add('d-none');
          }
          li.innerHTML = contents;
          return li.outerHTML;
        },
        noContent: false,
      },
      {
        //text as computer code in a document
        tag: 'code',
        tagConstructor: (component, match, contents) => {
          var span = document.createElement('span');
          span.classList.add('fhml');
          span.classList.add('highlight');
          span.classList.add('fhml-code');
          span.classList.add('p-2');
          span.style.background = '#dcdce5';
          span.style.display = 'inline-block';
          span.innerHTML =
              '<pre class="mb-0"><code>' + contents + '</code></pre>';

          return span.outerHTML;
        },

        noContent: false,
      },
      {
        tag: 'portal',
        tagConstructor: (component, match, attr) => {
          console.warn('THIS PORTAL TAG IS DEPRECATED. PLEASE USE fhportal!');
          const attrList = attr.split(',');
          const idPart = attrList[0];
          const portalId = document.querySelectorAll(`[id^='${idPart}']`)[0].id;
          const portalElement = document.getElementById(portalId);
          const parentPortalElement = (portalElement.parentNode as HTMLElement);
          parentPortalElement.style.display = "none";
          const newElement = (portalElement.cloneNode(true) as HTMLElement);
          newElement.id = `${portalElement.id}_portal`;

          EVENTS_LIST.forEach(el => {
            newElement.setAttribute(`on${el}`, `document.getElementById("${portalElement.id}").${el}();`);
          })

          if (attrList.length > 1) {
            const stylesList: string[] = attrList[1].split(' ');
            stylesList.forEach(styleName => {
              newElement.classList.add(styleName);
            });
          }

          return newElement.outerHTML;
        },
        attr: '([a-zA-Z0-9\-\, ]+)',
        noContent: true
      },
      {
        tag: 'fhportal',
        tagConstructor: (component, match, attr) => {

          try {
            const parseAttr = (attr: string): {
              v?: string,
              layer?: string,
              wrapped?: string,
              id?: string,
              classes?: string,
              replaceParentId?: string,
              removeWrapper?: string,
              searchParent?: string,
              searchClosestLike?: string
            } => {
              console.log(attr);
              const res = {};
              const variables = attr.split(';');
              for (const variable of variables) {
                let [key, value] = variable.split('=')
                if (value[0] === '[' && value[value.length - 1] === ']') {
                  value = value.substr(1);
                  value = value.substr(0, value.length - 1);
                }
                if (['id', 'classes', 'wrapped', 'layer', 'v', 'searchClosestLike', 'searchParent', 'replaceParentId', 'removeWrapper'].indexOf(key) > -1) {
                  res[key] = value
                }
              }
              return res;
            }
            const meta = parseAttr(attr)
            const portal = document.createElement('fh-portal')
            portal.setAttribute('used', 'false');
            if (meta.id) {
              portal.setAttribute('objId', meta.id);
            } else {
              return '<div>NO fhportal ID!</div>'
            }
            if (meta.searchClosestLike) {
              portal.setAttribute('searchClosestLike', meta.searchClosestLike);
            }
            if (meta.searchParent) {
              portal.setAttribute('searchParent', meta.searchParent);
            }
            if (meta.classes) {
              portal.setAttribute('classes', meta.classes);
            }
            if (meta.wrapped) {
              portal.setAttribute('wrapped', meta.wrapped);
            }
            if (meta.layer) {
              portal.setAttribute('layer', meta.layer);
            }
            if (meta.v) {
              portal.setAttribute('v', meta.v);
            }
            if (meta.replaceParentId) {
              portal.setAttribute('replaceParentId', meta.replaceParentId)
            }
            if (meta.removeWrapper) {
              portal.setAttribute('removeWrapper', meta.removeWrapper)
            }
            component.hasPortal = true;
            return portal.outerHTML;
          } catch (e: any) {
            component.hasPortal = false;
            console.error(e.message);
            return '<div>Error portal</div>'
          }

        },
        attr: '(.+)',
        noContent: true
      },
      {
        tag: 'nbsp',
        tagConstructor: (component, match, attr) => {
          const repTime = Number(attr);
          if (!isNaN(repTime)) {
            var span = document.createElement('span');
            span.style.paddingLeft = `${repTime}em`;
            return span.outerHTML;
          }
          return '';
        },
        attr: '([a-zA-Z0-9\-\, ]+)',
        noContent: true
      },
      {
        tag: 'unescape',
        tagConstructor: (component, match, attr) => {
          const code = String(attr);
          const span = document.createElement('span');
          span.innerHTML += `&#${code};`;
          return span.outerHTML;
        },
        attr: '([a-zA-Z0-9\-\, ]+)',
        noContent: true
      },
      {
        tag: 'ahref',
        tagConstructor: (component, match, attr, contents) => {
          try {
            const parseAttr = (attr: string): { href?: string, alt?: string, target?: string } => {
              const res = {};
              const variables = attr.split(';');
              for (const variable of variables) {
                let [key, value] = variable.split('=')
                if (value[0] === '[' && value[value.length - 1] === ']') {
                  value = value.substr(1);
                  value = value.substr(0, value.length - 1);
                }
                if (['href', 'alt', 'target'].indexOf(key) > -1) {
                  res[key] = value
                }
              }
              return res;
            }

            const meta = parseAttr(attr)
            const aElement = document.createElement('a');

            if (meta.href) {
              aElement.href = meta.href;
            }
            if (meta.alt) {
              aElement.setAttribute('alt', meta.alt);
            }
            if (meta.target) {
              aElement.setAttribute('target', meta.target);
            }
            if (!!contents) {
              aElement.innerHTML = contents;
            }
            return aElement.outerHTML;
          } catch (e: any) {
            console.error(e.message);
            return ''
          }
        },
        attr: '(.+)',
        noContent: false
      },
      {
        tag: 'extAttributes/',
        tagConstructor: (component, match, contents) => {
          return '';
        },
        noContent: true
      },
    ];

    for (const tagMeta of additionalTags) {
      this.registerTag(
          tagMeta.tag,
          tagMeta.tagConstructor,
          tagMeta.attr,
          tagMeta.noContent
      );
    }
  }

  escapeHtml(string) {
    return String(string).replace(/[&<>"`]/g, (s) => {
      return this.escapedMap[s];
    });
  }

  // tslint:disable-next-line:no-unnecessary-initializer
  registerTag(name, fn, attributes = undefined, noContent = undefined) {
    let regexString = '';
    regexString += '\\[';
    regexString += name;

    if (typeof attributes === 'string') {
      regexString += "='" + attributes + "'";
    } else if (attributes && typeof attributes === 'object') {
      const keys = Object.keys(attributes);
      if (keys.length) {
        regexString += '( (';
        const attrRegex = [];
        Object.keys(attributes).forEach(function (key) {
          const value = attributes[key];
          attrRegex.push(key + "='" + value + "'");
        });
        regexString += attrRegex.join('|');
        regexString += '))+';
      }
    }
    regexString += '\\]';
    if (!noContent) {
      regexString += '(.*?)';
      regexString += '\\[\\/' + name + '\\]';
    }

    this.supportedTags.push({
      name: name,
      attributes: attributes,
      fn: fn,
      regex: new RegExp(regexString, 'ig'),
    });
  }

  needParse(string) {
    let regexString = '\\[(';
    regexString += this.supportedTags
        .map(function (tag) {
          return tag.name;
        })
        .join('|');
    regexString += ')[=a-zA-Z0-9 #,\.\(\)\'\-\_.*]*\\]';
    const res = (string || '').match(new RegExp(regexString));

    return res && typeof res === 'object' ? !!res.length : !!res;
  }

  // tslint:disable-next-line:no-unnecessary-initializer
  parse(source, skipHtmlEscape = undefined) {
    let parsed = source.replaceAll(/(\r\n|\n|\r)/gm, "");

    if (!skipHtmlEscape) {
      parsed = this.escapeHtml(parsed);
    }
    this.supportedTags.forEach((tag) => {
      parsed = parsed.replace(tag.regex, tag.fn.bind(this, this.component));
    });

    return parsed;
  }

  public transform(valueText: string) {
    valueText =
        valueText === undefined || valueText === null ? '' : valueText.toString();

    const unescapedQuotesText = valueText
        .replace(/"/g, "'")
        .replace(/\\'/g, "'");
    if (this.needParse(unescapedQuotesText)) {
      return this.sanitizer.bypassSecurityTrustHtml(
          this.parse(unescapedQuotesText, true)
      )
    } else {
      return this.sanitizer.sanitize(
          SecurityContext.HTML,
          this.sanitizer.bypassSecurityTrustHtml(valueText.replace(/\\'/g, "'"))
      );
    }
  }
}

export const EVENTS_LIST = ["Event", "abort", "afterprint", "animationend", "animationiteration", "animationstart", "beforeprint", "beforeunload", "blur", "canplay", "canplaythrough", "change", "click", "contextmenu", "copy", "cut", "dblclick", "drag", "dragend", "dragenter", "dragleave", "dragover", "dragstart", "drop", "durationchange", "ended", "error", "focus", "fullscreenchange", "fullscreenerror", "hashchange", "input", "invalid", "keydown", "keypress", "keyup", "load", "loadeddata", "loadedmetadata", "loadstart", "message", "offline", "online", "open", "pagehide", "pageshow", "paste", "pause", "play", "playing", "popstate", "progress", "ratechange", "resize", "reset", "scroll", "search", "seeked", "seeking", "select", "show", "stalled", "storage", "submit", "suspend", "timeupdate", "toggle", "touchcancel", "touchend", "touchmove", "touchstart", "unload", "volumechange", "waiting", "wheel", "altKey", "animationName", "bubbles", "button", "buttons", "cancelable", "charCode", "changeTouches", "clientX", "clientY", "clipboardData", "code", "composed", "ctrlKey", "currentTarget", "data", "dataTransfer", "defaultPrevented", "deltaX", "deltaY", "deltaZ", "deltaMode", "detail", "elapsedTime", "eventPhase", "inputType", "isComposing", "isTrusted", "key", "keyCode", "location", "lengthComputable", "loaded", "metaKey", "MovementX", "MovementY", "newValue", "newURL", "offsetX", "offsetY", "oldValue", "oldURL", "onemptied", "pageX", "pageY", "persisted", "propertyName", "pseudoElement", "region", "relatedTarget", "repeat", "screenX", "screenY", "shiftKey", "state", "storageArea", "target", "targetTouches", "timeStamp", "total", "touches", "type", "url", "which", "view"];

type TagMetaType = {
  tag: string;

  tagConstructor: (...args: any[]) => string;

  attr?: any;

  noContent?: boolean;
};

//TODO Portal functionality need to bę rewriet fo Angular
@Injectable({
  providedIn: 'root',
})
export class FhmlPortalManager {
  constructor() {
    // this.init();
  }

  private MAX_VERSION = 2;
  private MIN_VERSION = 1;
  public static OBSERVER_CONFIG = {attributes: true, childList: true, subtree: true};
  public static OBSERVERS = [];

  private VERSION_STORE = {
    '1': this.version_1,
    '2': this.version_2
  }

  // init() {
  //     // setTimeout( () => {
  //     //   this.handleMutation(null, null);
  //     // }, 500)
  //
  // }

  private version_1({sourceElement, portal, sourceId}) {
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

  private version_2({sourceElement, portal, sourceId}) {
    const replaceParentId = portal.getAttribute('replaceParentId')
    if (!!replaceParentId) {
      const oldParent = document.getElementsByClassName(replaceParentId)[0];
      if (!!oldParent) {
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
        FhmlPortalManager.OBSERVERS[sourceId] = undefined;
      }
    });
    observer.observe(portal.parentElement, FhmlPortalManager.OBSERVER_CONFIG);
    if (FhmlPortalManager.OBSERVERS[sourceId]) {
      FhmlPortalManager.OBSERVERS[sourceId].disconnect();
    }
    FhmlPortalManager.OBSERVERS[sourceId] = observer;
  }

  private processVersion(version: number, args: { [key: string]: any }) {
    const f = this.VERSION_STORE[`${version}`];
    console.log(version, args, f)
    if (f) {
      f(args)
    }
  }


  public handleMutation = () => {
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
          if (v !== undefined && !isNaN(Number.parseInt(v)) && Number.parseInt(v) >= this.MIN_VERSION && Number.parseInt(v) <= this.MAX_VERSION) {
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
          } else if (isRemoveWrapper) {
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
