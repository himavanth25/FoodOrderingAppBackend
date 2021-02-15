package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.common.UnexpectedException;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * This class is used to interact with database related to Restaurants
 */
@Repository
public class RestaurantDao {

    @PersistenceContext
    private EntityManager entityManager;

    public List<RestaurantEntity> getAllRestaurants() {
        return entityManager.createNamedQuery("restaurants", RestaurantEntity.class).getResultList();
    }

    public List<RestaurantEntity> getAllRestaurantsByName(String searchResName) {
        TypedQuery<RestaurantEntity> restaurants = entityManager.createNamedQuery("restaurantByName",
                RestaurantEntity.class);
        restaurants.setParameter("searchResName", "%" + searchResName + "%");
        return restaurants.getResultList();
    }
}
