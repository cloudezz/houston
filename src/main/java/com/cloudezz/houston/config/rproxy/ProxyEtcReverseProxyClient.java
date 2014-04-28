package com.cloudezz.houston.config.rproxy;

import java.nio.charset.Charset;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.web.client.RestTemplate;

public class ProxyEtcReverseProxyClient implements ReverseProxyClient {

  private static final Logger log = Logger.getLogger(ProxyEtcReverseProxyClient.class);

  private String host;

  private String port ;

  private String username;

  private String password;
  
  private boolean https;

  private RestTemplate restTemplate;

  private HttpHeaders httpHeaders;

 
  public ProxyEtcReverseProxyClient(){
    
  }
  
  public ProxyEtcReverseProxyClient(String host,String port,boolean https,String username, String password){
    this.host=host;
    this.port=port;
    this.https=https;
    this.username=username;
    this.password=password;
  }
  
  
  @Override
  public void init() {
    restTemplate = new RestTemplate();
    httpHeaders = new HttpHeaders();
    String auth = username + ":" + password;
    byte[] encodedAuth = Base64.encode(auth.getBytes(Charset.forName("US-ASCII")));
    String authHeader = "Basic " + new String(encodedAuth);
    httpHeaders.set("Authorization", authHeader);
    httpHeaders.add("Content-Type", "application/json");

  }

  @Override
  public boolean add(String frontendURL, String backendURL) {
    if (frontendURL != null && backendURL != null && restTemplate != null && httpHeaders != null) {
      try {
        String body = "{\"source\" : \"" + frontendURL + "\", \"target\" : \"" + backendURL + "\"}";
        HttpEntity<String> httpEntity = new HttpEntity<String>(body, httpHeaders);
        String url = getProtocol()+ host + ":" + port + "/api/routes/add";
        ResponseEntity<String> response =
            restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        String responseBody = response.getBody();
        log.debug("Proxy-etc response for adding route : " + responseBody);
        if (responseBody != null && responseBody.contains("{") && responseBody.contains("}")) {
          JSONObject jObject = new JSONObject(responseBody);
          if (!jObject.isNull("id")) {
            return true;
          }
        }
      } catch (Exception ex) {
        log.error("Error while adding routes to proxy-etc", ex);
      }
    } else {
      log.error("ProxyEtcReverseProxyClient is not initialized or parameters passed are wrong");
    }
    return false;
  }

  @Override
  public boolean update(String frontendURL, String oldBackendURL, String newBackendURL) {
    if (frontendURL != null && oldBackendURL != null && newBackendURL != null
        && restTemplate != null && httpHeaders != null) {
      try {
        String body =
            "{\"source\" : \"" + frontendURL + "\", \"oldTarget\" : \"" + oldBackendURL
                + "\", \"newTarget\":\"" + newBackendURL + "\"}";
        HttpEntity<String> httpEntity = new HttpEntity<String>(body, httpHeaders);
        String url = getProtocol() + host + ":" + port + "/api/routes/updateTarget";
        ResponseEntity<String> response =
            restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        String responseBody = response.getBody();
        log.debug("Proxy-etc response for updating route : " + responseBody);
        if (responseBody != null && responseBody.contains("{") && responseBody.contains("}")) {
          JSONObject jObject = new JSONObject(responseBody);
          if (!jObject.isNull("id")) {
            return true;
          }
        }
      } catch (Exception ex) {
        log.error("Error while updating route to proxy-etc", ex);
      }
    } else {
      log.error("ProxyEtcReverseProxyClient is not initialized or parameters passed are wrong");
    }
    return false;
  }

  @Override
  public boolean delete(String frontendURL) {
    if (frontendURL != null && restTemplate != null && httpHeaders != null) {
      try {
        String body = "{\"source\" : \"" + frontendURL + "\"}";
        HttpEntity<String> httpEntity = new HttpEntity<String>(body, httpHeaders);
        String url = getProtocol() + host + ":" + port + "/api/routes/deleteRouteBySource";
        ResponseEntity<String> response =
            restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        String responseBody = response.getBody();
        if (responseBody != null && responseBody.contains("{") && responseBody.contains("}")) {
          log.debug("Proxy-etc response for deleting route : " + responseBody);
          JSONObject jObject = new JSONObject(responseBody);
          if (!jObject.isNull("id")) {
            return true;
          }
        }
      } catch (Exception ex) {
        log.error("Error while deleting route from proxy-etc", ex);
      }
    } else {
      log.error("ProxyEtcReverseProxyClient is not initialized or parameters passed are wrong");
    }
    return false;
  }

  @Override
  public boolean deleteBackend(String frontendURL, String backendURL) {
    if (frontendURL != null && restTemplate != null && httpHeaders != null) {
      try {
        String body = "{\"source\" : \"" + frontendURL + "\", \"target\" : \"" + backendURL + "\"}";
        HttpEntity<String> httpEntity = new HttpEntity<String>(body, httpHeaders);
        String url = getProtocol() + host + ":" + port + "/api/routes/deleteTarget";
        ResponseEntity<String> response =
            restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        String responseBody = response.getBody();
        if (responseBody != null && responseBody.contains("{") && responseBody.contains("}")) {
          log.debug("Proxy-etc response for deleting target : " + responseBody);
          JSONObject jObject = new JSONObject(responseBody);
          if (!jObject.isNull("id")) {
            return true;
          }
        }
      } catch (Exception ex) {
        log.error("Error while deleting target from proxy-etc", ex);
      }
    } else {
      log.error("ProxyEtcReverseProxyClient is not initialized or parameters passed are wrong");
    }
    return false;
  }

  @Override
  public boolean serverReachable() {
    try {
      if (restTemplate != null && httpHeaders != null) {
        HttpEntity<String> httpEntity = new HttpEntity<String>(httpHeaders);
        String url = getProtocol() + host + ":" + port + "/api/routes/isReachable";
        ResponseEntity<String> response =
            restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
        String body = response.getBody();
        if (body != null && body.equals("success")) {
          return true;
        }
      } else {
        log.error("ProxyEtcReverseProxyClient is not initialized");
      }
    } catch (Exception ex) {
      log.error("Error while pinging proxy-etc");
    }
    return false;
  }
  
  private String getProtocol(){
    if(https)
      return "https://";
    
    return "http://";
  }

}
