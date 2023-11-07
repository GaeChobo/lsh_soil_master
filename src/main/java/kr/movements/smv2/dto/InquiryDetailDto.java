package kr.movements.smv2.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@ApiModel("문의 상세")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InquiryDetailDto {

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
    @ApiModelProperty(value = "문의사항 이미지")
    private byte[] thumbnail;
}
