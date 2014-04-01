package com.cloudezz.houston.domain;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.cloudezz.houston.domain.ImgSettings.Form;
import com.cloudezz.houston.domain.ImgSettings.PortConfig.Port;
import com.cloudezz.houston.domain.ImgSettings.VolumeConfig.VolumeMapping;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Docker Image is the one hold the map between the build back git url and docker image name . This
 * object is a jpa object to the table that holds the map info
 * 
 * @author Thanneer
 * 
 */
@Entity
@Table(name = "T_IMAGE_INFO")
public class ImageInfo extends BaseEntity {

  private static final long serialVersionUID = 8051510635093548094L;

  private static final String DEFAULT_PORT_LIST = "22,4022,7946";

  @Id
  @Column(name = "id")
  protected String id;

  @Column(name = "image_name")
  private String imageName;

  @Column(name = "build_pack_git_url")
  private String buildPackGitURL;

  @Column(name = "logo_url")
  private String logoURL;

  @Column(name = "service_image", nullable = false, columnDefinition = "TINYINT")
  private boolean serviceImage;

  @Column(name = "img_settings_block", length = 5000)
  private String imgSettingsBlock;

  @Column(name = "init_script", length = 5000)
  private String initScript;

  @Column(name = "image_version")
  private String version;

  @Column(name = "short_desc")
  private String shortDesc;

  @Column(name = "description", length = 5000)
  private String desc;
  
  @Column(name = "role")
  private String role;

  // Comma separated ports
  @Column(name = "exposed_ports")
  private String exposedImagePorts;

  @Override
  public String getId() {
    return id;
  }

  @Override
  public void setId(String id) {
    this.id = id;
  }

  /**
   * @return the imageName
   */
  public String getImageName() {
    return imageName;
  }

  /**
   * @param imageName the imageName to set
   */
  public void setImageName(String imageName) {
    this.imageName = imageName;
  }

  /**
   * @return the buildPackGitURL
   */
  public String getBuildPackGitURL() {
    return buildPackGitURL;
  }

  /**
   * @param buildPackGitURL the buildPackGitURL to set
   */
  public void setBuildPackGitURL(String buildPackGitURL) {
    this.buildPackGitURL = buildPackGitURL;
  }

  public String getLogoURL() {
    return logoURL;
  }

  public void setLogoURL(String logoURL) {
    this.logoURL = logoURL;
  }

  public boolean isServiceImage() {
    return serviceImage;
  }

  public void setServiceImage(boolean isServiceImage) {
    this.serviceImage = isServiceImage;
  }


  public String getImgSettingsBlock() {
    return imgSettingsBlock;
  }

  public void setImgSettingsBlock(String imgSettingsBlock) {
    this.imgSettingsBlock = imgSettingsBlock;
  }

  @JsonIgnore
  public Form getEnvForm() throws JAXBException {
    if (getImgSettingsBlock() == null)
      throw new IllegalStateException("The settings block is not set yet");

    JAXBContext jc = JAXBContext.newInstance(ImgSettings.class);
    Unmarshaller unmarshaller = jc.createUnmarshaller();
    StringReader reader = new StringReader(getImgSettingsBlock());
    ImgSettings imgSettings = (ImgSettings) unmarshaller.unmarshal(reader);
    return imgSettings.getForm();
  }

  @JsonIgnore
  public List<Port> getPortsExposed() throws JAXBException {
    if (getImgSettingsBlock() == null)
      throw new IllegalStateException("The settings block is not set yet");

    JAXBContext jc = JAXBContext.newInstance(ImgSettings.class);
    Unmarshaller unmarshaller = jc.createUnmarshaller();
    StringReader reader = new StringReader(getImgSettingsBlock());
    ImgSettings imgSettings = (ImgSettings) unmarshaller.unmarshal(reader);
    if (imgSettings.getPortConfig() == null)
      return null;

    return imgSettings.getPortConfig().getPort();
  }

  @JsonIgnore
  public List<VolumeMapping> getVolumeMapping() throws JAXBException {
    if (getImgSettingsBlock() == null)
      throw new IllegalStateException("The settings block is not set yet");

    JAXBContext jc = JAXBContext.newInstance(ImgSettings.class);
    Unmarshaller unmarshaller = jc.createUnmarshaller();
    StringReader reader = new StringReader(getImgSettingsBlock());
    ImgSettings imgSettings = (ImgSettings) unmarshaller.unmarshal(reader);
    if (imgSettings.getVolumeConfig() == null)
      return null;

    return imgSettings.getVolumeConfig().getVolumeMapping();
  }

  public String getInitScript() {
    return initScript;
  }

  public void setInitScript(String initScript) {
    this.initScript = initScript;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getShortDesc() {
    return shortDesc;
  }

  public void setShortDesc(String shortDesc) {
    this.shortDesc = shortDesc;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  public String getExposedImagePorts() {
    return exposedImagePorts;
  }

  public void setExposedImagePorts(String exposedImagePorts) {
    this.exposedImagePorts = exposedImagePorts;
  }

  public List<String> getExposedPorts() {
    return new ArrayList<String>(Arrays.asList(exposedImagePorts.split(",")));
  }
  
  public List<String> getDefaultPorts() {
    return new ArrayList<String>(Arrays.asList(DEFAULT_PORT_LIST.split(",")));
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  // public static void main(String args[]) throws JsonProcessingException {
  // ObjectMapper mapper = new ObjectMapper();
  // ImageInfo info = new ImageInfo();
  // info.setImageName(" a ");
  // info.setBuildPackGitURL(" asa");
  // info.setEnvFormBlock(" asa");
  // info.setServiceImage(false);
  // info.setLogoURL(" asa");
  //
  // System.out.println(mapper.writeValueAsString(info));
  // }

}
