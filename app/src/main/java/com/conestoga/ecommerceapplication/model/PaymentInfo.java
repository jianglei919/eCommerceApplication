package com.conestoga.ecommerceapplication.model;

import java.io.Serializable;

public class PaymentInfo implements Serializable {
    private String firstName;
    private String lastName;
    private String address;
    private String unitNumber;
    private String city;
    private String state;
    private String postalCode;
    private String phone;
    private String email;
    private String paymentMethod;
    private String cardNumber;
    private String cvv;

    public PaymentInfo() {
    }

    public PaymentInfo(String firstName, String lastName, String address, String unitNumber,
                       String city, String state, String postalCode, String phone, String email,
                       String paymentMethod, String cardNumber, String cvv) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.unitNumber = unitNumber;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.phone = phone;
        this.email = email;
        this.paymentMethod = paymentMethod;
        this.cardNumber = cardNumber;
        this.cvv = cvv;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUnitNumber() {
        return unitNumber;
    }

    public void setUnitNumber(String unitNumber) {
        this.unitNumber = unitNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }
}
