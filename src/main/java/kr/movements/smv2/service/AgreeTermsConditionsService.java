package kr.movements.smv2.service;

import kr.movements.smv2.dto.AgreeTermsConditionsDTO;
import kr.movements.smv2.entity.AgreeTermsConditionsEntity;

import java.io.IOException;
import java.util.List;

public interface AgreeTermsConditionsService {

    List<AgreeTermsConditionsDTO> privacyIndexList();
    List<AgreeTermsConditionsDTO> serviceIndexList();
    byte[] exportToBlobText(Long policyId) throws IOException;
}
