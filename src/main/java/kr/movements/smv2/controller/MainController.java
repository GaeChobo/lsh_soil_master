package kr.movements.smv2.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import kr.movements.smv2.dto.Payload;
import kr.movements.smv2.entity.FileEntity;
import kr.movements.smv2.repository.FileRepository;
import kr.movements.smv2.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
@Slf4j
@Api(tags = "Main")
public class MainController {

    private final FileRepository fileRepository;
    private final FileService fileService;


    @ApiOperation(value = "AdminLogin")
    @GetMapping(value = "/admin/login")
    public Payload<String> adminLogin(HttpServletRequest request, HttpServletResponse response) {

        return new Payload<>( HttpStatus.OK, "admin Login!", request.getServletPath(), response);
    }
    @ApiOperation(value = "SiteManagerLogin")
    @GetMapping(value = "/site-manager/login")
    public Payload<String> siteManagerLogin(HttpServletRequest request, HttpServletResponse response) {

        return new Payload<>( HttpStatus.OK, "siteManager Login!", request.getServletPath(), response);
    }
    @ApiOperation(value = "DriverLogin")
    @GetMapping(value = "/driver/login")
    public Payload<String> driverLogin(HttpServletRequest request, HttpServletResponse response) {

        return new Payload<>( HttpStatus.OK, "driver Login!", request.getServletPath(), response);
    }

    @ApiOperation(value = "test")
    @PostMapping(value = "/test")
    public FileEntity s3Test(HttpServletRequest request, HttpServletResponse response, @RequestParam MultipartFile s3File) {
        // 파일 등록 테스트
        String s3DirPath = "Test";
        FileEntity fileSave = fileService.saveImgWithThumbnail(s3File, s3DirPath);


        return fileSave;
    }
}
