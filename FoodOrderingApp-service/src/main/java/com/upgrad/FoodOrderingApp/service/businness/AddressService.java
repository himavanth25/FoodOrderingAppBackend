package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.AddressDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.UUID;


@Service
public class AddressService {

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private AddressDao addressDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public AddressEntity saveAddress(String authTokenEntity, String city,String buildingName,String locality,String pincode,String stateUUID) throws SignUpRestrictedException, AuthenticationFailedException, SaveAddressException, AddressNotFoundException {

        CustomerAuthTokenEntity customerAuthTokenEntity=customerDao.getUserAuthTokenByAccessToken(authTokenEntity);
        if(customerAuthTokenEntity == null ) {
            throw new AuthenticationFailedException("ATHR-001", "Customer is not Logged in.");
        }
        if((customerAuthTokenEntity != null && customerAuthTokenEntity.getLogoutAt() != null)) {
            throw new AuthenticationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
        }
        Duration duration=Duration.between(ZonedDateTime.now(),customerAuthTokenEntity.getExpiresAt());
        if((customerAuthTokenEntity != null && duration.isNegative())) {
            throw new AuthenticationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");
        }
        if (city == null || buildingName ==null || locality==null || pincode==null ||stateUUID==null) {
            throw new SaveAddressException("SAR-001","No field can be empty");
        }
        if(!pincode.matches("[0-9]{6}")){
            throw new SaveAddressException("SAR-002","Invalid pincode");
        }
        CustomerEntity customerEntity=customerAuthTokenEntity.getUser();
        AddressEntity createAddress=new AddressEntity();
        createAddress.setCity(city);
        createAddress.setFlat_buil_number(buildingName);
        createAddress.setLocality(locality);
        createAddress.setPincode(pincode);
        createAddress.setActive(1);
        StateEntity add=addressDao.getState(stateUUID);
        if(add==null){
            throw new AddressNotFoundException("ANF-002","No state by this id");
        }
        createAddress.setState_id(add);
        createAddress.setUuid(UUID.randomUUID().toString());
        AddressEntity addressEntity=addressDao.createAddress(createAddress);
        CustomerAddressEntity customerAddressEntity=new CustomerAddressEntity();
        customerAddressEntity.setAddressID(addressEntity);
        customerAddressEntity.setCustomerID(customerEntity);
        CustomerAddressEntity customerAddressEntity1=addressDao.setCustomerAddress(customerAddressEntity);
        return addressEntity;

    }


}
