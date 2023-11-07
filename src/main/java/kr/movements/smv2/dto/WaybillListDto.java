package kr.movements.smv2.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@ApiModel("송장 list dto")
@NoArgsConstructor
@Data
public class WaybillListDto {
    @ApiModelProperty(value = "송장 id")
    private Long waybillId;
    @ApiModelProperty(value = "송장번호")
    private String waybillNum;
    @ApiModelProperty(value = "회원명")
    private String  userName;
    @ApiModelProperty(value = "연락처")
    private String  userPhone;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "출발일시")
    private LocalDateTime  departureTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "도착일시")
    private LocalDateTime arriveTime;
    @ApiModelProperty(value = "송장상태")
    private String  waybillStatus;

    @QueryProjection
    public WaybillListDto(Long waybillId, String waybillNum, String userName, String userPhone, LocalDateTime departureTime, LocalDateTime arriveTime, String waybillStatus) {
        this.waybillId = waybillId;
        this.waybillNum = waybillNum;
        this.userName = userName;
        this.userPhone = userPhone;
        this.departureTime = departureTime;
        this.arriveTime = arriveTime;
        this.waybillStatus = waybillStatus;
    }
}

