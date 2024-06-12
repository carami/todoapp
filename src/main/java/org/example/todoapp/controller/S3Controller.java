package org.example.todoapp.controller;

import lombok.RequiredArgsConstructor;
import org.example.todoapp.entity.FileEntity;
import org.example.todoapp.service.FileDatabaseService;
import org.example.todoapp.service.S3Service;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.awt.*;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/s3")
public class S3Controller {
    private final S3Service s3Service;
    private final FileDatabaseService fileDatabaseService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String uuid = UUID.randomUUID().toString();
            String datePath = LocalDate.now().toString().replace("-", "/");
            String key = datePath + "/" + uuid;

            s3Service.uploadFile(file, key);
            fileDatabaseService.saveFileMetadata(uuid, key, file.getOriginalFilename(), file.getSize(), file.getContentType());

            return ResponseEntity.ok("File uploaded successfully: " + file.getOriginalFilename());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to upload file: " + e.getMessage());
        }
    }

    @GetMapping("/download/{uuid}")
    public ResponseEntity<StreamingResponseBody> downloadFile(@PathVariable("uuid") String uuid) {
        try {
            Optional<FileEntity> fileEntityOptional = fileDatabaseService.getFileMetadata(uuid);
            if (!fileEntityOptional.isPresent()) {
                return ResponseEntity.status(404).body(null);
            }

            FileEntity fileEntity = fileEntityOptional.get();
            InputStream inputStream = s3Service.downloadFile(fileEntity.getPath());

            StreamingResponseBody responseBody = outputStream -> {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                inputStream.close();
            };

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileEntity.getOriginalFilename() + "\"")
                    .body(responseBody);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }


//    @PostMapping("/upload")
//    public ResponseEntity<String> uploadFile(@RequestParam("file")MultipartFile file){
//        try{
//            s3Service.uploadFile(file);
//
//            return ResponseEntity.ok("파일업로드 성공^^" + file.getOriginalFilename());
//
//        }catch (Exception e){
//            return ResponseEntity.status(500).body("파일업로드 실패ㅠㅠ"+e.getMessage());
//        }
//    }
//
//    @GetMapping("/download/{key}")
//    public ResponseEntity<StreamingResponseBody> downloadFile(@PathVariable("key") String key){
//        try{
//            InputStream inputStream = s3Service.downloadFile(key);
//            StreamingResponseBody responseBody = outputStream -> {
//                byte[] buffer = new byte[1024];
//                int bytesRead;
//                while((bytesRead = inputStream.read(buffer)) != -1){
//                    outputStream.write(buffer,0,bytesRead);
//                }
//                inputStream.close();
//            };
//            return  ResponseEntity.ok()
//                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+key+"\"")
//                    .body(responseBody);
//
//        }catch (Exception e){
//            return ResponseEntity.status(500).body(null);
//        }
//    }
}
