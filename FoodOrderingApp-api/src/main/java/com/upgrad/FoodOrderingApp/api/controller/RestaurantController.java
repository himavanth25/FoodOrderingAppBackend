package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.CategoryList;
import com.upgrad.FoodOrderingApp.api.model.RestaurantDetailsResponse;
import com.upgrad.FoodOrderingApp.api.model.RestaurantDetailsResponseAddress;
import com.upgrad.FoodOrderingApp.api.model.RestaurantDetailsResponseAddressState;
import com.upgrad.FoodOrderingApp.service.businness.RestaurantService;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<List<RestaurantDetailsResponse>> getAllRestaurants() {
        // TODO 1. - authorize token, return 401 UNAUTHORIZED if provided wrong credentials
        // TODO 2. - INTERNAL SERVER ERROR in case of unexpected error
        List<RestaurantDetailsResponse> responses = new ArrayList<>();
        for (RestaurantEntity restaurantEntity : restaurantService.getAllRestaurants()) {
            RestaurantDetailsResponse restaurantDetailsResponse = new RestaurantDetailsResponse();
            restaurantDetailsResponse.setId(UUID.fromString(restaurantEntity.getUuid()));
            restaurantDetailsResponse.setAddress(getRestaurantAddressResp(restaurantEntity));
            restaurantDetailsResponse.setAveragePrice(restaurantEntity.getAverage_price_for_two());
            restaurantDetailsResponse.setCategories(getRestaurantCategoryResp(restaurantEntity.getRestaurantCategories()));
            restaurantDetailsResponse.setCustomerRating(restaurantEntity.getCustomer_rating());
            restaurantDetailsResponse.setNumberCustomersRated(restaurantEntity.getNumber_of_customers_rated());
            restaurantDetailsResponse.setPhotoURL(restaurantEntity.getPhoto_url());
            restaurantDetailsResponse.setRestaurantName(restaurantEntity.getRestaurant_name());
            responses.add(restaurantDetailsResponse);
        }
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    /**
     * Prepare a restaurant detail response
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
     * Prepares the response object of Restaurant category
     * @param restaurantCategory
     * @return restaurant category in alphabetical order
     */
    private List<CategoryList> getRestaurantCategoryResp(List<CategoryEntity> restaurantCategory) {
        // sort category alphabetically
        Collections.sort(restaurantCategory, new Comparator<CategoryEntity>() {
            @Override
            public int compare(CategoryEntity o1, CategoryEntity o2) {
                return o1.getCategoryName().compareTo(o2.getCategoryName());
            }
        });
        List<CategoryList> categoryListList = new ArrayList<>();
        for (CategoryEntity category : restaurantCategory) {
            CategoryList categoryList = new CategoryList();
            categoryList.setCategoryName(category.getCategoryName());
            categoryList.setId(UUID.fromString(category.getUuid()));
            categoryListList.add(categoryList);
        }
        return categoryListList;
    }
}
