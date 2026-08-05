package com.abdelrahman027.sbecom2.service;

import com.abdelrahman027.sbecom2.model.Product;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {

    String uploadImage(String path, MultipartFile image) throws IOException;
    void deleteOldImage(Product productFromDb, String path) throws IOException;
}
