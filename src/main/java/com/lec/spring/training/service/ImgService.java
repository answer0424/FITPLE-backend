package com.lec.spring.training.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ImgService {

    // 여러이미지 파일 저장 ->  List
    List<String> saveImages(List<MultipartFile> files, String dir) throws IOException;
    // 단일 이미지 저장 -> MulitpartFile (필요한가?)
    String saveImage(MultipartFile file, String dir) throws IOException;
    // img 불러오기
    byte[] loadImage(String filename, String dir) throws IOException;
    // 삭제
    void deleteImage(String filename, String dir) throws IOException;

}
