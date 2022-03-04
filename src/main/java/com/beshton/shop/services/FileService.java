package com.beshton.shop.services;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface FileService {
    String store(MultipartFile file);

    S3Object loadFileAsS3Object(String fileName);

    byte[] getFile(String key);

    void delete(String fileName);

    List<S3ObjectSummary> loadAllFiles();


}
