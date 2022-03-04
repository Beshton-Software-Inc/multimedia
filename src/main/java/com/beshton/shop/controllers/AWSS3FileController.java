package com.beshton.shop.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.beshton.shop.exceptions.FileStorageException;
import com.beshton.shop.payload.UploadFileResponse;
import com.beshton.shop.services.impl.AWSS3Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;

@RestController
public class AWSS3FileController {
    private static final Logger logger = LoggerFactory.getLogger(AWSS3FileController.class);

    @Autowired
    private AWSS3Service awsS3Service;

    @PostMapping("/uploadFile-to-s3")
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file) {
        String fileName = awsS3Service.store(file);
        //Map<String, String> response = new HashMap<>();
        //response.put("publicURL", publicURL);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/download-from-s3/")
                .path(fileName)
                .toUriString();

        String fileDeleteUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/delete-from-s3/")
                .path(fileName)
                .toUriString();

        return new UploadFileResponse(fileName, fileDownloadUri, fileDeleteUri,
                file.getContentType(), file.getSize());
    }

    @PostMapping("/uploadMultipleFiles-to-s3")
    public List<UploadFileResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        return Arrays.asList(files)
                .stream()
                .map(file -> uploadFile(file))
                .collect(Collectors.toList());
    }

    @DeleteMapping("/delete-from-s3/{fileName:.+}")
    public String deleteImage(@PathVariable String fileName, HttpServletRequest request) {
        S3Object object = awsS3Service.loadFileAsS3Object(fileName);
        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = object.getObjectMetadata().getContentType();
        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        }

        // Fallback to the default content type if type could not be determined
        if(!contentType.startsWith("image/")) {
            throw new FileStorageException("Sorry!" + fileName +" It is not an image!");
        }

        awsS3Service.delete(fileName);
        String ret = fileName + " Deletion successful.";
        return ret;
    }

    @GetMapping("/userS3Images")
    public List<UploadFileResponse> displayAll() throws IOException {
        // Load file as Resource
        List<S3ObjectSummary> s3Objects = awsS3Service.loadAllFiles();
        List<UploadFileResponse> listOfRes= new ArrayList<UploadFileResponse>(s3Objects.size());
        for (S3ObjectSummary str:s3Objects){
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/download-from-s3/")
                    .path(str.getKey())
                    .toUriString();

            String fileDeleteUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/delete-from-s3/")
                    .path(str.getKey())
                    .toUriString();

            String mimeType = awsS3Service.getContentType(str.getKey());
            if (mimeType.startsWith("image/")) {
                UploadFileResponse newResponse = new UploadFileResponse(str.getKey(), fileDownloadUri, fileDeleteUri,
                        mimeType, str.getSize());
                listOfRes.add(newResponse);
            }
        }
        return listOfRes;
    }

    @GetMapping("/download-from-s3/{fileName:.+}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        // Load file as Resource
        S3Object object = awsS3Service.loadFileAsS3Object(fileName);

        byte[] data = awsS3Service.getFile(fileName);
        ByteArrayResource resource = new ByteArrayResource(data);
        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = object.getObjectMetadata().getContentType();
        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        }

        // Fallback to the default content type if type could not be determined
        if(!contentType.startsWith("image/")) {
            throw new FileStorageException("Sorry!" + fileName +" It is not an image!");
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + object.getKey() + "\"")
                .body(resource);
    }
}