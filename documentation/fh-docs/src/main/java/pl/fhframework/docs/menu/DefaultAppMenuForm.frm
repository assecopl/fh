<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="documentationHolder" label="{$.fh.docs.menudocumentation_appmenu}">
    <AvailabilityConfiguration>
        <ReadOnly>fhdocsappmenudefaultapp,fhdocsappmenumenuxml,fhdocsappmenucustomtxt1,fhdocsappmenucustomtxt2
        </ReadOnly>
    </AvailabilityConfiguration>
    <OutputLabel width="md-12" value="{$.fh.docs.appmenu.defaultapp}" id="_Form_OutputLabel"/>
    <InputText id="fhdocsappmenudefaultapp" rowsCount="1" width="md-12">
        <![CDATA[![ESCAPE[
system.usecases.exclude.classes=pl.fhframework.app.menu.MenuUC
    ]]]]>
    </InputText>
    <PanelGroup width="md-12" label="{$.fh.docs.appmenu.definition}" id="_Form_PanelGroup1">
        <OutputLabel width="md-12" value="{$.fh.docs.appmenu.menuxml}" id="_Form_PanelGroup1_OutputLabel1"/>
        <InputText id="fhdocsappmenumenuxml" rowsCount="13" width="md-12">
            <![CDATA[![ESCAPE[
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<menu>
    <group label="$.i18n.groupLabel">
        <usecase ref="pl.fhframework.app.example.StartUC" label="Start" cloudExposed="false"/>
        <usecase ref="pl.fhframework.app.example.ChildUC" label="$.i18n.ucLabel" cloudExposed="false" inputFactory="ofUseCaseInputFactory"/>
        <group label="Inner group">
            ...
        </group>
    </group>
    <group label="Second group">
        ...
    </group>
</menu>
    ]]]]>
        </InputText>
        <OutputLabel width="md-12" value="{$.fh.docs.appmenu.menuxml.ucattrs1}" id="_Form_PanelGroup1_OutputLabel2"/>
        <Table collection="{ucAttrs}" iterator="item" id="_Form_PanelGroup1_Table">
            <Column label="{$.fh.docs.appmenu.menuxml.ucattrs2}" value="{item.value1}" width="20" id="_Form_PanelGroup1_Table_Column1"/>
            <Column label="{$.fh.docs.appmenu.menuxml.ucattrs3}" value="{item.value2}" id="_Form_PanelGroup1_Table_Column2"/>
            <Column label="{$.fh.docs.appmenu.menuxml.ucattrs4}" value="{item.value3}" width="20" id="_Form_PanelGroup1_Table_Column3"/>
        </Table>
    </PanelGroup>
    <PanelGroup width="md-12" label="{$.fh.docs.appmenu.custom.label}" id="_Form_PanelGroup2">
        <OutputLabel width="md-12" value="{$.fh.docs.appmenu.custom.txt1}" id="_Form_PanelGroup2_OutputLabel1"/>
        <InputText id="fhdocsappmenucustomtxt1" rowsCount="11" width="md-12">
            <![CDATA[![ESCAPE[
@UseCase
public class MenuUC implements ISystemUseCase, IUseCase18nListener {
    @Autowired
    private MenuManager menuManager;

    @Override
    public void start() {
        List<MenuElement> menuElements = menuManager.getMenuModel(SessionManager.getUserSession().getSystemUser());

        form = showForm(MenuForm.class, menuElements);
        ...
    ]]]]>
        </InputText>
        <OutputLabel width="md-12" value="{$.fh.docs.appmenu.custom.txt2}" id="_Form_PanelGroup2_OutputLabel2"/>
        <InputText id="fhdocsappmenucustomtxt2" rowsCount="2" width="md-12">
            <![CDATA[![ESCAPE[
<Form container="menuForm" id="menuFormInner" xmlns="http://fh.asseco.com/form/1.0">
    <Tree collection="{menuElements}" iterator="element" ...
    ]]]]>
        </InputText>
        <OutputLabel width="md-12" value="{$.fh.docs.appmenu.custom.txt3}" id="_Form_PanelGroup2_OutputLabel3"/>
    </PanelGroup>
    <PanelGroup width="md-12" label="{$.fh.docs.appmenu.factory.label}" id="_Form_PanelGroup3">
        <OutputLabel value="{$.fh.docs.appmenu.factory.txt1}" width="md-12" id="_Form_PanelGroup3_OutputLabel1"/>
        <Spacer width="md-12" height="10px" id="_Form_PanelGroup3_Spacer1"/>
        <OutputLabel width="md-12" value="{$.fh.docs.appmenu.factory.txt1_1}" id="_Form_PanelGroup3_OutputLabel2"/>
        <Spacer width="md-12" height="10px" id="_Form_PanelGroup3_Spacer2"/>
        <OutputLabel width="md-12" value="{$.fh.docs.appmenu.factory.txt1_2}" id="_Form_PanelGroup3_OutputLabel3"/>
        <Spacer width="md-12" height="10px" id="_Form_PanelGroup3_Spacer3"/>
        <OutputLabel width="md-12" value="{$.fh.docs.appmenu.factory.txt1_3}" id="_Form_PanelGroup3_OutputLabel4"/>
        <Spacer width="md-12" height="20px" id="_Form_PanelGroup3_Spacer4"/>
        <OutputLabel width="md-12" value="{$.fh.docs.appmenu.factory.txt2}" id="_Form_PanelGroup3_OutputLabel5"/>
        <Spacer width="md-12" height="20px" id="_Form_PanelGroup3_Spacer5"/>
        <Button label="{$.fh.docs.appmenu.factory.run1}" width="md-3" onClick="runOf" id="_Form_PanelGroup3_Button1"/>
        <Button label="{$.fh.docs.appmenu.factory.run2}" width="md-3" onClick="runNew" id="_Form_PanelGroup3_Button2"/>
        <Button label="{$.fh.docs.appmenu.factory.run3}" width="md-3" onClick="runNull" id="_Form_PanelGroup3_Button3"/>
    </PanelGroup>
</Form>