package com.cloudezz.houston.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.cloudezz.houston.domain.ImageInfo;

/**
 * Spring Data JPA repository for the ImageInfo entity.
 */
public interface ImageInfoRepository extends JpaRepository<ImageInfo, String> {

  
  @Query
  ImageInfo findByImageName(String imageName);
}
