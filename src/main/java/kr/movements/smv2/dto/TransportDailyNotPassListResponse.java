package kr.movements.smv2.dto;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@ApiModel("운송정보 일일 불통과 목록 팝업 response")
@Getter
@Builder
public class TransportDailyNotPassListResponse {

    @ApiModelProperty(value = "송장 id")
    private Long waybillId;
    @ApiModelProperty(value = "송장번호")
    private String waybillNum;
    @ApiModelProperty(value = "상차지명")
    private String startSiteName;
    @ApiModelProperty(value = "하차지명")
    private String endSiteName;
    @ApiModelProperty(value = "운송기사 회원명")
    private String driverName;
    @ApiModelProperty(value = "차량번호")
    private String carNumber;

    @QueryProjection
    public TransportDailyNotPassListResponse(Long waybillId, String waybillNum, String startSiteName, String endSiteName, String driverName, String carNumber) {
        this.waybillId = waybillId;
        this.waybillNum = waybillNum;
        this.startSiteName = startSiteName;
        this.endSiteName = endSiteName;
        this.driverName = driverName;
        this.carNumber = carNumber;
    }
}
