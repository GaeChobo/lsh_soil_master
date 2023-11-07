package kr.movements.smv2.dto;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@ApiModel("계약서 등록 dto")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractSaveDto {

    @ApiModelProperty(value = "계약서 작성자명")
    private Long userId;

    @ApiModelProperty(value = "상차지 id")
    private Long startSiteId;

    @ApiModelProperty(value = "하차지 id")
    private Long endSiteId;

    @ApiModelProperty(value = "자재코드")
    private List<MaterialListDto> material;

}
