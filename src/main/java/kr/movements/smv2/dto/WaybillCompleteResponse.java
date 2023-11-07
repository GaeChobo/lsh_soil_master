package kr.movements.smv2.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@ApiModel("송장 통과 response")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class WaybillCompleteResponse {
    @ApiModelProperty(value = "송장 id")
    private Long waybillId;
    @ApiModelProperty(value = "지연여부")
    private boolean delayAt;
//    @JsonFormat(pattern = "HH:mm")
//    @ApiModelProperty(value = "예상소요시간")
//    private LocalDateTime estimatedTime;
    @ApiModelProperty(value = "실제소요시간(초)")
    private Integer durationTime;
    @ApiModelProperty(value = "예상소요시간(milli 초)")
    private Integer ete;
}
