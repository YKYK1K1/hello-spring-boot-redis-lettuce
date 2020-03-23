package com.yky.hello.spring.boot.redis.lettuce.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @version 1.0
 * @ClassName User
 * @Description TODO
 * @Author YKY
 * @Date 2020/3/22 11:03
 **/
@Data
public class User implements Serializable {
    private String id;
    private String name;
    private Integer age;
    private String phone;
}
