package com.increff.pos.service;

import com.increff.pos.dao.UserDao;
import com.increff.pos.pojo.UserPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

@Service
public class UserService {
    @Autowired
    private UserDao userDao;

    @Transactional(rollbackOn = ApiException.class)
    public void insertUser(UserPojo userPojo) throws ApiException {
        UserPojo existingUserPojo = userDao.getUserByEmail(userPojo.getEmail());
        if(Objects.nonNull(existingUserPojo)){
            throw new ApiException("Email already exists");
        }
        userDao.insertUser(userPojo);
    }

    @Transactional(rollbackOn = ApiException.class)
    public UserPojo getUserByEmail(String email){
        return userDao.getUserByEmail(email);
    }

    @Transactional(rollbackOn = ApiException.class)
    public List<UserPojo> getAllUsers(){
        return userDao.getAllUsers();
    }

}
