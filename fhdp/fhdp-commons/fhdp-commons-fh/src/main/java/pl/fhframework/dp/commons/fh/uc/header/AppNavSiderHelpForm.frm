<Form xmlns="http://fh.asseco.com/form/1.0" id="settingsSider" container="appSider" formType="STANDARD" modalSize="REGULAR">
    <AvailabilityConfiguration>
        <Invisible when="onlyContrastStyle==false">onlyOneStyle</Invisible>
        <Invisible when="onlyContrastStyle==true">allStyles</Invisible>
    </AvailabilityConfiguration>

    <PanelHeaderFhDP info="{$.declaration.ct.sider.settings.info}" title="{$.declaration.ct.sider.settings.title}" onClick="closeAppSider" width="md-12" />

    <Group styleClasses="search-box-group">
        <PanelGroup styleClasses="switches" inlineStyle="margin-left: -15px;">
            <Row styleClasses="switch switch--unselected" inlineStyle="margin-bottom: 5px;">
                <OutputLabel width="md-6" value="{$.declaration.ct.sider.settings.tightMode}" styleClasses="switch-label"/>
                <CheckBox label=" " value=" " onChange="changeNarrowStyle"/>
            </Row>
            <Row styleClasses="switch switch--unselected" inlineStyle="margin-bottom: 5px;">
                <OutputLabel width="md-6" value="{$.declaration.ct.sider.settings.labelOperations}" styleClasses="switch-label"/>
                <CheckBox label=" " value=" " onChange="changeOperationLabels"/>
            </Row>

<!--            <Row styleClasses="switch switch&#45;&#45;unselected" inlineStyle="margin-bottom: 5px;">-->
<!--&lt;!&ndash;                <OutputLabel width="md-6" value="{$.declaration.ct.sider.settings.labelOperations}" styleClasses="switch-label"/>&ndash;&gt;-->
<!--                <CheckBox width="md-12" labelPosition="left" label="{$.declaration.ct.sider.settings.labelOperations}" value=" " onChange="changeOperationLabels"/>-->
<!--            </Row>-->

            <Row id="onlyOneStyle" styleClasses="switch switch--unselected" inlineStyle="margin-bottom: 5px;">
                <OutputLabel width="md-6" value="{$.declaration.ct.sider.settings.style}" styleClasses="switch-label"/>
                <CheckBox label=" " value="onlyContrastStyleChecked" onChange="onChangeOnlyContrastStyle"/>
            </Row>

            <Row id="allStyles" styleClasses="switch switch--unselected" inlineStyle="margin-bottom: 5px;">
                <OutputLabel width="md-3" value="{$.declaration.ct.sider.settings.style}" styleClasses="switch-label"/>
                <RadioOptionsGroup width="md-6" label=" "
                                   value="{selectedStyle}"
                                   values="{possibleStyles}"
                                   onChange="onChangeStyle"
                                   labelPosition="left"
                                   styleClasses="radio-positioned-left"
                                    />
            </Row>

        </PanelGroup>
    </Group>
</Form>