package kr.movements.smv2.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.Map;

@ApiModel("admin 통계 response")
@Getter
@Builder
public class StatisticsAdminResponse {
    @ApiModelProperty(value = "상차지 수")
    private Integer startSiteCount;
    @ApiModelProperty(value = "상차지 수 증감율")
    private Integer startSiteIncreaseRate;
    @ApiModelProperty(value = "상차지 전 달 대비 증가 수")
    private Integer startSiteIncreaseCount;
    @ApiModelProperty(value = "하차지 수")
    private Integer endSiteCount;
    @ApiModelProperty(value = "하차지 수 증감율")
    private Integer endSiteIncreaseRate;
    @ApiModelProperty(value = "하차지 전 달 대비 증가 수")
    private Integer endSiteIncreaseCount;
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
    @ApiModelProperty(value = "자재종류별 물량")
    private Map<String, Integer> materials;
}
