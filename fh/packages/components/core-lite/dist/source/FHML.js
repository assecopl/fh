"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
Object.defineProperty(exports, "__esModule", { value: true });
var inversify_1 = require("inversify");
var FHML = /** @class */ (function () {
    function FHML() {
        this.supportedTags = [];
        this.escapedMap = {
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
    FHML.prototype.escapeHtml = function (string) {
        return String(string).replace(/[&<>"`]/g, function (s) {
            return this.escapedMap[s];
        }.bind(this));
    };
    ;
    FHML.prototype.registerTag = function (name, fn, attributes, noContent) {
        if (attributes === void 0) { attributes = undefined; }
        if (noContent === void 0) { noContent = undefined; }
        var regexString = '';
        regexString += '\\[';
        regexString += name;
        if (typeof attributes === 'string') {
            regexString += "='" + attributes + "'";
        }
        else if (attributes && typeof attributes === 'object') {
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
    ;
    FHML.prototype.needParse = function (string) {
        var regexString = '\\[(';
        regexString += this.supportedTags.map(function (tag) {
            return tag.name;
        }).join('|');
        regexString += ')[=a-zA-Z0-9 #,\.\(\)\'\-]*\\]';
        var res = (string || '').match(new RegExp(regexString));
        return (res && typeof res === 'object') ? !!res.length : !!res;
    };
    ;
    FHML.prototype.parse = function (source, skipHtmlEscape) {
        if (skipHtmlEscape === void 0) { skipHtmlEscape = undefined; }
        var parsed = source;
        if (!skipHtmlEscape) {
            parsed = this.escapeHtml(parsed);
        }
        this.supportedTags.forEach(function (tag) {
            parsed = parsed.replace(tag.regex, tag.fn);
        });
        return parsed;
    };
    ;
    FHML.prototype.resolveValueTextOrEmpty = function (valueText) {
        valueText = valueText || '';
        var unescapedQuotesText = valueText.replace(/&#39;/g, "'");
        if (this.needParse(unescapedQuotesText)) {
            return this.parse(unescapedQuotesText);
        }
        else {
            return this.escapeHtml(valueText);
        }
    };
    ;
    FHML = __decorate([
        inversify_1.injectable(),
        __metadata("design:paramtypes", [])
    ], FHML);
    return FHML;
}());
exports.FHML = FHML;
//# sourceMappingURL=FHML.js.map