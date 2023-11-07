package kr.movements.smv2.service;

import kr.movements.smv2.entity.code.CommonCode;

import java.util.List;
import java.util.Map;

public interface CodeService {
    Map<String, String> getCodeList(String code);
    Map<String, String> getParentCode();
}
