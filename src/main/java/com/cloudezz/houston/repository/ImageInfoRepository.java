package com.cloudezz.houston.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cloudezz.houston.domain.ImageInfo;

/**
 * Spring Data JPA repository for the ImageInfo entity.
 */
public interface ImageInfoRepository extends JpaRepository<ImageInfo, String> {

}
