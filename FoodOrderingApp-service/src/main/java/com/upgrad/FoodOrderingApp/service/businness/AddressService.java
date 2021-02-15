package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.AddressDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service
public class AddressService {

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private AddressDao addressDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public AddressEntity saveAddress(String authTokenEntity, String city,String buildingName,String locality,String pincode,String stateUUID) throws SignUpRestrictedException {

        CustomerAuthTokenEntity customerAuthTokenEntity=customerDao.getUserAuthTokenByAccessToken(authTokenEntity);
        CustomerEntity customerEntity=customerAuthTokenEntity.getUser();
        AddressEntity createAddress=new AddressEntity();
        createAddress.setCity(city);
        createAddress.setFlat_buil_number(buildingName);
        createAddress.setLocality(locality);
        createAddress.setPincode(pincode);
        createAddress.setActive(1);
        createAddress.setState_id(addressDao.getState(stateUUID));
        createAddress.setUuid(UUID.randomUUID().toString());
        AddressEntity addressEntity=addressDao.createAddress(createAddress);
        CustomerAddressEntity customerAddressEntity=new CustomerAddressEntity();
        customerAddressEntity.setAddressID(addressEntity);
        customerAddressEntity.setCustomerID(customerEntity);
        CustomerAddressEntity customerAddressEntity1=addressDao.setCustomerAddress(customerAddressEntity);
        return addressEntity;

    }


}
