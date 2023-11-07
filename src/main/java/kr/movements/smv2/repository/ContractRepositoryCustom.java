package kr.movements.smv2.repository;

import kr.movements.smv2.dto.ContractList;
import kr.movements.smv2.dto.DriverWaybillSearchEndSiteResponse;
import kr.movements.smv2.dto.DriverWaybillSearchStartSiteResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ContractRepositoryCustom {

    List<DriverWaybillSearchStartSiteResponse> waybillSearchStartSite(String keyword);
    List<DriverWaybillSearchEndSiteResponse> waybillSearchEndSite(Long startSiteId);
}
