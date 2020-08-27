package de.example.aws.service;

import de.example.aws.repository.StorageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@Slf4j
@Service
public class StorageService {

    private final StorageRepository storageRepository;

    public StorageService(StorageRepository storageRepository) {
        this.storageRepository = storageRepository;
    }

    public String uploadFile(final MultipartFile multipartFile) throws IOException {
        log.info("File upload in progress.");
        final File file = convertMultiPartFileToFile(multipartFile);
        String fileName = multipartFile.getOriginalFilename();
        String fileUrl = storageRepository.uploadFileToBucket(fileName, file);
        log.info("File upload is completed.");
        if (!file.delete()) {
            throw new IOException(String.format("File %s deletion not successful.", fileName));
        }
        return fileUrl;
    }

    public String deleteFileFromBucket(String fileUrl) {
        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        return storageRepository.deleteFileFromBucket(fileName);
    }

    public InputStream getFileFromBucket(String fileUrl) throws IOException {
        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        return storageRepository.getFileFromBucket(fileName);
    }

    private File convertMultiPartFileToFile(final MultipartFile multipartFile) throws IOException {
        final File file = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        try (final FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(multipartFile.getBytes());
        }
        return file;
    }
}
