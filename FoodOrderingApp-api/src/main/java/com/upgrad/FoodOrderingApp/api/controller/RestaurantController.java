package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.RestaurantService;
import com.upgrad.FoodOrderingApp.service.common.UnexpectedException;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.InvalidRatingException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
                RestaurantList restaurant = getRestaurantListResponse(restaurantEntity);
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
            RestaurantList restaurant = getRestaurantListResponse(restaurantEntity);
            restaurantLists.add(restaurant);
        }
        restaurantListResponse.setRestaurants(restaurantLists);
        return new ResponseEntity(restaurantListResponse, HttpStatus.OK);
    }

    /**
     * End point for geting restaurants from category Id
     *
     * @param categoryUUID - search category Id
     * @return List of restaurants that serve searched category
     * @throws CategoryNotFoundException
     */
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
            RestaurantList restaurant = getRestaurantListResponse(restaurantEntity);
            restaurantLists.add(restaurant);
        }
        restaurantListResponse.setRestaurants(restaurantLists);
        return new ResponseEntity<>(restaurantListResponse, HttpStatus.OK);
    }

    /**
     * End point to get Restaurant details of a restaurant
     *
     * @param restaurantUUID
     * @return RestaurantDetailsResponse
     * @throws RestaurantNotFoundException
     */
    @RequestMapping(method = RequestMethod.GET, path = "/api/restaurant/{restaurant_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantDetailsResponse> getRestaurantById(
            @RequestBody @PathVariable("restaurant_id") final String restaurantUUID)
            throws RestaurantNotFoundException {
        if (restaurantUUID == null || restaurantUUID.isEmpty()) {
            throw new RestaurantNotFoundException("RNF-002", "Restaurant id field should not be empty)");
        }
        RestaurantEntity restaurantEntity = restaurantService.getRestaurantsById(restaurantUUID);
        return new ResponseEntity<>(getRestaurantDetailsResponse(restaurantEntity), HttpStatus.OK);
    }

    /**
     * Updates
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, path = "/api/restaurant/{restaurant_id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantUpdatedResponse> updateRestaurantDetails(
            @RequestBody @RequestParam(name = "customerRating") final Double rating,
            @RequestBody @PathVariable("restaurant_id") final String restaurantUUID,
            @RequestBody @RequestHeader("authorization") final String authToken)
            throws AuthenticationFailedException, RestaurantNotFoundException, InvalidRatingException {

        // authenticate logger-In user by checking its had valid auth token
        restaurantService.authenticate(authToken);
        if (restaurantUUID == null || restaurantUUID.isEmpty()) {
            throw new RestaurantNotFoundException("RNF-002", "Restaurant id field should not be empty)");
        }
        if (rating < 1 || rating > 5) {
            throw new InvalidRatingException("IRE-001", "Restaurant should be in the range of 1 to 5");
        }
        RestaurantUpdatedResponse restaurantUpdatedResponse = new RestaurantUpdatedResponse();
        RestaurantEntity restaurantEntity = restaurantService.updateRating(rating, restaurantUUID);
        restaurantUpdatedResponse.setId(UUID.fromString(restaurantEntity.getUuid()));
        restaurantUpdatedResponse.setStatus("RESTAURANT RATING UPDATED SUCCESSFULLY");
        return new ResponseEntity<>(restaurantUpdatedResponse, HttpStatus.OK);
    }

    private RestaurantDetailsResponse getRestaurantDetailsResponse(RestaurantEntity restaurantEntity) {
        RestaurantDetailsResponse restaurantDetailsResponse = new RestaurantDetailsResponse();
        restaurantDetailsResponse.setId(UUID.fromString(restaurantEntity.getUuid()));
        restaurantDetailsResponse.setAddress(getRestaurantAddressResp(restaurantEntity));
        restaurantDetailsResponse.setAveragePrice(restaurantEntity.getAveragePriceForTwo());
        restaurantDetailsResponse.setCategories(getRestaurantCategoryResp(restaurantEntity.getRestaurantCategories()));
        restaurantDetailsResponse.setCustomerRating(restaurantEntity.getCustomerRating());
        restaurantDetailsResponse.setNumberCustomersRated(restaurantEntity.getNumberOfCustomersRated());
        restaurantDetailsResponse.setPhotoURL(restaurantEntity.getPhotoUrl());
        restaurantDetailsResponse.setRestaurantName(restaurantEntity.getRestaurant_name());
        return restaurantDetailsResponse;
    }

    /**
     * Prepares a restaurant responseList
     *
     * @param restaurantEntity - restaurant Entity
     * @return restaurant responseList
     */
    private RestaurantList getRestaurantListResponse(RestaurantEntity restaurantEntity) {
        RestaurantList restaurant = new RestaurantList();
        restaurant.setId(UUID.fromString(restaurantEntity.getUuid()));
        restaurant.setAddress(getRestaurantAddressResp(restaurantEntity));
        restaurant.setAveragePrice(restaurantEntity.getAveragePriceForTwo());
        String categoryString = getRestaurantCategoryString(restaurantEntity.getRestaurantCategories());
        restaurant.setCategories(categoryString);
        restaurant.setCustomerRating(restaurantEntity.getCustomerRating());
        restaurant.setNumberCustomersRated(restaurantEntity.getNumberOfCustomersRated());
        restaurant.setPhotoURL(restaurantEntity.getPhotoUrl());
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
        restaurantDetailsResponseAddress.setCity(restaurantEntity.getAddressId().getCity());
        restaurantDetailsResponseAddress.setFlatBuildingName(restaurantEntity.getAddressId().getFlat_buil_number());
        restaurantDetailsResponseAddress.setId(UUID.fromString(restaurantEntity.getAddressId().getUuid()));
        restaurantDetailsResponseAddress.setLocality(restaurantEntity.getAddressId().getLocality());
        restaurantDetailsResponseAddress.setPincode(restaurantEntity.getAddressId().getPincode());
        restaurantDetailsResponseAddress.setState(getAddressStateResp(restaurantEntity.getAddressId().getState_id()));
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
            categoryList.setItemList(getItemListResp(category.getCategoryItems()));
            categoryListList.add(categoryList);
        }
        return categoryListList;
    }

    /**
     * prepares response List of items in a category
     *
     * @param items
     * @return
     */
    private List<ItemList> getItemListResp(List<ItemEntity> items) {
        List<ItemList> itemList = new ArrayList<>();
        for (ItemEntity item : items) {
            ItemList itemDetail = new ItemList();
            itemDetail.setId(UUID.fromString(item.getUuid()));
            itemDetail.setItemName(item.getItemName());
            if (item.getType() == 0) {
                itemDetail.setItemType(ItemList.ItemTypeEnum.VEG);
            } else {
                itemDetail.setItemType(ItemList.ItemTypeEnum.NON_VEG);
            }
            itemDetail.setPrice(item.getPrice().intValue());
            itemList.add(itemDetail);
        }
        return itemList;
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
