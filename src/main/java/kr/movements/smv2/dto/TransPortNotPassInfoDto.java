package kr.movements.smv2.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@ApiModel("운송정보 불통과정보 상세정보 팝업 파일 포함 response")
@Getter
@Builder
public class TransPortNotPassInfoDto {

    @ApiModelProperty(value = "송장 id")
    private Long waybillId;
    @ApiModelProperty(value = "송장번호")
    private String waybillNum;
    @ApiModelProperty(value = "회원정보 id")
    private Long userId;
    @ApiModelProperty(value = "운송기사 id")
    private Long driverId;
    @ApiModelProperty(value = "회원명")
    private String userName;
    @ApiModelProperty(value = "회원 연락처")
    private String userPhone;
    @ApiModelProperty(value = "상차지명")
    private String startSiteName;
    @ApiModelProperty(value = "하차지명")
    private String endSiteName;
    @ApiModelProperty(value = "자재종류 코드")
    private String materialTypeCode;
    @ApiModelProperty(value = "물량")
    private Integer materialVolume;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "출발 일시")
    private LocalDateTime departureTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "도착 일시")
    private LocalDateTime arriveTime;
    @ApiModelProperty(value = "불통과 사유")
    private String reason;
    @ApiModelProperty(value = "파일 경로")
    private byte[] thumbnail;
}
