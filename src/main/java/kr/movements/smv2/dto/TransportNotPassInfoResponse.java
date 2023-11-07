package kr.movements.smv2.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@ApiModel("운송정보 불통과정보 상세정보 팝업 response")
@Getter
@Builder
public class TransportNotPassInfoResponse {
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
    private Long fileId;

    @QueryProjection
    public TransportNotPassInfoResponse(Long waybillId, String waybillNum, Long userId, Long driverId, String userName, String userPhone, String startSiteName, String endSiteName, String materialTypeCode, Integer materialVolume, LocalDateTime departureTime, LocalDateTime arriveTime, String reason, Long fileId) {
        this.waybillId = waybillId;
        this.waybillNum = waybillNum;
        this.userId = userId;
        this.driverId = driverId;
        this.userName = userName;
        this.userPhone = userPhone;
        this.startSiteName = startSiteName;
        this.endSiteName = endSiteName;
        this.materialTypeCode = materialTypeCode;
        this.materialVolume = materialVolume;
        this.departureTime = departureTime;
        this.arriveTime = arriveTime;
        this.reason = reason;
        this.fileId = fileId;

    }
}
