package saeed.life.Model;

import java.io.Serializable;

public class User implements Serializable {

    private String name;
    private String email;
    private String bloodType;
    private String phone;
    private String gender;
    private String token;

    public User() {
    }

    public User(String name, String email, String bloodType, String phone, String gender, String token) {
        this.name = name;
        this.email = email;
        this.bloodType = bloodType;
        this.phone = phone;
        this.gender = gender;
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getBloodType() {
        return bloodType;
    }

    public String getPhone() {
        return phone;
    }

    public String getGender() {
        return gender;
    }

    public String getToken() {
        return token;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
