package kr.movements.smv2.service;

import kr.movements.smv2.common.exception.BadRequestException;
import kr.movements.smv2.common.exception.NotFoundException;
import kr.movements.smv2.dto.*;
import kr.movements.smv2.entity.*;
import kr.movements.smv2.entity.code.CommonCode;
import kr.movements.smv2.repository.*;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service("ContractService")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;
    private final MaterialRepository materialRepository;
    private final SiteManagerRepository siteMemberRepository;
    private final WaybillRepository waybillRepository;
    private final UserInfoRepository userInfoRepository;

    @Override
    public Long findByContractId(Long startSiteId, Long endSiteId) {
        return contractRepository.findByContractId(startSiteId, endSiteId);
    }

    @Override
    public Page<ContractDeleteInfoDto> findWaybillWithContract(Long contractId, String waybillStatus, Pageable pageable) {

        ContractEntity contractEntity = contractRepository.findByIdAndHasDeleted(contractId, Boolean.FALSE)
                .orElseThrow(()-> new BadRequestException("확인할 수 없는 계약서정보입니다."));

        return waybillRepository.findWaybillWithContract(contractEntity.getId(), waybillStatus, pageable);
    }

    @Override
    public List<SiteAreaMapInfo> mySiteLocation(Long userId) {

        UserInfoEntity userInfoEntity = userInfoRepository.findByIdAndHasDeleted(userId, Boolean.FALSE).orElseThrow(()-> new BadRequestException("확인할 수 없는 유저정보입니다."));

        SiteManagerEntity siteManagerEntity = siteMemberRepository.findByUserIdAndHasDeleted(userInfoEntity, Boolean.FALSE).orElseThrow(()-> new BadRequestException("확인할 수 없는 현장정보입니다."));

        return siteMemberRepository.mySiteLocation(siteManagerEntity.getId());
    }

    @Override
    public List<SiteAreaMapInfo> contractMapList(Long userId, String searchType) {

        UserInfoEntity userInfoEntity = userInfoRepository.findByIdAndHasDeleted(userId, Boolean.FALSE).orElseThrow(()-> new BadRequestException("확인할 수 없는 유저정보입니다."));

        SiteManagerEntity siteManagerEntity = siteMemberRepository.findByUserIdAndHasDeleted(userInfoEntity, Boolean.FALSE).orElseThrow(()-> new BadRequestException("확인할 수 없는 현장정보입니다."));

        //상차지 검색 시
        if(searchType.equals(CommonCode.SITE_TYPE_START_SITE.getCode())) {

            return siteMemberRepository.contractMapList(CommonCode.SITE_TYPE_START_SITE.getCode());

        } else if(searchType.equals(CommonCode.SITE_TYPE_END_SITE.getCode())) {

            return siteMemberRepository.contractMapList(CommonCode.SITE_TYPE_END_SITE.getCode());

        }else {

            throw new BadRequestException("현장 구분코드를 넣어주세요");
        }

    }

    @Override
    public boolean contractExistence(ExistenceDto dto) {

        siteMemberRepository.findById(dto.getStartSiteId()).orElseThrow(() -> new BadRequestException("확인할 수 없는 상차지 현장관리자입니다."));

        siteMemberRepository.findById(dto.getEndSiteId()).orElseThrow(() -> new BadRequestException("확인할 수 없는 하차지 현장관리자입니다."));

        Long contractId = contractRepository.findByContractId(dto.getStartSiteId(), dto.getEndSiteId());

        if(contractId == null) {

            return true;

        } else {

            return false;
        }
    }

    @Override
    @Transactional
    public void contractDelete(Long contractId) {

        ContractEntity contractEntity = contractRepository.findByIdAndHasDeleted(contractId, Boolean.FALSE).orElseThrow(() -> new BadRequestException("계약서를 확인 할 수 없습니다."));

        if (waybillRepository.existsByContractAndHasDeleted(contractEntity, false)) {
            throw new BadRequestException("하위 송장이 존재합니다.");
        }
//        contractRepository.contractDelete(contractEntity.getId());
        contractEntity.setHasDeleted(true);
    }

    public void processMaterialList(List<MaterialDto> materialAdd, List<MaterialUpdateDto> materialUpdate) {

        // 자재 추가 처리 로직
        for (MaterialDto dto : materialAdd) {
            String materialTypeCode = dto.getMaterialCode();
            CommonCode.codeTypeCheck(CommonCode.MATERIAL_TYPE, materialTypeCode);

        }

        // 자재 수정 처리 로직
        for (MaterialUpdateDto dto : materialUpdate) {
            String materialTypeCode = dto.getMaterialCode();
            CommonCode.codeTypeCheck(CommonCode.MATERIAL_TYPE, materialTypeCode);
        }
    }

    @Override
    @Transactional
    public void materialUpdate(Long contractId, ContractUpdateResponse contractUpdateResponse) {

        ContractEntity contractEntity = contractRepository.findByIdAndHasDeleted(contractId, false).orElseThrow(() -> new BadRequestException("계약서정보를 확인 할 수 없습니다"));

        //기존에 등록된 자재정보. 조회결과가 무조건 있어야 함.
        List<MaterialEntity> materialEntityList = materialRepository.findByContractIdAndHasDeleted(contractId, Boolean.FALSE).orElseThrow(() -> new BadRequestException("관리자에게 문의하여 주세요"));

        //자재 추가 시 자재코드 검증
        if(contractUpdateResponse.getMaterialAdd().size() > 1) {
            for(MaterialDto materialDto : contractUpdateResponse.getMaterialAdd()) {

            }
        }

//        List<String> addMaterialCodes = contractUpdateResponse.getMaterialAdd().stream()
//                .map(MaterialDto::getMaterialCode)
//                .collect(Collectors.toList());
//
//        List<String> updateMaterialCodes = contractUpdateResponse.getMaterialUpdate().stream()
//                .map(MaterialUpdateDto::getMaterialCode)
//                .collect(Collectors.toList());
//
//        //교차 검증 중복 자재 코드 확인(addRequest, updateRequest)
//        List<String> duplicateMaterialCodes = addMaterialCodes.stream()
//                .filter(updateMaterialCodes::contains)
//                .collect(Collectors.toList());

//        if (!duplicateMaterialCodes.isEmpty()) {
//            throw new BadRequestException("Add 목록과 Update 목록에 중복된 자재 코드가 포함되어 있습니다.");
//        }

        //Add.
        for (MaterialDto material : contractUpdateResponse.getMaterialAdd()) {
            CommonCode.codeTypeCheck(CommonCode.MATERIAL_TYPE, material.getMaterialCode()); //자재 코드 검증
            //기존에 등록된 자재가 없는지 확인
            boolean materialEntity = materialRepository.existsByContractAndMaterialCodeAndHasDeleted(contractEntity, material.getMaterialCode(), false);
            if (materialEntity) {
                throw new BadRequestException("이미 등록된 자재종류는 추가 할 수 없습니다");
            }

//            //Request add 중복 자재 코드 검증
//            long duplicateCount = contractUpdateResponse.getMaterialAdd().stream()
//                    .filter(addMaterial -> addMaterial.getMaterialCode().equals(material.getMaterialCode()))
//                    .count();
//            if (duplicateCount > 1) {
//                throw new BadRequestException("Add 목록에 중복된 자재 코드가 포함되어 있습니다.");
//            }

            //save
            materialRepository.save(MaterialEntity.builder()
                    .contract(contractEntity)
                    .materialCode(material.getMaterialCode())
                    .contractVolume(material.getContractVolume())
                    .build());
        }

        //update 검증. 기존에 등록된 송장이 있는지? 누적운송량 체크
        for (MaterialUpdateDto material : contractUpdateResponse.getMaterialUpdate()) {
            //material -> contract -> waybill 운송된 송장 여부 확인
            String materialType = material.getMaterialCode();
            Integer materialVolume = material.getContractVolume();
            MaterialEntity materialEntity = materialRepository.findByIdAndHasDeleted(material.getMaterialId(), false).orElseThrow(() -> new BadRequestException("자재정보를 확인 할 수 없습니다"));

            //누적운송량이 수정할 계약 물량보다 크다면 수정불가
            Integer volume = waybillRepository.sumByMaterialVolume(contractEntity.getId(), materialType);
            if (volume == null) {
                volume = 0;
            }

            if (volume > materialVolume) {
                throw new BadRequestException("계약물량은 누적운송량보다 커야합니다. 자재종류 : " + CommonCode.valueOfCode(materialType) + ", 누적운송량 : " + volume);
            }

            if (contractUpdateResponse.getMaterialAdd().stream().anyMatch(addMaterial -> addMaterial.getMaterialCode().equals(material.getMaterialCode()))) {
                throw new BadRequestException("Add 목록에 이미 등록된 자재 종류가 있습니다.");
            }

//            //DB 수정하려는 자재코드가 있는 경우 막기위한 예외처리 -> 있어야 정상입니다!!
//            if (materialEntityList.stream()
//                    .anyMatch(entity -> entity.getMaterialCode().equals(materialEntity.getMaterialCode()))) {
//                throw new BadRequestException("DB에 이미 등록된 자재 종류가 있습니다.");
//            }

//            //Request update 중복 자재 코드 검증
//            long duplicateCount = contractUpdateResponse.getMaterialUpdate().stream()
//                    .filter(updateMaterial -> updateMaterial.getMaterialCode().equals(material.getMaterialCode()))
//                    .count();
//            if (duplicateCount > 1) {
//                throw new BadRequestException("Update 목록에 중복된 자재 코드가 포함되어 있습니다.");
//            }

            //update
            materialEntity.update(materialVolume); //해당 entity 볼륨 수정
        }

        //delete 검증. 기존에 등록된 송장이 있는지? 누적운송량 체크
        for (MaterialDeleteDto material : contractUpdateResponse.getMaterialDelete()) {
            //material -> contract -> waybill에 송장 존재여부 확인
            MaterialEntity materialEntity = materialRepository.findByIdAndHasDeleted(material.getMaterialId(), false).orElseThrow(() -> new BadRequestException("자재정보를 확인 할 수 없습니다"));

            //해당 자재 종류로 등록된 송장이 존재한다면 삭제불가
            boolean isWaybill = waybillRepository.existsByContractAndMaterialTypeCodeAndHasDeleted(contractEntity, materialEntity.getMaterialCode(), false);
            if (isWaybill) {
                throw new BadRequestException("등록된 송장이 있어서 삭제할 수 없습니다. 자재종류 : " + CommonCode.valueOfCode(materialEntity.getMaterialCode()));
            }
            //delete
            materialEntity.setHasDeleted(true);
        }
    }

    @Override
    public Page<ContractList> adminContractSearchList(Pageable pageable, String searchType, String keyword) {

        if (keyword == null) {
            keyword = "";
        }

        Page<ContractList> result;

        if (searchType == null || searchType.isEmpty()) {
            result = contractRepository.adminContractList(pageable);
        } else {
            switch (searchType) {
                case "상차지":
                    result = contractRepository.adminContractOfStartSiteNameList(keyword, pageable);
                    break;
                case "하차지":
                    result = contractRepository.adminContractOfEndSiteNameList(keyword, pageable);
                    break;
                default:
                    result = contractRepository.adminContractList(pageable);
                    break;
            }
        }

        return result;
    }


    @Override
    public Page<ContractList> SearchContractList(Long userId, Pageable pageable, String keyword) {
        UserInfoEntity userInfoEntity = userInfoRepository.findByIdAndHasDeleted(userId, Boolean.FALSE)
                .orElseThrow(() -> new BadRequestException("확인할 수 없는 유저정보입니다."));

        SiteManagerEntity siteManagerEntity = siteMemberRepository.findByUserIdAndHasDeleted(userInfoEntity, Boolean.FALSE)
                .orElseThrow(() -> new BadRequestException("확인할 수 없는 현장정보입니다."));

        String siteType = siteManagerEntity.getSiteTypeCode();

        if (keyword == null) {
            keyword = "";
        }

        Page<ContractList> result;

        if (siteType.equals("2010")) {
            result = contractRepository.contractOfStartSiteList(siteManagerEntity.getId(), keyword, pageable);
        } else if (siteType.equals("2020")) {
            result = contractRepository.contractOfEndSiteList(siteManagerEntity.getId(), keyword, pageable);
        } else {
            throw new BadRequestException("유효하지 않은 현장 유형입니다.");
        }

        return result;
    }

    @Override
    public ContractInfoResponse contractInfo(Long contractId) {

        ContractEntity contractEntity = contractRepository.findById(contractId).orElseThrow(() -> new BadRequestException("계약서 정보를 확인할 수 없습니다."));

        Long userId = contractEntity.getCreatedBy();

        ContractInfo contractInfo = contractRepository.contractInfo(contractId);

        List<MaterialInfo> result = materialRepository.materialInfo(contractId);

        List<MaterialInfoList> materialList = new ArrayList<>();

        UserInfoEntity userInfoEntity = userInfoRepository.findById(userId).orElseThrow(() -> new BadRequestException("유저 정보를 확인할 수 없습니다."));

        for(int i = 0; i < result.size(); i++) {

            int sum = 0;

            List<Integer> transVolume = waybillRepository.contractTransportVolume(result.get(i).getMaterialCode(), contractId);

            if (transVolume != null) {
                for(int j = 0; j < transVolume.size(); j++) {
                    sum += transVolume.get(j);
                }
            }

            MaterialInfoList materialInfoList = new MaterialInfoList(
                    result.get(i).getMaterialId(),
                    result.get(i).getMaterialCode(),
                    result.get(i).getContractVolume(),
                    sum);
            materialList.add(materialInfoList);
        }

        return  ContractInfoResponse.builder()
                .startSiteName(contractInfo.getstartSiteName())
                .endSiteName(contractInfo.getendSiteName())
                .creator(userInfoEntity.getUserName())
                .materialList(materialList)
                .waybillUseAt(waybillRepository.existsByContractAndHasDeleted(contractEntity, Boolean.FALSE))
                .build();
    }

    @Override
    public AdminContractInfoResponse adminContractInfo(Long contractId) {

        ContractEntity contractEntity = contractRepository.findById(contractId).orElseThrow(() -> new BadRequestException("계약서 정보를 확인할 수 없습니다."));

//        Long userId = Long.parseLong(contractEntity.getCreatedBy());
        Long userId = contractEntity.getCreatedBy();

        ContractInfo contractInfo = contractRepository.contractInfo(contractId);

        List<MaterialInfo> result = materialRepository.materialInfo(contractId);

        List<MaterialInfoList> materialList = new ArrayList<>();

        UserInfoEntity userInfoEntity = userInfoRepository.findById(userId).orElseThrow(() -> new BadRequestException("유저 정보를 확인할 수 없습니다."));

        LocalDateTime createTime = contractEntity.getCreatedDate();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        String formattedDateTime = createTime.format(formatter);

        for(int i = 0; i < result.size(); i++) {

            int sum = 0;

            List<Integer> transVolume = waybillRepository.contractTransportVolume(result.get(i).getMaterialCode(), contractId);

            if (transVolume != null) {
                for(int j = 0; j < transVolume.size(); j++) {
                    sum += transVolume.get(j);
                }
            }

            MaterialInfoList materialInfoList = new MaterialInfoList(
                    result.get(i).getMaterialId(),
                    result.get(i).getMaterialCode(),
                    result.get(i).getContractVolume(),
                    sum);
            materialList.add(materialInfoList);
        }

        return  AdminContractInfoResponse.builder()
                .startSiteName(contractInfo.getstartSiteName())
                .endSiteName(contractInfo.getendSiteName())
                .creator(userInfoEntity.getUserName())
                .userPhone(userInfoEntity.getUserPhone())
                .createDate(formattedDateTime)
                .materialList(materialList)
                .waybillUseAt(waybillRepository.existsByContractAndHasDeleted(contractEntity, Boolean.FALSE))
                .build();
    }

    public void materialException(List<MaterialListDto> material) {

        for (MaterialListDto dto : material) {
            String materialTypeCode = dto.getMaterialCode();
            CommonCode.codeTypeCheck(CommonCode.MATERIAL_TYPE, materialTypeCode);
        }
    }

    @Override
    @Transactional
    public Long saveContract(ContractSaveDto dto) {

        //자재종류 코드 검증
        for(MaterialListDto material : dto.getMaterial()) {
            CommonCode.codeTypeCheck(CommonCode.MATERIAL_TYPE, material.getMaterialCode());
        }

        UserInfoEntity userInfoEntity = userInfoRepository.findById(dto.getUserId())
                .orElseThrow(() -> new BadRequestException("유저정보를 확인할 수 없습니다."));
        SiteManagerEntity siteManagerEntity_start = siteMemberRepository.findById(dto.getStartSiteId())
                .orElseThrow(() -> new BadRequestException("상차지 정보를 확인할 수 없습니다."));
        SiteManagerEntity siteManagerEntity_end = siteMemberRepository.findById(dto.getEndSiteId())
                .orElseThrow(() -> new BadRequestException("하차지 정보를 확인할 수 없습니다."));

        int result = contractRepository.contractExisting(dto.getStartSiteId(), dto.getEndSiteId());

        if (!siteManagerEntity_start.getSiteTypeCode().equals(CommonCode.SITE_TYPE_START_SITE.getCode())) {
            throw new BadRequestException("상차지 현장이 아닙니다.");
        }

        if (!siteManagerEntity_end.getSiteTypeCode().equals(CommonCode.SITE_TYPE_END_SITE.getCode())) {
            throw new BadRequestException("하차지 현장이 아닙니다.");
        }

        if (result > 0) {
            throw new BadRequestException("중복되는 계약서입니다.");
        }

        // 없는 자재코드 들어왔을 떄 예외처리
        materialException(dto.getMaterial());

        // 중복된 자재 코드 검사
        List<String> duplicatedMaterialCodes = duplicatedMaterialCodes(dto.getMaterial());
        if (!duplicatedMaterialCodes.isEmpty()) {
            throw new BadRequestException("중복된 자재 코드가 있습니다: " + String.join(", ", duplicatedMaterialCodes));
        }

        ContractEntity contractEntity = contractRepository.save(
                ContractEntity.builder()
                        .startSiteId(siteManagerEntity_start)
                        .endSiteId(siteManagerEntity_end)
                        .build()
        );

//        contractRepository.contractCreatorUpdate(userInfoEntity.getId().toString(), contractEntity.getId());

        for (MaterialListDto materialDto : dto.getMaterial()) {
            MaterialEntity materialEntity = MaterialEntity.builder()
                    .contract(contractEntity)
                    .materialCode(materialDto.getMaterialCode())
                    .contractVolume(materialDto.getContractVolume())
                    .build();
            materialRepository.save(materialEntity);
        }
        return contractEntity.getId();
    }

    private List<String> duplicatedMaterialCodes(List<MaterialListDto> materialList) {
        List<String> duplicatedCodes = new ArrayList<>();
        Set<String> uniqueCodes = new HashSet<>();

        for (MaterialListDto materialDto : materialList) {
            String materialCode = materialDto.getMaterialCode();
            if (!uniqueCodes.add(materialCode) && !duplicatedCodes.contains(materialCode)) {
                duplicatedCodes.add(materialCode);
            }
        }

        return duplicatedCodes;
    }
}
