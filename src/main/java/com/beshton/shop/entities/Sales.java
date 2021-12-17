package com.beshton.shop.entities;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import  java.util.Date;
import java.util.Arrays;

@Entity
public class Sales {

    private @Id
    @GeneratedValue
    Long id;
    // do I need to create a constructor for id?
    private String itemName;
    private String sellerFirstName;
    private String sellerLastName;
    private String category;
    private Long price;
    private String postalCode;
    private String manufacturer;
    private String modelName;
    private String condition;
    private String description;
    private String saleStatus;
    private Date date;
    private Long latitude;
    private Long longitude;

    Sales() {

    }

    public Sales(Long id, String itemName, String sellerFirstName, String sellerLastName, String category, Long price, String postalCode, String manufacturer, String modelName, String condition, String description, String saleStatus, Date date, Long latitude, Long longitude) {
        this.id = id;
        this.itemName = itemName;
        this.sellerFirstName = sellerFirstName;
        this.sellerLastName = sellerLastName;
        this.category = category;
        this.price = price;
        this.postalCode = postalCode;
        this.manufacturer = manufacturer;
        this.modelName = modelName;
        this.condition = condition;
        this.description = description;
        this.saleStatus = saleStatus;
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
    }


    public List<Long> getLocation() {
        java.util.List<Long> latLongPair = Arrays.asList(new Long[]{this.latitude, this.longitude});
        return latLongPair;
    }

    public void setLocation(List<Long> location) {
        java.util.List<Long> latLongPair = location;
        this.latitude = latLongPair[0];
        this.longitude = latLongPair[1];
    }

    public String getSellerName() {
        return this.sellerFirstName + " " + this.sellerLastName;
    }

    public void setSellerName(String name) {
        String[] nameParts = name.split(" ");
        this.sellerFirstName = nameParts[0];
        this.sellerLastName = nameParts[1];
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getSellerFirstName() {
        return sellerFirstName;
    }

    public void setSellerFirstName(String sellerFirstName) {
        this.sellerFirstName = sellerFirstName;
    }

    public String getSellerLastName() {
        return sellerLastName;
    }

    public void setSellerLastName(String sellerLastName) {
        this.sellerLastName = sellerLastName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSaleStatus() {
        return saleStatus;
    }

    public void setSaleStatus(String saleStatus) {
        this.saleStatus = saleStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
