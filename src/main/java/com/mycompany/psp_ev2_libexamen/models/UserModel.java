package com.mycompany.psp_ev2_libexamen.models;

public class UserModel {

    // Propiedades
    private int id;
    private String name;
    private String pass;

    // Contructores
    public UserModel() {} // Contructor vacio

    public UserModel(int id, String name, String pass) {
        this.id = id;
        this.name = name;
        this.pass = pass;
    }

    // Getters & Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    @Override
    public String toString() {
        return "UserModel{" + "id=" + id + ", name=" + name + ", pass=" + pass + '}';
    }

    
    
}

