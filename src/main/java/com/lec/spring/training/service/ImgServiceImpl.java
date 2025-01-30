package com.lec.spring.training.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;



@Service
public class ImgServiceImpl implements ImgService {

    @Override
    public List<String> saveImages(List<MultipartFile> files, String dir) throws IOException {
      return files.stream()
              .filter(file -> !file.isEmpty())
              .map(file -> {
                  try{
                      return saveImage(file, dir);
                  }catch (IOException e){
                      e.printStackTrace();
                      throw new RuntimeException("이미지 저장 오류 발생 " + file.getOriginalFilename(), e);
                  }
              })
              .collect(Collectors.toList());
    }

    @Override
    public String saveImage(MultipartFile file, String dir) throws IOException {
     if(file.isEmpty() || file.getOriginalFilename() == null || file.getOriginalFilename().trim().isEmpty()) {
         throw new IllegalArgumentException("파일이 비어 있거나 잘못된 파일입니다.");
     }
     // 확장자
        String originalFilename = file.getOriginalFilename();
        String ext = originalFilename.substring(originalFilename.lastIndexOf(".")+1).toLowerCase();
        if(!isValidaition(ext)){
            throw new IOException("허용되지 않은 파일 : " + ext);
        }

        //랜덤 파일
        String fileName = UUID.randomUUID().toString() + "." + originalFilename;
        Path path = Paths.get(dir, fileName);

        //디렉토리 생성
        Files.createDirectories(path.getParent());

        //파일 저장
        Files.write(path, file.getBytes());

        return path.toString();
    }

    private boolean isValidaition(String ext) {
        List<String> allowedExtensions = List.of("jpg", "jpeg", "png", "gif", "bmp", "webp");
        return allowedExtensions.contains(ext);
    }

    @Override
    public byte[] loadImage(String filename, String dir) throws IOException {
        Path path = Paths.get(dir, filename);
        if (!Files.exists(path)) {
            throw new IOException("파일을 찾을 수 없습니다: " + filename);
        }
        return Files.readAllBytes(path);
    }

    @Override
    public void deleteImage(String filename, String dir) throws IOException {
        Path path = Paths.get(dir, filename);
        if (Files.exists(path)) {
            Files.delete(path);
        } else {
            throw new IOException("삭제할 파일을 찾을 수 없습니다: " + filename);
        }
    }

}// end TrainerDetailService

