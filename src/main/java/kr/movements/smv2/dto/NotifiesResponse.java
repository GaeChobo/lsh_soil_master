package kr.movements.smv2.dto;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@ApiModel("현장관리자 알림 response")
@Getter
@Builder
public class NotifiesResponse {
    @ApiModelProperty(value = "알림 id")
    private Long notifyId;
    @ApiModelProperty(value = "현장명")
    private String siteName;
    @ApiModelProperty(value = "송장 id")
    private Long waybillId;
    @ApiModelProperty(value = "송장번호")
    private String waybillNum;

    @QueryProjection
    public NotifiesResponse(Long notifyId, String siteName, Long waybillId, String waybillNum) {
        this.notifyId = notifyId;
        this.siteName = siteName;
        this.waybillId = waybillId;
        this.waybillNum = waybillNum;
    }
}
