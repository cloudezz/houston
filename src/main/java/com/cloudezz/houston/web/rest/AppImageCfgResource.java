package com.cloudezz.houston.web.rest;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.cloudezz.houston.deployer.Deployer;
import com.cloudezz.houston.deployer.docker.client.CloudezzDeployException;
import com.cloudezz.houston.domain.AppImageCfg;
import com.cloudezz.houston.domain.DockerHostMachine;
import com.cloudezz.houston.domain.EnvForm;
import com.cloudezz.houston.domain.FileMeta;
import com.cloudezz.houston.repository.AppImageCfgRepository;
import com.cloudezz.houston.repository.DockerHostMachineRepository;
import com.codahale.metrics.annotation.Timed;

/**
 * REST controller for managing AppImageCfg.
 */
@RestController
@RequestMapping("/app")
public class AppImageCfgResource {

	private final Logger log = LoggerFactory
			.getLogger(AppImageCfgResource.class);

	@Inject
	private AppImageCfgRepository appimagecfgRepository;

	@Inject
	private DockerHostMachineRepository dockerHostMachineRepository;

	@Autowired
	private Deployer deployer;

	/**
	 * POST /rest/appimagecfgs -> Create a new appimagecfg.
	 */
	@RequestMapping(value = "/rest/appimagecfgs", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	@Timed
	public void create(@RequestBody AppImageCfg appimagecfg) {
		log.debug("REST request to save AppImageCfg : {}", appimagecfg);
		if (appimagecfg.getDockerHostMachine() == null) {
			DockerHostMachine dockerHostMachine = dockerHostMachineRepository
					.getOne("127.0.0.1");
			appimagecfg.setDockerHostMachine(dockerHostMachine);
		}
		appimagecfg.setDockerImageName("cloudezz/tomcat7");
		appimagecfgRepository.save(appimagecfg);
	}

	@RequestMapping(value = "/rest/appimagecfgs/start/{id}", method = RequestMethod.POST)
	@Timed
	public boolean start(@PathVariable String id, HttpServletResponse response) {
		AppImageCfg appImageCfg = appimagecfgRepository.findOne(id);
		if (appImageCfg == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
		boolean status = false;
		try {
			status = deployer.start(appImageCfg);
		} catch (CloudezzDeployException e) {
			log.error(e.getMessage());
		}
		return status;
	}

	@RequestMapping(value = "/rest/appimagecfgs/stop/{id}", method = RequestMethod.POST)
	@Timed
	public boolean stop(@PathVariable String id, HttpServletResponse response) {
		AppImageCfg appImageCfg = appimagecfgRepository.findOne(id);
		if (appImageCfg == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
		boolean status = false;
		// try {
		// status = deployer.stop(appImageCfg);
		// } catch (CloudezzDeployException e) {
		// log.error(e.getMessage());
		// }
		return status;
	}

	/**
	 * GET /rest/appimagecfgs -> get all the appimagecfgs.
	 */
	@RequestMapping(value = "/rest/appimagecfgs", method = RequestMethod.GET, produces = "application/json")
	@Timed
	public List<AppImageCfg> getAll() {
		log.debug("REST request to get all AppImageCfgs");
		try {
			return appimagecfgRepository.findAll();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * GET /rest/appimagecfgs/:id -> get the "id" appimagecfg.
	 */
	@RequestMapping(value = "/rest/appimagecfgs/{id}", method = RequestMethod.GET, produces = "application/json")
	@Timed
	public AppImageCfg get(@PathVariable String id, HttpServletResponse response) {
		log.debug("REST request to get AppImageCfg : {}", id);
		AppImageCfg appimagecfg = appimagecfgRepository.findOne(id);
		if (appimagecfg == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
		return appimagecfg;
	}

	/**
	 * DELETE /rest/appimagecfgs/:id -> delete the "id" appimagecfg.
	 */
	@RequestMapping(value = "/rest/appimagecfgs/{id}", method = RequestMethod.DELETE, produces = "application/json")
	@Timed
	public void delete(@PathVariable String id, HttpServletResponse response) {
		log.debug("REST request to delete AppImageCfg : {}", id);
		appimagecfgRepository.delete(id);
	}

	@RequestMapping(value = "/rest/loadForm", method = RequestMethod.GET, produces = "application/json")
	@Timed
	public EnvForm getForm(@RequestParam String serviceId) {
		EnvForm formObj = getFormFromXml();
		return formObj;
	}

	@RequestMapping(value = "/rest/saveForm", method = RequestMethod.POST, produces = "application/json")
	@Timed
	public EnvForm saveForm(@RequestBody EnvForm form) {
		System.out.println(form);
		return form;
	}

	LinkedList<FileMeta> files = new LinkedList<FileMeta>();
	FileMeta fileMeta = null;

	@RequestMapping(value = "/rest/upload", method = RequestMethod.POST)
	public @ResponseBody
	LinkedList<FileMeta> upload(MultipartHttpServletRequest request,
			HttpServletResponse response) {
		Iterator<String> itr = request.getFileNames();
		MultipartFile mpf = null;
		while (itr.hasNext()) {
			mpf = request.getFile(itr.next());
			System.out.println((mpf.getOriginalFilename() + "uploaded!" + files
					.size()));
			if (files.size() >= 10)
				files.poll();
			fileMeta = new FileMeta();
			fileMeta.setFileName(mpf.getOriginalFilename());
			fileMeta.setFileSize(mpf.getSize() / 1024 + " Kb");
			fileMeta.setFileType(mpf.getContentType());
			try {
				fileMeta.setBytes(mpf.getBytes());
				System.out.println(System.getProperty("java.io.tmpdir"));
				FileCopyUtils.copy(
						mpf.getBytes(),
						new FileOutputStream(System
								.getProperty("java.io.tmpdir")
								+ mpf.getOriginalFilename()));
				System.out.println("File uploaded and saved in"
						+ System.getProperty("java.io.tmpdir"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			files.add(fileMeta);
		}
		return files;
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
