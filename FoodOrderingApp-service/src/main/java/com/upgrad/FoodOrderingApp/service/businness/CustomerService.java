package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {
    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    @Autowired
    private CustomerDao customerDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity signup(CustomerEntity customerEntity) throws SignUpRestrictedException
     {

        //Throw exception if given user name already exists
//        if(userDao.getUserByUserName(userEntity.getUsername()) != null){
//            throw new SignUpRestrictedException("SGR-001","Try any other Username, this Username has already been taken");
//        }
//
//        //Throw exception if given email address already exists
//        if(userDao.getUserByEmailAddress(userEntity.getEmail()) != null){
//            throw new SignUpRestrictedException("SGR-002","This user has already been registered, try with any other emailId");
//        }

        //Get encrypted password from cryptography provider, the method returns an array of Strings
        //Salt will be at index 0 in the return array and encrypted password will be at index 1
        String[] encryptedText = cryptographyProvider.encrypt(customerEntity.getPassword());
         customerEntity.setSalt(encryptedText[0]);
         customerEntity.setPassword(encryptedText[1]);

        return customerDao.createUser(customerEntity);
    }
}
