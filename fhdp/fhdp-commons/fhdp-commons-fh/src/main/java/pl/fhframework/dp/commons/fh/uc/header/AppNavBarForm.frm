<Form container="navbarForm" id="navbarFormInner" xmlns="http://fh.asseco.com/form/1.0" formType="HEADER">
    <AvailabilityConfiguration>
        <Invisible when="translationLT==false">lithuanianLang</Invisible>
        <Invisible when="translationPL==false">polishLang</Invisible>
        <Invisible when="translationEN==false">englishLang</Invisible>
<!--        <Invisible when="translationNO==false">noLang</Invisible>-->
        <Invisible when="-language eq 'pl'">polishLang</Invisible>
        <Invisible when="-language eq 'en'">englishLang</Invisible>
        <Invisible when="-language eq 'lt'">lithuanianLang</Invisible>
        <Invisible when="-guest">diLogout</Invisible>
        <Invisible when="-not guest">diLogin</Invisible>
        <Invisible when="languageDropdown==false">languageDropdown</Invisible>
        <Invisible when="appSider==false">settingsSider</Invisible>
        <Invisible when="operationSider==false">operationSider</Invisible>
        <Invisible when="helpSider==false">helpSider</Invisible>
        <Invisible when="sessionClock==false">sessionClock</Invisible>
    </AvailabilityConfiguration>
    <Row styleClasses="flex-nowrap">
        <Dropdown width="md-2" label="{language}"
                  styleClasses="tray-button tray-button--text button"
                  id="languageDropdown" hint="{$.fhdp.menu.ui.navbar.toogleLanguage}">
            <DropdownItem value="{$.fhdp.menu.ui.navbar.buttons.english}" icon="fas fa-globe-europe" id="englishLang" onClick="setLanguageEnglish" />
            <DropdownItem value="{$.fhdp.menu.ui.navbar.buttons.lithuanian}" icon="fas fa-globe-europe" id="lithuanianLang" onClick="setLanguageLithuanian" />
            <DropdownItem value="{$.fhdp.menu.ui.navbar.buttons.polish}" icon="fas fa-globe-europe" id="polishLang" onClick="setLanguagePolish" />
            <DropdownItem value="{$.menu.ui.navbar.buttons.no_lang}" icon="fas fa-globe-europe" id="noLang" onClick="setLanguageNo" />
            <DropdownItem value="View Key" icon="fas fa-globe-europe" onClick="setViewkey" />
        </Dropdown>
        <Button width="md-2" label="[icon='fas fa-sliders-h']" hintTrigger="HOVER"
                onClick="displayAppSider" styleClasses="tray-button button"
                id="settingsSider" hint="{$.fhdp.menu.ui.navbar.settings}"/>
        <Button width="md-2" label="[icon='fas fa-cog']" hintTrigger="HOVER"
                onClick="displayOperationSider" styleClasses="tray-button button"
                id="operationSider" hint="{$.fhdp.menu.ui.navbar.operations}"/>
        <Button width="md-2" label="[icon='fas fa-question-circle']" hintTrigger="HOVER"
                onClick="displayHelpPageSider"  styleClasses="tray-button button"
                id="helpSider"  hint="{$.fhdp.menu.ui.navbar.help}"/>
        <Button width="md-2" label="[icon='far fa-clock']"
                hint="{counter}" hintTrigger="HOVER"
                styleClasses="tray-button button"
                id="sessionClock"/>
        <Button id="diLogout" width="md-2" hintTrigger="HOVER"
                hint="{$.fhdp.menu.ui.navbar.buttons.logout}"
                styleClasses="tray-button button"
                onClick="logout" label="[icon='fa fa-power-off']"/>
    </Row>

</Form>
