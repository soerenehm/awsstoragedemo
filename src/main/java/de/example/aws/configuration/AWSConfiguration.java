package de.example.aws.configuration;

import de.example.aws.repository.LocalRepository;
import de.example.aws.repository.S3Repository;
import de.example.aws.repository.StorageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Slf4j
@Configuration
public class AWSConfiguration {

    @Bean
    public S3Client s3Client(
            @Value("${aws.s3.access_key_id}") String accessKeyId,
            @Value("${aws.s3.secret_access_key}") String secretAccessKey,
            @Value("${aws.s3.region}") String region) {
        final AwsCredentialsProvider awsCredentialsProvider = () -> new AwsCredentials() {
            @Override
            public String accessKeyId() {
                return accessKeyId;
            }

            @Override
            public String secretAccessKey() {
                return secretAccessKey;
            }
        };
        return S3Client.
                builder().
                region(Region.of(region)).
                credentialsProvider(awsCredentialsProvider).
                build();
    }

    @Bean
    @ConditionalOnProperty(name = "aws.s3.enabled", havingValue = "true", matchIfMissing = true)
    public StorageRepository storageRepository(
            S3Client s3Client,
            @Value("${aws.s3.bucket}") String bucketName,
            @Value("${aws.s3.endpointUrl}") String endpointUrl) {
        // Lombok Builder doesn`t work
        log.info("S3Repository is used");
        return new S3Repository(s3Client, bucketName, endpointUrl);
    }

    @Bean
    @ConditionalOnProperty(name = "aws.s3.enabled", havingValue = "false")
    public StorageRepository localRepository() {
        log.info("LocalRepository is used");
        return new LocalRepository();
    }
}
