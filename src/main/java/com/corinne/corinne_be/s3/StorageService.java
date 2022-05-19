package com.corinne.corinne_be.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.corinne.corinne_be.dto.user_dto.ProfileResponseDto;
import com.corinne.corinne_be.model.User;
import com.corinne.corinne_be.repository.UserRepository;
import com.corinne.corinne_be.security.UserDetailsImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class StorageService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Autowired
    private AmazonS3 s3Client;

    private final UserRepository userRepository;

    //이미지수정
    @Transactional
    public ProfileResponseDto registImage(MultipartFile file, UserDetailsImpl userDetails) throws IOException {
        Long userId = userDetails.getUser().getUserId();
        User user = userRepository.findById(userId).orElseThrow(IllegalArgumentException::new);
        String imgurl = uploadFile(file);
        user.profileImgUpdate(imgurl);
        return new ProfileResponseDto(imgurl);
    }

    // 게시글 등록(이미지 첨부)
    public String uploadFile(MultipartFile file) {

        // 이미지 업로드
       String url = "";
        try{
            File fileObj = convertMultiPartFileToFile(file);
            String forExtensionName = file.getOriginalFilename();
            String fileName = System.currentTimeMillis() + "_" + UUID.randomUUID() + "." +forExtensionName.substring(forExtensionName.lastIndexOf(".") + 1);
            s3Client.putObject(new PutObjectRequest(bucketName, fileName, fileObj));
            url = s3Client.getUrl(bucketName,fileName).toString();
            System.out.println(url);
            fileObj.delete();
        } catch (Exception e){
        }

        return url;
    }

    // 이미지 변환(업로드)
    private File convertMultiPartFileToFile(MultipartFile file) {
        File convertedFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            log.error("Error converting multipartFile to file", e);
        }
        return convertedFile;
    }


}
