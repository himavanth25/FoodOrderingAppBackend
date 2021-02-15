package com.upgrad.FoodOrderingApp.service.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "restaurant")
@NamedQueries({
        @NamedQuery(name = "restaurants", query = "select q from RestaurantEntity q order by q.customerRating desc"),
        @NamedQuery(name = "restaurantByName", query = "select q from RestaurantEntity q where lower(q.restaurant_name)" +
                " like lower(:searchResName) order by q.customerRating desc"),
        @NamedQuery(name = "restauranById", query = "select q from RestaurantEntity q where q.uuid = :uuid"),
})
public class RestaurantEntity implements Serializable {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "UUID")
    @NotNull
    private String uuid;

    @Column(name = "RESTAURANT_NAME")
    @NotNull
    private String restaurant_name;

    @Column(name = "PHOTO_URL")
    @NotNull
    private String photoUrl;

    @Column(name = "CUSTOMER_RATING")
    @NotNull
    private BigDecimal customerRating;

    @Column(name = "AVERAGE_PRICE_FOR_TWO")
    @NotNull
    private Integer averagePriceForTwo;

    @Column(name = "NUMBER_OF_CUSTOMERS_RATED")
    @NotNull
    private Integer numberOfCustomersRated;

    @ManyToOne
    @JoinColumn(name = "address_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private AddressEntity addressId;

    @ManyToMany()
    @JoinTable(name = "RESTAURANT_CATEGORY",
            joinColumns = @JoinColumn(name = "RESTAURANT_ID"),
            inverseJoinColumns = @JoinColumn(name = "CATEGORY_ID"))
    private List<CategoryEntity> restaurantCategories;

    @ManyToMany
    @JoinTable(name = "RESTAURANT_ITEM",
            joinColumns = @JoinColumn(name = "RESTAURANT_ID"),
            inverseJoinColumns = @JoinColumn(name = "ITEM_ID"))
    private List<ItemEntity> restaurantItems;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getRestaurant_name() {
        return restaurant_name;
    }

    public void setRestaurant_name(String restaurant_name) {
        this.restaurant_name = restaurant_name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photo_url) {
        this.photoUrl = photo_url;
    }

    public BigDecimal getCustomerRating() {
        return customerRating;
    }

    public void setCustomerRating(BigDecimal customer_rating) {
        this.customerRating = customer_rating;
    }

    public Integer getAveragePriceForTwo() {
        return averagePriceForTwo;
    }

    public void setAveragePriceForTwo(Integer average_price_for_two) {
        this.averagePriceForTwo = average_price_for_two;
    }

    public Integer getNumberOfCustomersRated() {
        return numberOfCustomersRated;
    }

    public void setNumberOfCustomersRated(Integer number_of_customers_rated) {
        this.numberOfCustomersRated = number_of_customers_rated;
    }

    public AddressEntity getAddressId() {
        return addressId;
    }

    public void setAddressId(AddressEntity address_id) {
        this.addressId = address_id;
    }

    public List<CategoryEntity> getRestaurantCategories() {
        return restaurantCategories;
    }

    public void setRestaurantCategories(List<CategoryEntity> restaurantCategories) {
        this.restaurantCategories = restaurantCategories;
    }

    public List<ItemEntity> getRestaurantItems() {
        return restaurantItems;
    }

    public void setRestaurantItems(List<ItemEntity> restaurantItems) {
        this.restaurantItems = restaurantItems;
    }
}
