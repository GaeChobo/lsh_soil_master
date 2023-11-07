package kr.movements.smv2.dto;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@ApiModel("운송기사 송장등록 상차지조회 response")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DriverWaybillSearchStartSiteResponse {
    @ApiModelProperty(value = "상차지 id")
    private Long startSiteId;
    @ApiModelProperty(value = "상차지명")
    private String startSiteName;
    @ApiModelProperty(value = "위도")
    private Double lat;
    @ApiModelProperty(value = "경도")
    private Double lon;
    @ApiModelProperty(value = "거리")
    private Double distance;
    @QueryProjection
    public DriverWaybillSearchStartSiteResponse(Long startSiteId, String startSiteName, Double lat, Double lon) {
        this.startSiteId = startSiteId;
        this.startSiteName = startSiteName;
        this.lat = lat;
        this.lon = lon;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }
}
