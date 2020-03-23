package com.yky.hello.spring.boot.redis.lettuce.service.impl;

import com.yky.hello.spring.boot.redis.lettuce.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @version 1.0
 * @ClassName UserServiceImpl
 * @Description TODO
 * @Author YKY
 * @Date 2020/3/22 15:11
 **/
@Service
@Slf4j
public class UserServiceImpl {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Resource(name = "redisTemplate")
    private HashOperations<String, String, User> hashOps;

    /**
     * TODO String 类型的演示
     * Redis 有什么命令 <---> Jedis 就有什么方法
     * --> Lettuce --> RedisTemplate 模板 （Jedis/..)进一步封装。
     * RedisTemplate 方法和命令 肯定不一样
     * <p>
     * Redis String 类型
     * 需求： 用户输入一个 key
     * 先判断 Redis 中是否存在该数据
     * 如果存在，在 Redis 中进行查询
     * 如果不存在，在MySQL数据库查询，将结果贡献给Redis，并返回
     *
     * @param key
     * @return
     */
    public String getString(String key) {
        //如果存在
        if (redisTemplate.hasKey(key)) {
            log.info("---->在Redis 查询出来的");
            return (String) redisTemplate.opsForValue().get(key);
        } else {
            String val = "RedisTemplate模板学习lettuce客户端";
            log.info("---->MySQL 中查询出来：" + val);
            redisTemplate.opsForValue().set(key, val);
            log.info("---->在MySQL 查询出的结果存入Redis中");
            return val;
        }
    }

    /**
     * 测试 String 类型
     * 需求： 用户输入一个 redis 数据 该 Key 的有效期为28小时
     *
     * @param key
     * @param value
     */
    public void expireStr(String key, String value) {
        //存值
        redisTemplate.opsForValue().set(key, value);
        redisTemplate.expire(key, 28, TimeUnit.HOURS);
    }

    /**
     * 测试 Hash 类型 演示
     *
     * @param id
     * @return 根据 ID 查询用户对象信息
     * 先判断 Redis 中是否存在该Key
     * 如果不存在，查询数据库 MySQL 并将结果添加到 Redis 中，并返回
     * 如果存在，直接将结果在 Redis中查询，并返回。
     */
    public User selectById(String id) {

        // redisTemplate.hasKey(key) 判断整个 Key 是否存在
        if (redisTemplate.opsForHash().hasKey("user", id)) {
            log.info("---->在Redis 查询出来的");
            return (User) redisTemplate.opsForHash().get("user", id);
        } else {
            User user = new User();
            user.setId(id);
            user.setName("haha");
            user.setAge(11);
            log.info("---->MySQL 中查询出来：" + user);
            /**
             * @param h 用户实体 user
             * @param hk 用户主键 id
             * @param hv 整个对象
             */
            hashOps.put("user", id, user);
            return user;
        }
    }
    /**
     * 问题1： 出现了很多相同的字符串。 -->提取出来
     *      答1： 声明一个工具类
     *      答2： 在实体Bean 声明一个方法
     * 问题2： 强制类型转换
     * 问题3： redisTemplate.opsForHash() 写很长一串？
     *      答：
     */

}
