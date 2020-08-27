package de.example.aws.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class StorageRepositoryTest {
    private static final String BUCKET_NAME = "bucketName";
    private static final String ENDPOINT_URL = "endpointUrl";
    private static final String FILE_NAME = "projects.json";

    @Mock
    private S3Client s3ClientMock;

    private StorageRepository storageRepository;

    @BeforeEach
    void setUp() {
        storageRepository = new S3Repository(s3ClientMock, BUCKET_NAME, ENDPOINT_URL);
    }

    @Test
    void shouldUploadFileToBucket() throws IOException {
        // given
        File file = new File(getClass().getResource("/" + FILE_NAME).getFile());
        // when
        String response = storageRepository.uploadFileToBucket(FILE_NAME, file);
        // then
        final ArgumentCaptor<PutObjectRequest> putObjectRequestArgumentCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3ClientMock).putObject(putObjectRequestArgumentCaptor.capture(), any(RequestBody.class));

        PutObjectRequest putObjectRequest = putObjectRequestArgumentCaptor.getValue();
        assertThat(putObjectRequest.bucket(), is(BUCKET_NAME));
        assertThat(putObjectRequest.key(), is(FILE_NAME));

        assertThat(response, is(ENDPOINT_URL + "/" + BUCKET_NAME + "/" + FILE_NAME));
    }

    @Test
    void shouldDeleteFileFromBucket() throws IOException {
        // when
        String response = storageRepository.deleteFileFromBucket(FILE_NAME);
        // then
        final ArgumentCaptor<DeleteObjectRequest> deleteObjectRequestArgumentCaptor = ArgumentCaptor.forClass(DeleteObjectRequest.class);
        verify(s3ClientMock).deleteObject(deleteObjectRequestArgumentCaptor.capture());

        DeleteObjectRequest deleteObjectRequest = deleteObjectRequestArgumentCaptor.getValue();
        assertThat(deleteObjectRequest.bucket(), is(BUCKET_NAME));
        assertThat(deleteObjectRequest.key(), is(FILE_NAME));

        assertThat(response, not(emptyOrNullString()));
    }

    @Test
    void shouldGetFileFromBucket() throws IOException {
        // when
        storageRepository.getFileFromBucket(FILE_NAME);
        // then
        final ArgumentCaptor<GetObjectRequest> getObjectRequestArgumentCaptor = ArgumentCaptor.forClass(GetObjectRequest.class);
        verify(s3ClientMock).getObject(getObjectRequestArgumentCaptor.capture(), any(ResponseTransformer.class));

        GetObjectRequest getObjectRequest = getObjectRequestArgumentCaptor.getValue();
        assertThat(getObjectRequest.bucket(), is(BUCKET_NAME));
        assertThat(getObjectRequest.key(), is(FILE_NAME));
    }
}
