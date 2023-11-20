import {
  Component,
  EventEmitter,
  forwardRef,
  Injector,
  Input, OnDestroy,
  OnInit,
  Optional,
  Output,
  SimpleChanges,
  SkipSelf,
} from '@angular/core';
import {FhngReactiveInputC} from '../../models/componentClasses/FhngReactiveInputC';
import {BootstrapWidthEnum} from './../../models/enums/BootstrapWidthEnum';
import {FhngComponent} from '../../models/componentClasses/FhngComponent';
import {IDataAttributes} from "../../models/interfaces/IDataAttributes";
import {BootstrapStyleType} from "../../models/CommonTypes";
import {BootstrapStyleEnum} from "../../models/enums/BootstrapStyleEnum";
import {HttpErrorResponse, HttpEvent, HttpEventType} from "@angular/common/http";
import {catchError, last, map, takeUntil} from "rxjs/operators";
import {lastValueFrom, Subject, throwError} from "rxjs";
import {PresentationStyleEnum} from "../../models/enums/PresentationStyleEnum";
import {NotificationService} from "../../service/Notification";
import {I18nService} from "../../service/i18n.service";

type UpdateOn =  'change' | 'blur' | 'submit';

export interface IFileUploadDataAttributes extends IDataAttributes {
  onUpload?: string;
  multiple: boolean;
  language?: string;
  maxSize?: number;
  extensions?: string;
  style?: BootstrapStyleType,
  fileNames?: string[];
  presentationStyle?: PresentationStyleEnum
}

@Component({
  selector: 'fhng-file-upload',
  templateUrl: './file-upload.component.html',
  styleUrls: ['./file-upload.component.scss'],
  providers: [
    {
      provide: FhngComponent,
      useExisting: forwardRef(() => FileUploadComponent),
    }
  ]
})
export class FileUploadComponent extends FhngReactiveInputC implements OnInit, OnDestroy {
  public override width = BootstrapWidthEnum.MD3;

  public override mb2 = false;

  @Input()
  public override updateOn: UpdateOn = 'change'; //Domyślnie upload plików musi aktualizować/odpisywać się od razu.

  @Input()
  public maxSize: number = 1000000; //KB

  @Input()
  public extensions: string = null;

  @Input()
  public multiple: boolean = false;

  @Output()
  public upload: EventEmitter<File> = new EventEmitter<File>();

  @Input()
  public onUpload: string;

  @Input()
  public language: string;

  @Input()
  public style: BootstrapStyleType = BootstrapStyleEnum.PRIMARY;

  @Input()
  public file: File | File[] | null = null;

  @Input()
  public fileNames: string[] = [];

  public progress: number = 0;

  public showProgressBar: boolean = false;

  public dragover: boolean = false;

  private _destroyRequest$ = new Subject<void>();

  constructor(
      public override injector: Injector,
      @Optional() @SkipSelf() parentFhngComponent: FhngComponent,
      private _notification: NotificationService,
      private _i18n: I18nService
  ) {
    super(injector, parentFhngComponent);
  }

  public override ngOnInit(): void {
    super.ngOnInit();
  }

  public override ngOnChanges(changes: SimpleChanges): void {
    super.ngOnChanges(changes);
  }

  public override ngOnDestroy() {
    super.ngOnDestroy();

    this._destroyRequest$.next();
    this._destroyRequest$.complete();
  }


  public override mapAttributes(data: IFileUploadDataAttributes): void {
    super.mapAttributes(data);

    this.extensions = data.extensions || this.extensions;
    this.onUpload = data.onUpload || this.onUpload;
    this.multiple = data.multiple || this.multiple;
    this.language = data.language || this.language;
    this.maxSize = data.maxSize || this.maxSize;
    this.style = data.style || this.style || BootstrapStyleEnum.PRIMARY;
    this.fileNames = data.fileNames || this.fileNames || [];
  }

  public override extractChangedAttributes() {
    let attrs = {};
    if (this.valueChanged) {
      attrs['fileIds'] = this.rawValue;
      this.valueChanged = false;
    }

    return attrs;
  };

  public override handleError (error: HttpErrorResponse) {
    console.log('handleError', error);
    let msg = this._i18n.__('fh.upload.message.upload_error'),
        level: 'error' | 'warning' = 'error';

    if (error.status === 409) {
      msg = this._i18n.__('fh.upload.message.max_file_exceeded');
    } else if (error.status === 400) {
      msg = this._i18n.__('fh.upload.message.incorrect_file_extension', [this.extensions]);
    } else if (error.status === -1) {
      level = 'warning';
      msg = this._i18n.__('fh.upload.message.upload_aborted');
    }

    if (level === 'warning') {
      this._notification.showWarning(msg,null,10000);
    } else {
      this._notification.showError(msg,null,10000);
    }

    return throwError(() => new Error(msg));
  }

  public onAddedFiles($event: Event & {target?: {files?: FileList}}): void {
    $event.preventDefault();

    let file: File | File[] | null =
        $event && $event.target.files && $event.target.files.item(0);
    let formData: FormData = null;

    if (this.multiple && $event &&  $event.target.files) {
      file = Array.from($event.target.files);
    }

    if (
        this._requiredFilesType(file, this.getExtensions()) &&
        this._filesSizeValidator(file)
    ) {
      this.file = file;
      formData = this._createFormDataObject($event.target.files);
    } else {
      this.file = null;
    }

    this.valueChanged = true;

    let request = this.fireHttpMultiPartEvent(
      'onUpload',
      this.onUpload,
      '/fhdp-demo-app/fileUpload',
      formData
    ).pipe(
        map(event => this._getEventMessage(event, formData)),
        takeUntil(this._destroyRequest$),
        last()
    );

    lastValueFrom(request).then((response: any) => {
      if (response.type === HttpEventType.UploadProgress) {
        this.showProgressBar = false;
        this.handleError({status: -1} as HttpErrorResponse);
      }

      if (response.body) {
        this.valueChanged = true;
        this.rawValue = response.body.ids;
        this.value = this.file = null;
        this.fireEventWithLock('onUpload', this.onUpload);
      }
    });
  }

  public getExtensions(): string[] {
    return this.extensions ? this.extensions.split(',') : null;
  }

  public getButtonClass(): {[name: string]: any } {
    let _class = {
      'btn-danger': [
          PresentationStyleEnum.DANGER,
          PresentationStyleEnum.ERROR,
          PresentationStyleEnum.BLOCKER
        ].includes(this.presentationStyle),
      'btn-info': this.presentationStyle === PresentationStyleEnum.INFO,
      'btn-primary': this.presentationStyle === PresentationStyleEnum.OK,
      'btn-success': this.showProgressBar || this.presentationStyle === PresentationStyleEnum.SUCCESS,
      'btn-warning': this.presentationStyle === PresentationStyleEnum.WARNING
    };

    _class['btn-' + this.style] = !this.showProgressBar;

    return _class;
  }

  public onDragOver($event: Event): void {
    $event.preventDefault();
    $event.stopPropagation();
    this.dragover = true;
  }

  public onDragLeave($event: Event): void {
    $event.preventDefault();
    $event.stopPropagation();
    this.dragover = false;
  }

  public onDrop($event: DragEvent): void {
    let _files: FileList = $event.dataTransfer.files;

    $event.preventDefault();
    $event.stopPropagation();
    this.dragover = false;

    if (_files) {
      let event = new Event('change');

      Object.defineProperty(event, 'target', {writable: false, value: {files: _files}});

      this.onAddedFiles(event);
    }
  }

  public onCancelUpload($event?: Event): void {
    if (this.showProgressBar) {
      $event?.preventDefault();
      $event?.stopPropagation();
      this._destroyRequest$.next();
    }
  }

  private _requiredFilesType(files: File | File[], type: string[]): boolean {
    if (files instanceof Array) {
      for (let i = 0; i < files.length; i++) {
        if (!this._requiredFileType(files[i], type)) {
          return false;
        }
      }
      return true;
    }
    return this._requiredFileType(files, type);
  }

  private _requiredFileType(file: File, type: string[]): boolean {
    let existValue: boolean = false;

    if (file && type) {
      const path = file.name.replace(/^.*[\\\/]/, '');
      var re = /(?:\.([^.]+))?$/;

      const extension = '.' + re.exec(path)[1];
      for (let i = 0; i < type.length; i++) {
        const typeFile = type[i].toLowerCase();
        if (typeFile === extension.toLowerCase()) {
          existValue = true;
        }
      }

      if (existValue === true) {
        return true;
      } else {
        this._notification.showError(
          this._i18n.__('fh.upload.message.incorrect_file_extension', [this.extensions]), //Plik tego formatu nie jest obsługiwany. Akceptowalne pliki : ${this.extensions}.
          null,
          10000
        );
        return false;
      }
    }
    return true;
  }

  private _filesSizeValidator(files: File | File[]): boolean {
    if (files instanceof Array) {
      for (let i = 0; i < files.length; i++) {
        if (!this._filesSizeValidator(files[i])) {
          //TODO Translate!!
          // Plik ${file.name} jest za duży. Maksymalny rozmiar pliku to ${this.maxSize / 1024} KB.
          this._notification.showError(
              this._i18n.__('incorect_file_size', [files[i].name, this.maxSize/1024]),
              null,
              10000
          );
          return false;
        }
      }
      return true;
    }

    return (files as File).size <= this.maxSize;
  }

  private _createFormDataObject (file: FileList): FormData {
    let formData = new FormData();

    for (let i = 0; i < file?.length; i++) {
      formData.append('file', file[i]);

      //TODO: dodać walidacje dodawanych plików
    }

    formData.append('componentId', this.id);
    formData.append('formId', this.formId);
    formData.append('containerId', this.formsManager.findForm(this.formId)?.container.id);

    return formData;
  }

  private _getEventMessage(event: HttpEvent<any>, file?: FormData): HttpEvent<any> {
    console.log('_getEventMessage', event)
    switch (event.type) {
      case HttpEventType.Sent:
        this.showProgressBar = true;
        this.progress = 0;
        break;

      case HttpEventType.UploadProgress:
        // Compute and show the % done:
        this.progress = event.total ? Math.round(100 * event.loaded / event.total) : 0;
        break

      case HttpEventType.Response:
        this.progress = 100;
        this.showProgressBar = false;
        break;

      default:
        this.progress = 100;
        this.showProgressBar = false;
        break;
    }

    return event;
  }
}
