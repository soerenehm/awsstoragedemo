package de.example.aws.repository;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface StorageRepository {
    String uploadFileToBucket(final String fileName, final File file);

    String deleteFileFromBucket(String fileName);

    InputStream getFileFromBucket(String fileName) throws IOException;
}
