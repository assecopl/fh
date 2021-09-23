import {injectable} from "inversify";

@injectable()
class FHML {
    private supportedTags: any[];
    private escapedMap: { "&": string; "<": string; ">": string; "\"": string; "`": string };


    constructor() {
        this.supportedTags = [];
        this.escapedMap = { // not escaping ' = / as they are FHML input and output characters
            '&': '&amp;',
            '<': '&lt;',
            '>': '&gt;',
            '"': '&quot;',
            '`': '&#x60;',
        };

        this.registerTag('b', function (match, contents) {
            var span = document.createElement('span');
            span.classList.add('fhml');
            span.classList.add('fhml-tag-b');

            span.innerHTML = contents;

            return span.outerHTML;
        });
        this.registerTag('i', function (match, contents) {
            var span = document.createElement('span');
            span.classList.add('fhml');
            span.classList.add('fhml-tag-i');

            span.innerHTML = contents;

            return span.outerHTML;
        });
        this.registerTag('u', function (match, contents) {
            var span = document.createElement('span');
            span.classList.add('fhml');
            span.classList.add('fhml-tag-u');

            span.innerHTML = contents;

            return span.outerHTML;
        });
        this.registerTag('lt', function (match, contents) {
            var span = document.createElement('span');
            span.classList.add('fhml');
            span.classList.add('fhml-tag-lt');

            span.innerHTML = contents;

            return span.outerHTML;
        });
        this.registerTag('size', function (match, size, contents) {
            var span = document.createElement('span');
            span.classList.add('fhml');
            span.classList.add('fhml-tag-size');
            span.style.fontSize = size + 'px';

            span.innerHTML = contents;

            return span.outerHTML;
        }, '([0-9]+)');
        this.registerTag('color', function (match, color, contents) {
            var span = document.createElement('span');
            span.classList.add('fhml');
            span.classList.add('fhml-tag-color');
            span.style.color = color;

            span.innerHTML = contents;

            return span.outerHTML;
        }, '(#?[a-z0-9]+)');
        this.registerTag('icon', function (match, iconName) {
            var icon = document.createElement('i');
            icon.classList.add('fhml');
            icon.classList.add('fhml-tag-icon');
            iconName.split(' ').forEach(function (cssClass) {
                icon.classList.add(cssClass);
            });

            return icon.outerHTML;
        }, '([a-z \-]+)', true);


        const additionalTags: TagMetaType[] = [

            {
                //New line tag
                tag: 'br/',
                tagConstructor: (match, contents) => {

                    var br = document.createElement('br');

                    return br.outerHTML;

                },

                noContent: true

            },

            {
                //Text with custom css class
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
                //text strikethrough
                tag: 'del',

                tagConstructor: (match, contents) => {

                    var del = document.createElement('del');
                    del.classList.add('fhml');
                    del.innerHTML = contents;

                    return del.outerHTML;

                },
                noContent: false
            },

            {
                //text strikethrough
                tag: 's',
                tagConstructor: (match, contents) => {

                    var del = document.createElement('del');
                    del.classList.add('fhml');
                    del.innerHTML = contents;

                    return del.outerHTML;

                },

                noContent: false

            },

            {
                //text highlight
                tag: 'mark',

                tagConstructor: (match, contents) => {

                    var mark = document.createElement('mark');
                    mark.classList.add('fhml');
                    mark.innerHTML = contents;

                    return mark.outerHTML;

                },

                noContent: false

            },

            {
                //A section that is quoted from another source
                tag: 'blockquote',

                tagConstructor: (match, contents) => {

                    var blockquote = document.createElement('blockquote');
                    blockquote.classList.add('fhml');
                    blockquote.classList.add('blockquote')

                    blockquote.innerHTML = contents;

                    return blockquote.outerHTML;

                },

                noContent: false

            },

            {
                //A section that is quoted from another source
                tag: 'q',
                tagConstructor: (match, contents) => {

                    var blockquote = document.createElement('blockquote');
                    blockquote.classList.add('fhml');
                    blockquote.classList.add('blockquote')

                    blockquote.innerHTML = contents;

                    return blockquote.outerHTML;

                },
                noContent: false
            },

            {
                //Blocquote footer / footer
                tag: 'bqFooter',
                tagConstructor: (match, contents) => {

                    var blockquote = document.createElement('footer');
                    blockquote.classList.add('fhml');
                    blockquote.classList.add('blockquote-footer')

                    blockquote.innerHTML = contents;

                    return blockquote.outerHTML;

                },
                noContent: false

            },

            {
                //List tag
                tag: 'ul',
                tagConstructor: (match, contents) => {

                    var ul = document.createElement('ul');
                    ul.classList.add('fhml');
                    ul.innerHTML = contents;

                    return ul.outerHTML;
                },
                noContent: false
            },

            {
                //List element tag
                tag: 'li',
                tagConstructor: (match, contents) => {
                    var li = document.createElement('li');
                    li.classList.add('fhml');
                    li.innerHTML = contents;
                    return li.outerHTML;
                },
                noContent: false

            },
            {
                //List element tag
                tag: 'counter',
                tagConstructor: (match, contents) => {
                    var li = document.createElement('span');
                    li.classList.add('badge');
                    li.classList.add('badge-danger');
                    if (contents <= 0) {
                        li.classList.add('d-none');
                    }
                    li.innerHTML = contents;
                    return li.outerHTML;
                },
                noContent: false
            },
            {
                //text as computer code in a document
                tag: 'code',
                tagConstructor: (match, contents) => {

                    var span = document.createElement('span');
                    span.classList.add('fhml');
                    span.classList.add('highlight');
                    span.classList.add('fhml-code');
                    span.classList.add('p-2');
                    span.style.background = '#dcdce5';
                    span.style.display = 'inline-block'
                    span.innerHTML = '<pre class="mb-0"><code>' + contents + '</code></pre>';

                    return span.outerHTML;

                },

                noContent: false

            }

        ]

        for (const tagMeta of additionalTags) {
            this.registerTag(tagMeta.tag, tagMeta.tagConstructor, tagMeta.attr, tagMeta.noContent);
        }


    }


    escapeHtml(string) {
        return String(string).replace(/[&<>"`]/g, function (s) {
            return this.escapedMap[s];
        }.bind(this));
    };

    registerTag(name, fn, attributes = undefined, noContent = undefined) {
        var regexString = '';
        regexString += '\\[';
        regexString += name;

        if (typeof attributes === 'string') {
            regexString += "='" + attributes + "'";
        } else if (attributes && typeof attributes === 'object') {
            var keys = Object.keys(attributes);
            if (keys.length) {
                regexString += '( (';
                var attrRegex = [];
                Object.keys(attributes).forEach(function (key) {
                    var value = attributes[key];
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
            regex: new RegExp(regexString, 'ig')
        });
    };

    needParse(string) {
        var regexString = '\\[(';
        regexString += this.supportedTags.map(function (tag) {
            return tag.name;
        }).join('|');
        regexString += ')[=a-zA-Z0-9 #,\.\(\)\'\-]*\\]';
        var res = (string || '').match(new RegExp(regexString));

        return (res && typeof res === 'object') ? !!res.length : !!res;
    };

    parse(source, skipHtmlEscape = undefined) {
        var parsed = source;

        if (!skipHtmlEscape) {
            parsed = this.escapeHtml(parsed);
        }
        this.supportedTags.forEach(function (tag) {
            parsed = parsed.replace(tag.regex, tag.fn);
        });

        return parsed;
    };

    resolveValueTextOrEmpty(valueText) {
        valueText = valueText || '';
        var unescapedQuotesText = valueText.replace(/&#39;/g, "'");
        if (this.needParse(unescapedQuotesText)) {
            return this.parse(unescapedQuotesText);
        } else {
            return this.escapeHtml(valueText);
        }
    };

    public removeHtmlTags(text: string) {
        return text.replace(/<\/?[^>]+(>|$)/g, "").trim();
    };

}

export {FHML};


export type TagMetaType = {

    tag: string,

    tagConstructor: (...args: any[]) => string,

    attr?: any,

    noContent?: boolean

};
