import {TableColumnComponent} from '../table-column/table-column.component';
import {FhngHTMLElementC} from '../../models/componentClasses/FhngHTMLElementC';
import {TableRowComponent} from '../table-row/table-row.component';
import {Directive, EventEmitter} from '@angular/core';
import {Page} from '@fhng/ng-core';

/**
 * FIXME Przebudować Tabelę!!!!! Między innymi tak aby nie korzystać z ViewCHild i ContentCHild - działają z opuźnieniem.
 */

@Directive()
export abstract class TableComponentRef extends FhngHTMLElementC {
  abstract rowsArray: TableRowComponent[];
  public selected: any;
  public selectedChange: EventEmitter<any>;
  public collection: Page<any> | Array<any>;
  public selectionCheckboxes: boolean;
  public selectAllChceckbox: boolean;
  public multiselect: boolean;
  public selectable: boolean;

  abstract select(row: any);

  abstract toggleSelectAll();

  abstract registerColumn(
    column: TableColumnComponent,
    row: TableRowComponent | any
  ): void;
}
