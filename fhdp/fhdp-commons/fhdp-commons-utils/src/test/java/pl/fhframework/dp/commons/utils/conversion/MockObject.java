package pl.fhframework.dp.commons.utils.conversion;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Setter
@Getter
public class MockObject {
  private String stringTest;
  private String stringEmptyTest;

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
  private byte[] content;

  private NestedObject nestedObject;
  private NestedObject emptyNestedObject;
  private List<NestedObject> emptyNestedObjects = new ArrayList();
  private List<NestedObject> nestedObjects = new ArrayList();
  private Set<NestedObject> emptyNestedObjectSet = new HashSet();
  private Set<NestedObject> nestedObjectSet = new HashSet();
}
