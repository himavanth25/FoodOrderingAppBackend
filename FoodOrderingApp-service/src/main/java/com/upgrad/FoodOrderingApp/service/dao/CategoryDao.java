package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class CategoryDao {

    @PersistenceContext
    private EntityManager entityManager;

    public CategoryEntity getCategoryByUuid(String uuid) {
        TypedQuery<CategoryEntity> categoryEntityTypedQuery = entityManager.createNamedQuery("categoryByUuid", CategoryEntity.class).
                setParameter("uuid", uuid);
        List<CategoryEntity> resultList = categoryEntityTypedQuery.getResultList();
        if (resultList.size() > 0) {
            return resultList.get(0);
        }
        return new CategoryEntity();
    }
}
