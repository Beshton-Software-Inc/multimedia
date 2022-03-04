package com.beshton.shop.controllers;

import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.util.IOUtils;
import com.beshton.shop.payload.UploadFileResponse;
import com.beshton.shop.services.impl.AWSS3Service;
import com.beshton.shop.services.impl.ImageUploadServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AWSS3FileController.class)
public class AWSS3FileControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private AWSS3Service awss3Service;

    UploadFileResponse response1;
    UploadFileResponse response2;
    File file1;
    File file2;
    File file3;
    String file4;
    File file5;

    @BeforeEach
    private void setup() throws Exception {
        //System.setProperty("user.dir", "C:\\Users\\cw131\\IdeaProjects\\multimedia-main\\src\\test\\java\\com\\beshton\\shop\\controllers\\data");
        response1 = new UploadFileResponse("2222.jpg", "http://localhost:8086/downloadFile/2222.jpg",
                "http://localhost:8086/deleteImage/2222.jpg", "image/jpeg", 351219);
        response2 = new UploadFileResponse("f93968013d344812a4bc485a025650a2-0001.jpg", "http://localhost:8086/downloadFile/f93968013d344812a4bc485a025650a2-0001.jpg",
                "http://localhost:8086/deleteImage/f93968013d344812a4bc485a025650a2-0001.jpg", "image/jpeg", 367865);

        ClassLoader classLoader = getClass().getClassLoader();
        file3 = new File(classLoader.getResource("1111.jpg").getFile());
        file1 = new File(classLoader.getResource("2222.jpg").getFile());
        file2 = new File(classLoader.getResource("f93968013d344812a4bc485a025650a2-0001.jpg").getFile());
        file4 = "2222123.jpg";
        file5 = new File(classLoader.getResource("cdf-clinic-raw.csv").getFile());
    }

    @Test
    public void getAllUserImages_withTwoEntities_success() throws Exception{
        Date lastModified = new Date();
        ObjectListing objectListing = new ObjectListing();
        S3ObjectSummary objectSummary1 = new S3ObjectSummary();
        objectSummary1.setBucketName("test-bucket");
        objectSummary1.setKey("1111.jpg");
        objectSummary1.setLastModified(lastModified);
        objectListing.getObjectSummaries().add(objectSummary1);

        S3ObjectSummary objectSummary2 = new S3ObjectSummary();
        objectSummary2.setBucketName("test-bucket");
        objectSummary2.setKey("2222.jpg");
        objectSummary2.setLastModified(lastModified);
        objectListing.getObjectSummaries().add(objectSummary2);

        List<S3ObjectSummary> s3Objects = new ArrayList<>();
        s3Objects.add(objectSummary1);
        s3Objects.add(objectSummary2);

        Mockito.when(awss3Service.loadAllFiles()).thenReturn(s3Objects);
        Mockito.when(awss3Service.getContentType(objectSummary1.getKey())).thenReturn("image/jpeg");
        Mockito.when(awss3Service.getContentType(objectSummary2.getKey())).thenReturn("image/jpeg");

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                "/userS3Images").accept(
                MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",hasSize(2)))
                .andExpect(jsonPath("$[0].fileName",is("1111.jpg")))
                .andExpect(jsonPath("$[0].fileType",is("image/jpeg")))
                .andExpect(jsonPath("$[0].fileDownloadUri",is("http://localhost/download-from-s3/1111.jpg")))
                .andExpect(jsonPath("$[0].fileDeleteUri",is("http://localhost/delete-from-s3/1111.jpg")))
                .andExpect(jsonPath("$[1].fileName",is("2222.jpg")))
                .andExpect(jsonPath("$[1].fileType",is("image/jpeg")))
                .andExpect(jsonPath("$[1].fileDownloadUri",is("http://localhost/download-from-s3/2222.jpg")))
                .andExpect(jsonPath("$[1].fileDeleteUri",is("http://localhost/delete-from-s3/2222.jpg")))
                .andReturn();
        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    public void downloadFile_withExistFile_success() throws Exception{
        /*Path filePath = Paths.get(file1.getAbsolutePath());
        Resource resource = new UrlResource(filePath.toUri());
        S3Object s3Object = mock(S3Object.class);
        byte[] byteArray = Files.readAllBytes(filePath);
        ObjectMetadata metadataCopy = new ObjectMetadata();
        metadataCopy.setContentType("image/jpeg");
        metadataCopy.setContentLength(file1.length());
        s3Object.setKey("2222.jpg");
        s3Object.setObjectMetadata(metadataCopy);
        s3Object.setBucketName("test-bucket");
        System.out.println(s3Object.getObjectMetadata().getContentType());

        Mockito.when(awss3Service.loadFileAsS3Object(s3Object.getKey())).thenReturn(s3Object);
        Mockito.when(awss3Service.getFile(file1.getName())).thenReturn(byteArray);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                "/download-from-s3/2222.jpg").accept(
                MediaType.APPLICATION_JSON);;
        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();

        /*assertEquals("image/jpeg", result.getResponse().getHeader("Content-Type"));
        assertEquals("attachment; filename=\"2222.jpg\"", result.getResponse().getHeader("Content-Disposition"));
        assertEquals("351219", result.getResponse().getHeader("Content-Length").toString());*/
        //System.out.println(result.getResponse().getHeaderNames());
    }
}
