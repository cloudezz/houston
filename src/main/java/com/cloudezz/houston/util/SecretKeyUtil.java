package com.cloudezz.houston.util;



import java.io.IOException;
import java.security.SecureRandom;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Perform AES-128 encryption.
 */
public final class SecretKeyUtil {
  private static final Logger log = LoggerFactory.getLogger(SecretKeyUtil.class);


  /**
   * Private constructor that should never be called.
   */
  private SecretKeyUtil() {}


  public static String getSerfSecretKey() {
    try {
      return IOUtils.toString(Base64.encodeBase64(getRandomByteArray(16)), "UTF-8");
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
    return null;
  }

  public static byte[] getRandomByteArray(int size) {
    SecureRandom random = new SecureRandom();
    return random.generateSeed(size);
  }

}
