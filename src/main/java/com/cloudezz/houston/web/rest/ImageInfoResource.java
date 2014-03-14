package com.cloudezz.houston.web.rest;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.xml.bind.JAXBException;

import org.joda.time.LocalDateTime;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cloudezz.houston.domain.ImageInfo;
import com.cloudezz.houston.domain.ImgSettings.Form;
import com.cloudezz.houston.repository.ImageInfoRepository;
import com.cloudezz.houston.repository.RepositoryUtils;
import com.cloudezz.houston.security.AuthoritiesConstants;
import com.cloudezz.houston.web.propertyeditors.LocaleDateTimeEditor;
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
    if (imageInfo.getId() == null) {
      imageInfo.setId(RepositoryUtils.generateSmallId());
    }
    imageInfoRepository.save(imageInfo);
  }

  @RequestMapping(value = "/rest/imageinfos", method = RequestMethod.GET,
      produces = "application/json")
  @RolesAllowed(AuthoritiesConstants.USER)
  public List<ImageInfo> findAll() {
    return imageInfoRepository.findAll();
  }

  @RequestMapping(value = "/rest/imageinfos/type/{type}", method = RequestMethod.GET,
      produces = "application/json")
  @RolesAllowed(AuthoritiesConstants.USER)
  public List<ImageInfo> findByType(@PathVariable(value = "type") String type) {
    if (type.equalsIgnoreCase("service"))
      return imageInfoRepository.findByServiceImage(true);

    return findAll();
  }

  @RequestMapping(value = "/rest/imageInfos/{name}", method = RequestMethod.GET,
      produces = "application/json")
  @RolesAllowed(AuthoritiesConstants.USER)
  public ImageInfo findByName(@PathVariable(value = "name") String name) {
    return imageInfoRepository.findByImageName(name);
  }

  @RequestMapping(value = "/rest/imageInfos/form/{id}", method = RequestMethod.GET,
      produces = "application/json")
  @RolesAllowed(AuthoritiesConstants.USER)
  public Form getEnvForm(@PathVariable(value = "id") String id) throws JAXBException {
    ImageInfo imageInfo = imageInfoRepository.findOne(id);
    if (imageInfo == null) {
      throw new EntityNotFoundException("Couldnt find env form for image with name " + id);
    }
    return imageInfo.getEnvForm();
  }
}
