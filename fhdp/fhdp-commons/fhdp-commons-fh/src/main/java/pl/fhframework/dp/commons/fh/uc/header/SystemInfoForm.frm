<Form container="systemInfo" xmlns="http://fh.asseco.com/form/1.0" formType="HEADER">
    <!--    <OutputLabel width="md-12" horizontalAlign="center" value="{$msg.app.version.label} {version}" hint="{subversion}"/>-->
    <HtmlView id="userSystem" text="{user}" styleClasses="font-weight-bold"/>
    <Spacer width="md-2" height="5"/>
    <HtmlView id="userOffice" text="{office}"/>
    <HtmlView id="sessionCounter" text="{serverIP}"/>
    <!--    <LocaleBundle basename="translationsMessageSource" var="msg"/>-->
</Form>