package pl.fhframework.dp.commons.fh.declaration.handling;

import java.util.Collection;

public interface IDeclarationHandlingForm {

    void initMainTabContainer();

    void initDeclRightPanel(Object model, String formReference, String variantId);
    void initDeclRightPanel(Object model, String formReference, String variantId, String target);

    void initPanel(String panelId, Object model, String formReference, String variantId);

    void addDynamicTab(DynamicTab dynamicTab);
    int indexOfTab(DynamicTab dt);
    void removeDynamicTab(DynamicTab dt);
    DynamicTab getActiveDynamicTab(Collection<DynamicTab> dynamicTabs);


    DynamicTab getDynamicTab(String tabId);
}
