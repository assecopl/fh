## Control Tree

| <center>Identifier </center>  |  <center>Type  </center> | <center> Boundable  </center> |  <center>DefaultValue </center>  |  <center>Description  </center>    |  
|-----------|------|-----------|--------------|----------------|
|availability|AccessibilityEnum|true||Accessibility of an Component|
|collapsedNodeIcon|String|false|fas fa-caret-right|Expand icon|
|collection|ModelBinding|true||Collection of data to be displayed|
|dynamic|boolean|false|true|Is tree processing itself, once it was displayed.|
|expanded|boolean|false|false|Are all levels already opened by default. This option is ignored when lazy = true.|
|height|String|false||Component height in "px" or "%", "px" is default height unit. There should not be any character between number and unit - height="80px" is valid, height="80 px" is invalid.|
|hiddenElementsTakeUpSpace|boolean|false|false|Parameter for HIDDEN components. Makes hidden elements still take up space in the page.|
|hint|ModelBinding|true||Hint for component, visible after hovering over specified component part|
|hintPlacement|HintPlacement|false|TOP|Placement of the hint for component. Available values: top, left, right, bottom. If value is not set then position will be chosen dynamically.|
|hintTrigger|HintTrigger|false|HOVER_FOCUS|Trigger of the hint for component. Available values: HOVER_FOCUS, HOVER. If value is not set then position will be HOVER_FOCUS.|
|horizontalAlign|HorizontalAlign|false|left|Component bootstrap option to place component on the left, center or right side of the view.|
|id|String|false||Component identifier (should be unique within the view)|
|iterator|String|false||Name of the iterator variable used to refer each data|
|lazy|boolean|false|false|Are tree node lazy loaded while user expands them.|
|leafIcon|String|false||Leaf icon, displayed when node has no children. Default is 'hidden'|
|lines|boolean|false||Draw lines between parent and children. Default is 'true'|
|marginBottom|String|false||Component margin amount in "px" to leave outter gap at the bottom. There should not be any character between number and unit - marginBottom="10px" is valid, marginBottom="10 px" is invalid.|
|marginLeft|String|false||Component margin amount in "px" to leave outter gap on the left side. There should not be any character between number and unit - marginLeft="10px" is valid, marginLeft="10 px" is invalid.|
|marginRight|String|false||Component margin amount in "px" to leave outter gap on the right side. There should not be any character between number and unit - marginRight="10px" is valid, marginRight="10 px" is invalid.|
|marginTop|String|false||Component margin amount in "px" to leave outter gap at the top. There should not be any character between number and unit - marginTop="10px" is valid, marginTop="10 px" is invalid.|
|nextLevelExpandableExpression|String|true|true|Current object based expression which checks if next level should be expandable, which means that state / type of the object suggests that it has / may have child nodes. Returned value may be false-positive which results in disapearing of expand icon after clicking on it. Used only when lazy=true.|
|nodeIcon|String|false|fas fa-caret-down|Collapse icon|
|onDesignerToolboxDrop|ActionBinding|false||If the component is dropped on form edited in designer.|
|paddingBottom|String|false||Component padding amount in "px" to leave outter gap at the bottom. There should not be any character between number and unit - paddingBottom="10px" is valid, paddingBottom="10 px" is invalid. Only positive values.|
|paddingLeft|String|false||Component padding amount in "px" to leave outter gap at the bottom. There should not be any character between number and unit - paddingLeft="10px" is valid, paddingLeft="10 px" is invalid. Only positive values.|
|paddingRight|String|false||Component padding amount in "px" to leave outter gap at the bottom. There should not be any character between number and unit - paddingRight="10px" is valid, paddingRight="10 px" is invalid. Only positive values.|
|paddingTop|String|false||Component padding amount in "px" to leave outter gap at the bottom. There should not be any character between number and unit - paddingTop="10px" is valid, paddingTop="10 px" is invalid. Only positive values.|
|relation|String|true||Name of the property in object, that should process nested relation|
|selected|ModelBinding|true||Selected object|
|styleClasses|String|false||Component style classes, should be separated by ',' character|
|verticalAlign|VerticalAlign|false|bottom|Option to align component vertically relative to parent element. Available values: top, middle, bottom.|
