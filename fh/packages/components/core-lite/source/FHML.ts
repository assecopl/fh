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
}

export {FHML};