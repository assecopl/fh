<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" id="ExternalReturnForm" label="Mechanizm powrotu z zewnętrznych stron do aplikacji FH." container="mainForm" formType="STANDARD">
    <AvailabilityConfiguration>
        <ReadOnly>annotationExample,remoteEventParameterExample,remoteEventExample,remoteEventExample2</ReadOnly>
    </AvailabilityConfiguration>
    <OutputLabel width="md-12" value="Mechanizm powrotu z zewnętrznych stron pozwala użytkownikom opuścić aplikację FH w celu wykonania jakiejś akcji,                         a podczas powrotu do aplikacji zostaje przywrócony aktualny uprzednio stosu przypadków użycia i wyświetlanych formularzy.                         Funkcjonalność w znaczący sposób ułatwia pracę z FH, pozwalając użytkownikom na swobodniejszą interakcję z zewnętrznymi serwisami." id="_Form_OutputLabel"/>
    <Spacer width="md-12" height="20px" id="_Form_Spacer"/>
    <PanelGroup label="Użycie" id="_Form_PanelGroup1">
        <Group id="_Form_PanelGroup1_Group1">
            <OutputLabel width="md-12" value="[b]Sesja HTTP[/b]" id="_Form_PanelGroup1_Group1_OutputLabel1"/>
            <OutputLabel width="md-12" value="Wpisanie głównego adresu aplikacji powoduje odtworzenie ostatniego stanu FH, pod warunkiem, że sesja HTTP nadal istnieje." id="_Form_PanelGroup1_Group1_OutputLabel2"/>
        </Group>
        <Group id="_Form_PanelGroup1_Group2">
            <OutputLabel width="md-12" value="[b]Rozszerzona adnotacja @Action[/b]" id="_Form_PanelGroup1_Group2_OutputLabel1"/>
            <OutputLabel width="md-12" value="Adnotacje na akcjach (@Action) zostały rozszerzone o atrybuty:" id="_Form_PanelGroup1_Group2_OutputLabel2"/>
            <OutputLabel width="md-12" value="- 'remote', typ boolean (true / false): określa, czy dana akcja jest dostępna z zewnątrz." id="_Form_PanelGroup1_Group2_OutputLabel3"/>
            <OutputLabel width="md-12" value="- 'defaultRemote', typ boolean (true / false): określa, czy dana akcja powinna się uruchomić domyślnie, bez podawania konkretnej nazwy akcji.                             Atrybut 'defaultRemote' może mieć przypisaną wartość 'true' tylko dla jednej akcji dla danego przypadku użycia." id="_Form_PanelGroup1_Group2_OutputLabel4"/>
            <OutputLabel width="md-12" value="Oba atrybuty mają przypisaną domyślną wartość 'false'." id="_Form_PanelGroup1_Group2_OutputLabel5"/>
            <InputText id="annotationExample" width="md-12" rowsCount="1">
                <![CDATA[
    @Action(remote = true, defaultRemote = true)
                        ]]>
            </InputText>
        </Group>
        <Group id="_Form_PanelGroup1_Group3">
            <OutputLabel width="md-12" value="[b]Parametr 'remoteEvent'[/b]" id="_Form_PanelGroup1_Group3_OutputLabel1"/>
            <OutputLabel width="md-12" value="To, która akcja z atrybutem 'remote' ustawionym na 'true' zostanie wywołana, określa parametr 'remoteEvent' przekazywany w adresie URL.                             Parametr ten nie jest obowiązkowy. Jeśli zostanie on pominięty, aplikacja zachowa się w jeden z dwóch możliwych sposóbów:" id="_Form_PanelGroup1_Group3_OutputLabel2"/>
            <OutputLabel width="md-12" value="- zostanie wywołana akcja z atrybutem 'defaultRemote', dla którego wartość została ustawiona na 'true'." id="_Form_PanelGroup1_Group3_OutputLabel3"/>
            <OutputLabel width="md-12" value="- żadna akcja nie zostanie wywołana." id="_Form_PanelGroup1_Group3_OutputLabel4"/>
            <InputText id="remoteEventParameterExample" width="md-12" rowsCount="1">
                <![CDATA[
    http://MAIN_APPLICATION_ADDRESS/?remoteEvent=EVENT_NAME
                        ]]>
            </InputText>
        </Group>
        <Group id="_Form_PanelGroup1_Group4">
            <OutputLabel width="md-12" value="[b]Złożone parametry[/b]" id="_Form_PanelGroup1_Group4_OutputLabel1"/>
            <OutputLabel width="md-12" value="Parametry mogą zostać przekazane w postaci Map&lt;String,String&gt;." id="_Form_PanelGroup1_Group4_OutputLabel2"/>
        </Group>
    </PanelGroup>
    <PanelGroup label="Przykłady" id="_Form_PanelGroup2">
        <Group id="_Form_PanelGroup2_Group1">
            <OutputLabel width="md-12" value="[b]Przekierowanie do zewnętrznej strony[/b]" id="_Form_PanelGroup2_Group1_OutputLabel1"/>
            <OutputLabel width="md-12" value="Aby zobaczyć, jak zachowuje się aplikacja po powrocie z zewnętrznej strony, należy wykonać następujące akcje: " id="_Form_PanelGroup2_Group1_OutputLabel2"/>
            <OutputLabel value="- przejście na zewnętrzną stronę, na przykład: " width="md-3" id="_Form_PanelGroup2_Group1_OutputLabel3"/>
            <Link url="http://www.google.pl" width="md-9" newWindow="false" id="_Form_PanelGroup2_Group1_Link"/>
            <OutputLabel width="md-12" value="- będąc na innej stronie, w pasku adresu podajemy główny adres aplikacji." id="_Form_PanelGroup2_Group1_OutputLabel4"/>
            <OutputLabel width="md-12" value="Należy pamiętać, że powrót do aplikacji FH wraz z odtworzeniem jej poprzedniego stanu ograniczony jest czasowo trwaniem sesji HTTP." id="_Form_PanelGroup2_Group1_OutputLabel5"/>
        </Group>
        <Group id="_Form_PanelGroup2_Group2">
            <OutputLabel width="md-12" value="[b]Powrót do aplikacji z parametrem 'remoteEvent'[/b]" id="_Form_PanelGroup2_Group2_OutputLabel1"/>
            <OutputLabel width="md-12" value="Aby zobaczyć, jak zachowuje się aplikacja po powrocie z zewnętrznej strony wraz z parametrem 'remoteEvent', należy wykonać następujące akcje: " id="_Form_PanelGroup2_Group2_OutputLabel2"/>
            <OutputLabel value="- przejście na zewnętrzną stronę, na przykład: " width="md-3" id="_Form_PanelGroup2_Group2_OutputLabel3"/>
            <Link url="http://www.google.pl" width="md-9" newWindow="false" id="_Form_PanelGroup2_Group2_Link"/>
            <OutputLabel width="md-12" value="- będąc na innej stronie, w pasku adresu podajemy główny adres aplikacji, a następnie dodajemy nazwę parametru wraz z akcją, którą chcemy wykonać. Na przykład: " id="_Form_PanelGroup2_Group2_OutputLabel4"/>
            <InputText id="remoteEventExample" width="md-12" rowsCount="1">
                <![CDATA[
    /?remoteEvent=name
                        ]]>
            </InputText>
        </Group>
        <Group id="_Form_PanelGroup2_Group3">
            <OutputLabel width="md-12" value="Akcję z parametrem 'remoteEvent' możemy także wywołać przekazując do niej dodatkowe dane. Parametr musi być zdefiniowany w danym przypadku użycia. Na przykład: " id="_Form_PanelGroup2_Group3_OutputLabel"/>
            <InputText id="remoteEventExample2" width="md-12" rowsCount="1">
                <![CDATA[
    /?remoteEvent=name&name=Jack
                        ]]>
            </InputText>
        </Group>
        <Group id="_Form_PanelGroup2_Group4">
            <OutputLabel width="md-12" value="[b]Przeładowanie strony[/b]" id="_Form_PanelGroup2_Group4_OutputLabel1"/>
            <OutputLabel width="md-12" value="Wykorzystanie mechanizmu rozwiązuje także problem przeładowania aplikacji. Naciśnięcie klawisza F5 lub przycisku 'odśwież' w oknie przeglądarki                             nie powoduje zgubienia stosu używanych obecnie przypadków użycia. Aplikacja zostaje załadowana powownie, a użytkownik może kontynuować czynności jakie zaplanował w FH." id="_Form_PanelGroup2_Group4_OutputLabel2"/>
        </Group>
    </PanelGroup>
</Form>