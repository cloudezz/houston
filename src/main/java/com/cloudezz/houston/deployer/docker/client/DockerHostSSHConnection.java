package com.cloudezz.houston.deployer.docker.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

import com.cloudezz.houston.domain.DockerHostMachine;

/**
 * This a helper class for SSH connectivity.
 */

public class DockerHostSSHConnection {

  private final Logger log = LoggerFactory.getLogger(DockerHostSSHConnection.class);

  private Connection connection;
  private SCPClient scpcClient;

  public DockerHostSSHConnection(DockerHostMachine dockerHostMachine) throws IOException {
    String keyfilePass = ""; // will be ignored if not needed
    /* Create a connection instance */
    connection =
        new Connection(dockerHostMachine.getHostName(), Integer.parseInt(dockerHostMachine
            .getSshPort()));

    /* Now connect */
    connection.connect();

    boolean isAuthenticated = false;
    if (dockerHostMachine.getKey() != null && dockerHostMachine.getKey().length() > 0) {
      char[] keyfileArray = new String(dockerHostMachine.getKey()).toCharArray();
      /* Authenticate */
      isAuthenticated =
          connection.authenticateWithPublicKey(dockerHostMachine.getUsername(), keyfileArray,
              keyfilePass);
    } else {
      isAuthenticated =
          connection.authenticateWithPassword(dockerHostMachine.getUsername(),
              dockerHostMachine.getPassword());
    }

    if (!isAuthenticated)
      throw new IOException("Authentication failed.");
  }


  private SCPClient getSCPClient() throws IOException {
    if (scpcClient == null) {
      scpcClient = new SCPClient(connection);
    }
    return scpcClient;
  }

  /**
   * This method upload a file via SCPC
   * 
   * @param data The file data
   * @param remoteFileName The remote file name
   * @param remoteTargetDirectory The remote file directory
   * @throws IOException
   */
  public void upload(byte[] data, String remoteFileName, String remoteTargetDirectory)
      throws IOException {
    getSCPClient().put(data, remoteFileName, remoteTargetDirectory);
  }

  /**
   * This method execute a command via SSH
   * 
   * @param cmd The command to execute
   * @return Return the outout of the command as a List of Strings
   * @throws IOException
   */
  public List<String> execCommand(String cmd) throws IOException {
    return execCommand(cmd, false);
  }

  /**
   * This method execute a command via SSH
   * 
   * @param cmd The command to execute
   * @param sudo need to append sudo or not to cmd
   * @return Return the outout of the command as a List of Strings
   * @throws IOException
   */
  public Map<String, List<String>> execCommand(boolean sudo ,String... cmds) throws IOException {
    Map<String, List<String>> mapCmdtoResult = new HashMap<>();
    for (String cmd : cmds) {
      List<String> result = execCommand(cmd, sudo);
      mapCmdtoResult.put(cmd, result);
    }
    
    return mapCmdtoResult;
  }

  /**
   * This method execute a command via SSH
   * 
   * @param cmd The command to execute
   * @param sudo need to append sudo or not to cmd
   * @return Return the outout of the command as a List of Strings
   * @throws IOException
   */
  public List<String> execCommand(String cmd, boolean sudo) throws IOException {
    if (cmd == null)
      return new ArrayList<>();

    if (sudo) {
      cmd = "sudo " + cmd;
    }

    /* Create a session */
    Session session = connection.openSession();

    List<String> output = new ArrayList<String>();
    try {
      session.requestDumbPTY();
      session.execCommand(cmd);

      
      InputStream stdout = new StreamGobbler(session.getStdout());
      InputStream stderr = new StreamGobbler(session.getStderr());
      
      InputStreamReader insrout=new InputStreamReader(stdout);
      InputStreamReader insrerr=new InputStreamReader(stderr);
      
      BufferedReader stdoutReader = new BufferedReader(insrout);
      BufferedReader stderrReader = new BufferedReader(insrerr);

      while (true)
      {
          String line = stdoutReader.readLine();
          if (line == null)
          {    
              break;
          }    
          System.out.println("o>" + line);
      }
      
      while (true)
      {
          String line = stderrReader.readLine();
          if (line == null)
          {    break;}
          System.out.println("o>" + line);
      }
      
    } finally {
      session.close();
    }

    for (String str : output) {
      log.debug(str);
    }

    return output;
  }

  /**
   * This method closes the SSH connections
   */
  public void close() {
    /* Close the connection */
    if (connection != null) {
      connection.close();
      connection = null;
    }

    if (scpcClient != null) {
      scpcClient = null;
    }
  }

}
