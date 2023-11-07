package kr.movements.smv2.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@ApiModel("실시간 운송 현황 response")
@Getter
@Builder
public class TransportRealTimeListResponse {
    @ApiModelProperty(value = "실시간 운송 리스트")
    private Page<TransportRealTimeListDto> content;
    @ApiModelProperty(value = "일일 불통과 건수")
    private Integer dailyFailureCount;
}