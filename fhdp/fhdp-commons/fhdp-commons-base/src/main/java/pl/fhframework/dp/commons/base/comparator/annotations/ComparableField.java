package pl.fhframework.dp.commons.base.comparator.annotations;



import pl.fhframework.dp.commons.base.comparator.ChangeTypeEnum;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited /* Note that this meta-annotation type has no effect if the annotated type is used to annotate anything other than a class. Note also that this meta-annotation only causes annotations to be inherited from superclasses; annotations on implemented interfaces have no effect. */
@Documented
public @interface ComparableField {

	String Xpath() default "";

	/**
	 * Znacznik, czy dane pole jest brane pod uwagę w sprostowaniu <BR/> Przykład : dla IE313 część danych pomimo że
	 * występuje w komunikacie, to na ENS pozostaje wartość oryginalna z IE515 np. numer własny
	 *
	 * @return
	 */
	boolean rectificate() default true;

	/**
	 * Znacznik czy pole jest wysyłane do ZEFIR-a, a tym samym będzie brane pod uwage na koniec decyzji administracyjnej
	 * do ponownego wysłania.
	 * @return
	 */
	boolean zefir() default false;

	/**
	 * Znacznik, czy dane pole jest obecne w podstawowej schemie
	 *
	 * @return
	 */
	boolean inSchema() default true;

	/**
	 * Zmiany do zignorowania (domylnie puste)
	 * @return
	 */
	ChangeTypeEnum[]  ignore() default {};

	/**
	 * Czy należy dodać xpath bez żadnej modyfikacji, np. dodajac znak @
	 * @return
	 */
	boolean rawXpath() default false;

	int precision() default -1;

	/**
	 * Znacznik czy pole nalezy skanowac w pełni tak jakby do wszystkich
	 * elementów pola (czyli ma znaczenie tylko dla klas złożonych) była dodana ta adnotacja
	 */
	boolean recursive() default false;
}
