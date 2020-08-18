
package com.mathandcs.kino.effectivejava.agile.builer;

/**
 *  * Created by dashwang on 2017-11-22.
 */
public class User {
    //All final attributes
    private final String firstName; // required
    private final String lastName; // required
    private final int age; // optional
    private final String phone; // optional
    private final String address; // optional

    public User(UserBuilder builder) {
        this.firstName = builder.firstName();
        this.lastName = builder.lastName();
        this.age = builder.age();
        this.phone = builder.phone();
        this.address = builder.address();
    }

    //All getter, and NO setter to provde immutability
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public int getAge() {
        return age;
    }
    public String getPhone() {
        return phone;
    }
    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return "User: "+this.firstName+", "+this.lastName+", "+this.age+", "+this.phone+", "+this.address;
    }

    public static void main(String[] args) {
        User user = new UserBuilder("Dash", "Wang")
                .age(22)
                .phone("123456789")
                .address("Beijing")
                .build();
        System.out.println(user);
    }

}
