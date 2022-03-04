package com.beshton.shop.controllers;

import com.beshton.shop.exceptions.FileStorageException;
import com.beshton.shop.exceptions.MyFileNotFoundException;
import com.beshton.shop.payload.UploadFileResponse;
import com.beshton.shop.services.impl.ImageUploadServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ImageUploadController {
    private static final Logger logger = LoggerFactory.getLogger(ImageUploadController.class);

    @Autowired
    private ImageUploadServiceImpl fileStorageService;

    @PostMapping("/uploadFile")
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file) {
        String fileName = fileStorageService.store(file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile/")
                .path(fileName)
                .toUriString();

        String fileDeleteUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/deleteImage/")
                .path(fileName)
                .toUriString();

        return new UploadFileResponse(fileName, fileDownloadUri, fileDeleteUri,
                file.getContentType(), file.getSize());
    }

    @PostMapping("/uploadMultipleFiles")
    public List<UploadFileResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        return Arrays.asList(files)
                .stream()
                .map(file -> uploadFile(file))
                .collect(Collectors.toList());
    }

    @DeleteMapping("/deleteImage/{fileName:.+}")
    public String deleteImage(@PathVariable String fileName, HttpServletRequest request) {
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if(!contentType.startsWith("image/")) {
            throw new FileStorageException("Sorry!" + fileName +" It is not an image!");
        }

        fileStorageService.delete(fileName);
        String ret = fileName + " Deletion successful.";
        return ret;
    }

    @GetMapping("/downloadFile/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
            throw new FileStorageException("Could not download file, something wrong");
        }

        // Fallback to the default content type if type could not be determined

        if(contentType == null) {
            contentType = "application/octet-stream";
        }


        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("/userImages")
    public List<UploadFileResponse> displayAll() throws IOException {
        // Load file as Resource
            File[] files = fileStorageService.loadAllFiles();
            List<UploadFileResponse> listOfRes= new ArrayList<UploadFileResponse>(files.length);
            for (File str:files){
                String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/downloadFile/")
                        .path(str.getName())
                        .toUriString();

                String fileDeleteUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/deleteImage/")
                        .path(str.getName())
                        .toUriString();

                Path path = str.toPath();
                String mimeType = Files.probeContentType(path);
                if (mimeType.startsWith("image/")) {
                    UploadFileResponse newResponse = new UploadFileResponse(str.getName(), fileDownloadUri, fileDeleteUri,
                            mimeType, str.length());
                    listOfRes.add(newResponse);
                }

            }

            return listOfRes;
    }

}
