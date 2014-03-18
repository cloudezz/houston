package com.cloudezz.houston.config.rproxy;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class HipacheReverseProxyClient implements ReverseProxyClient {

  private Jedis jedis;

  private JedisPool pool;

  private String redisHost;

  private int redisPort;

  private String redisPassword;

  public HipacheReverseProxyClient() {

  }

  @Override
  public void init() {
    JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
    jedisPoolConfig.setMaxActive(10);
    pool =
        new JedisPool(jedisPoolConfig, getRedisHost(), getRedisPort(), Protocol.DEFAULT_TIMEOUT,
            getRedisPassword());
  }

  public String getRedisHost() {
    return redisHost;
  }

  public void setRedisHost(String redisHost) {
    this.redisHost = redisHost;
  }

  public int getRedisPort() {
    return redisPort;
  }

  public void setRedisPort(int redisPort) {
    this.redisPort = redisPort;
  }

  public String getRedisPassword() {
    return redisPassword;
  }

  public void setRedisPassword(String redisPassword) {
    this.redisPassword = redisPassword;
  }


  @Override
  public boolean add(String frontendURL, String backendURL) {
    Long id = null;
    try {
      jedis = pool.getResource();
      id = jedis.rpush(frontendURL, backendURL);
    } catch (JedisConnectionException e) {
      // returnBrokenResource when the state of the object is unrecoverable
      if (null != jedis) {
        pool.returnBrokenResource(jedis);
        jedis = null;
      }
    } finally {
      // it's important to return the Jedis instance to the pool once you've finished using it
      if (null != jedis)
        pool.returnResource(jedis);
    }
    return (id != null);
  }

  @Override
  public boolean serverReachable() {
    try {
      jedis = pool.getResource();
      return jedis.isConnected();
    } catch (JedisConnectionException e) {
      // returnBrokenResource when the state of the object is unrecoverable
      if (null != jedis) {
        pool.returnBrokenResource(jedis);
        jedis = null;
      }
    } finally {
      // it's important to return the Jedis instance to the pool once you've finished using it
      if (null != jedis)
        pool.returnResource(jedis);
    }

    return false;
  }

  @Override
  public boolean update(String frontendURL, String oldBackendURL, String newBackendURL) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean delete(String frontendURL) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean delete(String frontendURL, String backendURL) {
    // TODO Auto-generated method stub
    return false;
  }

}
