package com.cloudezz.houston.domain;

import java.io.StringReader;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

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

  @Id
  @Column(name = "image_name")
  private String imageName;

  @Column(name = "build_pack_git_url")
  private String buildPackGitURL;

  @Column(name = "logo_url")
  private String logoURL;

  @Column(name = "is_service_image")
  private boolean isServiceImage;

  @Column(name = "env_form_block")
  private String envFormBlock;

  @Override
  public String getId() {
    return imageName;
  }

  @Override
  public void setId(String id) {
    imageName = id;
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
    return isServiceImage;
  }

  public void setServiceImage(boolean isServiceImage) {
    this.isServiceImage = isServiceImage;
  }

  public String getEnvFormBlock() {
    return envFormBlock;
  }

  public void setEnvFormBlock(String envFormBlock) {
    this.envFormBlock = envFormBlock;
  }

  public EnvForm getEnvForm() throws JAXBException {
    if (getEnvFormBlock() == null)
      throw new IllegalStateException("The env block is not set yet");
    
    JAXBContext jc = JAXBContext.newInstance(EnvForm.class);
    Unmarshaller unmarshaller = jc.createUnmarshaller();
    StringReader reader = new StringReader(getEnvFormBlock());
    return (EnvForm) unmarshaller.unmarshal(reader);
  }



}
