package pl.fhframework.docs.uc;

import pl.fhframework.core.designer.IDocumentationUseCase;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.url.UseCaseWithUrl;
import pl.fhframework.docs.forms.component.RepeaterForm;
import pl.fhframework.docs.forms.component.model.RepeaterElement;
import pl.fhframework.docs.forms.model.example.Student;
import pl.fhframework.docs.forms.service.RepeaterExampleDataService;
import pl.fhframework.docs.forms.service.StudentService;
import pl.fhframework.annotations.Action;
import pl.fhframework.events.BreakLevelEnum;

import java.text.DecimalFormat;


/**
 * Use case supporting Repeater documentation
 */
@UseCase
public class RepeaterUC implements IDocumentationUseCase<RepeaterElement> {
    private RepeaterElement model;

    @Override
    public void start(RepeaterElement model) {
        this.model = model;
        showForm(RepeaterForm.class, model);
    }


    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void addStudent() {
        StudentService.addStudent();
    }

    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void removeStudent(Student student) {
        StudentService.removeStudent(student);
    }

    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void addClasses(Student student) {
        StudentService.addClass(student);
    }

    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void removeClasses(Student.Classes classes) {
        StudentService.removeClass(classes);
    }


    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void onShuffleLinks() {
        RepeaterExampleDataService.shuffleLinks();
    }

   @Action(breakOnErrors = BreakLevelEnum.NEVER)
   public void onAddToBasket(RepeaterExampleDataService.Book book) {
       RepeaterExampleDataService.getBasket().addBook(book);
   }

    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void onDelFromBasket(RepeaterExampleDataService.BasketItem bookInBasket) {
        RepeaterExampleDataService.getBasket().delBook(bookInBasket);
    }

    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void onConvertFrom(RepeaterExampleDataService.CurrencyCalc currencyCalc) {
        try {
            Double fromValue = Double.valueOf(currencyCalc.getFromValue().replace(",", "."));
            DecimalFormat df = new DecimalFormat("#.##");
            currencyCalc.setToValue(String.valueOf(df.format(fromValue * currencyCalc.getConvertValue())));
        } catch (NumberFormatException e){currencyCalc.setToValue("");}
    }

    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void onConvertTo(RepeaterExampleDataService.CurrencyCalc currencyCalc) {
        try {
            Double toValue = Double.valueOf(currencyCalc.getToValue());
            DecimalFormat df = new DecimalFormat("#.##");
            currencyCalc.setFromValue(String.valueOf(df.format(toValue * 1/currencyCalc.getConvertValue())));
        } catch (NumberFormatException e){currencyCalc.setFromValue(" ");}
    }


}


