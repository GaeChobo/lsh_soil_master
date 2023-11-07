package kr.movements.smv2.dto;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@ApiModel("운송기사 송장등록 하차지조회 response")
@Getter
@Builder
public class DriverWaybillSearchEndSiteResponse {
    @ApiModelProperty(value = "하차지 id")
    private Long endSiteId;
    @ApiModelProperty(value = "하차지명")
    private String endSiteName;
    @QueryProjection
    public DriverWaybillSearchEndSiteResponse(Long endSiteId, String endSiteName) {
        this.endSiteId = endSiteId;
        this.endSiteName = endSiteName;
    }
}
