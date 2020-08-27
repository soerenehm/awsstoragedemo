package de.example.aws.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class LocalRepository implements StorageRepository {

    @Value("classpath:projects.json")
    Resource fileResource;

    @Override
    public String uploadFileToBucket(String fileName, File file) {
        return null;
    }

    @Override
    public String deleteFileFromBucket(String fileName) {
        return null;
    }

    @Override
    public InputStream getFileFromBucket(String fileName) throws IOException {
        return fileResource.getInputStream();
    }
}
