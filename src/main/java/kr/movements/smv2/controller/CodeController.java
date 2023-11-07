package kr.movements.smv2.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import kr.movements.smv2.dto.Payload;
import kr.movements.smv2.service.CodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@Api(tags = "코드관리")
public class CodeController {
//    private final CodeService codeService;
//    private final CodeRepository codeRepository;


    private final CodeService codeService;


    @ApiOperation(value = "하위 code list 조회")
    @GetMapping(value = "/common/codes/{code}")
    public Payload<Map<String, String>> getCodeList(HttpServletRequest request, HttpServletResponse response,
                                                    @PathVariable String code) {

        return new Payload<Map<String, String>>(HttpStatus.OK, request.getServletPath(), codeService.getCodeList(code), response);
    }
    @ApiOperation(value = "최상위 code list 조회")
    @GetMapping(value = "/common/codes")
    public Payload<Map<String, String>> getParentCodeList(HttpServletRequest request, HttpServletResponse response) {
        return new Payload<Map<String, String>>(HttpStatus.OK, request.getServletPath(), codeService.getParentCode(), response);
    }


/*
    @ApiIgnore
    @GetMapping(value = "/master/codes/init")
    public Payload<List<CodeEntity>> codeInit(HttpServletRequest request, HttpServletResponse response) {
        codeRepository.deleteAll();
        List<CodeEntity> codes = codeService.initCodes();
        return new Payload<>(HttpStatus.OK, request.getServletPath(), codes, response);
    }

    @ApiOperation(value = "공통코드 등록")
    @PostMapping(value = "/master/code/create")
    public Payload<String> codeCreate(HttpServletRequest request, HttpServletResponse response, @RequestBody CodeDto dto) {

        codeService.codeSave(dto);

        return new Payload<>(HttpStatus.CREATED, request.getServletPath(), response);
    }

 */

}
