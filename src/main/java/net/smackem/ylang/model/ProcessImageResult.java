package net.smackem.ylang.model;

public interface ProcessImageResult {
    boolean isSuccess();
    String getMessage();
    byte[] getImageDataPng();
}
