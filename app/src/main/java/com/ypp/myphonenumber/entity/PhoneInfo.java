package com.ypp.myphonenumber.entity;

/**
 * Created by adminstrator on 2016/12/2.
 */
public class PhoneInfo {
    public String phoneName;
    public String phoneNumber;

    public PhoneInfo() {
    }

    public PhoneInfo(String phoneName, String phoneNumber) {
        this.phoneName = phoneName;
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneName() {
        return phoneName;
    }

    public void setPhoneName(String phoneName) {
        this.phoneName = phoneName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "\nPhoneInfo{" +
                "phoneName='" + phoneName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
