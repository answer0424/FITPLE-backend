package com.lec.spring.training.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class ImgServiceImpl implements ImgService {
    private static final Logger logger = Logger.getLogger(ImgServiceImpl.class.getName());
    private static final String BASE_UPLOAD_DIR = "./uploads/certification";

    @Override
    public List<String> saveImages(List<MultipartFile> files, String dir) throws IOException {
        List<String> savedPaths = new ArrayList<>();

        if (files == null || files.isEmpty()) {
            logger.warning("No files provided for upload");
            return savedPaths;
        }

        // 실제 저장 경로 생성
        String actualDir = (dir != null && !dir.trim().isEmpty()) ? dir : BASE_UPLOAD_DIR;
        Path uploadPath = Paths.get(actualDir).toAbsolutePath().normalize();

        // 디렉토리 생성
        Files.createDirectories(uploadPath);
        logger.info("Upload directory created: " + uploadPath);

        for (MultipartFile file : files) {
            if (file != null && !file.isEmpty()) {
                try {
                    String savedPath = saveImage(file, actualDir);
                    savedPaths.add(savedPath);
                    logger.info("File saved successfully: " + savedPath);
                } catch (Exception e) {
                    logger.severe("Error saving file: " + e.getMessage());
                    throw new IOException("파일 저장 중 오류 발생: " + file.getOriginalFilename(), e);
                }
            }
        }

        return savedPaths;
    }

    @Override
    public String saveImage(MultipartFile file, String dir) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("유효하지 않은 파일입니다.");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new IllegalArgumentException("파일 이름이 유효하지 않습니다.");
        }

        // 실제 저장 경로 설정
        String actualDir = (dir != null && !dir.trim().isEmpty()) ? dir : BASE_UPLOAD_DIR;
        Path uploadPath = Paths.get(actualDir).toAbsolutePath().normalize();

        // 확장자 확인
        String ext = getFileExtension(originalFilename);
        if (!isValidExtension(ext)) {
            throw new IOException("허용되지 않은 파일 확장자입니다: " + ext);
        }

        // 고유한 파일명 생성
        String newFilename = UUID.randomUUID().toString() + "." + ext;
        Path targetPath = uploadPath.resolve(newFilename).normalize();

        // 파일 저장
        Files.copy(file.getInputStream(), targetPath);
        logger.info("File saved to: " + targetPath);

        return targetPath.toString();
    }

    private String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    private boolean isValidExtension(String ext) {
        List<String> allowedExtensions = List.of("jpg", "jpeg", "png", "gif", "bmp", "webp");
        return allowedExtensions.contains(ext.toLowerCase());
    }

    @Override
    public byte[] loadImage(String filename, String dir) throws IOException {
        String actualDir = (dir != null && !dir.trim().isEmpty()) ? dir : BASE_UPLOAD_DIR;
        Path filePath = Paths.get(actualDir).resolve(filename).normalize();

        if (!Files.exists(filePath)) {
            throw new IOException("파일을 찾을 수 없습니다: " + filename);
        }

        return Files.readAllBytes(filePath);
    }

    @Override
    public void deleteImage(String filename, String dir) throws IOException {
        String actualDir = (dir != null && !dir.trim().isEmpty()) ? dir : BASE_UPLOAD_DIR;
        Path filePath = Paths.get(actualDir).resolve(filename).normalize();

        if (Files.exists(filePath)) {
            Files.delete(filePath);
            logger.info("File deleted: " + filePath);
        } else {
            throw new IOException("삭제할 파일을 찾을 수 없습니다: " + filename);
        }
    }
}