package kr.movements.smv2.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@ApiModel("실시간위치 등록 dto")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationSaveDto {

    @ApiModelProperty(value = "송장 ID", example = "1682580462169460")
    private Long waybillId;
    @ApiModelProperty(value = "위도" , example ="0.5")
//    private BigDecimal lat;
    private Double lat;
    @ApiModelProperty(value = "경도", example ="0.5")
//    private BigDecimal lon;
    private Double lon;
}
