package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class CustomerService {
    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    @Autowired
    private CustomerDao customerDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity signup(CustomerEntity customerEntity) throws SignUpRestrictedException {

        //Throw exception if given user name already exists
        if (customerDao.getUserByPhoneNumber(customerEntity.getContactNumber()) != null) {
            throw new SignUpRestrictedException("SGR-001", "This contact number is already registered! Try other contact number.");
        }

        //Throw exception if given email address already exists
        if (customerEntity.getContactNumber() == null || customerEntity.getEmail() == null || customerEntity.getFirstName() == null
                || customerEntity.getPassword() == null) {
            throw new SignUpRestrictedException("SGR-005", "Except last name all fields should be filled");
        }

        if (!customerEntity.getContactNumber().matches("[0-9]{10}")) {
            throw new SignUpRestrictedException("SGR-003", "Invalid contact number!");
        }
        if (!customerEntity.getEmail().matches("[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new SignUpRestrictedException("SGR-002", "Invalid email-id format!");
        }

        if (!customerEntity.getPassword().matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[#@$%&*!^])(?=\\\\S+$).{8,20}$")) {
            throw new SignUpRestrictedException("SGR-004", "Weak password!");
        }

        //Get encrypted password from cryptography provider, the method returns an array of Strings
        //Salt will be at index 0 in the return array and encrypted password will be at index 1
        String[] encryptedText = cryptographyProvider.encrypt(customerEntity.getPassword());
        customerEntity.setSalt(encryptedText[0]);
        customerEntity.setPassword(encryptedText[1]);

        return customerDao.createUser(customerEntity);
    }
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthTokenEntity signin(final String userName, final String password) throws AuthenticationFailedException {
        CustomerEntity userEntity = customerDao.getUserByUserName(userName);

        //Throw exception if given user name does not exist in database
        if(userEntity == null){
            throw new AuthenticationFailedException("ATH-001","This contact number has not been registered!");
        }

        final String encryptedPassword = cryptographyProvider.encrypt(password, userEntity.getSalt());

        //Throw exception if provided password does not match with the password stored in database
        if(!encryptedPassword.equals(userEntity.getPassword())){
            throw new AuthenticationFailedException("ATH-002","Invalid Credentials");
        }

        //Construct a JWT token using JwtTokenProvider and persist it in the database before returning to controller
        JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
        CustomerAuthTokenEntity userAuthToken = new CustomerAuthTokenEntity();
        userAuthToken.setUuid(UUID.randomUUID().toString());
        userAuthToken.setUser(userEntity);
        final ZonedDateTime now = ZonedDateTime.now();
        final ZonedDateTime expiresAt = now.plusHours(8);
        userAuthToken.setAccessToken(jwtTokenProvider.generateToken(userEntity.getUuid(),now,expiresAt));
        userAuthToken.setLoginAt(now);
        userAuthToken.setExpiresAt(expiresAt);

        customerDao.createAuthToken(userAuthToken);

        return userAuthToken;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity signout(final String authorization) throws AuthenticationFailedException {

        CustomerAuthTokenEntity userAuthToken = customerDao.getUserAuthTokenByAccessToken(authorization);

        //Throw exception if either the JWT access token is invalid orer if the user has already signed out
        //In case user has already signed out then the logout time of the user will not be null
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

        userAuthToken.setLogoutAt(ZonedDateTime.now());
        customerDao.updateLogOutTime(userAuthToken);
        return userAuthToken.getUser();
    }
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity updateCustomer(final String authorization,String firstName,String lastName) throws UpdateCustomerException, AuthenticationFailedException {
        CustomerAuthTokenEntity authTokenEntity= customerDao.getUserAuthTokenByAccessToken(authorization);
        if(authTokenEntity == null ) {
            throw new AuthenticationFailedException("ATHR-001", "Customer is not Logged in.");
        }

        if((authTokenEntity != null && authTokenEntity.getLogoutAt() != null)) {
            throw new AuthenticationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
        }
        Duration duration=Duration.between(ZonedDateTime.now(),authTokenEntity.getExpiresAt());
        if((authTokenEntity != null && duration.isNegative())) {
            throw new AuthenticationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");
        }
        if(firstName==null || firstName.isEmpty()){
            throw new UpdateCustomerException("UCR-002","First name field should not be empty");
        }
        CustomerEntity customer=authTokenEntity.getUser();
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customerDao.updateCustomerDetails(customer);
        return customer;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity updatePassword(final String authorization,String oldPassword,String newPassword) throws UpdateCustomerException, AuthenticationFailedException {
        CustomerAuthTokenEntity authTokenEntity= customerDao.getUserAuthTokenByAccessToken(authorization);
        if(authTokenEntity == null ) {
            throw new AuthenticationFailedException("ATHR-001", "Customer is not Logged in.");
        }

        if((authTokenEntity != null && authTokenEntity.getLogoutAt() != null)) {
            throw new AuthenticationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
        }
        Duration duration=Duration.between(ZonedDateTime.now(),authTokenEntity.getExpiresAt());
        if((authTokenEntity != null && duration.isNegative())) {
            throw new AuthenticationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");
        }

        CustomerEntity customer=authTokenEntity.getUser();

        if(!customer.getPassword().equals(oldPassword)){
            throw new UpdateCustomerException("UCR-004", "Incorrect old password!");
        }
        if (!customer.getPassword().matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[#@$%&*!^]).{8,20}")) {
            throw new UpdateCustomerException("UCR-001", "Weak password!");
        }
        customer.setPassword(newPassword);
        customerDao.updateCustomerDetails(customer);
        return customer;
    }

}
