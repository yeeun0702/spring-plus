package org.example.expert.domain.s3.controller;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.s3.service.S3Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final S3Service s3Service;

    /**
     * 프로필 이미지 업로드 API
     * - 사용자로부터 파일과 userId를 form-data 형식으로 전달받아 S3에 저장
     * - 저장된 이미지의 URL을 응답으로 반환
     *
     * @param file 업로드할 이미지 파일 (.jpg/.png)
     * @param userId 해당 이미지를 업로드한 사용자 ID
     * @return 업로드된 이미지의 URL (S3 공개 주소)
     */
    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam MultipartFile file,
                                         @RequestParam String userId) {
        String imageUrl = s3Service.uploadProfileImage(file, userId);
        return ResponseEntity.ok(imageUrl);
    }


    /**
     * 프로필 이미지 삭제 API
     * - 클라이언트가 전달한 이미지 URL을 바탕으로 S3에서 해당 이미지 삭제
     *
     * @param fileUrl 삭제할 이미지의 전체 URL
     * @return HTTP 200 OK
     */
    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete(@RequestParam String fileUrl) {
        s3Service.deleteProfileImage(fileUrl);
        return ResponseEntity.ok().build();
    }
}

