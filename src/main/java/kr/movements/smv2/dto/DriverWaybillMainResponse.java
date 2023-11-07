package kr.movements.smv2.dto;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@ApiModel("운송기사 송장 main response")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class DriverWaybillMainResponse {
    @ApiModelProperty(value = "송장 id")
    private Long waybillId;
    @ApiModelProperty(value = "송장상태 코드")
    private String waybillStatusCode;
    @ApiModelProperty(value = "송장번호")
    private String waybillNum;
    @ApiModelProperty(value = "회원명")
    private String  driverName;
    @ApiModelProperty(value = "일일 완료 송장 수")
    private Integer dailyCount;
    @ApiModelProperty(value = "상차지명")
    private String startSiteName;
    @ApiModelProperty(value = "하차지명")
    private String endSiteName;
    @ApiModelProperty(value = "도착예정시간")
    private Integer ete;
    @ApiModelProperty(value = "지연 여부")
    private Boolean delayAt;
    @ApiModelProperty(value = "차량번호")
    private String carNumber;
    @ApiModelProperty(value = "출발시간")
    private LocalDateTime departureTime;
}
