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
    public void add(UserPojo userPojo) throws ApiException {
        UserPojo existingUserPojo = userDao.select(userPojo.getEmail());
        if(Objects.nonNull(existingUserPojo)){
            throw new ApiException("Email already exists");
        }
        userDao.insert(userPojo);
    }
    @Transactional(rollbackOn = ApiException.class)
    public UserPojo select(String email){
        return userDao.select(email);
    }

    @Transactional(rollbackOn = ApiException.class)
    public List<UserPojo> selectAll(){
        return userDao.selectAll();
    }

}
