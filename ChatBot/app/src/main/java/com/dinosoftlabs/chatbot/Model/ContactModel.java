package com.dinosoftlabs.chatbot.Model;

public class ContactModel {
    String name,email,surl;

    ContactModel(){

    }
    public ContactModel(String name, String email, String surl) {
        this.name = name;
        this.email = email;
        this.surl = surl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public  String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSurl() {
        return surl;
    }

    public void setSurl(String surl) {
        this.surl = surl;
    }
}
