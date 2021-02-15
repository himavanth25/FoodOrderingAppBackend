package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.AddressService;

import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;

import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/")
public class addressController {
    @Autowired
    AddressService addressService;

    @RequestMapping(method = RequestMethod.POST, path = "/address", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SaveAddressResponse> saveAddress(@RequestHeader("authorization") final String authorization,final SaveAddressRequest saveAddressRequests) throws SignUpRestrictedException, AuthenticationFailedException, AddressNotFoundException, SaveAddressException {
        String authToken = authorization.startsWith("Bearer ") ? authorization.split("Bearer ")[1] : authorization;
        AddressEntity addressEntity = addressService.saveAddress(authToken,saveAddressRequests.getCity(),saveAddressRequests.getFlatBuildingName(),saveAddressRequests.getLocality()
                                    ,saveAddressRequests.getPincode(),saveAddressRequests.getStateUuid());
        SaveAddressResponse response=new SaveAddressResponse().id(addressEntity.getUuid()).status("ADDRESS SUCCESSFULLY REGISTERED");
        return new ResponseEntity<SaveAddressResponse>(response, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/address/customer",  produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity <AddressListResponse> getAllAddress(@RequestHeader("authorization") final String authorization) throws SignUpRestrictedException, AuthenticationFailedException, AddressNotFoundException, SaveAddressException {
        String authToken = authorization.startsWith("Bearer ") ? authorization.split("Bearer ")[1] : authorization;
        List<AddressEntity> addressEntity = addressService.getAllAddress(authToken);
        List<AddressList> allAddress=new ArrayList<>();
        for (AddressEntity ent:addressEntity) {
            AddressList list=new AddressList();
            list.city(ent.getCity());
            list.flatBuildingName(ent.getFlat_buil_number());
            list.locality(ent.getLocality());
            list.pincode(ent.getPincode());


            allAddress.add(list);
        }

        AddressListResponse response=new AddressListResponse().addresses(allAddress);
        return new ResponseEntity<AddressListResponse>(response, HttpStatus.CREATED);
    }



    private String parseAuthToken(final String authorization){
        //Authorization header will be in the format "Bearer JWT-token"
        //Split the authorization header based on "Bearer " prefix to extract only the JWT token required for service class
        //If authorization header doesn't contain "Bearer " prefix then pass it as it is since it will be from test cases
        return authorization.startsWith("Bearer ")? authorization.split("Bearer ")[1]: authorization;
    }
}
