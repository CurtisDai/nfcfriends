package com.nfc.application;

import com.google.firebase.storage.StorageReference;

public class BusinessCard {
    private String uri;
    private String name;
    private String organization;
    private String address;
    private String telephone;
    private String email;
    private boolean isFront;
    private StorageReference cover_url;
    private StorageReference profilePic_url;

    public BusinessCard( ){

    }
    public void setUri(String uri){
        this.uri = uri;
    }

    public String getUri(){
        return uri;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void setOrganization(String organization){
        this.organization = organization;
    }

    public String getOrganization(){
        return organization;
    }

    public void setAddress(String address){
        this.address = address;
    }

    public String getAddress(){
        return address;
    }

    public void setTelephone(String telephone){
        this.telephone = telephone;
    }

    public String getTelephone(){
        return telephone;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getEmail(){
        return email;
    }

    //show the card
    public boolean isFront() {
        return isFront;
    }

    public void setFront(boolean front) {
        isFront = front;
    }

    public StorageReference getCover_url() {
        return this.cover_url;
    }

    public void setCover_url(StorageReference cover_url) {
        this.cover_url  = cover_url;
    }

    public StorageReference getProfilePic_url() {
        return this.profilePic_url;
    }

    public void setProfilePic_url(StorageReference profilePic_url) {
        this.profilePic_url  = profilePic_url;
    }
}
