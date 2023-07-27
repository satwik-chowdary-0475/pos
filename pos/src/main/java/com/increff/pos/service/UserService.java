package com.increff.pos.service;

import com.increff.pos.dao.UserDao;
import com.increff.pos.pojo.UserPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(rollbackOn = ApiException.class)
public class UserService {
    @Autowired
    private UserDao userDao;

    public void insertUser(UserPojo userPojo) throws ApiException {
        UserPojo existingUserPojo = userDao.getUserByEmail(userPojo.getEmail());
        if (Objects.nonNull(existingUserPojo)) {
            throw new ApiException("Email already exists");
        }

        userDao.insertUser(userPojo);
    }

    public UserPojo getUserByEmail(String email) {
        return userDao.getUserByEmail(email);
    }

    public List<UserPojo> getAllUsers() {
        return userDao.getAllUsers();
    }

}
