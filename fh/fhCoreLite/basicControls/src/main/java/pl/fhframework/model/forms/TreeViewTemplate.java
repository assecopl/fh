package pl.fhframework.model.forms;


public interface TreeViewTemplate<T extends FormElement> {

    T getCopy(Form form);
    T getCopy(Tree tree, int nodeNumber);

}
