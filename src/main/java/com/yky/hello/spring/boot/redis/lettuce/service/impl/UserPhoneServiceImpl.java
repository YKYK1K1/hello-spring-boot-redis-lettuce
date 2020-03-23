package com.yky.hello.spring.boot.redis.lettuce.service.impl;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import com.yky.hello.spring.boot.redis.lettuce.entity.User;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @version 1.0
 * @ClassName UserPhoneServiceImpl
 * @Description TODO
 * @Author YKY
 * @Date 2020/3/23 12:11
 **/
@Service
@Log
public class UserPhoneServiceImpl {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 用户在客户端输入手机号，点击发送后随机生成4位数字码。有效期为20秒。
     *
     * @param phone
     * @return
     */
    public String getPhoneCode(String phone) {
        String str1 = "user:" + phone;
        String str2 = "num:" + phone;
        String code = Math.round(new Random().nextInt(9999)) + "";
        System.out.println(code);

        if (!redisTemplate.hasKey(str2)) {
            redisTemplate.opsForHash().put(str2, "num", 0);
            redisTemplate.expire(str2, 5, TimeUnit.MINUTES);
        } else {
            int num = (int) redisTemplate.opsForHash().get(str2, "num");
            redisTemplate.opsForHash().put(str2, "num", num + 1);
        }
        //如果redis中不存在验证码
        if (!redisTemplate.hasKey(str1)) {
            redisTemplate.opsForHash().put(str1, phone, code);
            redisTemplate.expire(str1, 40, TimeUnit.SECONDS);

            log.info("将验证码存储到redis中");
        }

        return code;
    }

    /**
     * 输入验证码，点击验证，返回成功或者失败。且每个手机号在5分钟内只能验证3次。并给相应信息提示
     *
     * @param code
     * @param phone
     * @return
     */
    public String VerificationCode(String phone, String code) {
        String str1 = "user:" + phone;
        String str2 = "num:" + phone;
        if (!redisTemplate.hasKey(str2)) {
            return "请重新发送验证码";
        }
        int num = (int) redisTemplate.opsForHash().get(str2, "num");
        if (num < 3) {
            //如果用户存在验证码
            if (redisTemplate.hasKey(str1)) {
                String redisCode = (String) redisTemplate.opsForHash().get(str1, phone);
                if (code.equals(redisCode)) {
                    return "用户登陆成功";
                } else if (num == 2) {
                    return "验证失败，请重新验证,您还有最后一次验证机会";
                }
            } else {
                return "验证码已过期 请重新发送验证码";
            }
        } else {
            Long expire = redisTemplate.getExpire(str2, TimeUnit.MINUTES);
            return String.format("验证次数过多超过三次 请等待5分钟 剩余时间 %s", expire);
        }

        return null;
    }

}
