package com.cloudezz.houston.deployer.docker.model;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Represent only the response received from 'GET /containers/json' as all the data is not sent to
 * get the full details of the container use container inspect response object
 * 
 * @author Thanneer
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Container {

  @JsonProperty("Id")
  public String id;

  @JsonProperty("Names")
  public String[] names;

  @JsonProperty("Command")
  public String command;

  @JsonProperty("Image")
  public String image;

  @JsonProperty("Created")
  public long created;

  @JsonProperty("Status")
  public String status;

  @JsonProperty("Ports")
  public Port[] ports; // Example value "49164->6900, 49165->7100"

  @JsonProperty("SizeRw")
  public int sizeRw;

  @JsonProperty("Size")
  public int size;

  @JsonProperty("VirtualSize")
  public int virtualSize;

  @JsonProperty("SizeRootFs")
  public int sizeRootFs;

  @JsonProperty("Repository")
  public String repository;

  @JsonProperty("Tag")
  public String tag;

  /**
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * @param id the id to set
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * @return the names
   */
  public String[] getNames() {
    return names;
  }

  /**
   * @param names the names to set
   */
  public void setNames(String[] names) {
    this.names = names;
  }

  /**
   * @return the command
   */
  public String getCommand() {
    return command;
  }

  /**
   * @param command the command to set
   */
  public void setCommand(String command) {
    this.command = command;
  }

  /**
   * @return the image
   */
  public String getImage() {
    return image;
  }

  /**
   * @param image the image to set
   */
  public void setImage(String image) {
    this.image = image;
  }

  /**
   * @return the created
   */
  public long getCreated() {
    return created;
  }

  /**
   * @param created the created to set
   */
  public void setCreated(long created) {
    this.created = created;
  }

  /**
   * @return the status
   */
  public String getStatus() {
    return status;
  }

  /**
   * @param status the status to set
   */
  public void setStatus(String status) {
    this.status = status;
  }

  /**
   * @return the ports
   */
  public Port[] getPorts() {
    return ports;
  }

  /**
   * @param ports the ports to set
   */
  public void setPorts(Port[] ports) {
    this.ports = ports;
  }

  /**
   * @return the sizeRw
   */
  public int getSizeRw() {
    return sizeRw;
  }

  /**
   * @param sizeRw the sizeRw to set
   */
  public void setSizeRw(int sizeRw) {
    this.sizeRw = sizeRw;
  }

  /**
   * @return the size
   */
  public int getSize() {
    return size;
  }

  /**
   * @param size the size to set
   */
  public void setSize(int size) {
    this.size = size;
  }

  /**
   * @return the virtualSize
   */
  public int getVirtualSize() {
    return virtualSize;
  }

  /**
   * @param virtualSize the virtualSize to set
   */
  public void setVirtualSize(int virtualSize) {
    this.virtualSize = virtualSize;
  }

  /**
   * @return the sizeRootFs
   */
  public int getSizeRootFs() {
    return sizeRootFs;
  }

  /**
   * @param sizeRootFs the sizeRootFs to set
   */
  public void setSizeRootFs(int sizeRootFs) {
    this.sizeRootFs = sizeRootFs;
  }

  /**
   * @return the repository
   */
  public String getRepository() {
    return repository;
  }

  /**
   * @param repository the repository to set
   */
  public void setRepository(String repository) {
    this.repository = repository;
  }

  /**
   * @return the tag
   */
  public String getTag() {
    return tag;
  }

  /**
   * @param tag the tag to set
   */
  public void setTag(String tag) {
    this.tag = tag;
  }

  @Override
  public String toString() {
    return "Container{" + "id='" + id + '\'' + ", command='" + command + '\'' + ", image='" + image
        + '\'' + ", created=" + created + ", status='" + status + '\'' + ", ports="
        + Arrays.toString(ports) + ", sizeRw=" + sizeRw + ", size=" + size + ", virtualSize="
        + virtualSize + ", sizeRootFs=" + sizeRootFs + ", repository=" + repository + ", tag="
        + tag + '}';
  }
 
}
