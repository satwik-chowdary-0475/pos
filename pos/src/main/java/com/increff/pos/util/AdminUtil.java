package com.increff.pos.util;

import com.increff.pos.pojo.UserPojo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.persistence.Version;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class AdminUtil {

    private static List<String> adminsList;

    @Value("${admins}")
    private void setAdmins(String admins) {
        adminsList = Arrays.asList(admins.split(","));
    }

    public static Boolean checkAdmin(String email) {
        return adminsList.contains(email);
    }

    public static Authentication convert(UserPojo userPojo) {
        // Create principal
        UserPrincipal principal = new UserPrincipal();
        principal.setEmail(userPojo.getEmail());
        principal.setId(userPojo.getId());

        // Create Authorities
        ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority(userPojo.getRole().name()));
        // you can add more roles if required

        // Create Authentication
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(principal, null,
                authorities);
        return token;
    }
}
