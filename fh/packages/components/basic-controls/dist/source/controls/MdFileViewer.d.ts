import { HTMLFormComponent } from "fh-forms-handler";
declare class MdFileViewer extends HTMLFormComponent {
    private mapElement;
    private mapId;
    private source;
    private resourceBasePath;
    private marked;
    constructor(componentObj: any, parent: HTMLFormComponent);
    create(): void;
    /**
     * Function that loads rexternal md files an parse it to HTML.
     * @param relativeUrl (relative url of md file)
     * @param resourceBasePath (path in resources url of md file)
     */
    loadMdFile(relativeUrl: string, resourceBasePath?: string): void;
    /**
     * Handle logic of inside links.
     * If href attribute has .md extensions it will be open inside this container.
     */
    addHrefHandler(): void;
    display(): void;
    update(change: any): void;
    wrap(skipLabel: any): void;
    /**
     * Create renderer that extends default behaviour of  "marked" renderer.
     * Ads Bootstrap 4 and Highlight.js
     *
     */
    private markedStyleHandler;
}
export { MdFileViewer };
