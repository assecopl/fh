package pl.fhframework.dp.transport.dto.commons;

import lombok.Getter;
import lombok.Setter;
import org.apache.http.NameValuePair;

import java.io.Serializable;

/**
 * @author <a href="mailto:jacek_borowiec@skg.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 04.09.2019
 */
@Getter @Setter
public class NameValueDto implements Serializable, NameValuePair {
    private Long id;
    private String name;
    private String value;
    private String unitCategory;

    /**
     * Dictionary 001 - AIS
     */
    private String city;
    private String street;

    public NameValueDto() {
    }

    public NameValueDto(String name, String value) {
      this.name = name;
      this.value = value;
    }

    public NameValueDto(Long id, String name, String value) {
        this(name, value);
        this.id = id;
    }

     public NameValueDto(Long id, String name, String value , String unitCategory) {
        this(id, name, value);
        this.unitCategory = unitCategory;
    }

    public NameValueDto(String name, String value, String city, String street) {
      this(name, value);
      this.city = city;
      this.street = street;
    }

    public NameValueDto(Long id, String name, String value, String city, String street) {
      this(id, name, value);
      this.city = city;
      this.street = street;
    }
}
