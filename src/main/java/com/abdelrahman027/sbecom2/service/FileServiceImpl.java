    package com.abdelrahman027.sbecom2.service;

    import com.abdelrahman027.sbecom2.dto.ProductDTO;
    import com.abdelrahman027.sbecom2.exception.ResourceNotFoundException;
    import com.abdelrahman027.sbecom2.model.Product;
    import com.abdelrahman027.sbecom2.repository.ProductRepository;
    import lombok.RequiredArgsConstructor;
    import org.modelmapper.ModelMapper;
    import org.springframework.stereotype.Service;
    import org.springframework.web.multipart.MultipartFile;

    import java.io.File;
    import java.io.IOException;
    import java.nio.file.Files;
    import java.nio.file.Path;
    import java.nio.file.Paths;
    import java.util.UUID;

    @Service
    @RequiredArgsConstructor
    public class FileServiceImpl implements FileService{

        @Override
        public String uploadImage(String path, MultipartFile image) throws IOException {
            String originalFileName = image.getOriginalFilename();
            String randomId = UUID.randomUUID().toString();
            String fileName = randomId.concat(originalFileName.substring(originalFileName.lastIndexOf(".")));
            String filePath = path + File.separator + fileName;

            File folder = new File(path);
            if (!folder.exists()) Files.createDirectories(Paths.get(path));


            Files.copy(image.getInputStream(), Paths.get(filePath));

            return fileName;
        }

        @Override
        public void deleteOldImage(Product productFromDb, String path) throws IOException {
            String imageName = productFromDb.getImage();
            if (imageName == null || imageName.isBlank()) return;
            Path oldImagePath = Paths.get(path).resolve(imageName);
            Files.deleteIfExists(oldImagePath);
        }
    }
