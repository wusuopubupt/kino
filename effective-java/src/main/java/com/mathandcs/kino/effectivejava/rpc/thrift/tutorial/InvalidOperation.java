/**
 * Autogenerated by Thrift Compiler (0.9.3)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.mathandcs.kino.effectivejava.rpc.thrift.tutorial;

import org.apache.thrift.EncodingUtils;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;
import org.apache.thrift.scheme.TupleScheme;

import javax.annotation.Generated;
import java.util.*;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked"})
/**
 * Structs can also be exceptions, if they are nasty.
 */
@Generated(value = "Autogenerated by Thrift Compiler (0.9.3)", date = "2017-06-26")
public class InvalidOperation extends TException implements org.apache.thrift.TBase<InvalidOperation, InvalidOperation._Fields>, java.io.Serializable, Cloneable, Comparable<InvalidOperation> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("InvalidOperation");

  private static final org.apache.thrift.protocol.TField WHAT_OP_FIELD_DESC = new org.apache.thrift.protocol.TField("whatOp", org.apache.thrift.protocol.TType.I32, (short)1);
  private static final org.apache.thrift.protocol.TField WHY_FIELD_DESC = new org.apache.thrift.protocol.TField("why", org.apache.thrift.protocol.TType.STRING, (short)2);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new InvalidOperationStandardSchemeFactory());
    schemes.put(TupleScheme.class, new InvalidOperationTupleSchemeFactory());
  }

  public int whatOp; // required
  public String why; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    WHAT_OP((short)1, "whatOp"),
    WHY((short)2, "why");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // WHAT_OP
          return WHAT_OP;
        case 2: // WHY
          return WHY;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int __WHATOP_ISSET_ID = 0;
  private byte __isset_bitfield = 0;
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.WHAT_OP, new org.apache.thrift.meta_data.FieldMetaData("whatOp", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.WHY, new org.apache.thrift.meta_data.FieldMetaData("why", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(InvalidOperation.class, metaDataMap);
  }

  public InvalidOperation() {
  }

  public InvalidOperation(
    int whatOp,
    String why)
  {
    this();
    this.whatOp = whatOp;
    setWhatOpIsSet(true);
    this.why = why;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public InvalidOperation(InvalidOperation other) {
    __isset_bitfield = other.__isset_bitfield;
    this.whatOp = other.whatOp;
    if (other.isSetWhy()) {
      this.why = other.why;
    }
  }

  public InvalidOperation deepCopy() {
    return new InvalidOperation(this);
  }

  @Override
  public void clear() {
    setWhatOpIsSet(false);
    this.whatOp = 0;
    this.why = null;
  }

  public int getWhatOp() {
    return this.whatOp;
  }

  public InvalidOperation setWhatOp(int whatOp) {
    this.whatOp = whatOp;
    setWhatOpIsSet(true);
    return this;
  }

  public void unsetWhatOp() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __WHATOP_ISSET_ID);
  }

  /** Returns true if field whatOp is set (has been assigned a value) and false otherwise */
  public boolean isSetWhatOp() {
    return EncodingUtils.testBit(__isset_bitfield, __WHATOP_ISSET_ID);
  }

  public void setWhatOpIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __WHATOP_ISSET_ID, value);
  }

  public String getWhy() {
    return this.why;
  }

  public InvalidOperation setWhy(String why) {
    this.why = why;
    return this;
  }

  public void unsetWhy() {
    this.why = null;
  }

  /** Returns true if field why is set (has been assigned a value) and false otherwise */
  public boolean isSetWhy() {
    return this.why != null;
  }

  public void setWhyIsSet(boolean value) {
    if (!value) {
      this.why = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case WHAT_OP:
      if (value == null) {
        unsetWhatOp();
      } else {
        setWhatOp((Integer)value);
      }
      break;

    case WHY:
      if (value == null) {
        unsetWhy();
      } else {
        setWhy((String)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case WHAT_OP:
      return getWhatOp();

    case WHY:
      return getWhy();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case WHAT_OP:
      return isSetWhatOp();
    case WHY:
      return isSetWhy();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof InvalidOperation)
      return this.equals((InvalidOperation)that);
    return false;
  }

  public boolean equals(InvalidOperation that) {
    if (that == null)
      return false;

    boolean this_present_whatOp = true;
    boolean that_present_whatOp = true;
    if (this_present_whatOp || that_present_whatOp) {
      if (!(this_present_whatOp && that_present_whatOp))
        return false;
      if (this.whatOp != that.whatOp)
        return false;
    }

    boolean this_present_why = true && this.isSetWhy();
    boolean that_present_why = true && that.isSetWhy();
    if (this_present_why || that_present_why) {
      if (!(this_present_why && that_present_why))
        return false;
      if (!this.why.equals(that.why))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    List<Object> list = new ArrayList<Object>();

    boolean present_whatOp = true;
    list.add(present_whatOp);
    if (present_whatOp)
      list.add(whatOp);

    boolean present_why = true && (isSetWhy());
    list.add(present_why);
    if (present_why)
      list.add(why);

    return list.hashCode();
  }

  @Override
  public int compareTo(InvalidOperation other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetWhatOp()).compareTo(other.isSetWhatOp());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetWhatOp()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.whatOp, other.whatOp);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetWhy()).compareTo(other.isSetWhy());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetWhy()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.why, other.why);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("InvalidOperation(");
    boolean first = true;

    sb.append("whatOp:");
    sb.append(this.whatOp);
    first = false;
    if (!first) sb.append(", ");
    sb.append("why:");
    if (this.why == null) {
      sb.append("null");
    } else {
      sb.append(this.why);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class InvalidOperationStandardSchemeFactory implements SchemeFactory {
    public InvalidOperationStandardScheme getScheme() {
      return new InvalidOperationStandardScheme();
    }
  }

  private static class InvalidOperationStandardScheme extends StandardScheme<InvalidOperation> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, InvalidOperation struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // WHAT_OP
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.whatOp = iprot.readI32();
              struct.setWhatOpIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // WHY
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.why = iprot.readString();
              struct.setWhyIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, InvalidOperation struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(WHAT_OP_FIELD_DESC);
      oprot.writeI32(struct.whatOp);
      oprot.writeFieldEnd();
      if (struct.why != null) {
        oprot.writeFieldBegin(WHY_FIELD_DESC);
        oprot.writeString(struct.why);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class InvalidOperationTupleSchemeFactory implements SchemeFactory {
    public InvalidOperationTupleScheme getScheme() {
      return new InvalidOperationTupleScheme();
    }
  }

  private static class InvalidOperationTupleScheme extends TupleScheme<InvalidOperation> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, InvalidOperation struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetWhatOp()) {
        optionals.set(0);
      }
      if (struct.isSetWhy()) {
        optionals.set(1);
      }
      oprot.writeBitSet(optionals, 2);
      if (struct.isSetWhatOp()) {
        oprot.writeI32(struct.whatOp);
      }
      if (struct.isSetWhy()) {
        oprot.writeString(struct.why);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, InvalidOperation struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(2);
      if (incoming.get(0)) {
        struct.whatOp = iprot.readI32();
        struct.setWhatOpIsSet(true);
      }
      if (incoming.get(1)) {
        struct.why = iprot.readString();
        struct.setWhyIsSet(true);
      }
    }
  }

}

