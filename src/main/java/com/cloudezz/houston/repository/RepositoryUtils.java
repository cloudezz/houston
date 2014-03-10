package com.cloudezz.houston.repository;

import org.apache.commons.lang.RandomStringUtils;

public class RepositoryUtils {

  public static String generateId() {
    String id = RandomStringUtils.randomNumeric(5);
    return id;
  }
  
  
  public static String generateBigId() {
    String id = RandomStringUtils.randomNumeric(12);
    return id;
  }
}
