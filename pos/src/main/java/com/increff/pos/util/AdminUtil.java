package com.increff.pos.util;

import com.increff.pos.pojo.UserPojo;
import com.increff.pos.pojo.UserRole;
import com.increff.pos.spring.ApplicationProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class AdminUtil {

    public static Boolean checkAdmin(String email) {
        return ApplicationProperties.adminsList.contains(email);
    }

    public static Authentication  convert(UserPojo userPojo) {

        UserPrincipal principal = new UserPrincipal();
        principal.setEmail(userPojo.getEmail());
        principal.setId(userPojo.getId());

        ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(userPojo.getRole().name()));

        return new UsernamePasswordAuthenticationToken(principal, null, authorities);
    }

    public static String getRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(authentication) || Objects.isNull(authentication.getAuthorities())) return "";
        List<String> authorities = authentication.getAuthorities()
                .stream()
                .map(Object::toString)
                .collect(Collectors.toList());
        if (authorities.contains(UserRole.SUPERVISOR.name())) {
            return UserRole.SUPERVISOR.name();
        }
        return UserRole.OPERATOR.name();
    }
}
