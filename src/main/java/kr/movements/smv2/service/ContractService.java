package kr.movements.smv2.service;

import kr.movements.smv2.dto.*;
import kr.movements.smv2.entity.ContractEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ContractService {

    Page<ContractDeleteInfoDto> findWaybillWithContract(Long contractId, String waybillStatus, Pageable pageable);
    Long findByContractId(Long startSiteId, Long endSiteId);
    List<SiteAreaMapInfo> mySiteLocation(Long userId);
    List<SiteAreaMapInfo> contractMapList(Long userId, String searchType);
    boolean contractExistence(ExistenceDto dto);
    void contractDelete(Long contractId);
//    void materialUpdate(List<MaterialSaveDto> dto);
    void materialUpdate(Long contractId, ContractUpdateResponse contractUpdateResponse);
    //void materialUpdate(MaterialUpdateDto dto, Long materialId);
    //void materialDelete(Long materialId, Long contractId);
    Page<ContractList> SearchContractList(Long userId,Pageable pageable, String keyword);

    Page<ContractList> adminContractSearchList(Pageable pageable, String searchType, String keyword);

    Long saveContract(ContractSaveDto contractSaveDto);

    ContractInfoResponse contractInfo(Long contractId);

    AdminContractInfoResponse adminContractInfo(Long contractId);
}
