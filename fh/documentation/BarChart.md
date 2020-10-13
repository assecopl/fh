##1. Kontrolka BarChart

| <center>Identifier </center>  |  <center>Type  </center> | <center> Boundable  </center> |  <center>DefaultValue </center>  |  <center>Description  </center>    |  
|-----------|------|-----------|--------------|----------------|
|Identifier|Type|Boundable|Default value|Description|
|availability|AccessibilityEnum|true||Accessibility of an Component|
|colors|ModelBinding|true||Defines colors of data series, values are separated with.For example colors=&quot;black,blue,yellow&quot; says that first series of data is black, second is blue, third is yellow.|
|height|String|false||Component height in "px" or "%", "px" is default height unit. There should not be any character between number and unit - height="80px" is valid, height="80 px" is invalid.|
|hiddenElementsTakeUpSpace|boolean|false|false|Parameter for HIDDEN components. Makes hidden elements still take up space in the page.|
|hint|ModelBinding|true||Hint for component, visible after hovering over specified component part|
|hintPlacement|HintPlacement|false|TOP|Placement of the hint for component. Available values: top, left, right, bottom. If value is not set then position will be chosen dynamically.|
|hintTrigger|HintTrigger|false|HOVER_FOCUS|Trigger of the hint for component. Available values: HOVER_FOCUS, HOVER. If value is not set then position will be HOVER_FOCUS.|
|horizontalAlign|HorizontalAlign|false|left|Component bootstrap option to place component on the left, center or right side of the view.|
|id|String|false||Component identifier (should be unique within the view)|
|marginBottom|String|false||Component margin amount in "px" to leave outter gap at the bottom. There should not be any character between number and unit - marginBottom="10px" is valid, marginBottom="10 px" is invalid.|
|marginLeft|String|false||Component margin amount in "px" to leave outter gap on the left side. There should not be any character between number and unit - marginLeft="10px" is valid, marginLeft="10 px" is invalid.|
|marginRight|String|false||Component margin amount in "px" to leave outter gap on the right side. There should not be any character between number and unit - marginRight="10px" is valid, marginRight="10 px" is invalid.|
|marginTop|String|false||Component margin amount in "px" to leave outter gap at the top. There should not be any character between number and unit - marginTop="10px" is valid, marginTop="10 px" is invalid.|
|onDesignerToolboxDrop|ActionBinding|false||If the component is dropped on form edited in designer.|
|paddingBottom|String|false||Component padding amount in "px" to leave outter gap at the bottom. There should not be any character between number and unit - paddingBottom="10px" is valid, paddingBottom="10 px" is invalid. Only positive values.|
|paddingLeft|String|false||Component padding amount in "px" to leave outter gap at the bottom. There should not be any character between number and unit - paddingLeft="10px" is valid, paddingLeft="10 px" is invalid. Only positive values.|
|paddingRight|String|false||Component padding amount in "px" to leave outter gap at the bottom. There should not be any character between number and unit - paddingRight="10px" is valid, paddingRight="10 px" is invalid. Only positive values.|
|paddingTop|String|false||Component padding amount in "px" to leave outter gap at the bottom. There should not be any character between number and unit - paddingTop="10px" is valid, paddingTop="10 px" is invalid. Only positive values.|
|stacked|ModelBinding|true|false|Determines if series of data should be stacked or not|
|styleClasses|String|false||Component style classes, should be separated by ',' character|
|title|ModelBinding|true||Title of a chart|
|values|ModelBinding|true||List<ChartSeries> object representing series of data. First ChartSeries of data has to contain all values from X series.|
|verticalAlign|VerticalAlign|false|bottom|Option to align component vertically relative to parent element. Available values: top, middle, bottom.|
|width|String|false|"md-12" for all components but not Button FileUpload or FileDownload - "md-2"|Component bootstrap size option to place component in one row and different column width. For Column this value should be numeric, because it will be translated to percentage value.|
|xAxisLabel|ModelBinding|true||Label of X axis|
|yAxisLabel|ModelBinding|true||Label of Y axis|
|yAxisMax|ModelBinding|true||Max value of Y axis|
|yAxisMin|ModelBinding|true||Min value on Y axis|
