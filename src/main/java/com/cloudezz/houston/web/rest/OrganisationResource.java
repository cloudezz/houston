package com.cloudezz.houston.web.rest;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cloudezz.houston.domain.Organisation;
import com.cloudezz.houston.repository.OrganisationRepository;
import com.cloudezz.houston.util.RepositoryUtils;
import com.cloudezz.houston.web.rest.dto.OrganisationDTO;
import com.codahale.metrics.annotation.Timed;

/**
 * REST controller for managing Organisations.
 */
@RestController
@RequestMapping("/app")
public class OrganisationResource {

  private final Logger log = LoggerFactory.getLogger(OrganisationResource.class);

  @Inject
  private OrganisationRepository organisationRepository;

  /**
   * GET /rest/organisation/:id -> get the organisation with id
   */
  @RequestMapping(value = "/rest/organisation/{id}", method = RequestMethod.GET,
      produces = "application/json")
  @Timed
  public Organisation getOrganisationById(@PathVariable String id, HttpServletResponse response) {
    log.debug("REST request to get Organisation : {}", id);
    Organisation org = organisationRepository.findByOrgId(id);
    if (org == null) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
    return org;
  }

  @RequestMapping(value = "/rest/organisation", method = RequestMethod.POST,
      produces = "application/json")
  @Timed
  public Organisation createOrganisation(@RequestBody OrganisationDTO orgDTO,
      HttpServletResponse response) {
    Organisation org = new Organisation();
    org.setId(RepositoryUtils.generateSmallId());
    org.setName(orgDTO.getOrgName());
    org.setDesc(orgDTO.getOrgDesc());

    log.debug("REST request to get Organisation : {}", org.getName());
    org = organisationRepository.saveAndFlush(org);
    return org;
  }

  /**
   * GET /rest/organisations -> get all the organisations.
   */
  @RequestMapping(value = "/rest/organisation", method = RequestMethod.GET,
      produces = "application/json")
  @Timed
  public List<OrganisationDTO> getAllOrganisations() {
    log.debug("REST request to get all organisation");
    List<OrganisationDTO> orgDtos = null;
    try {
      List<Organisation> orgs = organisationRepository.findAll();
      orgDtos = convertToDto(orgs);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    return orgDtos;
  }

  private List<OrganisationDTO> convertToDto(List<Organisation> orgs) {
    List<OrganisationDTO> orgDtos = new ArrayList<>();
    for (Organisation org : orgs) {
      OrganisationDTO orgDto = new OrganisationDTO();
      orgDto.setOrgId(org.getId());
      orgDto.setOrgName(org.getName());
      orgDto.setOrgDesc(org.getDesc());
      orgDtos.add(orgDto);
    }
    return orgDtos;
  }


}
