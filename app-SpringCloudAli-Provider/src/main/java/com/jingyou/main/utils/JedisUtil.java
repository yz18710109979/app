package com.jingyou.main.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.*;

@Slf4j
@Component
public class JedisUtil {
    private static JedisPool jedisPool;
    private static String prefix = "";
    private static String redisHost;
    private static Integer redisPort;
    private static String redisPwd;
    private static final Long RELEASE_SUCCESS = 1L;
    @Value("${jedisClusterPrefix}")
    public void setJedisClusterPrefix(String jedisClusterPrefix) {
        if (StringUtils.isNotEmpty(jedisClusterPrefix)) {
            prefix = jedisClusterPrefix + "_";
        }
    }
    @Value("${jedisClusterHostList}")
    private void initRedisHostInfo(String hosts) {
        String[] split = hosts.split(":");
        redisHost= split[0];
        redisPort= Integer.parseInt(split[1]);
        redisPwd = split[2];
        redisPoolFactory();
    }
    /**
     * Jedis初始化
     */
    public static void redisPoolFactory() {
        //哨兵主服务器名
//        String redisHost="sever10.122.46.90";
        //连接超时
        //读取数据超时
        Integer timeout=10000;
        //最大连接数
        Integer maxTotal = 50;
        //连接阻塞时间-1为无限等待
        Long maxWait = -1L;
        //最大空闲连接
        Integer maxIdle = 10;
        //最小空闲连接
        Integer minIdle = 5;
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMaxWaitMillis(maxWait);
        jedisPoolConfig.setMaxTotal(maxTotal);
        jedisPoolConfig.setMinIdle(minIdle);
//        jedisPool = new JedisPool(jedisPoolConfig, redisHost, redisPort, timeout, "Redis@166", 0);
        jedisPool = new JedisPool(jedisPoolConfig, redisHost, redisPort, timeout, redisPwd,0);
    }
    public static Map<String, String> getHash(String key, int...second) {
        Jedis jedis = JedisUtil.jedisPool.getResource();
        key = prefix + key;
        Map<String, String> res=new HashMap<>(16);
        try {
            res= jedis.hgetAll(key);
            expireInside(jedis,key,second);
            return res;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            jedis.close();
        }
    }
    /**
     * 写入map
     */
    public static void setHash(String key,Map<String,String>map, int...second) {
        Jedis jedis = jedisPool.getResource();
        key = prefix + key;
        try {
            jedis.hmset(key, map);
            expireInside(jedis,key,second);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            jedis.close();
        }
    }
    /**
     * 写入字符串
     */
    public static void setString(String key,String data, int...second) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            key = prefix + key;
            jedis.set(key,data);
            expireInside(jedis,key,second);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            jedis.close();
        }
    }
    /**
     * 获取字符串
     */
    public static String getString(String key, int...second) {
        Jedis jedis = jedisPool.getResource();
        String res="";
        key = prefix + key;
        try{
            res= jedis.get(key);
            expireInside(jedis,key,second);
            return res;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }finally{
            jedis.close();
        }
    }
    /**
     * 删除Key
     */
    public static Boolean delKey(String key){
        Jedis jedis = jedisPool.getResource();
        key = prefix + key;
        try {
            if(Objects.equals(jedis.del(key),RELEASE_SUCCESS)){
                return true;
            }
            return false;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            jedis.close();
        }
    }
    /**
     * 判断key是否存在
     */
    public static Boolean exists(String key){
        Jedis jedis = jedisPool.getResource();
        key = prefix + key;
        try {
            return jedis.exists(key);
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
        finally {
            jedis.close();
        }
    }
    /**
     * 设置key的时间
     */
    public static Long expire(String key,Integer second){
        Jedis jedis = jedisPool.getResource();
        key = prefix + key;
        try {
            return jedis.expire(key,second);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            jedis.close();
        }
    }
    /**
     * 获取map里某个字段的值
     */
    public static String hget(String key, String field,int...second){
        Jedis jedis = jedisPool.getResource();
        key = prefix + key;
        try {
            String res=jedis.hget(key,field);
            if(StringUtils.isNotEmpty(res)){
                expireInside(jedis,key,second);
                return res;
            }
            return null;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
        finally {
            jedis.close();
        }
    }
    /**
     * 删除map里的某个字段
     */
    public static Boolean hdel(String key,String... fields){
        Jedis jedis = jedisPool.getResource();
        key = prefix + key;
        try {
            if(Objects.equals(jedis.hdel(key,fields),RELEASE_SUCCESS)){
                return true;
            }
            return false;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        } finally {
            jedis.close();
        }
    }
    /**
     * 添加或修改map里的某个字段的值
     */
    public static Boolean hset(String key,String field,String value,int...second){
        Jedis jedis = jedisPool.getResource();
        key = prefix + key;
        try {
            if(Objects.equals(jedis.hset(key,field,value),RELEASE_SUCCESS)){
                expireInside(jedis,key,second);
                return true;
            }
            return false;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        } finally {
            jedis.close();
        }
    }
    /**
     * <p>通过key 删除指定的 field </p>*
     * @param key
     * @return
     */
    public static Set<String> hkeys(String key) {
        key = prefix + key;
        Jedis jedis = jedisPool.getResource();
        Set<String> res =new HashSet<>();
        try {
            res=jedis.hkeys(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }
    /**
     * 添加或修改map
     */
    public static Boolean hmset(String key,Map<String,String> map,int...second){
        Jedis jedis = jedisPool.getResource();
        key = prefix + key;
        try {
            if(StringUtils.isEmpty(jedis.hmset(key,map))){
                return false;
            }
            expireInside(jedis,key,second);
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
        finally {
            jedis.close();
        }
    }
    /**
     * Redis分布式锁判定
     */
    public static Object eval(String key,List<String> keys,List<String> args){
        Jedis jedis = jedisPool.getResource();
        key = prefix + key;
        try {
            return jedis.eval(key,keys,args);
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
        finally {
            jedis.close();
        }
    }

    /**
     * 延长key的时间(内部用)
     */
    private static void expireInside(Jedis jedis,String key,int... second){
        if (StringUtils.isNotEmpty(key)) {
            int exp = 1800 * 60 * 24;
            jedis.expire(key, second.length > 0 ? second[0] : exp);
        }
    }

    /**
     * 获取自增值
     * @param key
     * @return
     */
    public static Long incr(String key) {
        Jedis jedis = jedisPool.getResource();
        try{
            key = prefix + key;
            return jedis.incr(key);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            jedis.close();
        }
        return null;
    }

    /**
     *
     * @param key
     * @return
     */
    public static Set<String> keys(String key) {
        try{
            Set<String> keys = jedisPool.getResource().keys(key);
            return keys;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param key
     * @return
     */
    public static Long ttl(String key) {
        return jedisPool.getResource().ttl(prefix + key);
    }
}
