package com.increff.pos.dao;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.increff.pos.pojo.UserPojo;

@Repository
public class UserDao extends AbstractDao {

    private static String SELECT_BY_ID = "select p from UserPojo p where id=:id";
    private static String SELECT_BY_EMAIL = "select p from UserPojo p where email=:email";
    private static String SELECT_ALL = "select p from UserPojo p";


    @Transactional
    public void insertUser(UserPojo userPojo) {
        em().persist(userPojo);
    }

    @Transactional
    public UserPojo getUserById(int id) {
        TypedQuery<UserPojo> query = getQuery(SELECT_BY_ID, UserPojo.class);
        query.setParameter("id", id);
        return getSingle(query);
    }

    @Transactional
    public UserPojo getUserByEmail(String email) {
        TypedQuery<UserPojo> query = getQuery(SELECT_BY_EMAIL, UserPojo.class);
        query.setParameter("email", email);
        return getSingle(query);
    }

    @Transactional
    public List<UserPojo> getAllUsers() {
        TypedQuery<UserPojo> query = getQuery(SELECT_ALL, UserPojo.class);
        return query.getResultList();
    }

}