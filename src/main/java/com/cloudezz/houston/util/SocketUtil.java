package com.cloudezz.houston.util;



import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to find a free port on docker host
 * 
 * @author Thanneer
 * @since 1.0.0
 */
public class SocketUtil {

  private static Logger log = LoggerFactory.getLogger(SocketUtil.class);

  /**
   * Finds free or available ports . The portsNo is the number of free ports that has to be
   * returned.
   * 
   * @param host
   * @param portsNo
   * @return
   */
  public static String[] nextAvailablePort(String host, int portsNo) {
    log.debug("Finding "+  portsNo+ " free ports in host " + host);
    String[] result = new String[portsNo];
    List<ServerSocket> servers = new ArrayList<ServerSocket>();
    ServerSocket tempServer = null;

    for (int i = 0; i < portsNo; i++) {
      try {
        tempServer = new ServerSocket();
        tempServer.bind(new InetSocketAddress(host, 0));
        tempServer.setReuseAddress(true);
        servers.add(tempServer);
        result[i] = tempServer.getLocalPort() + "";
      } catch (Exception e) {
        // do nothing
      } finally {
        for (ServerSocket server : servers) {
          try {
            server.close();
          } catch (IOException e) {
            // Continue closing servers.
          }
        }
      }
    }
    return result;
  }


}
