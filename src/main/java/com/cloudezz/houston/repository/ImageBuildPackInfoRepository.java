package com.cloudezz.houston.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cloudezz.houston.domain.ImageBuildPackInfo;

/**
 * Spring Data JPA repository for the ImageBuildPackInfo entity.
 */
public interface ImageBuildPackInfoRepository extends JpaRepository<ImageBuildPackInfo, String> {

}
