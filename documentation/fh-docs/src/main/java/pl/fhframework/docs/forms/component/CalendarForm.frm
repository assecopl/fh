<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="documentationHolder" label="{componentName}">
    <AvailabilityConfiguration>
        <ReadOnly>calendarExampleCode1,calendarExampleCode2,calendarExampleCode3,calendarExampleCode4,calendarExampleCode5,calendarExampleCode6,calendarExampleCode7,calendarExampleCode8,calendarExampleJavaCode1,calendarExampleJavaCode2</ReadOnly>
    </AvailabilityConfiguration>
    <OutputLabel width="md-12" value="{description}" id="_Form_OutputLabel"/>
    <Spacer width="md-12" height="25px" id="_Form_Spacer"/>
    <TabContainer id="_Form_TabContainer">
        <Tab label="{$.fh.docs.component.examples}" id="_Form_TabContainer_Tab1">
                <TabContainer id="_Form_TabContainer_Tab1_TabContainer">
                    <Tab label="{$.fh.docs.component.calendar_simple_data}" id="_Form_TabContainer_Tab1_TabContainer_Tab1">
                        <PanelGroup width="md-12" label="{$.fh.docs.component.calendar_with_predefined_values}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup1">
                            <Calendar label="{$.fh.docs.component.calendar_schedule_of_random_events}" values="2017-01-01|2017-01-02|2017-01-03|2017-01-10" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup1_Calendar"/>

                            <InputText id="calendarExampleCode1" label="XML" width="md-12" rowsCount="1">
                                <![CDATA[
<Calendar label="Schedule of random events" values="2017-01-01|2017-01-02|2017-01-03|2017-01-10"/>
                                ]]>
                            </InputText>
                        </PanelGroup>

                        <PanelGroup width="md-12" label="{$.fh.docs.component.calendar_with_predefined_values_for_allowed_date}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup2">
                            <Calendar label="{$.fh.docs.component.calendar_schedule_of_random_events}" values="2017-01-01|2017-01-02|2017-01-03|2017-01-10" minDate="2017-01-05" maxDate="2017-01-20" blockedDates="2017-01-10|2017-01-11|2017-01-12" value="2017-01-19" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup2_Calendar"/>

                            <InputText id="calendarExampleCode6" label="XML" width="md-12" rowsCount="2">
                                <![CDATA[
<Calendar label="Schedule of random events" values="2017-01-01|2017-01-02|2017-01-03|2017-01-10" minDate="2017-01-05" maxDate="2017-01-20" blockedDates="2017-01-10|2017-01-11|2017-01-12" value="2017-01-19"/>
                                ]]>
                            </InputText>
                        </PanelGroup>


                        <PanelGroup width="md-12" label="{$.fh.docs.component.calendar_month_and_year_menu_selection}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup3">
                            <Calendar label="{$.fh.docs.component.calendar_schedule_of_random_events}" values="2017-01-01|2017-01-02|2017-01-03|2017-01-10" minDate="2017-01-05" maxDate="2017-01-20" blockedDates="2017-01-10|2017-01-11|2017-01-12" value="2017-01-19" changeMonth="true" changeYear="true" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup3_Calendar"/>

                            <InputText id="calendarExampleCode8" label="XML" width="md-12" rowsCount="2">
                                <![CDATA[
<Calendar label="Schedule of random events" values="2017-01-01|2017-01-02|2017-01-03|2017-01-10" minDate="2017-01-05" maxDate="2017-01-20" blockedDates="2017-01-10|2017-01-11|2017-01-12" value="2017-01-19" changeMonth="true" changeYear="true"/>
                                ]]>
                            </InputText>
                        </PanelGroup>
                    </Tab>

                    <Tab label="{$.fh.docs.component.calendar_simple_binding_and_collections}" id="_Form_TabContainer_Tab1_TabContainer_Tab2">
                        <PanelGroup width="md-12" label="{$.fh.docs.component.calendar_with_simple_collection}" id="_Form_TabContainer_Tab1_TabContainer_Tab2_PanelGroup1">
                            <Calendar label="{$.fh.docs.component.calendar_schedule_of_random_events}" values="{scheduleOfSimpleCollection}" id="_Form_TabContainer_Tab1_TabContainer_Tab2_PanelGroup1_Calendar"/>

                            <InputText id="calendarExampleCode2" label="XML" width="md-12" rowsCount="1">
                                <![CDATA[
<Calendar label="Schedule of random events" values="\{scheduleOfSimpleCollection\}"/>
                                ]]>
                            </InputText>

                            <InputText id="calendarExampleJavaCode2" label="Java" width="md-12" rowsCount="5">
                                <![CDATA[
public List<Date> getScheduleOfSimpleCollection() \{
    return Arrays.asList(conversionService.convert(CURRENT_YEAR_MONTH + "-03", LocalDate.class),
            conversionService.convert(CURRENT_YEAR_MONTH + "-01", LocalDate.class),
            conversionService.convert(CURRENT_YEAR_MONTH + "-02", LocalDate.class));
\}
                                ]]>
                            </InputText>
                        </PanelGroup>

                        <PanelGroup width="md-12" label="{$.fh.docs.component.calendar_with_holidays_and_weekends}" id="_Form_TabContainer_Tab1_TabContainer_Tab2_PanelGroup2">
                            <Calendar label="{$.fh.docs.component.calendar_schedule_of_random_events}" values="{weekends}" minDate="{minDate}" maxDate="{maxDate}" id="_Form_TabContainer_Tab1_TabContainer_Tab2_PanelGroup2_Calendar"/>

                            <InputText id="calendarExampleCode7" label="XML" width="md-12" height="50" rowsCount="1">
                                <![CDATA[
 <Calendar label="Schedule of random events" values="\{weekends\}" minDate="\{minDate\}" maxDate="\{maxDate\}"/>
                                ]]>
                            </InputText>
                        </PanelGroup>
                    </Tab>

                    <Tab label="{$.fh.docs.component.calendar_multi_collections}" id="_Form_TabContainer_Tab1_TabContainer_Tab3">
                        <PanelGroup width="md-12" label="{$.fh.docs.component.calendar_with_advanced_biding}" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup">
                            <Calendar label="{$.fh.docs.component.calendar_schedule_of_game_events}" values="{scheduleOfGameEventsData}" value="{selectedGameDate}" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup_Calendar"/>

                            <InputText id="calendarExampleCode3" label="XML" width="md-12" rowsCount="1">
                                <![CDATA[
<Calendar label="Schedule of game events" values="\{scheduleOfGameEventsData\}" value="\{selectedGameDate\}"/>
                                ]]>
                            </InputText>

                            <InputText id="calendarExampleJavaCode1" label="Java" width="md-12" rowsCount="17">
                                <![CDATA[
public MultiValueMap<Entry<String, String>, Date> getScheduleOfSportEventsData() \{
    MultiValueMap<Entry<String, String>, Date> eventsCollection = new LinkedMultiValueMap<>();
    SimpleImmutableEntry<String, String> holidays = new SimpleImmutableEntry<>(HOLIDAYS, YELLOW);
    eventsCollection.add(holidays, conversionService.convert(CURRENT_YEAR_MONTH + "-01", LocalDate.class));
    eventsCollection.add(holidays, conversionService.convert(CURRENT_YEAR_MONTH + "-02", LocalDate.class));
    eventsCollection.add(holidays, conversionService.convert(CURRENT_YEAR_MONTH + "-23", LocalDate.class));

    SimpleImmutableEntry<String, String> selectedDates = new SimpleImmutableEntry<>(SELECTED_DATES, BLUE);
    eventsCollection.add(selectedDates, conversionService.convert(CURRENT_YEAR_MONTH + "-10", LocalDate.class));
    eventsCollection.add(selectedDates, conversionService.convert(CURRENT_YEAR_MONTH + "-20", LocalDate.class));

    SimpleImmutableEntry<String, String> gameEvents = new SimpleImmutableEntry<>(SPORT_EVENTS, RED);
    eventsCollection.add(gameEvents, conversionService.convert(CURRENT_YEAR_MONTH + "-05", LocalDate.class));
    eventsCollection.add(gameEvents, conversionService.convert(CURRENT_YEAR_MONTH + "-24", LocalDate.class));

    return eventsCollection;
\}
                                ]]>
                            </InputText>
                        </PanelGroup>
                    </Tab>

                    <Tab label="{$.fh.docs.component.calendar_actions}" id="_Form_TabContainer_Tab1_TabContainer_Tab4">
                        <PanelGroup width="md-12" label="{$.fh.docs.component.calendar_onchange_action}" id="_Form_TabContainer_Tab1_TabContainer_Tab4_PanelGroup1">
                            <Calendar label="{$.fh.docs.component.calendar_schedule_of_sport_events}" values="{scheduleOfSportEventsData}" value="{selectedSportDate}" onChange="-" id="_Form_TabContainer_Tab1_TabContainer_Tab4_PanelGroup1_Calendar1"/>
                            <Calendar label="{$.fh.docs.component.calendar_binded_schedule_of_sport_events}" values="{scheduleOfSportEventsData}" value="{selectedSportDate}" onChange="-" id="_Form_TabContainer_Tab1_TabContainer_Tab4_PanelGroup1_Calendar2"/>

                            <InputText id="calendarExampleCode4" label="XML" width="md-12" rowsCount="2">
                                <![CDATA[
<Calendar label="Schedule of sport events" values="\{scheduleOfSportEventsData\}" value="\{selectedSportDate\}" onChange="-"/>
<Calendar label="Binded schedule of sport events" values="\{scheduleOfSportEventsData\}" value="\{selectedSportDate\}" onChange="-"/>
                                ]]>
                            </InputText>
                        </PanelGroup>

                        <PanelGroup width="md-12" label="{$.fh.docs.component.calendar_with_multi_sizes}" id="_Form_TabContainer_Tab1_TabContainer_Tab4_PanelGroup2">
                            <Calendar label="{$.fh.docs.component.calendar_schedule_of_music_events}" values="{scheduleOfMusicEventsData}" value="{selectedMusicDate}" width="xs-2,sm-8,md-4,lg-10" onChange="-" id="_Form_TabContainer_Tab1_TabContainer_Tab4_PanelGroup2_Calendar"/>

                            <InputText id="calendarExampleCode5" label="XML" width="md-12" rowsCount="1">
                                <![CDATA[
<Calendar label="Schedule of music events" values="\{scheduleOfMusicEventsData\}" value="\{selectedMusicDate\}" width="xs-2,sm-8,md-4,lg-10" onChange="-"/>
                                ]]>
                            </InputText>
                        </PanelGroup>
                    </Tab>
                </TabContainer>
        </Tab>

        <Tab label="{$.fh.docs.component.attributes}" id="_Form_TabContainer_Tab2">
            <Table iterator="item" collection="{attributes}" id="_Form_TabContainer_Tab2_Table">
                <Column label="{$.fh.docs.component.attributes_identifier}" value="{item.name}" width="15" id="_Form_TabContainer_Tab2_Table_Column1"/>
                <Column label="{$.fh.docs.component.attributes_type}" value="{item.type}" width="15" id="_Form_TabContainer_Tab2_Table_Column2"/>
                <Column label="{$.fh.docs.component.attributes_boundable}" value="{item.boundable}" width="10" id="_Form_TabContainer_Tab2_Table_Column3"/>
                <Column label="{$.fh.docs.component.attributes_default_value}" value="{item.defaultValue}" width="20" id="_Form_TabContainer_Tab2_Table_Column4"/>
                <Column label="{$.fh.docs.component.attributes_description}" value="{item.description}" width="40" id="_Form_TabContainer_Tab2_Table_Column5"/>
            </Table>
        </Tab>
    </TabContainer>



   <Button id="pBack" label="[icon='fa fa-chevron-left'] {$.fh.docs.component.back}" onClick="backToFormComponentsList()"/>

</Form>