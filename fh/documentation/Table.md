## Kontrolka Table

| <center>Identifier </center>  |  <center>Type  </center> | <center> Boundable  </center> |  <center>DefaultValue </center>  |  <center>Description  </center>    |  
|-----------|------|-----------|--------------|----------------|
|availability|AccessibilityEnum|true||Accessibility of an Component|
|collection|ModelBinding|true||Collection of data to be displayed|
|fixedHeader|boolean|false||Enables fixed headers when scrolling. Works only if table height is set.|
|height|String|false||Component height in "px" or "%", "px" is default height unit. There should not be any character between number and unit - height="80px" is valid, height="80 px" is invalid.|
|hiddenElementsTakeUpSpace|boolean|false|false|Parameter for HIDDEN components. Makes hidden elements still take up space in the page.|
|hint|ModelBinding|true||Hint for component, visible after hovering over specified component part|
|hintPlacement|HintPlacement|false|TOP|Placement of the hint for component. Available values: top, left, right, bottom. If value is not set then position will be chosen dynamically.|
|hintTrigger|HintTrigger|false|HOVER_FOCUS|Trigger of the hint for component. Available values: HOVER_FOCUS, HOVER. If value is not set then position will be HOVER_FOCUS.|
|horizontalAlign|HorizontalAlign|false|left|Component bootstrap option to place component on the left, center or right side of the view.|
|horizontalScrolling|boolean|false|false|Determines if horizontal scrolling is enabled in table.|
|id|String|false||Component identifier (should be unique within the view)|
|ieFocusFixEnabled|boolean|false|false|Enables IE inline elements focus fix. Attribute is ignored in non-IE browsers. If this attribute is set to true, spanning columns doesn't work.|
|iterator|String|false||Name of the iterator variable used to refer each data|
|label|ModelBinding|true||Represents label for created component. Supports FHML - Fh Markup Language.|
|marginBottom|String|false||Component margin amount in "px" to leave outter gap at the bottom. There should not be any character between number and unit - marginBottom="10px" is valid, marginBottom="10 px" is invalid.|
|marginLeft|String|false||Component margin amount in "px" to leave outter gap on the left side. There should not be any character between number and unit - marginLeft="10px" is valid, marginLeft="10 px" is invalid.|
|marginRight|String|false||Component margin amount in "px" to leave outter gap on the right side. There should not be any character between number and unit - marginRight="10px" is valid, marginRight="10 px" is invalid.|
|marginTop|String|false||Component margin amount in "px" to leave outter gap at the top. There should not be any character between number and unit - marginTop="10px" is valid, marginTop="10 px" is invalid.|
|minRows|Integer|false||Minimum of displayed rows.|
|multiselect|boolean|false|false|Determines if multiselect is enabled in table. If multiselect is set to true, selectedElement has to be set to Collection.|
|onDesignerToolboxDrop|ActionBinding|false||If the component is dropped on form edited in designer.|
|onRowClick|ActionBinding|false||If the table row is clicked that method will be executed|
|onRowDoubleClick|ActionBinding|false||If the table row is clicked twice that method will be executed|
|paddingBottom|String|false||Component padding amount in "px" to leave outter gap at the bottom. There should not be any character between number and unit - paddingBottom="10px" is valid, paddingBottom="10 px" is invalid. Only positive values.|
|paddingLeft|String|false||Component padding amount in "px" to leave outter gap at the bottom. There should not be any character between number and unit - paddingLeft="10px" is valid, paddingLeft="10 px" is invalid. Only positive values.|
|paddingRight|String|false||Component padding amount in "px" to leave outter gap at the bottom. There should not be any character between number and unit - paddingRight="10px" is valid, paddingRight="10 px" is invalid. Only positive values.|
|paddingTop|String|false||Component padding amount in "px" to leave outter gap at the bottom. There should not be any character between number and unit - paddingTop="10px" is valid, paddingTop="10 px" is invalid. Only positive values.|
|rowHeight|RowHeight|false|normal|Sets row height on tables|
|rowStylesMap|ModelBinding|true||Map of colored rows in pattern like <BusinessObject, Color>|
|selectable|boolean|false||Enables row selection feature.|
|selected|ModelBinding|true||Selected table row|
|selectionCheckboxes|boolean|false||Allows row selection using checkbox. Works only if multiselect is also enabled.|
|styleClasses|String|false||Component style classes, should be separated by ',' character|
|tableGrid|TableGrid|false|hide|Displays or hides grid on tables|
|tableStripes|TableStripes|false|hide|Displays or hides gray stripes on table rows|
|verticalAlign|VerticalAlign|false|bottom|Option to align component vertically relative to parent element. Available values: top, middle, bottom.|
|width|String|false|"md-12" for all components but not Button FileUpload or FileDownload - "md-2"|Component bootstrap size option to place component in one row and different column width. For Column this value should be numeric, because it will be translated to percentage value.|
