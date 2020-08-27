package de.example.aws.repository;

import org.springframework.http.MediaType;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.InputStream;

public class S3Repository implements StorageRepository {

    private S3Client s3Client;
    private String bucketName;
    private String endpointUrl;

    public S3Repository(S3Client s3Client, String bucketName, String endpointUrl) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.endpointUrl = endpointUrl;
    }

    @Override
    public String uploadFileToBucket(final String fileName, final File file) {
        final PutObjectRequest putObjectRequest = PutObjectRequest.builder().
                bucket(bucketName).
                contentEncoding(MediaType.APPLICATION_OCTET_STREAM_VALUE).
                key(fileName).
                build();
        final RequestBody requestBody = RequestBody.fromFile(file);
        s3Client.putObject(putObjectRequest, requestBody);

        return endpointUrl + "/" + bucketName + "/" + fileName;
    }

    @Override
    public String deleteFileFromBucket(String fileName) {
        final DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder().
                bucket(bucketName).
                key(fileName).
                build();
        // If you delete an object that does not exist, Amazon S3 will return a success (not an error message).
        s3Client.deleteObject(deleteObjectRequest);
        return "Successfully deleted";
    }

    @Override
    public InputStream getFileFromBucket(String fileName) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder().
                bucket(bucketName).
                key(fileName).
                build();
        final ResponseTransformer<GetObjectResponse, ResponseInputStream<GetObjectResponse>> responseTransformer = ResponseTransformer.toInputStream();
        return s3Client.getObject(getObjectRequest, responseTransformer);
    }
}
