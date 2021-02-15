package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.AddressService;

import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;

import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/")
public class addressController {
    @Autowired
    AddressService addressService;

    @RequestMapping(method = RequestMethod.POST, path = "/address", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SaveAddressResponse> saveAddress(@RequestHeader("authorization") final String authorization,final SaveAddressRequest saveAddressRequests) throws SignUpRestrictedException {
        String authToken = authorization.startsWith("Bearer ") ? authorization.split("Bearer ")[1] : authorization;
        AddressEntity addressEntity = addressService.saveAddress(authToken,saveAddressRequests.getCity(),saveAddressRequests.getFlatBuildingName(),saveAddressRequests.getLocality()
                                    ,saveAddressRequests.getPincode(),saveAddressRequests.getStateUuid());
        SaveAddressResponse response=new SaveAddressResponse().id(addressEntity.getUuid()).status("ADDRESS SUCCESSFULLY REGISTERED");
        return new ResponseEntity<SaveAddressResponse>(response, HttpStatus.CREATED);
    }



    private String parseAuthToken(final String authorization){
        //Authorization header will be in the format "Bearer JWT-token"
        //Split the authorization header based on "Bearer " prefix to extract only the JWT token required for service class
        //If authorization header doesn't contain "Bearer " prefix then pass it as it is since it will be from test cases
        return authorization.startsWith("Bearer ")? authorization.split("Bearer ")[1]: authorization;
    }
}
