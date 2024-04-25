package com.example.entity;


import java.io.Serializable;

public class User extends Account implements Serializable {  // 这个接口是一个标记接口，不包含任何方法，只是用来指示编译器这个类可以被序列化。也就是可以转换成字节流进行传输或者持久化到磁盘上。
    private static final long serialVersionUID = 1L; // 主要是为了控制版本的一致性，确保在对象的序列化和反序列化过程中不会出现版本不匹配的问题

    private Integer id;
    private String username;
    private String password;
    private String name;
    private String avatar;
    private String role;
    private String sex;
    private String phone;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
