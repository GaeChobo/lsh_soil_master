package kr.movements.smv2.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@ApiModel("운송기사 송장리스트 dto")
@Getter
@NoArgsConstructor
@Builder
public class DriverWayBillListResponse {
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "송장 업데이트 날짜")
    private LocalDateTime modifiedDate;
    @ApiModelProperty(value = "송장 id")
    private Long waybillId;
    @ApiModelProperty(value = "상차지명")
    private String startSiteName;
    @ApiModelProperty(value = "하차지명")
    private String endSiteName;
    @ApiModelProperty(value = "송장번호")
    private String waybillNumber;
    @ApiModelProperty(value = "gps 허용여부")
    private boolean gpsAgree;
    @ApiModelProperty(value = "송장상태 코드")
    private String waybillStatusCode;
    @ApiModelProperty(value = "자재종류 코드")
    private String materialTypeCode;

    @QueryProjection
    public DriverWayBillListResponse(LocalDateTime modifiedDate, Long waybillId, String startSiteName, String endSiteName, String waybillNumber, boolean gpsAgree, String waybillStatusCode, String materialTypeCode) {
        this.modifiedDate = modifiedDate;
        this.waybillId = waybillId;
        this.startSiteName = startSiteName;
        this.endSiteName = endSiteName;
        this.waybillNumber = waybillNumber;
        this.gpsAgree = gpsAgree;
        this.waybillStatusCode = waybillStatusCode;
        this.materialTypeCode = materialTypeCode;
    }
}
