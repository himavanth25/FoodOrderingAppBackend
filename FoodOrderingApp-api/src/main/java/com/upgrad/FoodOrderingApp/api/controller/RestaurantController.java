package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.RestaurantService;
import com.upgrad.FoodOrderingApp.service.common.UnexpectedException;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.*;


@RestController
@RequestMapping("/")
@CrossOrigin
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    /**
     * End point for getting detailed list of all restaurants
     *
     * @return List of RestaurantDetailsResponse
     */
    @RequestMapping(method = RequestMethod.GET, path = "restaurant", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getAllRestaurants() {
        // TODO 1. - authorize token, return 401 UNAUTHORIZED if provided wrong credentials
        RestaurantListResponse restaurantListResponse = new RestaurantListResponse();
        List<RestaurantList> restaurantLists = new ArrayList<>();
        restaurantListResponse.setRestaurants(restaurantLists);

        try {
            List<RestaurantEntity> allRestaurants = restaurantService.getAllRestaurants();
            if (allRestaurants.isEmpty()) {
                return new ResponseEntity(restaurantListResponse, HttpStatus.NO_CONTENT);
            }
            for (RestaurantEntity restaurantEntity : allRestaurants) {
                RestaurantList restaurant = getRestaurantResponse(restaurantEntity);
                restaurantLists.add(restaurant);
            }
            return new ResponseEntity(restaurantListResponse, HttpStatus.OK);
            // INTERNAL SERVER ERROR in case of unexpected error
        } catch (UnexpectedException ue) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * search restaurant by name and return list of matching results
     *
     * @param restaurantName
     * @return
     * @throws RestaurantNotFoundException
     */
    @RequestMapping(method = RequestMethod.GET, path = "restaurant/name/{restaurant_name}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getRestaurantsByName(@RequestBody(required = false) @PathVariable("restaurant_name") final String restaurantName) throws RestaurantNotFoundException {
        if (restaurantName == null || restaurantName.isEmpty()) {
            throw new RestaurantNotFoundException("RNF-003", "Restaurant name field should not be empty");
        }
        RestaurantListResponse restaurantListResponse = new RestaurantListResponse();
        List<RestaurantList> restaurantLists = new ArrayList<>();

        List<RestaurantEntity> allRestaurants = restaurantService.getAllRestaurantsByName(restaurantName);
        if (allRestaurants.isEmpty()) {
            return new ResponseEntity(restaurantListResponse, HttpStatus.NO_CONTENT);
        }
        for (RestaurantEntity restaurantEntity : allRestaurants) {
            RestaurantList restaurant = getRestaurantResponse(restaurantEntity);
            restaurantLists.add(restaurant);
        }
        restaurantListResponse.setRestaurants(restaurantLists);
        return new ResponseEntity(restaurantListResponse, HttpStatus.OK);
    }

    // Get Restaurants by Category Id “/restaurant/category/{category_id}”
    @RequestMapping(method = RequestMethod.GET, path = "/restaurant/category/{category_id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getRestaurantByCategory(
            @RequestBody @PathVariable("category_id") final String categoryUUID)
            throws CategoryNotFoundException {
        if (categoryUUID == null || categoryUUID.isEmpty()) {
            throw new CategoryNotFoundException("CNF-001", "Category id field should not be empty");
        }
        RestaurantListResponse restaurantListResponse = new RestaurantListResponse();
        List<RestaurantList> restaurantLists = new ArrayList<>();

        List<RestaurantEntity> allRestaurants = restaurantService.getAllRestaurantsByCategoryId(categoryUUID);
        for (RestaurantEntity restaurantEntity : allRestaurants) {
            RestaurantList restaurant = getRestaurantResponse(restaurantEntity);
            restaurantLists.add(restaurant);
        }
        restaurantListResponse.setRestaurants(restaurantLists);
        return new ResponseEntity<>(restaurantListResponse, HttpStatus.OK);
    }

    /**
     * Prepares a restaurant responseList
     *
     * @param restaurantEntity - restaurant Entity
     * @return restaurant responseList
     */
    private RestaurantList getRestaurantResponse(RestaurantEntity restaurantEntity) {
        RestaurantList restaurant = new RestaurantList();
        restaurant.setId(UUID.fromString(restaurantEntity.getUuid()));
        restaurant.setAddress(getRestaurantAddressResp(restaurantEntity));
        restaurant.setAveragePrice(restaurantEntity.getAverage_price_for_two());
        String categoryString = getRestaurantCategoryString(restaurantEntity.getRestaurantCategories());
        restaurant.setCategories(categoryString);
        restaurant.setCustomerRating(restaurantEntity.getCustomer_rating());
        restaurant.setNumberCustomersRated(restaurantEntity.getNumber_of_customers_rated());
        restaurant.setPhotoURL(restaurantEntity.getPhoto_url());
        restaurant.setRestaurantName(restaurantEntity.getRestaurant_name());
        return restaurant;
    }

    /**
     * Prepare a restaurant Address response
     *
     * @param restaurantEntity - restaurant Entity
     * @return RestaurantDetailsResponseAddress
     */
    private RestaurantDetailsResponseAddress getRestaurantAddressResp(RestaurantEntity restaurantEntity) {
        RestaurantDetailsResponseAddress restaurantDetailsResponseAddress = new RestaurantDetailsResponseAddress();
        restaurantDetailsResponseAddress.setCity(restaurantEntity.getAddress_id().getCity());
        restaurantDetailsResponseAddress.setFlatBuildingName(restaurantEntity.getAddress_id().getFlat_buil_number());
        restaurantDetailsResponseAddress.setId(UUID.fromString(restaurantEntity.getAddress_id().getUuid()));
        restaurantDetailsResponseAddress.setLocality(restaurantEntity.getAddress_id().getLocality());
        restaurantDetailsResponseAddress.setPincode(restaurantEntity.getAddress_id().getPincode());
        restaurantDetailsResponseAddress.setState(getAddressStateResp(restaurantEntity.getAddress_id().getState_id()));
        return restaurantDetailsResponseAddress;
    }

    /**
     * This method prepares response object of Address State
     *
     * @param stateEntity address state entity
     * @return State Response
     */
    private RestaurantDetailsResponseAddressState getAddressStateResp(StateEntity stateEntity) {
        RestaurantDetailsResponseAddressState restaurantDetailsResponseAddressState = new RestaurantDetailsResponseAddressState();
        restaurantDetailsResponseAddressState.setId(UUID.fromString(stateEntity.getUuid()));
        restaurantDetailsResponseAddressState.setStateName(stateEntity.getState_name());
        return restaurantDetailsResponseAddressState;
    }

    /**
     * Prepares the detailed response object of Restaurant category
     *
     * @param restaurantCategory
     * @return restaurant category in alphabetical order
     */
    private List<CategoryList> getRestaurantCategoryResp(List<CategoryEntity> restaurantCategory) {
        List<CategoryList> categoryListList = new ArrayList<>();
        for (CategoryEntity category : restaurantCategory) {
            CategoryList categoryList = new CategoryList();
            categoryList.setCategoryName(category.getCategoryName());
            categoryList.setId(UUID.fromString(category.getUuid()));
            categoryListList.add(categoryList);
        }
        return categoryListList;
    }

    /**
     * @param restaurantCategory
     * @return - comma separated string of categories of restaurant
     */
    private String getRestaurantCategoryString(List<CategoryEntity> restaurantCategory) {
        // sort category alphabetically
        Collections.sort(restaurantCategory, new Comparator<CategoryEntity>() {
            @Override
            public int compare(CategoryEntity o1, CategoryEntity o2) {
                return o1.getCategoryName().compareTo(o2.getCategoryName());
            }
        });

        StringBuilder category = new StringBuilder();
        Iterator<CategoryEntity> itr = restaurantCategory.iterator();
        while (itr.hasNext()) {
            category.append(itr.next().getCategoryName());
            if (itr.hasNext()) {
                category.append(",");
            }
        }
        return category.toString();
    }
}
