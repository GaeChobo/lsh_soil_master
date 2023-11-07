package kr.movements.smv2.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@ApiModel("계약서 중복 여부 dto")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExistenceDto {
    @ApiModelProperty(value = "상차지 siteId")
    private Long startSiteId;
    @ApiModelProperty(value = "하차지 siteId")
    private Long endSiteId;
}
