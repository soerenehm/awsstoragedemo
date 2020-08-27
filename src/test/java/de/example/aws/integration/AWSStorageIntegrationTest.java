package de.example.aws.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AWSStorageIntegrationTest {

    @Value("${aws.s3.bucket}")
    String bucketName;
    @Value("${aws.s3.endpointUrl}")
    String endpointUrl;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void shouldUploadFile() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new org.springframework.core.io.ClassPathResource("/projects.json"));
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange("http://localhost:" + port + "/s3/uploadFile", HttpMethod.POST, requestEntity, String.class);

        assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void shouldDeleteFile() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        String url = endpointUrl + "/" + bucketName + "/" + "projects.json";
        headers.add("url", url);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        ResponseEntity responseEntity = restTemplate.exchange("http://localhost:" + port + "/s3/deleteFile", HttpMethod.DELETE, requestEntity, String.class);

        assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(responseEntity.getBody(), is("Successfully deleted"));
    }

    @Test
    public void shouldGetFile() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        String url = endpointUrl + "/" + bucketName + "/" + "projects.json";
        headers.add("url", url);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<byte[]> responseEntity = restTemplate.exchange("http://localhost:" + port + "/s3/getFile", HttpMethod.GET, requestEntity, byte[].class);

        assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));

        File file = File.createTempFile("download", "tmp");
        StreamUtils.copy(responseEntity.getBody(), new FileOutputStream(file));
        String content = Files.readString(Path.of(file.getPath()));
        assertThat(content, containsString("Online-Versandhandel"));
    }
}
