package kr.movements.smv2.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@ApiModel("지연사유 등록 dto")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DelaySaveDto {

    @ApiModelProperty(value = "송장 ID" , example = "1352435123412")
    private Long waybillId;
    @ApiModelProperty(value = "사유 텍스트", example = "사유 텍스트")
    @Size(max = 200, message = "최소 10글자 이상 200글자를 초과할 수 없습니다.")
    private String reason;
    @ApiModelProperty(value = "지연 사유 타입", example = "6010 : 교통사고, 6020 : 휴식, 6030 : 직접입력")
    private String delayCode;
}