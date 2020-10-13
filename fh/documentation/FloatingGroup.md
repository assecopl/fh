## Kontrolka FloatingGroup

| <center>Identifier </center>  |  <center>Type  </center> | <center> Boundable  </center> |  <center>DefaultValue </center>  |  <center>Description  </center>    |  
|-----------|------|-----------|--------------|----------------| 
|availability|AccessibilityEnum|true||Accessibility of an Component|
|borderVisible|boolean|false|false|Defines if group should have border. False by default.|
|collapsed|ModelBinding|true|false|Contains a state for collapsible group, if the group is currently collapsed or expanded. Depends on attribute: collapsible, if it's not set then binding will not be resolved. False by default.|
|collapsible|boolean|false|false|Defines if group can be collapsed or expanded. False by default.|
|floatingHeight|String|false||Floating group height (floating state only). Negative value would be substracted from the height of the screen. Defaults to height attribute value.|
|floatingOnly|Boolean|true|false|Defines if group will be in floating mode only|
|floatingState|Enum|true|PINNED_MINIMIZED|Defines state of floating group|
|floatingWidth|String|false||Floating group width (floating state only). Negative value would be substracted from the width of the screen. Defaults to width attribute value.|
|headingType|HeadingTypeEnum|false|Default|Defines heading tag for component Set it to H1, H2, H3, H4, H5, H6 or Auto. With Auto value system will calculate tag based on components hierarchy. Default there is no heading tag.|
|height|String|false||Component height in "px" or "%", "px" is default height unit. There should not be any character between number and unit - height="80px" is valid, height="80 px" is invalid.|
|hiddenElementsTakeUpSpace|boolean|false|false|Parameter for HIDDEN components. Makes hidden elements still take up space in the page.|
|hideButtons|boolean|false||Determines if pinning/fullscreen buttons are hidden.|
|hideHeader|boolean|false||Determines if header with label and buttons is hidden.|
|hint|ModelBinding|true||Hint for component, visible after hovering over specified component part|
|hintPlacement|HintPlacement|false|TOP|Placement of the hint for component. Available values: top, left, right, bottom. If value is not set then position will be chosen dynamically.|
|hintTrigger|HintTrigger|false|HOVER_FOCUS|Trigger of the hint for component. Available values: HOVER_FOCUS, HOVER. If value is not set then position will be HOVER_FOCUS.|
|horizontalAlign|HorizontalAlign|false|left|Component bootstrap option to place component on the left, center or right side of the view.|
|id|String|false||Component identifier (should be unique within the view)|
|isDraggable|Boolean|false|true|Determine if user can change position of an element by dragging it.|
|label|ModelBinding|true||Represents label for created component. Supports FHML - FH Markup Language.|
|left|String|false||Floating group offset from left side of the screen (floating state only). Negative value would be substracted from the width of the screen.|
|marginBottom|String|false||Component margin amount in "px" to leave outter gap at the bottom. There should not be any character between number and unit - marginBottom="10px" is valid, marginBottom="10 px" is invalid.|
|marginLeft|String|false||Component margin amount in "px" to leave outter gap on the left side. There should not be any character between number and unit - marginLeft="10px" is valid, marginLeft="10 px" is invalid.|
|marginRight|String|false||Component margin amount in "px" to leave outter gap on the right side. There should not be any character between number and unit - marginRight="10px" is valid, marginRight="10 px" is invalid.|
|marginTop|String|false||Component margin amount in "px" to leave outter gap at the top. There should not be any character between number and unit - marginTop="10px" is valid, marginTop="10 px" is invalid.|
|onDesignerToolboxDrop|ActionBinding|false||If the component is dropped on form edited in designer.|
|onToggle|ActionBinding|false||Name of event which will be invoked after cliking collapse group button. Over it it also defines if group can be collapsed or expanded. Empty by default (not collapsible).|
|onToggleFullScreen|ActionBinding|false||Name of action which will be invoked after toggling full screen|
|onTogglePin|ActionBinding|false||Name of action which will be invoked after pinning/unpinning|
|paddingBottom|String|false||Component padding amount in "px" to leave outter gap at the bottom. There should not be any character between number and unit - paddingBottom="10px" is valid, paddingBottom="10 px" is invalid. Only positive values.|
|paddingLeft|String|false||Component padding amount in "px" to leave outter gap at the bottom. There should not be any character between number and unit - paddingLeft="10px" is valid, paddingLeft="10 px" is invalid. Only positive values.|
|paddingRight|String|false||Component padding amount in "px" to leave outter gap at the bottom. There should not be any character between number and unit - paddingRight="10px" is valid, paddingRight="10 px" is invalid. Only positive values.|
|paddingTop|String|false||Component padding amount in "px" to leave outter gap at the bottom. There should not be any character between number and unit - paddingTop="10px" is valid, paddingTop="10 px" is invalid. Only positive values.|
|pinningMode|FloatingPinMode|false|normal|Pinning mode of the floating group. Valid values are: normal, button, invisible|
|styleClasses|String|false||Component style classes, should be separated by ',' character|
|top|String|false||Floating group offset from top of the screen (floating state only). Negative value would be substracted from the height of the screen.|
|verticalAlign|VerticalAlign|false|bottom|Option to align component vertically relative to parent element. Available values: top, middle, bottom.|
|width|String|false|"md-12" for all components but not Button FileUpload or FileDownload - "md-2"|Component bootstrap size option to place component in one row and different column width. For Column this value should be numeric, because it will be translated to percentage value.|
