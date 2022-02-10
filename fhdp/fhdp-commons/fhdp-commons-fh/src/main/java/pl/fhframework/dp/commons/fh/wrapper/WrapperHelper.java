package pl.fhframework.dp.commons.fh.wrapper;

import org.apache.commons.beanutils.PropertyUtilsBean;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;

public class WrapperHelper {
    public static Long getSequenceNumber(Object o,
                                                PropertyUtilsBean pub) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return (Long) pub.getProperty(o, "sequenceNumber");
    }

    public static void setSequenceNumber(Object o, Long sequenceNumber,
                                          PropertyUtilsBean pub) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        pub.setProperty(o, "sequenceNumber", sequenceNumber);
    }

    public static List getList(Object o, String propertyName,
                                PropertyUtilsBean pub) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return (List) pub.getProperty(o, propertyName);
    }

    public static void handleEdit(Wrapper selected, List list, Object changedItem) {
        try {
            PropertyUtilsBean pub = new PropertyUtilsBean();

            // get sequenceNumber
            Long editedItemSequenceNumber = getSequenceNumber(selected.getElement(), pub);

            Object item = null;

            // find table object in declaration
            for (Object o : list) {
                if (getSequenceNumber(o, pub).equals(editedItemSequenceNumber)) {
                    item = o;
                }
            }

            // get outer table object index in declaration
            int editedItemIndex = list.indexOf(item);

            // replace object
            list.set(editedItemIndex, ((Wrapper) changedItem).getElement());

        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void handleEditNestedOuter(Wrapper selected, List list, Object changedItem) {
        PropertyUtilsBean pub = new PropertyUtilsBean();
        try {
            Long editedItemSequenceNumber;
            editedItemSequenceNumber = getSequenceNumber((((NestedWrapper) changedItem).getElement()), pub);

            Optional<Object> editedItem = list
                    .stream().filter(item -> {
                        try {
                            return getSequenceNumber(item, pub).equals(editedItemSequenceNumber);
                        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                            e.printStackTrace();
                            return false;
                        }
                    }).findFirst();

            if (editedItem.isPresent()) {
                int editedItemIndex = list.indexOf(editedItem.get());
                list.set(editedItemIndex, ((NestedWrapper) changedItem).getElement());
            }
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void handleEditNestedInner(NestedWrapper selectedOuter, List listOuter, Object changedItem, String nestedPropertyName) {
        try {
            PropertyUtilsBean pub = new PropertyUtilsBean();

            // get outer table equenceNumber object
            Long editedOuterItemSequenceNumber = getSequenceNumber(selectedOuter.getElement(), pub);

            Object outerItem = null;

            // find outer table object in declaration
            for (Object o : listOuter) {
                if (getSequenceNumber(o, pub).equals(editedOuterItemSequenceNumber)) {
                    outerItem = o;
                }
            }

            // get outer table object index in declaration
            int editedOuterItemIndex = listOuter.indexOf(outerItem);

            // get inner table object sequencenumber
            Long editedInnerItemSequenceNumber = getSequenceNumber(((Wrapper) changedItem).getElement(), pub);

            // find currenty edited inner table object
            Object innerItem = null;

            List innerList = getList(outerItem, nestedPropertyName, pub);

            for (Object o : innerList) {
                if (getSequenceNumber(o, pub).equals(editedInnerItemSequenceNumber)) {
                    innerItem = o;
                }
            }

            // get inner table index
            int editedInnerItemIndex = getList(listOuter.get(editedOuterItemIndex), nestedPropertyName, pub).indexOf(innerItem);

            // replace object
            getList(listOuter.get(editedOuterItemIndex), nestedPropertyName, pub).set(editedInnerItemIndex,
                    ((Wrapper) changedItem).getElement());

        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void handleAddNested(Object it, List entityList, Object selectedItem, String nestedPropertyName) {
        PropertyUtilsBean pub = new PropertyUtilsBean();
        try {
            for (Object entityGuarantee : entityList) {
                if (getSequenceNumber(entityGuarantee, pub).equals(getSequenceNumber(selectedItem, pub))) {
                    getList(entityGuarantee, nestedPropertyName, pub)
                            .add(((Wrapper) it).getElement());
                }
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static void handleDeleteNested(Object it, List entityList, Object selectedItem, String nestedPropertyName) {
        PropertyUtilsBean pub = new PropertyUtilsBean();

        try {
            // get outer table object sequenceNumber
            final Long editedOuterItemSequenceNumber = getSequenceNumber(selectedItem, pub);

            // find outer table object in declaration
            Optional<Object> outerItem = entityList
                    .stream().filter(item -> {
                        try {
                            return getSequenceNumber(item, pub).equals(editedOuterItemSequenceNumber);
                        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                            e.printStackTrace();
                            return false;
                        }
                    }).findFirst();

            getList(outerItem.get(), nestedPropertyName, pub).remove(((Wrapper) it).getElement());

        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

    }

    public static void handleSelectedSequenceNumber(Wrapper selected, List entityList, Object it) {
        PropertyUtilsBean pub = new PropertyUtilsBean();
        try {
            setSequenceNumber(selected.getElement(), setSequenceNumber(entityList), pub);
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    public static Long setSequenceNumber(List<?> list) throws IllegalAccessException {
        if(list.size() != 0){
            int lastIndex = list.size()-1;
            Object ob = list.get(lastIndex);

            for(Field field : ob.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                if(field.getName().equals("sequenceNumber") || field.getName().equals("goodsItemNumber")) {
                    Long nextIndex = (Long) field.get(ob);
                    return nextIndex + 1;
                }
            }
        }
        return Long.valueOf(list.size()+1);
    }
}
