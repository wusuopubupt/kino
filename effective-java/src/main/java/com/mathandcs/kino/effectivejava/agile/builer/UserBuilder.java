package com.mathandcs.kino.effectivejava.agile.builer;

/**
 * Created by dashwang on 11/22/17.
 *
 * 参考: http://www.jianshu.com/p/e2a2fe3555b9
 *
 */
public class UserBuilder {
    private final String firstName;
    private final String lastName;
    private int age;
    private String phone;
    private String address;

    // 非空属性，必须在构造器中指定
    public UserBuilder(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String firstName() {
        return firstName;
    }

    public String lastName() {
        return lastName;
    }

    public UserBuilder age(int age) {
        this.age = age;
        return this;
    }

    public int age() {
        return this.age;
    }

    public String phone() {
        return this.phone;
    }

    public UserBuilder phone(String phone) {
        this.phone = phone;
        return this;
    }

    public String address() {
        return this.address;
    }

    public UserBuilder address(String address) {
        this.address = address;
        return this;
    }

    //Return the finally consrcuted User object
    public User build() {
        User user = new User(this);
        return user;
    }

}