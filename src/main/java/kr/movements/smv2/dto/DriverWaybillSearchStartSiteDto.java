package kr.movements.smv2.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

@ApiModel("운송기사 송장등록 - 상차지검색 dto")
@Getter
@Builder
public class DriverWaybillSearchStartSiteDto {
    @ApiModelProperty(value = "위도", example = "37.378205803359755")
    private Double lat;
    @ApiModelProperty(value = "경도", example = "126.95981025695801")
    private Double lon;
    @ApiModelProperty(value = "검색어", example = "")
    private String keyword;

}
