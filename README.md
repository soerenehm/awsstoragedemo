# AWS Demo Project for AWS S3 with Spring Boot

A rest controller offers endpoints to upload, get and delete a file for an already existing AWS S3 bucket.

## - Prerequisites

- Existing AWS S3 bucket in your AWS account.

- Change XXX parameter values in [application.yml](src/main/resources/application.yml) 

## - Following dependencies are used: 

### Application
- AWS SDK For Java
- Web
- Lombok

### Swagger
- Swagger API [http://localhost:8080/v3/api-docs/](http://localhost:8080/v3/api-docs/)

## - REST API Details

For correct usage of REST endpoints see [AWSStorageIntegrationTest](src/test/java/de/example/aws/integration/AWSStorageIntegrationTest.java)

### Upload a file

File has to be attached as form-data in body.

- http://localhost:8080/s3/uploadFile

### Get a file

Set header attribute `url` with `aws.s3.endpointUrl/aws.s3.bucket/filename` 

- http://localhost:8080/s3/getFile

### Delete a file

Set header attribute `url` with `aws.s3.endpointUrl/aws.s3.bucket/filename`   

- http://localhost:8080/s3/deleteFile 

           

