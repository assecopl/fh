import {Directive, HostListener, Input, OnInit} from "@angular/core";
import {FormComponent} from "../components/form/form.component";
import {FhngComponent} from "../models/componentClasses/FhngComponent";

@Directive({
  selector: '[fhComponentKeySupport]',
})
class FhComponentKeySupport implements OnInit{
  private keyDefinitions: any[];
  // private formElement: any;
  // private postponedKeyDownHandler: null;
  private keyToHandlerMap: {} | any = null;


  // @Input()
  // protected keyDefinition: string[] = []
  @Input()
  protected onKeyEvent: string = null;
  @Input()
  protected keyEvent: string = null;

  private keyEventToHandlersMap:{} = {}
  @Input()
  private keyEventHandlers:any[] = [];

  private postponedKeyDownHandler:any =  null;

  constructor(
    private component: FhngComponent) {
  }

  ngOnInit(): void {
     if(!this.keyEvent) {
       this.keyEvent = this.component.keyEvent
     }
    if(!this.onKeyEvent) {
      this.onKeyEvent = this.component.onKeyEvent
    }
    this.createKeyToHandlerMap();
  }

  @HostListener('keydown', ['$event']) onKeydownHandler(
    event: KeyboardEvent
  ):void {
    if (this.keyToHandlerMap != null) {

            var supportedKeyDownHandler = this.findSupportedKeyHandler(event);
            if (supportedKeyDownHandler != null) {
              event.stopPropagation();
              event.preventDefault();
              event.cancelBubble = true;
              event.returnValue = false;
              this.postponedKeyDownHandler = supportedKeyDownHandler;
            }

    }
  }
  @HostListener('blur', ['$event'])
  @HostListener('keyup', ['$event'])
  onEnterHandler(
    event: KeyboardEvent
  ) {
          if (this.postponedKeyDownHandler != null) {
            var eventSuffix = '';
            if (this.postponedKeyDownHandler.id != this.component.id) {
              eventSuffix = '#' + this.postponedKeyDownHandler.id;
            }
            this.component.fireEvent('onKeyEvent' + eventSuffix, this.postponedKeyDownHandler.onKeyEvent);
            this.postponedKeyDownHandler = null;
          }
  }


  private modifiers_map = {
    'CTRL': 100000,
    'ALT': 10000,
    'SHIFT': 1000
  };

  private special_keys_map = {
    'F1': 112,
    'F2': 113,
    'F3': 114,
    'F4': 115,
    'F5': 116,
    'F6': 117,
    'F7': 118,
    'F8': 119,
    'F9': 120,
    'F10': 121,
    'F11': 122,
    'F12': 123,
    'ESC': 27,
    'ESCAPE': 27,
    'TAB': 9,
    'SPACE': 32,
    'RETURN': 13,
    'ENTER': 13,
    'BACKSPACE': 8,
    'SCROLLLOCK': 145,
    'SCROLL_LOCK': 145,
    'SCROLL': 145,
    'CAPSLOCK': 20,
    'CAPS_LOCK': 20,
    'CAPS': 20,
    'NUMLOCK': 144,
    'NUM_LOCK': 144,
    'NUM': 144,
    'PAUSE': 19,
    'BREAK': 19,
    'INSERT': 45,
    'HOME': 36,
    'DELETE': 46,
    'DEL': 46,
    'END': 35,
    'PAGEUP': 33,
    'PAGE_UP': 33,
    'PAGEDOWN': 34,
    'PAGE_DOWN': 34,
    'LEFT': 37,
    'UP': 38,
    'RIGHT': 39,
    'DOWN': 40
  };

  /**
   * Adds event listener to input (or other element). Returns function which removes that listener.
   */
  // public addKeyEventListeners(input, requiredEventSource = undefined) {
  //   if (this.keyToHandlerMap != null) {
  //     var keydownListener = function (event) {
  //       if (requiredEventSource != null && event.originalEvent.srcElement !== requiredEventSource) {
  //         return;
  //       }
  //       var supportedKeyDownHandler = this.findSupportedKeyHandler(event);
  //       if (supportedKeyDownHandler != null) {
  //         event.stopPropagation();
  //         event.preventDefault();
  //         event.cancelBubble = true;
  //         event.returnValue = false;
  //         this.postponedKeyDownHandler = supportedKeyDownHandler;
  //         return false;
  //       }
  //     }
  //       .bind(this);
  //
  //     var keyupBlurListener = function
  //     (event) {
  //       if (this.postponedKeyDownHandler != null) {
  //         var eventSuffix = '';
  //         if (this.postponedKeyDownHandler.id != this.formElement.id) {
  //           eventSuffix = '#' + this.postponedKeyDownHandler.id;
  //         }
  //         this.formElement.fireEvent('onKeyEvent' + eventSuffix, this.postponedKeyDownHandler.onKeyEvent);
  //         this.postponedKeyDownHandler = null;
  //       }
  //     }
  //       .bind(this);
  //
  //     $(input).on('keydown', keydownListener);
  //     $(input).on('keyup blur', keyupBlurListener);
  //
  //     return function () {
  //       $(input).off('keydown', keydownListener);
  //       $(input).off('keyup blur', keyupBlurListener);
  //     };
  //   } else {
  //     return function () {
  //     };
  //   }
  // }

  public supportsKey(event) {
    return this.findSupportedKeyHandler(event) != null;
  }

  public findSupportedKeyHandler(event) {
    return this.keyToHandlerMap[this.transformKeyEvent(event)];
  }

  public createKeyToHandlerMap() {
    this.keyEventToHandlersMap = [];
    // insert component's own keyEvent and onKeyEvent attributes as OnKeyEvent element
    if (this.keyEvent != null && this.keyEvent != '' && this.onKeyEvent != null && this.onKeyEvent != '') {
      this.keyEventHandlers.splice(0, 0, {
        'id': this.component.id,
        'onKeyEvent': this.onKeyEvent,
        'keyEvent': this.keyEvent
      });
    }

    for (var h = 0; h < this.keyEventHandlers.length; h++) {
      var handler = this.keyEventHandlers[h];
      var keyEvents = handler.keyEvent.split('|');
      for (var k = 0; k < keyEvents.length; k++) {
        var keyEventInt = this.transformKeyDefinition(keyEvents[k]);
        this.keyEventToHandlersMap[keyEventInt] = handler;

      }
    }
    this.keyToHandlerMap = this.keyEventToHandlersMap
  }

  public transformKeyDefinition(definition) {
    var definitionParts = definition.toUpperCase().split('+');
    var wasPlus = false;
    var wasKeyDefined = false;
    var definitionInt = 0;
    for (var i = 0; i < definitionParts.length; i++) {
      var part = definitionParts[i];
      var originalLen = part.length;
      part = part.trim();
      if (part.length == 0) {
        if (originalLen > 0) {
          part = ' '; // restore to single space
        } else if (wasPlus) {
          part = '+'; // CTRL++ -> CTRL, +
          wasPlus = false;
        } else {
          wasPlus = true;
          continue;
        }
      }
      if (!wasKeyDefined && part.length == 1) {
        wasKeyDefined = true;
        definitionInt += part.charCodeAt(0);
      } else {
        if (this.modifiers_map[part]) {
          definitionInt += this.modifiers_map[part];
        } else if (!wasKeyDefined) {
          if (part[0] === '$') {
            wasKeyDefined = true;
            try {
              definitionInt += parseInt(part.substring(1));
            } catch (e) {
              if (console) console.log(e);
            }
          } else if (this.special_keys_map[part]) {
            wasKeyDefined = true;
            definitionInt += this.special_keys_map[part];
          }
        }
      }
    }

    return definitionInt;
  }

  public transformKeyEvent(event) {
    var definitionInt = 0;
    var code = 0;
    if (event.keyCode != null) {
      code = event.keyCode;
    } else if (event.which != null) {
      code = event.which;
    }
    if (event.shiftKey) {
      definitionInt += this.modifiers_map['SHIFT'];
    }
    if (event.altKey) {
      definitionInt += this.modifiers_map['ALT'];
    }
    if (event.ctrlKey) {
      definitionInt += this.modifiers_map['CTRL'];
    }
    definitionInt += code;
    return definitionInt;
  }
}


export {FhComponentKeySupport};
