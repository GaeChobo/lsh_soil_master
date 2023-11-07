package kr.movements.smv2.service;

import kr.movements.smv2.entity.code.CommonCode;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service(value = "codeService")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CodeServiceImpl implements CodeService {
	private Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public Map<String, String> getCodeList(String code) {
        return CommonCode.getCodeList(code);
    }

    @Override
    public Map<String, String> getParentCode() {
        return CommonCode.getParentCodeList();
    }

//	private final CodeRepository codeRepository;
/*
    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<CodeEntity> initCodes() {
        List<CodeEntity> codes = new ArrayList<>();
        codes.add(new CodeEntity("10", "권한", "0", 1));
        codes.add(new CodeEntity("1010", "Admin", "10", 2));
        codes.add(new CodeEntity("1020", "Manager", "10", 2));
        codes.add(new CodeEntity("1030", "Driver", "10", 2));

        return codeRepository.saveAll(codes);
    }
*/



}
