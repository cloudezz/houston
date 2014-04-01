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


  // the ports below 1024 are system ports and 49155 to 65535 is a good range for dynamic port
  private static final int MIN_PORT_NUMBER = 49155;

  // the ports above 49155 to 65535 is a good range for dynamic port
  private static final int MAX_PORT_NUMBER = 65535;

  /**
   * Finds a free or available port between {@link #MIN_PORT_NUMBER} and {@link #MAX_PORT_NUMBER}.
   * 
   * @return a free or available port
   * @throw RuntimeException if a port could not be found
   */
  public static String[] nextAvailablePort(String host, int portsNo) {
    // int start=MIN_PORT_NUMBER;
    // int end=MAX_PORT_NUMBER;
    // String[] availablePorts = new String[portsNo];
    // for (int i = 0; i < availablePorts.length; i++) {
    // for (int j = start; j <= end; j++) {
    // if (available(host, j)) {
    // availablePorts[i] = j + "";
    // start = j+1;
    // break;
    // }
    // }
    // }
    // return availablePorts;
    return getFreePorts(host, portsNo);
  }

  public static String[] getFreePorts(String host, int portNumber) {
    String[] result = new String[portNumber];
    List<ServerSocket> servers = new ArrayList<ServerSocket>();
    ServerSocket tempServer = null;

    for (int i = 0; i < portNumber; i++) {
      try {
        tempServer = new ServerSocket();
        tempServer.bind(new InetSocketAddress(host, 0));
        tempServer.setReuseAddress(true);
        servers.add(tempServer);
        result[i] = tempServer.getLocalPort() + "";
      } catch (Exception e) {
       System.out.println(e.getMessage());
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

//  /**
//   * Returns true if the specified port is available on this host.
//   * 
//   * @param port the port to check
//   * @return true if the port is available, false otherwise
//   */
//  private static boolean available(final String host, final int port) {
//    log.debug("--------------Testing port " + port + " on host " + host);
//    Socket s = null;
//    try {
//      s = new Socket(host, port);
//      // If the code makes it this far without an exception it means
//      // something is using the port and has responded.
//      return false;
//    } catch (IOException e) {
//      log.debug("--------------Port " + port + " is available on host " + host);
//      return true;
//    } finally {
//      if (s != null) {
//        try {
//          s.close();
//        } catch (IOException e) {
//          throw new RuntimeException("You should handle this error.", e);
//        }
//      }
//    }
//  }
//
//  public static void main(String[] args) {
//    for (String string : SocketUtil.nextAvailablePort("127.0.0.1", 10)) {
//      System.out.println(string);
//    }
//  }
}
