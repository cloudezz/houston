package com.cloudezz.houston.util;

import java.io.IOException;
import java.net.Socket;

public class ConnectionUtils {

  public static boolean urlReachable(String host, int port) {
    Socket socket = null;
    boolean reachable = false;
    try {
      socket = new Socket(host, port);
      reachable = true;
    } catch (IOException e) {
      reachable = false;
    } finally {
      if (socket != null)
        try {
          socket.close();
        } catch (IOException e) {
        }
    }
    return reachable;
  }
}
