package com.wallet.crypto.trustapp.repository.entity;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class Contact extends RealmObject {

    @PrimaryKey
    private int id;
    private String name;
    private String phone;
    private String address;
    private String email;

    public int getId(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }

    public String getAddress(){
        return this.address;
    }

    public String getPhone(){
        return this.phone;
    }

    public String getEmail(){
        return this.email;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setAddress(String address){
        this.address = address;
    }

    public void setPhone(String phone){
        this.phone = phone;
    }

    public void setEmail(String email){
        this.email = email;
    }

}
