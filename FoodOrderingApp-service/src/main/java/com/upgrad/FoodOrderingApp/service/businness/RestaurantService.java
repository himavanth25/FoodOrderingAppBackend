package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.common.GenericErrorCode;
import com.upgrad.FoodOrderingApp.service.common.UnexpectedException;
import com.upgrad.FoodOrderingApp.service.dao.CategoryDao;
import com.upgrad.FoodOrderingApp.service.dao.RestaurantDao;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.List;

@Service
public class RestaurantService {

    @Autowired
    private RestaurantDao restaurantDao;

    @Autowired
    private CategoryDao categoryDao;


    /**
     * @return - List of All Restaurants
     */
    public List<RestaurantEntity> getAllRestaurants() {
        List<RestaurantEntity> restaurants = restaurantDao.getAllRestaurants();
        if(restaurants.isEmpty()){
            restaurants = new ArrayList<>();
        }
        return restaurants;
    }

    /**
     * @param - restaurant name to be searched
     * @return - List of All Restaurants that matches search keyword
     */
    public List<RestaurantEntity> getAllRestaurantsByName(String searchResName) throws RestaurantNotFoundException {
        if (searchResName == null || searchResName.isEmpty()) {
            throw new RestaurantNotFoundException("RNF-003", "Restaurant name field should not be empty");
        }
        List<RestaurantEntity> restaurants = restaurantDao.getAllRestaurantsByName(searchResName);
        if(restaurants.isEmpty()){
            restaurants = new ArrayList<>();
        }
        return restaurants;
    }

    public List<RestaurantEntity> getAllRestaurantsByCategoryId(String categoryUUID) throws CategoryNotFoundException {
        CategoryEntity categoryEntities = categoryDao.getCategoryByUuid(categoryUUID);
        List<RestaurantEntity> restaurantEntities = new ArrayList<>();
        if(categoryEntities.getRestaurants() != null){
            restaurantEntities = categoryEntities.getRestaurants();
            return restaurantEntities;
        }
        else{
            throw new CategoryNotFoundException("CNF-002", "No category by this id");
        }
    }

    public RestaurantEntity getRestaurantsById(String restaurantUUID) throws RestaurantNotFoundException {
        RestaurantEntity restaurantEntity = restaurantDao.getRestaurantsById(restaurantUUID);
        return restaurantEntity;
    }
}
