<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="documentationHolder" label="{two}" layout="vertical">
    <AvailabilityConfiguration>
        <ReadOnly>staticText1,simpleBinding1,combinedBinding</ReadOnly>
        <ReadOnly when="-peopleList[0].happy">nestedSetAccess1,nestedSetAccess2</ReadOnly>
        <Invisible when="-peopleList[0] == peopleList[1]">nestedSetAccess1</Invisible>
        <Invisible when="-peopleList[0] != peopleList[1]">nestedSetAccess2</Invisible>
        <Invisible when="isPersonHappy(peopleList[0])">nestedSetAccess1,nestedSetAccess2</Invisible>
        <SetByProgrammer>listAccess1,listAccess2</SetByProgrammer>
        <Variant id="hidden">
            <Invisible>method1,method2</Invisible>
        </Variant>
    </AvailabilityConfiguration>
    <LocaleBundle basename="docsMessageSource" var="msg"/>
    <OutputLabel width="md-12" value="{one}" id="_Form_OutputLabel1"/>
    <OutputLabel width="md-12" value="{$.fh.welcome.info}" id="_Form_OutputLabel2"/>
    <OutputLabel width="md-12" value="{$msg.fh.welcome.info}" id="_Form_OutputLabel3"/>
    <TabContainer id="_Form_TabContainer">
        <Tab label="Examples" id="_Form_TabContainer_Tab1">
            <Group id="InputText_group">
                <Group width="md-12" id="_Form_TabContainer_Tab1_Group1_Group1">
                    <InputText id="staticText1" width="md-2" height="300" value="Static text"/>
                    <InputText id="simpleBinding1" width="md-2" height="300" value="{one}"/>
                    <InputText id="combinedBinding" width="md-2" height="300" value="Static text {one} and {two}"/>
                </Group>
                <Group width="md-12" id="_Form_TabContainer_Tab1_Group1_Group2">
                    <InputText width="md-12" id="propertyChainExample" value="{man.parent.firstName}"/>
                    <InputText width="md-12" id="propertyChainCombinedExample" value="{man.firstName} {man.lastName}"/>
                </Group>
                <Group width="md-12" id="_Form_TabContainer_Tab1_Group1_Group3">
                    <InputText id="listAccess1" width="md-1" value="{peopleList[counter].firstName}"/>
                    <InputText id="listAccess2" width="md-1" value="{peopleList[counter].parent.firstName}"/>
                </Group>
                <Group width="md-12" id="_Form_TabContainer_Tab1_Group1_Group4">
                    <InputText id="page1" width="md-1" value="{peoplePage[counter].firstName}"/>
                    <InputText id="pageModel1" width="md-1" value="{peoplePageModel[counter].firstName}"/>
                    <InputText id="pageModel2" width="md-1" value="{peopleCollectionPageModel[counter].firstName}"/>
                </Group>
                <Group width="md-12" id="_Form_TabContainer_Tab1_Group1_Group5">
                    <InputText id="arrayAccess1" width="md-1" value="{peopleArray}"/>
                    <InputText id="arrayAccess2" width="md-1" value="{peopleArray[counter].parent}"/>
                    <InputText id="arrayAccess3" width="md-1" value="{peopleArray[counter].parent.firstName}"/>
                </Group>
                <Group width="md-12" id="_Form_TabContainer_Tab1_Group1_Group6">
                    <InputText id="setAccess1" width="md-1" value="{peopleSet[counter]}"/>
                    <InputText id="setAccess2" width="md-1" value="{peopleSet[counter].parent.firstName}"/>
                </Group>
                <Group width="md-12" id="_Form_TabContainer_Tab1_Group1_Group7">
                    <InputText id="nestedSetAccess1" width="md-1" value="{peopleList[counter].childrenSet[counter]}"/>
                    <InputText id="nestedSetAccess2" width="md-1" value="{peopleList[counter].childrenSet[counter].childrenSet[counter].parent.firstName}"/>
                </Group>
                <Group width="md-12" id="_Form_TabContainer_Tab1_Group1_Group8">
                    <InputText id="literalIndex1" width="md-1" value="{peopleList[1].firstName}"/>
                    <InputText id="literalIndex2" width="md-1" value="{peopleList[2].childrenSet[12]}"/>
                    <InputText id="literalIndex3" width="md-1" value="{peopleSet[7].childrenSet[17]}"/>
                </Group>
            </Group>
            <Group width="md-12" id="_Form_TabContainer_Tab1_Group2">
                <InputText id="literal1" width="md-1" value="{'test'}"/>
                <InputText id="literal2" width="md-1" value="{77}"/>
                <InputText id="literal3" width="md-1" value="{null}"/>
                <InputText id="literal4" width="md-1" value="{true}"/>
                <InputText id="literal5" width="md-1" value="{1.7}"/>
            </Group>
            <Group width="md-12" id="_Form_TabContainer_Tab1_Group3">
                <InputText id="method1" width="md-1" value="{peopleList[1].sayHello(peopleList[0])}"/>
                <InputText id="method2" width="md-1" value="{peopleList[1].sayHello(peopleList[0], peopleList[0].parent)}"/>
                <InputText id="method3" width="md-1" value="{itest.toString()}"/>
            </Group>
            <Group width="md-12" id="_Form_TabContainer_Tab1_Group4">
                <InputText id="symbol1" width="md-1" value="{extractManFromModel(THIS)}"/>
                <InputText id="symbol2" width="md-1" value="{FORM.formatPerson(peopleList[0])}"/>
            </Group>
            <Group width="md-12" id="_Form_TabContainer_Tab1_Group5">
                <InputText id="type1" width="md-1" value="{T(pl.fhframework.compiler.forms.FormCompilerTest.Person).sayHelloStatic(peopleList[0])}"/>
            </Group>
        </Tab>
        <Tab label="Repeaters" id="_Form_TabContainer_Tab2">
            <Group width="md-12" id="_Form_TabContainer_Tab2_Group">
                <Repeater collection="{peopleList}" iterator="man" id="_Form_TabContainer_Tab2_Group_Repeater">
                    <OutputLabel width="md-12" value="{man.firstName}" id="_Form_TabContainer_Tab2_Group_Repeater_OutputLabel"/>
                    <Repeater collection="{man.childrenSet}" iterator="child" id="_Form_TabContainer_Tab2_Group_Repeater_Repeater">
                        <OutputLabel width="md-12" value="{man$rowNo}.{child$rowNo}." id="_Form_TabContainer_Tab2_Group_Repeater_Repeater_OutputLabel1"/>
                        <OutputLabel width="md-12" value="{child$index}" id="_Form_TabContainer_Tab2_Group_Repeater_Repeater_OutputLabel2"/>
                        <OutputLabel width="md-12" value="{child.lastName}" id="_Form_TabContainer_Tab2_Group_Repeater_Repeater_OutputLabel3"/>
                    </Repeater>
                </Repeater>
            </Group>
        </Tab>
        <Tab label="Repeaters2" id="_Form_TabContainer_Tab3">
            <Group width="md-12" id="_Form_TabContainer_Tab3_Group">
                <Repeater collection="{peopleList}" iterator="man" id="_Form_TabContainer_Tab3_Group_Repeater">
                    <OutputLabel width="md-12" value="{man.firstName}" id="_Form_TabContainer_Tab3_Group_Repeater_OutputLabel"/>
                    <Button label="Nop" onClick="-" id="_Form_TabContainer_Tab3_Group_Repeater_Button1"/>
                    <Button label="MyAction" onClick="myAction(man)" id="_Form_TabContainer_Tab3_Group_Repeater_Button2"/>
                    <Repeater collection="{man.childrenSet}" iterator="child" id="_Form_TabContainer_Tab3_Group_Repeater_Repeater">
                        <OutputLabel width="md-12" value="{man$rowNo}.{child$rowNo}." id="_Form_TabContainer_Tab3_Group_Repeater_Repeater_OutputLabel1"/>
                        <OutputLabel width="md-12" value="{child$index}" availability="{T(pl.fhframework.model.forms.AccessibilityEnum).HIDDEN}" id="_Form_TabContainer_Tab3_Group_Repeater_Repeater_OutputLabel2"/>
                        <OutputLabel width="md-12" value="{child.lastName}" availability="VIEW" id="_Form_TabContainer_Tab3_Group_Repeater_Repeater_OutputLabel3"/>
                        <Button label="Remove" onClick="myRemove(man, child)" id="_Form_TabContainer_Tab3_Group_Repeater_Repeater_Button"/>
                    </Repeater>
                </Repeater>
            </Group>
        </Tab>
    </TabContainer>
    <Table collection="{peopleList}" iterator="man" id="_Form_Table">
        <Iterator id="child" collection="{man.childrenSet}"/>

        <Column label="No" id="_Form_Table_Column1">
            <OutputLabel width="md-12" value="{man$rowNo}" id="_Form_Table_Column1_OutputLabel"/>
        </Column>
        <Column label="Last name" id="_Form_Table_Column2">
            <OutputLabel width="md-12" value="{man.lastName}" id="_Form_Table_Column2_OutputLabel"/>
        </Column>
        <Column label="Last name inline" value="{man.lastName}" id="_Form_Table_Column3"/>

        <Column label="Child No" iterationRef="child" id="_Form_Table_Column4">
            <OutputLabel width="md-12" value="{child$rowNo}" id="_Form_Table_Column4_OutputLabel"/>
        </Column>
        <Column label="Child last name" iterationRef="child" id="_Form_Table_Column5">
            <OutputLabel width="md-12" value="{child.lastName}" id="_Form_Table_Column5_OutputLabel"/>
        </Column>
    </Table>
    <TablePaged collection="{peoplePageModel}" iterator="manOnPage" id="_Form_TablePaged">
        <ColumnPaged label="No" id="_Form_TablePaged_ColumnPaged1">
            <OutputLabel width="md-12" value="{manOnPage$rowNo}" id="_Form_TablePaged_ColumnPaged1_OutputLabel"/>
        </ColumnPaged>
        <ColumnPaged label="Last name" id="_Form_TablePaged_ColumnPaged2">
            <OutputLabel width="md-12" value="{manOnPage.lastName}" id="_Form_TablePaged_ColumnPaged2_OutputLabel"/>
        </ColumnPaged>
        <ColumnPaged label="Last name inline" value="{manOnPage.lastName}" id="_Form_TablePaged_ColumnPaged3"/>
    </TablePaged>
    <Button id="pBack" label="$(chevron-left)Back" onClick="backToFormComponentsList()"/>

</Form>