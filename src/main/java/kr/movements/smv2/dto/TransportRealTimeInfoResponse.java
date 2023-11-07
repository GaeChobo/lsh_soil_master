package kr.movements.smv2.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import kr.movements.smv2.entity.LocationInfoEntity;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@ApiModel("운송기사 상세정보 response")
@Getter
@Builder
public class TransportRealTimeInfoResponse {
    @ApiModelProperty(value = "위치정보")
    private List<TransportRealTimeInfoDto> locations;
//    @ApiModelProperty(value = "위치정보2")
//    private List<LocationInfoEntity> locations2;
    @ApiModelProperty(value = "회원 id")
    private Long userId;
//    @ApiModelProperty(value = "운송기사 id")
//    private Long driverId;
    @ApiModelProperty(value = "계약서 id")
    private Long contractId;
    @ApiModelProperty(value = "상차지명")
    private String startSiteName;
    @ApiModelProperty(value = "상차지 위도")
    private Double startSiteLat;
    @ApiModelProperty(value = "상차지 경도")
    private Double startSiteLon;
    @ApiModelProperty(value = "하차지명")
    private String endSiteName;
    @ApiModelProperty(value = "하차지 위도")
    private Double endSiteLat;
    @ApiModelProperty(value = "하차지 경도")
    private Double endSiteLon;
    @ApiModelProperty(value = "자재종류")
    private String materialType;
    @ApiModelProperty(value = "운송량")
    private Integer transportVolume;
}
