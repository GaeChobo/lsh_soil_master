package kr.movements.smv2.service;

import kr.movements.smv2.common.exception.BadRequestException;
import kr.movements.smv2.dto.InquiryDetailDto;
import kr.movements.smv2.dto.InquiryDto;
import kr.movements.smv2.entity.FileEntity;
import kr.movements.smv2.entity.InquiryEntity;
import kr.movements.smv2.entity.UserInfoEntity;
import kr.movements.smv2.entity.code.CommonCode;
import kr.movements.smv2.repository.FileRepository;
import kr.movements.smv2.repository.InquiryRepository;
import kr.movements.smv2.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InquiryServiceImpl implements InquiryService{

    private final UserInfoRepository userInfoRepository;
    private final FileRepository fileRepository;
    private final InquiryRepository inquiryRepository;
    private final FileService fileService;

    @Override
    public void inquiryRegister(Long userId, String inquiryCode, String email,String inquiryText, Long fileId) {

        UserInfoEntity userInfoEntity = userInfoRepository.findByIdAndHasDeleted(userId, Boolean.FALSE)
                .orElseThrow(() -> new BadRequestException("유저정보를 확인할 수 없습니다."));

        Optional<FileEntity> fileEntity = fileId != null ? fileRepository.findByIdAndHasDeleted(fileId, false) : Optional.empty();

        if(inquiryCode.equals(CommonCode.INQUIRY_REASON_TRANSIT.getCode()) || inquiryCode.equals(CommonCode.INQUIRY_REASON_TRANSIT.getCode())) {

            InquiryEntity inquiryEntity = InquiryEntity.builder()
                    .inquiryCode(inquiryCode)
                    .inquiryText(inquiryText)
                    .receiveEmail(email)
                    .userInfoId(userInfoEntity)
                    .file(fileEntity.orElse(null))
                    .build();

            inquiryRepository.save(inquiryEntity);
        }

        else {
            throw new BadRequestException("잘못된 문의 코드입니다.");
        }

    }

    @Override
    public Page<InquiryDto> inquiryList(Long userId, Pageable pageable) {

        UserInfoEntity userInfoEntity = userInfoRepository.findByIdAndHasDeleted(userId, Boolean.FALSE)
                .orElseThrow(() -> new BadRequestException("유저정보를 확인할 수 없습니다."));


        return inquiryRepository.getInquiryList(userInfoEntity.getId(), pageable);
    }


    @Override
    public Page<InquiryDto> adminInquiryList(Pageable pageable) {

        return inquiryRepository.getAdminInquiryList(pageable);
    }

    @Override
    public InquiryDetailDto inquiryDetail(Long inquiryId) throws IOException {

        InquiryDetailDto.InquiryDetailDtoBuilder inquiryDetailDtoBuilder = InquiryDetailDto.builder();

        Optional<InquiryEntity> inquiryEntity = inquiryRepository.findByIdAndHasDeleted(inquiryId, Boolean.FALSE);

        if(inquiryEntity.isPresent()) {
            if(inquiryEntity.get().getFile() != null) {
                byte[] thumbnail = fileService.getThumbnail(inquiryEntity.get().getFile().getId());
                inquiryDetailDtoBuilder.thumbnail(thumbnail);

            }
        }

        if(inquiryEntity.get().getInquiryCode().equals(CommonCode.INQUIRY_REASON_TRANSIT.getCode())) {
            inquiryDetailDtoBuilder.inquiryValue(CommonCode.INQUIRY_REASON_TRANSIT.getValue());
        }else if (inquiryEntity.get().getInquiryCode().equals(CommonCode.INQUIRY_REASON_ETC)) {
            inquiryDetailDtoBuilder.inquiryValue(CommonCode.INQUIRY_REASON_ETC.getValue());
        }

        inquiryDetailDtoBuilder.inquiryText(inquiryEntity.get().getInquiryText())
                .receiveEmail(inquiryEntity.get().getReceiveEmail())
                .inquiryId(inquiryEntity.get().getId())
                .inquiryCode(inquiryEntity.get().getInquiryCode());

        return inquiryDetailDtoBuilder.build();
    }
}
