package kr.movements.smv2.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@ApiModel("현장관리자 - 송장등록 - 상/하치지 목록 조회")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WaybillSearchSiteListDto {

    @ApiModelProperty(value = "현장 id")
    private Long siteId;
    @ApiModelProperty(value = "현장명")
    private String  siteName;
    @ApiModelProperty(value = "현장구분")
    private String  siteType;
    @ApiModelProperty(value = "주소")
    private String  address;
    @ApiModelProperty(value = "상세주소")
    private String  addressDetail;
    @ApiModelProperty(value = "위도")
//    private BigDecimal latitude;
    private double latitude;
    @ApiModelProperty(value = "경도")
//    private BigDecimal longitude;
    private double longitude;
    @ApiModelProperty(value = "계약서 id")
    private Long contractId;
    @ApiModelProperty(value = "거리(meter)")
    private double distance;

}
