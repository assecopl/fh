<Form container="menuForm" id="menuInner" xmlns="http://fh.asseco.com/form/1.0" >
    <Row width="md-12" elementsHorizontalAlign="CENTER" styleClasses="search-padding,search-label">
    <InputText width="md-4" label="{$.menu.form.search}" value="{searchText}" onInput="filter" styleClasses="search-input"/>
    </Row>
    <Row width="md-12" elementsHorizontalAlign="AROUND" styleClasses="tree-menu">
        <Repeater width="md-12" collection="{menuElements}" iterator="el" styleClasses="child-wrapper-min-width">
            <Tree width="md-3" collection="{el.elements}"
                  expanded="true"
                  dynamic="true"
                  iterator="element"
                  relation="{children}"
                  nodeIcon="fa fa-caret-down"
                  collapsedNodeIcon="fa fa-caret-right">
                <TreeElement width="md-3" onLabelClick="menuElementClicked(element)"
                             label="{element.decoratedName}"
                             icon="{element.icon}" styleClasses="menu-position"/>
            </Tree>
        </Repeater>
    </Row>
</Form>
