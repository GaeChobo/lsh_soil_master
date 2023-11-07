package kr.movements.smv2.service;

import kr.movements.smv2.entity.FileEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


public interface FileService {
    FileEntity saveFile(MultipartFile mFile, String s3DirPath);
    FileEntity updateFile(MultipartFile mFile, Long fileId);

    void imgFileCheck(MultipartFile imgFile);
    FileEntity saveImg(MultipartFile image, String s3DirPath);
    FileEntity saveImgWithThumbnail(MultipartFile image, String s3DirPath);
    FileEntity updateImg(MultipartFile image, Long fileId);
    FileEntity updateImgWithThumbnail(MultipartFile image, Long fileId, String oldStoredName);
    void deleteImg(Long fileId);
    byte[] getThumbnail(Long imgFileId) throws IOException;
    byte[] getImage(Long imgFileId) throws IOException;

}