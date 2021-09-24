package pl.fhframework.core.rules.dynamic.model;

import pl.fhframework.core.rules.dynamic.blockly.BlocklyBlock;

import java.security.SecureRandom;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by pawel.ruta on 2017-06-30.
 */
public interface RuleElement {
    BlocklyBlock convertToBlockly(Function<String, String> formatter);

    static RuleElement convertFromBlockly(BlocklyBlock block) {
        return null;
    }

    RuleElement getParent();

    void setParent(RuleElement parent);

    StatementsList getSurroundParent();

    void setSurroundParent(StatementsList statementsList);

    void processValueChange(String name, String value);

    void processValueChange(String name, RuleElement value);

    Map<String, RuleElement> getComplexValues();

    Class getInputType(String name);

    String getId();

    void setId(String id);

    Double getX();

    void setX(Double x);

    Double getY();

    void setY(Double y);

    default String getOrGenerateId() {
        if (getId() == null) {
            final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%&[]";
            final SecureRandom rnd = new SecureRandom();
            final int len = 16;

            StringBuilder sb = new StringBuilder(len);
            for (int i = 0; i < len; i++)
                sb.append(AB.charAt(rnd.nextInt(AB.length())));

            setId(sb.toString());
        }

        return getId();
    }
}
