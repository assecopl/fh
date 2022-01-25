package pl.fhframework.dp.transport.dto.parameters;

import java.util.ArrayList;
import java.util.List;

public interface ISubstantiveParametersParameterDefinition {

  SubstantiveParametersDto createParameterFromDefinition(String definitionName);
  SubstantiveParametersDto createParameterFromDefinition(String definitionName, String officeId);

  /**
   * Goes through the received list and rewrites elements to readable list
   * @return
   */
  static List<SubstantiveParametersParameterItem> handleReadonlyList(List<SubstantiveParametersParameterItem> readOnlyList) {
    List<SubstantiveParametersParameterItem> readableList = new ArrayList<>();
    for(SubstantiveParametersParameterItem item : readOnlyList) {
      readableList.add(item);
    }
    return readableList;
  }

  /**
   * Returns a collection of string objects
   * It is required by FH framework, working directly with a 'String' causes locking edition of the collection elements
   * @param collectionOfItems
   * @return
   */
  static List<SubstantiveParametersParameterItem> createSubstantiveParametersParameterItem(List<String> collectionOfItems) {
    List<SubstantiveParametersParameterItem> substantiveParametersParameterItemList = new ArrayList<>();
    for (String item: collectionOfItems) {
      substantiveParametersParameterItemList.add(new SubstantiveParametersParameterItem(item));
    }
    return substantiveParametersParameterItemList;
  }
}
