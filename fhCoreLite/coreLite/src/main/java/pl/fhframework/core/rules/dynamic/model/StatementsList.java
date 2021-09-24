package pl.fhframework.core.rules.dynamic.model;

import pl.fhframework.core.rules.dynamic.blockly.BlocklyBlock;
import pl.fhframework.core.rules.dynamic.blockly.BlocklyNext;
import pl.fhframework.core.rules.dynamic.blockly.BlocklyStatement;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public interface StatementsList extends Serializable {
    List<Statement> getStatements();

    default BlocklyBlock convertStatementsToBlockly(Function<String, String> formatter) {
        Iterator<Statement> statementIterator = getStatements().iterator();
        BlocklyBlock block = statementIterator.next().convertToBlockly(formatter);
        BlocklyBlock prevBlock = block;
        while (statementIterator.hasNext()) {
            BlocklyBlock nextBlock = statementIterator.next().convertToBlockly(formatter);
            prevBlock.setNext(new BlocklyNext(nextBlock));
            prevBlock = nextBlock;
        }

        return block;
    }

    default void addStatements(BlocklyBlock block, Function<String, String> formatter) {
        if (!getStatements().isEmpty()) {
            BlocklyStatement blocklyStatement = new BlocklyStatement();
            blocklyStatement.setName("statements");

            blocklyStatement.getBlocks().add(convertStatementsToBlockly(formatter));

            block.setStatement(blocklyStatement);
        }
    }
}
