<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" formType="STANDARD">
    <PanelHeaderFhDP title="ExtendedFHML" onClick="close" width="md-12" />

    <PanelGroup width="md-12" label="Łamanie linii: [br&#8205;/]" >
        <OutputLabel width="md-12" value="Linia pierwsza[br/]Linia druga"/>
        <Spacer width="md-12" />
        <OutputLabel width="md-12" value="[code]&lt;OutputLabel width=&#34;md-12&#34; value=&#34;Linia pierwsza[br&#8205;/]Linia druga&#34;/&gt;[/code]"/>
    </PanelGroup>
    <PanelGroup width="md-12" label="Dodanie klasy stylu z poziomu FHML: [className='styl1,styl2'&#8205;]" >
        <OutputLabel width="md-12" value="[className='old-value']Styl wartości starej[/className]"/>
        <Spacer width="md-12" />
        <OutputLabel width="md-12" value="[code]&lt;OutputLabel width=&#34;md-12&#34; value=&#34;[className='old-value'&#8205;]Styl wartości starej[/className]&#34;/&gt;[/code]"/>
    </PanelGroup>
    <PanelGroup width="md-12" label="Dodanie klasy stylu z poziomu FHML: cząstkowe" >
        <OutputLabel width="md-12" value="Sample text with badge [className='tab-badge']{sampleNumber}[/className]"/>
        <Spacer width="md-12" />
        <OutputLabel width="md-12" value="[code]&lt;OutputLabel width=&#34;md-12&#34; value=&#34;Sample text with badge [className='tab-badge'&#8205;]{sampleNumber}[/className]&#34;/&gt;[/code]"/>
    </PanelGroup>
    <PanelGroup width="md-12" label="Użycie dekoratora [del&#8205;] lub [s&#8205;] (przekreślenie)" >
        <OutputLabel width="md-12" value="[del]To jest dekorator 'del'[/del]"/>
        <OutputLabel width="md-12" value="[s]To jest dekorator 's'[/s]"/>
        <Spacer width="md-12" />
        <OutputLabel width="md-12" value="[code]&lt;OutputLabel width=&#34;md-12&#34; value=&#34;[del&#8205;]To jest dekorator 'del'[/del]&#34;/&gt;[br/]&lt;OutputLabel width=&#34;md-12&#34; value=&#34;[s&#8205;]To jest dekorator 's'[/s]&#34;/&gt;[/code]"/>
    </PanelGroup>
    <PanelGroup width="md-12" label="Użycie dekoratora [mark&#8205;]" >
        <OutputLabel width="md-12" value="Ten tekst jest [mark]częściowo[/mark] podświetlony!"/>
        <OutputLabel width="md-12" value="[mark]Ten tekst jest cały podświetlony![/mark]"/>
        <Spacer width="md-12" />
        <OutputLabel width="md-12" value="[code]&lt;OutputLabel width=&#34;md-12&#34; value=&#34;Ten tekst jest [mark&#8205;]częściowo[/mark] podświetlony!&#34;/&gt;[br/]&lt;OutputLabel width=&#34;md-12&#34; value=&#34;[mark&#8205;]Ten tekst jest cały podświetlony![/mark]&#34;/&gt;[/code]"/>
    </PanelGroup>
    <PanelGroup width="md-12" label="Cytowanie za pomocą [blockquote&#8205;] lub [q&#8205;]" >
        <OutputLabel width="md-12" value="[blockquote]To jest cytat z 'blockquote'[/blockquote]"/>
        <OutputLabel width="md-12" value="[q]To jest cytat z 'q'[/q]"/>
        <Spacer width="md-12" />
        <OutputLabel width="md-12" value="[code]&lt;OutputLabel width=&#34;md-12&#34; value=&#34;[blockquote&#8205;]To jest cytat z 'blockquote'[/blockquote]&#34;/&gt;[br/]&lt;OutputLabel width=&#34;md-12&#34; value=&#34;[q&#8205;]To jest cytat z 'q'[/q]&#34;/&gt;[/code]"/>
    </PanelGroup>
    <PanelGroup width="md-12" label="Cytowanie ze stopką" >
        <OutputLabel width="md-12" value="[q]To jest cytat z 'q'[br/][bqFooter]ze [b]stopką[/b] 'bqFooter'[/bqFooter][/q]"/>
        <Spacer width="md-12" />
        <OutputLabel width="md-12" value="[code]&lt;OutputLabel width=&#34;md-12&#34; value=&#34;[q&#8205;]To jest cytat z 'q'[br/&#8205;][bqFooter&#8205;]ze stopką 'bqFooter'[/bqFooter][/q]&#34;/&gt;[/code]"/>
    </PanelGroup>
    <PanelGroup width="md-12" label="Tworzenie list ul/li" >
        <OutputLabel width="md-12" value="[ul][li]Element 1[/li][li]Element 2[/li][li]Element 3[/li][/ul]"/>
        <Spacer width="md-12" />
        <OutputLabel width="md-12" value="[code]&lt;OutputLabel width=&#34;md-12&#34; value=&#34;[ul&#8205;][li&#8205;]Element 1[/li][li&#8205;]Element 2[/li][li&#8205;]Element 3[/li][/ul][/code]"/>
    </PanelGroup>
    <PanelGroup width="md-12" label="portal" >
        <Button id="exampleButton" width="md-3" label="Info" style="info"/>
        <PanelGroup width="md-10" label="[className='d-inline-block,d-flex,p-2']This is button moved by portal: [portal='exampleButton'][/className]"/>
        <Spacer width="md-12" />
        <OutputLabel width="md-12" value="[code]&lt;Button id=&#34;exampleButton&#34; width=&#34;md-3&#34; label=&#34;Info&#34; style=&#34;info&#34;/&gt;[br/]&lt;PanelGroup width=&#34;md-10&#34; label=&#34;[className='d-inline-block,d-flex,p-2'&#8205;]This is button moved by portal: [portal='exampleButton'&#8205;][/className]&#34;/&gt;[/code]"/>
    </PanelGroup>
    <PanelGroup width="md-12" label="fhportal" >
        <Combo id="exampleButton_2" avalibility="EDIT" width="md-3" value="test" values="test"/>
        <PanelGroup width="md-10" label="[className='d-inline-block,d-flex,p-2']This is button moved by fhportal: [fhportal='v=2;id=exampleButton_2;classes=[test,col-md-5];wrapped=true;layer=2'][/className]"/>
        <Spacer width="md-12" />
        <OutputLabel width="md-12" value="[code]&lt;Button id=&#34;exampleButton&#34; width=&#34;md-3&#34; label=&#34;Info&#34; style=&#34;info&#34;/&gt;[br/]&lt;PanelGroup width=&#34;md-10&#34; label=&#34;[className='d-inline-block,d-flex,p-2'&#8205;]This is button moved by fhportal: [fhportal='id=exampleButton2;classes=[test,test2];wrapped=true;layer=2'&#8205;][/className]&#34;/&gt;[/code]"/>
    </PanelGroup>
    <PanelGroup width="md-12" label="Wcięcie paragrafu lub powtórzona spacja">
        <OutputLabel width="md-12" value="[nbsp='3']Some text[br/][br/]Some[nbsp='3']text[br/][br/][nbsp='1']This is one nbsp[br/][br/]" />
        <Spacer width="md-12" />
        <OutputLabel width="md-12" value="[code][nbsp=3&#8205;]Some text[/code][br/]OR[br/][code]Some[nbsp=3&#8205;]text[/code]"/>
    </PanelGroup>
    <PanelGroup width="md-12" label="Dodanie znaku specjalnego" >
        <OutputLabel width="md-12" value="Użycie symbolu 'pi' (&amp;#928): [unescape='928']"/>
        <OutputLabel width="md-12" value="Użycie symbolu 'pik' (&amp;#9824): [unescape='9824']"/>
        <Spacer width="md-12" />
        <OutputLabel width="md-12" value="[code]&lt;OutputLabel width=&#34;md-12&#34; value=&#34;Użycie symbolu 'pi' (&amp;#928): [unescape='928'&#8205;]&#34;/&gt;[br/]&lt;OutputLabel width=&#34;md-12&#34; value=&#34;Użycie symbolu 'pik' (&amp;#9824): [unescape='9824'&#8205;]&#34;/&gt;[/code]"/>
    </PanelGroup>

    <PanelGroup width="md-12" label="Rozszerzenie atrybutów" >
        <OutputLabel width="md-12" value="Użycie znacznika [extAttributes/&#8205;] rozszerza wartości atrybutów innych znaczników o znaki specjalne takie jak ';' , ':' itd."/>
        <Spacer width="md-12" />
    </PanelGroup>

    <PanelGroup width="md-12" label="Dodanie odsyłacza" >
        <OutputLabel width="md-12" value="[extAttributes/]Odsyłacz do [ahref='href=https://www.google.pl/;alt=Alt opis;target=_blank']Google[/ahref]"/>
        <Spacer width="md-12" />
        <OutputLabel width="md-12" value="[code]&lt;OutputLabel width=&#34;md-12&#34; value=&#34;[extAttributes/&#8205;]Odsyłacz do [ahref='href=https://www.google.pl/;alt=Alt opis;target=_blank'&#8205;]Google[/ahref]/&gt;[/code]"/>
    </PanelGroup>

    <PanelGroup width="md-12" label="Test of FHMLStandalone">
        <FHMLStandalone tag="p" content="[u][nbsp='3']This is [s]FHMLStandalone[/s] - the tag that contains [mark]tag[/mark] and [mark]content[/mark] only![/u]"/>
    </PanelGroup>
    <PanelGroup width="md-12" label="Menu group">
        <OutputLabel width="md-12" value="[code][className='menu-group--icon'][icon&#8205;='fa fa-book'][/className&#8205;][className='menu-group--name'] Examples[/className&#8205;][/code]"/>
    </PanelGroup>
    <PanelGroup width="md-12" label="Menu element">
        <OutputLabel width="md-12" value="[code][className='menu-position--short']H6[/className&#8205;][className='menu-position--name']Import Postal Declaration[/className&#8205;][/code]"/>
    </PanelGroup>
</Form>
