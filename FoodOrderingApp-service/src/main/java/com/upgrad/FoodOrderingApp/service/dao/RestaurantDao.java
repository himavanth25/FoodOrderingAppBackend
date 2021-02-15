package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;
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

    public RestaurantEntity getRestaurantsById(String restaurantUUID) throws RestaurantNotFoundException {
        TypedQuery<RestaurantEntity> restaurant = entityManager.createNamedQuery("restauranById", RestaurantEntity.class);
        restaurant.setParameter("uuid", restaurantUUID);
        // Should always get 1 record from db
        List<RestaurantEntity> resultList = restaurant.getResultList();
        if (resultList.size() == 1) {
            return resultList.get(0);
        }
        throw new RestaurantNotFoundException("RNF-001", "No restaurant by this id");
    }

    public RestaurantEntity updateRating(Double rating, String restaurantUuid) throws RestaurantNotFoundException {
        RestaurantEntity restaurantEntity = getRestaurantsById(restaurantUuid);
        if(restaurantEntity != null){
            Integer numOfRatings = restaurantEntity.getNumberOfCustomersRated();
            restaurantEntity.setNumberOfCustomersRated(++numOfRatings);
            double avgRating = restaurantEntity.getCustomerRating().doubleValue();
            avgRating = (avgRating + rating)/numOfRatings;
            restaurantEntity.setCustomerRating(new BigDecimal(avgRating));
            entityManager.merge(restaurantEntity);
        }
        return restaurantEntity;
    }
}
