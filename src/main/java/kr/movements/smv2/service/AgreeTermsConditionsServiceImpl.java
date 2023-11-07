
package kr.movements.smv2.service;

import kr.movements.smv2.dto.AgreeTermsConditionsDTO;
import kr.movements.smv2.entity.AgreeTermsConditionsEntity;
import kr.movements.smv2.entity.code.CommonCode;
import kr.movements.smv2.repository.AgreeTermsConditionsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("AgreeTermsConditionsService")
@RequiredArgsConstructor
@Transactional
public class AgreeTermsConditionsServiceImpl implements AgreeTermsConditionsService {

    private final AgreeTermsConditionsRepository agreeTermsConditionsRepository;

    @Override
    public List<AgreeTermsConditionsDTO> privacyIndexList() {

        List<AgreeTermsConditionsEntity> result = agreeTermsConditionsRepository.findByPolicyTypeAndHasDeletedOrderByCreatedDateDesc(CommonCode.POLICY_PRIVACY.getCode(), Boolean.FALSE);

        List<AgreeTermsConditionsDTO> dtoList = new ArrayList<>();
        for (AgreeTermsConditionsEntity entity : result) {
            AgreeTermsConditionsDTO dto = AgreeTermsConditionsDTO.builder()
                    .policyId(entity.getId())
                    .privateTitle(entity.getPolicyTitle())
                    .build();
            dtoList.add(dto);
        }

        return dtoList;
    }
    @Override
    public List<AgreeTermsConditionsDTO> serviceIndexList() {

        List<AgreeTermsConditionsEntity> result = agreeTermsConditionsRepository.findByPolicyTypeAndHasDeletedOrderByCreatedDateDesc(CommonCode.POLICY_SERVICE.getCode(), Boolean.FALSE);

        List<AgreeTermsConditionsDTO> dtoList = new ArrayList<>();
        for (AgreeTermsConditionsEntity entity : result) {
            AgreeTermsConditionsDTO dto = AgreeTermsConditionsDTO.builder()
                    .policyId(entity.getId())
                    .privateTitle(entity.getPolicyTitle())
                    .build();
            dtoList.add(dto);
        }

        return dtoList;
    }
    @Override
    public byte[] exportToBlobText(Long policyId) throws IOException {
        // 최신 데이터 조회
        Optional<AgreeTermsConditionsEntity> agreeTermsConditionsEntity = agreeTermsConditionsRepository.findByIdAndHasDeleted(policyId, Boolean.FALSE);
        if (agreeTermsConditionsEntity.isPresent()) {
            // 텍스트 데이터 가져오기
            String textData = agreeTermsConditionsEntity.get().getPolicyText();
            if (textData != null) {
                return textToBlob(textData);
            }
        }
        // 데이터가 없거나 텍스트 데이터가 null인 경우
        return null;
    }
    private byte[] textToBlob(String text) throws IOException {
        return text.getBytes(StandardCharsets.UTF_8);
    }

}
