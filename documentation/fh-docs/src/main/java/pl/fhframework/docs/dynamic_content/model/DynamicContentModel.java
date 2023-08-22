package pl.fhframework.docs.dynamic_content.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by krzysztof.kobylarek on 2016-12-28.
 */
public class DynamicContentModel {

    @Getter @Setter
    private String componentChosen = "OutputLabel";

    @Getter @Setter
    private String selectOneMenuValue = "Two";

    @Getter
    private List<String> selectOneMenuValues = Arrays.asList("One", "Two", "Three");

    @Getter @Setter
    private boolean checkboxValue = true;

    public enum ChosenType {
        OUTPUTLABEL("OutputLabel"),
        SELECT_ONE_MENU("SelectOneMenu"),
        CHECKBOX("CheckBox"),
        INPUTTEXT("InputText"),
        REPEATER("Repeater"),
        GROUP("Group"),
        GROUP_IN_REPEATER("Group inside Repeater"),
        REPEATER_IN_REPEATER("Repeater in Repeater")
        ;
        String toString = "";
        ChosenType(String toString){
            this.toString=toString;
        }

        static Optional<ChosenType> get(String toString){
            return Arrays.stream(ChosenType.values()).filter((e)->e.toString.equals(toString)).findFirst();
        }
    }

    public Optional<ChosenType> getComponentChosenType() {
        return ChosenType.get(componentChosen);
    }

    public void setComponentChosen(String componentChosen) {
        this.componentChosen = componentChosen;
    }

    private static String getRandomString(){
        return UUID.randomUUID().toString().substring(0,5).toUpperCase();
    }

    @Getter
    private List<Iteration> iterations = createIterations();


    @Getter
    public static class Iteration {
        String label=getRandomString();

        @Getter
        private List<Iteration> iterations = null;
    }

    private static List<Iteration> createIterations(){
        Iteration i1 = new Iteration();
        Iteration i2 = new Iteration();
        Iteration i3 = new Iteration();

        i1.iterations = Arrays.asList(new Iteration());
        i2.iterations = Arrays.asList(new Iteration());
        i3.iterations = Arrays.asList(new Iteration());

        return Arrays.asList(i1, i2, i3);
    }

    @Getter @Setter
    private String inputTextValue="-";

}

