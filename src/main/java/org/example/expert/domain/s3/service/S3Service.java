package org.example.expert.domain.s3.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadProfileImage(MultipartFile file, String userId) {
        String fileName = "profile/" + userId + "_" + file.getOriginalFilename();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());

        try {
            // ACL 없이 업로드 (버킷 정책에 따라 접근 가능 여부 결정됨)
            PutObjectRequest request = new PutObjectRequest(bucket, fileName, file.getInputStream(), metadata);
            amazonS3.putObject(request);
        } catch (IOException e) {
            throw new RuntimeException("S3 업로드 실패", e);
        }

        return amazonS3.getUrl(bucket, fileName).toString();
    }

    public void deleteProfileImage(String fileUrl) {
        String key = fileUrl.substring(fileUrl.indexOf("profile/")); // key 추출
        amazonS3.deleteObject(bucket, key);
    }
}
