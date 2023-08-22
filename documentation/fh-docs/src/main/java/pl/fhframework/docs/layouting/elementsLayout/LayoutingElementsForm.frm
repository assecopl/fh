<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" id="LayoutingElementsForm" label="{$.fh.docs.layouting.layouting_elements}" container="mainForm" formType="STANDARD">
    <AvailabilityConfiguration>
        <ReadOnly>exampleCode1,exampleCode2,exampleCode3,exampleCode4,exampleCode5,
            exampleCode6,exampleCode7,exampleCode8,exampleCode9,exampleCode10,exampleCode11
        </ReadOnly>
    </AvailabilityConfiguration>
    <OutputLabel width="md-12" inlineStyle="font-size: 1.1em;" value="Oś pionowa" id="_Form_OutputLabel1"/>
    <Spacer width="md-12" height="25px" id="_Form_Spacer1"/>
    <TabContainer id="_Form_TabContainer1">
        <Tab label="Na górze" id="_Form_TabContainer1_Tab1">
            <OutputLabel width="md-12" styleClasses="mb-3" value="Wszystkie elementy potomne ustawione będą przy górnej krawędzi wiersza" id="_Form_TabContainer1_Tab1_OutputLabel"/>
            <Row height="150px" inlineStyle="border: 1px solid #ced4da; margin-left: 0;" elementsVerticalAlign="top">
                <Button label="Test 1" id="_Form_TabContainer1_Tab1_Row_Button1"/>
                <Button label="Test 2" id="_Form_TabContainer1_Tab1_Row_Button2"/>
            </Row>
            <InputText id="exampleCode1" label="{$.fh.docs.component.code}" width="md-12" rowsCount="5">
                <![CDATA[![ESCAPE[<Row height="150px" elementsVerticalAlign="top">
                <Button label="Test 1" />
                <Button label="Test 2" />
</Row>]]]]>
            </InputText>
        </Tab>
        <Tab label="Na środku" id="_Form_TabContainer1_Tab2">
            <OutputLabel width="md-12" styleClasses="mb-3" value="Wszystkie elementy potomne ustawione będą na środku wysokości wiersza" id="_Form_TabContainer1_Tab2_OutputLabel"/>
            <Row height="150px" inlineStyle="border: 1px solid #ced4da; margin-left: 0;" elementsVerticalAlign="middle">
                <Button label="Test 1" id="_Form_TabContainer1_Tab2_Row_Button1"/>
                <Button label="Test 2" id="_Form_TabContainer1_Tab2_Row_Button2"/>
            </Row>
            <InputText id="exampleCode2" label="{$.fh.docs.component.code}" width="md-12" rowsCount="5">
                <![CDATA[![ESCAPE[<Row height="150px" elementsVerticalAlign="middle">
                <Button label="Test 1" />
                <Button label="Test 2" />
</Row>]]]]>
            </InputText>
        </Tab>
        <Tab label="Na dole" id="_Form_TabContainer1_Tab3">
            <OutputLabel width="md-12" styleClasses="mb-3" value="Wszystkie elementy potomne ustawione będą przy dolnej krawędzi wiersza" id="_Form_TabContainer1_Tab3_OutputLabel"/>
            <Row height="150px" inlineStyle="border: 1px solid #ced4da; margin-left: 0;" elementsVerticalAlign="bottom">
                <Button label="Test 1" id="_Form_TabContainer1_Tab3_Row_Button1"/>
                <Button label="Test 2" id="_Form_TabContainer1_Tab3_Row_Button2"/>
            </Row>
            <InputText id="exampleCode11" label="{$.fh.docs.component.code}" width="md-12" rowsCount="5">
                <![CDATA[![ESCAPE[<Row height="150px" elementsVerticalAlign="bottom">
                <Button label="Test 1" />
                <Button label="Test 2" />
</Row>]]]]>
            </InputText>
        </Tab>
        <Tab label="Wyjątki" id="_Form_TabContainer1_Tab4">
            <OutputLabel width="md-12" styleClasses="mb-3" value="Pomimo ustawionego atrybutu &quot;elementsVerticalAlign&quot; element potomny ma możliwość indywidualnego ustawienia swojej pozyci poprzez użycie atrybutu &quot;verticalAlign&quot;." id="_Form_TabContainer1_Tab4_OutputLabel"/>
            <Row height="150px" inlineStyle="border: 1px solid #ced4da; margin-left: 0;" elementsVerticalAlign="bottom">
                <Button label="Test 1" id="_Form_TabContainer1_Tab4_Row_Button1"/>
                <Button verticalAlign="middle" label="Test 2" id="_Form_TabContainer1_Tab4_Row_Button2"/>
                <Button label="Test 3" id="_Form_TabContainer1_Tab4_Row_Button3"/>
            </Row>
            <InputText id="exampleCode3" label="{$.fh.docs.component.code}" width="md-12" rowsCount="5">
                <![CDATA[![ESCAPE[<Row height="150px" elementsVerticalAlign="bottom">
                <Button label="Test 1"/>
                <Button verticalAlign="middle" label="Test 2"/>
                <Button label="Test 3"/>
</Row>]]]]>
            </InputText>
        </Tab>
    </TabContainer>

    <Spacer width="md-12" height="25px" id="_Form_Spacer2"/>
    <OutputLabel width="md-12" inlineStyle="font-size: 1.1em;" value="Oś pozioma" id="_Form_OutputLabel2"/>
    <Spacer width="md-12" height="25px" id="_Form_Spacer3"/>
    <TabContainer id="_Form_TabContainer2">
        <Tab label="Po lewej" id="_Form_TabContainer2_Tab1">
            <OutputLabel width="md-12" styleClasses="mb-3" value="Wszystkie elementy potomne ustawione będą przy lewej krawędzi wiersza" id="_Form_TabContainer2_Tab1_OutputLabel"/>
            <Row inlineStyle="border: 1px solid #ced4da; margin-left: 0;" elementsHorizontalAlign="LEFT">
                <Button label="Test 1" id="_Form_TabContainer2_Tab1_Row_Button1"/>
                <Button label="Test 2" id="_Form_TabContainer2_Tab1_Row_Button2"/>
            </Row>
            <InputText id="exampleCode4" label="{$.fh.docs.component.code}" width="md-12" rowsCount="5">
                <![CDATA[![ESCAPE[<Row elementsHorizontalAlign="LEFT">
                <Button label="Test 1" />
                <Button label="Test 2" />
</Row>]]]]>
            </InputText>
        </Tab>
        <Tab label="Na środku" id="_Form_TabContainer2_Tab2">
            <OutputLabel width="md-12" styleClasses="mb-3" value="Wszystkie elementy potomne ustawione będą na środku wiersza w osi poziomej" id="_Form_TabContainer2_Tab2_OutputLabel"/>
            <Row inlineStyle="border: 1px solid #ced4da; margin-left: 0;" elementsHorizontalAlign="CENTER">
                <Button label="Test 1" id="_Form_TabContainer2_Tab2_Row_Button1"/>
                <Button label="Test 2" id="_Form_TabContainer2_Tab2_Row_Button2"/>
            </Row>
            <InputText id="exampleCode5" label="{$.fh.docs.component.code}" width="md-12" rowsCount="5">
                <![CDATA[![ESCAPE[<Row elementsHorizontalAlign="CENTER">
                <Button label="Test 1" />
                <Button label="Test 2" />
</Row>]]]]>
            </InputText>
        </Tab>
        <Tab label="Po prawej" id="_Form_TabContainer2_Tab3">
            <OutputLabel width="md-12" styleClasses="mb-3" value="Wszystkie elementy potomne ustawione będą przy prawej krawędzi wiersza" id="_Form_TabContainer2_Tab3_OutputLabel"/>
            <Row inlineStyle="border: 1px solid #ced4da; margin-left: 0;" elementsHorizontalAlign="RIGHT">
                <Button label="Test 1" id="_Form_TabContainer2_Tab3_Row_Button1"/>
                <Button label="Test 2" id="_Form_TabContainer2_Tab3_Row_Button2"/>
            </Row>
            <InputText id="exampleCode6" label="{$.fh.docs.component.code}" width="md-12" rowsCount="5">
                <![CDATA[![ESCAPE[<Row elementsHorizontalAlign="RIGHT">
                <Button label="Test 1" />
                <Button label="Test 2" />
</Row>]]]]>
            </InputText>
        </Tab>
    </TabContainer>

    <Spacer width="md-12" height="25px" id="_Form_Spacer4"/>
    <OutputLabel width="md-12" inlineStyle="font-size: 1.1em;" value="Równomierne rozmieczenie" id="_Form_OutputLabel3"/>
    <Spacer width="md-12" height="25px" id="_Form_Spacer5"/>
    <TabContainer id="_Form_TabContainer3">
        <Tab label="Przykład 1" id="_Form_TabContainer3_Tab1">
            <OutputLabel width="md-12" styleClasses="mb-3" value="Elementy potomne będą rozmieszczone równomiernie poprzez podzielenie dostępnej wolnej przestrzeni w wierszu tak, aby po lewej i prawej stronie, każdego elementu z osobna, znajdował się taki sam margines." id="_Form_TabContainer3_Tab1_OutputLabel"/>
            <Row inlineStyle="border: 1px solid #ced4da; margin-left: 0;" elementsHorizontalAlign="AROUND">
                <Button label="Test 1" id="_Form_TabContainer3_Tab1_Row_Button1"/>
                <Button label="Test 2" id="_Form_TabContainer3_Tab1_Row_Button2"/>
                <Button label="Test 3" id="_Form_TabContainer3_Tab1_Row_Button3"/>
            </Row>
            <InputText id="exampleCode7" label="{$.fh.docs.component.code}" width="md-12" rowsCount="5">
                <![CDATA[![ESCAPE[<Row elementsHorizontalAlign="AROUND">
                <Button label="Test 1" />
                <Button label="Test 2" />
                <Button label="Test 3" />
</Row>]]]]>
            </InputText>
        </Tab>
        <Tab label="Przykład 2" id="_Form_TabContainer3_Tab2">
            <OutputLabel width="md-12" styleClasses="mb-3" value="Elementy potomne będą rozmieszczone równomiernie poprzez podzielenie dostępnej wolnej przestrzeni w wierszu tak, aby po lewej i prawej stronie, każdego elementu z osobna, znajdował się taki sam margines z wyjątkiem pierwszego i ostatniego elementu." id="_Form_TabContainer3_Tab2_OutputLabel"/>
            <Row inlineStyle="border: 1px solid #ced4da; margin-left: 0;" elementsHorizontalAlign="BETWEEN">
                <Button label="Test 1" id="_Form_TabContainer3_Tab2_Row_Button1"/>
                <Button label="Test 2" id="_Form_TabContainer3_Tab2_Row_Button2"/>
                <Button label="Test 3" id="_Form_TabContainer3_Tab2_Row_Button3"/>
            </Row>
            <InputText id="exampleCode8" label="{$.fh.docs.component.code}" width="md-12" rowsCount="5">
                <![CDATA[![ESCAPE[<Row elementsHorizontalAlign="BETWEEN">
                <Button label="Test 1" />
                <Button label="Test 2" />
                <Button label="Test 3"/>
</Row>]]]]>
            </InputText>
        </Tab>
    </TabContainer>

    <Spacer width="md-12" height="25px" id="_Form_Spacer6"/>
    <OutputLabel width="md-12" inlineStyle="font-size: 1.1em;" value="Elementy o stałej szerokości" id="_Form_OutputLabel4"/>
    <Spacer width="md-12" height="25px" id="_Form_Spacer7"/>
    <TabContainer id="_Form_TabContainer4">
        <Tab label="Przykład 1" id="_Form_TabContainer4_Tab1">
            <OutputLabel width="md-12" styleClasses="mb-3" value="Wewnątrz &lt;Row&gt; możliwe jest korzystanie z elementów o stałej szerokości wyrażonej poprzez atrybut &quot;width&quot;. Przykład pokazuje równomierne rozmieszczenie w osi poziomej elementów o różnych szerokościach w jednym wierszu." id="_Form_TabContainer4_Tab1_OutputLabel"/>
            <Row inlineStyle="border: 1px solid #ced4da; margin-left: 0;" elementsHorizontalAlign="BETWEEN">
                <Button label="1" width="70px" id="_Form_TabContainer4_Tab1_Row_Button1"/>
                <Button label="2" width="70px" id="_Form_TabContainer4_Tab1_Row_Button2"/>
                <Button label="3" width="150px" id="_Form_TabContainer4_Tab1_Row_Button3"/>
                <Button label="4" width="70px" id="_Form_TabContainer4_Tab1_Row_Button4"/>
                <Button label="5" width="70px" id="_Form_TabContainer4_Tab1_Row_Button5"/>
                <Button label="6" width="70px" id="_Form_TabContainer4_Tab1_Row_Button6"/>
                <Button label="7" width="70px" id="_Form_TabContainer4_Tab1_Row_Button7"/>
            </Row>
            <InputText id="exampleCode10" label="{$.fh.docs.component.code}" width="md-12" rowsCount="5">
                <![CDATA[![ESCAPE[<Row elementsHorizontalAlign="BETWEEN">
            <Button label="1" width="70px"/>
            <Button label="2" width="70px"/>
            <Button label="3" width="150px"/>
            <Button label="4" width="70px"/>
            <Button label="5" width="70px"/>
            <Button label="6" width="70px"/>
            <Button label="7" width="70px"/>
</Row>]]]]>
            </InputText>
        </Tab>
        <Tab label="Przykład 2" id="_Form_TabContainer4_Tab2">
            <OutputLabel width="md-12" styleClasses="mb-3" value="Wewnątrz &lt;Row&gt; możliwe jest korzystanie z elementów o stałej szerokości wyrażonej poprzez atrybut &quot;width&quot;. Przykład pokazuje równomierne rozmieszczenie w osi poziomej elementów o różnych szerokościach w jednym wierszu za pomocą atrybutu &quot;horizontalAlign&quot; na każdym z elementów potomnych z osobna." id="_Form_TabContainer4_Tab2_OutputLabel"/>
            <Row inlineStyle="border: 1px solid #ced4da; margin-left: 0;">
                <Button label="Test 1" horizontalAlign="left" width="100px" id="_Form_TabContainer4_Tab2_Row_Button1"/>
                <Button label="Test 2" horizontalAlign="center" width="100px" id="_Form_TabContainer4_Tab2_Row_Button2"/>
                <Button label="Test 3" horizontalAlign="right" width="250px" id="_Form_TabContainer4_Tab2_Row_Button3"/>
            </Row>
            <InputText id="exampleCode9" label="{$.fh.docs.component.code}" width="md-12" rowsCount="5">
                <![CDATA[![ESCAPE[<Row>
            <Button label="Test 1" horizontalAlign="left" width="100px"/>
            <Button label="Test 2" horizontalAlign="center" width="100px"/>
            <Button label="Test 3" horizontalAlign="right" width="250px"/>
</Row>]]]]>
            </InputText>
        </Tab>
    </TabContainer>
    <Spacer width="md-12" height="25px" id="_Form_Spacer8"/>
</Form>