package kr.movements.smv2.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import kr.movements.smv2.dto.Payload;
import kr.movements.smv2.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Map;

@Slf4j
@Api(tags = "Admin")
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class TestController {

    private final EmailService emailService;

    @ApiOperation("이메일 테스트")
    @PostMapping(value = "/test/email")
    public Payload<String> EmailTest(@RequestBody Map<String, String> message, HttpServletRequest request, HttpServletResponse response) throws Exception {

        System.out.println(message.get("Test"));

        emailService.sendMail(message.get("Test"));

        return new Payload<>( HttpStatus.OK, "email send!", request.getServletPath(), response);
    }


    @ApiOperation("이메일 테스트")
    @PostMapping(value = "/test/QRcode")
    public ResponseEntity<byte[]> QRTest(@RequestBody Map<String, String> message, HttpServletRequest request, HttpServletResponse response) throws Exception {

        JSONObject obj = new JSONObject();

        obj.put("Test1", message.get("Test1"));
        obj.put("Test2", message.get("Test2"));
        obj.put("Test3", message.get("Test3"));

        //QR Code 컬러
        int qrcodeColor = 0xFF000000;

        //QR Code 배경컬러
        int backgroundColor = 0xFFFFFFFF;

        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        String codeurl = new String(obj.toString().getBytes("UTF-8"), "ISO-8859-1");

        //QR Code의 Width, Height 값
        BitMatrix bitMatrix = qrCodeWriter.encode(codeurl, BarcodeFormat.QR_CODE, 200, 200);


        try (ByteArrayOutputStream out = new ByteArrayOutputStream();) {

            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", out);

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(out.toByteArray());
        }
    }

}
