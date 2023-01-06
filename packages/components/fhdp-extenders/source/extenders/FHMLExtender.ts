import {FhContainer, FHML,} from "fh-forms-handler";
import {EVENTS_LIST} from "../constants";
import {Portal} from './PortalProcessHandler';

type TagMetaType = {
    tag: string,
    tagConstructor: (...args:any[]) => string,
    attr?: any,
    noContent?: boolean
};

const meta: TagMetaType[] = [
    {
        tag: 'br/',
        tagConstructor: (match, contents) => {
            var br = document.createElement('br');
            return br.outerHTML;
        },
        noContent: true
    },
    {
        tag: 'hr/',
        tagConstructor: (match, contents) => {
            var hr = document.createElement('hr');
            return hr.outerHTML;
        },
        noContent: true
    },
    {
        tag: 'className',
        tagConstructor: (match, classes, contents) => {
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
        tag: 'del',
        tagConstructor: (match, contents) => {
            var del = document.createElement('del');
            del.innerHTML = contents;
            return del.outerHTML;
        },
        noContent: false
    },
    {
         tag: 's',
         tagConstructor: (match, contents) => {
             var del = document.createElement('del');
             del.innerHTML = contents;
             return del.outerHTML;
         },
         noContent: false
    },
    {
         tag: 'mark',
         tagConstructor: (match, contents) => {
             var mark = document.createElement('mark');
             mark.innerHTML = contents;
             return mark.outerHTML;
         },
         noContent: false
    },
    {
         tag: 'blockquote',
         tagConstructor: (match, contents) => {
             var blockquote = document.createElement('blockquote');
             blockquote.classList.add('blockquote')
             blockquote.innerHTML = contents;
             return blockquote.outerHTML;
         },
         noContent: false
    },
    {
        tag: 'q',
        tagConstructor: (match, contents) => {
            var blockquote = document.createElement('blockquote');
            blockquote.classList.add('blockquote')
            blockquote.innerHTML = contents;
            return blockquote.outerHTML;
        },
        noContent: false
    },
    {
        tag: 'bqFooter',
        tagConstructor: (match, contents) => {
            var blockquote = document.createElement('footer');
            blockquote.classList.add('blockquote-footer')
            blockquote.innerHTML = contents;
            return blockquote.outerHTML;
        },
        noContent: false
    },
    {
        tag: 'ul',
        tagConstructor: (match, contents) => {
            var ul = document.createElement('ul');
            ul.innerHTML = contents;
            return ul.outerHTML;
        },
        noContent: false
    },
    {
        tag: 'li',
        tagConstructor: (match, contents) => {
            var li = document.createElement('li');
            li.innerHTML = contents;
            return li.outerHTML;
        },
        noContent: false
    },
    {
        tag: 'code',
        tagConstructor: (match, contents) => {
            const fhml = FhContainer.get<FHML>('FHML');
            var span = document.createElement('span');
            span.classList.add('highlight');
            span.style.background = '#dcdce5';
            span.style.padding = '5px';
            span.style.display = 'inline-block'
            span.innerHTML = '<pre><code>' + contents + '</code></pre>';
            return span.outerHTML;
        },
        noContent: false
    },
    {
        tag: 'portal',
        tagConstructor: (match, attr) => {
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

            if(attrList.length > 1) {
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
        tagConstructor: (match, attr) => {
            new Portal();
            try {
                const parseAttr = (attr: string): {v?:string, layer?: string, wrapped?: string, id? : string, classes? : string, replaceParentId?: string, removeWrapper?: string, searchParent?: string, searchClosestLike?: string} => {
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
                if(meta.replaceParentId) {
                    portal.setAttribute('replaceParentId', meta.replaceParentId)
                }
                if(meta.removeWrapper) {
                    portal.setAttribute('removeWrapper', meta.removeWrapper)
                }
                return portal.outerHTML;
            } catch (e) {
                console.error(e.message);
                return '<div>Error portal</div>'
            }
        },
        attr: '(.+)',
        noContent: true
    },
    {
        tag: 'nbsp',
        tagConstructor: (match, attr) => {
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
        tagConstructor: (match, attr) => {
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
        tagConstructor: (match, attr, contents) => {
            try {
                const parseAttr = (attr: string): {href?:string, alt?: string, target?: string} => {
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
            } catch (e) {
                console.error(e.message);
                return ''
            }
        },
        attr: '(.+)',
        noContent: false
    },
    {
        tag: 'extAttributes/',
        tagConstructor: (match, contents) => {
            return '';
        },
        noContent: true
    },
]

function registerTags(tagsMeta: TagMetaType[]) {
    const fhml = FhContainer.get<FHML>('FHML');
    const oldParse = fhml.needParse.bind(fhml);
    fhml.needParse = function(str) {
        var regexString = '\\[(';
        regexString += meta.map(function (tag) {
            return tag.tag;
        }).join('|');
        regexString += ')[=a-zA-Z0-9 #,\.\(\)\'\-\_.*]*\\]';
        var res = (str || '').match(new RegExp(regexString));

        return (res && typeof res === 'object') ? !!res.length : oldParse(str);
    };
    for (const tagMeta of tagsMeta) {
        fhml.registerTag(tagMeta.tag, tagMeta.tagConstructor, tagMeta.attr, tagMeta.noContent);
    }
}

export const extendFHML = (): void => {
    registerTags(meta)
}