package com.dyl.gw.support.jpa.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;

/**
 * Description: User
 * Author: DIYILIU
 * Update: 2018-07-18 15:55
 */

@Data
@Entity
@Table(name = "user_info")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    private String username;

    @JsonIgnore
    private String password;

    private String name;

    private String wechat;

    private String tel;

    private String email;

    private String address;
}
