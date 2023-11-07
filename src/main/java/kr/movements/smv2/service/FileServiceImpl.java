package kr.movements.smv2.service;

import kr.movements.smv2.common.MainProperty;
import kr.movements.smv2.common.exception.NotFoundException;
import kr.movements.smv2.entity.FileEntity;
import kr.movements.smv2.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import kr.movements.smv2.common.exception.BadRequestException;
import kr.movements.smv2.common.exception.BootException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import kr.movements.smv2.util.FileUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

@Service(value = "fileService")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    private final FileUtils fileUtils = FileUtils.getInstance();
    private final S3Service s3Service;
    private final MainProperty mainProperty;
    private final FileRepository fileRepository;

    // FIXME: 보류) s3 저장 로직 -> 유틸 함수, 파일 엔티티 관련 로직 -> 각 도메인 별 서비스로 분리 필요
    //          (rollbackFor = Exception.class 사용을 지양하고 각 도메인 별 하나의 트랜잭션 안에서 다 관리해야함)
    //         추후 고도화 혹은 리팩토링시 수정해야함.

    @Transactional(rollbackFor = Exception.class)
    @Override
    public FileEntity saveFile(MultipartFile mFile, String s3DirPath) {
        if(mFile.isEmpty() || mFile.getSize() <= 0) {
            throw new BadRequestException("유효하지 않은 파일입니다. file name: "+ mFile.getOriginalFilename() + ", file size: "+ mFile.getSize());
        }
        s3DirPath += "/" + LocalDate.now();
        String mime = mFile.getOriginalFilename().substring(mFile.getOriginalFilename().lastIndexOf(".")+1);
        String storedName = System.currentTimeMillis() + "." + mime;
        FileEntity saveFile = fileRepository.save(FileEntity.builder()
            .filePath(s3DirPath)
            .fileSize(mFile.getSize())
            .fileType(mFile.getContentType())
            .mime(mime)
            .originalName(mFile.getOriginalFilename())
            .storedName(storedName)
            .build());

        try {
            s3Service.put(s3DirPath, storedName, mFile);
        } catch(IOException e) {
            e.printStackTrace();
//            throw new kr.movements.smv2.common.exception.IOException("파일을 저장하는 도중 오류가 발생하였습니다. 관리자에게 문의해주세요.");
            throw new kr.movements.smv2.common.exception.IOException("파일을 저장하는 도중 오류가 발생하였습니다. 관리자에게 문의해주세요.");
        }

        return saveFile;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public FileEntity updateFile(MultipartFile mFile, Long fileId) {
        if(mFile.isEmpty() || mFile.getSize() <= 0) {
            throw new BadRequestException("유효하지 않은 파일입니다. file name: "+ mFile.getOriginalFilename() + ", file size: "+ mFile.getSize());
        }
        FileEntity fileEntity = fileRepository.findById(fileId).orElseThrow(() -> new BadRequestException("해당 id의 파일이 존재하지 않습니다."));
        String fileName = mFile.getOriginalFilename();
        String mime = fileName.substring(fileName.lastIndexOf(".")+1);
        String newStoredName = System.currentTimeMillis() + "." + mime;

        try{
            s3Service.delete(fileEntity.getFilePath(), fileEntity.getStoredName());     // 기존 파일 삭제
            s3Service.put(fileEntity.getFilePath(), newStoredName, mFile);    // 새로운 파일 저장
        } catch (IOException e){
            e.printStackTrace();
        }

        fileEntity.update(mFile.getSize(), mFile.getContentType(), mime, fileName, newStoredName);
        return fileEntity;
    }

    @Override
    public void imgFileCheck(MultipartFile imgFile) {
        if(imgFile.getSize() <= 0 || !StringUtils.hasText(fileUtils.getImgExtension(imgFile.getContentType()))){
            throw new BadRequestException("유효하지 않은 파일입니다. file name: "+ imgFile.getOriginalFilename() + ", file size: "+ imgFile.getSize());
        }
    }

    private void saveThumbnail(MultipartFile imageFile, String s3DirPath, String storedFileName) {
        double orgWidth = 0;
        double orgHeight = 0;
        BufferedImage bImage = null;
        s3DirPath += "/" + LocalDate.now();

        try {
            bImage = ImageIO.read(imageFile.getInputStream());
            orgWidth = bImage.getWidth();
            orgHeight = bImage.getHeight();
        } catch (IOException e){
            e.printStackTrace();
        }

        int x = 0;
        int y = 0;
        int width = mainProperty.thumbnailWidth;
        int height = mainProperty.thumbnailHeight;

        double w = width / orgWidth;
        double h = height / orgHeight;

        if (w > h) {
            height = (int) (orgHeight * w);
            y = (int) ((mainProperty.thumbnailHeight) - (orgHeight * w)) / 2;
        } else {
            width = (int) (orgWidth * h);
            x = (int) ((mainProperty.thumbnailWidth) - (orgWidth * h)) / 2;
        }

        BufferedImage resizeImage = new BufferedImage(mainProperty.thumbnailWidth, mainProperty.thumbnailHeight, BufferedImage.TYPE_INT_RGB);
        resizeImage.getGraphics().drawImage(bImage.getScaledInstance(width, height, Image.SCALE_SMOOTH), x, y, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

//        String thumbnailUrl = "";
//        String fileUrl = "";

        // ex) 1234567.jpg -> 1234567_thumbnail.png
        String thumbnailName = storedFileName.substring(0, storedFileName.lastIndexOf(".")) + "_thumbnail.png";

        try {
            ImageIO.write(resizeImage, "png", baos);
            s3Service.put(s3DirPath, thumbnailName, baos.toByteArray());
//            thumbnailUrl = fileUrl.substring(0, fileUrl.lastIndexOf("/")) + File.separator + awsConfiguration.getPath() + File.separator + s3DirPath + File.separator + thumbnailName;
        } catch(IOException e){
            e.printStackTrace();
            throw new kr.movements.smv2.common.exception.IOException("파일을 저장하는 도중 오류가 발생하였습니다. 관리자에게 문의해주세요.");
        }

//        return thumbnailUrl;
    }

    private void updateThumbnail(MultipartFile imageFile, Long fileId, String oldStoredName) {
        FileEntity file = fileRepository.findByIdAndHasDeleted(fileId, false).orElseThrow(() -> new BadRequestException("해당 id의 파일이 존재하지 않습니다."));
        String thumbnailName = oldStoredName.substring(0, oldStoredName.lastIndexOf(".")) + "_thumbnail.png";
        s3Service.delete(file.getFilePath(), thumbnailName);
        saveThumbnail(imageFile, file.getFilePath(), file.getStoredName());
    }

    @Override
    public FileEntity saveImg(MultipartFile imageFile, String s3DirPath) {
        imgFileCheck(imageFile);
        return saveFile(imageFile, s3DirPath);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public FileEntity saveImgWithThumbnail(MultipartFile imageFile, String s3DirPath) {
        FileEntity file = saveImg(imageFile, s3DirPath);
        saveThumbnail(imageFile, s3DirPath, file.getStoredName());
        return file;
    }

    @Override
    public FileEntity updateImg(MultipartFile imageFile, Long fileId) {
        imgFileCheck(imageFile);
        return updateFile(imageFile, fileId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public FileEntity updateImgWithThumbnail(MultipartFile imageFile, Long fileId, String oldStoredName) {
        FileEntity file = updateImg(imageFile, fileId);
        updateThumbnail(imageFile, fileId, oldStoredName);
        return file;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteImg(Long fileId) {
        FileEntity file = fileRepository.findByIdAndHasDeleted(fileId, false).orElseThrow(() -> new BadRequestException("해당 id의 이미지 파일이 이미 삭제되었거나 존재하지 않습니다."));
        s3Service.delete(file.getFilePath(), file.getStoredName());
        String thumbnailName = file.getStoredName().substring(0, file.getStoredName().lastIndexOf(".")) + "_thumbnail.png";
        s3Service.delete(file.getFilePath(), thumbnailName);
        file.setHasDeleted(true);
    }

    @Override
    public byte[] getThumbnail(Long imgFileId) {
        FileEntity file = fileRepository.findByIdAndHasDeleted(imgFileId, false).orElseThrow(() -> new BadRequestException("해당 id의 이미지 파일이 존재하지 않습니다."));
        String[] storedNames = file.getStoredName().split("\\.");
        byte[] thumbnail;

        try {
//            thumbnail = s3Service.get(file.getFilePath(), storedNames[0] + "_thumbnail." + storedNames[1]);
            thumbnail = s3Service.get(file.getFilePath(), storedNames[0] + "_thumbnail.png");
        } catch(IOException e) {
            e.printStackTrace();
            throw new BootException("썸네일을 불러오는 도중 오류가 발생했습니다. 관리자에게 문의해주세요.");
        }

        return thumbnail;
    }

    @Override
    public byte[] getImage(Long imgFileId) throws IOException {
        FileEntity file = fileRepository.findByIdAndHasDeleted(imgFileId, false).orElseThrow(() -> new BadRequestException("해당 id의 이미지 파일이 존재하지 않습니다."));
        byte[] image;
        try {
            image = s3Service.get(file.getFilePath(), file.getStoredName());
        } catch(IOException e) {
            e.printStackTrace();
            throw new BootException("이미지를 불러오는 도중 오류가 발생했습니다. 관리자에게 문의해주세요.");
        }

        return image;
    }

}