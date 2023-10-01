import {
    ComponentRef,
    Directive,
    DoCheck, EmbeddedViewRef, Injector,
    Input, isDevMode, IterableChangeRecord, IterableChanges, IterableDiffer,
    IterableDiffers,
    NgIterable,
    TemplateRef,
    TrackByFunction,
    ViewContainerRef, ViewRef
} from '@angular/core';
import {GlobalErrorHandler} from "../../../ng-default-app/src/utils/GlobalErrorHandler";

@Directive({
    selector: '[iterator][iteratorOf]'
})
export class EwopIteratorDirective<T> implements DoCheck {


    /**
     * The value of the iterable expression, which can be used as a
     * [template input variable](guide/structural-directives#template-input-variable).
     */
    @Input()
    set iteratorOf(iteratorOf: NgIterable<T>) {
        this._repeaterDirective = iteratorOf;
        this._repeaterDirectiveDirty = true;
    }


    /**
     * A function that defines how to track changes for items in the iterable.
     *
     * When items are added, moved, or removed in the iterable,
     * the directive must re-render the appropriate DOM nodes.
     * To minimize churn in the DOM, only nodes that have changed
     * are re-rendered.
     *
     * By default, the change detector assumes that
     * the object instance identifies the node in the iterable.
     * When this function is supplied, the directive uses
     * the result of calling this function to identify the item node,
     * rather than the identity of the object itself.
     *
     * The function receives two inputs,
     * the iteration index and the node object ID.
     */
    @Input()
    set iteratorTrackBy(fn: TrackByFunction<T>) {
        if (isDevMode() && fn != null && typeof fn !== 'function') {
            // TODO(vicb): use a log service once there is a public one available
            if (<any>console && <any>console.warn) {
                console.warn(
                    `trackBy must be a function, but received ${JSON.stringify(fn)}. ` +
                    `See https://angular.io/docs/ts/latest/api/common/index/NgFor-directive.html#!#change-propagation for more information.`);
            }
        }
        this._trackByFn = fn;
    }

    get iteratorTrackBy(): TrackByFunction<T> {
        return this._trackByFn;
    }

    private _repeaterDirective !: NgIterable<T>;
    private _repeaterDirectiveDirty: boolean = true;
    private _differ: IterableDiffer<T> | null = null;
    // TODO(issue/24571): remove '!'.
    private _trackByFn !: TrackByFunction<T>;

    constructor(
        private _viewContainer: ViewContainerRef,
        private _template: TemplateRef<RepeaterContext<T>>, private _differs: IterableDiffers, private i: Injector) {
    }

    /**
     * A reference to the template that is stamped out for each item in the iterable.
     * @see [template reference variable](guide/template-syntax#template-reference-variables--var-)
     */
    @Input()
    set iteratorTemplate(value: TemplateRef<RepeaterContext<T>>) {
        if (value) {
            this._template = value;
        }
    }

    /**
     * Applies the changes when needed.
     */
    ngDoCheck(): void {
        if (this._repeaterDirectiveDirty) {
            this._repeaterDirectiveDirty = false;
            // React on repeaterDirective changes only once all inputs have been initialized
            const value = this._repeaterDirective;
            if (!this._differ && value) {
                try {
                    this._differ = this._differs.find(value).create(this.iteratorTrackBy);
                } catch {
                    GlobalErrorHandler.throwError(new Error(
                        `Cannot find a differ supporting object '${value}' of type '${getTypeName(value)}'. NgFor only supports binding to Iterables such as Arrays.`));
                }
            }
        }
        if (this._differ) {
            const changes = this._differ.diff(this._repeaterDirective);
            if (changes) this._applyChanges(changes);
        }
    }

    private _applyChanges(changes: IterableChanges<T>) {
        const insertTuples: RecordViewTuple<T>[] = [];
        changes.forEachOperation(
            (item: IterableChangeRecord<any>, adjustedPreviousIndex: number | null,
             currentIndex: number | null) => {

                if (item.previousIndex == null) {
                    const view = this._viewContainer.createEmbeddedView(
                        this._template, new RepeaterContext<T>(null !, this._repeaterDirective !, -1, -1),
                        currentIndex === null ? undefined : currentIndex);
                    const tuple = new RecordViewTuple<T>(item, view);
                    insertTuples.push(tuple);
                } else if (currentIndex == null) {
                    this._viewContainer.remove(
                        adjustedPreviousIndex === null ? undefined : adjustedPreviousIndex);
                } else if (adjustedPreviousIndex !== null) {
                    const view = this._viewContainer.get(adjustedPreviousIndex) !;
                    this._viewContainer.move(view, currentIndex);
                    const tuple = new RecordViewTuple(item, <EmbeddedViewRef<RepeaterContext<T>>>view);
                    insertTuples.push(tuple);
                }
            });

        for (let i = 0; i < insertTuples.length; i++) {
            this._perViewChange(insertTuples[i].view, insertTuples[i].record);
        }

        for (let i = 0, ilen = this._viewContainer.length; i < ilen; i++) {
            const viewRef = <EmbeddedViewRef<RepeaterContext<T>>>this._viewContainer.get(i);
            viewRef.context.index = i;
            viewRef.context.count = ilen;
            viewRef.context.repeaterDirective = this._repeaterDirective !;
        }

        changes.forEachIdentityChange((record: any) => {
            const viewRef =
                <EmbeddedViewRef<RepeaterContext<T>>>this._viewContainer.get(record.currentIndex);
            viewRef.context.$implicit = record.item;
        });
    }

    private _perViewChange(
        view: EmbeddedViewRef<RepeaterContext<T>>, record: IterableChangeRecord<any>) {
        view.context.$implicit = record.item;
    }

    /**
     * Asserts the correct type of the context for the template that `repeaterDirective` will render.
     *
     * The presence of this method is a signal to the Ivy template type-check compiler that the
     * `repeaterDirective` structural directive renders its template with a specific context type.
     */
    static ngTemplateContextGuard<T>(dir: EwopIteratorDirective<T>, ctx: any):
        ctx is RepeaterContext<T> {
        return true;
    }

}

class RecordViewTuple<T> {
    constructor(public record: any, public view: EmbeddedViewRef<RepeaterContext<T>>) {
    }
}

function getTypeName(type: any): string {
    return type['name'] || typeof type;
}

export class RepeaterContext<T> {
    constructor(public $implicit: T, public repeaterDirective: NgIterable<T>, public index: number, public count: number) {
    }

    get first(): boolean {
        return this.index === 0;
    }

    get last(): boolean {
        return this.index === this.count - 1;
    }

    get even(): boolean {
        return this.index % 2 === 0;
    }

    get odd(): boolean {
        return !this.even;
    }
}
