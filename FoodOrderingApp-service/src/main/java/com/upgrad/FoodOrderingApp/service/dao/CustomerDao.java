package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class CustomerDao {

    @PersistenceContext
    private EntityManager entityManager;

    public CustomerEntity createUser(CustomerEntity customerEntity){
        entityManager.persist(customerEntity);
        return customerEntity;
    }

    public CustomerEntity getUserByPhoneNumber(final String phoneNumer){
        try {
            return entityManager.createNamedQuery("userByPhone", CustomerEntity.class).setParameter("phone",phoneNumer)
                    .getSingleResult();
        }
        catch (NoResultException nre){
            return null;
        }
    }

    public CustomerEntity getUserByUserName(final String userName){
        try {
            return entityManager.createNamedQuery("userByPhone", CustomerEntity.class).setParameter("phone", userName)
                    .getSingleResult();
        }
        catch (NoResultException nre){
            return null;
        }
    }

    public CustomerAuthTokenEntity createAuthToken(final CustomerAuthTokenEntity userAuthTokenEntity){
        entityManager.persist(userAuthTokenEntity);
        return userAuthTokenEntity;
    }
}
