package de.example.aws.controller;

import de.example.aws.service.StorageService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.utils.IoUtils;

@RestController
@RequestMapping("/s3")
public class StorageController {

    private final StorageService service;

    public StorageController(StorageService service) {
        this.service = service;
    }

    @PostMapping(value = "/uploadFile")
    public ResponseEntity<String> uploadFile(@RequestParam(value = "file") final MultipartFile multipartFile) {
        String response;
        try {
            response = this.service.uploadFile(multipartFile);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response = String.format("Error %s uploading file %s", e.getMessage(), multipartFile.getOriginalFilename());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/deleteFile")
    public ResponseEntity<String> deleteFile(@RequestHeader(value = "url") String fileUrl) {
        String response;
        try {
            response = this.service.deleteFileFromBucket(fileUrl);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response = String.format("Error %s deleting file %s", e.getMessage(), fileUrl);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getFile")
    public ResponseEntity<byte[]> getFile(@RequestHeader("url") String fileUrl) {
        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        try {
            final byte[] bytes = IoUtils.toByteArray(this.service.getFileFromBucket(fileUrl));
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(bytes);
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }
}
