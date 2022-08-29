<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" formType="STANDARD" modalSize="REGULAR">
    <PanelHeaderFhDP title="Embedded Views" info="Example" onClick="close" width="md-12" />
    <TabContainer>
        <Tab label="Embedded view - external" >
            <PanelGroup>
                <EmbeddedView src="http://www.africau.edu/images/default/sample.pdf" height="600px"/>
            </PanelGroup>
        </Tab>
        <Tab label="HTML view">
            <HtmlView width="md-12" styleClasses="announcement-view" text="{content}"/>
        </Tab>
        <Tab label="XML view">
            <OutputLabel width="md-12" value="&lt;XMLViewerFhDP content=&#34;exampleXML&#34; styleClass=&#34;MuiPaper-root&#34; /&gt;" />
            <XMLViewerFhDP content="{exampleXML}" styleClasses="MuiPaper-root" />
        </Tab>
        <Tab label="Map picker: world">
            <OutputLabel width="md-12" value="[code]&lt;RegionPickerFhDP map=&quot;world&quot; code=&quot;\{pickedCountryCode\}&quot; /&gt;[/code]" />
            <OutputLabel width="md-12" value="Selected contry code {pickedCountryCode}" />
<!--            <RegionPickerFhDP map="world" code="{pickedCountryCode}"/>-->
        </Tab>
        <Tab label="Map picker: europe">
            <OutputLabel width="md-12" value="[code]&lt;RegionPickerFhDP map=&quot;europe&quot; code=&quot;\{pickedCountryCode\}&quot; /&gt;[/code]" />
            <OutputLabel width="md-12" value="Selected contry code {pickedCountryCode}" />
<!--            <RegionPickerFhDP map="europe" code="{pickedCountryCode}" />-->
        </Tab>
    </TabContainer>
</Form>
