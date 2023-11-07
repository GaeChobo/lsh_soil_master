package kr.movements.smv2.dto;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import kr.movements.smv2.entity.code.CommonCode;
import lombok.*;

@ApiModel("문의 리스트")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InquiryDto {

    @ApiModelProperty(value = "문의내용 id")
    private Long inquiryId;
    @ApiModelProperty(value = "문의내용 타입 코드")
    private String inquiryCode;
    @ApiModelProperty(value = "문의내용 value")
    private String inquiryValue;
    @ApiModelProperty(value = "답변받을 이메일")
    private String receiveEmail;
    @ApiModelProperty(value = "문의 내용")
    private String inquiryText;

    private String mapInquiryCodeToValue(String inquiryCode) {
        // Implement the mapping logic here based on the inquiry code
        if (CommonCode.INQUIRY_REASON_TRANSIT.getCode().equals(inquiryCode)) {
            return CommonCode.INQUIRY_REASON_TRANSIT.getValue();
        } else if (CommonCode.INQUIRY_REASON_ETC.getCode().equals(inquiryCode)) {
            return CommonCode.INQUIRY_REASON_ETC.getValue();
        }
        // Handle other cases if needed
        return null;
    }

    @QueryProjection
    public InquiryDto(Long inquiryId, String inquiryCode, String receiveEmail, String inquiryText) {
        this.inquiryId = inquiryId;
        this.inquiryCode = inquiryCode;
        this.receiveEmail = receiveEmail;
        this.inquiryText = inquiryText;
        this.inquiryValue = mapInquiryCodeToValue(inquiryCode);
    }
}
