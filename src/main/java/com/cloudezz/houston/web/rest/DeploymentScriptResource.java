package com.cloudezz.houston.web.rest;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cloudezz.houston.domain.DeploymentScript;
import com.cloudezz.houston.domain.User;
import com.cloudezz.houston.repository.DeploymentScriptRepository;
import com.cloudezz.houston.repository.UserRepository;
import com.cloudezz.houston.security.SecurityUtils;
import com.codahale.metrics.annotation.Timed;

/**
 * REST controller for managing DeploymentScript.
 */
@RestController
@RequestMapping("/app")
public class DeploymentScriptResource {

  private final Logger log = LoggerFactory.getLogger(DeploymentScriptResource.class);

  @Inject
  private DeploymentScriptRepository deploymentScriptRepository;

  @Inject
  private UserRepository userRepository;

  @Value("${deploymentScript.filePath}")
  private String filePath;

  /**
   * GET /rest/deploymentScript -> get all the deploymentScript.
   */
  @RequestMapping(value = "/rest/deploymentScript", method = RequestMethod.GET,
      produces = "application/json")
  @Timed
  public List<Map<String, Object>> getAllForCurrentUser() {
    log.debug("REST request to get all DeploymentScripts");
    List<Map<String, Object>> scripts = new ArrayList<Map<String, Object>>();
    List<DeploymentScript> deploymentScripts = new ArrayList<DeploymentScript>();
    try {
      User currentLoggedInUser = userRepository.getOne(SecurityUtils.getCurrentLogin());
      deploymentScripts = deploymentScriptRepository.getAllForUser(currentLoggedInUser);
      for (Iterator<DeploymentScript> iterator = deploymentScripts.iterator(); iterator.hasNext();) {
        DeploymentScript deploymentScript = iterator.next();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", deploymentScript.getId());
        map.put("scriptName", deploymentScript.getScriptName());
        map.put("scriptId", deploymentScript.getScriptId());
        map.put("description", deploymentScript.getDesc());
        map.put("folderStructure",
            getFolderStructure(new File(filePath + "/" + deploymentScript.getScriptId())));
        scripts.add(map);
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    return scripts;
  }

  /**
   * GET /rest/deploymentScript/:id -> get the "id" deploymentScript.
   */
  @RequestMapping(value = "/rest/deploymentScript/{id}", method = RequestMethod.GET,
      produces = "application/json")
  @Timed
  public DeploymentScript get(@PathVariable String id, HttpServletResponse response) {
    log.debug("REST request to get DeploymentScript : {}", id);
    DeploymentScript deploymentScript = deploymentScriptRepository.findOne(id);
    if (deploymentScript == null) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
    return deploymentScript;
  }

  /**
   * DELETE /rest/deploymentScript/:id -> delete the "id" deploymentScript.
   */
  @RequestMapping(value = "/rest/deploymentScript/{id}", method = RequestMethod.DELETE,
      produces = "application/json")
  @Timed
  public void delete(@PathVariable String id, HttpServletResponse response) {
    log.debug("REST request to delete application : {}", id);
    try {
      DeploymentScript deploymentScript = deploymentScriptRepository.findOne(id);
      if (deploymentScript == null) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      } else {
        File dir = new File(filePath + File.separator + deploymentScript.getScriptId());
        FileUtils.deleteDirectory(dir);
        deploymentScriptRepository.delete(id);
      }
    } catch (Exception e) {
      log.error("Failed during delete deploymentScript", e);
    }
  }

  /**
   * POST /rest/deploymentScript -> Create a new deploymentScript.
   */
  @RequestMapping(value = "/rest/deploymentScript/create", method = RequestMethod.POST)
  public Map<String, String> create(HttpServletRequest request,
      @RequestParam("file") MultipartFile multipartFile,
      @RequestParam("scriptName") String scriptName, @RequestParam("description") String description) {
    log.debug("REST request to save deploymentScript : {}");
    Map<String, String> map = new HashMap<String, String>();
    try {
      if (scriptName != null && description != null && multipartFile != null) {
        DeploymentScript deploymentScript = new DeploymentScript();
        deploymentScript.setScriptName(scriptName);
        deploymentScript.setDesc(description);

        String scriptId = RandomStringUtils.randomAlphanumeric(10);
        String ext = "";

        ext = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
        File file = new File(filePath + "/" + scriptId + "." + ext);

        byte[] bytes = multipartFile.getBytes();
        BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(file));
        stream.write(bytes);
        stream.close();

        unzipFile(filePath + "/" + scriptId, ext);


        File directory = new File(filePath + "/" + scriptId);
        String ymlFileName = "";

        if (directory.exists() && directory.isDirectory()) {
          File[] files = directory.listFiles();
          for (File file1 : files) {
            String fileExt = FilenameUtils.getExtension(filePath + "/" + file1.getName());
            if (fileExt.equals("yml")) {
              ymlFileName = file1.getName();
            }
          }
        }

        if (!ymlFileName.equals("")) {
          // set owner
          String email = SecurityUtils.getCurrentLogin();
          User currentUser = userRepository.getOne(email);

          deploymentScript.setOwner(currentUser);
          deploymentScript.setScriptId((scriptId));
          deploymentScript.setYmlFileName(ymlFileName);

          deploymentScript = deploymentScriptRepository.saveAndFlush(deploymentScript);
          log.debug("Saved the deploymentScript : " + deploymentScript.getScriptName());
          map.put("message", "File uploaded successfully");
        } else {
          FileUtils.deleteDirectory(directory);
          map.put("error", "Upload failed : yml file not existed");
        }
      } else {
        map.put("error", "Prameters passed are incorrect");
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      map.put("error", "File Upload Failed");
    }
    return map;
  }

  public List<Map<String, Object>> getFolderStructure(File file) {
    List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    if (file.isDirectory()) {
      File[] files = file.listFiles();
      for (File fileInDir : files) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (fileInDir.isDirectory()) {
          map.put("name", fileInDir.getName());
          map.put("type", "folder");
          map.put("files", getFolderStructure(fileInDir));
        } else {
          map.put("name", fileInDir.getName());
          map.put("type", "item");
          map.put("files", "");
        }
        list.add(map);
      }
    } else {
      return null;
    }
    return list;
  }

  public void unzipFile(String filename, String ext) throws IOException {
    ZipFile zipFile = new ZipFile(filename + "." + ext);
    Enumeration<?> enu = zipFile.entries();
    while (enu.hasMoreElements()) {
      ZipEntry zipEntry = (ZipEntry) enu.nextElement();

      String name = zipEntry.getName();
      long size = zipEntry.getSize();
      long compressedSize = zipEntry.getCompressedSize();
      System.out.printf("name: %-20s | size: %6d | compressed size: %6d\n", name, size,
          compressedSize);

      File file = new File(filename + "/" + name);
      if (name.endsWith("/")) {
        file.mkdirs();
        continue;
      }

      File parent = file.getParentFile();
      if (parent != null) {
        parent.mkdirs();
      }

      InputStream is = zipFile.getInputStream(zipEntry);
      FileOutputStream fos = new FileOutputStream(file);
      byte[] bytes = new byte[1024];
      int length;
      while ((length = is.read(bytes)) >= 0) {
        fos.write(bytes, 0, length);
      }
      is.close();
      fos.close();
    }
    zipFile.close();
    File file = new File(filename + "." + ext);
    file.delete();
  }

}
