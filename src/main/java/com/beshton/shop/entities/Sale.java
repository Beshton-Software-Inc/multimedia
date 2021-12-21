package com.beshton.shop.entities;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Arrays;
import java.util.Objects;

@Entity
public class Sale {

    private @Id
    @GeneratedValue
    Long id;
    private String itemName;
    private String sellerFirstName;
    private String sellerLastName;
    private String category;
    private Long price;
    private String postalCode;
    private String manufacturer;
    private String modelName;
    private Boolean used;
    private String description;
    private String saleStatus;
    private String timeStamp;
    private Long latitude;
    private Long longitude;

    Sale() {

    }

    public Sale(String itemName, String sellerFirstName, String sellerLastName, String category, Long price,
                String postalCode, String manufacturer, String modelName, Boolean used, String description,
                String saleStatus, String timeStamp, Long latitude, Long longitude) {
        this.itemName = itemName;
        this.sellerFirstName = sellerFirstName;
        this.sellerLastName = sellerLastName;
        this.category = category;
        this.price = price;
        this.postalCode = postalCode;
        this.manufacturer = manufacturer;
        this.modelName = modelName;
        this.used = used;
        this.description = description;
        this.saleStatus = saleStatus;
        this.timeStamp = timeStamp;
        this.latitude = latitude;
        this.longitude = longitude;
    }


    public List<Long> getLocation() {
        java.util.List<Long> latLongPair = Arrays.asList(new Long[]{this.latitude, this.longitude});
        return latLongPair;
    }

    public void setLocation(List<Long> location) {
        java.util.List<Long> latLongPair = location;
        this.latitude = latLongPair.get(0);
        this.longitude = latLongPair.get(1);
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

    public Boolean getUsed() {
        return used;
    }

    public void setUsed(Boolean used) {
        this.used = used;
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


    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;
        if (!(o instanceof Sale))
            return false;
        Sale sale = (Sale) o;
        return Objects.equals(this.id, sale.id) && Objects.equals(this.sellerFirstName, sale.sellerFirstName)
                && Objects.equals(this.sellerLastName, sale.sellerLastName) && Objects.equals(this.itemName, sale.itemName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.sellerFirstName, this.sellerLastName, this.itemName);
    }

    @Override
    public String toString() {
        return "Sale{" + "id=" + this.id + ", firstName='" + this.sellerFirstName + '\'' + ", lastName='" + this.sellerLastName
                + '\'' + ", role='" + this.itemName + '\'' + '}';
    }

    public Long getLatitude() {
        return latitude;
    }

    public void setLatitude(Long latitude) {
        this.latitude = latitude;
    }

    public Long getLongitude() {
        return longitude;
    }

    public void setLongitude(Long longitude) {
        this.longitude = longitude;
    }
}
