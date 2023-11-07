package kr.movements.smv2.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
@ApiModel("운송내역 미처리 상세")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransportUnprocessedDetailDto {
    @ApiModelProperty(value = "유저명")
    private String userName;
    @ApiModelProperty(value = "연락처")
    private String userPhone;
    @ApiModelProperty(value = "차량번호")
    private String carNumber;
    @ApiModelProperty(value = "운송회사")
    private String driverCompany;
    @ApiModelProperty(value = "송장번호")
    private String waybillNum;
    @ApiModelProperty(value = "상차지명")
    private String startSiteName;
    @ApiModelProperty(value = "하차지명")
    private String endSiteName;
    @ApiModelProperty(value = "자재코드")
    private String materialTypeCode;
    @ApiModelProperty(value = "자재코드")
    private String materialValue;
    @ApiModelProperty(value = "운송량")
    private Integer transportVolume;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "출발시간")
    private LocalDateTime departureTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "강제종료시간")
    private LocalDateTime arriveTime;
    @ApiModelProperty(value = "강제종료 사유")
    private String reason;

    @QueryProjection
    public TransportUnprocessedDetailDto(String userName, String userPhone, String carNumber, String driverCompany, String waybillNum, String startSiteName, String endSiteName, String materialTypeCode,Integer transportVolume, LocalDateTime departureTime, LocalDateTime arriveTime, String reason) {
        this.userName = userName;
        this.userPhone = userPhone;
        this.carNumber = carNumber;
        this.driverCompany = driverCompany;
        this.waybillNum = waybillNum;
        this.startSiteName = startSiteName;
        this.endSiteName = endSiteName;
        this.materialTypeCode = materialTypeCode;
        this.transportVolume = transportVolume;
        this.departureTime = departureTime;
        this.arriveTime = arriveTime;
        this.reason = reason;
    }
}
