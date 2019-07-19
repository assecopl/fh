import {HTMLFormComponent} from "fh-forms-handler";
import * as marked from "marked";
import {Renderer} from "marked";
import * as highlightjs from "highlightjs";

class MdFileViewer extends HTMLFormComponent {
    private mapElement: any;
    private mapId: string;
    private source: any;
    private resourceBasePath:string = null;
    private marked:any = null;

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);

        this.mapId = '';
        this.source = this.componentObj.src;
        this.resourceBasePath = this.componentObj.resourceBasePath;

        this.mapElement = null;

    }

    create() {
        var viewer = document.createElement('div');
        viewer.id = this.id;
        viewer.classList.add("fc");
        viewer.classList.add("md-file-viewer");



        this.loadMdFile(this.source);

        this.component = viewer;
        this.hintElement = this.component;
        // if (this.width) {
            this.wrap(false);
        // } else {
        //     this.htmlElement = this.component;
        //     this.contentWrapper = this.htmlElement;
        // }
        this.addStyles();

        this.display();
    };

    /**
     * Function that loads rexternal md files an parse it to HTML.
     * @param relativeUrl (relative url of md file)
     * @param resourceBasePath (path in resources url of md file)
     */
    public loadMdFile(relativeUrl:string, resourceBasePath:string = null){
        if(resourceBasePath && !relativeUrl.includes(resourceBasePath)){
            relativeUrl = resourceBasePath + relativeUrl;
        }
        $.get(this.util.getPath(relativeUrl), function(data:string){
            this.component.innerHTML = marked(data, {renderer: this.markedStyleHandler()});
            this.addHrefHandler();
        }.bind(this)).catch()
    }

    /**
     * Handle logic of inside links.
     * If href attribute has .md extensions it will be open inside this container.
     */
    public addHrefHandler(){
        const collection:HTMLCollection | any = this.component.getElementsByTagName("a");

        $.each(collection, function (key, elem) {
            let href:string = elem.getAttribute("href");
            if(href.includes(".md")) {
                elem.addEventListener('click', function (event:Event){
                    event.stopPropagation();
                    event.preventDefault();
                    if(this.util.isUrlRelative(href)){
                        this.loadMdFile(href, this.resourceBasePath);
                    } else {
                        this.loadMdFile(href);
                    }
            }.bind(this))

            } else {
                elem.attr('target','_blank');
            }
        }.bind(this));
    }


    display() {
        super.display();
        if (this.mapElement) {
            this.htmlElement.appendChild(this.mapElement);
        }
    };

    update(change) {
        super.update(change);

        if (change.changedAttributes) {
            $.each(change.changedAttributes, function (name, newValue) {
                switch (name) {
                    case 'src':
                        this.src = newValue;
                        this.component.src = this.src;
                        break;
                }
            }.bind(this));
        }
    };

    wrap(skipLabel) {
        super.wrap(skipLabel);
    };

    /**
     * Create renderer that extends default behaviour of  "marked" renderer.
     * Ads Bootstrap 4 and Highlight.js
     *
     */
    private markedStyleHandler():Renderer{
        const renderer = new marked.Renderer()


        renderer.heading = (text, level) => {
            return '<h' + level + ' class="md-inpage-anchor">'
                +    text
                + '</h' + level + '>'
        }

        renderer.code = (code, language) => {

            var valid = !!(language && highlightjs.getLanguage(language))
            var highlighted = valid ? highlightjs.highlight(language, code).value : code

            return '<pre><code class="hljs lang-' + language + '">'
                + highlighted
                + '</code></pre>'
        };

        renderer.table = (header, body) => {
            return '<table class="table table-bordered table-striped" >'
                +     '<thead class="thead-default">'
                +         header
                +     '</thead>'
                +     '<tbody>'
                +         body
                +     '</tbody>'
                + '</table>'
        };

        renderer.blockquote = (quote:string) => {
            return '<blockquote class="blockquote">'+ quote + '</blockquote>';
        };

        return renderer
    }
}

export {MdFileViewer};