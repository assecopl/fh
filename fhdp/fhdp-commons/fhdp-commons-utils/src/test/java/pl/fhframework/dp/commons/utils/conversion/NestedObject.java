package pl.fhframework.dp.commons.utils.conversion;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.*;

@Setter
@Getter
public class NestedObject {
  private String stringTest;
  private String stringEmptyTest;
  private ArrayList<String> itemEmptyArray = new ArrayList<>();
  private List<String> itemEmptyList = new ArrayList<>();
  private Collection<String> itemEmptyCollection = new ArrayList<>();
  private Map<String, String> itemEmptyMap = new HashMap<String, String>();

  private ArrayList<Integer> itemArray = new ArrayList<>();
  private List<String> itemList = new ArrayList<>();
  private Collection<String> itemCollection = new ArrayList<>();
  private Map<String, String> itemMap = new HashMap<String, String>();

  private byte[] content;
  private boolean booleanTest;
  private byte byteTest;
  private char charTest;
  private double doubleTest;
  private float floatTest;
  private int intTest;
  private long longTest;
  private short shortTest;

  private boolean booleanEmptyTest;
  private byte byteEmptyTest;
  private char charEmptyTest;
  private double doubleEmptyTest;
  private float floatEmptyTest;
  private int intEmptyTest;
  private long longEmptyTest;
  private short shortEmptyTest;

  private Boolean booleanObjTest;
  private Byte byteObjTest;
  private Character charObjTest;
  private Double doubleObjTest;
  private Float floatObjTest;
  private Integer integerObjTest;
  private Long longObjTest;
  private Short shortObjTest;
  private BigInteger bigIntegerObjTest;

  private Boolean booleanObjEmptyTest;
  private Byte byteObjEmptyTest;
  private Character charObjEmptyTest;
  private Double doubleObjEmptyTest;
  private Float floatObjEmptyTest;
  private Integer integerObjEmptyTest;
  private Long longObjEmptyTest;
  private Short shortObjEmptyTest;
  private BigInteger bigIntegerObjEmptyTest;
}