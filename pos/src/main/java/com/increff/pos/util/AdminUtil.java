package com.increff.pos.util;

import com.increff.pos.pojo.UserPojo;
import com.increff.pos.pojo.UserRole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.persistence.Version;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    public static String getRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ;
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
