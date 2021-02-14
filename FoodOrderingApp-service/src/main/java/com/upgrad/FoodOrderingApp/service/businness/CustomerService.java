package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

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
        if(customerDao.getUserByPhoneNumber(customerEntity.getContactNumber()) != null){
            throw new SignUpRestrictedException("SGR-001","This contact number is already registered! Try other contact number.");
        }

        //Throw exception if given email address already exists
        if(customerEntity.getContactNumber()==null || customerEntity.getEmail()==null || customerEntity.getFirstName()==null
                || customerEntity.getPassword()==null){
            throw new SignUpRestrictedException("SGR-005","Except last name all fields should be filled");
        }

        if(!customerEntity.getContactNumber().matches("[0-9]{10}")){
            throw new SignUpRestrictedException("SGR-003","Invalid contact number!");
        }
        if(!customerEntity.getEmail().matches("[A-Za-z0-9+_.-]+@(.+)$")){
            throw new SignUpRestrictedException("SGR-002","Invalid email-id format!");
        }

        if(!customerEntity.getPassword().matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&-+=()])(?=\\\\S+$).{8, 20}$")){
            throw new SignUpRestrictedException("SGR-004","Weak password!");
        }

        //Get encrypted password from cryptography provider, the method returns an array of Strings
        //Salt will be at index 0 in the return array and encrypted password will be at index 1
        String[] encryptedText = cryptographyProvider.encrypt(customerEntity.getPassword());
         customerEntity.setSalt(encryptedText[0]);
         customerEntity.setPassword(encryptedText[1]);

        return customerDao.createUser(customerEntity);
    }
}
