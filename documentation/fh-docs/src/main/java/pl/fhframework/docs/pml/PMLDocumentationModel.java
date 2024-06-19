package pl.fhframework.docs.pml;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Kamil Trusiak on 17.01.2017.
 */
@Getter
@Setter
public class PMLDocumentationModel {
    private String boldExample = "This is [b]bold[/b] text";
    private String italicExample = "This text's style is [i]italic[/i]";
    private String underlineExample = "This text has an [u]underline[/u]";
    private String lineThroughExample = "This text has an [lt]line through[/lt]";
    private String sizeExample = "This text is [size='24']quite big[/size]";
    private String colorExample = "This is [color='red']red[/color] and this [color='#ff0000']too[/color]";

    private String mixedExample1 = "[color='green']This is an [b]example[/b] of mixed[/color] [u][i]usage[/i] of tags[/u]";
    private String mixedExample2 = "[size='24']Another [b]example[/b][/size] of [color='blue'][b]mixed[/b] tags[/color]";

    private String addonExample1 = "[icon='fa fa-check'] Hey, I have an icon here";

    //New 30.09.2020
    private String highlightExample = "This text style is [mark]highlight[/mark].";
    private String blockquoteExample = "This text's style is [blockquote]blockquote[/blockquote] [q]blockquote[/q]";
    private String brExample = "This text's here [br/] text in new line";
    private String classNameExample = "This text here [className='text-primary'] has primary color[/classname].";
    private String delExample = "This text style is [del]strikethrough[/del] [s]strikethrough[/s].";
    private String bqFooterExample = "This text style is [bqFooter]blockquote footer/ footer[/bqFooter]";
    private String codeExample = "This text style is [br/][code]code goes here[/code]";
    private String listExample = "This list style [ul] code [li] element 1 [/li] [li] element 2 [/li][/ul]";
}
