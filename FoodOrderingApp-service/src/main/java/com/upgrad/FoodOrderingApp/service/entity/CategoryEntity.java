package com.upgrad.FoodOrderingApp.service.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "CATEGORY")
public class CategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @NotNull
    @Column(name = "UUID")
    private String uuid;

    @NotNull
    @Column(name = "CATEGORY_NAME")
    private String categoryName;

    @ManyToMany(mappedBy = "restaurantCategories")
    private List<RestaurantEntity> restaurants;

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

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
