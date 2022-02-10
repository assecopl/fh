<Form container="appHeaderButtons" id="appHeaderButtonsInner" xmlns="http://fh.asseco.com/form/1.0">
    <Button id="headerMainMenuButton" width="md-6" label="[icon='fas fa-bars']"
            onClick="toggleMenu" hint="{$.fhdp.bar.menu.toggle}" hintTrigger="HOVER"
            styleClasses="app-header-button app-header-button--menu"/>
    <Button id="headerSearchButton" width="md-6" label="[icon='fas fa-search']"
            onClick="toggleSidebar" hint="{$.fhdp.bar.sidebar.toggle}" hintTrigger="HOVER"
            styleClasses="app-header-button" availability="{sideBarButtonAvailability}"/>
<!--    <Button width="md-2" label="[icon='fas fa-sitemap']"-->
<!--            onClick="toggleAdvancedSidebar" hint="Toggle advanced search (dev)"-->
<!--            styleClasses="app-header-button"/>-->
<!--    <Button width="md-2" label="[icon='fa stop-circle']"-->
<!--            onClick="toggleButtons" hint="Toggle buttons (dev)"-->
<!--            styleClasses="app-header-button"/>-->
<!--    <Button width="md-2" label="[icon='fas fa-hat-cowboy-side']"-->
<!--            onClick="toggleSearchButtons" hint="Toggle search buttons (dev)"-->
<!--            styleClasses="app-header-button"/>-->
</Form>
