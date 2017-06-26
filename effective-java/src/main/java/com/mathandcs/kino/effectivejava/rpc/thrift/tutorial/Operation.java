/**
 * Autogenerated by Thrift Compiler (0.9.3)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.mathandcs.kino.effectivejava.rpc.thrift.tutorial;


/**
 * You can define enums, which are just 32 bit integers. Values are optional
 * and start at 1 if not supplied, C style again.
 */
public enum Operation implements org.apache.thrift.TEnum {
  ADD(1),
  SUBTRACT(2),
  MULTIPLY(3),
  DIVIDE(4);

  private final int value;

  private Operation(int value) {
    this.value = value;
  }

  /**
   * Get the integer value of this enum value, as defined in the Thrift IDL.
   */
  public int getValue() {
    return value;
  }

  /**
   * Find a the enum type by its integer value, as defined in the Thrift IDL.
   * @return null if the value is not found.
   */
  public static Operation findByValue(int value) { 
    switch (value) {
      case 1:
        return ADD;
      case 2:
        return SUBTRACT;
      case 3:
        return MULTIPLY;
      case 4:
        return DIVIDE;
      default:
        return null;
    }
  }
}
