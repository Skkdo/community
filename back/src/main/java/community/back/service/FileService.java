package community.back.service;

import community.back.common.ResponseCode;
import community.back.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileService {
    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;

    @Value("${cloud.aws.s3.path}")
    private String s3Path;

    @Transactional
    public String uploadS3(MultipartFile file) {

        String originalFilename = file.getOriginalFilename();
        String filename = UUID.randomUUID() + "_" + originalFilename;
        String contentType = file.getContentType();

        PutObjectRequest request = PutObjectRequest.builder().bucket(bucketName).key(s3Path + filename).contentType(contentType).contentLength(file.getSize()).build();

        try (InputStream fileInputStream = file.getInputStream()) {
            s3Client.putObject(request, RequestBody.fromInputStream(fileInputStream, file.getSize()));
        } catch (IOException e) {
            throw new BusinessException(ResponseCode.DATABASE_ERROR);
        }
        return getUrl(filename);
    }

    private String getUrl(String fileName) {
        GetUrlRequest request = GetUrlRequest.builder().bucket(this.bucketName).key(s3Path + fileName).build();
        return s3Client.utilities().getUrl(request).toString();
    }
}
