package com.increff.pos.dto;

import com.increff.pos.dto.helper.HelperDto;
import com.increff.pos.model.data.InfoData;
import com.increff.pos.model.form.LoginForm;
import com.increff.pos.model.form.UserForm;
import com.increff.pos.pojo.UserPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.UserService;
import com.increff.pos.util.AdminUtil;
import com.increff.pos.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.Objects;

@Service
public class UserDto {
    @Autowired
    private UserService userService;
    @Autowired
    private InfoData infoData;

    @Transactional(rollbackOn = ApiException.class)
    public void login(HttpServletRequest request, LoginForm loginForm) throws ApiException {
        UserPojo userPojo = userService.getUserByEmail(loginForm.getEmail());
        boolean authenticated = (Objects.nonNull(userPojo) && Objects.equals(userPojo.getPassword(), loginForm.getPassword()));
        if (!authenticated) {
            infoData.setMessage("Invalid username or password");
            throw new ApiException("Invalid username or password");
        }
        Authentication authentication = AdminUtil.convert(userPojo);
        HttpSession session = request.getSession(true);
        SecurityUtil.createContext(session);
        SecurityUtil.setAuthentication(authentication);
    }

    @Transactional(rollbackOn = ApiException.class)
    public ModelAndView logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return new ModelAndView("redirect:/");
    }

    @Transactional(rollbackOn = ApiException.class)
    public void signup(UserForm userForm) throws ApiException {
        HelperDto.normalise(userForm);
        UserPojo userPojo = HelperDto.convert(userForm);
        userService.insertUser(userPojo);
        infoData.setMessage("User added successfully!");
    }
}
