package com.cloudezz.houston.service;

import org.springframework.stereotype.Service;

import com.cloudezz.security.service.SecretKeyNotFoundException;
import com.cloudezz.security.service.SecretKeyService;

@Service
public class SecretKeyServiceImpl implements SecretKeyService {

  @Override
  public String getSecretKey(String userKey) throws SecretKeyNotFoundException {
    // TODO: Security issue : need to come from config file later
    return "static-testing-purpose";
  }

  @Override
  public String getDefaultSecretKey() throws SecretKeyNotFoundException {
    // TODO: Security issue : need to come from config file later
    return "static-testing-purpose";
  }

  @Override
  public boolean useDefault() {
    return true;
  }

}
