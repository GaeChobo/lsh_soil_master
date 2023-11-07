package kr.movements.smv2.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

@ApiModel("통계 자재별 물량 dto")
@Getter
@Builder
public class StatisticsMaterialDto {
    @ApiModelProperty(value = "토사 수량")
    private Integer tosaVolume;
    @ApiModelProperty(value = "암 수량")
    private Integer stoneVolume;
    @ApiModelProperty(value = "모래 수량")
    private Integer sandVolume;
    @ApiModelProperty(value = "자갈 수량")
    private Integer pebbleVolume;
    @ApiModelProperty(value = "파쇄석 수량")
    private Integer crashedStoneVolume;
    @ApiModelProperty(value = "석분 수량")
    private Integer stonePowderVolume;
    @ApiModelProperty(value = "혼합골재 수량")
    private Integer mixedAggregateVolume;
    @ApiModelProperty(value = "잡석 수량")
    private Integer rubbleVolume;
}
