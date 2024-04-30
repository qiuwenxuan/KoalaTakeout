package com.example.entity;

import java.io.Serializable;

/**
 * 商家表
 **/
public class Business extends Account implements Serializable {
    private static final long serialVersionUID = 1L;
    private String phone;
    private String info;
    private String address;
    private String license;
    private String status;
    private String timeRange;
    private String type;
    /**
     * 是否收藏
     **/
    private boolean isCollect;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimeRange() {
        return timeRange;
    }

    public void setTimeRange(String timeRange) {
        this.timeRange = timeRange;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean getIsCollect() {
        return isCollect;
    }

    public void setIsCollect(boolean isCollect) {
        this.isCollect = isCollect;
    }
}
