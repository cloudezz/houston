package com.cloudezz.houston.web.rest.dto;

public class AppImageCfgDTO extends BaseImageCfgDTO {

  protected String initScript;

  public String getInitScript() {
    return initScript;
  }

  public void setInitScript(String initScript) {
    this.initScript = initScript;
  }
}
