package com.example.bootredis01.controller;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * 最终版
 */
@RestController
public class GoodController {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    private static final String REDIS_LOCK_KEY = "redisLock";
    @Value("${server.port}")
    private String serverPort;
    @Resource
    private Redisson redisson;

    @GetMapping("/buyGoods")
    public String buyGoods(){
/**
 * 我们无法保证一个业务的执行时间，有可能是 10s，有可能是 20s，也有可能更长。因为执行业务的时候可能会调用其他服务，我们并不能保证其他服务的调用时间。如果设置的锁过期了，
 * 当前业务还正在执行，那么之前设置的锁就失效了，就有可能出现超卖问题。
 * 因此我们需要确保 redisLock 过期时间大于业务执行时间的问题，即面临如何对 Redis 分布式锁进行续期的问题
 *
 * 使用 Redisson 实现自动续期功能
 */
        //1、单机锁--无法解决分布式的超卖现象
        String value = UUID.randomUUID().toString()+Thread.currentThread().getName();
        //获取锁
        RLock redissonLock = redisson.getLock(REDIS_LOCK_KEY);
        //上锁
        redissonLock.lock();
        try {
            String gNum = stringRedisTemplate.opsForValue().get("goods:001");
            int goodsNum = gNum == null ? 0 : Integer.parseInt(gNum);
            String retStr = null;
            if(goodsNum > 0){
                int relNum = goodsNum - 1;
                stringRedisTemplate.opsForValue().set("goods:001",relNum+"");
                retStr = "你已经成功秒杀商品，此时还剩余：" + relNum + "件" + "\t 服务器端口: " + serverPort;
            } else {
                retStr = "商品已经售罄/活动结束/调用超时，欢迎下次光临" + "\t 服务器端口: " + serverPort;
            }
            System.out.println(retStr);
            return retStr;
        }finally {
            //一般情况到这里可以解决分布式锁，但有超高并发的情况下，可能会抛出IllegalMonitorStateException异常，
            //原因是解锁 lock 的线程并不是当前线程
            //在释放锁之前加一个判断：还在持有锁的状态，并且是当前线程持有的锁再解锁
            //redissonLock.unlock();// 解锁
            if(redissonLock.isLocked() && redissonLock.isHeldByCurrentThread()){
                redissonLock.unlock();
            }

        }
    }



}
