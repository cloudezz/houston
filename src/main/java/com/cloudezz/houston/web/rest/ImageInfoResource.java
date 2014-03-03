package com.cloudezz.houston.web.rest;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.websocket.server.PathParam;
import javax.xml.bind.JAXBException;

import org.joda.time.LocalDateTime;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cloudezz.houston.domain.EnvForm;
import com.cloudezz.houston.domain.ImageInfo;
import com.cloudezz.houston.repository.ImageInfoRepository;
import com.cloudezz.houston.security.AuthoritiesConstants;
import com.cloudezz.houston.web.propertyeditors.LocaleDateTimeEditor;
import com.cloudezz.houston.web.rest.dto.AppImageCfgDTO;
import com.codahale.metrics.annotation.Timed;

/**
 * REST controller for getting the audit events.
 */
@RestController
@RequestMapping("/app")
public class ImageInfoResource {

  @Inject
  private ImageInfoRepository imageInfoRepository;

  @InitBinder
  public void initBinder(WebDataBinder binder) {
    binder.registerCustomEditor(LocalDateTime.class, new LocaleDateTimeEditor("yyyy-MM-dd", false));
  }
  
  @RequestMapping(value = "/rest/imageinfos", method = RequestMethod.POST,
      produces = "application/json", consumes = "application/json")
  @Timed
  public void create(@RequestBody ImageInfo imageInfo) {
    imageInfoRepository.save(imageInfo);
  }

  @RequestMapping(value = "/rest/imageinfos", method = RequestMethod.GET,
      produces = "application/json")
  @RolesAllowed(AuthoritiesConstants.ADMIN)
  public List<ImageInfo> findAll() {
    return imageInfoRepository.findAll();
  }

  @RequestMapping(value = "/rest/imageInfos/{name}", method = RequestMethod.GET,
      produces = "application/json")
  @RolesAllowed(AuthoritiesConstants.USER)
  public ImageInfo findByName(@PathParam(value = "name") String name) {
    return imageInfoRepository.findByImageName(name);
  }

  @RequestMapping(value = "/rest/imageInfos/{name}/form", method = RequestMethod.GET,
      produces = "application/json")
  @RolesAllowed(AuthoritiesConstants.USER)
  public EnvForm getEnvForm(@PathParam(value = "name") String name) throws JAXBException {
    ImageInfo imageInfo = imageInfoRepository.findByImageName(name);
    if (imageInfo == null) {
      throw new EntityNotFoundException("Couldnt find env form for image with name " + name);
    }
    return imageInfo.getEnvForm();
  }

}
