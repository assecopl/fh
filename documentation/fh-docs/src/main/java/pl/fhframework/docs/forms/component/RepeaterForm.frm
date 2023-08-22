<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="documentationHolder" label="{componentName}">
    <AvailabilityConfiguration>
        <ReadOnly>
            repeaterExample1Code,repeaterExample2Code,repeaterExample3Code,repeaterExample4Code,repeaterExample5Model,repeaterExample5Code,repeaterExample6Code,repeaterExample6Model
        </ReadOnly>
    </AvailabilityConfiguration>
    <OutputLabel width="md-12" value="{description}" id="_Form_OutputLabel"/>
    <Spacer width="md-12" height="25px" id="_Form_Spacer"/>
    <TabContainer id="_Form_TabContainer">
        <Tab label="{$.fh.docs.component.examples}" id="_Form_TabContainer_Tab1">
                <TabContainer id="_Form_TabContainer_Tab1_TabContainer">
                    <Tab label="{$.fh.docs.component.repeater_displaying_text_content}" id="_Form_TabContainer_Tab1_TabContainer_Tab1">
                        <PanelGroup width="md-12" label="{$.fh.docs.component.repeater_example_1_simple_use_one_collection_to_iterate_through}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup1">
                            <Repeater collection="{people}" iterator="person" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup1_Repeater">
                                <Group id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup1_Repeater_Group">
                                    <InputText width="md-2" value="{person$rowNo}" availability="VIEW" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup1_Repeater_Group_InputText1"/>
                                    <InputText width="md-2" value="{person.name}" availability="VIEW" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup1_Repeater_Group_InputText2"/>
                                    <InputText width="md-2" value="{person.surname}" availability="VIEW" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup1_Repeater_Group_InputText3"/>
                                    <InputText width="md-2" value="{person.city}" availability="VIEW" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup1_Repeater_Group_InputText4"/>
                                    <InputText width="md-2" value="{person.status}" availability="VIEW" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup1_Repeater_Group_InputText5"/>
                                    <InputText width="md-2" value="{person.gender}" availability="VIEW" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup1_Repeater_Group_InputText6"/>
                                </Group>
                            </Repeater>
                            <InputText id="repeaterExample1Code" label="{$.fh.docs.component.code}" width="md-12" rowsCount="5">
                                <![CDATA[![ESCAPE[<Repeater collection="{people}" iterator="person">
                                <Group>
                                    <InputText width="md-2" value="{person$rowNo}" availability="VIEW" />
                                    <InputText width="md-2" value="{person.name}" availability="VIEW" />
                                    <InputText width="md-2" value="{person.surname}" availability="VIEW" />
                                    <InputText width="md-2" value="{person.city}" availability="VIEW" />
                                    <InputText width="md-2" value="{person.status}" availability="VIEW" />
                                    <InputText width="md-2" value="{person.gender}" availability="VIEW" />
                                </Group>
                            </Repeater>]]]]>
                            </InputText>
                        </PanelGroup>
                        <PanelGroup width="md-12" label="{$.fh.docs.component.repeater_example_2_nested_repeaters}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup2">
                            <Button width="md-2" label="{$.fh.docs.component.repeater_add_student}" onClick="addStudent()" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup2_Button"/>
                            <Spacer width="md-12" height="25px" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup2_Spacer"/>
                            <Repeater collection="{students}" iterator="student" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup2_Repeater">
                                <PanelGroup width="md-6,sm-12" borderVisible="true" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup2_Repeater_PanelGroup">
                                    <InputText availability="VIEW" width="md-2" value="{student$rowNo}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup2_Repeater_PanelGroup_InputText1"/>
                                    <InputText availability="VIEW" width="md-5" value="{student.person.name}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup2_Repeater_PanelGroup_InputText2"/>
                                    <InputText availability="VIEW" width="md-5" value="{student.person.surname}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup2_Repeater_PanelGroup_InputText3"/>
                                    <Group width="md-12" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup2_Repeater_PanelGroup_Group1">
                                        <Button width="md-4" label="{$.fh.docs.component.repeater_add_classes}" onClick="addClasses({student})" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup2_Repeater_PanelGroup_Group1_Button"/>
                                    </Group>
                                    <Group width="md-12" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup2_Repeater_PanelGroup_Group2">
                                        <Repeater collection="{student.classes}" iterator="class" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup2_Repeater_PanelGroup_Group2_Repeater">
                                            <PanelGroup borderVisible="true" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup2_Repeater_PanelGroup_Group2_Repeater_PanelGroup">
                                                <Button width="md-4" label="{$.fh.docs.component.repeater_remove_classes}" onClick="removeClasses({class})" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup2_Repeater_PanelGroup_Group2_Repeater_PanelGroup_Button"/>
                                                <Repeater collection="{class.grades}" iterator="gradeIter" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup2_Repeater_PanelGroup_Group2_Repeater_PanelGroup_Repeater">
                                                    <Group width="md-12" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup2_Repeater_PanelGroup_Group2_Repeater_PanelGroup_Repeater_Group">
                                                        <InputText availability="VIEW" label="{$.fh.docs.component.repeater_class}" value="{class.className}" labelPosition="up" width="md-4" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup2_Repeater_PanelGroup_Group2_Repeater_PanelGroup_Repeater_Group_InputText1"/>
                                                        <InputText availability="VIEW" label="{$.fh.docs.component.repeater_teacher}" value="{class.teacher}" labelPosition="up" width="md-4" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup2_Repeater_PanelGroup_Group2_Repeater_PanelGroup_Repeater_Group_InputText2"/>
                                                        <InputText availability="VIEW" label="{$.fh.docs.component.repeater_grade}" value="{gradeIter.grade}" labelPosition="up" width="md-4" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup2_Repeater_PanelGroup_Group2_Repeater_PanelGroup_Repeater_Group_InputText3"/>
                                                    </Group>
                                                </Repeater>
                                            </PanelGroup>
                                        </Repeater>
                                    </Group>
                                </PanelGroup>
                            </Repeater>
                            <InputText id="repeaterExample2Code" label="{$.fh.docs.component.code}" width="md-12" rowsCount="5">
                                <![CDATA[![ESCAPE[<Repeater collection="{students}" iterator="student">
                                <PanelGroup width="md-6,sm-12" borderVisible="true">
                                    <InputText availability="VIEW" width="md-2" value="{student$rowNo}"/>
                                    <InputText availability="VIEW" width="md-5" value="{student.person.name}"/>
                                    <InputText availability="VIEW" width="md-5" value="{student.person.surname}"/>
                                    <Group width="md-12">
                                        <Button width="md-4" label="{$.fh.docs.component.repeater_add_classes}"
                                                onClick="addClasses({student})"/>
                                    </Group>
                                    <Group width="md-12">
                                        <Repeater collection="{student.classes}" iterator="class">
                                            <PanelGroup borderVisible="true">
                                                <Button width="md-4"
                                                        label="{$.fh.docs.component.repeater_remove_classes}"
                                                        onClick="removeClasses({class})"/>
                                                <Repeater collection="{class.grades}" iterator="gradeIter">
                                                    <Group width="md-12">
                                                        <InputText availability="VIEW" label="{$.fh.docs.component.repeater_class}" value="{class.className}" labelPosition="up" width="md-4" />
                                                        <InputText availability="VIEW" label="{$.fh.docs.component.repeater_teacher}" value="{class.teacher}" labelPosition="up" width="md-4" />
                                                        <InputText availability="VIEW" label="{$.fh.docs.component.repeater_grade}" value="{gradeIter.grade}" labelPosition="up" width="md-4" />
                                                    </Group>
                                                </Repeater>
                                            </PanelGroup>
                                        </Repeater>
                                    </Group>
                                </PanelGroup>
                            </Repeater>]]]]>
                            </InputText>
                        </PanelGroup>
                        <PanelGroup width="md-12" label="{$.fh.docs.component.repeater_example_3_nested_repeaters_regrouped}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup3">
                            <Repeater collection="{students}" iterator="student" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup3_Repeater">
                                <PanelGroup width="md-6,sm-12" borderVisible="true" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup3_Repeater_PanelGroup">
                                    <InputText availability="VIEW" width="md-6" value="{student.person.name}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup3_Repeater_PanelGroup_InputText1"/>
                                    <InputText availability="VIEW" width="md-6" value="{student.person.surname}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup3_Repeater_PanelGroup_InputText2"/>
                                    <Repeater collection="{student.classes}" iterator="class" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup3_Repeater_PanelGroup_Repeater">
                                        <Group id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup3_Repeater_PanelGroup_Repeater_Group">
                                            <InputText availability="VIEW" label="{$.fh.docs.component.repeater_class}" value="{class.className}" labelPosition="up" width="md-4" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup3_Repeater_PanelGroup_Repeater_Group_InputText1"/>
                                            <InputText availability="VIEW" label="{$.fh.docs.component.repeater_teacher}" value="{class.teacher}" labelPosition="up" width="md-4" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup3_Repeater_PanelGroup_Repeater_Group_InputText2"/>

                                            <PanelGroup width="md-12" label="{$.fh.docs.component.repeater_grades}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup3_Repeater_PanelGroup_Repeater_Group_PanelGroup">
                                                <Repeater collection="{class.grades}" iterator="gradeIter" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup3_Repeater_PanelGroup_Repeater_Group_PanelGroup_Repeater">
                                                    <InputText availability="VIEW" width="md-6" value="{gradeIter.grade}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup3_Repeater_PanelGroup_Repeater_Group_PanelGroup_Repeater_InputText1"/>
                                                    <InputText availability="VIEW" width="md-6" value="{gradeIter.description}" id="_Form_TabContainer_Tab1_TabContainer_Tab1_PanelGroup3_Repeater_PanelGroup_Repeater_Group_PanelGroup_Repeater_InputText2"/>
                                                </Repeater>
                                            </PanelGroup>
                                        </Group>
                                    </Repeater>
                                </PanelGroup>
                            </Repeater>
                        </PanelGroup>
                        <InputText id="repeaterExample3Code" label="{$.fh.docs.component.code}" width="md-12" rowsCount="5">
                            <![CDATA[![ESCAPE[<Repeater collection="{students}" iterator="student">
                                <PanelGroup width="md-6,sm-12" borderVisible="true">
                                    <InputText availability="VIEW" width="md-6" value="{student.person.name}"/>
                                    <InputText availability="VIEW" width="md-6" value="{student.person.surname}"/>
                                    <Repeater collection="{student.classes}" iterator="class">
                                        <Group>
                                            <InputText availability="VIEW" label="{$.fh.docs.component.repeater_class}" value="{class.className}" labelPosition="up" width="md-4" />
                                            <InputText availability="VIEW" label="{$.fh.docs.component.repeater_teacher}" value="{class.teacher}" labelPosition="up" width="md-4" />

                                            <PanelGroup width="md-12" label="{$.fh.docs.component.repeater_grades}">
                                                <Repeater collection="{class.grades}"
                                                          iterator="gradeIter">
                                                    <InputText availability="VIEW" width="md-6"  value="{gradeIter.grade}" />
                                                    <InputText availability="VIEW" width="md-6"  value="{gradeIter.description}" />
                                                </Repeater>
                                            </PanelGroup>
                                        </Group>
                                    </Repeater>
                                </PanelGroup>
                            </Repeater>]]]]>
                        </InputText>
                        <InputText id="repeaterExample4Code" label="{$.fh.docs.component.code}" width="md-12" rowsCount="5">
                            <![CDATA[![ESCAPE[<PanelGroup  width="md-12" label="Example 4. Same repeaters displayed another way">
    <Repeater collection="{students}" iterator="student">
        <Group>
            <OutputLabel width="md-1" value="{student$rowNo}"/>
            <OutputLabel width="md-1" value="Student: {student.person.name}

            "/>
            <OutputLabel width="md-1" value="{student.person.surname} "/>
            <PanelGroup>
                <Repeater collection="{student.classes}" iterator="class">
                    <OutputLabel width="md-1" value="{student$rowNo}.{class$rowNo}"/>
                    <OutputLabel width="md-1" value="Class: {class.className}"/>
                    <OutputLabel width="md-2" value=" Teacher: {class.teacher}"/>
                    <PanelGroup>
                        <Repeater collection="{class.grades}" iterator="gradeIter">
                            <Spacer width="md-1"/><OutputLabel width="md-11" value=" Grade: {gradeIter.grade} ({gradeIter.description})"/>
                        </Repeater>
                    </PanelGroup>
                </Repeater>
            </PanelGroup>
        </Group>
    </Repeater>
</PanelGroup>]]]]>
                        </InputText>
                    </Tab>
                    <Tab label="{$.fh.docs.component.repeater_user_interaction_books_example}" id="_Form_TabContainer_Tab1_TabContainer_Tab2">
                        <PanelGroup label="{$.fh.docs.component.repeater_list_of_books_basket}" id="_Form_TabContainer_Tab1_TabContainer_Tab2_PanelGroup1">
                            <Repeater id="bookInBasket" collection="{basket.books}" iterator="bookInBasket">
                                <Group width="md-12" id="_Form_TabContainer_Tab1_TabContainer_Tab2_PanelGroup1_Repeater_Group">
                                    <OutputLabel width="md-3" verticalAlign="top" value="{bookInBasket.book.author} - {bookInBasket.book.title}" id="_Form_TabContainer_Tab1_TabContainer_Tab2_PanelGroup1_Repeater_Group_OutputLabel1"/>
                                    <OutputLabel width="md-1" value="{bookInBasket.quantity}" verticalAlign="top" id="_Form_TabContainer_Tab1_TabContainer_Tab2_PanelGroup1_Repeater_Group_OutputLabel2"/>
                                    <Button width="md-2" label="{$.fh.docs.component.repeater_remove}" onClick="onDelFromBasket({bookInBasket})" id="_Form_TabContainer_Tab1_TabContainer_Tab2_PanelGroup1_Repeater_Group_Button"/>
                                </Group>
                            </Repeater>
                        </PanelGroup>
                        <PanelGroup id="booksGroup" label="{$.fh.docs.component.repeater_list_of_books}" borderVisible="true">
                            <Repeater collection="{books}" iterator="book" id="_Form_TabContainer_Tab1_TabContainer_Tab2_PanelGroup2_Repeater">
                                <PanelGroup width="md-6,sm-12" borderVisible="true" id="_Form_TabContainer_Tab1_TabContainer_Tab2_PanelGroup2_Repeater_PanelGroup">
                                    <InputText labelPosition="left" availability="VIEW" width="md-12" label="{$.fh.docs.component.repeater_title}" value="{book.title} " id="_Form_TabContainer_Tab1_TabContainer_Tab2_PanelGroup2_Repeater_PanelGroup_InputText1"/>
                                    <InputText labelPosition="left" availability="VIEW" width="md-12" label="{$.fh.docs.component.repeater_author}" value="{book.author} " id="_Form_TabContainer_Tab1_TabContainer_Tab2_PanelGroup2_Repeater_PanelGroup_InputText2"/>
                                    <Button width="md-12" label="{$.fh.docs.component.repeater_add_to_basket}" onClick="onAddToBasket({book})" id="_Form_TabContainer_Tab1_TabContainer_Tab2_PanelGroup2_Repeater_PanelGroup_Button"/>
                                </PanelGroup>
                            </Repeater>
                        </PanelGroup>

                        <InputText id="repeaterExample5Model" label="{$.fh.docs.component.repeater_data_model}" width="md-12" rowsCount="5">
                            <![CDATA[![ESCAPE[@AllArgsConstructor
@Getter @Setter
public class BasketItem {
    private Book book;
    private Integer quantity;
}

private List<Book> _books = Arrays.asList(
    new Book[]{
            new Book("Make me","Lee Child",""),
            new Book("The Bone Clocks","David Mitchell",""),
            new Book("The Girl with the Dragon Tattoo","Stieg Larsson",""),
            new Book("American Gods","Neil Gaiman","")
});


public static List<Book> getBooks() {
    return INSTANCE._books;
}]]]]>
                        </InputText>
                        <InputText id="repeaterExample5Code" label="{$.fh.docs.component.code}" width="md-12" rowsCount="5">
                            <![CDATA[![ESCAPE[<Repeater id="bookInBasket" collection="{basket.books}" iterator="bookInBasket">
<OutputLabel width="md-3" value="{bookInBasket.book.author} - {bookInBasket.book.title}"/>
<OutputLabel width="md-1" value="{bookInBasket.quantity}"/>
<Button label="Remove" onClick="onDelFromBasket({bookInBasket})"/>
</Repeater>
<Repeater collection="{books}" iterator="book">
<PanelGroup>
    <Group>
        <Group>
            <OutputLabel width="md-1" value="Title: "/>
            <OutputLabel width="md-1" value="{book.title} "/>
        </Group>
        <Group>
            <OutputLabel width="md-1" value="Author: "/>
            <OutputLabel width="md-2" value="{book.author} "/>
        </Group>
    </Group>
    <Group>
        <InputText width="md-4" rowsCount="5" label="Description" value="{book.description}"/>
    </Group>
    <Group>
        <Button width="md-2" label="Add to basket" onClick="onAddToBasket()"/>
    </Group>
</PanelGroup>
<PanelGroup label="Short description">
    <OutputLabel value="About &#8222;{book.title}&#8221; by {book.author}"/>
    <OutputLabel width="md-3" value="{book.description}"/>
</PanelGroup>
</Repeater>]]]]>
                        </InputText>
                    </Tab>
                    <Tab label="{$.fh.docs.component.repeater_user_interaction_currency_converter}" id="_Form_TabContainer_Tab1_TabContainer_Tab3">
                        <PanelGroup label="{$.fh.docs.component.repeater_simple_currency_converter}" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup">
                            <Repeater collection="{currencyCalcs}" iterator="cal" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup_Repeater">
                                <Group id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup_Repeater_Group">
                                    <InputText width="md-2" value="{cal.fromValue}" onInput="onConvertFrom({cal})" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup_Repeater_Group_InputText1"/>
                                    <OutputLabel width="md-1" value="{cal.from.currencyShort}" verticalAlign="top" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup_Repeater_Group_OutputLabel1"/>
                                    <OutputLabel width="md-2" value=" ({cal.from.currencyName})" verticalAlign="top" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup_Repeater_Group_OutputLabel2"/>
                                    <InputText width="md-2" value="{cal.toValue}" onInput="onConvertTo({cal})" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup_Repeater_Group_InputText2"/>
                                    <OutputLabel width="md-1" value="{cal.to.currencyShort}" verticalAlign="top" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup_Repeater_Group_OutputLabel3"/>
                                    <OutputLabel width="md-3" value=" ({cal.to.currencyName})" verticalAlign="top" id="_Form_TabContainer_Tab1_TabContainer_Tab3_PanelGroup_Repeater_Group_OutputLabel4"/>
                                </Group>
                            </Repeater>
                        </PanelGroup>
                        <InputText id="repeaterExample6Model" label="{$.fh.docs.component.repeater_data_model}" width="md-12" rowsCount="5">
                            <![CDATA[![ESCAPE[@AllArgsConstructor
@Getter @Setter
public static class CurrencyItem {
    String currencyName;
    String currencyShort;
}

@AllArgsConstructor
@Getter @Setter
public static class CurrencyCalc {
    private CurrencyItem from;
    private CurrencyItem to;
    private Double convertValue;
    private String fromValue="", toValue="";

    private void invert(){
        CurrencyItem temp = from;
        from = to;
        to = temp;
        convertValue = 1/convertValue;
    }
}

private static CurrencyItem PLN = new CurrencyItem("Polish zloty", "PLN");
private static CurrencyItem GBP = new CurrencyItem("Pound sterling", "GBP");
private static CurrencyItem EUR = new CurrencyItem("European euro", "EUR");
private static CurrencyItem USD = new CurrencyItem("United States dollar", "USD");

private static List<CurrencyCalc> currencyCalcs = Arrays.asList(
    currencyCalcGet(PLN, GBP, 0.1875), currencyCalcGet(PLN, EUR, 0.22), currencyCalcGet(PLN, USD, 0.23)
);]]]]>
                        </InputText>
                        <InputText id="repeaterExample6Code" label="{$.fh.docs.component.code}" width="md-12" rowsCount="5">
                            <![CDATA[![ESCAPE[<Repeater collection="{currencyCalcs}" iterator="cal">
                                <Group>
                                    <InputText width="md-2" value="{cal.fromValue}"
                                               onInput="onConvertFrom(this)"/>
                                    <OutputLabel width="md-1" value="{cal.from.currencyShort}" verticalAlign="top"/>
                                    <OutputLabel width="md-2" value=" ({cal.from.currencyName})" verticalAlign="top"/>
                                    <InputText width="md-2" value="{cal.toValue}"
                                               onInput="onConvertTo(this)"/>
                                    <OutputLabel width="md-1" value="{cal.to.currencyShort}" verticalAlign="top"/>
                                    <OutputLabel width="md-3" value=" ({cal.to.currencyName})" verticalAlign="top"/>
                                </Group>
                            </Repeater>]]]]>
                        </InputText>
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