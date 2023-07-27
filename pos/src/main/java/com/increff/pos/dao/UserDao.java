package com.increff.pos.dao;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.increff.pos.pojo.UserPojo;

@Repository
@Transactional
public class UserDao extends AbstractDao {

    private static final String SELECT_BY_EMAIL = "select p from UserPojo p where email=:email";
    private static final String SELECT_ALL = "select p from UserPojo p";


    public void insertUser(UserPojo userPojo) {
        em().persist(userPojo);
    }

    public UserPojo getUserByEmail(String email) {
        TypedQuery<UserPojo> query = getQuery(SELECT_BY_EMAIL, UserPojo.class);
        query.setParameter("email", email);
        return getSingle(query);
    }

    public List<UserPojo> getAllUsers() {
        TypedQuery<UserPojo> query = getQuery(SELECT_ALL, UserPojo.class);
        return query.getResultList();
    }

}