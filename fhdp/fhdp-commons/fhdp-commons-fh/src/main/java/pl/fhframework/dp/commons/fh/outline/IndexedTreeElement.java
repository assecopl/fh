package pl.fhframework.dp.commons.fh.outline;


public class IndexedTreeElement<T> extends TreeElement<T> {

    private int index;
    private int parentIndex;
    private boolean addIndexToLabel;
    private String indexedObjectName;

    public IndexedTreeElement(String label, String icon, T obj, int index, String indexedObjectName, boolean addIndexToLabel) {
        super(label, icon, obj);
        this.index = index;
        this.addIndexToLabel = addIndexToLabel;
        this.indexedObjectName = indexedObjectName;
    }

    public IndexedTreeElement(String label, String icon, T obj, int index, String indexedObjectName, boolean addIndexToLabel, int parentIndex) {
        this(label, icon, obj, index, indexedObjectName, addIndexToLabel);
        this.parentIndex = parentIndex;
    }


    @Override
    public String getLabel() {
        if (addIndexToLabel) {
            return String.format("%s [%d]", super.getLabel(), index + 1);
        } else {
            return super.getLabel();
        }
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setParentIndexIndex(int parentIndex) {
        this.parentIndex = parentIndex;
    }

    public int getParentIndexIndex() {
        return parentIndex;
    }

    public String getIndexedObjectName() {
        return indexedObjectName;
    }

    public void setIndexedObjectName(String indexedObjectName) {
        this.indexedObjectName = indexedObjectName;
    }

    public boolean isAddIndexToLabel() {
        return addIndexToLabel;
    }

    public void setAddIndexToLabel(boolean addIndexToLabel) {
        this.addIndexToLabel = addIndexToLabel;
    }
}
