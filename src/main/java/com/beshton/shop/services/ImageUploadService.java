package com.beshton.shop.services;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;


public interface ImageUploadService {

    String store(MultipartFile file);

    Resource loadFileAsResource(String fileName);

}
