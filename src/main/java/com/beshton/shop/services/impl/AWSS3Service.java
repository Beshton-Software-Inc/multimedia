package com.beshton.shop.services.impl;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.beshton.shop.exceptions.FileStorageException;
import com.beshton.shop.exceptions.MyFileNotFoundException;
import com.beshton.shop.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.amazonaws.services.s3.AmazonS3Client;

@Service
public class AWSS3Service implements FileService {
    @Autowired
    private AmazonS3Client awsS3Client;

    private String s3BucketName = "myfirstmybucket";
    @Override
    public String store(MultipartFile file) {

        //String filenameExtension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        //String key = UUID.randomUUID().toString() + "." +filenameExtension;
        String fileType= file.getContentType();
        try {
            if (!fileType.startsWith("image/")) {
                throw new FileStorageException("Sorry!" + fileName +" It is not an image!");
            }

            if(fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }
            ObjectMetadata metaData = new ObjectMetadata();
            metaData.setContentLength(file.getSize());
            metaData.setContentType(fileType);
            awsS3Client.putObject(this.s3BucketName, fileName, file.getInputStream(), metaData);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An exception occured while uploading the file");
        }

        awsS3Client.setObjectAcl(this.s3BucketName, fileName, CannedAccessControlList.PublicRead);

        return fileName;
    }

    @Override
    public S3Object loadFileAsS3Object(String fileName) {
        S3Object s3Obj = null;
        try {
            s3Obj = awsS3Client.getObject(this.s3BucketName, fileName);

        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
            throw new AmazonServiceException("AmazonServiceException");
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
            throw new SdkClientException("SdkClientException");
        }
        return s3Obj;
    }

    @Override
    public byte[] getFile(String key) {
        S3Object obj = awsS3Client.getObject(s3BucketName, key);
        S3ObjectInputStream stream = obj.getObjectContent();
        try {
            byte[] content = IOUtils.toByteArray(stream);
            obj.close();
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void delete(String fileName) {
        try {
            /*ObjectListing listing = awsS3Client.listObjects(s3BucketName);
            List<String> list = listing.getCommonPrefixes();
            List<S3ObjectSummary> summaries = listing.getObjectSummaries();
            for (String str:list){
                System.out.println(str);
            }

            for(S3ObjectSummary sum: summaries){
                System.out.println(sum.getKey());
                System.out.println(sum.getBucketName());
                if (sum.getKey() == fileName) {
                    awsS3Client.deleteObject(this.s3BucketName,fileName);
                    return;
                }
            }*/
            awsS3Client.deleteObject(this.s3BucketName,fileName);
        }
        catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
            throw new AmazonServiceException("AmazonServiceException");
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
            throw new AmazonServiceException("SdkClientException");
        }
    }

    @Override
    public List<S3ObjectSummary> loadAllFiles() {

        ObjectListing listing = awsS3Client.listObjects(s3BucketName);
        List<S3ObjectSummary> summaries = listing.getObjectSummaries();

        return summaries;
    }

    public String getContentType(String key){
        ObjectMetadata objectMetadata = awsS3Client.getObjectMetadata(s3BucketName,key);
        return objectMetadata.getContentType();
    }
}