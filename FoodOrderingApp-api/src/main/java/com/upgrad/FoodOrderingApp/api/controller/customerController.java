package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.LoginResponse;
import com.upgrad.FoodOrderingApp.api.model.LogoutResponse;
import com.upgrad.FoodOrderingApp.api.model.SignupCustomerRequest;
import com.upgrad.FoodOrderingApp.api.model.SignupCustomerResponse;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class customerController {
    @Autowired
    CustomerService customerService;

    @RequestMapping(method= RequestMethod.POST, path="/customer/signup", consumes= MediaType.APPLICATION_JSON_UTF8_VALUE, produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupCustomerResponse> customerSignUp(final SignupCustomerRequest signupCustomerRequest) throws SignUpRestrictedException {
        final CustomerEntity customerEntity=new CustomerEntity();
        customerEntity.setUuid(UUID.randomUUID().toString());
        customerEntity.setFirstName(signupCustomerRequest.getFirstName());
        customerEntity.setLastName(signupCustomerRequest.getLastName());
        customerEntity.setContactNumber(signupCustomerRequest.getContactNumber());
        customerEntity.setEmail(signupCustomerRequest.getEmailAddress());
        customerEntity.setPassword(signupCustomerRequest.getPassword());
        final CustomerEntity createdCustomer=customerService.signup(customerEntity);
        SignupCustomerResponse response=new SignupCustomerResponse().id(createdCustomer.getUuid()).status("CUSTOMER SUCCESSFULLY REGISTERED");
        return new ResponseEntity<SignupCustomerResponse>(response, HttpStatus.CREATED);
    }

    @RequestMapping(method= RequestMethod.POST, path="/customer/login", produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LoginResponse> signin(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException {

        //The authorization header will be in the format "Basic base64encoded username:password"
        //First split the header and separate Basic to retrieve the base64encoded username:password
        //Decode the base64encoded string and split it based on : to retrieve username and password
        if(!authorization.contains("Basic")){
            throw new AuthenticationFailedException("ATH-003","Incorrect format of decoded customer name and password");
        }
        byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
        String decodedText = new String(decode);
        String[] decodedArray = decodedText.split(":");

        CustomerAuthTokenEntity userAuthToken = customerService.signin(decodedArray[0],decodedArray[1]);
        CustomerEntity user = userAuthToken.getUser();

        //Signin response will be the response body for a successful signin request
        LoginResponse loginResponse = new LoginResponse().id(user.getUuid()).message("LOGGED IN SUCCESSFULLY")
                                        .firstName(user.getFirstName())
                                        .lastName(user.getLastName())
                                        .contactNumber(user.getContactNumber())
                                        .emailAddress(user.getEmail());

        //JWT access token is added to the successful sigin response header
        HttpHeaders headers = new HttpHeaders();
        headers.add("access_token",userAuthToken.getAccessToken());
        return new ResponseEntity<LoginResponse>(loginResponse,headers,HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/user/logout", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LogoutResponse> logout(@RequestHeader("authorization") final String authorization) throws  AuthenticationFailedException {
        //Authorization header will be in the format "Bearer JWT-token"
        //Split the authorization header based on "Bearer " prefix to extract only the JWT token required for service class
        //If authorization header doesn't contain "Bearer " prefix then pass it as it is since it will be from test cases
        String authToken = authorization.startsWith("Bearer ")? authorization.split("Bearer ")[1]: authorization;
        CustomerEntity customerEntity = customerService.signout(authToken);

        LogoutResponse signoutResponse = new LogoutResponse().id(customerEntity.getUuid()).message("LOGGED OUT SUCCESSFULLY");
        return new ResponseEntity<LogoutResponse>(signoutResponse, HttpStatus.OK);
    }
}
