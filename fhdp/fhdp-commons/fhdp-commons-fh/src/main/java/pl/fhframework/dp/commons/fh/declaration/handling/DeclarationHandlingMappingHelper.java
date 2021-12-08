package pl.fhframework.dp.commons.fh.declaration.handling;

import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.Map;

public class DeclarationHandlingMappingHelper {

    private Object declaration;

    private String PREFIX = "class ";
    private String FHDP_PREFIX = "pl.fhframework.dp";
    private String FH_PREFIX = "pl.fh";
    private String PRIMITIVE_PREFIX = "java.lang";
    private String parent;

    public DeclarationHandlingMappingHelper(Object declaration) {
        this.declaration = declaration;
    }

    public DeclarationHandlingMappingHelper(Object declaration, String parent) {
        this.declaration = declaration;
        this.parent = parent;
    }

    private boolean isFhdpClass(String cls) {
        String temp = cls;
        if (temp.startsWith(PREFIX)) {
            temp = temp.split(PREFIX)[1];
        }
        return temp.startsWith(FHDP_PREFIX) || temp.startsWith(FH_PREFIX);
    }

//    public Map<String, Object> flatMap() {
    public Map<String, String> flatMap() {
        Map <String, String> res = new HashedMap();
        try {
            BeanWrapperImpl wrapper = new BeanWrapperImpl(declaration);
            PropertyDescriptor[] fields = wrapper.getPropertyDescriptors();
            for (PropertyDescriptor f : fields) {
                if (f == null || f.getPropertyType() == null) {
                    continue;
                }
                if (isFhdpClass(f.getPropertyType().toString())) {
                    String prnt = f.getName();
                    if (parent != null ) {
                        prnt = parent + "." + prnt;
                    }
                    Object val = wrapper.getPropertyValue(f.getName());
                    if (val != null) {
                        DeclarationHandlingMappingHelper temp = new DeclarationHandlingMappingHelper(val, prnt);
                        Map<String, String> subRes = temp.flatMap();
                        res.putAll(subRes);
                    }
                } else {
                    String val;
                    if (wrapper.isReadableProperty(f.getName())) {
                        Object rawval = wrapper.getPropertyValue(f.getName());
                        if (rawval == null) {
                            val = "";
                        } else {
                            val = rawval.toString();
                        }
                        if (parent != null) {
                            System.out.println(parent + "." + f.getName() + " = " + val);
                            res.put(parent + "." + f.getName(), val);
                        } else {
                            System.out.println(f.getName() + " = " + val);
                            res.put(f.getName(), val);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }
}
