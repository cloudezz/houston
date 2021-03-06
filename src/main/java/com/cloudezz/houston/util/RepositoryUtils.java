package com.cloudezz.houston.util;

import org.apache.commons.lang.RandomStringUtils;

public class RepositoryUtils {

  public static String generateSmallId() {
    String id = RandomStringUtils.randomNumeric(7);
    return id;
  }
  
  public static String generateBigId() {
    String id = RandomStringUtils.randomNumeric(15);
    return id;
  }
  
  public static String generateBigRandomAlphabetic() {
    String id = RandomStringUtils.randomAlphabetic(64);
    return id;
  }
  
  
}
