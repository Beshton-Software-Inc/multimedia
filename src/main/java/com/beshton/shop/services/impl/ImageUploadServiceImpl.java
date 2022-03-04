package com.beshton.shop.services.impl;

import com.beshton.shop.exceptions.FileStorageException;
import com.beshton.shop.exceptions.MyFileNotFoundException;
import com.beshton.shop.services.ImageUploadService;
import com.beshton.shop.property.FileStorageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.List;


@Service
public class ImageUploadServiceImpl{
    private final Path fileStorageLocation;

    @Autowired
    public ImageUploadServiceImpl(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public String store(MultipartFile file) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            // Check if the file's name contains invalid characters
            String fileType= file.getContentType();
            if (!fileType.startsWith("image/")) {
                throw new FileStorageException("Sorry!" + fileName +" It is not an image!");
            }

            if(fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if(resource != null && resource.exists()) {
                return resource;
            } else {
                throw new MyFileNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new MyFileNotFoundException("File not found " + fileName, ex);
        }
    }

    public File[] loadAllFiles() {
            String filePath = this.fileStorageLocation.toString();
            // creates a file object
            File files = new File(filePath);

            // returns an array of all files
            File[] fileList = files.listFiles();
            for(File file:fileList){
                System.out.println(file.getName());
            }
            return fileList;
    }

    public void delete(String fileName) {
        Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
        try {
            Files.deleteIfExists(filePath);
        }
        catch (NoSuchFileException e) {
            throw new MyFileNotFoundException("No such file/directory exists");
        }
        catch (DirectoryNotEmptyException e) {
            throw new FileStorageException("Directory is not empty.");
        }
        catch (IOException e) {
            System.out.println("Invalid permissions.");
        }
    }

}
