import {injectable} from "inversify";

declare const ENV_IS_DEVELOPMENT: boolean;
declare const $ :any;

@injectable()
class LayoutHandler {

    public static mainLayout:string = "standard";
    private prefix:string = "fh-layout-";

    public currentMainLayout:string = LayoutHandler.mainLayout;
    public targetLayout:string = LayoutHandler.mainLayout;

    /**
     * Functions that finds specific container in target layout
     * Created for bakward compatibility with JS function getElementById.
     * @param containerId
     * @param jqueryObject
     * @return HTML DOM Object or null*
     */
    public getLayoutContainer(containerId:string, jqueryObject:boolean = false){
        const container = $("#"+this.targetLayout+" #"+containerId);
        if(container.length == 0){
            const containerFallBack = $("#"+containerId);
            if(containerFallBack.length == 0){
                return null
            } else {
                return (jqueryObject ? containerFallBack : containerFallBack[0]);
            }
        } else {
           return (jqueryObject ? container : container[0]);
        }

    }

    /**
     * Functions that finds specific container in current layout
     * Created for bakward compatibility with JS function getElementById.
     * @param containerId
     * @param jqueryObject
     * @return HTML DOM Object or null*
     *
     */
    public getCurrentLayoutContainer(containerId:string, jqueryObject:boolean = false){
        const container = $("#"+this.currentMainLayout+" #"+containerId);
        if(container.length == 0){
            const containerFallBack = $("#"+containerId);
            if(containerFallBack.length == 0){
                return null
            } else {
                return (jqueryObject ? containerFallBack : containerFallBack[0]);
            }
        } else {
            return (jqueryObject ? container : container[0]);
        }

    }

    public getCurrentMainLayout(){
        return this.currentMainLayout;
    }

    /**
     * Function that prepare age for layout processing. If layout will be changed function hides all layouts.
     * @param layout
     */
    public startLayoutProcessing(layout:string):void {
        if(this.currentMainLayout != layout) {
            $(".fh-layout-div").addClass("d-none");
            this.targetLayout = this.prefix+layout;
        }
        if (ENV_IS_DEVELOPMENT) {
            console.log("startLayoutProcessing", layout, this.currentMainLayout, this.targetLayout);
        }
    }

    /**
     * Fuction that block layout change. Used when another UC shows form on modal element.
     * Used in Form.ts;
     * @param isModal
     */
    public blockLayoutChangeForModal() :void{

        this.targetLayout = this.currentMainLayout;

        if (ENV_IS_DEVELOPMENT) {
            console.log("blockLayoutChangeForModal", this.currentMainLayout, this.targetLayout);
        }
    }

    /**
     * Fuction that block layout change. Used when UC is in design mode
     * Used in Form.ts;
     */
    public blockLayoutChangeForDesigner(){

        this.targetLayout = LayoutHandler.mainLayout;

        if (ENV_IS_DEVELOPMENT) {
            console.log("blockLayoutChangeForDesigner", this.currentMainLayout, this.targetLayout);
        }

    }

    /**
     * Function that finish layout processing. Moves exist contetnt from one layout to another.
     * Moving designer components is not implemented.
     */
    public finishLayoutProcessing(){
        if(this.currentMainLayout != this.targetLayout) {
            if (ENV_IS_DEVELOPMENT) {
                console.log("Ustawiam currentLayout");
            }
            //TODO Maybay we do not have to moves mainForm content on layout change.To discuss.
            const currentMainForm: any = this.getCurrentLayoutContainer( "mainForm", true);
            const currentMenuForm = this.getCurrentLayoutContainer("menuForm", true);
            const currentNavbarForm = this.getCurrentLayoutContainer("navbarForm", true);

            const targetMainForm = this.getLayoutContainer("mainForm", true);
            const targetMenuForm = this.getLayoutContainer("menuForm", true);
            const targetNavbarForm = this.getLayoutContainer( "navbarForm", true);

            currentMainForm.contents().appendTo(targetMainForm);
            currentMenuForm.contents().appendTo(targetMenuForm);
            currentNavbarForm.contents().appendTo(targetNavbarForm);
            currentMainForm.html("");
            currentMenuForm.html("");
            currentNavbarForm.html("");

            this.currentMainLayout = this.targetLayout;

        }
        $("#" + this.targetLayout).removeClass("d-none");

        if (ENV_IS_DEVELOPMENT) {
            console.log("finishLayoutProcessing", this.currentMainLayout, this.targetLayout);
        }
    }

}

export {LayoutHandler};