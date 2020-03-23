package com.yky.hello.spring.boot.redis.lettuce;

import com.yky.hello.spring.boot.redis.lettuce.entity.User;
import com.yky.hello.spring.boot.redis.lettuce.service.impl.UserPhoneServiceImpl;
import com.yky.hello.spring.boot.redis.lettuce.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class HelloSpringRedisLettuceApplicationTests {
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private UserPhoneServiceImpl userPhoneService;

    @Test
    void contextLoads() {
        userService.getString("AAAA");
    }

    @Test
    void t1() {
        String redisStr = userService.getString("redisStr");
        System.out.println(redisStr);
    }

    @Test
    void t2() {
        userService.expireStr("test", "测试数据");
        System.out.println("操作成功");
    }

    @Test
    void t3() {
        User user = userService.selectById("12");
        System.out.println(user);
    }

    @Test
    void phonecode() {
        userPhoneService.getPhoneCode("17640619596");
    }

    @Test
    void verificationCode() {
        String s = userPhoneService.VerificationCode("17640619596", "2254");
        System.out.println(s);
    }
}
