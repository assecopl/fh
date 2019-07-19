package pl.fhframework.core.util;

import org.springframework.util.ReflectionUtils;
import pl.fhframework.core.FhException;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.model.forms.*;

import java.io.*;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by krzysztof.kobylarek on 2016-12-09.
 */
public class DebugUtils {

    public interface DebugNameSupplier {

        public String getDebugName();
    }

    public static Throwable getRootCause(Throwable e) {
        return FhException.getRootCause(e);
    }

    public static String collectionInfo(Iterable collection) {
        StringBuilder sb = new StringBuilder("");
        for (Object obj : collection) {
            if (sb.length() != 0) {
                sb.append(", \n\t\t");
            }
            sb.append(getDebugName(obj)).append("(").append(System.identityHashCode(obj)).append(")");
        }
        String result = sb.toString();
        return "[" + result + "]";
    }

    public static void printToFileForm(Form form){
        StringBuilder sb = new StringBuilder();
        String prefix="-" +
                "";
        sb.append(prefix+formElementInfo(form)).append(System.getProperty("line.separator"));
        printToFileForm(form.getSubcomponents(), sb, prefix, 1);


        try ( BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("repeater.log")))) {
            output.write(sb.toString());
        } catch (Exception e){}


        FhLogger.debug(DebugUtils.class, logger -> logger.log(sb.toString()));
    }

    private static String getDebugName(Object obj) {
        if (obj instanceof DebugNameSupplier) {
            return ((DebugNameSupplier) obj).getDebugName();
        } else {
            return obj.getClass().getSimpleName();
        }
    }

    private static String formElementInfo(Component formElement){
        Optional<Field> valueField = Arrays.stream(formElement.getClass().getDeclaredFields()).filter(f->f.getName().equals("value")).findAny();
        Object value=null;
        if (valueField.isPresent()){
            valueField.get().setAccessible(true);
            value = ReflectionUtils.getField(valueField.get(), formElement);
        }

        Optional<Field> orginalValueField = Arrays.stream(formElement.getClass().getDeclaredFields()).filter(f->f.getName().equals("orginalValue2")).findAny();
        Object orginalValue=null;
        if (orginalValueField.isPresent()){
            orginalValueField.get().setAccessible(true);
            orginalValue = ReflectionUtils.getField(orginalValueField.get(), formElement);
        }

        Optional<Field> collectionField = Arrays.stream(formElement.getClass().getDeclaredFields()).filter(f->f.getName().equals("collection")).findAny();
        Object collection=null;
        if (collectionField.isPresent()){
            collectionField.get().setAccessible(true);
            collection = ReflectionUtils.getField(collectionField.get(), formElement);
        }

        StringBuilder bindingResolved=new StringBuilder();
        ReflectionUtils.doWithFields(formElement.getClass(), new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                field.setAccessible(true);
                bindingResolved.append(ReflectionUtils.getField(field, formElement));
            }
        }, new ReflectionUtils.FieldFilter() {
            @Override
            public boolean matches(Field field) {
                return field.getName().equals("bindingResolved");
            }
        });

        Optional<Field> iteratorField = Arrays.stream(formElement.getClass().getDeclaredFields()).filter(f->f.getName().equals("iterator")).findAny();
        Object iterator=null;
        if (iteratorField.isPresent()){
            iteratorField.get().setAccessible(true);
            iterator = ReflectionUtils.getField(iteratorField.get(), formElement);
        }

        Optional<Field> labelField = Arrays.stream(formElement.getClass().getDeclaredFields()).filter(f->f.getName().equals("label")).findAny();
        Object label=null;
        if (labelField.isPresent()){
            labelField.get().setAccessible(true);
            label = ReflectionUtils.getField(labelField.get(), formElement);
        }

        StringBuilder binding=new StringBuilder();
        ReflectionUtils.doWithFields(formElement.getClass(), new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                field.setAccessible(true);
                binding.append(ReflectionUtils.getField(field, formElement));
            }
        }, new ReflectionUtils.FieldFilter() {
            @Override
            public boolean matches(Field field) {
                return field.getName().equals("binding");
            }
        });

        Optional<Field> iterationContextInfoField = Arrays.stream(formElement.getClass().getDeclaredFields()).filter(f->f.getName().equals("iterationContextInfo")).findAny();
        Object iterationContextInfo=null;
        if (iterationContextInfoField.isPresent()){
            iterationContextInfoField.get().setAccessible(true);
            iterationContextInfo = ReflectionUtils.getField(iterationContextInfoField.get(), formElement);
        }


        return formElement.getId()/*+" --- ["+ formElement.toString()+"]"*/
                + (label!=null ?  " --- [label: "+label.toString()+"]" : "")
                + (value!=null ?  " --- [value: "+value.toString()+"]" : "")
                + (orginalValue!=null ?  " --- [orginalValue: "+orginalValue.toString()+"]" : "")
                + (collection!=null ?  " --- [collection: "+collection.toString()+"]" : "")
                + (iterator!=null ?  " --- [iterator: "+iterator.toString()+"]" : "")
                + (binding!=null ?  " --- [binding: "+binding.toString()+"]" : "")
                + (bindingResolved.length()>0 ?  " --- [bindingResolved: "+bindingResolved.toString()+"]" : "")
                + (formElement instanceof IRepeatable ? iterationContextInfo : "")
                ;
    }

    private static String prefixPlusPlus(String prefix){
        return prefix.intern()+prefix.intern();
    }

    private static String copyPrefix(int count, String prefix){
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<count*2; i++){
            sb.append(prefix);
        }
        return sb.toString();
    }

    private static void printToFileForm(List<Component> formElementList, StringBuilder sb, String prefix, int level){
        for(Component element : formElementList){
            sb.append(copyPrefix(level, prefix)+formElementInfo(element)+System.getProperty("line.separator"));
            if (element instanceof IGroupingComponent){
                if (element instanceof GroupingComponent) {
                    printToFileForm(((GroupingComponent<Component>) element).getSubcomponents(), sb, prefix, level+1);
                } else if (element instanceof IRepeatable) {
                    printToFileForm(((IRepeatable) element).getBindedSubcomponents().stream()
                            .map(ctx -> ctx.getComponent() ).collect(Collectors.toList()), sb, prefix, level+1);
                }
            }
        }
    }

    public static String timeAsString(long timeSpanInNanos) {
        if (timeSpanInNanos < 1000) {
            return timeSpanInNanos + "ns";
        } else if (timeSpanInNanos < 1000000) {
            return timeSpanInNanos / 1000 + " \u00B5s";

        } else if (timeSpanInNanos < 1000000000) {
            return (timeSpanInNanos / 100000)/10.0 + "ms";
        } else {
            double warosc = timeSpanInNanos;
            warosc = Math.round(warosc / 1000000.0) / 1000.0;
            return warosc + " s";
        }
    }

    public static String sizeAsString(long rozmiarWBajtach) {
        if (rozmiarWBajtach < 4096) {
            return rozmiarWBajtach + " bytes";
        } else if (rozmiarWBajtach < 10485760) {
            return rozmiarWBajtach / 1024 + " KB";
        } else {
            return rozmiarWBajtach / 1048576 + " MB";
        }
    }

    /**
     * Gets stack trace as String
     * @param e exception
     * @return stack trace as String
     */
    public static String getStackTrace(Throwable e) {
        StringWriter stackTrace = new StringWriter();
        PrintWriter stackTracePrinter = new PrintWriter(stackTrace);
        e.printStackTrace(stackTracePrinter);
        return stackTrace.toString();
    }

    public static String getJoinedMessage(Throwable e) {
        StringBuilder builder = new StringBuilder(StringUtils.nullToEmpty(e.getMessage()));
        while (e.getCause() != null) {
            e = e.getCause();
            if (e.getMessage() != null) {
                builder.append(" / ").append(e.getMessage());
            }
        }
        return builder.toString();
    }

    /**
     * Sleeps for given time. Uses Thread.sleep()
     * @param millis time to sleep in millis
     */
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            FhLogger.error(e);
        }
    }
}
