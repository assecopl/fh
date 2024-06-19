<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="documentationHolder" label="{$.fh.docs.changelog_change_log}">
    <OutputLabel width="md-12" id="basicInfoLabel" value="{$.fh.docs.changelog_everything_you_need_to_know_about} "/>
    <Spacer width="md-12" height="20px" id="_Form_Spacer"/>
    <TabContainer id="changeLogTabs">
        <Tab label="{$.fh.docs.changelog_new_features}" id="_Form_TabContainer_Tab1">
            <Spacer width="md-12" height="20px" id="_Form_TabContainer_Tab1_Spacer"/>
            <OutputLabel width="md-12" value="{$.fh.docs.changelog_list_of_new_features_and_improvements}" id="_Form_TabContainer_Tab1_OutputLabel"/>

            <PanelGroup id="newFeatures">
                <TabContainer id="newFeaturesTabs">
                    <Tab id="newTab" label="{$.fh.docs.changelog_new}">
                        <Spacer width="md-12" height="20px" id="_Form_TabContainer_Tab1_PanelGroup_TabContainer_Tab1_Spacer"/>
                        <Repeater collection="{newImprovements}" iterator="improvement" id="_Form_TabContainer_Tab1_PanelGroup_TabContainer_Tab1_Repeater">
                            <OutputLabel width="md-12" icon="fa fa-check" value="[color='#43b762']{improvement.title}[/color]" id="_Form_TabContainer_Tab1_PanelGroup_TabContainer_Tab1_Repeater_OutputLabel"/>
                            <PanelGroup id="_Form_TabContainer_Tab1_PanelGroup_TabContainer_Tab1_Repeater_PanelGroup">
                                <OutputLabel width="md-12" value="{improvement.description}" id="_Form_TabContainer_Tab1_PanelGroup_TabContainer_Tab1_Repeater_PanelGroup_OutputLabel1"/>
                                <Spacer width="md-12" height="10px" id="_Form_TabContainer_Tab1_PanelGroup_TabContainer_Tab1_Repeater_PanelGroup_Spacer"/>
                                <OutputLabel value="{$.fh.docs.changelog_authors}: " width="md-1" id="_Form_TabContainer_Tab1_PanelGroup_TabContainer_Tab1_Repeater_PanelGroup_OutputLabel2"/><OutputLabel value="{improvement.authors}" width="md-11" id="_Form_TabContainer_Tab1_PanelGroup_TabContainer_Tab1_Repeater_PanelGroup_OutputLabel3"/>
                                <OutputLabel value="{$.fh.docs.changelog_release}: " width="md-1" id="_Form_TabContainer_Tab1_PanelGroup_TabContainer_Tab1_Repeater_PanelGroup_OutputLabel4"/><OutputLabel value="{improvement.version}" formatter="dataFormatter" width="md-11" id="_Form_TabContainer_Tab1_PanelGroup_TabContainer_Tab1_Repeater_PanelGroup_OutputLabel5"/>
                                <OutputLabel value="{$.fh.docs.changelog_ticket}: " width="md-1" id="_Form_TabContainer_Tab1_PanelGroup_TabContainer_Tab1_Repeater_PanelGroup_OutputLabel6"/><Link value="{improvement.ticketUrl}" url="{improvement.ticketUrl}" width="md-11" id="_Form_TabContainer_Tab1_PanelGroup_TabContainer_Tab1_Repeater_PanelGroup_Link"/>
                                <OutputLabel value="{$.fh.docs.changelog_area}: " width="md-1" id="_Form_TabContainer_Tab1_PanelGroup_TabContainer_Tab1_Repeater_PanelGroup_OutputLabel7"/><OutputLabel value="{improvement.functionalArea}" width="md-11" id="_Form_TabContainer_Tab1_PanelGroup_TabContainer_Tab1_Repeater_PanelGroup_OutputLabel8"/>
                            </PanelGroup>
                        </Repeater>
                    </Tab>
                    <Tab id="allTab" label="{$.fh.docs.changelog_all}">
                        <TablePaged id="pageableImprovementsTable" iterator="improvement" collection="{allImprovements}" onRowClick="-" onPageChange="onImprovementsPageChange(this)" onPageSizeChange="onImprovementsPageChange(this)" onSortChange="onImprovementsPageChange(this)" pageSize="10">
                            <ColumnPaged label="{$.fh.docs.changelog_title}" value="{improvement.title}" sortBy="Title" width="10" id="_Form_TabContainer_Tab1_PanelGroup_TabContainer_Tab2_TablePaged_ColumnPaged1"/>
                            <ColumnPaged label="{$.fh.docs.changelog_description}" value="{improvement.description}" sortBy="Description" id="_Form_TabContainer_Tab1_PanelGroup_TabContainer_Tab2_TablePaged_ColumnPaged2"/>
                            <ColumnPaged label="{$.fh.docs.changelog_authors}" value="{improvement.authors}" width="10" id="_Form_TabContainer_Tab1_PanelGroup_TabContainer_Tab2_TablePaged_ColumnPaged3"/>
                            <ColumnPaged label="{$.fh.docs.changelog_version}" width="10" id="_Form_TabContainer_Tab1_PanelGroup_TabContainer_Tab2_TablePaged_ColumnPaged4">
                                <OutputLabel width="md-12" value="{improvement.version}" formatter="dataFormatter" id="_Form_TabContainer_Tab1_PanelGroup_TabContainer_Tab2_TablePaged_ColumnPaged4_OutputLabel"/>
                            </ColumnPaged>
                            <ColumnPaged label="{$.fh.docs.changelog_ticket}" width="10" id="_Form_TabContainer_Tab1_PanelGroup_TabContainer_Tab2_TablePaged_ColumnPaged5">
                                <OutputLabel width="md-12" value="{improvement.ticketUrl}" id="_Form_TabContainer_Tab1_PanelGroup_TabContainer_Tab2_TablePaged_ColumnPaged5_OutputLabel"/>
                            </ColumnPaged>
                        </TablePaged>
                    </Tab>
                </TabContainer>
            </PanelGroup>
        </Tab>

        <Tab label="{$.fh.docs.changelog_bug_fixes}" id="_Form_TabContainer_Tab2">
            <Spacer width="md-12" height="20px" id="_Form_TabContainer_Tab2_Spacer"/>
            <OutputLabel width="md-12" value="{$.fh.docs.changelog_list_of_bug_fixes}" id="_Form_TabContainer_Tab2_OutputLabel"/>

            <PanelGroup id="bugFixes">
                <TabContainer id="_Form_TabContainer_Tab2_PanelGroup_TabContainer">
                    <Tab label="{$.fh.docs.changelog_new}" id="_Form_TabContainer_Tab2_PanelGroup_TabContainer_Tab1">
                        <Repeater collection="{newBugFixes}" iterator="bug" id="_Form_TabContainer_Tab2_PanelGroup_TabContainer_Tab1_Repeater">
                            <Spacer width="md-12" height="20px" id="_Form_TabContainer_Tab2_PanelGroup_TabContainer_Tab1_Repeater_Spacer"/>
                            <OutputLabel width="md-12" icon="fa fa-bug" value="[color='#b74343']{bug.title}[/color]" id="_Form_TabContainer_Tab2_PanelGroup_TabContainer_Tab1_Repeater_OutputLabel"/>
                            <PanelGroup id="_Form_TabContainer_Tab2_PanelGroup_TabContainer_Tab1_Repeater_PanelGroup">
                                <OutputLabel width="md-12" value="{bug.description}" id="_Form_TabContainer_Tab2_PanelGroup_TabContainer_Tab1_Repeater_PanelGroup_OutputLabel1"/>
                                <Spacer width="md-12" height="10px" id="_Form_TabContainer_Tab2_PanelGroup_TabContainer_Tab1_Repeater_PanelGroup_Spacer"/>
                                <OutputLabel value="{$.fh.docs.changelog_authors}: " width="md-1" id="_Form_TabContainer_Tab2_PanelGroup_TabContainer_Tab1_Repeater_PanelGroup_OutputLabel2"/><OutputLabel value="{bug.authors}" width="md-11" id="_Form_TabContainer_Tab2_PanelGroup_TabContainer_Tab1_Repeater_PanelGroup_OutputLabel3"/>
                                <OutputLabel value="{$.fh.docs.changelog_release}: " width="md-1" id="_Form_TabContainer_Tab2_PanelGroup_TabContainer_Tab1_Repeater_PanelGroup_OutputLabel4"/><OutputLabel value="{bug.version}" formatter="dataFormatter" width="md-11" id="_Form_TabContainer_Tab2_PanelGroup_TabContainer_Tab1_Repeater_PanelGroup_OutputLabel5"/>
                                <OutputLabel value="{$.fh.docs.changelog_ticket}: " width="md-1" id="_Form_TabContainer_Tab2_PanelGroup_TabContainer_Tab1_Repeater_PanelGroup_OutputLabel6"/><Link value="{bug.ticketUrl}" url="{bug.ticketUrl}" width="md-11" id="_Form_TabContainer_Tab2_PanelGroup_TabContainer_Tab1_Repeater_PanelGroup_Link"/>
                                <OutputLabel value="{$.fh.docs.changelog_area}: " width="md-1" id="_Form_TabContainer_Tab2_PanelGroup_TabContainer_Tab1_Repeater_PanelGroup_OutputLabel7"/><OutputLabel value="{bug.functionalArea}" width="md-11" id="_Form_TabContainer_Tab2_PanelGroup_TabContainer_Tab1_Repeater_PanelGroup_OutputLabel8"/>
                            </PanelGroup>
                        </Repeater>
                    </Tab>
                    <Tab label="{$.fh.docs.changelog_all}" id="_Form_TabContainer_Tab2_PanelGroup_TabContainer_Tab2">
                        <TablePaged id="pageableBugFixesTable" iterator="bug" collection="{allBugFixes}" onRowClick="-" onPageChange="onBugFixesPageChange(this)" onPageSizeChange="onBugFixesPageChange(this)" onSortChange="onBugFixesPageChange(this)" pageSize="10">
                            <ColumnPaged label="{$.fh.docs.changelog_title}" value="{bug.title}" sortBy="Title" width="10" id="_Form_TabContainer_Tab2_PanelGroup_TabContainer_Tab2_TablePaged_ColumnPaged1"/>
                            <ColumnPaged label="{$.fh.docs.changelog_description}" value="{bug.description}" sortBy="Description" id="_Form_TabContainer_Tab2_PanelGroup_TabContainer_Tab2_TablePaged_ColumnPaged2"/>
                            <ColumnPaged label="{$.fh.docs.changelog_authors}" value="{bug.authors}" width="10" id="_Form_TabContainer_Tab2_PanelGroup_TabContainer_Tab2_TablePaged_ColumnPaged3"/>
                            <ColumnPaged label="{$.fh.docs.changelog_version}" width="10" id="_Form_TabContainer_Tab2_PanelGroup_TabContainer_Tab2_TablePaged_ColumnPaged4">
                                <OutputLabel width="md-12" value="{bug.version}" formatter="dataFormatter" id="_Form_TabContainer_Tab2_PanelGroup_TabContainer_Tab2_TablePaged_ColumnPaged4_OutputLabel"/>
                            </ColumnPaged>
                            <ColumnPaged label="{$.fh.docs.changelog_ticket}" width="10" id="_Form_TabContainer_Tab2_PanelGroup_TabContainer_Tab2_TablePaged_ColumnPaged5">
                                <OutputLabel width="md-12" value="{bug.ticketUrl}" id="_Form_TabContainer_Tab2_PanelGroup_TabContainer_Tab2_TablePaged_ColumnPaged5_OutputLabel"/>
                            </ColumnPaged>
                        </TablePaged>
                    </Tab>
                </TabContainer>
            </PanelGroup>
        </Tab>
    </TabContainer>
    <Button label="{$.fh.docs.changelog_update}" onClick="onUpdateButtonClick()" id="_Form_Button"/>
</Form>