package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CategoryDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.dao.RestaurantDao;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RestaurantService {

    @Autowired
    private RestaurantDao restaurantDao;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private CustomerDao customerDao;


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

    public Boolean authenticate(String authToken) throws AuthenticationFailedException {
        CustomerAuthTokenEntity userAuthToken = customerDao.getUserAuthTokenByAccessToken(authToken);
        if(userAuthToken == null ) {
            throw new AuthenticationFailedException("ATHR-001", "Customer is not Logged in.");
        }
        if((userAuthToken != null && userAuthToken.getLogoutAt() != null)) {
            throw new AuthenticationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
        }
        Duration duration=Duration.between(ZonedDateTime.now(),userAuthToken.getExpiresAt());
        if((userAuthToken != null && duration.isNegative())) {
            throw new AuthenticationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");
        }
        return true;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public RestaurantEntity updateRating(Double rating, String restaurantUuid) throws RestaurantNotFoundException {
        return restaurantDao.updateRating(rating, restaurantUuid);
    }
}
