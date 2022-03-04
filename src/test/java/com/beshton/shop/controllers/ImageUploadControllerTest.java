package com.beshton.shop.controllers;

import com.beshton.shop.controllers.ImageUploadController;
import com.beshton.shop.payload.UploadFileResponse;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.util.NestedServletException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ImageUploadController.class)
public class ImageUploadControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ImageUploadServiceImpl imageUploadService;

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
        List<UploadFileResponse> responses = new ArrayList<>(Arrays.asList(response1, response2));
        File[] files = {file1,file2};

        Mockito.when(imageUploadService.loadAllFiles()).thenReturn(files);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                "/userImages").accept(
                MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",hasSize(2)))
                .andExpect(jsonPath("$[0].fileName",is("2222.jpg")))
                .andExpect(jsonPath("$[1].fileName",is("f93968013d344812a4bc485a025650a2-0001.jpg")))
                .andReturn();


        String resp = result.getResponse().getContentAsString();
        String s = JsonPath.parse(resp).read("$[1].fileDownloadUri");
        int size = JsonPath.parse(resp).read("$[0].size");
        System.out.println(s);
        System.out.println(size);
    }

    @Test
    public void downloadFile_withExistFile_success() throws Exception{
        Path filePath = Paths.get(file1.getAbsolutePath());
        Resource resource = new UrlResource(filePath.toUri());

        Mockito.when(imageUploadService.loadFileAsResource(file1.getName())).thenReturn(resource);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                "/downloadFile/2222.jpg").accept(
                MediaType.APPLICATION_JSON);;
        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();

        assertEquals("image/jpeg", result.getResponse().getHeader("Content-Type"));
        assertEquals("attachment; filename=\"2222.jpg\"", result.getResponse().getHeader("Content-Disposition"));
        assertEquals("7876", result.getResponse().getHeader("Content-Length").toString());
        System.out.println(result.getResponse().getHeaderNames());

    }

    @Test
    public void downloadFile_withNoExistFile_exceptionOccur() throws Exception{
        /*Path filePath = Paths.get(file4);
        Resource resource = new UrlResource(filePath.toUri());

        Path filePath = Paths.get(file5.getAbsolutePath());
        Resource resource = new UrlResource(filePath.toUri());

        Mockito.when(imageUploadService.loadFileAsResource(file5.getName())).thenThrow(new MyFileNotFoundException("File not found " + file5.getName()));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                "/downloadFile/virufy-cdf-clinic-raw.csv").accept(
                MediaType.APPLICATION_JSON);
        Throwable exception = assertThrows(NestedServletException.class, () -> mockMvc.perform(requestBuilder));
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound())
                .andReturn());
        assertEquals("Key can not be null.", exception.getMessage());*/
    }

    @Test
    public void uploadFile_withOneFile_success() throws Exception{
        Path filePath = Paths.get(file3.getAbsolutePath());
        String name = file3.getName();
        String originalFileName = file3.getName();
        String contentType = Files.probeContentType(filePath);
        byte[] content = null;
        try {
            content = Files.readAllBytes(filePath);
        } catch (final IOException e) {
        }
        MockMultipartFile mFile = new MockMultipartFile("file",
                originalFileName, contentType, content);


        Mockito.when(imageUploadService.store(mFile)).thenReturn(name);

        MvcResult result = mockMvc.perform(
                multipart("/uploadFile")
                .file(mFile)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName",is("1111.jpg")))
                .andExpect(jsonPath("$.fileType",is("image/jpeg")))
                .andExpect(jsonPath("$.fileDownloadUri",is("http://localhost/downloadFile/1111.jpg")))
                .andExpect(jsonPath("$.fileDeleteUri",is("http://localhost/deleteImage/1111.jpg")))
                .andReturn();
        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    public void uploadMultipleFiles_withMultipleFiles_success() throws Exception{
        Path filePath = Paths.get(file3.getAbsolutePath());
        Path filePath1 = Paths.get(file1.getAbsolutePath());
        String name = file3.getName();
        String name1 = file1.getName();
        String originalFileName = file3.getName();
        String originalFileName1 = file1.getName();
        String contentType = Files.probeContentType(filePath);
        String contentType1 = Files.probeContentType(filePath1);
        byte[] content = null;
        try {
            content = Files.readAllBytes(filePath);
        } catch (final IOException e) {
        }
        MockMultipartFile mFile = new MockMultipartFile("files",
                originalFileName, contentType, content);
        byte[] content1 = null;
        try {
            content1 = Files.readAllBytes(filePath1);
        } catch (final IOException e) {
        }
        MockMultipartFile mFile1 = new MockMultipartFile("files",
                originalFileName1, contentType1, content1);

        Mockito.when(imageUploadService.store(mFile)).thenReturn(name);
        Mockito.when(imageUploadService.store(mFile1)).thenReturn(name1);

        MvcResult result = mockMvc.perform(
                        multipart("/uploadMultipleFiles")
                                .file(mFile)
                                .file(mFile1)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",hasSize(2)))
                .andExpect(jsonPath("$[0].fileName",is("1111.jpg")))
                .andExpect(jsonPath("$[0].fileType",is("image/jpeg")))
                .andExpect(jsonPath("$[0].fileDownloadUri",is("http://localhost/downloadFile/1111.jpg")))
                .andExpect(jsonPath("$[0].fileDeleteUri",is("http://localhost/deleteImage/1111.jpg")))
                .andExpect(jsonPath("$[1].fileName",is("2222.jpg")))
                .andExpect(jsonPath("$[1].fileType",is("image/jpeg")))
                .andExpect(jsonPath("$[1].fileDownloadUri",is("http://localhost/downloadFile/2222.jpg")))
                .andExpect(jsonPath("$[1].fileDeleteUri",is("http://localhost/deleteImage/2222.jpg")))
                .andReturn();
        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    public void deleteFile_withExistFile_success() throws Exception{
        Path filePath = Paths.get(file1.getAbsolutePath());
        Resource resource = new UrlResource(filePath.toUri());
        Mockito.when(imageUploadService.loadFileAsResource(file1.getName())).thenReturn(resource);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete(
                "/deleteImage/2222.jpg").accept(
                MediaType.APPLICATION_JSON);;
        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();

        assertEquals( file1.getName() + " Deletion successful.", result.getResponse().getContentAsString());
        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    public void deleteFile_withNoExistFile_ExceptionOccur() throws Exception{
        Path filePath = Paths.get(file5.getAbsolutePath());
        Resource resource = new UrlResource(filePath.toUri());
        Mockito.when(imageUploadService.loadFileAsResource(file5.getName())).thenReturn(resource);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete(
                "/deleteImage/cdf-clinic-raw.csv").accept(
                MediaType.APPLICATION_JSON);;


        Throwable exception = assertThrows(NestedServletException.class, () -> mockMvc.perform(requestBuilder));

        System.out.println(exception.getCause().getMessage());
        assertEquals("Sorry!" + file5.getName() +" It is not an image!", exception.getCause().getMessage());
    }

}
