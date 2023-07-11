package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

// TODO: enum for role
@Entity
@Setter
@Getter
@Table(
        name = "users"
)
public class UserPojo extends AbstractPojo{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private UserRole role;
}