package kr.movements.smv2.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@ApiModel("현장관리자 통계 response")
@Getter
@Builder
public class StatisticsSiteResponse {
//    @ApiModelProperty(value = "계약 현장 수")
//    private Integer siteCount;
    @ApiModelProperty(value = "계약 수")
    private Integer contractCount;
    @ApiModelProperty(value = "계약 수 증감율")
    private Integer contractIncreaseRate;
    @ApiModelProperty(value = "계약 전 달 대비 증가 수")
    private Integer contractIncreaseCount;
    @ApiModelProperty(value = "송장 수")
    private Integer waybillCount;
    @ApiModelProperty(value = "송장 수 증감율")
    private Integer waybillIncreaseRate;
    @ApiModelProperty(value = "송장 전 달 대비 증가 수")
    private Integer waybillIncreaseCount;
    @ApiModelProperty(value = "자재 종류별 물량")
    private Map<String, Integer> materials;

}
