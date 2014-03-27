package com.cloudezz.houston.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.cloudezz.harmony.HarmonyConfigurator;

@Configuration
public class HarmonyProxyConfiguration {

  private final Logger log = LoggerFactory.getLogger(HarmonyProxyConfiguration.class);


  @Bean
  public HarmonyConfigurator harmonyProxyClient() throws Exception {

    HarmonyConfigurator harmonyConfigurator = null;
    try {
      String urlConfigFile =
          HarmonyProxyConfiguration.class.getClassLoader()
              .getResource("harmony/HAProxyConfigurator.properties").getPath();
      harmonyConfigurator = new HarmonyConfigurator(urlConfigFile);


      // check reverse proxy server conn settings
      if (!harmonyConfigurator.proxyConnectionStatus()) {
        log.warn("Warning! Not able to reach harmony reverse proxy server");
        log.debug("Did you configure your harmony HAproxy server settings in your harmony/HAProxyConfigurator.properties file");
      }
    } catch (Exception e) {
      log.error(e.getMessage());
    }

    return harmonyConfigurator;
  }



}
