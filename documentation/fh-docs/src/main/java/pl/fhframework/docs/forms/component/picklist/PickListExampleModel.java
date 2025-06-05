package pl.fhframework.docs.forms.component.picklist;

import pl.fhframework.model.forms.composites.picklist.PickListModel;
import pl.fhframework.model.forms.model.OptionsListElementModel;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by krzysztof.kobylarek on 2017-01-07.
 */
public class PickListExampleModel extends PickListModel {

    private AtomicInteger id = new AtomicInteger(1);

    private class Car extends OptionsListElementModel {

        Car(String brand, String model, boolean checked){
            this.brand=brand;
            this.model=model;
            this.setChecked(checked);
            setId(id.getAndIncrement());
        }
        Car(String brand, String model){
            this(brand, model, false);
        }

        String brand; String model;

        @Override
        public String toString() {
            return brand+" "+model;
        }

        @Override
        public String getValue() {
            return brand+" "+model;
        }
    }

    private List<OptionsListElementModel> carList1 = createList1();
    private List<OptionsListElementModel> carList2 = createList2();

    private List<OptionsListElementModel> createList1() {
        carList1 = new LinkedList<>();
        carList1.add(new Car("Honda", "C-HR", false));
        carList1.add(new Car("Opel","Insignia"));
        carList1.add(new Car("Nissan","Qashqai"));
        return carList1;
    }

    private List<OptionsListElementModel> createList2() {
        carList2 = new LinkedList<>();
        carList2.add(new Car("Audi", "A5"));
        carList2.add(new Car("BWM","M6"));
        return carList2;
    }

    @Override
    public List<OptionsListElementModel> getValuesList1() {
        return carList1;
    }

    @Override
    public List<OptionsListElementModel> getValuesList2() {
        return carList2;
    }

    @Override
    public String getTitleList1() {
        return "Cars available";
    }

    @Override
    public String getTitleList2() {
        return "Cars to order";
    }
}
