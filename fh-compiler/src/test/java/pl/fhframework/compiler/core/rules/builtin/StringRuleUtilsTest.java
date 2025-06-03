package pl.fhframework.compiler.core.rules.builtin;

import org.junit.Assert;
import org.junit.Test;
import pl.fhframework.core.rules.builtin.StringRuleUtils;

/**
 * Created by Paweł on 2017-11-22.
 */
public class StringRuleUtilsTest {
    private StringRuleUtils stringUtils = new StringRuleUtils();

    @Test
    public void testStringInsert() {
        Assert.assertEquals(stringUtils.stringInsert(null, 5, null), null);
        Assert.assertEquals(stringUtils.stringInsert("", 3, null), null);
        Assert.assertEquals(stringUtils.stringInsert("text", 2, null), "text");
        Assert.assertEquals(stringUtils.stringInsert("text", 1, ""), "text");
        Assert.assertEquals(stringUtils.stringInsert("text", 2, "  "), "te  xt");
        Assert.assertEquals(stringUtils.stringInsert("text", 0, "  "), "  text");
        Assert.assertEquals(stringUtils.stringInsert("text", -1, "  "), "  text");
        Assert.assertEquals(stringUtils.stringInsert("text", 4, "  "), "text  ");
        Assert.assertEquals(stringUtils.stringInsert("text", 10, "  "), "text  ");
    }

    @Test
    public void testStringRepeat() {
        Assert.assertEquals(stringUtils.stringRepeat("text", 2), "texttext");
        Assert.assertEquals(stringUtils.stringRepeat(null, 3), null);
        Assert.assertEquals(stringUtils.stringRepeat("", 4), "");
        Assert.assertEquals(stringUtils.stringRepeat("", -1), "");
    }

    @Test
    public void testStringSwapCase() {
        Assert.assertEquals(stringUtils.stringSwapCase("TeXt123 !"), "tExT123 !");
        Assert.assertEquals(stringUtils.stringSwapCase(""), "");
        Assert.assertEquals(stringUtils.stringSwapCase(null), null);
    }

    @Test
    public void testStringIsLetter() {
        Assert.assertEquals(stringUtils.stringIsLetter("abcdefABCDEF"), true);
        Assert.assertEquals(stringUtils.stringIsLetter("1"), false);
        Assert.assertEquals(stringUtils.stringIsLetter(" "), false);
        Assert.assertEquals(stringUtils.stringIsLetter("!"), false);
    }

    @Test
    public void testStringIsDigit() {
        Assert.assertEquals(stringUtils.stringIsDigit("1234567890"), true);
        Assert.assertEquals(stringUtils.stringIsDigit("a"), false);
        Assert.assertEquals(stringUtils.stringIsDigit(" "), false);
        Assert.assertEquals(stringUtils.stringIsDigit("!"), false);
    }

    @Test
    public void testStringIsWhitespace() {
        Assert.assertEquals(stringUtils.stringIsWhitespace(" "), true);
        Assert.assertEquals(stringUtils.stringIsWhitespace("a"), false);
        Assert.assertEquals(stringUtils.stringIsWhitespace("1"), false);
        Assert.assertEquals(stringUtils.stringIsWhitespace("!"), false);
    }

    @Test
    public void testStringIsLetterAt() {
        Assert.assertEquals(stringUtils.stringIsLetterAt("abcdefABCDEF", 2), true);
        Assert.assertEquals(stringUtils.stringIsLetterAt("abcdefABCDEF", -2), false);
        Assert.assertEquals(stringUtils.stringIsLetterAt("abcdefABCDEF", 20), false);
        Assert.assertEquals(stringUtils.stringIsLetterAt("1", 0), false);
        Assert.assertEquals(stringUtils.stringIsLetterAt(" ", 0), false);
        Assert.assertEquals(stringUtils.stringIsLetterAt("!", 0), false);
    }

    @Test
    public void testStringIsDigitAt() {
        Assert.assertEquals(stringUtils.stringIsDigitAt("1234567890", 5), true);
        Assert.assertEquals(stringUtils.stringIsDigitAt("1234567890", -1), false);
        Assert.assertEquals(stringUtils.stringIsDigitAt("1234567890", 11), false);
        Assert.assertEquals(stringUtils.stringIsDigitAt("a", 0), false);
        Assert.assertEquals(stringUtils.stringIsDigitAt(" ", 0), false);
        Assert.assertEquals(stringUtils.stringIsDigitAt("!", 0), false);
    }

    @Test
    public void testStringIsWhitespaceAt() {
        Assert.assertEquals(stringUtils.stringIsWhitespaceAt(" ", 0), true);
        Assert.assertEquals(stringUtils.stringIsWhitespaceAt(" ", -5), false);
        Assert.assertEquals(stringUtils.stringIsWhitespaceAt(" ", 3), false);
        Assert.assertEquals(stringUtils.stringIsWhitespaceAt("a", 0), false);
        Assert.assertEquals(stringUtils.stringIsWhitespaceAt("1", 0), false);
        Assert.assertEquals(stringUtils.stringIsWhitespaceAt("!", 0), false);
    }
}
