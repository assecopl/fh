import {TableColumnComponent} from '../table-column/table-column.component';
import {FhngHTMLElementC} from '../../models/componentClasses/FhngHTMLElementC';
import {TableRowComponent} from '../table-row/table-row.component';
import {Directive, EventEmitter, Input} from '@angular/core';
import {Page} from "../../Base";

/**
 * FIXME Przebudować Tabelę!!!!! Między innymi tak aby nie korzystać z ViewCHild i ContentCHild - działają z opuźnieniem.
 */

@Directive()
export abstract class TableComponentRef extends FhngHTMLElementC {
  abstract rowsArray: TableRowComponent[];
  // public selected: any;
  // public selectedChange: EventEmitter<any>;
  public collection: Page<any> | Array<any>;
  public selectionCheckboxes: boolean;
  public selectAllChceckbox: boolean;
  public multiselect: boolean;
  public selectable: boolean;

  /**
   * TODO Zastanowic sie jak zalatwic fixed header w tabeli. Opcje (pozycja sticky, transform-tranlsate na tr/th, kopia nagłówka z pozycja aboslutna)
   */
  @Input()
  public fixedHeader: boolean = false;

  abstract select(event, row: any);

  abstract onRowClickEvent(event, mainId, silent?)

  abstract toggleSelectAll(event);

  abstract registerColumn(
    column: TableColumnComponent,
    row: TableRowComponent | any
  ): void;
}
