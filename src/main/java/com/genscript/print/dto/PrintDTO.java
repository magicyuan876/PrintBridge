package com.genscript.print.dto;

public class PrintDTO {

    private String fileUrl;

    private String fileName;

    private boolean landscape = false;

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isLandscape() {
        return landscape;
    }

    public void setLandscape(boolean landscape) {
        this.landscape = landscape;
    }

    @Override
    public String toString() {
        return fileName;
    }
}
