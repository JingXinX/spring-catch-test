package com.example.bootredis01.controller;

import com.example.bootredis01.config.RedisUtils;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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


    @GetMapping("/buyGoods001")
    public String buyGoods001() throws Exception {
//        synchronized (this) {//1、单机锁--无法解决分布式的超卖现象
        String value = UUID.randomUUID().toString()+Thread.currentThread().getName();
        try {
           /* setIfAbsent() 就相当于 setnx，如果不存在就新建锁
            Boolean lockFlag = stringRedisTemplate.opsForValue().setIfAbsent(REDIS_LOCK_KEY, value);//但如果微服务挂掉宕机则无法保证finally的执行，Redis中的这个key没有被删除，其他微服务就一直抢不到锁
            // 设置过期时间为 10s  但是这个时间限定与加锁不是原子性的，保证不了加锁后宕机的解锁
            stringRedisTemplate.expire(REDIS_LOCK_KEY,10L, TimeUnit.SECONDS);//加入一个过期时间限定 key*/

            //Redis 实现了加锁的同时加上锁的过期时间以解锁保证原子性  将上面两行合并为
            Boolean lockFlag = stringRedisTemplate.opsForValue().setIfAbsent(REDIS_LOCK_KEY,value,10L,TimeUnit.SECONDS);

            if(null == lockFlag){
                return "抢锁失败 o(╥﹏╥)o";
            }
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
                //stringRedisTemplate.delete(REDIS_LOCK_KEY);//释放分布式锁,若业务逻辑处出现问题则不保证锁被释放
                return retStr;
    //        }//1
        }finally {//加finally 保证锁的释放

           /* //这里又出现了非原子性即执行if判断时锁还属于当前业务，刚执行完if，锁却被其他业务释放，出现误删锁现象
            if(value.equalsIgnoreCase(stringRedisTemplate.opsForValue().get(REDIS_LOCK_KEY))){//加判断，保证删的是自己的锁
                stringRedisTemplate.delete(REDIS_LOCK_KEY);//加锁有原子性有时间限制，但此处不保证删的是自己的锁（如 线程A执行时间大于锁过期时间，redis到时间即删除了A的锁）
            }*/

            /*//将上面两行优化为
            while (true){//使用事务解决删锁的非原子性,类似于执行命令
                //加事务，乐观锁
                stringRedisTemplate.watch(REDIS_LOCK_KEY);//当某个事务需要按条件执行时，就要使用这个命令将给定的键设置为受监控的状态.该命令可以实现redis的乐观锁
                // 判断是否是自己加的锁
                if(value.equalsIgnoreCase(stringRedisTemplate.opsForValue().get(REDIS_LOCK_KEY))){
                    stringRedisTemplate.setEnableTransactionSupport(true);
                    stringRedisTemplate.multi();//用于标记事务块的开始.Redis会将后续的命令逐个放入队列中，然后使用EXEC命令原子化地执行这个命令序列
                    stringRedisTemplate.delete(REDIS_LOCK_KEY);
                    List<Object> list = stringRedisTemplate.exec();//在一个事务中执行所有先前放入队列的命令，然后恢复正常的连接状态
                    // 判断事务是否执行成功，如果等于 null，就是没有删掉，删除失败，再回去 while  continue循环那再重新执行删除
                    if(null == list){
                        continue;
                    }
                }
                //如果删除成功，释放监控器，并且 break 跳出当前循环
                stringRedisTemplate.unwatch();//清除所有先前为一个事务监控的键
                break;
            }*/
            //上面事务代码使用lua脚本优化（推荐）可以防止别人动我们自己的锁。还有就是假如redis集群主机宕掉之后还没来得及异步复制到从机从机上位，而导致账单失效等等，
            // 但是自己写的也不是很强大。所以引进redisson
            // 获取连接对象
            Jedis jedis = RedisUtils.getGedis();
            // lua 脚本，摘自官网
            String script = "if redis.call('get', KEYS[1]) == ARGV[1]" + "then "
                    + "return redis.call('del', KEYS[1])" + "else " + "  return 0 " + "end";
            try {
                Object eval = jedis.eval(script, Collections.singletonList(REDIS_LOCK_KEY), Collections.singletonList(value));
                if("1".equals(eval)){
                    System.out.println("sussess");
                }else {
                    System.out.println("error");
                }

            }finally {
                // 关闭链接
                if(null != jedis){
                    jedis.close();
                }
            }
        }
    }



}
