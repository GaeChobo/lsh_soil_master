package kr.movements.smv2.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@ApiModel("최근 송장 상세정보 dto")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class WaybillLatestInfoDto {
    @ApiModelProperty(value = "송장id")
    private Long waybillId;
    @ApiModelProperty(value = "송장상태 코드")
    private String waybillStatusCode;
    @ApiModelProperty(value = "송장상태")
    private String waybillStatus;
    @ApiModelProperty(value = "송장번호")
    private String waybillNum;
}
