package com.beshton.shop.payload;

public class UploadFileResponse {
    private String fileName;
    private String fileDownloadUri;
    private String fileDeleteUri;
    private String fileType;
    private long size;

    public UploadFileResponse(String fileName, String fileDownloadUri, String fileDeleteUri, String fileType, long size) {
        this.fileName = fileName;
        this.fileDownloadUri = fileDownloadUri;
        this.fileDeleteUri = fileDeleteUri;
        this.fileType = fileType;
        this.size = size;
    }

    public String getFileName() {
        return this.fileName;
    }
    public String getFileDownloadUri() {
        return this.fileDownloadUri;
    }
    public String getFileDeleteUri() {
        return this.fileDeleteUri;
    }
    public String getFileType() {
        return this.fileType;
    }
    public long getSize() {
        return this.size;
    }

}
