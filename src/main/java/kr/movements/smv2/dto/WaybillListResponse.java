package kr.movements.smv2.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@ApiModel("송장 list response")
@Getter
@Builder
public class WaybillListResponse {
    @ApiModelProperty(value = "송장 리스트")
    private List<WaybillListDto> contents;
    @ApiModelProperty(value = "총 페이지 수")
    private Integer totalPages;
    @ApiModelProperty(value = "총 데이터 수")
    private Integer totalElements;
    @ApiModelProperty(value = "현재 페이지")
    private Integer page;
}
