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
    boolean result = proxyEtcReverseProxyClient.add("www.errzffero.com", "localhssosssts:80ssss89");
    Assert.assertTrue(result);
  }
  
  @Test
  public void testUpdate() {
    boolean result = proxyEtcReverseProxyClient.update("www.errzffero.com", "localhssosssts:80ssss89", "xxx:yyy");
    Assert.assertTrue(result);
  }
  
  @Test
  public void testDelete() {
    boolean result = proxyEtcReverseProxyClient.delete("www.errzffero.com");
    Assert.assertTrue(result);
  }
  
  @Test
  public void testDeleteBackend() {
    boolean result = proxyEtcReverseProxyClient.deleteBackend("www.errzffero.com", "localhssosssts:80ssss89");
    Assert.assertTrue(result);
  }

  @After
  public void delete(){
    proxyEtcReverseProxyClient.delete("www.errzero.com");
  }
}
