package com.cloudezz.houston.web.rest;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.joda.time.LocalDateTime;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cloudezz.houston.domain.EnvForm;
import com.cloudezz.houston.domain.ImageInfo;
import com.cloudezz.houston.repository.ImageInfoRepository;
import com.cloudezz.houston.security.AuthoritiesConstants;
import com.cloudezz.houston.web.propertyeditors.LocaleDateTimeEditor;

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
		binder.registerCustomEditor(LocalDateTime.class,
				new LocaleDateTimeEditor("yyyy-MM-dd", false));
	}

	@RequestMapping(value = "/rest/imageinfos/all", method = RequestMethod.GET, produces = "application/json")
	@RolesAllowed(AuthoritiesConstants.ADMIN)
	public List<ImageInfo> findAll() {
		return imageInfoRepository.findAll();
	}

	@RequestMapping(value = "/rest/imageInfos/{name}", method = RequestMethod.GET, produces = "application/json")
	@RolesAllowed(AuthoritiesConstants.USER)
	public ImageInfo findByName(@RequestParam(value = "name") String name) {
		return imageInfoRepository.findByImageName(name);
	}

	@RequestMapping(value = "/rest/imageInfos/form", method = RequestMethod.GET, produces = "application/json")
	@RolesAllowed(AuthoritiesConstants.USER)
	public EnvForm getEnvForm(@RequestParam(value = "name") String name)
			throws JAXBException {
		// ImageInfo imageInfo = imageInfoRepository.findByImageName(name);
		// if (imageInfo == null) {
		// throw new
		// EntityNotFoundException("Couldnt find env form for image with name "
		// + name);
		// }
		// return imageInfo.getEnvForm();
		return getFormFromXml();
	}

	private EnvForm getFormFromXml() {
		try {
			JAXBContext jaxbContext = JAXBContext
					.newInstance(com.cloudezz.houston.domain.EnvForm.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			EnvForm form = (EnvForm) unmarshaller
					.unmarshal(new FileInputStream(
							"C:/Projects/CloudezzWS/Cloudezz/src/main/resources/form/cloudezz-tomcat-form-builder.xml"));
			return form;

		} catch (JAXBException je) {
			je.printStackTrace();
			return null;
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return null;
		}
	}

}
