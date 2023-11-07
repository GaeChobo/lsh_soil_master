package kr.movements.smv2.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@ApiModel("계약서 상세장보 response")
@Getter
@Builder
@Setter
public class ContractInfoResponse {

    @ApiModelProperty(value = "자재 리스트")
    private List<MaterialInfoList> materialList;

    @ApiModelProperty(value = "상차지명")
    private String startSiteName;

    @ApiModelProperty(value = "하차지명")
    private String endSiteName;

    @ApiModelProperty(value = "등록자")
    private String creator;

    @ApiModelProperty(value = "계약서 송장 유무")
    private boolean waybillUseAt;

}
