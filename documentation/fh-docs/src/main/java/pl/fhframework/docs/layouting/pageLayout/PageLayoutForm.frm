<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" id="PageLayoutForm" label="{$.fh.docs.layouting.application_layout}" container="mainForm" formType="STANDARD">
    <AvailabilityConfiguration>
        <ReadOnly>exampleCode1,htmlTemplateExample-FULL,appConfig-full,layoutUCExample-FULL,
            formTemplateExample-FULL,htmlTemplateExample-panels,layoutUCExample-panels,
            formTemplateExample-panels,appConfig-panels
        </ReadOnly>
    </AvailabilityConfiguration>
    <OutputLabel width="md-12" value="{$.fh.docs.layouting.infoLabel}." id="_Form_OutputLabel"/>
    <Spacer width="md-12" height="25px" id="_Form_Spacer"/>
    <PanelGroup label="{$.fh.docs.layouting.usage}" id="_Form_PanelGroup1">
        <OutputLabel width="md-12" value="Wykorzystanie mechanizmu zmiany layoutu w zależności od przypadku użycia wymaga wykonania kilku kroków." id="_Form_PanelGroup1_OutputLabel1"/>
        <OutputLabel width="md-12" value="- Przygotowanie pliku HTML z pożądanym layoutem dla przypadku użycia. W pliku HTML muszą znaleźć się kontenery &lt;div&gt; wraz         z przypisanymi do nich ID. ID kontenerów jest wykorzystywane w plikach .frm, w których poprzez atrybut 'container' wskazujemy, gdzie formatka ma zostać wyświetlona." id="_Form_PanelGroup1_OutputLabel2"/>
        <OutputLabel width="md-12" value="- Dodanie nazwy layoutu do pliku konfiguracyjnego: [b]application.properites[/b]. Nazwy szablonów muszą być oddzielone przecinkami.         W celu zachowania przejrzystości kodu nazwa layoutu w pliku konfiguracyjnym i nazwa szablonu HTML powinny być ze sobą zgodne." id="_Form_PanelGroup1_OutputLabel3"/>
        <OutputLabel width="md-12" value="- Do przypadku użycia, który ma zostać wyświetlony w układzie innym niż standardowy, należy dodać poniższą adnotację: " id="_Form_PanelGroup1_OutputLabel4"/>
        <InputText id="exampleCode1" width="md-12" rowsCount="1">
            <![CDATA[![ESCAPE[@UseCaseWithLayout(layout = "NAZWA_LAYOUTU")]]]]>
        </InputText>
        <OutputLabel width="md-12" value="gdzie NAZWA_LAYOUTU musi być zgodna z nazwą podaną w pliku konfiguracyjnym aplikacji." id="_Form_PanelGroup1_OutputLabel5"/>
        <OutputLabel width="md-12" value="- Każdy layout aplikacji powinien zawierać trzy podstawowe kontenery: NavbarForm, MenuForm oraz MainForm." id="_Form_PanelGroup1_OutputLabel6"/>
        <OutputLabel width="md-12" value="Podane poniżej przykłady layoutów zawierają opis kroków wraz z fragmentami kodów." id="_Form_PanelGroup1_OutputLabel7"/>
    </PanelGroup>
    <PanelGroup label="{$.fh.docs.layouting.examples}" id="_Form_PanelGroup2">
        <Accordion id="_Form_PanelGroup2_Accordion">
            <PanelGroup label="{$.fh.docs.layouting.fullWidthExample}" id="_Form_PanelGroup2_Accordion_PanelGroup1">
                <OutputLabel width="md-12" value="[b]Przygotowanie pliku HTML w pożądanym układzie widoku aplikacji[/b]" id="_Form_PanelGroup2_Accordion_PanelGroup1_OutputLabel1"/>
                <OutputLabel width="md-12" value="Do wyświetlenia aplikacji w layoucie innym niż domyślny potrzebny jest nowy szablon HTML.                     Należy przygotować w nim puste kontenery, w których wyświetlane będą poszczególne formularze.                     Nazwa  przekazana w formularzu w atrybucie 'container' musi być zgodna z ID kontenera w szablonie HTML." id="_Form_PanelGroup2_Accordion_PanelGroup1_OutputLabel2"/>
                <OutputLabel width="md-12" value="Przykładowy plik HTML: " id="_Form_PanelGroup2_Accordion_PanelGroup1_OutputLabel3"/>
                <InputText width="md-12" id="htmlTemplateExample-FULL" rowsCount="20">
                    <![CDATA[![ESCAPE[<!--
LAYOUT : fhframework.layout.templates=full
-->
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:th="http://www.thymeleaf.org"
      xmlns:sec="http://www.thymeleaf.org/thymeleaf-extras-springsecurity4">
<head>
</head>
<body>
<div th:fragment="layout">
    <nav class="navbar navbar-expand-md navbar-dark bg-dark d-none">
        <div class="row">
            <div class="navbar-logo">
                <a class="navbar-brand" href="#" target="_self">
                    <img th:src="${@environment.getProperty('fh.application.logo.url')}"
                         th:alt="${@environment.getProperty('fh.application.title')}"/>
                </a>
            </div>
            <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarTogglerDemo05"
                    aria-controls="navbarTogglerDemo05" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse navbar-controls" id="navbarTogglerDemo05">
                <div id="navbarForm" class="navbar-nav"></div>
            </div>
        </div>
    </nav>
    <div class="container-fluid pt-5">
        <div id="menuForm" class="d-none"
             th:classappend="${@environment.getProperty('fh.application.menu.hide') == 'true'}?hidden"></div>
        <div id="formDesigner" class="d-none">
            <div class="row">
                <div id="formDesignerToolbox" class="col-md-2"></div>
                <div id="formDesignerComponents" class="col"></div>
                <div id="formDesignerModelProperties" class="col d-none"></div>
                <div id="formDesignerProperties" class="col-md-2"></div>
            </div>
        </div>
        <div class="row">
            <div class="col-12">
                <div id="mainForm" class="mt-3"></div>
            </div>
        </div>
    </div>
</div>
</body>
</html>]]]]>    </InputText>
                <OutputLabel width="md-12" value="Przykład formularza wyświetlanego w pełnej szerokości. W atrybucie 'container' podajemy ID kontenera HTML, w którym formularz zostanie wyświetlony.                     W tym przypadku formularz zostanie wyświetlony w &lt;div&gt; o ID 'mainForm'." id="_Form_PanelGroup2_Accordion_PanelGroup1_OutputLabel4"/>
                <InputText width="md-12" id="formTemplateExample-FULL" rowsCount="9">
                    <![CDATA[![ESCAPE[<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<Form xmlns="http://fh.asseco.com/form/1.0" id="runningFhForm" label="{$.fh.docs.layouting.page.full}"
      container="mainForm" formType="STANDARD">
    <OutputLabel inlineStyle="font-size: 1.1em;" horizontalAlign="center" value="Przypadek użycia umieszczony w nowym szablonie"/>
    <Spacer height="50px"/>
    <Button id="pBack" label="[icon='fa fa-chevron-left'] {$.fh.docs.component.back}" horizontalAlign="center"
            onClick="backToLayouting()"/>
    <Spacer height="50px"/>
</Form>]]]]>
                </InputText>
                <OutputLabel width="md-12" value="[b]Dodanie nazwy layoutu do pliku konfiguracyjnego aplikacji[/b]" id="_Form_PanelGroup2_Accordion_PanelGroup1_OutputLabel5"/>
                <OutputLabel width="md-12" value="W pliku konfiguracyjnym application.properties znajduje się lista layoutów wykorzystywanych w FH. Po przygotowaniu szablonu HTML jego nazwę dopisujemy do poniższej listy:" id="_Form_PanelGroup2_Accordion_PanelGroup1_OutputLabel6"/>
                <InputText width="md-12" id="appConfig-full" rowsCount="2">
                <![CDATA[![ESCAPE[# Pipe of additional layout names used in FH. Each layout must have an HTML template file with a corresponding name
fhframework.layout.templates=full]]]]>
                </InputText>
                <OutputLabel width="md-12" value="Nazwy szablonów oddzielamy przecinkami." id="_Form_PanelGroup2_Accordion_PanelGroup1_OutputLabel7"/>
                <OutputLabel width="md-12" value="[b]Dodanie adnotacji do przypadku użycia[/b]" id="_Form_PanelGroup2_Accordion_PanelGroup1_OutputLabel8"/>
                <OutputLabel width="md-12" value="Do przypadku użycia, który ma zostać wyświetlony w układzie innym niż domyślny, należy dodać adnotację:                     '@UseCaseWithLayout(layout = 'NAZWA_LAYOUTU')' gdzie NAZWA_LAYOUTU odpowiada nazwie podanej w pliku konfiguracyjnym." id="_Form_PanelGroup2_Accordion_PanelGroup1_OutputLabel9"/>

                <InputText width="md-12" id="layoutUCExample-FULL" rowsCount="9">
                    <![CDATA[![ESCAPE[@UseCase
@UseCaseWithUrl(alias = "docs-page-layouting")
@UseCaseWithLayout(layout = "full")
//@SystemFunction(DocsSystemFunction.FH_DOCUMENTATION_VIEW)
public class FullWidthPageUC implements IInitialUseCase {
    @Override
    public void start() {
        showForm(FullWidthPageForm.class, null);
    }

    @Action
    public void backToLayouting(){
        runUseCase(PageLayoutUC.class, IUseCaseNoCallback.EMPTY);
    }
}
]]]]>
                </InputText>
                <OutputLabel width="md-12" value="[b]Zobacz przykład: [/b]" id="_Form_PanelGroup2_Accordion_PanelGroup1_OutputLabel10"/>
                <Button width="md-12" id="pBack" label="{$.fh.docs.layouting.fullWidth}" onClick="showFullWidthExample()"/>
            </PanelGroup>
            <PanelGroup label="{$.fh.docs.layouting.gridLayout}" id="_Form_PanelGroup2_Accordion_PanelGroup2">
                <OutputLabel width="md-12" value="[b]Przygotowanie pliku HTML w pożądanym układzie widoku aplikacji[/b]" id="_Form_PanelGroup2_Accordion_PanelGroup2_OutputLabel1"/>
                <OutputLabel width="md-12" value="Do wyświetlenia aplikacji w layoucie innym niż domyślny potrzebny jest nowy szablon HTML.                     Należy przygotować w nim puste kontenery, w których wyświetlane będą poszczególne formularze.                     Nazwa  przekazana w formularzu w atrybucie 'container' musi być zgodna z ID kontenera w szablonie HTML." id="_Form_PanelGroup2_Accordion_PanelGroup2_OutputLabel2"/>
                <OutputLabel width="md-12" value="Przykładowy plik HTML: " id="_Form_PanelGroup2_Accordion_PanelGroup2_OutputLabel3"/>
                <InputText width="md-12" id="htmlTemplateExample-panels" rowsCount="20">
                <![CDATA[![ESCAPE[<!--
LAYOUT : fhframework.layout.templates=panels
-->
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:th="http://www.thymeleaf.org"
      xmlns:sec="http://www.thymeleaf.org/thymeleaf-extras-springsecurity4">
<head>
</head>
<body>
<div th:fragment="layout">
    <nav class="navbar navbar-expand-md navbar-dark bg-dark d-none">
        <div class="row">
            <div class="navbar-logo">
                <a class="navbar-brand" href="#" target="_self">
                    <img th:src="${@environment.getProperty('fh.application.logo.url')}"
                         th:alt="${@environment.getProperty('fh.application.title')}"/>
                </a>
            </div>
            <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarTogglerDemo05"
                    aria-controls="navbarTogglerDemo05" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse navbar-controls" id="navbarTogglerDemo05">
                <div id="navbarForm" class="navbar-nav"></div>
            </div>
        </div>
    </nav>
    <div class="container-fluid">
        <div id="menuForm" class="col-sm-12 col-md-3 col-lg-3 col-xl-2 mt-3 d-none"
             th:classappend="${@environment.getProperty('fh.application.menu.hide') == 'true'}?hidden"></div>
        <div class="row">
            <div id="sideFormLeft" class="col-sm-12 col-md-3 col-lg-3 col-xl-2 mt-3"></div>
            <div id="mainForm" class="col-sm-12 col-md-6 col-lg-6 col-xl-8 mt-3"></div>
            <div id="sideFormRight" class="col-sm-12 col-md-3 col-lg-3 col-xl-2 mt-3"></div>
        </div>
        <div id="formDesigner" class="d-none">
            <div class="row">
                <div id="formDesignerToolbox" class="col-md-2"></div>
                <div id="formDesignerComponents" class="col"></div>
                <div id="formDesignerModelProperties" class="col d-none"></div>
                <div id="formDesignerProperties" class="col-md-2"></div>
            </div>
        </div>
    </div>
</div>
</body>
</html>]]]]>    </InputText>
                <OutputLabel width="md-12" value="Przykład formularza wyświetlanego w układzie trzech paneli. W atrybucie 'container' podajemy ID kontenera HTML, w którym formularz zostanie wyświetlony.                     W tym przypadku formularz zostanie wyświetlony w &lt;div&gt; o ID 'sideFormLeft'." id="_Form_PanelGroup2_Accordion_PanelGroup2_OutputLabel4"/>
                <InputText width="md-12" id="formTemplateExample-panels" rowsCount="5">
                    <![CDATA[![ESCAPE[<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<Form xmlns="http://fh.asseco.com/form/1.0" id="panelsLayoutSidePanelLeft" label="{$.fh.docs.layouting.page.grid}"
      container="sideFormLeft" formType="STANDARD">
    <OutputLabel value="Panel boczny lewy"/>
</Form>]]]]>
                </InputText>
                <OutputLabel width="md-12" value="[b]Dodanie nazwy layoutu do pliku konfiguracyjnego aplikacji[/b]" id="_Form_PanelGroup2_Accordion_PanelGroup2_OutputLabel5"/>
                <OutputLabel width="md-12" value="W pliku konfiguracyjnym application.properties znajduje się lista layoutów wykorzystywanych w FH. Po przygotowaniu szablonu HTML jego nazwę dopisujemy do poniższej listy:" id="_Form_PanelGroup2_Accordion_PanelGroup2_OutputLabel6"/>
                <InputText width="md-12" id="appConfig-panels" rowsCount="2">
                    <![CDATA[![ESCAPE[# Pipe of additional layout names used in FH. Each layout must have an HTML template file with a corresponding name
fhframework.layout.templates=full,panels]]]]>
                </InputText>
                <OutputLabel width="md-12" value="Nazwy szablonów oddzielamy przecinkami." id="_Form_PanelGroup2_Accordion_PanelGroup2_OutputLabel7"/>
                <OutputLabel width="md-12" value="[b]Dodanie adnotacji do przypadku użycia[/b]" id="_Form_PanelGroup2_Accordion_PanelGroup2_OutputLabel8"/>
                <OutputLabel width="md-12" value="Do przypadku użycia, który ma zostać wyświetlony w układzie innym niż domyślny, należy dodać adnotację:                     '@UseCaseWithLayout(layout = 'NAZWA_LAYOUTU')' gdzie NAZWA_LAYOUTU odpowiada nazwie podanej w pliku konfiguracyjnym." id="_Form_PanelGroup2_Accordion_PanelGroup2_OutputLabel9"/>

                <InputText width="md-12" id="layoutUCExample-panels" rowsCount="9">
                    <![CDATA[![ESCAPE[@UseCase
@UseCaseWithUrl(alias = "docs-page-layouting-panels")
@UseCaseWithLayout(layout = "panels")
//@SystemFunction(DocsSystemFunction.FH_DOCUMENTATION_VIEW)
public class PanelsLayoutUC implements IInitialUseCase {
    @Override
    public void start() {
        showForm(PanelsLayoutSidePanelLeft.class, null);
        showForm(PanelsLayoutFormMain.class, null);
        showForm(PanelsLayoutSidePanelRight.class, null);
    }

    @Action
    public void backToLayouting(){
        runUseCase(PageLayoutUC.class, IUseCaseNoCallback.EMPTY);
    }
}
]]]]>
                </InputText>
                <OutputLabel width="md-12" value="[b]Zobacz przykład: [/b]" id="_Form_PanelGroup2_Accordion_PanelGroup2_OutputLabel10"/>
                <Button id="gridLayoutBtn" label="{$.fh.docs.layouting.gridLayout}" onClick="showGridLayoutExample()"/>
            </PanelGroup>
        </Accordion>
    </PanelGroup>
</Form>