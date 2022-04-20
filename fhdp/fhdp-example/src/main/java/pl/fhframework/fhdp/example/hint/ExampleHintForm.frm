<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" formType="STANDARD" modalSize="REGULAR">
    <PanelHeaderFhDP title="Hints" info="Example" onClick="close" width="md-12"/>

    <PanelGroup label="Type: 'STATIC', ICON:'far fa-question-circle'">
        <InputText width="md-4" hint="Przykładowy tekst hinta" label="Hint 1" hintIcon="far fa-question-circle"
                   hintType="STATIC" availability="VIEW"/>
        <OutputLabel width="md-12" value="[code]&lt;InputText width=&#34;md-4&#34; hint=&#34;Przykładowy tekst hinta&#34; label=&#34;Hint 1&#34; hintIcon=&#34;far fa-question-circle&#34; hintType=&#34;STATIC&#34; availability=&#34;VIEW&#34;/&gt;[/code]"/>
    </PanelGroup>

    <PanelGroup label="Type: 'STATIC_LEFT', ICON:'fas fa-question'">
        <InputText width="md-4" hint="Przykładowy tekst hinta" label="Hint 2" hintIcon="fas fa-question"
                   hintType="STATIC_LEFT" availability="VIEW"/>
        <OutputLabel width="md-12" value="[code]&lt;InputText width=&#34;md-4&#34; hint=&#34;Przykładowy tekst hinta&#34; label=&#34;Hint 2&#34; hintIcon=&#34;fas fa-question&#34; hintType=&#34;STATIC_LEFT&#34; availability=&#34;VIEW&#34;/&gt;[/code]"/>
    </PanelGroup>

    <PanelGroup label="Type: 'STATIC_POPOVER', ICON:'far fa-question-circle'">
        <InputText width="md-4" hint="Przykładowy tekst hinta" label="Hint 3" hintIcon="far fa-question-circle"
                   hintType="STATIC_POPOVER" availability="VIEW" hintTitle="Tytuł hinta"/>
        <OutputLabel width="md-12" value="[code]&lt;InputText width=&#34;md-4&#34; hint=&#34;Przykładowy tekst hinta&#34; label=&#34;Hint 3&#34; hintIcon=&#34;far fa-question-circle&#34; hintType=&#34;STATIC_POPOVER&#34; availability=&#34;VIEW&#34; hintTitle=&#34;Tytuł hinta&#34;/&gt;[/code]"/>
    </PanelGroup>

    <PanelGroup label="Type: 'STATIC_POPOVER_LEFT', ICON:'fas fa-question'">
        <InputText width="md-4" hint="Przykładowy tekst hinta" label="Hint 4" hintIcon="fas fa-question"
                   hintType="STATIC_POPOVER_LEFT" availability="VIEW" hintTitle="Tytuł hinta"/>
        <OutputLabel width="md-12" value="[code]&lt;InputText width=&#34;md-4&#34; hint=&#34;Przykładowy tekst hinta&#34; label=&#34;Hint 4&#34; hintIcon=&#34;fas fa-question&#34; hintType=&#34;STATIC_POPOVER_LEFT&#34; availability=&#34;VIEW&#34; hintTitle=&#34;Tytuł hinta&#34;/&gt;[/code]"/>
    </PanelGroup>

    <PanelGroup label="Type: 'STANDARD'">
        <InputText width="md-4" hint="Przykładowy tekst hinta" label="Hint 5"
                   hintType="STANDARD" availability="VIEW"/>
        <OutputLabel width="md-12" value="[code]&lt;InputText width=&#34;md-4&#34; hint=&#34;Przykładowy tekst hinta&#34; label=&#34;Hint 5&#34; hintType=&#34;STANDARD&#34; availability=&#34;VIEW&#34;/&gt;[/code]"/>
    </PanelGroup>

    <PanelGroup label="Type: 'STANDARD_POPOVER'">
        <InputText width="md-4" hint="Przykładowy tekst hinta" label="Hint 6"
                   hintType="STANDARD_POPOVER" availability="VIEW" hintTitle="Tytuł hinta"/>
        <OutputLabel width="md-12" value="[code]&lt;InputText width=&#34;md-4&#34; hint=&#34;Przykładowy tekst hinta&#34; label=&#34;Hint 6&#34;hintType=&#34;STANDARD_POPOVER &#34;availability=&#34;VIEW&#34; hintTitle=&#34;Tytuł hinta&#34;/&gt;[/code]"/>
    </PanelGroup>

</Form>