package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.annotations.DesignerXMLProperty;
import pl.fhframework.annotations.DocumentedComponentAttribute;
import pl.fhframework.annotations.XMLProperty;
import pl.fhframework.core.generator.ModelElement;
import pl.fhframework.core.generator.ModelElementType;
import pl.fhframework.model.HeadingTypeEnum;

import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.LOOK_AND_STYLE;


@ModelElement(type = ModelElementType.HIDDEN)
public abstract class GroupingComponentWithHeadingHierarchy<T extends Component>  extends GroupingComponent<T> {

    /**
     * Heading type
     */
    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(defaultValue = "Default")
    @DesignerXMLProperty(functionalArea = LOOK_AND_STYLE, priority = 66)
    @DocumentedComponentAttribute(defaultValue = "Default", value = "Defines heading tag for component Set it to H1, H2, H3, H4, H5, H6 or Auto. With Auto value system will calculate tag based on components hierarchy. Default there is no heading tag. ")
    private HeadingTypeEnum headingType = HeadingTypeEnum.Default;

    @Getter
    @Setter
    private String headingTypeValue;

    public GroupingComponentWithHeadingHierarchy(Form form) {
        super(form);
    }

    @Override
    public void init() {
        super.init();
        this.setHeadingTypeValue(this.resolveHeadingType(this.getClass(), this.headingType));
    }

    /**
     * Resolves heading based on current position of Component in components tree structure.
     * @param c
     * @param headingType
     * @return
     */
    private String resolveHeadingType(Class c, HeadingTypeEnum headingType){
        String hType = null;
        if(headingType != null && !headingType.equals(HeadingTypeEnum.Default)) {
            if (headingType.equals(HeadingTypeEnum.Auto)) {
                Integer i = 1;
                IGroupingComponent component = this.getGroupingParentComponent();
                while (component != null && i < 6) {
                    if (component instanceof GroupingComponentWithHeadingHierarchy) {
                        GroupingComponentWithHeadingHierarchy currentComponent = (GroupingComponentWithHeadingHierarchy) component;
                        //TODO Make changes in logic for blank labels.
                        if((currentComponent.headingType != null && !currentComponent.headingType.equals(HeadingTypeEnum.Default) && !currentComponent.headingType.equals(HeadingTypeEnum.Auto) )){
                          switch (currentComponent.headingType) {
                              case H1: i += 1; break;
                              case H2: i += 2; break;
                              case H3: i += 3; break;
                              case H4: i += 4; break;
                              case H5: i += 5; break;
                              case H6: i = 6; break;
                          }
                            component = null;
                            if(i > 6) i = 6;
                        } else {
                            i++;
                            component = ((Component) component).getGroupingParentComponent();
                        }
                    } else {
                        component = ((Component) component).getGroupingParentComponent();
                    }

                }
                hType = HeadingTypeEnum.valueOf("H" + i.toString()).toString();

            } else {
                hType = headingType.toString();
            }
        }
        return hType;
    }
}
