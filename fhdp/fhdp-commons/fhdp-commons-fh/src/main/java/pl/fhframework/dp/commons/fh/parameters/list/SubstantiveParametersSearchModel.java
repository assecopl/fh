package pl.fhframework.dp.commons.fh.parameters.list;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.dp.transport.dto.parameters.SubstantiveParametersDtoQuery;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
public class SubstantiveParametersSearchModel {
    SubstantiveParametersDtoQuery query;

    public SubstantiveParametersSearchModel(SubstantiveParametersDtoQuery query) {
        this.query = query;
    }

    //TODO:
    public void clearQuery() {
      Field[] allFields = this.getClass().getDeclaredFields();
      for (Field field : allFields) {
         try {
            if (ArrayList.class.isAssignableFrom(field.getType())) {
               field.setAccessible(true);
               ((Collection) field.get(this)).clear();
            }
            field.setAccessible(true);
            field.set(this, null);
         } catch (IllegalAccessException e) {
            e.printStackTrace();
         }
      }
    }

    //TODO:
    public void initQuery() {
      Field[] allFields = query.getClass().getDeclaredFields();
      for (Field field : allFields) {
         try {
            if (ArrayList.class.isAssignableFrom(field.getType())) {
               field.set(query, new ArrayList());
            }
         } catch (IllegalAccessException e) {
            e.printStackTrace();
         }
      }
    }
}
