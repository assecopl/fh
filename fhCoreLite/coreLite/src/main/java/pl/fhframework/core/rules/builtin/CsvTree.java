package pl.fhframework.core.rules.builtin;

import pl.fhframework.core.rules.BusinessRule;
import pl.fhframework.core.util.StringUtils;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * Changes text CSV-like data into CvsTreeElement list. May be used for filling trees in fast prototyping.
 * Input data should contain values for elements separated with pipe | character with subelements embeded in brackets.
 */
@BusinessRule(categories = {"collection", "csv"})
public class CsvTree {

    /**
     * Changes text CSV-like data into CvsTreeElement list. May be used for filling trees in fast prototyping.
     * @param csvData Input data should contain values for elements separated with pipe | character with subelements embeded in brackets.
     * @return list of csv tree elements
     */
    public List<CsvTreeElement> csvTree(String csvData) {
        LinkedList<CsvTreeElement> elementsStack = new LinkedList<>();
        CsvTreeElement root = new CsvTreeElement();
        elementsStack.push(root);

        if (!StringUtils.isNullOrEmpty(csvData)) {
            boolean newElementStart = true;
            for (char singleChar : csvData.toCharArray()) {
                if (newElementStart) {
                    elementsStack.push(new CsvTreeElement());
                    newElementStart = false;
                }
                CsvTreeElement currentElement = elementsStack.peek();

                if (singleChar == '|') {
                    finalizeElement(elementsStack);
                    newElementStart = true;
                } else if (singleChar == ')') {
                    finalizeElement(elementsStack);
                } else if (singleChar == '(') {
                    newElementStart = true;
                } else {
                    currentElement.setLabel(StringUtils.nullToEmpty(currentElement.getLabel()) + singleChar);
                }
            }
        }
        while (elementsStack.size() > 1) {
            finalizeElement(elementsStack);
        }
        return root.getSubelements();
    }

    private void finalizeElement(LinkedList<CsvTreeElement> elementsStack) {
        CsvTreeElement currentElement = elementsStack.removeFirst();
        elementsStack.peek().getSubelements().add(currentElement);
    }
}
