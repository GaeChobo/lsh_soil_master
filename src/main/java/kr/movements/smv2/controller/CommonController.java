package kr.movements.smv2.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import kr.movements.smv2.dto.Payload;
import kr.movements.smv2.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Slf4j
@Api(tags = "공통")
public class CommonController {

    private final FileService fileService;

    @ApiOperation(value = "원본이미지 조회")
    @GetMapping(value = "/common/image/origin/{fileId}")
    public Payload<byte[]> waybillImageSave(HttpServletRequest request,
                                            HttpServletResponse response,
                                            @PathVariable Long fileId) throws IOException {

        byte[] result = fileService.getImage(fileId);
        return new Payload<byte[]>(HttpStatus.OK, request.getServletPath(), result, response);
    }
}
