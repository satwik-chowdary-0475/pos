package com.increff.pos.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.persistence.Version;
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
}
