package com.cloudezz.houston.rproxy;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.cloudezz.houston.config.rproxy.ProxyEtcReverseProxyClient;

public class ProxyEtcReverseProxyClientTest {
  ProxyEtcReverseProxyClient proxyEtcReverseProxyClient = new ProxyEtcReverseProxyClient();

  @Before
  public void init(){
    proxyEtcReverseProxyClient.init();
  }
  
  @Test
  public void testServerReachable(){
    boolean result = proxyEtcReverseProxyClient.serverReachable();
    Assert.assertTrue(result);
  }

  @Test
  public void testAdd() {
    boolean result = proxyEtcReverseProxyClient.add("www.errzero.com", "localhost:8080");
    Assert.assertTrue(result);
  }
  
  @Test
  public void testUpdate() {
    boolean result = proxyEtcReverseProxyClient.update("www.errzero.com", "localhost:8080", "xxx:yyy");
    Assert.assertTrue(result);
  }
  
  @Test
  public void testDeleteBackend() {
    boolean result = proxyEtcReverseProxyClient.deleteBackend("www.errzero.com", "xxx:yyy");
    Assert.assertTrue(result);
  }

  @Test
  public void testDelete() {
    boolean result = proxyEtcReverseProxyClient.delete("www.errzero.com");
    Assert.assertTrue(result);
  }

  @After
  public void delete(){
    proxyEtcReverseProxyClient.delete("www.errzero.com");
  }
}
