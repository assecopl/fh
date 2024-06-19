<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="documentationHolder" label="{$.fh.docs.fhml_fh_markup_language}">
    <AvailabilityConfiguration>
        <ReadOnly>fhmlExample1,fhmlExample2,fhmlExample3,fhmlExample4,fhmlExample5,fhmlExample6,fhmlExample7</ReadOnly>
    </AvailabilityConfiguration>

    <OutputLabel width="md-12" value="{$.fh.docs.fhml_fh_markup_language_is_set_of_tags}" id="_Form_OutputLabel"/>
    <PanelGroup width="md-12" label="{$.fh.docs.fhml_basic_font_transformations}" id="_Form_PanelGroup1">
        <Row>
            <PanelGroup styleClasses="border" width="md-6" label="{$.fh.docs.fhml_bold}" id="_Form_PanelGroup1_Row1_PanelGroup1">
                <InputText width="md-12" id="fhmlExample1" value="{boldExample}" onInput="-"/>
                <OutputLabel width="md-12" value="{boldExample}" id="_Form_PanelGroup1_Row1_PanelGroup1_OutputLabel"/>
            </PanelGroup>
            <PanelGroup styleClasses="border" width="md-6" label="{$.fh.docs.fhml_italic}" id="_Form_PanelGroup1_Row1_PanelGroup2">
                <InputText width="md-12" id="fhmlExample2" value="{italicExample}" onInput="-"/>
                <OutputLabel width="md-12" value="{italicExample}" id="_Form_PanelGroup1_Row1_PanelGroup2_OutputLabel"/>
            </PanelGroup>
        </Row>
        <Row>
            <PanelGroup styleClasses="border" width="md-6" label="{$.fh.docs.fhml_underline}" id="_Form_PanelGroup1_Row2_PanelGroup1">
                <InputText width="md-12" id="fhmlExample3" value="{underlineExample}" onInput="-"/>
                <OutputLabel width="md-12" value="{underlineExample}" id="_Form_PanelGroup1_Row2_PanelGroup1_OutputLabel"/>
            </PanelGroup>
            <PanelGroup styleClasses="border" width="md-6" label="{$.fh.docs.fhml_size}" id="_Form_PanelGroup1_Row2_PanelGroup2">
                <OutputLabel width="md-12" value="{$.fh.docs.fhml_size_takes_pixels_as_a_parameter}" id="_Form_PanelGroup1_Row2_PanelGroup2_OutputLabel1"/>
                <InputText width="md-12" id="fhmlExample4" value="{sizeExample}" onInput="-"/>
                <OutputLabel width="md-12" value="{sizeExample}" id="_Form_PanelGroup1_Row2_PanelGroup2_OutputLabel2"/>
            </PanelGroup>
        </Row>
        <Row>
            <PanelGroup styleClasses="border" width="md-6" label="{$.fh.docs.fhml_color}" id="_Form_PanelGroup1_Row3_PanelGroup1">
                <OutputLabel width="md-12" value="{$.fh.docs.fhml_color_can_be_specified_same_as_in_css}" id="_Form_PanelGroup1_Row3_PanelGroup1_OutputLabel1"/>
                <InputText width="md-12" id="fhmlExample5" value="{colorExample}" onInput="-"/>
                <OutputLabel width="md-12" value="{colorExample}" id="_Form_PanelGroup1_Row3_PanelGroup1_OutputLabel2"/>
            </PanelGroup>
            <PanelGroup styleClasses="border" width="md-6" label="{$.fh.docs.fhml_line_through}" id="_Form_PanelGroup1_Row3_PanelGroup2">
                <InputText width="md-12" id="fhmlExample9" value="{lineThroughExample}" onInput="-"/>
                <OutputLabel width="md-12" value="{lineThroughExample}" id="_Form_PanelGroup1_Row3_PanelGroup2_OutputLabel"/>
            </PanelGroup>
        </Row>
        <Row>
            <!--    New 30.09.2020 -->
            <PanelGroup styleClasses="border" width="md-6" label="{$.fh.docs.fhml_highlight}" id="_Form_PanelGroup1_Row4_PanelGroup1">
                <InputText availability="VIEW" width="md-12" value="{highlightExample}" onInput="-" id="_Form_PanelGroup1_Row4_PanelGroup1_InputText"/>
                <OutputLabel width="md-12" value="{highlightExample}" id="_Form_PanelGroup1_Row4_PanelGroup1_OutputLabel"/>
            </PanelGroup>
            <PanelGroup styleClasses="border" width="md-6" label="{$.fh.docs.fhml_blockquote}" id="_Form_PanelGroup1_Row4_PanelGroup2">
                <InputText availability="VIEW" width="md-12" value="{blockquoteExample}" onInput="-" id="_Form_PanelGroup1_Row4_PanelGroup2_InputText"/>
                <OutputLabel width="md-12" value="{blockquoteExample}" id="_Form_PanelGroup1_Row4_PanelGroup2_OutputLabel"/>
            </PanelGroup>
        </Row>
        <Row>
            <PanelGroup styleClasses="border" width="md-6" label="{$.fh.docs.fhml_blockquote_footer}" id="_Form_PanelGroup1_Row5_PanelGroup1">
                <InputText availability="VIEW" width="md-12" value="{bqFooterExample}" onInput="-" id="_Form_PanelGroup1_Row5_PanelGroup1_InputText"/>
                <OutputLabel width="md-12" value="{bqFooterExample}" id="_Form_PanelGroup1_Row5_PanelGroup1_OutputLabel"/>
            </PanelGroup>
            <PanelGroup styleClasses="border" width="md-6" label="{$.fh.docs.fhml_new_line}" id="_Form_PanelGroup1_Row5_PanelGroup2">
                <InputText availability="VIEW" width="md-12" value="{brExample}" onInput="-" id="_Form_PanelGroup1_Row5_PanelGroup2_InputText"/>
                <OutputLabel width="md-12" value="{brExample}" id="_Form_PanelGroup1_Row5_PanelGroup2_OutputLabel"/>
            </PanelGroup>
        </Row>
        <Row>
            <PanelGroup styleClasses="border" width="md-6" label="{$.fh.docs.fhml_classname}" id="_Form_PanelGroup1_Row6_PanelGroup1">
                <InputText availability="VIEW" width="md-12" value="{classNameExample}" onInput="-" id="_Form_PanelGroup1_Row6_PanelGroup1_InputText"/>
                <OutputLabel width="md-12" value="{classNameExample}" id="_Form_PanelGroup1_Row6_PanelGroup1_OutputLabel"/>
            </PanelGroup>
            <PanelGroup styleClasses="border" width="md-6" label="{$.fh.docs.fhml_strikethrough}" id="_Form_PanelGroup1_Row6_PanelGroup2">
                <InputText availability="VIEW" width="md-12" value="{delExample}" onInput="-" id="_Form_PanelGroup1_Row6_PanelGroup2_InputText"/>
                <OutputLabel width="md-12" value="{delExample}" id="_Form_PanelGroup1_Row6_PanelGroup2_OutputLabel"/>
            </PanelGroup>
        </Row>
        <Row>
                <PanelGroup width="md-6" label="{$.fh.docs.fhml_code}" id="_Form_PanelGroup1_Row7_PanelGroup1">
                    <InputText availability="VIEW" width="md-12" value="{codeExample}" onInput="-" id="_Form_PanelGroup1_Row7_PanelGroup1_InputText"/>
                    <OutputLabel width="md-12" value="{codeExample}" id="_Form_PanelGroup1_Row7_PanelGroup1_OutputLabel"/>
            </PanelGroup>
            <PanelGroup width="md-6" label="{$.fh.docs.fhml_list}" id="_Form_PanelGroup1_Row7_PanelGroup2">
                    <InputText availability="VIEW" width="md-12" value="{listExample}" onInput="-" id="_Form_PanelGroup1_Row7_PanelGroup2_InputText"/>
                    <OutputLabel width="md-12" value="{listExample}" id="_Form_PanelGroup1_Row7_PanelGroup2_OutputLabel"/>
            </PanelGroup>
        </Row>
    </PanelGroup>
    <PanelGroup width="md-12" label="{$.fh.docs.fhml_mixed_transformations}" id="_Form_PanelGroup2">
        <OutputLabel width="md-12" value="{$.fh.docs.fhml_more_than_one_tag_can_me_used_at_a_time}" id="_Form_PanelGroup2_OutputLabel"/>
        <PanelGroup width="md-12" label="{$.fh.docs.fhml_mixed_example} 1" id="_Form_PanelGroup2_PanelGroup1">
            <InputText width="md-12" id="fhmlExample6" value="{mixedExample1}" onInput="-"/>
            <OutputLabel width="md-12" value="{mixedExample1}" id="_Form_PanelGroup2_PanelGroup1_OutputLabel"/>
        </PanelGroup>
        <PanelGroup width="md-12" label="{$.fh.docs.fhml_mixed_example} 2" id="_Form_PanelGroup2_PanelGroup2">
            <InputText width="md-12" id="fhmlExample7" value="{mixedExample2}" onInput="-"/>
            <OutputLabel width="md-12" value="{mixedExample2}" id="_Form_PanelGroup2_PanelGroup2_OutputLabel"/>
        </PanelGroup>
    </PanelGroup>
    <PanelGroup width="md-12" label="{$.fh.docs.fhml_text_addons}" id="_Form_PanelGroup3">
        <OutputLabel width="md-12" value="{$.fh.docs.fhml_things_that_can_be_added_to_label}" id="_Form_PanelGroup3_OutputLabel"/>
        <PanelGroup width="md-12" label="{$.fh.docs.fhml_icon}" id="_Form_PanelGroup3_PanelGroup">
            <InputText width="md-12" id="fhmlExample8" value="{addonExample1}" onInput="-"/>
            <OutputLabel width="md-12" value="{addonExample1}" id="_Form_PanelGroup3_PanelGroup_OutputLabel"/>
        </PanelGroup>
    </PanelGroup>
</Form>