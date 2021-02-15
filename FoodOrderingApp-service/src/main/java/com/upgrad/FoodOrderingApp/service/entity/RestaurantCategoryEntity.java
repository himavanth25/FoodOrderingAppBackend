package com.upgrad.FoodOrderingApp.service.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "RESTAURANT_CATEGORY")
public class RestaurantCategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @JoinColumn(name = "RESTAURANT_ID")
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private RestaurantEntity restaurant;

    @JoinColumn(name = "CATEGORY_ID")
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private CategoryEntity category;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public RestaurantEntity getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(RestaurantEntity restaurant) {
        this.restaurant = restaurant;
    }

    public CategoryEntity getCategory() {
        return category;
    }

    public void setCategory(CategoryEntity category) {
        this.category = category;
    }
}
