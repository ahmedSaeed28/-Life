package saeed.life.Model;

import java.io.Serializable;

public class DonationRequest implements Serializable{

    private String  name, bloodType, city, hospital, phone, userId, userName;

    public DonationRequest(String name, String bloodType, String city, String hospital, String phone,
                           String userId, String userName) {
        this.name = name;
        this.bloodType = bloodType;
        this.city = city;
        this.hospital = hospital;
        this.phone = phone;
        this.userId = userId;
        this.userName = userName;
    }

    public String getName() {
        return name;
    }

    public String getBloodType() {
        return bloodType;
    }

    public String getCity() {
        return city;
    }

    public String getHospital() {
        return hospital;
    }

    public String getPhone() {
        return phone;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public boolean equals(Object obj) {
        DonationRequest donationRequest = (DonationRequest)obj;
        if(name.equals(donationRequest.getName()) && bloodType.equals(donationRequest.getBloodType()) &&
                city.equals(donationRequest.getCity()) && hospital.equals(donationRequest.getHospital()) &&
                phone.equals(donationRequest.getPhone()) && userId.equals(donationRequest.getUserId()) &&
                userName.equals(donationRequest.getUserName())){
            return true;
        }
        return  false;
    }
}
