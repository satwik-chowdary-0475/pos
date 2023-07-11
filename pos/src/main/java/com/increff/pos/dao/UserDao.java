package com.increff.pos.dao;

import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.increff.pos.pojo.UserPojo;

@Repository
public class UserDao extends AbstractDao {

    private static String DELETE_BY_ID = "delete from UserPojo p where id=:id";
    private static String SELECT_BY_ID = "select p from UserPojo p where id=:id";
    private static String SELECT_BY_EMAIL = "select p from UserPojo p where email=:email";
    private static String SELECT_ALL = "select p from UserPojo p";


    @Transactional
    public void insert(UserPojo userPojo) {
        em().persist(userPojo);
    }

    @Transactional
    public int delete(int id) {
        Query query = em().createQuery(DELETE_BY_ID);
        query.setParameter("id", id);
        return query.executeUpdate();
    }

    @Transactional
    public UserPojo select(int id) {
        TypedQuery<UserPojo> query = getQuery(SELECT_BY_ID, UserPojo.class);
        query.setParameter("id", id);
        return getSingle(query);
    }

    @Transactional
    public UserPojo select(String email) {
        TypedQuery<UserPojo> query = getQuery(SELECT_BY_EMAIL, UserPojo.class);
        query.setParameter("email", email);
        return getSingle(query);
    }

    @Transactional
    public List<UserPojo> selectAll() {
        TypedQuery<UserPojo> query = getQuery(SELECT_ALL, UserPojo.class);
        return query.getResultList();
    }

}