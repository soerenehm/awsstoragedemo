package de.example.aws.service;

import de.example.aws.repository.StorageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StorageServiceTest {
    private static final String FILE_NAME = "projects.json";
    private static final String FILE_URL = "endpointUrl/bucketName/projects.json";

    @Mock
    private StorageRepository storageRepositoryMock;

    private StorageService storageService;

    @BeforeEach
    void setUp() {
        storageService = new StorageService(storageRepositoryMock);
    }

    @Test
    void shouldUploadFile() throws IOException {
        // given
        File file = new File(FILE_NAME);
        file.createNewFile();
        MultipartFile multipartFile = new MockMultipartFile(FILE_NAME, FILE_NAME, MediaType.MULTIPART_FORM_DATA_VALUE,
                getClass().getResourceAsStream(FILE_NAME));
        // when
        when(storageRepositoryMock.uploadFileToBucket(FILE_NAME, file)).thenReturn(FILE_URL);
        String fileUrl = storageService.uploadFile(multipartFile);
        // then
        assertThat(fileUrl, is(FILE_URL));
        verify(storageRepositoryMock).uploadFileToBucket(FILE_NAME, file);
    }

    @Test
    void shuldDeleteFileFromBucket() {
        // when
        storageService.deleteFileFromBucket(FILE_URL);
        // then
        verify(storageRepositoryMock).deleteFileFromBucket(FILE_NAME);
    }

    @Test
    void shouldGetFileFromBucket() throws IOException {
        // when
        when(storageRepositoryMock.getFileFromBucket(FILE_NAME)).thenReturn(
                getClass().getResourceAsStream("/" + FILE_NAME));
        storageService.getFileFromBucket(FILE_URL);
        // then
        verify(storageRepositoryMock).getFileFromBucket(FILE_NAME);
    }
}
